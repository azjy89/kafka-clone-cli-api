Test Produce event
create topic topic1 String
create producer producer1 String Random
create partition topic1 1
produce event producer1 topic1 basicEvent
create consumergroup group1 topic1 Range
create consumer group1 consumer1
consume event consumer1 1

Test show topic
create topic topic1 String
create partition topic1 1
create partition topic1 2
create producer producer1 String Manual
create producer producer2 String Manual
produce event producer1 topic1 basicEvent 1
produce event producer2 topic1 basicEvent2 2
show topic topic1

Test show consumer group

create topic topic1 String
create producer producer1 String Manual
create partition topic1 1
create partition topic1 2
produce event producer1 topic1 basicEvent 1
produce event producer1 topic1 basicEvent2 2
produce event producer1 topic1 basicEvent3 1
create consumergroup group1 topic1 Range
create consumer group1 consumer1
create consumer group1 consumer2
show consumergroup group1

Test parallel produce

create topic topic1 String
create producer producer1 String Random
create producer producer2 String Random
create partition topic1 0
parallel produce producer1 topic1 basicEvent producer2 topic1 basicEvent2
show topic topic1

create producer producer1 String Random
create producer producer2 String Random
create producer producer3 String Random
create topic topic1 String
create partition topic1 1
create partition topic1 2
create partition topic1 3
parallel produce producer1 topic1 basicEvent producer2 topic1 basicEvent2 producer3 topic1 basicEvent3
show topic topic1

create producer producer1 String Random
create topic topic1 String
create partition topic1 1
produce event producer1 topic1 basicEvent
produce event producer1 topic1 basicEvent2
produce event producer1 topic1 basicEvent3
create consumergroup group1 topic1 Range
create consumergroup group2 topic1 Range
create consumer group1 consumer1
create consumer group2 consumer2
parallel consume consumer1 1 consumer2 1

test playback

create producer producer1 String Random
create topic topic1 String
create partition topic1 1
produce event producer1 topic1 basicEvent
produce event producer1 topic1 basicEvent2
produce event producer1 topic1 basicEvent3
create consumergroup group1 topic1 Range
create consumer group1 consumer1
consume event consumer1 1
consume event consumer1 1
consume event consumer1 1
playback consumer1 1 1