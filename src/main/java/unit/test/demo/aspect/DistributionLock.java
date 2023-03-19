package unit.test.demo.aspect;

import java.lang.annotation.*;

/**
 * @author zhisong.guan
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributionLock {
    String prefix() default "";

    String key() default "";
}
