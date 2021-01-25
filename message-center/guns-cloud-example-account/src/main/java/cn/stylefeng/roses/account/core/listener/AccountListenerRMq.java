package cn.stylefeng.roses.account.core.listener;

import cn.stylefeng.roses.account.modular.service.IFlowRecordService;
import cn.stylefeng.roses.message.api.account.model.FlowRecord;
import cn.stylefeng.roses.message.api.order.GoodsFlowParam;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * @author: lhz
 * @date: 2021/1/25
 **/
public class AccountListenerRMq implements RocketMQListener<String> {
    private IFlowRecordService flowRecordService;
    @Override
    public void onMessage(String s) {
        //
        flowRecordService.recordFlow(convertJson2FlowRecord(s));
    }

    GoodsFlowParam convertJson2FlowRecord(String s) {
        return null;
    }


}
