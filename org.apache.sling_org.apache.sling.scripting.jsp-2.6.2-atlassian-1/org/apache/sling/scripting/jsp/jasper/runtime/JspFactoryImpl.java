/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.jsp.JspApplicationContext
 *  javax.servlet.jsp.JspEngineInfo
 *  javax.servlet.jsp.JspFactory
 *  javax.servlet.jsp.PageContext
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.Constants;
import org.apache.sling.scripting.jsp.jasper.runtime.JspApplicationContextImpl;
import org.apache.sling.scripting.jsp.jasper.runtime.PageContextImpl;

public class JspFactoryImpl
extends JspFactory {
    private Log log = LogFactory.getLog(JspFactoryImpl.class);
    private static final String SPEC_VERSION = "2.1";

    public PageContext getPageContext(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoflush) {
        if (Constants.IS_SECURITY_ENABLED) {
            PrivilegedGetPageContext dp = new PrivilegedGetPageContext(this, servlet, request, response, errorPageURL, needsSession, bufferSize, autoflush);
            return (PageContext)AccessController.doPrivileged(dp);
        }
        return this.internalGetPageContext(servlet, request, response, errorPageURL, needsSession, bufferSize, autoflush);
    }

    public void releasePageContext(PageContext pc) {
        if (pc == null) {
            return;
        }
        if (Constants.IS_SECURITY_ENABLED) {
            PrivilegedReleasePageContext dp = new PrivilegedReleasePageContext(this, pc);
            AccessController.doPrivileged(dp);
        } else {
            this.internalReleasePageContext(pc);
        }
    }

    public JspEngineInfo getEngineInfo() {
        return new JspEngineInfo(){

            public String getSpecificationVersion() {
                return JspFactoryImpl.SPEC_VERSION;
            }
        };
    }

    private PageContext internalGetPageContext(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoflush) {
        try {
            PageContextImpl pc = new PageContextImpl();
            pc.initialize(servlet, request, response, errorPageURL, needsSession, bufferSize, autoflush);
            return pc;
        }
        catch (Throwable ex) {
            this.log.fatal("Exception initializing page context", ex);
            return null;
        }
    }

    private void internalReleasePageContext(PageContext pc) {
        pc.release();
    }

    public JspApplicationContext getJspApplicationContext(ServletContext context) {
        return JspApplicationContextImpl.getInstance(context);
    }

    private class PrivilegedReleasePageContext
    implements PrivilegedAction {
        private JspFactoryImpl factory;
        private PageContext pageContext;

        PrivilegedReleasePageContext(JspFactoryImpl factory, PageContext pageContext) {
            this.factory = factory;
            this.pageContext = pageContext;
        }

        public Object run() {
            this.factory.internalReleasePageContext(this.pageContext);
            return null;
        }
    }

    private class PrivilegedGetPageContext
    implements PrivilegedAction {
        private JspFactoryImpl factory;
        private Servlet servlet;
        private ServletRequest request;
        private ServletResponse response;
        private String errorPageURL;
        private boolean needsSession;
        private int bufferSize;
        private boolean autoflush;

        PrivilegedGetPageContext(JspFactoryImpl factory, Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoflush) {
            this.factory = factory;
            this.servlet = servlet;
            this.request = request;
            this.response = response;
            this.errorPageURL = errorPageURL;
            this.needsSession = needsSession;
            this.bufferSize = bufferSize;
            this.autoflush = autoflush;
        }

        public Object run() {
            return this.factory.internalGetPageContext(this.servlet, this.request, this.response, this.errorPageURL, this.needsSession, this.bufferSize, this.autoflush);
        }
    }
}

