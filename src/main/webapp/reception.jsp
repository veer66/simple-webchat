<!DOCTYPE html>
<html lang="en-US">
	<head>
		<title>Websocket</title>
	</head>
	<body>
		WS
		<script>
		 let sock = new WebSocket("ws://localhost:8080/ws?receptionist=1", "ws1")
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
