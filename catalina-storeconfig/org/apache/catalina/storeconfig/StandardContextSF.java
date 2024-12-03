/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Container
 *  org.apache.catalina.Context
 *  org.apache.catalina.Engine
 *  org.apache.catalina.Host
 *  org.apache.catalina.LifecycleListener
 *  org.apache.catalina.Loader
 *  org.apache.catalina.Manager
 *  org.apache.catalina.Realm
 *  org.apache.catalina.WebResourceRoot
 *  org.apache.catalina.core.StandardContext
 *  org.apache.catalina.core.ThreadLocalLeakPreventionListener
 *  org.apache.catalina.deploy.NamingResourcesImpl
 *  org.apache.catalina.util.ContextName
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.JarScanner
 *  org.apache.tomcat.util.http.CookieProcessor
 */
package org.apache.catalina.storeconfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.ThreadLocalLeakPreventionListener;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.catalina.storeconfig.StoreFileMover;
import org.apache.catalina.util.ContextName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.http.CookieProcessor;

public class StandardContextSF
extends StoreFactoryBase {
    private static Log log = LogFactory.getLog(StandardContextSF.class);

    @Override
    public void store(PrintWriter aWriter, int indent, Object aContext) throws Exception {
        StoreDescription desc;
        if (aContext instanceof StandardContext && (desc = this.getRegistry().findDescription(aContext.getClass())) != null && desc.isStoreSeparate()) {
            URL configFile = ((StandardContext)aContext).getConfigFile();
            if (configFile != null) {
                if (desc.isExternalAllowed()) {
                    if (desc.isBackup()) {
                        this.storeWithBackup((StandardContext)aContext);
                    } else {
                        this.storeContextSeparate(aWriter, indent, (StandardContext)aContext);
                    }
                    return;
                }
            } else if (desc.isExternalOnly()) {
                StandardContext context = (StandardContext)aContext;
                Host host = (Host)context.getParent();
                File configBase = host.getConfigBaseFile();
                ContextName cn = new ContextName(context.getName(), false);
                String baseName = cn.getBaseName();
                File xml = new File(configBase, baseName + ".xml");
                context.setConfigFile(xml.toURI().toURL());
                if (desc.isBackup()) {
                    this.storeWithBackup((StandardContext)aContext);
                } else {
                    this.storeContextSeparate(aWriter, indent, (StandardContext)aContext);
                }
                return;
            }
        }
        super.store(aWriter, indent, aContext);
    }

    protected void storeContextSeparate(PrintWriter aWriter, int indent, StandardContext aContext) throws Exception {
        block14: {
            URL configFile = aContext.getConfigFile();
            if (configFile != null) {
                File config = new File(configFile.toURI());
                if (!config.isAbsolute()) {
                    config = new File(System.getProperty("catalina.base"), config.getPath());
                }
                if (!config.isFile() || !config.canWrite()) {
                    throw new IOException(sm.getString("standardContextSF.cannotWriteFile", new Object[]{configFile}));
                }
                if (log.isInfoEnabled()) {
                    log.info((Object)sm.getString("standardContextSF.storeContext", new Object[]{aContext.getPath(), config}));
                }
                try (FileOutputStream fos = new FileOutputStream(config);
                     PrintWriter writer = new PrintWriter(new OutputStreamWriter((OutputStream)fos, this.getRegistry().getEncoding()));){
                    this.storeXMLHead(writer);
                    super.store(writer, -2, aContext);
                    break block14;
                }
            }
            super.store(aWriter, indent, aContext);
        }
    }

    protected void storeWithBackup(StandardContext aContext) throws Exception {
        StoreFileMover mover = this.getConfigFileWriter((Context)aContext);
        if (mover != null) {
            if (mover.getConfigOld() == null || mover.getConfigOld().isDirectory() || mover.getConfigOld().exists() && !mover.getConfigOld().canWrite()) {
                throw new IOException(sm.getString("standardContextSF.moveFailed", new Object[]{mover.getConfigOld()}));
            }
            File dir = mover.getConfigSave().getParentFile();
            if (dir != null && dir.isDirectory() && !dir.canWrite()) {
                throw new IOException(sm.getString("standardContextSF.cannotWriteFile", new Object[]{mover.getConfigSave()}));
            }
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("standardContextSF.storeContextWithBackup", new Object[]{aContext.getPath(), mover.getConfigSave()}));
            }
            try (PrintWriter writer = mover.getWriter();){
                this.storeXMLHead(writer);
                super.store(writer, -2, aContext);
            }
            mover.move();
        }
    }

    protected StoreFileMover getConfigFileWriter(Context context) throws Exception {
        URL configFile = context.getConfigFile();
        StoreFileMover mover = null;
        if (configFile != null) {
            File config = new File(configFile.toURI());
            if (!config.isAbsolute()) {
                config = new File(System.getProperty("catalina.base"), config.getPath());
            }
            mover = new StoreFileMover("", config.getCanonicalPath(), this.getRegistry().getEncoding());
        }
        return mover;
    }

    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aContext, StoreDescription parentDesc) throws Exception {
        if (aContext instanceof StandardContext) {
            Realm realm;
            StandardContext context = (StandardContext)aContext;
            LifecycleListener[] listeners = context.findLifecycleListeners();
            ArrayList<LifecycleListener> listenersArray = new ArrayList<LifecycleListener>();
            for (LifecycleListener listener : listeners) {
                if (listener instanceof ThreadLocalLeakPreventionListener) continue;
                listenersArray.add(listener);
            }
            this.storeElementArray(aWriter, indent, listenersArray.toArray());
            Object[] valves = context.getPipeline().getValves();
            this.storeElementArray(aWriter, indent, valves);
            Loader loader = context.getLoader();
            this.storeElement(aWriter, indent, loader);
            if (context.getCluster() == null || !context.getDistributable()) {
                Manager manager = context.getManager();
                this.storeElement(aWriter, indent, manager);
            }
            if ((realm = context.getRealm()) != null) {
                Realm parentRealm = null;
                if (context.getParent() != null) {
                    parentRealm = context.getParent().getRealm();
                }
                if (realm != parentRealm) {
                    this.storeElement(aWriter, indent, realm);
                }
            }
            WebResourceRoot resources = context.getResources();
            this.storeElement(aWriter, indent, resources);
            String[] wLifecycles = context.findWrapperLifecycles();
            this.getStoreAppender().printTagArray(aWriter, "WrapperListener", indent + 2, wLifecycles);
            String[] wListeners = context.findWrapperListeners();
            this.getStoreAppender().printTagArray(aWriter, "WrapperLifecycle", indent + 2, wListeners);
            Object[] appParams = context.findApplicationParameters();
            this.storeElementArray(aWriter, indent, appParams);
            NamingResourcesImpl nresources = context.getNamingResources();
            this.storeElement(aWriter, indent, nresources);
            String[] wresources = context.findWatchedResources();
            wresources = this.filterWatchedResources(context, wresources);
            this.getStoreAppender().printTagArray(aWriter, "WatchedResource", indent + 2, wresources);
            JarScanner jarScanner = context.getJarScanner();
            this.storeElement(aWriter, indent, jarScanner);
            CookieProcessor cookieProcessor = context.getCookieProcessor();
            this.storeElement(aWriter, indent, cookieProcessor);
        }
    }

    protected File configBase(Context context) {
        File file = new File(System.getProperty("catalina.base"), "conf");
        Container host = context.getParent();
        if (host instanceof Host) {
            Container engine = host.getParent();
            if (engine instanceof Engine) {
                file = new File(file, engine.getName());
            }
            file = new File(file, host.getName());
            try {
                file = file.getCanonicalFile();
            }
            catch (IOException e) {
                log.error((Object)sm.getString("standardContextSF.canonicalPathError"), (Throwable)e);
            }
        }
        return file;
    }

    protected String[] filterWatchedResources(StandardContext context, String[] wresources) throws Exception {
        File configBase = this.configBase((Context)context);
        String confContext = new File(System.getProperty("catalina.base"), "conf/context.xml").getCanonicalPath();
        String confWeb = new File(System.getProperty("catalina.base"), "conf/web.xml").getCanonicalPath();
        String confHostDefault = new File(configBase, "context.xml.default").getCanonicalPath();
        String configFile = context.getConfigFile() != null ? new File(context.getConfigFile().toURI()).getCanonicalPath() : null;
        String webxml = "WEB-INF/web.xml";
        String tomcatwebxml = "WEB-INF/tomcat-web.xml";
        ArrayList<String> resource = new ArrayList<String>();
        for (String wresource : wresources) {
            if (wresource.equals(confContext) || wresource.equals(confWeb) || wresource.equals(confHostDefault) || wresource.equals(configFile) || wresource.equals(webxml) || wresource.equals(tomcatwebxml)) continue;
            resource.add(wresource);
        }
        return resource.toArray(new String[0]);
    }
}

