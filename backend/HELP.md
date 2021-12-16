#For Chatting app:
`$ brew services start mongodb/brew/mongodb-community`

`$ mongo`

`$ use chat`
`$ db.chat.find().pretty()`

tailable cursor requested on non capped collection' on server localhost:27017
`$ db.runCommand({convertToCapped: 'chat', size: 8192})`

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