package com.revolut.app.config.guiceconfig;

import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;

public class GuiceJettyModule extends ServletModule {

    @Override
    protected void configureServlets() {

        bind(GuiceFilter.class);
    }
}
