package com.revolut.app.config.guiceconfig;

import javax.inject.Inject;

import org.eclipse.jetty.server.Handler;

import com.google.inject.Injector;

/**
 * Walks through the guice injector bindings, visiting each one that is a Handler.
 */
public class HandlerScanner extends Scanner<Handler> {

    @Inject
    public HandlerScanner(Injector injector) {
        super(injector, Handler.class);
    }
}
