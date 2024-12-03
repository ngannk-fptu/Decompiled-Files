/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.websocket.DeploymentException
 *  javax.websocket.server.ServerContainer
 *  javax.websocket.server.ServerEndpoint
 *  javax.websocket.server.ServerEndpointConfig
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.SmartInitializingSingleton
 *  org.springframework.context.ApplicationContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.context.support.WebApplicationObjectSupport
 */
package org.springframework.web.socket.server.standard;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationObjectSupport;

public class ServerEndpointExporter
extends WebApplicationObjectSupport
implements InitializingBean,
SmartInitializingSingleton {
    @Nullable
    private List<Class<?>> annotatedEndpointClasses;
    @Nullable
    private ServerContainer serverContainer;

    public void setAnnotatedEndpointClasses(Class<?> ... annotatedEndpointClasses) {
        this.annotatedEndpointClasses = Arrays.asList(annotatedEndpointClasses);
    }

    public void setServerContainer(@Nullable ServerContainer serverContainer) {
        this.serverContainer = serverContainer;
    }

    @Nullable
    protected ServerContainer getServerContainer() {
        return this.serverContainer;
    }

    protected void initServletContext(ServletContext servletContext) {
        if (this.serverContainer == null) {
            this.serverContainer = (ServerContainer)servletContext.getAttribute("javax.websocket.server.ServerContainer");
        }
    }

    protected boolean isContextRequired() {
        return false;
    }

    public void afterPropertiesSet() {
        Assert.state((this.getServerContainer() != null ? 1 : 0) != 0, (String)"javax.websocket.server.ServerContainer not available");
    }

    public void afterSingletonsInstantiated() {
        this.registerEndpoints();
    }

    protected void registerEndpoints() {
        ApplicationContext context;
        LinkedHashSet<Class> endpointClasses = new LinkedHashSet<Class>();
        if (this.annotatedEndpointClasses != null) {
            endpointClasses.addAll(this.annotatedEndpointClasses);
        }
        if ((context = this.getApplicationContext()) != null) {
            String[] endpointBeanNames = context.getBeanNamesForAnnotation(ServerEndpoint.class);
            for (String beanName : endpointBeanNames) {
                endpointClasses.add(context.getType(beanName));
            }
        }
        for (Class endpointClass : endpointClasses) {
            this.registerEndpoint(endpointClass);
        }
        if (context != null) {
            Map endpointConfigMap = context.getBeansOfType(ServerEndpointConfig.class);
            for (ServerEndpointConfig endpointConfig : endpointConfigMap.values()) {
                this.registerEndpoint(endpointConfig);
            }
        }
    }

    private void registerEndpoint(Class<?> endpointClass) {
        ServerContainer serverContainer = this.getServerContainer();
        Assert.state((serverContainer != null ? 1 : 0) != 0, (String)"No ServerContainer set. Most likely the server's own WebSocket ServletContainerInitializer has not run yet. Was the Spring ApplicationContext refreshed through a org.springframework.web.context.ContextLoaderListener, i.e. after the ServletContext has been fully initialized?");
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Registering @ServerEndpoint class: " + endpointClass));
            }
            serverContainer.addEndpoint(endpointClass);
        }
        catch (DeploymentException ex) {
            throw new IllegalStateException("Failed to register @ServerEndpoint class: " + endpointClass, ex);
        }
    }

    private void registerEndpoint(ServerEndpointConfig endpointConfig) {
        ServerContainer serverContainer = this.getServerContainer();
        Assert.state((serverContainer != null ? 1 : 0) != 0, (String)"No ServerContainer set");
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Registering ServerEndpointConfig: " + endpointConfig));
            }
            serverContainer.addEndpoint(endpointConfig);
        }
        catch (DeploymentException ex) {
            throw new IllegalStateException("Failed to register ServerEndpointConfig: " + endpointConfig, ex);
        }
    }
}

