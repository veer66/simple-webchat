package rocks.veer66;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class ModMsgHelper {
	private static Logger logger = Logger.getLogger("simple-chat/mod-msg-helper");

	public static String modMsg(String url, MessageWrapper msgWrapper) {
		
		try {
			var gson = (new GsonBuilder()).create();
			var content = gson.toJson(msgWrapper);
			HttpResponse<JsonNode> res = Unirest.post(url)
				.header("accept", "application/json")
				.header("Content-Type", "application/json")
				.body(content)
				.asJson();
			if (res.getStatus() != 200) return "(CANNOT CONNECT ModMsg [invalid status])";
			JsonNode node = res.getBody();
			return node.getObject().getString("msg");
		} catch (UnirestException e) {
			e.printStackTrace();
			logger.info("CANNOT CONNECT ModMsg " + e.toString());
			return "(CANNOT CONNECT ModMsg)";
		}				
	}
	
	public static void main(String[] args) throws IOException {
	}

}
