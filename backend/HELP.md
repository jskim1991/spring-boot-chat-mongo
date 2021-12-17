#For Chatting app:
1. Run MongoDB as a macOS service:
`$ brew services start mongodb/brew/mongodb-community`

2. connect to local MongoDB instance on your localhost with default port 27017:
`$ mongo`

3. create database named `chatdb`:
`$ use chatdb`

4. create a capped collection with max size of 8192 bytes:
`$ db.createCollection('chat', { capped: true, size: 8192 })`

`$ db.chat.find().pretty()`

tailable cursor requested on non capped collection' on server localhost:27017
`$ db.runCommand({convertToCapped: 'chat', size: 8192})`

To delete all documents
`$ db.chat.drop()`
`$ db.chat.remove({})`


#For OrderServiceTests
Make sure replica is enabled for mongo

1. Add `replication` section to /usr/local/etc/mongod.conf
```
systemLog:
  destination: file
  path: /usr/local/var/log/mongodb/mongo.log
  logAppend: true
storage:
  dbPath: /usr/local/var/mongodb
net:
  bindIp: 127.0.0.1
replication:
  replSetName: rs0
```
2. `$ brew services restart mongodb/brew/mongodb-community`