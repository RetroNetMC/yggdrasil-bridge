package hu.retronet.mc.common.conditions;

import hu.retronet.mc.common.model.ServerType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

public class OnServerTypeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnServerType.class.getName());
        ServerType desiredValue = (ServerType) attributes.get("value");

        String actualValue = env.getProperty("app.server.type");

        if (actualValue == null) {
            return false;
        }

        return actualValue.equalsIgnoreCase(desiredValue.name());
    }

}