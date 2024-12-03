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
 *  org.apache.sling.api.SlingException
 *  org.apache.sling.api.scripting.ScriptEvaluationException
 *  org.apache.sling.api.scripting.SlingBindings
 *  org.apache.sling.commons.classloader.DynamicClassLoader
 */
package org.apache.sling.scripting.jsp.jasper.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.scripting.ScriptEvaluationException;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.commons.classloader.DynamicClassLoader;
import org.apache.sling.scripting.jsp.SlingPageException;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.Options;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.JavacErrorDetail;
import org.apache.sling.scripting.jsp.jasper.compiler.JspRuntimeContext;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.runtime.AnnotationProcessor;
import org.apache.sling.scripting.jsp.jasper.runtime.JspSourceDependent;

public class JspServletWrapper {
    private final Log log = LogFactory.getLog(JspServletWrapper.class);
    private final ServletConfig config;
    private final Options options;
    private final boolean isTagFile;
    private final String jspUri;
    private final JspCompilationContext ctxt;
    private volatile Servlet theServlet;
    private volatile Class<?> tagFileClass;
    private volatile long available = 0L;
    private volatile JasperException compileException;
    private volatile int tripCount;
    private volatile List<String> dependents;

    public JspServletWrapper(ServletConfig config, Options options, String jspUri, boolean isErrorPage, JspRuntimeContext rctxt) {
        this.isTagFile = false;
        this.config = config;
        this.options = options;
        this.jspUri = jspUri;
        this.ctxt = new JspCompilationContext(jspUri, isErrorPage, options, config.getServletContext(), rctxt);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Creating new wrapper for servlet " + jspUri);
        }
    }

    public JspServletWrapper(ServletConfig config, Options options, String jspUri, boolean isErrorPage, JspRuntimeContext rctxt, Servlet servlet) {
        this.isTagFile = false;
        this.config = config;
        this.options = options;
        this.jspUri = jspUri;
        this.ctxt = new JspCompilationContext(jspUri, isErrorPage, options, config.getServletContext(), rctxt, null);
        this.theServlet = servlet;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Creating new wrapper for servlet " + jspUri);
        }
    }

    public JspServletWrapper(ServletContext servletContext, Options options, String tagFilePath, TagInfo tagInfo, JspRuntimeContext rctxt, URL tagFileJarUrl) throws JasperException {
        this.isTagFile = true;
        this.config = null;
        this.options = options;
        this.jspUri = tagFilePath;
        this.ctxt = new JspCompilationContext(this.jspUri, tagInfo, options, servletContext, rctxt, tagFileJarUrl);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Creating new wrapper for tagfile " + this.jspUri);
        }
    }

    public JspCompilationContext getJspEngineContext() {
        return this.ctxt;
    }

    public boolean isValid() {
        if (this.theServlet != null && this.theServlet.getClass().getClassLoader() instanceof DynamicClassLoader) {
            return ((DynamicClassLoader)this.theServlet.getClass().getClassLoader()).isLive();
        }
        return true;
    }

    private Servlet loadServlet() throws ServletException, IOException {
        Servlet servlet = null;
        try {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Loading servlet " + this.jspUri);
            }
            servlet = (Servlet)this.ctxt.load().newInstance();
            AnnotationProcessor annotationProcessor = (AnnotationProcessor)this.config.getServletContext().getAttribute(AnnotationProcessor.class.getName());
            if (annotationProcessor != null) {
                annotationProcessor.processAnnotations(servlet);
                annotationProcessor.postConstruct(servlet);
            }
            List<String> oldDeps = this.dependents;
            if (servlet != null && servlet instanceof JspSourceDependent) {
                this.dependents = (List)((JspSourceDependent)servlet).getDependants();
                if (this.dependents == null) {
                    this.dependents = Collections.EMPTY_LIST;
                }
                this.ctxt.getRuntimeContext().addJspDependencies(this, this.dependents);
            }
            if (!this.equals(oldDeps, this.dependents)) {
                this.persistDependencies();
            }
        }
        catch (IllegalAccessException e) {
            throw new JasperException(e);
        }
        catch (InstantiationException e) {
            throw new JasperException(e);
        }
        catch (Exception e) {
            throw new JasperException(e);
        }
        servlet.init(this.config);
        return servlet;
    }

    public String getDependencyFilePath() {
        String name = this.isTagFile ? this.ctxt.getTagInfo().getTagClassName() : this.ctxt.getServletPackageName() + "." + this.ctxt.getServletClassName();
        String path = ":/" + name.replace('.', '/') + ".deps";
        return path;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void persistDependencies() {
        String path = this.getDependencyFilePath();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Writing dependencies for " + this.jspUri);
        }
        if (this.dependents != null && this.dependents.size() > 0) {
            OutputStream os = null;
            try {
                os = this.ctxt.getRuntimeContext().getIOProvider().getOutputStream(path);
                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
                for (String dep : this.dependents) {
                    writer.write(dep);
                    writer.write("\n");
                }
                writer.flush();
            }
            catch (IOException ioe) {
                this.log.warn("Unable to write dependenies file " + path + " : " + ioe.getMessage(), ioe);
            }
            finally {
                if (os != null) {
                    try {
                        os.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        } else {
            this.ctxt.getRuntimeContext().getIOProvider().delete(path);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class<?> loadTagFile() throws JasperException {
        if (this.compileException != null) {
            throw this.compileException;
        }
        if (this.tagFileClass == null) {
            JspServletWrapper jspServletWrapper = this;
            synchronized (jspServletWrapper) {
                if (this.tagFileClass == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Compiling tagfile " + this.jspUri);
                    }
                    this.compileException = this.ctxt.compile();
                    if (this.compileException != null) {
                        throw this.compileException;
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Loading tagfile " + this.jspUri);
                    }
                    this.tagFileClass = this.ctxt.load();
                    try {
                        Object tag = this.tagFileClass.newInstance();
                        List<String> oldDeps = this.dependents;
                        if (tag != null && tag instanceof JspSourceDependent) {
                            this.dependents = (List)((JspSourceDependent)tag).getDependants();
                            this.ctxt.getRuntimeContext().addJspDependencies(this, this.dependents);
                            if (this.dependents == null) {
                                this.dependents = Collections.EMPTY_LIST;
                            }
                        }
                        if (!this.equals(oldDeps, this.dependents)) {
                            this.persistDependencies();
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
        }
        return this.tagFileClass;
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
    public List<String> getDependants() {
        if (this.dependents == null) {
            JspServletWrapper jspServletWrapper = this;
            synchronized (jspServletWrapper) {
                if (this.dependents == null) {
                    String path = this.getDependencyFilePath();
                    InputStream is = null;
                    try {
                        is = this.ctxt.getRuntimeContext().getIOProvider().getInputStream(path);
                        if (is != null) {
                            String line;
                            if (this.log.isDebugEnabled()) {
                                this.log.debug("Loading dependencies for " + this.jspUri);
                            }
                            ArrayList<String> deps = new ArrayList<String>();
                            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
                            LineNumberReader lnr = new LineNumberReader(reader);
                            while ((line = lnr.readLine()) != null) {
                                deps.add(line.trim());
                            }
                            this.dependents = deps;
                        }
                    }
                    catch (IOException iOException) {
                    }
                    finally {
                        if (is != null) {
                            try {
                                is.close();
                            }
                            catch (IOException iOException) {}
                        }
                    }
                    if (this.dependents == null) {
                        this.dependents = Collections.emptyList();
                    }
                }
            }
        }
        return this.dependents;
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

    private boolean isOutDated() {
        String targetFile = this.ctxt.getClassFileName();
        long targetLastModified = this.ctxt.getRuntimeContext().getIOProvider().lastModified(targetFile);
        if (targetLastModified < 0L) {
            return true;
        }
        String jsp = this.ctxt.getJspFile();
        long jspRealLastModified = this.ctxt.getRuntimeContext().getIOProvider().lastModified(jsp);
        if (targetLastModified < jspRealLastModified) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Compiler: outdated: " + targetFile + " " + targetLastModified);
            }
            return true;
        }
        List<String> depends = this.getDependants();
        if (depends != null) {
            for (String include : depends) {
                long includeLastModified;
                if (include.startsWith("tld:") || (includeLastModified = this.ctxt.getRuntimeContext().getIOProvider().lastModified(include)) <= targetLastModified) continue;
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Compiler: outdated: " + targetFile + " because of dependency " + include + " : " + targetLastModified + " - " + includeLastModified);
                }
                return true;
            }
        }
        return false;
    }

    private void prepareServlet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (this.isOutDated()) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Compiling servlet " + this.jspUri);
            }
            this.compileException = this.ctxt.compile();
            if (this.compileException != null) {
                throw this.compileException;
            }
        }
        this.theServlet = this.loadServlet();
    }

    public void service(SlingBindings bindings) {
        try {
            this.service((HttpServletRequest)bindings.getRequest(), (HttpServletResponse)bindings.getResponse());
        }
        catch (ServletException se) {
            Throwable t;
            if (se.getRootCause() != null && (t = se.getRootCause()) instanceof Exception) {
                this.handleJspException((Exception)t);
            }
            this.handleJspException((Exception)((Object)se));
        }
        catch (SlingPageException se) {
            throw se;
        }
        catch (Exception ex) {
            this.handleJspException(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        block17: {
            try {
                JspServletWrapper jspServletWrapper;
                if (this.available > 0L && this.available < Long.MAX_VALUE) {
                    if (this.available > System.currentTimeMillis()) {
                        response.setDateHeader("Retry-After", this.available);
                        response.sendError(503, Localizer.getMessage("jsp.error.unavailable"));
                        return;
                    }
                    this.available = 0L;
                }
                if (this.theServlet == null) {
                    jspServletWrapper = this;
                    synchronized (jspServletWrapper) {
                        if (this.compileException != null) {
                            throw this.compileException;
                        }
                        if (this.theServlet == null) {
                            this.prepareServlet(request, response);
                        }
                    }
                }
                if (this.compileException != null) {
                    throw this.compileException;
                }
                if (this.theServlet instanceof SingleThreadModel) {
                    jspServletWrapper = this;
                    synchronized (jspServletWrapper) {
                        this.theServlet.service((ServletRequest)request, (ServletResponse)response);
                        break block17;
                    }
                }
                this.theServlet.service((ServletRequest)request, (ServletResponse)response);
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
                return;
            }
        }
    }

    public void destroy(boolean deleteGeneratedFiles) {
        if (this.isTagFile) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Destroying tagfile " + this.jspUri);
            }
            this.tagFileClass = null;
            if (deleteGeneratedFiles) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Deleting generated files for tagfile " + this.jspUri);
                }
                this.ctxt.getRuntimeContext().getIOProvider().delete(this.getDependencyFilePath());
            }
        } else {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Destroying servlet " + this.jspUri);
            }
            if (this.theServlet != null) {
                if (deleteGeneratedFiles) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug("Deleting generated files for servlet " + this.jspUri);
                    }
                    String name = this.isTagFile ? this.ctxt.getTagInfo().getTagClassName() : this.ctxt.getServletPackageName() + "." + this.ctxt.getServletClassName();
                    String path = ":/" + name.replace('.', '/') + ".class";
                    this.ctxt.getRuntimeContext().getIOProvider().delete(path);
                    this.ctxt.getRuntimeContext().getIOProvider().delete(this.getDependencyFilePath());
                    Compiler c = this.ctxt.getCompiler();
                    if (c != null) {
                        c.removeGeneratedFiles();
                    }
                }
                this.theServlet.destroy();
                AnnotationProcessor annotationProcessor = (AnnotationProcessor)this.config.getServletContext().getAttribute(AnnotationProcessor.class.getName());
                if (annotationProcessor != null) {
                    try {
                        annotationProcessor.preDestroy(this.theServlet);
                    }
                    catch (Exception e) {
                        this.log.error(Localizer.getMessage("jsp.error.file.not.found", e.getMessage()), e);
                    }
                }
                this.theServlet = null;
            }
        }
    }

    private RuntimeException wrapException(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException)e;
        }
        return new ScriptEvaluationException(this.ctxt.getJspFile(), e.getMessage() == null ? e.toString() : e.getMessage(), (Throwable)e);
    }

    protected void handleJspException(Exception ex) {
        Throwable result = null;
        try {
            StackTraceElement[] frames = ex.getStackTrace();
            StackTraceElement jspFrame = null;
            for (int i = 0; i < frames.length; ++i) {
                if (!frames[i].getClassName().equals(this.ctxt.getClassName())) continue;
                jspFrame = frames[i];
                break;
            }
            if (jspFrame != null) {
                int javaLineNumber = jspFrame.getLineNumber();
                JavacErrorDetail detail = ErrorDispatcher.createJavacError(jspFrame.getMethodName(), this.ctxt.activateCompiler().getPageNodes(), null, javaLineNumber, this.ctxt);
                int jspLineNumber = detail.getJspBeginLineNumber();
                if (jspLineNumber > 0) {
                    String origMsg = ex.getMessage() == null ? ex.toString() : ex.getMessage();
                    String message = this.options.getDisplaySourceFragment() && detail.getJspExtract() != null ? Localizer.getMessage("jsp.exception", detail.getJspFileName(), String.valueOf(jspLineNumber)).concat(" : ").concat(origMsg).concat("\n\n").concat(detail.getJspExtract()).concat("\n") : Localizer.getMessage("jsp.exception", detail.getJspFileName(), String.valueOf(jspLineNumber)).concat(" : ").concat(origMsg);
                    result = new SlingException(message, (Throwable)ex);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (result == null) {
            result = this.wrapException(ex);
        }
        throw result;
    }

    private boolean equals(List<String> oldDeps, List<String> newDeps) {
        if (oldDeps == null) {
            return newDeps == null || newDeps.size() == 0;
        }
        if (oldDeps.size() != newDeps.size()) {
            return false;
        }
        Iterator<String> i1 = oldDeps.iterator();
        Iterator<String> i2 = newDeps.iterator();
        while (i1.hasNext()) {
            if (i1.next().equals(i2.next())) continue;
            return false;
        }
        return true;
    }
}

