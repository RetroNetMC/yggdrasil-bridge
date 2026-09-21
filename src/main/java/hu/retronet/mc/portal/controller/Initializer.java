package hu.retronet.mc.portal.controller;

import hu.retronet.mc.common.config.SessionConfig;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

public class Initializer extends AbstractHttpSessionApplicationInitializer {

    public Initializer() {
        super(SessionConfig.class);
    }

}
