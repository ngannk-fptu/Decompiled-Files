/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.loader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class WebappLoader
extends LifecycleMBeanBase
implements Loader,
PropertyChangeListener {
    private static final Log log = LogFactory.getLog(WebappLoader.class);
    private WebappClassLoaderBase classLoader = null;
    private Context context = null;
    private boolean delegate = false;
    private String loaderClass = ParallelWebappClassLoader.class.getName();
    private ClassLoader parentClassLoader = null;
    private boolean reloadable = false;
    protected static final StringManager sm = StringManager.getManager(WebappLoader.class);
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String classpath = null;

    public WebappLoader() {
        this(null);
    }

    @Deprecated
    public WebappLoader(ClassLoader parent) {
        this.parentClassLoader = parent;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void setContext(Context context) {
        if (this.context == context) {
            return;
        }
        if (this.getState().isAvailable()) {
            throw new IllegalStateException(sm.getString("webappLoader.setContext.ise"));
        }
        if (this.context != null) {
            this.context.removePropertyChangeListener(this);
        }
        Context oldContext = this.context;
        this.context = context;
        this.support.firePropertyChange("context", oldContext, this.context);
        if (this.context != null) {
            this.setReloadable(this.context.getReloadable());
            this.context.addPropertyChangeListener(this);
        }
    }

    @Override
    public boolean getDelegate() {
        return this.delegate;
    }

    @Override
    public void setDelegate(boolean delegate) {
        boolean oldDelegate = this.delegate;
        this.delegate = delegate;
        this.support.firePropertyChange("delegate", (Object)oldDelegate, (Object)this.delegate);
    }

    public String getLoaderClass() {
        return this.loaderClass;
    }

    public void setLoaderClass(String loaderClass) {
        this.loaderClass = loaderClass;
    }

    public void setLoaderInstance(WebappClassLoaderBase loaderInstance) {
        this.classLoader = loaderInstance;
        this.setLoaderClass(loaderInstance.getClass().getName());
    }

    @Override
    public boolean getReloadable() {
        return this.reloadable;
    }

    @Override
    public void setReloadable(boolean reloadable) {
        boolean oldReloadable = this.reloadable;
        this.reloadable = reloadable;
        this.support.firePropertyChange("reloadable", (Object)oldReloadable, (Object)this.reloadable);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override
    public void backgroundProcess() {
        if (this.reloadable && this.modified()) {
            Thread currentThread = Thread.currentThread();
            try {
                currentThread.setContextClassLoader(WebappLoader.class.getClassLoader());
                if (this.context != null) {
                    this.context.reload();
                }
            }
            finally {
                if (this.context != null && this.context.getLoader() != null) {
                    currentThread.setContextClassLoader(this.context.getLoader().getClassLoader());
                }
            }
        }
    }

    public String[] getLoaderRepositories() {
        if (this.classLoader == null) {
            return new String[0];
        }
        URL[] urls = this.classLoader.getURLs();
        String[] result = new String[urls.length];
        for (int i = 0; i < urls.length; ++i) {
            result[i] = urls[i].toExternalForm();
        }
        return result;
    }

    public String getLoaderRepositoriesString() {
        String[] repositories = this.getLoaderRepositories();
        StringBuilder sb = new StringBuilder();
        for (String repository : repositories) {
            sb.append(repository).append(':');
        }
        return sb.toString();
    }

    public String getClasspath() {
        return this.classpath;
    }

    @Override
    public boolean modified() {
        return this.classLoader != null ? this.classLoader.modified() : false;
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String toString() {
        return ToStringUtil.toString((Object)this, this.context);
    }

    @Override
    protected void startInternal() throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("webappLoader.starting"));
        }
        if (this.context.getResources() == null) {
            log.info((Object)sm.getString("webappLoader.noResources", new Object[]{this.context}));
            this.setState(LifecycleState.STARTING);
            return;
        }
        try {
            this.classLoader = this.createClassLoader();
            this.classLoader.setResources(this.context.getResources());
            this.classLoader.setDelegate(this.delegate);
            this.setClassPath();
            this.setPermissions();
            this.classLoader.start();
            String contextName = this.context.getName();
            if (!contextName.startsWith("/")) {
                contextName = "/" + contextName;
            }
            ObjectName cloname = new ObjectName(this.context.getDomain() + ":type=" + this.classLoader.getClass().getSimpleName() + ",host=" + this.context.getParent().getName() + ",context=" + contextName);
            Registry.getRegistry(null, null).registerComponent((Object)this.classLoader, cloname, null);
        }
        catch (Throwable t) {
            t = ExceptionUtils.unwrapInvocationTargetException((Throwable)t);
            ExceptionUtils.handleThrowable((Throwable)t);
            throw new LifecycleException(sm.getString("webappLoader.startError"), t);
        }
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("webappLoader.stopping"));
        }
        this.setState(LifecycleState.STOPPING);
        ServletContext servletContext = this.context.getServletContext();
        servletContext.removeAttribute("org.apache.catalina.jsp_classpath");
        if (this.classLoader != null) {
            try {
                this.classLoader.stop();
            }
            finally {
                this.classLoader.destroy();
            }
            try {
                String contextName = this.context.getName();
                if (!contextName.startsWith("/")) {
                    contextName = "/" + contextName;
                }
                ObjectName cloname = new ObjectName(this.context.getDomain() + ":type=" + this.classLoader.getClass().getSimpleName() + ",host=" + this.context.getParent().getName() + ",context=" + contextName);
                Registry.getRegistry(null, null).unregisterComponent(cloname);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("webappLoader.stopError"), (Throwable)e);
            }
        }
        this.classLoader = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (!(event.getSource() instanceof Context)) {
            return;
        }
        if (event.getPropertyName().equals("reloadable")) {
            try {
                this.setReloadable((Boolean)event.getNewValue());
            }
            catch (NumberFormatException e) {
                log.error((Object)sm.getString("webappLoader.reloadable", new Object[]{event.getNewValue().toString()}));
            }
        }
    }

    private WebappClassLoaderBase createClassLoader() throws Exception {
        if (this.classLoader != null) {
            return this.classLoader;
        }
        if (this.parentClassLoader == null) {
            this.parentClassLoader = this.context.getParentClassLoader();
        } else {
            this.context.setParentClassLoader(this.parentClassLoader);
        }
        if (ParallelWebappClassLoader.class.getName().equals(this.loaderClass)) {
            return new ParallelWebappClassLoader(this.parentClassLoader);
        }
        Class<?> clazz = Class.forName(this.loaderClass);
        WebappClassLoaderBase classLoader = null;
        Class[] argTypes = new Class[]{ClassLoader.class};
        Object[] args = new Object[]{this.parentClassLoader};
        Constructor<?> constr = clazz.getConstructor(argTypes);
        classLoader = (WebappClassLoaderBase)constr.newInstance(args);
        return classLoader;
    }

    private void setPermissions() {
        if (!Globals.IS_SECURITY_ENABLED) {
            return;
        }
        if (this.context == null) {
            return;
        }
        ServletContext servletContext = this.context.getServletContext();
        File workDir = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
        if (workDir != null) {
            try {
                String workDirPath = workDir.getCanonicalPath();
                this.classLoader.addPermission(new FilePermission(workDirPath, "read,write"));
                this.classLoader.addPermission(new FilePermission(workDirPath + File.separator + "-", "read,write,delete"));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        for (URL url : this.context.getResources().getBaseUrls()) {
            this.classLoader.addPermission(url);
        }
    }

    private void setClassPath() {
        if (this.context == null) {
            return;
        }
        ServletContext servletContext = this.context.getServletContext();
        if (servletContext == null) {
            return;
        }
        StringBuilder classpath = new StringBuilder();
        ClassLoader loader = this.getClassLoader();
        if (this.delegate && loader != null) {
            loader = loader.getParent();
        }
        while (loader != null && this.buildClassPath(classpath, loader)) {
            loader = loader.getParent();
        }
        if (this.delegate && (loader = this.getClassLoader()) != null) {
            this.buildClassPath(classpath, loader);
        }
        this.classpath = classpath.toString();
        servletContext.setAttribute("org.apache.catalina.jsp_classpath", (Object)this.classpath);
    }

    private boolean buildClassPath(StringBuilder classpath, ClassLoader loader) {
        if (loader instanceof URLClassLoader) {
            URL[] repositories;
            for (URL url : repositories = ((URLClassLoader)loader).getURLs()) {
                String repository = url.toString();
                if (repository.startsWith("file://")) {
                    repository = UDecoder.URLDecode((String)repository.substring(7), (Charset)StandardCharsets.UTF_8);
                } else {
                    if (!repository.startsWith("file:")) continue;
                    repository = UDecoder.URLDecode((String)repository.substring(5), (Charset)StandardCharsets.UTF_8);
                }
                if (repository == null) continue;
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(repository);
            }
        } else {
            if (loader == ClassLoader.getSystemClassLoader()) {
                String cp = System.getProperty("java.class.path");
                if (cp != null && cp.length() > 0) {
                    if (classpath.length() > 0) {
                        classpath.append(File.pathSeparator);
                    }
                    classpath.append(cp);
                }
                return false;
            }
            if (!JreCompat.isGraalAvailable()) {
                log.info((Object)sm.getString("webappLoader.unknownClassLoader", new Object[]{loader, loader.getClass()}));
            }
            return false;
        }
        return true;
    }

    @Override
    protected String getDomainInternal() {
        return this.context.getDomain();
    }

    @Override
    protected String getObjectNameKeyProperties() {
        StringBuilder name = new StringBuilder("type=Loader");
        name.append(",host=");
        name.append(this.context.getParent().getName());
        name.append(",context=");
        String contextName = this.context.getName();
        if (!contextName.startsWith("/")) {
            name.append('/');
        }
        name.append(contextName);
        return name.toString();
    }
}

