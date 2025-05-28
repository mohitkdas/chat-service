let stompClient = null;
const urlParams = new URLSearchParams(window.location.search);
const roomId = urlParams.get('roomId');
document.getElementById("room").textContent = roomId;
const chatBox = document.getElementById("chat-box");

function connect() {
    const socket = new SockJS("/chat");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        stompClient.subscribe(`/topic/room/${roomId}`, (msg) => {
            const message = JSON.parse(msg.body);
            appendMessage(`${message.sender}: ${message.content}`);
        });
    });

    loadMessageHistory();
}

function loadMessageHistory() {
    axios.get(`/api/messages/${roomId}`)
        .then(response => {
            chatBox.innerHTML = "";
            response.data.forEach(msg => {
                appendMessage(`${msg.sender}: ${msg.content}`);
            });
        })
        .catch(err => console.error("Failed to load history", err));
}

function sendMessage() {
    const sender = document.getElementById("sender").value;
    const content = document.getElementById("message").value;

    if (!sender || !content) return;

    stompClient.send(`/app/chat/${roomId}`, {}, JSON.stringify({
        sender, content, roomId
    }));

    document.getElementById("message").value = "";
}

function appendMessage(text) {
    const div = document.createElement("div");
    div.textContent = text;
    chatBox.appendChild(div);
    chatBox.scrollTop = chatBox.scrollHeight;
}

connect();