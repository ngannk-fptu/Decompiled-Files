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
package org.apache.jasper.runtime;

import java.io.IOException;
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
import org.apache.jasper.Constants;
import org.apache.jasper.runtime.JspApplicationContextImpl;
import org.apache.jasper.runtime.PageContextImpl;

public class JspFactoryImpl
extends JspFactory {
    private static final boolean USE_POOL = Boolean.parseBoolean(System.getProperty("org.apache.jasper.runtime.JspFactoryImpl.USE_POOL", "true"));
    private static final int POOL_SIZE = Integer.parseInt(System.getProperty("org.apache.jasper.runtime.JspFactoryImpl.POOL_SIZE", "8"));
    private final ThreadLocal<PageContextPool> localPool = new ThreadLocal();

    public PageContext getPageContext(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoflush) {
        if (Constants.IS_SECURITY_ENABLED) {
            PrivilegedGetPageContext dp = new PrivilegedGetPageContext(this, servlet, request, response, errorPageURL, needsSession, bufferSize, autoflush);
            return AccessController.doPrivileged(dp);
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
                return "2.3";
            }
        };
    }

    private PageContext internalGetPageContext(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoflush) {
        PageContextImpl pc;
        if (USE_POOL) {
            PageContextPool pool = this.localPool.get();
            if (pool == null) {
                pool = new PageContextPool();
                this.localPool.set(pool);
            }
            if ((pc = pool.get()) == null) {
                pc = new PageContextImpl();
            }
        } else {
            pc = new PageContextImpl();
        }
        try {
            pc.initialize(servlet, request, response, errorPageURL, needsSession, bufferSize, autoflush);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return pc;
    }

    private void internalReleasePageContext(PageContext pc) {
        pc.release();
        if (USE_POOL && pc instanceof PageContextImpl) {
            this.localPool.get().put(pc);
        }
    }

    public JspApplicationContext getJspApplicationContext(ServletContext context) {
        if (Constants.IS_SECURITY_ENABLED) {
            return AccessController.doPrivileged(() -> JspApplicationContextImpl.getInstance(context));
        }
        return JspApplicationContextImpl.getInstance(context);
    }

    private static class PrivilegedGetPageContext
    implements PrivilegedAction<PageContext> {
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

        @Override
        public PageContext run() {
            return this.factory.internalGetPageContext(this.servlet, this.request, this.response, this.errorPageURL, this.needsSession, this.bufferSize, this.autoflush);
        }
    }

    private static class PrivilegedReleasePageContext
    implements PrivilegedAction<Void> {
        private JspFactoryImpl factory;
        private PageContext pageContext;

        PrivilegedReleasePageContext(JspFactoryImpl factory, PageContext pageContext) {
            this.factory = factory;
            this.pageContext = pageContext;
        }

        @Override
        public Void run() {
            this.factory.internalReleasePageContext(this.pageContext);
            return null;
        }
    }

    private static final class PageContextPool {
        private final PageContext[] pool = new PageContext[JspFactoryImpl.access$200()];
        private int current = -1;

        PageContextPool() {
        }

        public void put(PageContext o) {
            if (this.current < POOL_SIZE - 1) {
                ++this.current;
                this.pool[this.current] = o;
            }
        }

        public PageContext get() {
            PageContext item = null;
            if (this.current >= 0) {
                item = this.pool[this.current];
                --this.current;
            }
            return item;
        }
    }
}

