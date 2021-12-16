import { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import './styles.css'

const Chat = () => {
    const [incoming, setIncoming] = useState([])
    const [listening, setListening] = useState(false)
    const [roomNo, setRoomNo] = useState(1)
    const [username, setUsername] = useState(null)
    const inputRef = useRef()
    const scrollRef = useRef()

    useEffect(() => {
        let username = prompt('enter id')
        // let roomNumber = prompt("enter chat room number")
        const roomNumber = 1

        const eventSource = new EventSource(`http://localhost:8080/chat/roomNumber/${roomNumber}`)
        if (!listening) {
            eventSource.onmessage = (event) => {
                const data = JSON.parse(event.data)
                console.log('data', data)
                setIncoming((prevState) => [...prevState, data])
            }
            setUsername(username)
            setRoomNo(roomNumber)
            setListening(true)
        }

        return () => {
            eventSource.close()
        }
    }, [])

    useEffect(() => {
        scrollRef.current.scrollTop = scrollRef.current?.scrollHeight
    }, [incoming])

    const sendHandler = async () => {
        console.log(inputRef.current.value)
        if (inputRef.current.value.length === 0) {
            return
        }
        const result = await axios.post('/chat', {
            sender: username,
            receiver: '',
            message: inputRef.current.value,
            roomNumber: roomNo,
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

    const getTime = (date) => {
        const now = new Date(date)
        const hours = now.getHours()
        const minutes = now.getMinutes()
        const hoursToDisplay = hours < 10 ? `0${hours}` : `${hours}`
        const minutesToDisplay = minutes < 10 ? `0${minutes}` : `${minutes}`
        return `${hoursToDisplay}:${minutesToDisplay}`
    }

    return (
        <div className="container">
            <div className="chat-box" ref={scrollRef}>
                {incoming &&
                    incoming.map((m, index) => {
                        return m.sender === username ? (
                            <div key={index} className={`message ${m.sender === username ? 'message__mine' : ''}`}>
                                <span className="message-time">{getTime(m.createdAt)}</span>
                                <div className="message-box">
                                    <p>{m.message}</p>
                                </div>
                            </div>
                        ) : (
                            <div key={index} className={`message ${m.sender === username ? 'message__mine' : ''}`}>
                                <div className="message-box">
                                    <p>{m.message}</p>
                                </div>
                                <span className="message-time">{getTime(m.createdAt)}</span>
                            </div>
                        )
                    })}
            </div>
            <div className="input-box">
                <div className="input-wrapper">
                    <input
                        className="chat__input"
                        type="text"
                        ref={inputRef}
                        placeholder="Enter message here"
                        onKeyDown={keyDownHandler}
                    />
                    <button className="button" onClick={sendHandler}>
                        Send
                    </button>
                </div>
            </div>
        </div>
    )
}

export default Chat
