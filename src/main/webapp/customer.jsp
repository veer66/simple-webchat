<!DOCTYPE html>
<html lang="en-US">
	<head>
		<title>Websocket</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
	</head>
	<body>
		<div class="main">
			<form>
				<div class="header">
					<div>Room:
						<span id="room"></span>
					</div>
					<h1>CHAT</h1>
				</div>
				<div class="chat">
					<textarea id="messages"></textarea>
				</div>

				<div class="footer">
					<div class="wrapper">
						<input type="text" id="input_message" value="" length="10">
						<button class="submit-button" onclick="return sendMessage()">Send</button>
					</div>
				</div>
			</form>
		</div>
		
		<script>		 
		 function genRoom() {
             let array = new Uint8Array(16)
             window.crypto.getRandomValues(array)
             return btoa(String.fromCharCode.apply(null, array)).replace(/\//g, 'SS').replace(/\+/g, 'P')

         }

		 let room = genRoom()
		 let sock = new WebSocket("ws://localhost:8080/ws?room="+room)
		 let input_message_com = document.querySelector("input#input_message");
         let room_node = document.querySelector("span#room")
         let messages_box = document.querySelector("textarea#messages")
         
         room_node.appendChild(document.createTextNode(room))

         function sendMessage() {
             if (sock.readyState == 1 /* OPEN */ &&
                 !input_message_com.value.match(/^\s*$/)) {
                 let msg = input_message_com.value
                 sock.send(msg)
                 input_message_com.value = ""
             }
             return false
         }
		 
		 sock.onmessage = function(evt) {
			 let msg = evt.data
             messages_box.value += msg + "\n";
		 }
		 
		 sock.onopen = function() {
			 sock.send("^_^")
		 }		 
		</script>
	</body>
</html>
