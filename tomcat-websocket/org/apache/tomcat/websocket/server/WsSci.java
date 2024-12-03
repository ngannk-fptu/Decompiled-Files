/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContainerInitializer
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.annotation.HandlesTypes
 *  javax.websocket.ContainerProvider
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  javax.websocket.server.ServerApplicationConfig
 *  javax.websocket.server.ServerEndpoint
 *  javax.websocket.server.ServerEndpointConfig
 *  org.apache.tomcat.util.compat.JreCompat
 */
package org.apache.tomcat.websocket.server;

import java.lang.reflect.Modifier;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.websocket.server.WsContextListener;
import org.apache.tomcat.websocket.server.WsServerContainer;
import org.apache.tomcat.websocket.server.WsSessionListener;

@HandlesTypes(value={ServerEndpoint.class, ServerApplicationConfig.class, Endpoint.class})
public class WsSci
implements ServletContainerInitializer {
    public void onStartup(Set<Class<?>> clazzes, ServletContext ctx) throws ServletException {
        WsServerContainer sc = WsSci.init(ctx, true);
        if (clazzes == null || clazzes.size() == 0) {
            return;
        }
        HashSet<ServerApplicationConfig> serverApplicationConfigs = new HashSet<ServerApplicationConfig>();
        HashSet scannedEndpointClazzes = new HashSet();
        HashSet scannedPojoEndpoints = new HashSet();
        try {
            String wsPackage = ContainerProvider.class.getName();
            wsPackage = wsPackage.substring(0, wsPackage.lastIndexOf(46) + 1);
            for (Class<?> clazz : clazzes) {
                JreCompat jreCompat = JreCompat.getInstance();
                int modifiers = clazz.getModifiers();
                if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || !jreCompat.isExported(clazz) || clazz.getName().startsWith(wsPackage)) continue;
                if (ServerApplicationConfig.class.isAssignableFrom(clazz)) {
                    serverApplicationConfigs.add((ServerApplicationConfig)clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
                }
                if (Endpoint.class.isAssignableFrom(clazz)) {
                    Class<?> endpoint = clazz;
                    scannedEndpointClazzes.add(endpoint);
                }
                if (!clazz.isAnnotationPresent(ServerEndpoint.class)) continue;
                scannedPojoEndpoints.add(clazz);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new ServletException((Throwable)e);
        }
        HashSet filteredEndpointConfigs = new HashSet();
        HashSet filteredPojoEndpoints = new HashSet();
        if (serverApplicationConfigs.isEmpty()) {
            filteredPojoEndpoints.addAll(scannedPojoEndpoints);
        } else {
            for (ServerApplicationConfig serverApplicationConfig : serverApplicationConfigs) {
                Set configFilteredPojos;
                Set configFilteredEndpoints = serverApplicationConfig.getEndpointConfigs(scannedEndpointClazzes);
                if (configFilteredEndpoints != null) {
                    filteredEndpointConfigs.addAll(configFilteredEndpoints);
                }
                if ((configFilteredPojos = serverApplicationConfig.getAnnotatedEndpointClasses(scannedPojoEndpoints)) == null) continue;
                filteredPojoEndpoints.addAll(configFilteredPojos);
            }
        }
        try {
            for (ServerEndpointConfig serverEndpointConfig : filteredEndpointConfigs) {
                sc.addEndpoint(serverEndpointConfig);
            }
            for (Class clazz : filteredPojoEndpoints) {
                sc.addEndpoint(clazz, true);
            }
        }
        catch (DeploymentException deploymentException) {
            throw new ServletException((Throwable)deploymentException);
        }
    }

    static WsServerContainer init(ServletContext servletContext, boolean initBySciMechanism) {
        WsServerContainer sc = new WsServerContainer(servletContext);
        servletContext.setAttribute("javax.websocket.server.ServerContainer", (Object)sc);
        servletContext.addListener((EventListener)((Object)new WsSessionListener(sc)));
        if (initBySciMechanism) {
            servletContext.addListener((EventListener)((Object)new WsContextListener()));
        }
        return sc;
    }
}

