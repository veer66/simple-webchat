<!DOCTYPE html>
<html lang="en-US">
	<head>
		<title>Websocket</title>
	</head>
	<body>
		WS
		<script>

		 function genRoom() {
             let array = new Uint8Array(16)
             window.crypto.getRandomValues(array)
             return btoa(String.fromCharCode.apply(null, array)).replace(/\//g, 'SS').replace(/\+/g, 'P')

         }

		 let room = genRoom()
		 console.log("ROOM = " + room)
		 
		 let sock = new WebSocket("ws://localhost:8080/ws?room="+room, "ws1")
		 
		 console.log("SOCK = " + sock)

		 
		 sock.onmessage = function(evt) {
			 console.log("RECV: " + evt.data)
		 }
		 
		 sock.onopen = function() {
			 console.log("OPEN")
			 sock.send("KAKA")
		 }
		 
		</script>
	</body>
</html>
