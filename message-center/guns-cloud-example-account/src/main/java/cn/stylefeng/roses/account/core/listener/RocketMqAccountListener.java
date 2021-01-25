package cn.stylefeng.roses.account.core.listener;

import cn.stylefeng.roses.account.modular.service.IFlowRecordService;
import cn.stylefeng.roses.message.api.order.GoodsFlowParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;

/**
 * rocketmq的监听
 *
 * @author fengshuonan
 * @Date 2019/9/4 22:24
 */
@Service
public class RocketMqAccountListener {

    @Autowired
    private IFlowRecordService flowRecordService;

    @StreamListener(Sink.INPUT)
    public void receive(GoodsFlowParam goodsFlowParam) {
        flowRecordService.recordFlow(goodsFlowParam);
    }

}