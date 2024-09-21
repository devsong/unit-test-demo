package unit.test.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import unit.test.demo.util.JsonUtil;

/**
 * @author zhisong.guan
 */
@Aspect
@Component
@Order(value = AopOrders.GATEWAY_ORDER)
@Slf4j
public class GatewayAspect {
    @Pointcut("within(unit.test.demo.controller..*)")
    public void gateway() {
    }

    @Around("gateway()")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        Object ret = null;
        try {
            ret = point.proceed();
        } finally {
            log.info("args {}", JsonUtil.toJSONString(args));
        }
        return ret;
    }
}
