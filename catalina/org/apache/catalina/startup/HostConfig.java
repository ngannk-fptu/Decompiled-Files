/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.digester.Digester
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.DistributedManager;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Manager;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.security.DeployXmlPermission;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.startup.FailedContext;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.IOTools;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class HostConfig
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(HostConfig.class);
    protected static final StringManager sm = StringManager.getManager(HostConfig.class);
    protected static final long FILE_MODIFICATION_RESOLUTION_MS = 1000L;
    protected String contextClass = "org.apache.catalina.core.StandardContext";
    protected Host host = null;
    protected ObjectName oname = null;
    protected boolean deployXML = false;
    protected boolean copyXML = false;
    protected boolean unpackWARs = false;
    protected final Map<String, DeployedApplication> deployed = new ConcurrentHashMap<String, DeployedApplication>();
    @Deprecated
    protected final ArrayList<String> serviced = new ArrayList();
    private Set<String> servicedSet = ConcurrentHashMap.newKeySet();
    protected Digester digester = HostConfig.createDigester(this.contextClass);
    private final Object digesterLock = new Object();
    protected final Set<String> invalidWars = new HashSet<String>();

    public String getContextClass() {
        return this.contextClass;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setContextClass(String contextClass) {
        String oldContextClass = this.contextClass;
        this.contextClass = contextClass;
        if (!oldContextClass.equals(contextClass)) {
            Object object = this.digesterLock;
            synchronized (object) {
                this.digester = HostConfig.createDigester(this.getContextClass());
            }
        }
    }

    public boolean isDeployXML() {
        return this.deployXML;
    }

    public void setDeployXML(boolean deployXML) {
        this.deployXML = deployXML;
    }

    private boolean isDeployThisXML(File docBase, ContextName cn) {
        Policy currentPolicy;
        boolean deployThisXML = this.isDeployXML();
        if (Globals.IS_SECURITY_ENABLED && !deployThisXML && (currentPolicy = Policy.getPolicy()) != null) {
            try {
                URL contextRootUrl = docBase.toURI().toURL();
                CodeSource cs = new CodeSource(contextRootUrl, (Certificate[])null);
                PermissionCollection pc = currentPolicy.getPermissions(cs);
                DeployXmlPermission p = new DeployXmlPermission(cn.getBaseName());
                if (pc.implies(p)) {
                    deployThisXML = true;
                }
            }
            catch (MalformedURLException e) {
                log.warn((Object)sm.getString("hostConfig.docBaseUrlInvalid"), (Throwable)e);
            }
        }
        return deployThisXML;
    }

    public boolean isCopyXML() {
        return this.copyXML;
    }

    public void setCopyXML(boolean copyXML) {
        this.copyXML = copyXML;
    }

    public boolean isUnpackWARs() {
        return this.unpackWARs;
    }

    public void setUnpackWARs(boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.host = (Host)event.getLifecycle();
            if (this.host instanceof StandardHost) {
                this.setCopyXML(((StandardHost)this.host).isCopyXML());
                this.setDeployXML(((StandardHost)this.host).isDeployXML());
                this.setUnpackWARs(((StandardHost)this.host).isUnpackWARs());
                this.setContextClass(((StandardHost)this.host).getContextClass());
            }
        }
        catch (ClassCastException e) {
            log.error((Object)sm.getString("hostConfig.cce", new Object[]{event.getLifecycle()}), (Throwable)e);
            return;
        }
        if (event.getType().equals("periodic")) {
            this.check();
        } else if (event.getType().equals("before_start")) {
            this.beforeStart();
        } else if (event.getType().equals("start")) {
            this.start();
        } else if (event.getType().equals("stop")) {
            this.stop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean tryAddServiced(String name) {
        if (this.servicedSet.add(name)) {
            HostConfig hostConfig = this;
            synchronized (hostConfig) {
                this.serviced.add(name);
            }
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public void addServiced(String name) {
        this.servicedSet.add(name);
        HostConfig hostConfig = this;
        synchronized (hostConfig) {
            this.serviced.add(name);
        }
    }

    @Deprecated
    public boolean isServiced(String name) {
        return this.servicedSet.contains(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeServiced(String name) {
        this.servicedSet.remove(name);
        HostConfig hostConfig = this;
        synchronized (hostConfig) {
            this.serviced.remove(name);
        }
    }

    public synchronized long getDeploymentTime(String name) {
        DeployedApplication app = this.deployed.get(name);
        if (app == null) {
            return 0L;
        }
        return app.timestamp;
    }

    public boolean isDeployed(String name) {
        return this.deployed.containsKey(name);
    }

    protected static Digester createDigester(String contextClassName) {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("Context", contextClassName, "className");
        digester.addSetProperties("Context");
        return digester;
    }

    protected File returnCanonicalPath(String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(this.host.getCatalinaBase(), path);
        }
        try {
            return file.getCanonicalFile();
        }
        catch (IOException e) {
            return file;
        }
    }

    public String getConfigBaseName() {
        return this.host.getConfigBaseFile().getAbsolutePath();
    }

    protected void deployApps() {
        File appBase = this.host.getAppBaseFile();
        File configBase = this.host.getConfigBaseFile();
        String[] filteredAppPaths = this.filterAppPaths(appBase.list());
        this.deployDescriptors(configBase, configBase.list());
        this.deployWARs(appBase, filteredAppPaths);
        this.deployDirectories(appBase, filteredAppPaths);
    }

    protected String[] filterAppPaths(String[] unfilteredAppPaths) {
        Pattern filter = this.host.getDeployIgnorePattern();
        if (filter == null || unfilteredAppPaths == null) {
            return unfilteredAppPaths;
        }
        ArrayList<String> filteredList = new ArrayList<String>();
        Matcher matcher = null;
        for (String appPath : unfilteredAppPaths) {
            if (matcher == null) {
                matcher = filter.matcher(appPath);
            } else {
                matcher.reset(appPath);
            }
            if (matcher.matches()) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)sm.getString("hostConfig.ignorePath", new Object[]{appPath}));
                continue;
            }
            filteredList.add(appPath);
        }
        return filteredList.toArray(new String[0]);
    }

    protected void deployApps(String name) {
        File appBase = this.host.getAppBaseFile();
        File configBase = this.host.getConfigBaseFile();
        ContextName cn = new ContextName(name, false);
        String baseName = cn.getBaseName();
        if (this.deploymentExists(cn.getName())) {
            return;
        }
        File xml = new File(configBase, baseName + ".xml");
        if (xml.exists()) {
            this.deployDescriptor(cn, xml);
            return;
        }
        File war = new File(appBase, baseName + ".war");
        if (war.exists()) {
            this.deployWAR(cn, war);
            return;
        }
        File dir = new File(appBase, baseName);
        if (dir.exists()) {
            this.deployDirectory(cn, dir);
        }
    }

    protected void deployDescriptors(File configBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        ArrayList results = new ArrayList();
        for (String file : files) {
            ContextName cn;
            File contextXml = new File(configBase, file);
            if (!file.toLowerCase(Locale.ENGLISH).endsWith(".xml") || !this.tryAddServiced((cn = new ContextName(file, true)).getName())) continue;
            try {
                if (this.deploymentExists(cn.getName())) {
                    this.removeServiced(cn.getName());
                    continue;
                }
                results.add(es.submit(new DeployDescriptor(this, cn, contextXml)));
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.removeServiced(cn.getName());
                throw t;
            }
        }
        for (Future future : results) {
            try {
                future.get();
            }
            catch (Exception e) {
                log.error((Object)sm.getString("hostConfig.deployDescriptor.threaded.error"), (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void deployDescriptor(ContextName cn, File contextXml) {
        DeployedApplication deployedApp = new DeployedApplication(cn.getName(), true);
        long startTime = 0L;
        if (log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            log.info((Object)sm.getString("hostConfig.deployDescriptor", new Object[]{contextXml.getAbsolutePath()}));
        }
        Context context = null;
        boolean isExternalWar = false;
        boolean isExternal = false;
        File expandedDocBase = null;
        try {
            Object object = this.digesterLock;
            synchronized (object) {
                try (FileInputStream fis = new FileInputStream(contextXml);){
                    context = (Context)this.digester.parse((InputStream)fis);
                }
                catch (Exception e) {
                    log.error((Object)sm.getString("hostConfig.deployDescriptor.error", new Object[]{contextXml.getAbsolutePath()}), (Throwable)e);
                }
                finally {
                    this.digester.reset();
                    if (context == null) {
                        context = new FailedContext();
                    }
                }
            }
            if (context.getPath() != null) {
                log.warn((Object)sm.getString("hostConfig.deployDescriptor.path", new Object[]{context.getPath(), contextXml.getAbsolutePath()}));
            }
            Class<?> clazz = Class.forName(this.host.getConfigClass());
            LifecycleListener listener = (LifecycleListener)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            context.setConfigFile(contextXml.toURI().toURL());
            context.setName(cn.getName());
            context.setPath(cn.getPath());
            context.setWebappVersion(cn.getVersion());
            if (context.getDocBase() != null) {
                File docBase = new File(context.getDocBase());
                if (!docBase.isAbsolute()) {
                    docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                }
                if (!docBase.getCanonicalFile().toPath().startsWith(this.host.getAppBaseFile().toPath())) {
                    File dir;
                    File war;
                    isExternal = true;
                    deployedApp.redeployResources.put(contextXml.getAbsolutePath(), contextXml.lastModified());
                    deployedApp.redeployResources.put(docBase.getAbsolutePath(), docBase.lastModified());
                    if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        isExternalWar = true;
                    }
                    if ((war = new File(this.host.getAppBaseFile(), cn.getBaseName() + ".war")).exists()) {
                        log.warn((Object)sm.getString("hostConfig.deployDescriptor.hiddenWar", new Object[]{contextXml.getAbsolutePath(), war.getAbsolutePath()}));
                    }
                    if ((dir = new File(this.host.getAppBaseFile(), cn.getBaseName())).exists()) {
                        log.warn((Object)sm.getString("hostConfig.deployDescriptor.hiddenDir", new Object[]{contextXml.getAbsolutePath(), dir.getAbsolutePath()}));
                    }
                } else {
                    log.warn((Object)sm.getString("hostConfig.deployDescriptor.localDocBaseSpecified", new Object[]{docBase}));
                    context.setDocBase(null);
                }
            }
            this.host.addChild(context);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.error((Object)sm.getString("hostConfig.deployDescriptor.error", new Object[]{contextXml.getAbsolutePath()}), t);
        }
        finally {
            boolean unpackWAR;
            expandedDocBase = new File(this.host.getAppBaseFile(), cn.getBaseName());
            if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war") && !(expandedDocBase = new File(context.getDocBase())).isAbsolute()) {
                expandedDocBase = new File(this.host.getAppBaseFile(), context.getDocBase());
            }
            if ((unpackWAR = this.unpackWARs) && context instanceof StandardContext) {
                unpackWAR = ((StandardContext)context).getUnpackWAR();
            }
            if (isExternalWar) {
                if (unpackWAR) {
                    deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), expandedDocBase.lastModified());
                    this.addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), context);
                } else {
                    this.addWatchedResources(deployedApp, null, context);
                }
            } else {
                if (!isExternal) {
                    File warDocBase = new File(expandedDocBase.getAbsolutePath() + ".war");
                    if (warDocBase.exists()) {
                        deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), warDocBase.lastModified());
                    } else {
                        deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), 0L);
                    }
                }
                if (unpackWAR) {
                    deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), expandedDocBase.lastModified());
                    this.addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), context);
                } else {
                    this.addWatchedResources(deployedApp, null, context);
                }
                if (!isExternal) {
                    deployedApp.redeployResources.put(contextXml.getAbsolutePath(), contextXml.lastModified());
                }
            }
            this.addGlobalRedeployResources(deployedApp);
        }
        if (this.host.findChild(context.getName()) != null) {
            this.deployed.put(context.getName(), deployedApp);
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("hostConfig.deployDescriptor.finished", new Object[]{contextXml.getAbsolutePath(), System.currentTimeMillis() - startTime}));
        }
    }

    protected void deployWARs(File appBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        ArrayList results = new ArrayList();
        for (String file : files) {
            ContextName cn;
            if (file.equalsIgnoreCase("META-INF") || file.equalsIgnoreCase("WEB-INF")) continue;
            File war = new File(appBase, file);
            if (!file.toLowerCase(Locale.ENGLISH).endsWith(".war") || !war.isFile() || this.invalidWars.contains(file) || !this.tryAddServiced((cn = new ContextName(file, true)).getName())) continue;
            try {
                if (this.deploymentExists(cn.getName())) {
                    DeployedApplication app = this.deployed.get(cn.getName());
                    boolean unpackWAR = this.unpackWARs;
                    if (unpackWAR && this.host.findChild(cn.getName()) instanceof StandardContext) {
                        unpackWAR = ((StandardContext)this.host.findChild(cn.getName())).getUnpackWAR();
                    }
                    if (!unpackWAR && app != null) {
                        File dir = new File(appBase, cn.getBaseName());
                        if (dir.exists()) {
                            if (!app.loggedDirWarning) {
                                log.warn((Object)sm.getString("hostConfig.deployWar.hiddenDir", new Object[]{dir.getAbsoluteFile(), war.getAbsoluteFile()}));
                                app.loggedDirWarning = true;
                            }
                        } else {
                            app.loggedDirWarning = false;
                        }
                    }
                    this.removeServiced(cn.getName());
                    continue;
                }
                if (!this.validateContextPath(appBase, cn.getBaseName())) {
                    log.error((Object)sm.getString("hostConfig.illegalWarName", new Object[]{file}));
                    this.invalidWars.add(file);
                    this.removeServiced(cn.getName());
                    continue;
                }
                results.add(es.submit(new DeployWar(this, cn, war)));
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.removeServiced(cn.getName());
                throw t;
            }
        }
        for (Future future : results) {
            try {
                future.get();
            }
            catch (Exception e) {
                log.error((Object)sm.getString("hostConfig.deployWar.threaded.error"), (Throwable)e);
            }
        }
    }

    private boolean validateContextPath(File appBase, String contextPath) {
        StringBuilder docBase;
        String canonicalDocBase = null;
        try {
            String canonicalAppBase = appBase.getCanonicalPath();
            docBase = new StringBuilder(canonicalAppBase);
            if (canonicalAppBase.endsWith(File.separator)) {
                docBase.append(contextPath.substring(1).replace('/', File.separatorChar));
            } else {
                docBase.append(contextPath.replace('/', File.separatorChar));
            }
            canonicalDocBase = new File(docBase.toString()).getCanonicalPath();
            if (canonicalDocBase.endsWith(File.separator)) {
                docBase.append(File.separator);
            }
        }
        catch (IOException ioe) {
            return false;
        }
        return canonicalDocBase.equals(docBase.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void deployWAR(ContextName cn, File war) {
        InputStream istream;
        JarEntry entry;
        JarFile jar;
        boolean deployThisXML;
        Context context;
        boolean xmlInWar;
        File xml;
        block85: {
            xml = new File(this.host.getAppBaseFile(), cn.getBaseName() + "/" + "META-INF/context.xml");
            File warTracker = new File(this.host.getAppBaseFile(), cn.getBaseName() + "/META-INF/war-tracker");
            xmlInWar = false;
            try (JarFile jar22 = new JarFile(war);){
                JarEntry entry2 = jar22.getJarEntry("META-INF/context.xml");
                if (entry2 != null) {
                    xmlInWar = true;
                }
            }
            catch (IOException jar22) {
                // empty catch block
            }
            boolean useXml = false;
            if (xml.exists() && this.unpackWARs && (!warTracker.exists() || warTracker.lastModified() == war.lastModified())) {
                useXml = true;
            }
            context = null;
            deployThisXML = this.isDeployThisXML(war, cn);
            try {
                Object object;
                if (deployThisXML && useXml && !this.copyXML) {
                    object = this.digesterLock;
                    synchronized (object) {
                        try {
                            context = (Context)this.digester.parse(xml);
                        }
                        catch (Exception e) {
                            log.error((Object)sm.getString("hostConfig.deployDescriptor.error", new Object[]{war.getAbsolutePath()}), (Throwable)e);
                        }
                        finally {
                            this.digester.reset();
                            if (context == null) {
                                context = new FailedContext();
                            }
                        }
                    }
                    context.setConfigFile(xml.toURI().toURL());
                    break block85;
                }
                if (deployThisXML && xmlInWar) {
                    object = this.digesterLock;
                    synchronized (object) {
                        try {
                            jar = new JarFile(war);
                            try {
                                entry = jar.getJarEntry("META-INF/context.xml");
                                istream = jar.getInputStream(entry);
                                try {
                                    context = (Context)this.digester.parse(istream);
                                }
                                finally {
                                    if (istream != null) {
                                        istream.close();
                                    }
                                }
                            }
                            finally {
                                jar.close();
                            }
                        }
                        catch (Exception e) {
                            log.error((Object)sm.getString("hostConfig.deployDescriptor.error", new Object[]{war.getAbsolutePath()}), (Throwable)e);
                        }
                        finally {
                            this.digester.reset();
                            if (context == null) {
                                context = new FailedContext();
                            }
                            context.setConfigFile(UriUtil.buildJarUrl((File)war, (String)"META-INF/context.xml"));
                        }
                        break block85;
                    }
                }
                if (!deployThisXML && xmlInWar) {
                    log.error((Object)sm.getString("hostConfig.deployDescriptor.blocked", new Object[]{cn.getPath(), "META-INF/context.xml", new File(this.host.getConfigBaseFile(), cn.getBaseName() + ".xml")}));
                } else {
                    context = (Context)Class.forName(this.contextClass).getConstructor(new Class[0]).newInstance(new Object[0]);
                }
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)sm.getString("hostConfig.deployWar.error", new Object[]{war.getAbsolutePath()}), t);
            }
            finally {
                if (context == null) {
                    context = new FailedContext();
                }
            }
        }
        boolean copyThisXml = false;
        if (deployThisXML) {
            if (this.host instanceof StandardHost) {
                copyThisXml = ((StandardHost)this.host).isCopyXML();
            }
            if (!copyThisXml && context instanceof StandardContext) {
                copyThisXml = ((StandardContext)context).getCopyXML();
            }
            if (xmlInWar && copyThisXml) {
                xml = new File(this.host.getConfigBaseFile(), cn.getBaseName() + ".xml");
                try {
                    jar = new JarFile(war);
                    try {
                        entry = jar.getJarEntry("META-INF/context.xml");
                        istream = jar.getInputStream(entry);
                        try (FileOutputStream ostream = new FileOutputStream(xml);){
                            IOTools.flow(istream, ostream);
                        }
                        finally {
                            if (istream != null) {
                                istream.close();
                            }
                        }
                    }
                    finally {
                        jar.close();
                    }
                }
                catch (IOException jar3) {
                    // empty catch block
                }
            }
        }
        DeployedApplication deployedApp = new DeployedApplication(cn.getName(), xml.exists() && deployThisXML && copyThisXml);
        long startTime = 0L;
        if (log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            log.info((Object)sm.getString("hostConfig.deployWar", new Object[]{war.getAbsolutePath()}));
        }
        try {
            deployedApp.redeployResources.put(war.getAbsolutePath(), war.lastModified());
            if (deployThisXML && xml.exists() && copyThisXml) {
                deployedApp.redeployResources.put(xml.getAbsolutePath(), xml.lastModified());
            } else {
                deployedApp.redeployResources.put(new File(this.host.getConfigBaseFile(), cn.getBaseName() + ".xml").getAbsolutePath(), 0L);
            }
            Class<?> clazz = Class.forName(this.host.getConfigClass());
            LifecycleListener listener = (LifecycleListener)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            context.setName(cn.getName());
            context.setPath(cn.getPath());
            context.setWebappVersion(cn.getVersion());
            context.setDocBase(cn.getBaseName() + ".war");
            this.host.addChild(context);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.error((Object)sm.getString("hostConfig.deployWar.error", new Object[]{war.getAbsolutePath()}), t);
        }
        finally {
            boolean unpackWAR = this.unpackWARs;
            if (unpackWAR && context instanceof StandardContext) {
                unpackWAR = ((StandardContext)context).getUnpackWAR();
            }
            if (unpackWAR && context.getDocBase() != null) {
                File docBase = new File(this.host.getAppBaseFile(), cn.getBaseName());
                deployedApp.redeployResources.put(docBase.getAbsolutePath(), docBase.lastModified());
                this.addWatchedResources(deployedApp, docBase.getAbsolutePath(), context);
                if (deployThisXML && !copyThisXml && (xmlInWar || xml.exists())) {
                    deployedApp.redeployResources.put(xml.getAbsolutePath(), xml.lastModified());
                }
            } else {
                this.addWatchedResources(deployedApp, null, context);
            }
            this.addGlobalRedeployResources(deployedApp);
        }
        this.deployed.put(cn.getName(), deployedApp);
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("hostConfig.deployWar.finished", new Object[]{war.getAbsolutePath(), System.currentTimeMillis() - startTime}));
        }
    }

    protected void deployDirectories(File appBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        ArrayList results = new ArrayList();
        for (String file : files) {
            ContextName cn;
            File dir;
            if (file.equalsIgnoreCase("META-INF") || file.equalsIgnoreCase("WEB-INF") || !(dir = new File(appBase, file)).isDirectory() || !this.tryAddServiced((cn = new ContextName(file, false)).getName())) continue;
            try {
                if (this.deploymentExists(cn.getName())) {
                    this.removeServiced(cn.getName());
                    continue;
                }
                results.add(es.submit(new DeployDirectory(this, cn, dir)));
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.removeServiced(cn.getName());
                throw t;
            }
        }
        for (Future future : results) {
            try {
                future.get();
            }
            catch (Exception e) {
                log.error((Object)sm.getString("hostConfig.deployDir.threaded.error"), (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void deployDirectory(ContextName cn, File dir) {
        DeployedApplication deployedApp;
        long startTime = 0L;
        if (log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            log.info((Object)sm.getString("hostConfig.deployDir", new Object[]{dir.getAbsolutePath()}));
        }
        Context context = null;
        File xml = new File(dir, "META-INF/context.xml");
        File xmlCopy = new File(this.host.getConfigBaseFile(), cn.getBaseName() + ".xml");
        boolean copyThisXml = this.isCopyXML();
        boolean deployThisXML = this.isDeployThisXML(dir, cn);
        try {
            if (deployThisXML && xml.exists()) {
                Object object = this.digesterLock;
                synchronized (object) {
                    try {
                        context = (Context)this.digester.parse(xml);
                    }
                    catch (Exception e) {
                        log.error((Object)sm.getString("hostConfig.deployDescriptor.error", new Object[]{xml}), (Throwable)e);
                        context = new FailedContext();
                    }
                    finally {
                        this.digester.reset();
                        if (context == null) {
                            context = new FailedContext();
                        }
                    }
                }
                if (!copyThisXml && context instanceof StandardContext) {
                    copyThisXml = ((StandardContext)context).getCopyXML();
                }
                if (copyThisXml) {
                    Files.copy(xml.toPath(), xmlCopy.toPath(), new CopyOption[0]);
                    context.setConfigFile(xmlCopy.toURI().toURL());
                } else {
                    context.setConfigFile(xml.toURI().toURL());
                }
            } else if (!deployThisXML && xml.exists()) {
                log.error((Object)sm.getString("hostConfig.deployDescriptor.blocked", new Object[]{cn.getPath(), xml, xmlCopy}));
                context = new FailedContext();
            } else {
                context = (Context)Class.forName(this.contextClass).getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            Class<?> clazz = Class.forName(this.host.getConfigClass());
            LifecycleListener listener = (LifecycleListener)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            context.addLifecycleListener(listener);
            context.setName(cn.getName());
            context.setPath(cn.getPath());
            context.setWebappVersion(cn.getVersion());
            context.setDocBase(cn.getBaseName());
            this.host.addChild(context);
            deployedApp = new DeployedApplication(cn.getName(), xml.exists() && deployThisXML && copyThisXml);
        }
        catch (Throwable t) {
            try {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)sm.getString("hostConfig.deployDir.error", new Object[]{dir.getAbsolutePath()}), t);
                deployedApp = new DeployedApplication(cn.getName(), xml.exists() && deployThisXML && copyThisXml);
            }
            catch (Throwable throwable) {
                DeployedApplication deployedApp2 = new DeployedApplication(cn.getName(), xml.exists() && deployThisXML && copyThisXml);
                deployedApp2.redeployResources.put(dir.getAbsolutePath() + ".war", 0L);
                deployedApp2.redeployResources.put(dir.getAbsolutePath(), dir.lastModified());
                if (deployThisXML && xml.exists()) {
                    if (copyThisXml) {
                        deployedApp2.redeployResources.put(xmlCopy.getAbsolutePath(), xmlCopy.lastModified());
                    } else {
                        deployedApp2.redeployResources.put(xml.getAbsolutePath(), xml.lastModified());
                        deployedApp2.redeployResources.put(xmlCopy.getAbsolutePath(), 0L);
                    }
                } else {
                    deployedApp2.redeployResources.put(xmlCopy.getAbsolutePath(), 0L);
                    if (!xml.exists()) {
                        deployedApp2.redeployResources.put(xml.getAbsolutePath(), 0L);
                    }
                }
                this.addWatchedResources(deployedApp2, dir.getAbsolutePath(), context);
                this.addGlobalRedeployResources(deployedApp2);
                throw throwable;
            }
            deployedApp.redeployResources.put(dir.getAbsolutePath() + ".war", 0L);
            deployedApp.redeployResources.put(dir.getAbsolutePath(), dir.lastModified());
            if (deployThisXML && xml.exists()) {
                if (copyThisXml) {
                    deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), xmlCopy.lastModified());
                } else {
                    deployedApp.redeployResources.put(xml.getAbsolutePath(), xml.lastModified());
                    deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), 0L);
                }
            } else {
                deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), 0L);
                if (!xml.exists()) {
                    deployedApp.redeployResources.put(xml.getAbsolutePath(), 0L);
                }
            }
            this.addWatchedResources(deployedApp, dir.getAbsolutePath(), context);
            this.addGlobalRedeployResources(deployedApp);
        }
        deployedApp.redeployResources.put(dir.getAbsolutePath() + ".war", 0L);
        deployedApp.redeployResources.put(dir.getAbsolutePath(), dir.lastModified());
        if (deployThisXML && xml.exists()) {
            if (copyThisXml) {
                deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), xmlCopy.lastModified());
            } else {
                deployedApp.redeployResources.put(xml.getAbsolutePath(), xml.lastModified());
                deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), 0L);
            }
        } else {
            deployedApp.redeployResources.put(xmlCopy.getAbsolutePath(), 0L);
            if (!xml.exists()) {
                deployedApp.redeployResources.put(xml.getAbsolutePath(), 0L);
            }
        }
        this.addWatchedResources(deployedApp, dir.getAbsolutePath(), context);
        this.addGlobalRedeployResources(deployedApp);
        this.deployed.put(cn.getName(), deployedApp);
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("hostConfig.deployDir.finished", new Object[]{dir.getAbsolutePath(), System.currentTimeMillis() - startTime}));
        }
    }

    protected boolean deploymentExists(String contextName) {
        return this.deployed.containsKey(contextName) || this.host.findChild(contextName) != null;
    }

    protected void addWatchedResources(DeployedApplication app, String docBase, Context context) {
        String[] watchedResources;
        File docBaseFile = null;
        if (docBase != null && !(docBaseFile = new File(docBase)).isAbsolute()) {
            docBaseFile = new File(this.host.getAppBaseFile(), docBase);
        }
        for (String watchedResource : watchedResources = context.findWatchedResources()) {
            File resource = new File(watchedResource);
            if (!resource.isAbsolute()) {
                if (docBase != null) {
                    resource = new File(docBaseFile, watchedResource);
                } else {
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("Ignoring non-existent WatchedResource '" + resource.getAbsolutePath() + "'"));
                    continue;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Watching WatchedResource '" + resource.getAbsolutePath() + "'"));
            }
            app.reloadResources.put(resource.getAbsolutePath(), resource.lastModified());
        }
    }

    protected void addGlobalRedeployResources(DeployedApplication app) {
        File globalContextXml;
        File hostContextXml = new File(this.getConfigBaseName(), "context.xml.default");
        if (hostContextXml.isFile()) {
            app.redeployResources.put(hostContextXml.getAbsolutePath(), hostContextXml.lastModified());
        }
        if ((globalContextXml = this.returnCanonicalPath("conf/context.xml")).isFile()) {
            app.redeployResources.put(globalContextXml.getAbsolutePath(), globalContextXml.lastModified());
        }
    }

    protected synchronized void checkResources(DeployedApplication app, boolean skipFileModificationResolutionCheck) {
        String[] resources = app.redeployResources.keySet().toArray(new String[0]);
        long currentTimeWithResolutionOffset = System.currentTimeMillis() - 1000L;
        for (int i = 0; i < resources.length; ++i) {
            File resource = new File(resources[i]);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Checking context[" + app.name + "] redeploy resource " + resource));
            }
            long lastModified = app.redeployResources.get(resources[i]);
            if (resource.exists() || lastModified == 0L) {
                if (resource.lastModified() == lastModified || this.host.getAutoDeploy() && resource.lastModified() >= currentTimeWithResolutionOffset && !skipFileModificationResolutionCheck) continue;
                if (resource.isDirectory()) {
                    app.redeployResources.put(resources[i], resource.lastModified());
                    continue;
                }
                if (app.hasDescriptor && resource.getName().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    Context context = (Context)this.host.findChild(app.name);
                    String docBase = context.getDocBase();
                    if (!docBase.toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        File docBaseFile = new File(docBase);
                        if (!docBaseFile.isAbsolute()) {
                            docBaseFile = new File(this.host.getAppBaseFile(), docBase);
                        }
                        this.reload(app, docBaseFile, resource.getAbsolutePath());
                    } else {
                        this.reload(app, null, null);
                    }
                    app.redeployResources.put(resources[i], resource.lastModified());
                    app.timestamp = System.currentTimeMillis();
                    boolean unpackWAR = this.unpackWARs;
                    if (unpackWAR && context instanceof StandardContext) {
                        unpackWAR = ((StandardContext)context).getUnpackWAR();
                    }
                    if (unpackWAR) {
                        this.addWatchedResources(app, context.getDocBase(), context);
                    } else {
                        this.addWatchedResources(app, null, context);
                    }
                    return;
                }
                this.undeploy(app);
                this.deleteRedeployResources(app, resources, i, false);
                return;
            }
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException context) {
                // empty catch block
            }
            if (resource.exists()) continue;
            this.undeploy(app);
            this.deleteRedeployResources(app, resources, i, true);
            return;
        }
        resources = app.reloadResources.keySet().toArray(new String[0]);
        boolean update = false;
        for (String s : resources) {
            File resource = new File(s);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Checking context[" + app.name + "] reload resource " + resource));
            }
            long lastModified = app.reloadResources.get(s);
            if (resource.lastModified() != lastModified && (!this.host.getAutoDeploy() || resource.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck) || update) {
                if (!update) {
                    this.reload(app, null, null);
                    update = true;
                }
                app.reloadResources.put(s, resource.lastModified());
            }
            app.timestamp = System.currentTimeMillis();
        }
    }

    private void reload(DeployedApplication app, File fileToRemove, String newDocBase) {
        Context context;
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("hostConfig.reload", new Object[]{app.name}));
        }
        if ((context = (Context)this.host.findChild(app.name)).getState().isAvailable()) {
            if (fileToRemove != null && newDocBase != null) {
                context.addLifecycleListener(new ExpandedDirectoryRemovalListener(fileToRemove, newDocBase));
            }
            context.reload();
        } else {
            if (fileToRemove != null && newDocBase != null) {
                ExpandWar.delete(fileToRemove);
                context.setDocBase(newDocBase);
            }
            try {
                context.start();
            }
            catch (Exception e) {
                log.error((Object)sm.getString("hostConfig.context.restart", new Object[]{app.name}), (Throwable)e);
            }
        }
    }

    private void undeploy(DeployedApplication app) {
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("hostConfig.undeploy", new Object[]{app.name}));
        }
        Container context = this.host.findChild(app.name);
        try {
            this.host.removeChild(context);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.warn((Object)sm.getString("hostConfig.context.remove", new Object[]{app.name}), t);
        }
        this.deployed.remove(app.name);
    }

    private void deleteRedeployResources(DeployedApplication app, String[] resources, int i, boolean deleteReloadResources) {
        for (int j = i + 1; j < resources.length; ++j) {
            File current = new File(resources[j]);
            if ("context.xml.default".equals(current.getName()) || !this.isDeletableResource(app, current)) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("Delete " + current));
            }
            ExpandWar.delete(current);
        }
        if (deleteReloadResources) {
            String[] resources2;
            for (String s : resources2 = app.reloadResources.keySet().toArray(new String[0])) {
                File current = new File(s);
                if ("context.xml.default".equals(current.getName()) || !this.isDeletableResource(app, current)) continue;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Delete " + current));
                }
                ExpandWar.delete(current);
            }
        }
    }

    private boolean isDeletableResource(DeployedApplication app, File resource) {
        String canonicalConfigBase;
        String canonicalAppBase;
        String canonicalLocation;
        if (!resource.isAbsolute()) {
            log.warn((Object)sm.getString("hostConfig.resourceNotAbsolute", new Object[]{app.name, resource}));
            return false;
        }
        try {
            canonicalLocation = resource.getParentFile().getCanonicalPath();
        }
        catch (IOException e) {
            log.warn((Object)sm.getString("hostConfig.canonicalizing", new Object[]{resource.getParentFile(), app.name}), (Throwable)e);
            return false;
        }
        try {
            canonicalAppBase = this.host.getAppBaseFile().getCanonicalPath();
        }
        catch (IOException e) {
            log.warn((Object)sm.getString("hostConfig.canonicalizing", new Object[]{this.host.getAppBaseFile(), app.name}), (Throwable)e);
            return false;
        }
        if (canonicalLocation.equals(canonicalAppBase)) {
            return true;
        }
        try {
            canonicalConfigBase = this.host.getConfigBaseFile().getCanonicalPath();
        }
        catch (IOException e) {
            log.warn((Object)sm.getString("hostConfig.canonicalizing", new Object[]{this.host.getConfigBaseFile(), app.name}), (Throwable)e);
            return false;
        }
        return canonicalLocation.equals(canonicalConfigBase) && resource.getName().endsWith(".xml");
    }

    public void beforeStart() {
        if (this.host.getCreateDirs()) {
            File[] dirs;
            for (File dir : dirs = new File[]{this.host.getAppBaseFile(), this.host.getConfigBaseFile()}) {
                if (dir.mkdirs() || dir.isDirectory()) continue;
                log.error((Object)sm.getString("hostConfig.createDirs", new Object[]{dir}));
            }
        }
    }

    public void start() {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("hostConfig.start"));
        }
        try {
            ObjectName hostON = this.host.getObjectName();
            this.oname = new ObjectName(hostON.getDomain() + ":type=Deployer,host=" + this.host.getName());
            Registry.getRegistry(null, null).registerComponent((Object)this, this.oname, this.getClass().getName());
        }
        catch (Exception e) {
            log.warn((Object)sm.getString("hostConfig.jmx.register", new Object[]{this.oname}), (Throwable)e);
        }
        if (!this.host.getAppBaseFile().isDirectory()) {
            log.error((Object)sm.getString("hostConfig.appBase", new Object[]{this.host.getName(), this.host.getAppBaseFile().getPath()}));
            this.host.setDeployOnStartup(false);
            this.host.setAutoDeploy(false);
        }
        if (this.host.getDeployOnStartup()) {
            this.deployApps();
        }
    }

    public void stop() {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("hostConfig.stop"));
        }
        if (this.oname != null) {
            try {
                Registry.getRegistry(null, null).unregisterComponent(this.oname);
            }
            catch (Exception e) {
                log.warn((Object)sm.getString("hostConfig.jmx.unregister", new Object[]{this.oname}), (Throwable)e);
            }
        }
        this.oname = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void check() {
        if (this.host.getAutoDeploy()) {
            DeployedApplication[] apps;
            for (DeployedApplication app : apps = this.deployed.values().toArray(new DeployedApplication[0])) {
                if (!this.tryAddServiced(app.name)) continue;
                try {
                    this.checkResources(app, false);
                }
                finally {
                    this.removeServiced(app.name);
                }
            }
            if (this.host.getUndeployOldVersions()) {
                this.checkUndeploy();
            }
            this.deployApps();
        }
    }

    public void check(String name) {
        if (this.tryAddServiced(name)) {
            try {
                DeployedApplication app = this.deployed.get(name);
                if (app != null) {
                    this.checkResources(app, true);
                }
                this.deployApps(name);
            }
            finally {
                this.removeServiced(name);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void checkUndeploy() {
        if (this.deployed.size() < 2) {
            return;
        }
        TreeSet<String> sortedAppNames = new TreeSet<String>(this.deployed.keySet());
        Iterator iter = sortedAppNames.iterator();
        ContextName previous = new ContextName((String)iter.next(), false);
        do {
            ContextName current;
            if ((current = new ContextName((String)iter.next(), false)).getPath().equals(previous.getPath())) {
                Context previousContext = (Context)this.host.findChild(previous.getName());
                Context currentContext = (Context)this.host.findChild(current.getName());
                if (previousContext != null && currentContext != null && currentContext.getState().isAvailable() && this.tryAddServiced(previous.getName())) {
                    try {
                        int sessionCount;
                        Manager manager = previousContext.getManager();
                        if (manager != null && (sessionCount = manager instanceof DistributedManager ? ((DistributedManager)((Object)manager)).getActiveSessionsFull() : manager.getActiveSessions()) == 0) {
                            if (log.isInfoEnabled()) {
                                log.info((Object)sm.getString("hostConfig.undeployVersion", new Object[]{previous.getName()}));
                            }
                            DeployedApplication app = this.deployed.get(previous.getName());
                            String[] resources = app.redeployResources.keySet().toArray(new String[0]);
                            this.undeploy(app);
                            this.deleteRedeployResources(app, resources, -1, true);
                        }
                    }
                    finally {
                        this.removeServiced(previous.getName());
                    }
                }
            }
            previous = current;
        } while (iter.hasNext());
    }

    public void manageApp(Context context) {
        String contextName = context.getName();
        if (this.deployed.containsKey(contextName)) {
            return;
        }
        DeployedApplication deployedApp = new DeployedApplication(contextName, false);
        boolean isWar = false;
        if (context.getDocBase() != null) {
            File docBase = new File(context.getDocBase());
            if (!docBase.isAbsolute()) {
                docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
            }
            deployedApp.redeployResources.put(docBase.getAbsolutePath(), docBase.lastModified());
            if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                isWar = true;
            }
        }
        this.host.addChild(context);
        boolean unpackWAR = this.unpackWARs;
        if (unpackWAR && context instanceof StandardContext) {
            unpackWAR = ((StandardContext)context).getUnpackWAR();
        }
        if (isWar && unpackWAR) {
            File docBase = new File(this.host.getAppBaseFile(), context.getBaseName());
            deployedApp.redeployResources.put(docBase.getAbsolutePath(), docBase.lastModified());
            this.addWatchedResources(deployedApp, docBase.getAbsolutePath(), context);
        } else {
            this.addWatchedResources(deployedApp, null, context);
        }
        this.deployed.put(contextName, deployedApp);
    }

    public void unmanageApp(String contextName) {
        this.deployed.remove(contextName);
        this.host.removeChild(this.host.findChild(contextName));
    }

    protected static class DeployedApplication {
        public final String name;
        public final boolean hasDescriptor;
        public final LinkedHashMap<String, Long> redeployResources = new LinkedHashMap();
        public final HashMap<String, Long> reloadResources = new HashMap();
        public long timestamp = System.currentTimeMillis();
        public boolean loggedDirWarning = false;

        public DeployedApplication(String name, boolean hasDescriptor) {
            this.name = name;
            this.hasDescriptor = hasDescriptor;
        }
    }

    private static class DeployDescriptor
    implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File descriptor;

        DeployDescriptor(HostConfig config, ContextName cn, File descriptor) {
            this.config = config;
            this.cn = cn;
            this.descriptor = descriptor;
        }

        @Override
        public void run() {
            try {
                this.config.deployDescriptor(this.cn, this.descriptor);
            }
            finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }

    private static class DeployWar
    implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File war;

        DeployWar(HostConfig config, ContextName cn, File war) {
            this.config = config;
            this.cn = cn;
            this.war = war;
        }

        @Override
        public void run() {
            try {
                this.config.deployWAR(this.cn, this.war);
            }
            finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }

    private static class DeployDirectory
    implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File dir;

        DeployDirectory(HostConfig config, ContextName cn, File dir) {
            this.config = config;
            this.cn = cn;
            this.dir = dir;
        }

        @Override
        public void run() {
            try {
                this.config.deployDirectory(this.cn, this.dir);
            }
            finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }

    private static class ExpandedDirectoryRemovalListener
    implements LifecycleListener {
        private final File toDelete;
        private final String newDocBase;

        ExpandedDirectoryRemovalListener(File toDelete, String newDocBase) {
            this.toDelete = toDelete;
            this.newDocBase = newDocBase;
        }

        @Override
        public void lifecycleEvent(LifecycleEvent event) {
            if ("after_stop".equals(event.getType())) {
                Context context = (Context)event.getLifecycle();
                ExpandWar.delete(this.toDelete);
                context.setDocBase(this.newDocBase);
                context.removeLifecycleListener(this);
            }
        }
    }
}

