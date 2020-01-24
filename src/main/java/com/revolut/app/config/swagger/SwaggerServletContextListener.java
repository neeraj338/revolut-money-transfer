package com.revolut.app.config.swagger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.revolut.resource.AccountResource;

import io.swagger.config.ScannerFactory;
import io.swagger.jaxrs.config.BeanConfig;

final class SwaggerServletContextListener implements ServletContextListener {

    SwaggerServletContextListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {

        BeanConfig beanConfig = getBeanConfig();
        event.getServletContext().setAttribute("reader", beanConfig);
        event.getServletContext().setAttribute("swagger", beanConfig.getSwagger());
        event.getServletContext().setAttribute("scanner", ScannerFactory.getScanner());
    }

    private BeanConfig getBeanConfig() {

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[] { "http" });
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/api");

        beanConfig.setTitle("Api Documentation");
        beanConfig.setDescription("RESTEasy, Embedded Jetty, Swagger and Google Guice");

        // setScan() must be called last
        beanConfig.setResourcePackage(AccountResource.class.getPackage().getName());
        beanConfig.setScan(true);

        return beanConfig;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}
