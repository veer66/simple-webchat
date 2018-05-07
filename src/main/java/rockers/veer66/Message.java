package rockers.veer66;

public class Message {
	
	String room;
	String text;
	SenderType sender;
	
	Message(String room, String text, SenderType sender) {
		this.room = room;
		this.text = text;
		this.sender = sender;
	}

	public SenderType getSender() {
		return sender;
	}

	public void setSender(SenderType sender) {
		this.sender = sender;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
