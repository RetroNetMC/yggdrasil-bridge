package hu.retronet.mc.common.conditions;

import hu.retronet.mc.common.model.ServerType;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnServerTypeCondition.class)
public @interface ConditionalOnServerType {
    ServerType value();
}
