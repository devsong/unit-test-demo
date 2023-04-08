package unit.test.demo.aspect;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import unit.test.demo.exception.DuplicateRequestException;
import unit.test.demo.util.SpelUtil;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author zhisong.guan
 */
@Aspect
@Component
@RequiredArgsConstructor
@Order(value = AopOrders.DISTRIBUTION_LOCK_ORDER)
public class DistributionLockAspect {
    private static final String ANNOTATION_DISTRIBUTION_LOCK = "@annotation(unit.test.demo.aspect.DistributionLock)";
    private final RedissonClient redissonClient;

    @Pointcut(ANNOTATION_DISTRIBUTION_LOCK)
    public void distributionLock() {
    }

    @Around("distributionLock()")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        DistributionLock distributionLock = getDistributionLock(point);
        MethodSignature sig = (MethodSignature) point.getSignature();
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(sig.getName(), sig.getParameterTypes());
        Class<?> cls = target.getClass();
        String className = cls.getSimpleName();
        String methodName = currentMethod.getName();
        String prefix = StringUtils.isBlank(distributionLock.prefix()) ? methodName : distributionLock.prefix();
        String evalExpression = SpelUtil.generateKeyBySpel(distributionLock.key(), point);
        String lockKey = String.format("%s-%s-%s", className, prefix, evalExpression);
        RLock rLock = redissonClient.getLock(lockKey);
        Object ret;
        if (rLock.tryLock()) {
            try {
                ret = point.proceed();
            } finally {
                rLock.unlock();
            }
        } else {
            String msg = String.format("class %s methodName %s,key %s occurred parallel request", className, methodName, evalExpression);
            throw new DuplicateRequestException(msg);
        }
        return ret;
    }

    private DistributionLock getDistributionLock(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        DistributionLock distributionLock = AnnotationUtils.findAnnotation(signature.getMethod(), DistributionLock.class);
        if (Objects.nonNull(distributionLock)) {
            return distributionLock;
        }

        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), DistributionLock.class);
    }
}
