package cn.stylefeng.roses.order.core.rocketmq;

import cn.stylefeng.roses.message.api.order.model.GoodsOrder;
import cn.stylefeng.roses.order.modular.entity.RocketmqTransactionLog;
import cn.stylefeng.roses.order.modular.mapper.RocketmqTransactionLogMapper;
import cn.stylefeng.roses.order.modular.service.IOrderService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * 订单完成后发送给账户服务记账的事务消息监听
 *
 * @author fengshuonan
 * @Date 2019/9/4 21:59
 */
@RocketMQTransactionListener(txProducerGroup = "record-flow-tx-group")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FlowRecordMqTxListener implements RocketMQLocalTransactionListener {

    private final IOrderService orderService;
    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    //如果本地事务执行成功，但是消息发送成功，通知后面的服务执行事务
    //如果本地事务执行失败，但是消息发送成功，直接本地就回滚了
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageHeaders headers = msg.getHeaders();

        //获取事务id和订单记录
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        String orderString = (String) headers.get("order");

        GoodsOrder goodsOrder = JSON.parseObject(orderString, GoodsOrder.class);

        //更新订单状态为成功状态
        try {
            this.orderService.updateToSuccess(goodsOrder, transactionId);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
    //如果本地事务执行成功，但是消息没发送成功，那么回查
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);

        // select * from xxx where transaction_id = xxx
        RocketmqTransactionLog transactionLog = this.rocketmqTransactionLogMapper.selectOne(
                new QueryWrapper<RocketmqTransactionLog>().eq("transaction_id", transactionId));

        //如果事务记录存在则提交消息投递
        if (transactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
