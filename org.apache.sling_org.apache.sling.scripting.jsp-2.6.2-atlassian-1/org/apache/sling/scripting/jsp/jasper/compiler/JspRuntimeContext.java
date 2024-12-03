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
 *  org.apache.sling.api.SlingHttpServletRequest
 *  org.apache.sling.api.resource.path.Path
 *  org.apache.sling.api.scripting.SlingBindings
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.File;
import java.io.FilePermission;
import java.net.URL;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.path.Path;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.scripting.jsp.SlingJspPageContext;
import org.apache.sling.scripting.jsp.jasper.Constants;
import org.apache.sling.scripting.jsp.jasper.IOProvider;
import org.apache.sling.scripting.jsp.jasper.Options;
import org.apache.sling.scripting.jsp.jasper.runtime.JspFactoryImpl;
import org.apache.sling.scripting.jsp.jasper.security.SecurityClassLoad;
import org.apache.sling.scripting.jsp.jasper.servlet.JspServletWrapper;

public final class JspRuntimeContext {
    private final Log log = LogFactory.getLog(JspRuntimeContext.class);
    private final IOProvider ioProvider;
    private static final ThreadLocal<Integer> USE_OWN_FACTORY = new ThreadLocal();
    private ServletContext context;
    private Options options;
    private PermissionCollection permissionCollection;
    private final ConcurrentHashMap<String, JspServletWrapper> jsps = new ConcurrentHashMap();
    private final Map<String, Set<String>> depToJsp = new HashMap<String, Set<String>>();
    private final ConcurrentHashMap<String, Lock> tagFileLoadingLocks = new ConcurrentHashMap();

    public static JspFactoryHandler initFactoryHandler() {
        JspFactoryImpl factory = new JspFactoryImpl();
        SecurityClassLoad.securityClassLoad(((Object)((Object)factory)).getClass().getClassLoader());
        if (System.getSecurityManager() != null) {
            String basePackage = "org.apache.sling.scripting.jsp.jasper.";
            try {
                ((Object)((Object)factory)).getClass().getClassLoader().loadClass(basePackage + "runtime.JspFactoryImpl$PrivilegedGetPageContext");
                ((Object)((Object)factory)).getClass().getClassLoader().loadClass(basePackage + "runtime.JspFactoryImpl$PrivilegedReleasePageContext");
                ((Object)((Object)factory)).getClass().getClassLoader().loadClass(basePackage + "runtime.JspRuntimeLibrary");
                ((Object)((Object)factory)).getClass().getClassLoader().loadClass(basePackage + "runtime.JspRuntimeLibrary$PrivilegedIntrospectHelper");
                ((Object)((Object)factory)).getClass().getClassLoader().loadClass(basePackage + "runtime.ServletResponseWrapperInclude");
                ((Object)((Object)factory)).getClass().getClassLoader().loadClass(basePackage + "servlet.JspServletWrapper");
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalStateException(ex);
            }
        }
        JspFactoryHandler key = new JspFactoryHandler(JspFactory.getDefaultFactory(), factory);
        JspFactory.setDefaultFactory((JspFactory)key);
        return key;
    }

