/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.context;

import java.util.EventListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.lang.Nullable;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

public abstract class AbstractContextLoaderInitializer
implements WebApplicationInitializer {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        this.registerContextLoaderListener(servletContext);
    }

    protected void registerContextLoaderListener(ServletContext servletContext) {
        WebApplicationContext rootAppContext = this.createRootApplicationContext();
        if (rootAppContext != null) {
            ContextLoaderListener listener = new ContextLoaderListener(rootAppContext);
            listener.setContextInitializers(this.getRootApplicationContextInitializers());
            servletContext.addListener((EventListener)((Object)listener));
        } else {
            this.logger.debug((Object)"No ContextLoaderListener registered, as createRootApplicationContext() did not return an application context");
        }
    }

    @Nullable
    protected abstract WebApplicationContext createRootApplicationContext();

    @Nullable
    protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers() {
        return null;
    }
}

