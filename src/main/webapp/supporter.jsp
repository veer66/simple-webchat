<!DOCTYPE html>
<html lang="en-US">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8">
    </head>
    <body>
		<h1>Supporter Chat</h1>

		<p>...</p>
			
        <div id="main">            
        </div>
		
        <script>
         let sock = new WebSocket("ws://localhost:8080/ws?supporter=1");
         let room_map = {}
         let last_room = 1
         let main = document.querySelector("div#main")
		 
         function create_chat(room) {
             if (room_map[room]) {
             } else {
                 let chat_id = "chat-" + last_room
                 last_room += 1
                 room_map[room] = chat_id
                 let container = document.createElement("div")
                 container.setAttribute("id", chat_id)
                 
                 let frm = document.createElement("form")

                 let head = document.createElement("p")
                 head.appendChild(document.createTextNode("ROOM: " + room))
                 let hidden = document.createElement("input")
                 hidden.setAttribute("type", "hidden")
                 hidden.setAttribute("id", "room")
                 hidden.setAttribute("value", room)
                 head.appendChild(hidden)
                 frm.appendChild(head)

                 let content = document.createElement("p")
                 let textarea = document.createElement("textarea")
                 textarea.setAttribute("id", "messages")
                 textarea.setAttribute("rows", 10)
                 textarea.setAttribute("cols", 30)
                 content.appendChild(textarea)
                 frm.appendChild(content)
                 
                 let ctrl = document.createElement("p")
                 let input_text = document.createElement("input")
                 input_text.setAttribute("type", "text")
                 input_text.setAttribute("id", "input_message")
                 input_text.setAttribute("length", "10")
                 ctrl.appendChild(input_text)
                 let button = document.createElement("button")
                 button.setAttribute("id", "input_message")
                 button.setAttribute("class","submit-button")
                 button.setAttribute("onclick", "return sendMessage('" + chat_id + "')")
                 button.appendChild(document.createTextNode("Submit"))
                 ctrl.appendChild(button)
                 
                 frm.appendChild(ctrl)
                 container.appendChild(frm)
                 main.appendChild(container)
             }
         }


         function sendMessage(chat_id) {
             let input_message_com = document.querySelector(
                 "div#" + chat_id + " input#input_message")
             let messages_box = document.querySelector(
                 "div#" + chat_id +" textarea#messages")
             let room = document.querySelector(
                 "div#" + chat_id +" input#room").value
             
             if (sock.readyState == 1 /* OPEN */ &&
                 !input_message_com.value.match(/^\s*$/)) {
                 let msg = input_message_com.value
                 let pack_msg = room + ": " + msg
                 sock.send(pack_msg)
				 console.log("SENT: " + pack_msg)
                 input_message_com.value = ""
             }
             return false
         }

		 sock.onopen = function() {
		 }		 
		 
         sock.onmessage = function (event) {			 
             let msg = event.data
             let m = msg.match(/([^:]+):(.+)/)
             if (m) {
                 let room = m[1]
                 let msg = m[2]
                 create_chat(room)
                 let chat_id = room_map[room]
                 let messages = document.querySelector("div#" + chat_id + " textarea#messages")                 
                 messages.value += msg + "\n"
             }
         }

        </script>
    </body>
</html>
