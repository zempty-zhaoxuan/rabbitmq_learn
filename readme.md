## rabbitmq 的安装和基本认识

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672  -e RABBITMQ_DEFAULT_USER=zempty  -e RABBITMQ_DEFAULT_PASS=zempty123 rabbitmq
```
*  启动 web 端界面
docker exec -it rabbitmq rabbitmq-plugins enable rabbitmq_management
*  AMQP中的几个重要的概念：
1. server :又称 broker ,接收客户端的连接，实现AMQP 的实体服务
2. Connection: 连接，应用程序与Broker 的网络连接
3. Channel :网络信道，消息读写的通道，客户端可以建立多个 Channel ，每个Channel代表一个会话任务
4. Message： 消息，服务器和应用程序之间传送的数据，由 Properties和Body 组成。
5. Virtual host: 虚拟地址，用于进行逻辑隔离，最上层的消息路由。
6. Exchange：交换机，接受消息，根据路由键转发消息到绑定的队列
7. Binding : Exchange 和 Queue 之间的
虚拟链接，binding 中可以包含routing key
8. Routing key : 一个路由规则，虚拟机可用它来确定如何路由一个特定的消息
9. Queue : 也称为 Message Queue ,消息队列，保存消息并将它们转发给消费者。
* rabbitmq 的整体架构图如下：
![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200318173048.png)
* rabbitmq 的消息是如何流转的：
![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200318173527.png)

* 详解交换机 Exchange：
1.  name 交换机的名称
2. 交换机的类型： direct ,topic ,fanout,headers四种类型
3. Durability:是否需要持久化，true 为持久化
4. AutoDelete: 当最后一个绑定到 Exchange 上的队列删除后，自动删除该 Exchange
5. Internal : 当前 Exchange 是否用于RabbitMQ 的内部使用，默认为 false
6. Arguments: 扩展参数，用于扩展AMQP协议自制定化使用

## 交换机类型详解：
1. Direct Exchange : 发送到 Direct Exchange 的消息被转发到 RouteKey  中指定的 Queue ，**详情见代码**
2. Topic Exchange : 消息被转发到所有关心RouteKey 指定 Topic 的 queue 上，Exchange 将 RouteKey 和某 Topic 进行模糊匹配：![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200319172254.png)
![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200319172945.png)
**详情可见代码展示**
3. Fanout Exchange : 忽略 RouteKey ,只要交换机绑定到队列上就能收到消息，转发的消息最快。参考图如下所示：
![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200320102356.png)
**详情见代码**

Queue ：
1. 消息队列，实际存储消息数据
2. Durability : 是否持久化， Durable :是，Transient  : 否
3. Auto delete : 选 yes,代表最后一个监听被移除之后，queue 会被自动移除

Message:
1. 服务器和应用程序之间传送的数据
2. 本质上就是一段数据，由 Properties 和 Payload( Body) 组成
3. 常用属性：delivery mode , headers(自定义属性）

Virtual Host:
1. 访问的虚拟地址，用于进行逻辑隔离，最上层的消息路由
2. 一个 Virtual Host 里面可以有若干个 Exchange 和 Queue 
3. 一个 Virtual Host 里面不能有相同名称的 Exchange 和 Queue

## RabbitMQ 的高级特性：
1. 消息如何保障 100% 的投递成功？
	*  保障消息的成功发出
	*  保障 MQ 节点的成功接收
	*  发动端收到 MQ 节点的的确认应答
	* 完善的消息进行补偿机制
	
	解决方案参考图一：
	![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200320123907.png)
	解决方案参考图二：
	![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200320124131.png)
	
2. 幂等性的概念:
对一件事情多次操作，结果是唯一的
消费端实现幂等性，就意味着，我们的消息永远不会消费多次，即使我们收到多条一样的消息
	* 唯一 ID + 指纹码机制
        *  使用 redis 的原子特性实现
3. 理解 confirm 消息的确认机制
	* 消息的确认，是指生产者发送一个消息以后，如果 Broker 收到消息，则会个我们生产者一个应答，
	* 生产者进行接收应答，用来确定这条消息是否正常发送到 Broker ,这种方式也是消息的可靠性投递的核心保障
	![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200323115458.png)
	第一步： 在 channel 上开启确认模式： channel,confirmSelect()
	第二步：在 channel 上添加监听，addConfirmListener ,监听成功和失败的返回结果，根据具体的结果对消息进行重新发送/记录日志等后续的处理！
	**详情见代码**
	
4. Return 消息机制

* Return listener 用于处理一些不可路由的消息
* 我们的消息生产者，通过指定一个 Exchange 和 Rountingkey ,把消息送达到某一个队列中去，然后我们的消费者监听队列，进行消费处理操作
* 但是在某些情况下，如果我们在发送消息的时候，当前的 exchange 不存在或者指定的路由 key 路由不到，这个时候如果我们需要监听这种不可达的消息，就是使用 Return Listener: 在基础 API 中有一个关键的配置项：Mandatory 如果设为 true,则监听器会接收到路由不可达的消息，然后进行后续的处理，如果为 false ，那么 broker 端自动删除该消息。
![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200323140845.png)

5. 消费端限流
* rabbitmq 服务器上有上万条未处理的消息，这样客户端无法同时处理这些消息
* rabbitmq 提拱了一种 qos 功能，记载非自动确认消息的前提下，如果一定数目的消息未被确认前，不进行消费新的消息。
* 一个重要的方法 void BasicQos(prefetchSize,prefetchCount ,global)
参数详解： prefetchSize 一般设者成 0
prefetchCount: 会告诉 RabbitMq 不要同时给一个消费者推送多条消息，即一旦有 N 个消息还没有 ack ,consumer 将 block 掉，直到有消息 ack

global : true /false 是否将上面设置应用于 channel ,也就是限制是 channel 级别还是 consumer 级别
**详情见代码**

6. 消费端重回队列 
**详情见代码**

7. TTL 队列/消息
*  TTL是 Time To Live 的缩写，也就是生存时间
* rabbitmq 支持消息的过期时间，在消息发送的时候可以进行指定
* rabbitmq 支持队列的过期时间，从消息入队列开始计算，只要超过了队列的超时时间配置，消息就会自动被删除

8 . 死信队列
DLX ,Dead-Letter-Exchange
当一个消息在一个队列中编程死信之后，会被重新 publish 到另一个 Exchange ,这个 Exchange 就是 DLX
* 消息被拒绝（basic.reject/basic.nack) 并且 requeue = false 
* TTL 过期
* 队列达到最大的长度
*设置死信队列设置Exchange，Queue,rountingKey 然后进行绑定
==============================
## abbitmq 整合 spring AMQP
1. RabbitAdmin
RabbitAdmin类可以很好的操作 RabbitMQ
![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200330111712.png)
autoStartup 必须要设置为 true, 否则 Spring 容器不会 RabbitAdmin 类
RabbitAdmin 底层实现就是从 Spring 容器中获取 Exchange ，Binding, RoutingKey , Queue 的@Bean 声明
底层使用 RabbitTemplate 的 execute 方法执行对应的声明，修改，删除等一系例 RabbitMQ 基础功能操作
**详情见代码**

2. SpringAMQP 声明
使用 @Bean 的形式进行一个声明，如下图：
![](https://raw.githubusercontent.com/kickcodeman/pics/master/20200330134601.png)

3. RabbitTemplate
RabbitTemplate 消息模版，是 SpringAMQP 整合的时候进行发送消息的关键类。
该类提供了丰富的发送消息的方法，包括可靠性投递消息的方法，回调监听消息接口 ConfirmCallbak ,返回值确认接口 ReturnCallback.我们需要注入到 Spring容器当中，然后直接使用.

4. SimpleMessageListenerContainer
消息监听容器，这个类非常的强大， 我们可以对他进行很多设置，对于消费者的配置，这个类都可以满足
监听队列，自动启动，自动声明功能
设置事务特性， 事务管理器， 事务属性，事务容量（并发）, 是否开启事务，回滚消息等。
设者消费者数量，最小最大数量，批量消费
设置消息确认和自动确认模式，是否重回队列，异常捕获 handler 函数
设置消费者标签的生成策略，是否独占模式，消费者属性等。
设置具体的监听器，消息转换器等等。
SimpleMessageListenerContainer可以进行动态设置，为什么可以动态感知配置变更？
**详情见代码**
5. MessageListenerAdapter
**详情见代码**
defaultListenerMethod 默认监听方法名称，用于设置监听方法名称
delegate 委托对象：实际真实的委托对象，用于处理消息
queueOrTagToMethodName 队列标识和方法名称组成的集合，可以一一进行队列和方法名称的匹配，队列和方法名称绑定，队列的消息会被绑定的方法接受处理[img 8BB4A037-1D17-474C-B4DC-9AFE765175C5]
6. MessageConverter
消息转换器，我们在进行发送消息的时候，正常情况下消息体为二进制的数据方式进行传输，如果我们希望内部帮助我们进行转换， 或者自定义的转换器，就需要用到 MessageConverter 。

自定义转换器： MessageConverter ,一般需要实现这个接口，重写两个重要的方法：
*  toMessage : java 对象转换为 Message
*  fromMessage:  Message 对象转换为 java 对象

MessageConverter 消息转换器的几种类型：
*  Jackson2JsonMessageConverter :可以进行 java 对象的转换功能
* DefaultJacksonJavaTypeMapper 映射器： 可以进行 java 对象的映射关系
* 自定义二进制转换器：比如图片类型、PDF、流媒体

## RabbitMq 整合 springboot
生产端配置参数：
1. 	publisher-confirms ,实现一个监听器用用语监听 Broker 端给我们返回的确认请求：RabbitTemplate.ConfirmCallback
2. publisher-returns, 保证消息对 Broker 端是可达的，如果出现路由键不可达的情况，则使用监听器对不可达的消息进行后续的处理，保证消息的路由成功：RabbitTemplate.ReturnCallback
3. 注意一点，在发送消息的时候对 template 进行配置 mandatory = true 保证监听有效
4. 生产端还可以配置其它属性，比如发送重试，超市时间，次数, 间隔等。

消费端配置：
spring.rabbitmq.listener.simple.acknowledge-mode= MANUAL
spring.rabbitmq.listener.simple.concurrency =1
spring.rabbitqm.listener.simple.max-concurrency=5
首先配置手工确认模式，用于 ACK 的手工处理，这样我们可以保证消息的可靠性送达，或者消费者消费失败的时候可以做到重回队列，根据业务记录直至等处理。

可以设置消费端监听个数和最大的个数，用于控制消费端的并发情况

RabbitListener 注解的使用
消费端监听 @RabbitMQListener 注解，这个对于在实际工作中非常好用。
@RabbitMqListener 是一个组合注解，里面可以注解配置@QueueBinding 、@Queue、@Exchange 直接通过这个组合注解一次性搞定消费端交换机、队列、绑定、路由、并且配置监听功能等。


## spring cloud stream 整合

整体的一个架构：
![](https://raw.githubusercontent.com/kickcodeman/pics/master/springcloud%20stream.png)

spring cloud stream 整体架构的核心概念图：
![](https://raw.githubusercontent.com/kickcodeman/pics/master/springcoudstream%E6%95%B4%E4%BD%93%E6%9E%B6%E6%9E%84.png)

Barista 接口： Barista 接口是定义来作为后面类的参数，这一接口定义通道类型和通道名称，通道名称是作为配置用，通道类型则决定了 app 这一通道进行发送消息还是从中接收消息。

@Output : 输出注解，用于定义发送消息的接口。
@Input : 输入注解，用于定义消息的消费者接口
@StreamListener : 用于定义监听方法的注解

使用 Spring Cloud Stream  非常简单，只需要使用好以上三个注解即可，在使用高性能消息的生产和消费的场景非常适合，但是使用 spring cloud stream 框架有一个非常大的问题就是不能实现可靠性投递，无法保证消息的 100%的可靠性，会存在少量消息丢失的问题。
**详情见代码**
