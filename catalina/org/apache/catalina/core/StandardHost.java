/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardHostValve;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;

public class StandardHost
extends ContainerBase
implements Host {
    private static final Log log = LogFactory.getLog(StandardHost.class);
    private String[] aliases = new String[0];
    private final Object aliasesLock = new Object();
    private String appBase = "webapps";
    private volatile File appBaseFile = null;
    private String xmlBase = null;
    private volatile File hostConfigBase = null;
    private boolean autoDeploy = true;
    private String configClass = "org.apache.catalina.startup.ContextConfig";
    private String contextClass = "org.apache.catalina.core.StandardContext";
    private boolean deployOnStartup = true;
    private boolean deployXML = !Globals.IS_SECURITY_ENABLED;
    private boolean copyXML = false;
    private String errorReportValveClass = "org.apache.catalina.valves.ErrorReportValve";
    private boolean unpackWARs = true;
    private String workDir = null;
    private boolean createDirs = true;
    private final Map<ClassLoader, String> childClassLoaders = new WeakHashMap<ClassLoader, String>();
    private Pattern deployIgnore = null;
    private boolean undeployOldVersions = false;
    private boolean failCtxIfServletStartFails = false;

    public StandardHost() {
        this.pipeline.setBasic(new StandardHostValve());
    }

    @Override
    public boolean getUndeployOldVersions() {
        return this.undeployOldVersions;
    }

    @Override
    public void setUndeployOldVersions(boolean undeployOldVersions) {
        this.undeployOldVersions = undeployOldVersions;
    }

    @Override
    public ExecutorService getStartStopExecutor() {
        return this.startStopExecutor;
    }

    @Override
    public String getAppBase() {
        return this.appBase;
    }

    @Override
    public File getAppBaseFile() {
        if (this.appBaseFile != null) {
            return this.appBaseFile;
        }
        File file = new File(this.getAppBase());
        if (!file.isAbsolute()) {
            file = new File(this.getCatalinaBase(), file.getPath());
        }
        try {
            file = file.getCanonicalFile();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.appBaseFile = file;
        return file;
    }

    @Override
    public void setAppBase(String appBase) {
        if (appBase.trim().equals("")) {
            log.warn((Object)sm.getString("standardHost.problematicAppBase", new Object[]{this.getName()}));
        }
        String oldAppBase = this.appBase;
        this.appBase = appBase;
        this.support.firePropertyChange("appBase", oldAppBase, this.appBase);
        this.appBaseFile = null;
    }

    @Override
    public String getXmlBase() {
        return this.xmlBase;
    }

    @Override
    public void setXmlBase(String xmlBase) {
        String oldXmlBase = this.xmlBase;
        this.xmlBase = xmlBase;
        this.support.firePropertyChange("xmlBase", oldXmlBase, this.xmlBase);
    }

    @Override
    public File getConfigBaseFile() {
        if (this.hostConfigBase != null) {
            return this.hostConfigBase;
        }
        String path = null;
        if (this.getXmlBase() != null) {
            path = this.getXmlBase();
        } else {
            StringBuilder xmlDir = new StringBuilder("conf");
            Container parent = this.getParent();
            if (parent instanceof Engine) {
                xmlDir.append('/');
                xmlDir.append(parent.getName());
            }
            xmlDir.append('/');
            xmlDir.append(this.getName());
            path = xmlDir.toString();
        }
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(this.getCatalinaBase(), path);
        }
        try {
            file = file.getCanonicalFile();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.hostConfigBase = file;
        return file;
    }

    @Override
    public boolean getCreateDirs() {
        return this.createDirs;
    }

    @Override
    public void setCreateDirs(boolean createDirs) {
        this.createDirs = createDirs;
    }

    @Override
    public boolean getAutoDeploy() {
        return this.autoDeploy;
    }

    @Override
    public void setAutoDeploy(boolean autoDeploy) {
        boolean oldAutoDeploy = this.autoDeploy;
        this.autoDeploy = autoDeploy;
        this.support.firePropertyChange("autoDeploy", oldAutoDeploy, this.autoDeploy);
    }

    @Override
    public String getConfigClass() {
        return this.configClass;
    }

    @Override
    public void setConfigClass(String configClass) {
        String oldConfigClass = this.configClass;
        this.configClass = configClass;
        this.support.firePropertyChange("configClass", oldConfigClass, this.configClass);
    }

    public String getContextClass() {
        return this.contextClass;
    }

    public void setContextClass(String contextClass) {
        String oldContextClass = this.contextClass;
        this.contextClass = contextClass;
        this.support.firePropertyChange("contextClass", oldContextClass, this.contextClass);
    }

    @Override
    public boolean getDeployOnStartup() {
        return this.deployOnStartup;
    }

    @Override
    public void setDeployOnStartup(boolean deployOnStartup) {
        boolean oldDeployOnStartup = this.deployOnStartup;
        this.deployOnStartup = deployOnStartup;
        this.support.firePropertyChange("deployOnStartup", oldDeployOnStartup, this.deployOnStartup);
    }

    public boolean isDeployXML() {
        return this.deployXML;
    }

    public void setDeployXML(boolean deployXML) {
        this.deployXML = deployXML;
    }

    public boolean isCopyXML() {
        return this.copyXML;
    }

    public void setCopyXML(boolean copyXML) {
        this.copyXML = copyXML;
    }

    public String getErrorReportValveClass() {
        return this.errorReportValveClass;
    }

    public void setErrorReportValveClass(String errorReportValveClass) {
        String oldErrorReportValveClassClass = this.errorReportValveClass;
        this.errorReportValveClass = errorReportValveClass;
        this.support.firePropertyChange("errorReportValveClass", oldErrorReportValveClassClass, this.errorReportValveClass);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("standardHost.nullName"));
        }
        name = name.toLowerCase(Locale.ENGLISH);
        String oldName = this.name;
        this.name = name;
        this.support.firePropertyChange("name", oldName, this.name);
    }

    public boolean isUnpackWARs() {
        return this.unpackWARs;
    }

    public void setUnpackWARs(boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }

    public String getWorkDir() {
        return this.workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    @Override
    public String getDeployIgnore() {
        if (this.deployIgnore == null) {
            return null;
        }
        return this.deployIgnore.toString();
    }

    @Override
    public Pattern getDeployIgnorePattern() {
        return this.deployIgnore;
    }

    @Override
    public void setDeployIgnore(String deployIgnore) {
        String oldDeployIgnore = this.deployIgnore == null ? null : this.deployIgnore.toString();
        this.deployIgnore = deployIgnore == null ? null : Pattern.compile(deployIgnore);
        this.support.firePropertyChange("deployIgnore", oldDeployIgnore, deployIgnore);
    }

    public boolean isFailCtxIfServletStartFails() {
        return this.failCtxIfServletStartFails;
    }

    public void setFailCtxIfServletStartFails(boolean failCtxIfServletStartFails) {
        boolean oldFailCtxIfServletStartFails = this.failCtxIfServletStartFails;
        this.failCtxIfServletStartFails = failCtxIfServletStartFails;
        this.support.firePropertyChange("failCtxIfServletStartFails", oldFailCtxIfServletStartFails, failCtxIfServletStartFails);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addAlias(String alias) {
        alias = alias.toLowerCase(Locale.ENGLISH);
        Object object = this.aliasesLock;
        synchronized (object) {
            for (String s : this.aliases) {
                if (!s.equals(alias)) continue;
                return;
            }
            String[] newAliases = Arrays.copyOf(this.aliases, this.aliases.length + 1);
            newAliases[this.aliases.length] = alias;
            this.aliases = newAliases;
        }
        this.fireContainerEvent("addAlias", alias);
    }

    @Override
    public void addChild(Container child) {
        if (!(child instanceof Context)) {
            throw new IllegalArgumentException(sm.getString("standardHost.notContext"));
        }
        child.addLifecycleListener(new MemoryLeakTrackingListener());
        Context context = (Context)child;
        if (context.getPath() == null) {
            ContextName cn = new ContextName(context.getDocBase(), true);
            context.setPath(cn.getPath());
        }
        super.addChild(child);
    }

    public String[] findReloadedContextMemoryLeaks() {
        System.gc();
        ArrayList<String> result = new ArrayList<String>();
        for (Map.Entry<ClassLoader, String> entry : this.childClassLoaders.entrySet()) {
            ClassLoader cl = entry.getKey();
            if (!(cl instanceof WebappClassLoaderBase) || ((WebappClassLoaderBase)cl).getState().isAvailable()) continue;
            result.add(entry.getValue());
        }
        return result.toArray(new String[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] findAliases() {
        Object object = this.aliasesLock;
        synchronized (object) {
            return this.aliases;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAlias(String alias) {
        alias = alias.toLowerCase(Locale.ENGLISH);
        Object object = this.aliasesLock;
        synchronized (object) {
            int n = -1;
            for (int i = 0; i < this.aliases.length; ++i) {
                if (!this.aliases[i].equals(alias)) continue;
                n = i;
                break;
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.aliases.length - 1];
            for (int i = 0; i < this.aliases.length; ++i) {
                if (i == n) continue;
                results[j++] = this.aliases[i];
            }
            this.aliases = results;
        }
        this.fireContainerEvent("removeAlias", alias);
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        String errorValve = this.getErrorReportValveClass();
        if (errorValve != null && !errorValve.equals("")) {
            try {
                Valve[] valves;
                boolean found = false;
                for (Valve valve : valves = this.getPipeline().getValves()) {
                    if (!errorValve.equals(valve.getClass().getName())) continue;
                    found = true;
                    break;
                }
                if (!found) {
                    ErrorReportValve valve = ErrorReportValve.class.getName().equals(errorValve) ? new ErrorReportValve() : (Valve)Class.forName(errorValve).getConstructor(new Class[0]).newInstance(new Object[0]);
                    this.getPipeline().addValve(valve);
                }
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)sm.getString("standardHost.invalidErrorReportValveClass", new Object[]{errorValve}), t);
            }
        }
        super.startInternal();
    }

    public String[] getValveNames() throws Exception {
        Valve[] valves = this.getPipeline().getValves();
        String[] mbeanNames = new String[valves.length];
        for (int i = 0; i < valves.length; ++i) {
            ObjectName oname;
            if (!(valves[i] instanceof JmxEnabled) || (oname = ((JmxEnabled)((Object)valves[i])).getObjectName()) == null) continue;
            mbeanNames[i] = oname.toString();
        }
        return mbeanNames;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getAliases() {
        Object object = this.aliasesLock;
        synchronized (object) {
            return this.aliases;
        }
    }

    @Override
    protected String getObjectNameKeyProperties() {
        StringBuilder keyProperties = new StringBuilder("type=Host");
        keyProperties.append(this.getMBeanKeyProperties());
        return keyProperties.toString();
    }

    private class MemoryLeakTrackingListener
    implements LifecycleListener {
        private MemoryLeakTrackingListener() {
        }

        @Override
        public void lifecycleEvent(LifecycleEvent event) {
            if (event.getType().equals("after_start") && event.getSource() instanceof Context) {
                Context context = (Context)event.getSource();
                StandardHost.this.childClassLoaders.put(context.getLoader().getClassLoader(), context.getServletContext().getContextPath());
            }
        }
    }
}

