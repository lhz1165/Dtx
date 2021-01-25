package cn.stylefeng.roses.message.api.account;


import cn.stylefeng.roses.message.api.account.model.FlowRecord;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 流水记录的接口
 *
 * @author stylefeng
 * @Date 2018/4/16 21:47
 */
@RequestMapping("/api/flowRecord")
public interface FlowRecordApi {

    /**
     * 根据订单id查询流水记录
     */
    @RequestMapping("/findOrderFlowRecord")
    FlowRecord findOrderFlowRecord(@RequestParam("orderId") Long orderId);

}