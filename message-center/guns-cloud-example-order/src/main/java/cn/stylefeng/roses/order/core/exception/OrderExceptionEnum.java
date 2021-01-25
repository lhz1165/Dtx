package cn.stylefeng.roses.order.core.exception;


import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import lombok.Getter;

/**
 * 订单异常
 *
 * @author stylefeng
 * @Date 2018/1/4 22:40
 */
@Getter
public enum OrderExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 错误枚举
     */
    ORDER_NULL(500, "没有订单"),
    ORDER_ERROR(500, "订单系统内部异常");


    OrderExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

}
