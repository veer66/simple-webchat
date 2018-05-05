package rockers.veer66;

import java.net.ServerSocket;
import java.util.logging.Logger;

public class App 
{
	private static Logger logger = Logger.getLogger("simple-websocket");;
	private static int PORT = 10005;
    public static void main( String[] args ) throws Exception
    {
    	logger.info("INIT");
    	var server = new ServerSocket(PORT);
    	logger.info("Server start at " + PORT);
    	var client = server.accept();
    	logger.info("Connected");
    }
}
