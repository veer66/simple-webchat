package rockers.veer66;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws")
public class ChatServlet {

	private static Logger logger = Logger.getLogger("simple-chat");
	private static ConcurrentLinkedQueue<Message> msgQueue = new ConcurrentLinkedQueue<>();
	private static ConcurrentHashMap<String, ChatServlet> rooms = new ConcurrentHashMap<>();
	private static Set<ChatServlet> supporters = Collections.synchronizedSet(new HashSet<>());

	Session session;
	String room;
	SenderType sender;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@OnMessage
	public void receive(Session session, String text) {
		var msg = new Message(room, text, sender);
		msgQueue.add(msg);
		submit_all();
	}

	public static void submit_all() {
		try {
			while (!supporters.isEmpty() && !msgQueue.isEmpty()) {
				submit();
			}
		} catch (IOException e) {
			logger.info("Cannot submit message: " + e);
		}
	}

	public static void submit() throws IOException {
		var msg = msgQueue.poll();
		switch (msg.sender) {
		case CUSTOMER:
			for (var supporter : supporters) {
				supporter.getSession().getBasicRemote().sendText(msg.getText());
			}
			break;
		case SUPPORTER:
			var consumer = rooms.get(msg.room);
			if (consumer != null) {
				consumer.getSession().getBasicRemote().sendText(msg.getText());
			}
			break;
		}
	}

	@OnOpen
	public void start(Session session) {
		logger.info("start chat");
		setSession(session);
		var supporterQuery = session.getRequestParameterMap().get("supporter");
		if (supporterQuery != null && supporterQuery.size() > 0 && 
				supporterQuery.get(0).equals("1")) {
			logger.info("Supporter");
			supporters.add(this);
			sender = SenderType.SUPPORTER;
		} else {
			var roomQuery = session.getRequestParameterMap().get("room");
			if (roomQuery != null && roomQuery.size() == 1) {
				logger.info("Customer");
				room = roomQuery.get(0);
				rooms.put(room, this);
				sender = SenderType.CUSTOMER;
			}
		}
		submit_all();
	}

	@OnClose
	public void end() {
		logger.info("end chat");
		switch (sender) {
		case CUSTOMER:
			rooms.remove(room);
			break;
		case SUPPORTER:
			supporters.remove(this);
			break;
		}
	}

	@OnError
	public void onError(Throwable t) throws Throwable {
		logger.info("err: " + t);
	}
}
