package com.revolut.app;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.apache.bval.guice.ValidationModule;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.revolut.app.config.guiceconfig.EventListenerScanner;
import com.revolut.app.config.guiceconfig.GuiceJettyModule;
import com.revolut.app.config.guiceconfig.GuiceModule;
import com.revolut.app.config.guiceconfig.HandlerScanner;
import com.revolut.app.config.persistence.PersistenceModule;
import com.revolut.app.config.servletconfig.RestEasyModule;
import com.revolut.app.config.swagger.SwaggerModule;

@ThreadSafe
public class RevolutAccountTransactionApp {
	
	private static final Logger logger = LoggerFactory.getLogger(RevolutAccountTransactionApp.class);
	
	private static final String APPLICATION_PATH = "/api";
    private static final String CONTEXT_ROOT = "/";
    
    private static final String PERSISTENCE_UNIT_NAME = "revolutDB";
    private final GuiceFilter filter;
    private final EventListenerScanner eventListenerScanner;
    private final HandlerScanner handlerScanner;
    // Guice can work with both javax and guice annotations.
    @Inject
    public RevolutAccountTransactionApp(GuiceFilter filter
    		, EventListenerScanner eventListenerScanner
    		, HandlerScanner handlerScanner) {
        this.filter = filter;
        this.eventListenerScanner = eventListenerScanner;
        this.handlerScanner = handlerScanner;
    }

    public static void main(String[] args) throws Exception {

        try {
            Log.setLog(new Slf4jLog());

            final Injector injector = Guice.createInjector(new GuiceJettyModule()
            		, new RestEasyModule(APPLICATION_PATH)
                    , new GuiceModule()
                    , new PersistenceModule(PERSISTENCE_UNIT_NAME)
                    , new ValidationModule()
                    , new SwaggerModule(APPLICATION_PATH));
            
            
            injector.getInstance(RevolutAccountTransactionApp.class).run();

        } catch (Throwable t) {
        	logger.error(ExceptionUtils.getStackTrace(t));
        	throw t;
        }
    }

    public void run() throws Exception {

        final int port = 8080;
        final Server server = new Server(port);

        // Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        final ServletContextHandler context = new ServletContextHandler(server, CONTEXT_ROOT);

        // Add the GuiceFilter (all requests will be routed through GuiceFilter).
        FilterHolder filterHolder = new FilterHolder(filter);
        context.addFilter(filterHolder, APPLICATION_PATH + "/*", null);

        // Setup the DefaultServlet at "/".
        final ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
        context.addServlet(defaultServlet, CONTEXT_ROOT);

        // Set the path to our static (Swagger UI) resources
        String resourceBasePath = RevolutAccountTransactionApp.class.getResource("/swagger-ui").toExternalForm();
        context.setResourceBase(resourceBasePath);
        context.setWelcomeFiles(new String[] { "index.html" });

        // Add any Listeners that have been bound, for example, the
        // GuiceResteasyBootstrapServletContextListener which gets bound in the RestEasyModule.

        /*
        eventListenerScanner.accept(new Visitor<EventListener>() {
        
            @Override
            public void visit(EventListener listener) {
                context.addEventListener(listener);
            }
        });
        */

        eventListenerScanner.accept((listener) -> {
            context.addEventListener(listener);
        });

        final HandlerCollection handlers = new HandlerCollection();

        // The Application context is currently the server handler, add it to the list.
        handlers.addHandler(server.getHandler());

        // Add any Handlers that have been bound

        /*
        handlerScanner.accept(new Visitor<Handler>() {
        
            @Override
            public void visit(Handler handler) {
                handlers.addHandler(handler);
            }
        });
        */

        handlerScanner.accept((handler) -> {
            handlers.addHandler(handler);
        });

        server.setHandler(handlers);
        server.start();
        server.join();
    }
}