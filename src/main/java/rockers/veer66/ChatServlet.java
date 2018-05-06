package rockers.veer66;

import java.io.IOException;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws")
public class ChatServlet {
	
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
}
