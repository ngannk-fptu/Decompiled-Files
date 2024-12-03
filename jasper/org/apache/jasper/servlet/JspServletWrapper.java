/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.SingleThreadModel
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.tagext.TagInfo
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.Jar
 */
package org.apache.jasper.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SingleThreadModel;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.TagInfo;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.JavacErrorDetail;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.SmapInput;
import org.apache.jasper.compiler.SmapStratum;
import org.apache.jasper.runtime.ExceptionUtils;
import org.apache.jasper.runtime.InstanceManagerFactory;
import org.apache.jasper.runtime.JspSourceDependent;
import org.apache.jasper.util.FastRemovalDequeue;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.Jar;

public class JspServletWrapper {
    private static final Map<String, Long> ALWAYS_OUTDATED_DEPENDENCIES = new HashMap<String, Long>();
    private final Log log = LogFactory.getLog(JspServletWrapper.class);
    private volatile Servlet theServlet;
    private final String jspUri;
    private volatile Class<?> tagHandlerClass;
    private final JspCompilationContext ctxt;
    private long available = 0L;
    private final ServletConfig config;
    private final Options options;
    private volatile boolean mustCompile = true;
    private volatile boolean reload = true;
    private final boolean isTagFile;
    private int tripCount;
    private JasperException compileException;
    private volatile long servletClassLastModifiedTime;
    private long lastModificationTest = 0L;
    private long lastUsageTime = System.currentTimeMillis();
    private FastRemovalDequeue.Entry unloadHandle;
    private final boolean unloadAllowed;
    private final boolean unloadByCount;
    private final boolean unloadByIdle;

    public JspServletWrapper(ServletConfig config, Options options, String jspUri, JspRuntimeContext rctxt) {
        this.isTagFile = false;
        this.config = config;
        this.options = options;
        this.jspUri = jspUri;
        this.unloadByCount = options.getMaxLoadedJsps() > 0;
        this.unloadByIdle = options.getJspIdleTimeout() > 0;
        this.unloadAllowed = this.unloadByCount || this.unloadByIdle;
        this.ctxt = new JspCompilationContext(jspUri, options, config.getServletContext(), this, rctxt);
    }

    public JspServletWrapper(ServletContext servletContext, Options options, String tagFilePath, TagInfo tagInfo, JspRuntimeContext rctxt, Jar tagJar) {
        this.isTagFile = true;
        this.config = null;
        this.options = options;
        this.jspUri = tagFilePath;
        this.tripCount = 0;
        this.unloadByCount = options.getMaxLoadedJsps() > 0;
        this.unloadByIdle = options.getJspIdleTimeout() > 0;
        this.unloadAllowed = this.unloadByCount || this.unloadByIdle;
        this.ctxt = new JspCompilationContext(this.jspUri, tagInfo, options, servletContext, this, rctxt, tagJar);
    }

