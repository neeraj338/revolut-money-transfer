package com.revolut.app.config.swagger;

import javax.servlet.ServletContextListener;

import com.google.common.base.Preconditions;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.servlet.ServletModule;

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

public class SwaggerModule extends ServletModule {

    private final String path;

    public SwaggerModule() {
        this.path = null;
    }

    public SwaggerModule(final String path) {
        Preconditions.checkArgument(path.length() == 0 || path.startsWith("/"), "Path must begin with '/'");
        Preconditions.checkArgument(!path.endsWith("/"), "Path must not have a trailing '/'");
        this.path = path;  // e.g., "/api"
    }

    @Override
    protected void configureServlets() {

        // We must initialise Swagger before the first request is processed.
        // That's why JEE defines the ServletContextListener interface.
        // Swagger doesn't provide a ServletContextListener implementation
        // so we need to (i.e., SwaggerServletContextListener).
        Multibinder<ServletContextListener> multibinder = Multibinder.newSetBinder(binder(),
                ServletContextListener.class);
        multibinder.addBinding().to(SwaggerServletContextListener.class);

        bind(ApiListingResource.class);
        bind(SwaggerSerializers.class);

        if (path == null) {
            filter("/*").through(ApiOriginFilter.class);
        } else {
            filter(path + "/*").through(ApiOriginFilter.class);
        }
    }
}