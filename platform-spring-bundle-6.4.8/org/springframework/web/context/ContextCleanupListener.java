/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.context;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

public class ContextCleanupListener
implements ServletContextListener {
    private static final Log logger = LogFactory.getLog(ContextCleanupListener.class);

    public void contextInitialized(ServletContextEvent event) {
    }

    public void contextDestroyed(ServletContextEvent event) {
        ContextCleanupListener.cleanupAttributes(event.getServletContext());
    }

    static void cleanupAttributes(ServletContext servletContext) {
        Enumeration attrNames = servletContext.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            Object attrValue;
            String attrName = (String)attrNames.nextElement();
            if (!attrName.startsWith("org.springframework.") || !((attrValue = servletContext.getAttribute(attrName)) instanceof DisposableBean)) continue;
            try {
                ((DisposableBean)attrValue).destroy();
            }
            catch (Throwable ex) {
                if (!logger.isWarnEnabled()) continue;
                logger.warn((Object)("Invocation of destroy method failed on ServletContext attribute with name '" + attrName + "'"), ex);
            }
        }
    }
}

