package rocks.veer66;

public class MessageWrapper {

	public SenderType sender;
	public String msg;
	
	public MessageWrapper(SenderType sender, String msg) {		
		this.sender = sender;
		this.msg = msg;
	}
}
