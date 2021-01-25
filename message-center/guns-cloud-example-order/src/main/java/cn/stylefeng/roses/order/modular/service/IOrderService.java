package cn.stylefeng.roses.order.modular.service;

import cn.stylefeng.roses.message.api.order.model.GoodsOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author stylefeng123
 * @since 2018-05-05
 */
public interface IOrderService extends IService<GoodsOrder> {

    /**
     * <pre>
     * 测试下单业务
     * </pre>
     *
     * @author stylefeng
     * @Date 2018/5/6 12:10
     */
    Long makeTestOrder();

    /**
     * <pre>
     * 完成订单
     *
     * 说明：
     *  本业务为分布式事务解决方案之可靠消息最终一致性方案的实例
     * </pre>
     *
     * @author stylefeng
     * @Date 2018/5/6 12:10
     */
    void finishOrder(String orderId);

    /**
     * 将订单更新成成功状态，附带存储本地事务消息记录
     *
     * @author fengshuonan
     * @Date 2019/9/4 22:01
     */
    void updateToSuccess(GoodsOrder order, String transactionId);
}
