/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.jasper.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.jasper.Constants;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.SmapStratum;
import org.apache.jasper.runtime.ExceptionUtils;
import org.apache.jasper.servlet.JspCServletContext;
import org.apache.jasper.servlet.JspServletWrapper;
import org.apache.jasper.util.FastRemovalDequeue;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class JspRuntimeContext {
    private final Log log = LogFactory.getLog(JspRuntimeContext.class);
    private final AtomicInteger jspReloadCount = new AtomicInteger(0);
    private final AtomicInteger jspUnloadCount = new AtomicInteger(0);
    private final ServletContext context;
    private final Options options;
    private final ClassLoader parentClassLoader;
    private final PermissionCollection permissionCollection;
    private final CodeSource codeSource;
    private final String classpath;
    private volatile long lastCompileCheck = -1L;
    private volatile long lastJspQueueUpdate = System.currentTimeMillis();
    private long jspIdleTimeout;
    private final Map<String, JspServletWrapper> jsps = new ConcurrentHashMap<String, JspServletWrapper>();
    private FastRemovalDequeue<JspServletWrapper> jspQueue = null;
    private final Map<String, SmapStratum> smaps = new ConcurrentHashMap<String, SmapStratum>();
    private volatile boolean compileCheckInProgress = false;

    public JspRuntimeContext(ServletContext context, Options options) {
        this.context = context;
        this.options = options;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = this.getClass().getClassLoader();
        }
        if (this.log.isDebugEnabled()) {
            if (loader != null) {
                this.log.debug((Object)Localizer.getMessage("jsp.message.parent_class_loader_is", loader.toString()));
            } else {
                this.log.debug((Object)Localizer.getMessage("jsp.message.parent_class_loader_is", "<none>"));
            }
        }
        this.parentClassLoader = loader;
        this.classpath = this.initClassPath();
        if (context instanceof JspCServletContext) {
            this.codeSource = null;
            this.permissionCollection = null;
            return;
        }
        if (Constants.IS_SECURITY_ENABLED) {
            SecurityHolder holder = this.initSecurity();
            this.codeSource = holder.cs;
            this.permissionCollection = holder.pc;
        } else {
            this.codeSource = null;
            this.permissionCollection = null;
        }
        String appBase = context.getRealPath("/");
        if (!options.getDevelopment() && appBase != null && options.getCheckInterval() > 0) {
            this.lastCompileCheck = System.currentTimeMillis();
        }
        if (options.getMaxLoadedJsps() > 0) {
            this.jspQueue = new FastRemovalDequeue(options.getMaxLoadedJsps());
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)Localizer.getMessage("jsp.message.jsp_queue_created", "" + options.getMaxLoadedJsps(), context.getContextPath()));
            }
        }
        this.jspIdleTimeout = options.getJspIdleTimeout() * 1000;
    }

    public void addWrapper(String jspUri, JspServletWrapper jsw) {
        this.jsps.put(jspUri, jsw);
    }

    public JspServletWrapper getWrapper(String jspUri) {
        return this.jsps.get(jspUri);
    }

    public void removeWrapper(String jspUri) {
        this.jsps.remove(jspUri);
    }

    public FastRemovalDequeue.Entry push(JspServletWrapper jsw) {
        FastRemovalDequeue.Entry entry;
        JspServletWrapper replaced;
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)Localizer.getMessage("jsp.message.jsp_added", jsw.getJspUri(), this.context.getContextPath()));
        }
        if ((replaced = (JspServletWrapper)(entry = this.jspQueue.push(jsw)).getReplaced()) != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)Localizer.getMessage("jsp.message.jsp_removed_excess", replaced.getJspUri(), this.context.getContextPath()));
            }
            this.unloadJspServletWrapper(replaced);
            entry.clearReplaced();
        }
        return entry;
    }

    public void makeYoungest(FastRemovalDequeue.Entry unloadHandle) {
        if (this.log.isTraceEnabled()) {
            JspServletWrapper jsw = (JspServletWrapper)unloadHandle.getContent();
            this.log.trace((Object)Localizer.getMessage("jsp.message.jsp_queue_update", jsw.getJspUri(), this.context.getContextPath()));
        }
        this.jspQueue.moveFirst(unloadHandle);
    }

    public int getJspCount() {
        return this.jsps.size();
    }

    public CodeSource getCodeSource() {
        return this.codeSource;
    }

    public ClassLoader getParentClassLoader() {
        return this.parentClassLoader;
    }

    public PermissionCollection getPermissionCollection() {
        return this.permissionCollection;
    }

    public void destroy() {
        for (JspServletWrapper jspServletWrapper : this.jsps.values()) {
            jspServletWrapper.destroy();
        }
    }

    public void incrementJspReloadCount() {
        this.jspReloadCount.incrementAndGet();
    }

    public void setJspReloadCount(int count) {
        this.jspReloadCount.set(count);
    }

    public int getJspReloadCount() {
        return this.jspReloadCount.intValue();
    }

    public int getJspQueueLength() {
        if (this.jspQueue != null) {
            return this.jspQueue.getSize();
        }
        return -1;
    }

    public int getJspUnloadCount() {
        return this.jspUnloadCount.intValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkCompile() {
        if (this.lastCompileCheck < 0L) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now <= this.lastCompileCheck + (long)this.options.getCheckInterval() * 1000L) {
            return;
        }
        this.lastCompileCheck = now;
        ArrayList<JspServletWrapper> wrappersToReload = new ArrayList<JspServletWrapper>();
        this.compileCheckInProgress = true;
        Object[] wrappers = this.jsps.values().toArray();
        for (Object wrapper : wrappers) {
            JspServletWrapper jsw = (JspServletWrapper)wrapper;
            JspCompilationContext ctxt = jsw.getJspEngineContext();
            JspServletWrapper jspServletWrapper = jsw;
            synchronized (jspServletWrapper) {
                try {
                    ctxt.compile();
                    if (jsw.getReload()) {
                        wrappersToReload.add(jsw);
                    }
                }
                catch (FileNotFoundException ex) {
                    ctxt.incrementRemoved();
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    jsw.getServletContext().log(Localizer.getMessage("jsp.error.backgroundCompilationFailed"), t);
                }
            }
        }
        this.compileCheckInProgress = false;
        for (JspServletWrapper jsw : wrappersToReload) {
            try {
                if (jsw.isTagFile()) {
                    JspRuntimeContext jspRuntimeContext = this;
                    synchronized (jspRuntimeContext) {
                        jsw.loadTagFile();
                        continue;
                    }
                }
                jsw.getServlet();
            }
            catch (ServletException e) {
                jsw.getServletContext().log(Localizer.getMessage("jsp.error.reload"), (Throwable)e);
            }
        }
    }

    public boolean isCompileCheckInProgress() {
        return this.compileCheckInProgress;
    }

    public String getClassPath() {
        return this.classpath;
    }

    public long getLastJspQueueUpdate() {
        return this.lastJspQueueUpdate;
    }

    public Map<String, SmapStratum> getSmaps() {
        return this.smaps;
    }

    private String initClassPath() {
        StringBuilder cpath = new StringBuilder();
        if (this.parentClassLoader instanceof URLClassLoader) {
            URL[] urls;
            for (URL url : urls = ((URLClassLoader)this.parentClassLoader).getURLs()) {
                if (!url.getProtocol().equals("file")) continue;
                try {
                    String decoded = url.toURI().getPath();
                    cpath.append(decoded + File.pathSeparator);
                }
                catch (URISyntaxException e) {
                    this.log.warn((Object)Localizer.getMessage("jsp.warning.classpathUrl"), (Throwable)e);
                }
            }
        }
        cpath.append(this.options.getScratchDir() + File.pathSeparator);
        String cp = (String)this.context.getAttribute(Constants.SERVLET_CLASSPATH);
        if (cp == null || cp.equals("")) {
            cp = this.options.getClassPath();
        }
        String path = cpath.toString() + cp;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Compilation classpath initialized: " + path));
        }
        return path;
    }

    private SecurityHolder initSecurity() {
        Policy policy = Policy.getPolicy();
        CodeSource source = null;
        PermissionCollection permissions = null;
        if (policy != null) {
            try {
                String codeBase;
                String docBase = this.context.getRealPath("/");
                if (docBase == null) {
                    docBase = this.options.getScratchDir().toString();
                }
                if (!(codeBase = docBase).endsWith(File.separator)) {
                    codeBase = codeBase + File.separator;
                }
                File contextDir = new File(codeBase);
                URL url = contextDir.getCanonicalFile().toURI().toURL();
                source = new CodeSource(url, (Certificate[])null);
                permissions = policy.getPermissions(source);
                if (!docBase.endsWith(File.separator)) {
                    permissions.add(new FilePermission(docBase, "read"));
                    docBase = docBase + File.separator;
                } else {
                    permissions.add(new FilePermission(docBase.substring(0, docBase.length() - 1), "read"));
                }
                docBase = docBase + "-";
                permissions.add(new FilePermission(docBase, "read"));
                String workDir = this.options.getScratchDir().toString();
                if (!workDir.endsWith(File.separator)) {
                    permissions.add(new FilePermission(workDir, "read,write"));
                    workDir = workDir + File.separator;
                }
                workDir = workDir + "-";
                permissions.add(new FilePermission(workDir, "read,write,delete"));
                permissions.add(new RuntimePermission("accessClassInPackage.org.apache.jasper.runtime"));
            }
            catch (IOException | RuntimeException e) {
                this.context.log(Localizer.getMessage("jsp.error.security"), (Throwable)e);
            }
        }
        return new SecurityHolder(source, permissions);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void unloadJspServletWrapper(JspServletWrapper jsw) {
        this.removeWrapper(jsw.getJspUri());
        JspServletWrapper jspServletWrapper = jsw;
        synchronized (jspServletWrapper) {
            jsw.destroy();
        }
        this.jspUnloadCount.incrementAndGet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkUnload() {
        if (this.log.isTraceEnabled()) {
            int queueLength = -1;
            if (this.jspQueue != null) {
                queueLength = this.jspQueue.getSize();
            }
            this.log.trace((Object)Localizer.getMessage("jsp.message.jsp_unload_check", this.context.getContextPath(), "" + this.jsps.size(), "" + queueLength));
        }
        long now = System.currentTimeMillis();
        if (this.jspIdleTimeout > 0L) {
            Object[] wrappers;
            long unloadBefore = now - this.jspIdleTimeout;
            for (Object wrapper : wrappers = this.jsps.values().toArray()) {
                JspServletWrapper jsw;
                JspServletWrapper jspServletWrapper = jsw = (JspServletWrapper)wrapper;
                synchronized (jspServletWrapper) {
                    if (jsw.getLastUsageTime() < unloadBefore) {
                        if (this.log.isDebugEnabled()) {
                            this.log.debug((Object)Localizer.getMessage("jsp.message.jsp_removed_idle", jsw.getJspUri(), this.context.getContextPath(), "" + (now - jsw.getLastUsageTime())));
                        }
                        if (this.jspQueue != null) {
                            this.jspQueue.remove(jsw.getUnloadHandle());
                        }
                        this.unloadJspServletWrapper(jsw);
                    }
                }
            }
        }
        this.lastJspQueueUpdate = now;
    }

    private static class SecurityHolder {
        private final CodeSource cs;
        private final PermissionCollection pc;

        private SecurityHolder(CodeSource cs, PermissionCollection pc) {
            this.cs = cs;
            this.pc = pc;
        }
    }
}

