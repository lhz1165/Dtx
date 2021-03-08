# 实现分布式事务

## rocketMQ实现分布式事务

定时任务扫描到奖励表，发送到mq，用户服务订阅这个服务增加积分  

订单-受益人-奖励-状态

### try阶段

1. 阶段奖励表状态更新为正在发放,
2. 向mq发送事务消息header={order_id tx_id}  body={奖励记录}
3. mq收到消息会有回调方法executeLocalTransaction（），在里面 update 状态为已发送，然后本地事务表插入一条记录(tx_id desc  方便回查)，给mq发消息说commit/rollback
4. 如果未收到 刚刚的commit/rollback，回调方法checkLocalTransaction，就主动根据tx_id查本地事务表，如果有这条表示事务成功，没有就表示事务失败

### confirm/cancel阶段

​	用户积分服务订阅这个MQ，收到消息拿出来，找到对应的用户增加积分，和收支记录

为了防止重复消费，需要根据order_id来判断幂等性，如果收支记录有这笔订单的积分那么直接返回



## 	ActiveMq实现分布式事务（通过可靠消息最终一致性）

1. 创建消息
2. 消息存入mysql的消息表（待确认）
3. 更新奖励表的发奖励的状态为成功
4. 更新此消息（发送中）
5. 放入消息队列

第二阶段消费消息

​	拿到消息，根据消息转化成消息对象，

​	根据消息对象来幂等性判断，新增积分的增加记录，并给用户加上去，然后消息表删除这条消息





上面任何一个环节都有可能出问题，所以引入定时器去定时查询，防止数据库和消息中间件的不一致

# 定时器扫描数据库的消息

基于模板方法模式

模板方法checkMessages()



1.分页查询发送中和待确认的消息记录，并放入集合 protected abstract PageResult<ReliableMessage> getPageResult(PageQuery pageQuery);

key=messageId 根据序列自动生成工具生成的

value=message对象{messageId,messageBody,messageDataType,consumerQueue,messageSendTimes,alreadyDead(死？活),status(发送重待确认)}

2.处理消息processMessage(Map<String, ReliableMessage> messages)；



重点说一下处理消息

## 处理状态为“发送中”但超时没有被成功消费确认的消息

1. 判断发送次数
2. 如果超过最大发送次数直接退出，标记为死亡
3. 重发消息

## 处理状态为“待确认”但已超时的消息

1. 根据这个消息body包含的订单信息，查询该订单表中的状态，如果订单表成功那么修改状态为发送中并重发消息。
2. 如果订单失败，那么直接删除这一条消息记录
