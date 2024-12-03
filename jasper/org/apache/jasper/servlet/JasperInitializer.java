/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContainerInitializer
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.jsp.JspFactory
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.SimpleInstanceManager
 */
package org.apache.jasper.servlet;

import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspFactory;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.TldCache;
import org.apache.jasper.runtime.JspFactoryImpl;
import org.apache.jasper.security.SecurityClassLoad;
import org.apache.jasper.servlet.TldScanner;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.xml.sax.SAXException;

public class JasperInitializer
implements ServletContainerInitializer {
    private static final String MSG = "org.apache.jasper.servlet.JasperInitializer";
    private final Log log = LogFactory.getLog(JasperInitializer.class);

    public void onStartup(Set<Class<?>> types, ServletContext context) throws ServletException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)Localizer.getMessage("org.apache.jasper.servlet.JasperInitializer.onStartup", context.getServletContextName()));
        }
        if (context.getAttribute(InstanceManager.class.getName()) == null) {
            context.setAttribute(InstanceManager.class.getName(), (Object)new SimpleInstanceManager());
        }
        boolean validate = Boolean.parseBoolean(context.getInitParameter("org.apache.jasper.XML_VALIDATE_TLD"));
        String blockExternalString = context.getInitParameter("org.apache.jasper.XML_BLOCK_EXTERNAL");
        boolean blockExternal = blockExternalString == null ? true : Boolean.parseBoolean(blockExternalString);
        TldScanner scanner = this.newTldScanner(context, true, validate, blockExternal);
        try {
            scanner.scan();
        }
        catch (IOException | SAXException e) {
            throw new ServletException((Throwable)e);
        }
        for (String listener : scanner.getListeners()) {
            context.addListener(listener);
        }
        context.setAttribute(TldCache.SERVLET_CONTEXT_ATTRIBUTE_NAME, (Object)new TldCache(context, scanner.getUriTldResourcePathMap(), scanner.getTldResourcePathTaglibXmlMap()));
    }

    protected TldScanner newTldScanner(ServletContext context, boolean namespaceAware, boolean validate, boolean blockExternal) {
        return new TldScanner(context, namespaceAware, validate, blockExternal);
    }

    static {
        JspFactoryImpl factory = new JspFactoryImpl();
        SecurityClassLoad.securityClassLoad(((Object)((Object)factory)).getClass().getClassLoader());
        if (JspFactory.getDefaultFactory() == null) {
            JspFactory.setDefaultFactory((JspFactory)factory);
        }
    }
}

