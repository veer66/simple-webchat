package rocks.veer66;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

@ServerEndpoint("/ws")
public class ChatServlet {

	private static Logger logger = Logger.getLogger("simple-chat");
	private static ConcurrentLinkedQueue<Message> msgQueue = new ConcurrentLinkedQueue<>();
	private static ConcurrentHashMap<String, ChatServlet> rooms = new ConcurrentHashMap<>();
	private static Set<ChatServlet> supporters = Collections.synchronizedSet(new HashSet<>());

	private static String modUrl;

	static {
		try {
			var in = ModMsgHelper.class.getResource("/config.json").openConnection().getInputStream();
			var isr = new InputStreamReader(in, "UTF-8");
			Config conf = new Gson().fromJson(isr, Config.class);
			isr.close();
			in.close();
			modUrl = conf.mod_url;
		} catch (IOException e) {
			logger.info("Cannot read config: " + e.toString());
		}
	}

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
		switch (sender) {
		case CUSTOMER:
			var msg = new Message(room, text, sender);
			var additionalMsg = ModMsgHelper.modMsg(modUrl, new MessageWrapper(SenderType.SUPPORTER, "สวัสดีครับ"));
			msgQueue.add(msg);
			submit_all();
			break;
		case SUPPORTER:
			var m = Pattern.compile("([^:]+): (.+)").matcher(text);
			if (m.matches() && m.groupCount() == 2) {
				var room = m.group(1);
				var _text = m.group(2);
				var _msg = new Message(room, _text, sender);
				msgQueue.add(_msg);
				submit_all();
			}
			break;
		}

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
		var consumer = rooms.get(msg.room);
		switch (msg.sender) {
		case CUSTOMER:
			for (var supporter : supporters) {
				supporter.getSession().getBasicRemote().sendText(msg.getRoom() + ":" + msg.getText());
			}

			if (consumer != null) {
				consumer.getSession().getBasicRemote().sendText(msg.getText());
			}
			break;
		case SUPPORTER:
			for (var supporter : supporters) {
				supporter.getSession().getBasicRemote().sendText(msg.getRoom() + ":" + msg.getText());
			}
			if (consumer != null) {
				consumer.getSession().getBasicRemote().sendText(msg.getText());
			}
			break;
		}
	}

	@OnOpen
	public void start(Session session) {
		setSession(session);
		var supporterQuery = session.getRequestParameterMap().get("supporter");
		if (supporterQuery != null && supporterQuery.size() > 0 && supporterQuery.get(0).equals("1")) {
			supporters.add(this);
			sender = SenderType.SUPPORTER;
		} else {
			var roomQuery = session.getRequestParameterMap().get("room");
			if (roomQuery != null && roomQuery.size() == 1) {
				room = roomQuery.get(0);
				rooms.put(room, this);
				sender = SenderType.CUSTOMER;
			}
		}
		submit_all();
	}

	@OnClose
	public void end() {
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
