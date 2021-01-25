package cn.stylefeng.roses.order.modular.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.stylefeng.roses.core.util.ToolUtil;
import cn.stylefeng.roses.kernel.model.exception.RequestEmptyException;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.message.api.order.enums.OrderStatusEnum;
import cn.stylefeng.roses.message.api.order.model.GoodsOrder;
import cn.stylefeng.roses.order.core.exception.OrderExceptionEnum;
import cn.stylefeng.roses.order.modular.entity.RocketmqTransactionLog;
import cn.stylefeng.roses.order.modular.mapper.OrderMapper;
import cn.stylefeng.roses.order.modular.service.IOrderService;
import cn.stylefeng.roses.order.modular.service.RocketmqTransactionLogService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 * 订单服务 （通过rocketmq自带的事务消息功能完成）
 * </p>
 *
 * @author stylefeng123
 * @since 2018-05-05
 */
@Service
public class MqTxMessageOrderServiceImpl extends ServiceImpl<OrderMapper, GoodsOrder> implements IOrderService {

    @Autowired
    private Source source;

    @Autowired
    private RocketmqTransactionLogService rocketmqTransactionLogService;

    @Override
    public Long makeTestOrder() {

        //创建订单
        GoodsOrder goods = createGoods();

        //下单
        this.save(goods);

        //返回订单号
        return goods.getId();
    }

    @Override
    public void finishOrder(String orderId) {

        if (ToolUtil.isEmpty(orderId)) {
            throw new RequestEmptyException();
        }

        GoodsOrder order = this.getById(orderId);

        if (order == null) {
            throw new ServiceException(OrderExceptionEnum.ORDER_NULL);
        }

        if (OrderStatusEnum.SUCCESS.getStatus().equals(order.getStatus())) {
            return;
        }

        // 发送事务消息，让账户服务来接收
        String transactionId = UUID.randomUUID().toString();
        this.source.output().send(MessageBuilder.withPayload(order)
                .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                .setHeader("order_id", orderId)

                //转化成string，否则FlowRecordMqTxListener不能正常接收值
                .setHeader("order", JSON.toJSONString(order))
                .build()
        );

        //真正的业务处理要和保存本地事务消息在一块，详情见FlowRecordMqTxListener

    }

    private GoodsOrder createGoods() {
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsName("商品" + RandomUtil.randomNumbers(4));
        goodsOrder.setCount(Integer.valueOf(RandomUtil.randomNumbers(1)));
        goodsOrder.setCreateTime(new Date());
        goodsOrder.setSum(new BigDecimal(RandomUtil.randomDouble(10.0, 50.0)).setScale(2, RoundingMode.HALF_UP));
        goodsOrder.setId(IdWorker.getId());
        goodsOrder.setUserId(IdWorker.getId());
        goodsOrder.setStatus(OrderStatusEnum.NOT_SUCCESS.getStatus());  //未完成的订单

        return goodsOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateToSuccess(GoodsOrder order, String transactionId) {
        order.setStatus(OrderStatusEnum.SUCCESS.getStatus());
        this.updateById(order);

        //存储事务消息记录
        RocketmqTransactionLog entity = new RocketmqTransactionLog();
        entity.setTransactionId(transactionId);
        entity.setRemark("存储本地事务");
        rocketmqTransactionLogService.save(entity);
    }
}