    public JspCompilationContext getJspEngineContext() {
        return this.ctxt;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    public boolean getReload() {
        return this.reload;
    }

    private boolean getReloadInternal() {
        return this.reload && !this.ctxt.getRuntimeContext().isCompileCheckInProgress();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Servlet getServlet() throws ServletException {
        if (this.getReloadInternal() || this.theServlet == null) {
            JspServletWrapper jspServletWrapper = this;
            synchronized (jspServletWrapper) {
                if (this.getReloadInternal() || this.theServlet == null) {
                    Servlet servlet;
                    this.destroy();
                    try {
                        InstanceManager instanceManager = InstanceManagerFactory.getInstanceManager(this.config);
                        servlet = (Servlet)instanceManager.newInstance(this.ctxt.getFQCN(), this.ctxt.getJspLoader());
                    }
                    catch (Exception e) {
                        Throwable t = ExceptionUtils.unwrapInvocationTargetException(e);
                        ExceptionUtils.handleThrowable(t);
                        throw new JasperException(t);
                    }
                    servlet.init(this.config);
                    if (this.theServlet != null) {
                        this.ctxt.getRuntimeContext().incrementJspReloadCount();
                    }
                    this.theServlet = servlet;
                    this.reload = false;
                }
            }
        }
        return this.theServlet;
    }

    public ServletContext getServletContext() {
        return this.ctxt.getServletContext();
    }

    public void setCompilationException(JasperException je) {
        this.compileException = je;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setServletClassLastModifiedTime(long lastModified) {
        if (this.servletClassLastModifiedTime < lastModified) {
            JspServletWrapper jspServletWrapper = this;
            synchronized (jspServletWrapper) {
                if (this.servletClassLastModifiedTime < lastModified) {
                    this.servletClassLastModifiedTime = lastModified;
                    this.reload = true;
                    this.ctxt.clearJspLoader();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class<?> loadTagFile() throws JasperException {
        block14: {
            try {
                JspServletWrapper jspServletWrapper;
                if (this.ctxt.isRemoved()) {
                    throw new FileNotFoundException(this.jspUri);
                }
                if (this.options.getDevelopment() || this.mustCompile) {
                    jspServletWrapper = this;
                    synchronized (jspServletWrapper) {
                        if (this.options.getDevelopment() || this.mustCompile) {
                            this.ctxt.compile();
                            this.mustCompile = false;
                        }
                    }
                } else if (this.compileException != null) {
                    throw this.compileException;
                }
                if (!this.getReloadInternal() && this.tagHandlerClass != null) break block14;
                jspServletWrapper = this;
                synchronized (jspServletWrapper) {
                    if (this.getReloadInternal() || this.tagHandlerClass == null) {
                        this.tagHandlerClass = this.ctxt.load();
                        this.reload = false;
                    }
                }
            }
            catch (FileNotFoundException ex) {
                throw new JasperException(ex);
            }
        }
        return this.tagHandlerClass;
    }

    public Class<?> loadTagFilePrototype() throws JasperException {
        this.ctxt.setPrototypeMode(true);
        try {
            Class<?> clazz = this.loadTagFile();
            return clazz;
        }
        finally {
            this.ctxt.setPrototypeMode(false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Long> getDependants() {
        try {
            Object target;
            if (this.isTagFile) {
                if (this.reload) {
                    JspServletWrapper jspServletWrapper = this;
                    synchronized (jspServletWrapper) {
                        if (this.reload) {
                            this.tagHandlerClass = this.ctxt.load();
                            this.reload = false;
                        }
                    }
                }
                target = this.tagHandlerClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                target = this.getServlet();
            }
            if (target instanceof JspSourceDependent) {
                return ((JspSourceDependent)target).getDependants();
            }
        }
        catch (AbstractMethodError ame) {
            return ALWAYS_OUTDATED_DEPENDENCIES;
        }
        catch (Throwable ex) {
            ExceptionUtils.handleThrowable(ex);
        }
        return null;
    }

    public boolean isTagFile() {
        return this.isTagFile;
    }

    public int incTripCount() {
        return this.tripCount++;
    }

    public int decTripCount() {
        return this.tripCount--;
    }

    public String getJspUri() {
        return this.jspUri;
    }

    public FastRemovalDequeue.Entry getUnloadHandle() {
        return this.unloadHandle;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void service(HttpServletRequest request, HttpServletResponse response, boolean precompile) throws ServletException, IOException, FileNotFoundException {
        block41: {
            Servlet servlet;
            try {
                if (this.ctxt.isRemoved()) {
                    throw new FileNotFoundException(this.jspUri);
                }
                if (this.available > 0L && this.available < Long.MAX_VALUE) {
                    if (this.available > System.currentTimeMillis()) {
                        response.setDateHeader("Retry-After", this.available);
                        response.sendError(503, Localizer.getMessage("jsp.error.unavailable"));
                        return;
                    }
                    this.available = 0L;
                }
                if (this.options.getDevelopment() || this.mustCompile) {
                    JspServletWrapper jspServletWrapper = this;
                    synchronized (jspServletWrapper) {
                        if (this.options.getDevelopment() || this.mustCompile) {
                            this.ctxt.compile();
                            this.mustCompile = false;
                        }
                    }
                } else if (this.compileException != null) {
                    throw this.compileException;
                }
                servlet = this.getServlet();
                if (precompile) {
                    return;
                }
            }
            catch (FileNotFoundException fnfe) {
                throw fnfe;
            }
            catch (IOException | IllegalStateException | ServletException ex) {
                if (this.options.getDevelopment()) {
                    throw this.handleJspException((Exception)ex);
                }
                throw ex;
            }
            catch (Exception ex) {
                if (this.options.getDevelopment()) {
                    throw this.handleJspException(ex);
                }
                throw new JasperException(ex);
            }
            try {
                JspServletWrapper ex;
                if (this.unloadAllowed) {
                    ex = this;
                    synchronized (ex) {
                        if (this.unloadByCount) {
                            if (this.unloadHandle == null) {
                                this.unloadHandle = this.ctxt.getRuntimeContext().push(this);
                            } else if (this.lastUsageTime < this.ctxt.getRuntimeContext().getLastJspQueueUpdate()) {
                                this.ctxt.getRuntimeContext().makeYoungest(this.unloadHandle);
                                this.lastUsageTime = System.currentTimeMillis();
                            }
                        } else if (this.lastUsageTime < this.ctxt.getRuntimeContext().getLastJspQueueUpdate()) {
                            this.lastUsageTime = System.currentTimeMillis();
                        }
                    }
                }
                if (servlet instanceof SingleThreadModel) {
                    ex = this;
                    synchronized (ex) {
                        servlet.service((ServletRequest)request, (ServletResponse)response);
                        break block41;
                    }
                }
                servlet.service((ServletRequest)request, (ServletResponse)response);
            }
            catch (UnavailableException ex) {
                String includeRequestUri = (String)request.getAttribute("javax.servlet.include.request_uri");
                if (includeRequestUri != null) {
                    throw ex;
                }
                int unavailableSeconds = ex.getUnavailableSeconds();
                if (unavailableSeconds <= 0) {
                    unavailableSeconds = 60;
                }
                this.available = System.currentTimeMillis() + (long)unavailableSeconds * 1000L;
                response.sendError(503, ex.getMessage());
            }
            catch (IllegalStateException | ServletException ex) {
                if (this.options.getDevelopment()) {
                    throw this.handleJspException((Exception)ex);
                }
                throw ex;
            }
            catch (IOException ex) {
                if (this.options.getDevelopment()) {
                    throw new IOException(this.handleJspException(ex).getMessage(), ex);
                }
                throw ex;
            }
            catch (Exception ex) {
                if (this.options.getDevelopment()) {
                    throw this.handleJspException(ex);
                }
                throw new JasperException(ex);
            }
        }
    }

    public void destroy() {
        if (this.theServlet != null) {
            try {
                this.theServlet.destroy();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.log.error((Object)Localizer.getMessage("jsp.error.servlet.destroy.failed"), t);
            }
            InstanceManager instanceManager = InstanceManagerFactory.getInstanceManager(this.config);
            try {
                instanceManager.destroyInstance((Object)this.theServlet);
            }
            catch (Exception e) {
                Throwable t = ExceptionUtils.unwrapInvocationTargetException(e);
                ExceptionUtils.handleThrowable(t);
                this.log.error((Object)Localizer.getMessage("jsp.error.file.not.found", e.getMessage()), t);
            }
        }
    }

    public long getLastModificationTest() {
        return this.lastModificationTest;
    }

    public void setLastModificationTest(long lastModificationTest) {
        this.lastModificationTest = lastModificationTest;
    }

    public long getLastUsageTime() {
        return this.lastUsageTime;
    }

    protected JasperException handleJspException(Exception ex) {
        try {
            Throwable realException = ex;
            if (ex instanceof ServletException) {
                realException = ((ServletException)((Object)ex)).getRootCause();
            }
            StackTraceElement[] frames = realException.getStackTrace();
            StackTraceElement jspFrame = null;
            String servletPackageName = this.ctxt.getBasePackageName();
            for (StackTraceElement frame : frames) {
                if (!frame.getClassName().startsWith(servletPackageName)) continue;
                jspFrame = frame;
                break;
            }
            SmapStratum smap = null;
            if (jspFrame != null) {
                smap = this.ctxt.getCompiler().getSmap(jspFrame.getClassName());
            }
            if (smap == null) {
                return new JasperException(ex);
            }
            int javaLineNumber = jspFrame.getLineNumber();
            SmapInput source = smap.getInputLineNumber(javaLineNumber);
            if (source.getLineNumber() < 1) {
                throw new JasperException(ex);
            }
            JavacErrorDetail detail = new JavacErrorDetail(jspFrame.getMethodName(), javaLineNumber, source.getFileName(), source.getLineNumber(), null, this.ctxt);
            if (this.options.getDisplaySourceFragment()) {
                return new JasperException(Localizer.getMessage("jsp.exception", detail.getJspFileName(), "" + source.getLineNumber()) + System.lineSeparator() + System.lineSeparator() + detail.getJspExtract() + System.lineSeparator() + System.lineSeparator() + "Stacktrace:", ex);
            }
            return new JasperException(Localizer.getMessage("jsp.exception", detail.getJspFileName(), "" + source.getLineNumber()), ex);
        }
        catch (Exception je) {
            if (ex instanceof JasperException) {
                return (JasperException)((Object)ex);
            }
            return new JasperException(ex);
        }
    }

    static {
        ALWAYS_OUTDATED_DEPENDENCIES.put("/WEB-INF/web.xml", -1L);
    }
}

