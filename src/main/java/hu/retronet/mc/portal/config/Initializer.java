package hu.retronet.mc.portal.config;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

public class Initializer extends AbstractHttpSessionApplicationInitializer {

    public Initializer() {
        super(SessionConfig.class);
    }

}
