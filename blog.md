Initial Blog

Task 1 Preliminary Design

- Analysis of engineering requirements
- Going to have a tributary cluster, continuing topics, which contain partitions, which holds a list of messages/events
- Tributary controller will contain the map of producers and consumer groups (consumer groups will have consumers)
- Will use strategy pattern to implement both the production strategy and the rebalancing strategy which will be implemented in producers and consumer groups respective
- For consume events and the replay function, will implement a observer pattern where partitions are subscribed to consumers, and consumers will notify partitions when they consume/replay (different implementation) will also have an offset to keep track of what they consumed up to. Consumed messages will be added to each consumers list of consumed messages
- Usability Tests
- Need to be able to correctly create topic “create topic topic1 String”
- Need to then be able to create partition inside topic “create partition topic1 1”
- Need to be able to correctly create producers with production strategies “create producer producer1 String Manual”
- Need to be able to correctly produce event “produce event producer1 topic1 basicEvent 1”
- Need to be able to correctly create consumer group “create consumer group group1 topic1 Range”
- Need to be able to correctly create consumer “”create consumer group1 consumer1
- Need to be able to correctly show topic “show topic topic1”
- Need to be able to correctly show consumer group “show consumer group group 1”
- (\* need to also correctly produce events for both production strategies (which can be tested by just showing the topic each time to verify they are in the right place)
- (\* need to test the both the rebalancing strategies by adding multiple events and partitions and multiple consumer groups and consumers)
- (\* add more partitions and produce more events and add more consumers groups and consumers before the following)
- Need to be able to correctly rebalance using both methods, “set consumer group rebalancing <group> <rebalancing>”
- Need to be able to correctly parallel produce and parallel consume (which can be tested using the show functions) “parallel produce (<producer>, <topic>, <event>), …” “ parallel consume (<consumer>, <partition>) “
- Need to be able to correctly playback “playback <consumer> <partition> <offset>”
- Testing Plan
- For the testing plan, will have unit tests for each of the commands as well as their fail cases, eg. for create topic, will test create topic with the same id twice, will test with incorrect input, this will be done for every single command
- We will test the correctness of our output through show commands as well as additional helper methods like double, triple, eventLoader ect
- For our integration tests, will start with simpler mixed tests, eg testing the two producer strategies to more complex tests utilising all the components together, eg the full test down below which will likely be used for the video recording
- We will have test that will create all the topics, partitions and events as well as all the producers consumer groups and consumers, then call rebalance, then consume, then playback, making sure to use show at each step of the way to verify that everything is correct
- Full Tests to Verify All Usability
- PRODUCE, REBALANCE, CONSUME, PLAYBACK

create topic topic1 String
create partition topic1 1
create partition topic1 2

show topic topic1

create producer producer1 String Manual
produce event producer1 topic1 basicEvent 1
produce event producer1 topic1 basicEvent2 2
produce event producer1 topic1 basicEvent3 1

show topic topic1

create consumergroup group1 topic1 Range
create consumer group1 consumer1
create consumer group1 consumer2

show consumergroup group1

set consumer group rebalancing group1 RoundRobin

show consumergroup group1

delete consumer consumer1
delete consumer consumer2

show consumergroup group1

create consumergroup group2 topic1 Range
create consumer group2 consumer1

consume event consumer1 1

playback consumer1 1 0

- PARALLEL PRODUCE, PARALLEL CONSUME

create topic topic1 String
create partition topic1 1
create partition topic1 2
create partition topic1 3

show topic topic1

create producer producer1 String Random
create producer producer2 String Random
create producer producer3 String Random

parallel produce producer1 topic1 basicEvent producer2 topic1 basicEvent2 producer3 topic1 basicEvent3 producer1 topic1 basicEvent2

show topic topic1

create consumergroup group1 topic1 Range
create consumergroup group2 topic1 Range
create consumer group1 consumer1
create consumer group2 consumer2
parallel consume consumer1 1 consumer2 1

- Feature Driven Implementation
- Easier usability testing, as we can test each feature as its developed, eg can implement and test create topic before doing create partition
- Allows us to make quick changes, as the rapid feedback from being able to test each feature as we implement it allows us to fix it, eg we can fix produce event before trying to add consumer
- This allows us to clearly, easily and quickly identify errors in the implementation without having to figure out which part exactly has the issue
- The reason is cos this task is easy to do with feature drive, as each feature somewhat builds ontop of previous features

<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

Final Blog

Usability Tests

- Need to be able to correctly create topic “create topic topic1 String”
- Need to then be able to create partition inside topic “create partition topic1 1”
- Need to be able to correctly create producers with production strategies “create producer producer1 String Manual”
- Need to be able to correctly produce event “produce event producer1 topic1 basicEvent 1”
- Need to be able to correctly create consumer group “create consumer group group1 topic1 Range”
- Need to be able to correctly create consumer “”create consumer group1 consumer1
- Need to be able to correctly show topic “show topic topic1”
- Need to be able to correctly show consumer group “show consumer group group 1”
- (\* need to also correctly produce events for both production strategies (which can be tested by just showing the topic each time to verify they are in the right place))
- (\* need to test the both the rebalancing strategies by adding multiple events and partitions and multiple consumer groups and consumers)
- (\* add more partitions and produce more events and add more consumers groups and consumers before the following)
- Need to be able to correctly rebalance using both methods, “set consumer group rebalancing <group> <rebalancing>”
- Need to be able to correctly parallel produce and parallel consume (which can be tested using the show functions) “parallel produce (<producer>, <topic>, <event>), …” “ parallel consume (<consumer>, <partition>) “
- Need to be able to correctly playback “playback <consumer> <partition> <offset>”
- Testing Plan
- For the testing plan, will have unit tests for each of the commands as well as their fail cases, eg. for create topic, will test create topic with the same id twice, will test with incorrect input, this will be done for every single command
- We will test the correctness of our output through show commands as well as additional helper methods like double, triple, eventLoader ect
- For our integration tests, will start with simpler mixed tests, eg testing the two producer strategies to more complex tests utilising all the components together, eg the full test down below which will likely be used for the video recording
- We will have test that will create all the topics, partitions and events as well as all the producers consumer groups and consumers, then call rebalance, then consume, then playback, making sure to use show at each step of the way to verify that everything is correct

PRODUCE, REBALANCE, CONSUME, PLAYBACK

create topic topic1 String
create partition topic1 1
create partition topic1 2

show topic topic1

create producer producer1 String Manual
produce event producer1 topic1 basicEvent 1
produce event producer1 topic1 basicEvent2 2
produce event producer1 topic1 basicEvent3 1

show topic topic1

create consumergroup group1 topic1 Range
create consumer group1 consumer1
create consumer group1 consumer2

show consumergroup group1

set consumer group rebalancing group1 RoundRobin

show consumergroup group1

delete consumer consumer1
delete consumer consumer2

show consumergroup group1

create consumergroup group2 topic1 Range
create consumer group2 consumer1

consume event consumer1 1

playback consumer1 1 0

PARALLEL PRODUCE, PARALLEL CONSUME

create topic topic1 String
create partition topic1 1
create partition topic1 2
create partition topic1 3

show topic topic1

create producer producer1 String Random
create producer producer2 String Random
create producer producer3 String Random

parallel produce producer1 topic1 basicEvent producer2 topic1 basicEvent2 producer3 topic1 basicEvent3 producer1 topic1 basicEvent2

show topic topic1

create consumergroup group1 topic1 Range
create consumergroup group2 topic1 Range
create consumer group1 consumer1
create consumer group2 consumer2
parallel consume consumer1 1 consumer2 1

USABILITY TEST YOUTUBE LINK

https://www.youtube.com/watch?v=1uChHNZ98TI

<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

Design Patterns Overview

Facade Pattern
The API uses a facade pattern providing a simplified interface for the Tributary sytem

Strategy Pattern
Strategy patterns are used for rebalancing and for production.

Factory Pattern
A factory pattern is used for creating rebalancing strategies and the same is true for production stratgies.

Observer/PubSub Pattern
An observer pattern is used between consumers and partitions where partitions are subscribed to consumers to allow for event driven behaviour. ConsumerGroups are subscribed to topics to allow for rebalancing on partition creation as event driven behaviour

Singleton Pattern
Singleton pattern is used on the TributaryCluster to allow for correct parallel consumption/production behaviour

<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

Design Consideration Accomodations
To accomodate the design consideration of concurrency we used a Singleton pattern on the TributaryCluster and used synchronized code blocks for production and consumption to allow for correct parallel code behaviour (consumers not consuming the same event etc.). This ensured that only one thread during parallel consumption/production could act on the TributaryCluster.

To accomodate for the desing consideration of Generics and to ensure that an object of any type can be used as an event payload in a tributary topic we elected to use a String to represent everything and have a type field which allowed for determination of type. This way custom objects such as JSONObjects and files of different formats could be stored as an event value.

<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

UML
The final uml is located in the finaldesign folder.

<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

Reflection

Overall I think this assignment went quite well I think we have signficantly improved upon our design skills since assignments 1 and 2. We have also learnt to implement and adapt different design patterns to different use cases specifically in terms of event driven architecture as well as improved our skills in understanding design requirements and planning them out. However, this understanding of design requirements still seemed to be the most challenging part of the assignment. Another challenging part of the assignment was the dealing with of unforeseen issues within our initial design. This caused significant changes in our development including the structure of events and how events were produced. However, our overall approach remained feature driven as with a small two man team the integrability issues did not arise and feature driven approach lead to efficient and cohesive implementation.