    public JspRuntimeContext(ServletContext context, Options options, IOProvider ioProvider) {
        this.context = context;
        this.options = options;
        this.ioProvider = ioProvider;
        if (Constants.IS_SECURITY_ENABLED) {
            this.initSecurity();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addJspDependencies(JspServletWrapper jsw, List<String> deps) {
        if (deps != null) {
            String jspUri = jsw.getJspUri();
            Map<String, Set<String>> map = this.depToJsp;
            synchronized (map) {
                for (String dep : deps) {
                    Set<String> set = this.depToJsp.get(dep);
                    if (set == null) {
                        set = new HashSet<String>();
                        this.depToJsp.put(dep, set);
                    }
                    set.add(jspUri);
                }
            }
        }
    }

    public boolean handleModification(String scriptName, boolean isRemove) {
        JspServletWrapper wrapper;
        if (this.log.isDebugEnabled()) {
            this.log.debug("Handling modification " + scriptName);
        }
        if ((wrapper = this.jsps.remove(scriptName)) == null && isRemove) {
            boolean removed = false;
            Path path = new Path(scriptName);
            Iterator<Map.Entry<String, JspServletWrapper>> iter = this.jsps.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, JspServletWrapper> entry = iter.next();
                if (!path.matches(entry.getKey())) continue;
                iter.remove();
                removed |= this.handleModification(entry.getKey(), entry.getValue());
            }
            return removed;
        }
        return this.handleModification(scriptName, wrapper);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean handleModification(String scriptName, JspServletWrapper wrapper) {
        Set<String> deps;
        boolean removed = this.invalidate(wrapper);
        Map<String, Set<String>> map = this.depToJsp;
        synchronized (map) {
            deps = this.depToJsp.remove(scriptName);
        }
        if (deps != null) {
            for (String dep : deps) {
                removed |= this.invalidate(this.jsps.remove(dep));
            }
        }
        return removed;
    }

    private boolean invalidate(JspServletWrapper wrapper) {
        if (wrapper != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Invalidating jsp " + wrapper.getJspUri());
            }
            wrapper.destroy(true);
            return true;
        }
        return false;
    }

    public JspServletWrapper addWrapper(String jspUri, JspServletWrapper jsw) {
        JspServletWrapper previous = this.jsps.putIfAbsent(jspUri, jsw);
        if (previous == null) {
            this.addJspDependencies(jsw, jsw.getDependants());
            return jsw;
        }
        return previous;
    }

    public JspServletWrapper getWrapper(String jspUri) {
        return this.jsps.get(jspUri);
    }

    public void lockTagFileLoading(String tagFilePath) {
        Lock lock = this.getTagFileLoadingLock(tagFilePath);
        lock.lock();
    }

    public void unlockTagFileLoading(String tagFilePath) {
        Lock lock = this.getTagFileLoadingLock(tagFilePath);
        lock.unlock();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        Iterator<JspServletWrapper> servlets = this.jsps.values().iterator();
        while (servlets.hasNext()) {
            servlets.next().destroy(false);
        }
        this.jsps.clear();
        Map<String, Set<String>> map = this.depToJsp;
        synchronized (map) {
            this.depToJsp.clear();
        }
    }

    public IOProvider getIOProvider() {
        return this.ioProvider;
    }

    private void initSecurity() {
        Policy policy = Policy.getPolicy();
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
                URL url = contextDir.getCanonicalFile().toURL();
                CodeSource codeSource = new CodeSource(url, (Certificate[])null);
                this.permissionCollection = policy.getPermissions(codeSource);
                if (!docBase.endsWith(File.separator)) {
                    this.permissionCollection.add(new FilePermission(docBase, "read"));
                    docBase = docBase + File.separator;
                } else {
                    this.permissionCollection.add(new FilePermission(docBase.substring(0, docBase.length() - 1), "read"));
                }
                docBase = docBase + "-";
                this.permissionCollection.add(new FilePermission(docBase, "read"));
                String workDir = this.options.getScratchDir().toString();
                if (!workDir.endsWith(File.separator)) {
                    this.permissionCollection.add(new FilePermission(workDir, "read"));
                    workDir = workDir + File.separator;
                }
                workDir = workDir + "-";
                this.permissionCollection.add(new FilePermission(workDir, "read"));
                this.permissionCollection.add(new RuntimePermission("accessClassInPackage.org.apache.jasper.runtime"));
            }
            catch (Exception e) {
                this.context.log("Security Init for context failed", (Throwable)e);
            }
        }
    }

    private Lock getTagFileLoadingLock(String tagFilePath) {
        Lock existingLock;
        Lock lock = this.tagFileLoadingLocks.get(tagFilePath);
        if (lock == null && (existingLock = this.tagFileLoadingLocks.putIfAbsent(tagFilePath, lock = new ReentrantLock())) != null) {
            lock = existingLock;
        }
        return lock;
    }

    public static final class JspFactoryHandler
    extends JspFactory {
        private final JspFactory original;
        private final JspFactory own;

        public JspFactoryHandler(JspFactory orig, JspFactory own) {
            this.original = orig instanceof JspFactoryHandler ? ((JspFactoryHandler)orig).original : orig;
            this.own = own;
        }

        private JspFactory getFactory() {
            Integer useOwnFactory = (Integer)USE_OWN_FACTORY.get();
            if (useOwnFactory == null || useOwnFactory == 0) {
                return this.original;
            }
            return this.own;
        }

        public PageContext getPageContext(Servlet paramServlet, ServletRequest paramServletRequest, ServletResponse paramServletResponse, String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2) {
            SlingBindings slingBindings;
            PageContext context = this.getFactory().getPageContext(paramServlet, paramServletRequest, paramServletResponse, paramString, paramBoolean1, paramInt, paramBoolean2);
            if (paramServletRequest instanceof SlingHttpServletRequest && (slingBindings = (SlingBindings)paramServletRequest.getAttribute(SlingBindings.class.getName())) != null) {
                context = new SlingJspPageContext(context, slingBindings);
            }
            return context;
        }

        public void releasePageContext(PageContext paramPageContext) {
            this.getFactory().releasePageContext(paramPageContext);
        }

        public JspEngineInfo getEngineInfo() {
            return this.getFactory().getEngineInfo();
        }

        public JspApplicationContext getJspApplicationContext(ServletContext paramServletContext) {
            return this.getFactory().getJspApplicationContext(paramServletContext);
        }

        public void destroy() {
            JspFactory current = JspFactory.getDefaultFactory();
            if (current == this) {
                JspFactory.setDefaultFactory((JspFactory)this.original);
            }
        }

        public void incUsage() {
            Integer count = (Integer)USE_OWN_FACTORY.get();
            int newCount = 1;
            if (count != null) {
                newCount = count + 1;
            }
            USE_OWN_FACTORY.set(newCount);
        }

        public void decUsage() {
            Integer count = (Integer)USE_OWN_FACTORY.get();
            USE_OWN_FACTORY.set(count - 1);
        }

        public int resetUsage() {
            Integer count = (Integer)USE_OWN_FACTORY.get();
            USE_OWN_FACTORY.set(0);
            return count;
        }

        public void setUsage(int count) {
            USE_OWN_FACTORY.set(count);
        }
    }
}

