/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebService
 *  javax.servlet.ServletContainerInitializer
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.annotation.HandlesTypes
 *  javax.xml.ws.WebServiceProvider
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.xml.ws.transport.http.servlet.WSServletContextListener;
import java.net.URL;
import java.util.EventListener;
import java.util.Set;
import javax.jws.WebService;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.xml.ws.WebServiceProvider;

@HandlesTypes(value={WebService.class, WebServiceProvider.class})
public class WSServletContainerInitializer
implements ServletContainerInitializer {
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        try {
            URL sunJaxWsXml;
            if (c != null && !c.isEmpty() && (sunJaxWsXml = ctx.getResource("/WEB-INF/sun-jaxws.xml")) != null) {
                WSServletContextListener listener = new WSServletContextListener();
                listener.parseAdaptersAndCreateDelegate(ctx);
                ctx.addListener((EventListener)((Object)listener));
            }
        }
        catch (Exception e) {
            throw new ServletException((Throwable)e);
        }
    }
}

