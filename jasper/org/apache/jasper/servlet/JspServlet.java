/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.PeriodicEventListener
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.jasper.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jasper.Constants;
import org.apache.jasper.EmbeddedServletOptions;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.runtime.ExceptionUtils;
import org.apache.jasper.security.SecurityUtil;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.PeriodicEventListener;
import org.apache.tomcat.util.security.Escape;

public class JspServlet
extends HttpServlet
implements PeriodicEventListener {
    private static final long serialVersionUID = 1L;
    private final transient Log log = LogFactory.getLog(JspServlet.class);
    private transient ServletContext context;
    private ServletConfig config;
    private transient Options options;
    private transient JspRuntimeContext rctxt;
    private String jspFile;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
        this.context = config.getServletContext();
        String engineOptionsName = config.getInitParameter("engineOptionsClass");
        if (Constants.IS_SECURITY_ENABLED && engineOptionsName != null) {
            this.log.info((Object)Localizer.getMessage("jsp.info.ignoreSetting", "engineOptionsClass", engineOptionsName));
            engineOptionsName = null;
        }
        if (engineOptionsName != null) {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                Class<?> engineOptionsClass = loader.loadClass(engineOptionsName);
                Class[] ctorSig = new Class[]{ServletConfig.class, ServletContext.class};
                Constructor<?> ctor = engineOptionsClass.getConstructor(ctorSig);
                Object[] args = new Object[]{config, this.context};
                this.options = (Options)ctor.newInstance(args);
            }
            catch (Throwable e) {
                e = ExceptionUtils.unwrapInvocationTargetException(e);
                ExceptionUtils.handleThrowable(e);
                this.log.warn((Object)Localizer.getMessage("jsp.warning.engineOptionsClass", engineOptionsName), e);
                this.options = new EmbeddedServletOptions(config, this.context);
            }
        } else {
            this.options = new EmbeddedServletOptions(config, this.context);
        }
        this.rctxt = new JspRuntimeContext(this.context, this.options);
        if (config.getInitParameter("jspFile") != null) {
            this.jspFile = config.getInitParameter("jspFile");
            try {
                if (null == this.context.getResource(this.jspFile)) {
                    return;
                }
            }
            catch (MalformedURLException e) {
                throw new ServletException(Localizer.getMessage("jsp.error.no.jsp", this.jspFile), (Throwable)e);
            }
            try {
                if (SecurityUtil.isPackageProtectionEnabled()) {
                    AccessController.doPrivileged(() -> {
                        this.serviceJspFile(null, null, this.jspFile, true);
                        return null;
                    });
                } else {
                    this.serviceJspFile(null, null, this.jspFile, true);
                }
            }
            catch (IOException e) {
                throw new ServletException(Localizer.getMessage("jsp.error.precompilation", this.jspFile), (Throwable)e);
            }
            catch (PrivilegedActionException e) {
                Throwable t = e.getCause();
                if (t instanceof ServletException) {
                    throw (ServletException)t;
                }
                throw new ServletException(Localizer.getMessage("jsp.error.precompilation", this.jspFile), (Throwable)e);
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)Localizer.getMessage("jsp.message.scratch.dir.is", this.options.getScratchDir().toString()));
            this.log.debug((Object)Localizer.getMessage("jsp.message.dont.modify.servlets"));
        }
    }

    public int getJspCount() {
        return this.rctxt.getJspCount();
    }

    public void setJspReloadCount(int count) {
        this.rctxt.setJspReloadCount(count);
    }

    public int getJspReloadCount() {
        return this.rctxt.getJspReloadCount();
    }

    public int getJspQueueLength() {
        return this.rctxt.getJspQueueLength();
    }

    public int getJspUnloadCount() {
        return this.rctxt.getJspUnloadCount();
    }

    boolean preCompile(HttpServletRequest request) throws ServletException {
        String value;
        String queryString = request.getQueryString();
        if (queryString == null) {
            return false;
        }
        int start = queryString.indexOf(Constants.PRECOMPILE);
        if (start < 0) {
            return false;
        }
        if ((queryString = queryString.substring(start + Constants.PRECOMPILE.length())).length() == 0) {
            return true;
        }
        if (queryString.startsWith("&")) {
            return true;
        }
        if (!queryString.startsWith("=")) {
            return false;
        }
        int limit = queryString.length();
        int ampersand = queryString.indexOf(38);
        if (ampersand > 0) {
            limit = ampersand;
        }
        if ((value = queryString.substring(1, limit)).equals("true")) {
            return true;
        }
        if (value.equals("false")) {
            return true;
        }
        throw new ServletException(Localizer.getMessage("jsp.error.precompilation.parameter", Constants.PRECOMPILE, value));
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jspUri = this.jspFile;
        if (jspUri == null) {
            String pathInfo;
            jspUri = (String)request.getAttribute("javax.servlet.include.servlet_path");
            if (jspUri != null) {
                pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
                if (pathInfo != null) {
                    jspUri = jspUri + pathInfo;
                }
            } else {
                jspUri = request.getServletPath();
                pathInfo = request.getPathInfo();
                if (pathInfo != null) {
                    jspUri = jspUri + pathInfo;
                }
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("JspEngine --> " + jspUri));
            this.log.debug((Object)("\t     ServletPath: " + request.getServletPath()));
            this.log.debug((Object)("\t        PathInfo: " + request.getPathInfo()));
            this.log.debug((Object)("\t        RealPath: " + this.context.getRealPath(jspUri)));
            this.log.debug((Object)("\t      RequestURI: " + request.getRequestURI()));
            this.log.debug((Object)("\t     QueryString: " + request.getQueryString()));
        }
        try {
            boolean precompile = this.preCompile(request);
            this.serviceJspFile(request, response, jspUri, precompile);
        }
        catch (IOException | RuntimeException | ServletException e) {
            throw e;
        }
        catch (Throwable e) {
            ExceptionUtils.handleThrowable(e);
            throw new ServletException(e);
        }
    }

    public void destroy() {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"JspServlet.destroy()");
        }
        this.rctxt.destroy();
    }

    public void periodicEvent() {
        this.rctxt.checkUnload();
        this.rctxt.checkCompile();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void serviceJspFile(HttpServletRequest request, HttpServletResponse response, String jspUri, boolean precompile) throws ServletException, IOException {
        JspServletWrapper wrapper = this.rctxt.getWrapper(jspUri);
        if (wrapper == null) {
            JspServlet jspServlet = this;
            synchronized (jspServlet) {
                wrapper = this.rctxt.getWrapper(jspUri);
                if (wrapper == null) {
                    if (null == this.context.getResource(jspUri)) {
                        this.handleMissingResource(request, response, jspUri);
                        return;
                    }
                    wrapper = new JspServletWrapper(this.config, this.options, jspUri, this.rctxt);
                    this.rctxt.addWrapper(jspUri, wrapper);
                }
            }
        }
        try {
            wrapper.service(request, response, precompile);
        }
        catch (FileNotFoundException fnfe) {
            this.handleMissingResource(request, response, jspUri);
        }
    }

    private void handleMissingResource(HttpServletRequest request, HttpServletResponse response, String jspUri) throws ServletException, IOException {
        String includeRequestUri = (String)request.getAttribute("javax.servlet.include.request_uri");
        String msg = Localizer.getMessage("jsp.error.file.not.found", jspUri);
        if (includeRequestUri != null) {
            throw new ServletException(Escape.htmlElementContent((String)msg));
        }
        try {
            response.sendError(404, msg);
        }
        catch (IllegalStateException ise) {
            this.log.error((Object)msg);
        }
    }
}

