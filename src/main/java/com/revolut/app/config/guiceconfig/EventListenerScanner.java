package com.revolut.app.config.guiceconfig;

import java.util.EventListener;

import javax.inject.Inject;

import com.google.inject.Injector;

/**
 * Walks through the guice injector bindings, visiting each one that is an EventListener.
 */
public class EventListenerScanner extends Scanner<EventListener> {

    @Inject
    public EventListenerScanner(Injector injector) {
        super(injector, EventListener.class);
    }
}
