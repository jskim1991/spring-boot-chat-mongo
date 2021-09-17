import {useEffect, useRef, useState} from "react";
import axios from "axios";

const Chat = () => {
    const [incoming, setIncoming] = useState([])
    const [listening, setListening] = useState(false)
    const [roomNo, setRoomNo] = useState(1)
    const [username, setUsername] = useState(null)
    const inputRef = useRef()

    useEffect(() => {
        let username = prompt("enter id")
        // let roomNumber = prompt("enter chat room number")
        // const roomNumber = 1

        const eventSource = new EventSource(`http://localhost:8080/chat/roomNumber/${roomNumber}`)
        if (!listening) {
            eventSource.onmessage = (event) => {
                const data = JSON.parse(event.data);
                console.log('data', data)
                setIncoming(prevState => [...prevState, data])
            }
            setUsername(username)
            // setRoomNo(roomNumber)
            setListening(true)
        }

        return () => {
            eventSource.close();
        };
    }, [])

    const sendHandler = async () => {
        console.log(inputRef.current.value)
        const result = await axios.post('/chat', {
            sender: username,
            receiver: 'you',
            message: inputRef.current.value,
            roomNumber: roomNo
        })
        console.log('axios post', result)
        clearInput()
    }

    const clearInput = () => {
        inputRef.current.value = ''
    }

    const keyDownHandler = async (e) => {
        if (e.key === 'Enter') {
            await sendHandler()
        }

    }

    return (
        <div>
            <div>
                {
                    incoming && incoming.map((m, index) => {
                        const style = m.sender === username ? {
                            'textAlign': 'right'
                        } : {
                            'textAlign': 'left'
                        }

                        return (
                            <div key={index}
                                 style={style}>
                                <p>{m.message}</p>
                                <span>{m.createdAt}</span>
                            </div>
                        )
                    })
                }
            </div>
            <input type="text" ref={inputRef} placeholder="Enter message here"
                   onKeyDown={keyDownHandler}
            />
            <button onClick={sendHandler}>Send</button>
        </div>
    )
}

export default Chat;