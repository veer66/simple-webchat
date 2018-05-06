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
	private static Set<ChatServlet> receptionists = Collections.synchronizedSet(new HashSet<>());
	
	
	Session session;
	String room;
	boolean isReceptionist;
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@OnMessage
	public void echo(Session session, String msg) {
		try {
			if (session.isOpen()) {
				session.getBasicRemote().sendText("@@@");
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	@OnOpen
	public void start(Session session) {
		logger.info("start chat");		
		setSession(session);
		var receptionistQuery = session.getRequestParameterMap().get("receptionist");
		if (receptionistQuery != null && 
				receptionistQuery.size() > 0 && 
				receptionistQuery.get(0).equals("1")) {
			logger.info("Receptionist");
			isReceptionist = true;
			receptionists.add(this);
		} else {
			var roomQuery = session.getRequestParameterMap().get("room");
			if (roomQuery != null && roomQuery.size() == 1) {
				logger.info("Guest");
				room = roomQuery.get(0);
				rooms.put(room, this);
				isReceptionist = false;
			}						
		}
	}
	
	@OnClose
	public void end() {
		logger.info("end chat");
		if (isReceptionist) {
			receptionists.remove(this);
		} else {
			rooms.remove(room);
		}
	}
	
	@OnError
	public void onError(Throwable t) throws Throwable {
		logger.info("err: " + t);
	}
}
