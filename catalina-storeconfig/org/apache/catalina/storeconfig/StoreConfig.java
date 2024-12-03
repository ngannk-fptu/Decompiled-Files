/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Context
 *  org.apache.catalina.Host
 *  org.apache.catalina.Server
 *  org.apache.catalina.Service
 *  org.apache.catalina.core.StandardContext
 *  org.apache.catalina.mbeans.MBeanUtils
 *  org.apache.catalina.startup.Bootstrap
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import java.net.URL;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.catalina.startup.Bootstrap;
import org.apache.catalina.storeconfig.IStoreConfig;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFileMover;
import org.apache.catalina.storeconfig.StoreRegistry;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class StoreConfig
implements IStoreConfig {
    private static Log log = LogFactory.getLog(StoreConfig.class);
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.storeconfig");
    private String serverFilename = "conf/server.xml";
    private StoreRegistry registry;
    private Server server;

    public String getServerFilename() {
        return this.serverFilename;
    }

    public void setServerFilename(String string) {
        this.serverFilename = string;
    }

    @Override
    public StoreRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public void setServer(Server aServer) {
        this.server = aServer;
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    @Override
    public void setRegistry(StoreRegistry aRegistry) {
        this.registry = aRegistry;
    }

    @Override
    public void storeConfig() {
        this.store(this.server);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void storeServer(String aServerName, boolean backup, boolean externalAllowed) throws MalformedObjectNameException {
        block9: {
            ObjectName objectName;
            if (aServerName == null || aServerName.length() == 0) {
                log.error((Object)sm.getString("config.emptyObjectName"));
                return;
            }
            MBeanServer mserver = MBeanUtils.createServer();
            if (mserver.isRegistered(objectName = new ObjectName(aServerName))) {
                try {
                    Server aServer = (Server)mserver.getAttribute(objectName, "managedResource");
                    StoreDescription desc = null;
                    desc = this.getRegistry().findDescription(StandardContext.class);
                    if (desc != null) {
                        boolean oldSeparate = desc.isStoreSeparate();
                        boolean oldBackup = desc.isBackup();
                        boolean oldExternalAllowed = desc.isExternalAllowed();
                        try {
                            desc.setStoreSeparate(true);
                            desc.setBackup(backup);
                            desc.setExternalAllowed(externalAllowed);
                            this.store(aServer);
                            break block9;
                        }
                        finally {
                            desc.setStoreSeparate(oldSeparate);
                            desc.setBackup(oldBackup);
                            desc.setExternalAllowed(oldExternalAllowed);
                        }
                    }
                    this.store(aServer);
                }
                catch (Exception e) {
                    log.error((Object)sm.getString("config.storeServerError"), (Throwable)e);
                }
            } else {
                log.info((Object)sm.getString("config.objectNameNotFound", new Object[]{aServerName}));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void storeContext(String aContextName, boolean backup, boolean externalAllowed) throws MalformedObjectNameException {
        block9: {
            ObjectName objectName;
            if (aContextName == null || aContextName.length() == 0) {
                log.error((Object)sm.getString("config.emptyObjectName"));
                return;
            }
            MBeanServer mserver = MBeanUtils.createServer();
            if (mserver.isRegistered(objectName = new ObjectName(aContextName))) {
                try {
                    Context aContext = (Context)mserver.getAttribute(objectName, "managedResource");
                    URL configFile = aContext.getConfigFile();
                    if (configFile != null) {
                        StoreDescription desc = null;
                        desc = this.getRegistry().findDescription(aContext.getClass());
                        if (desc == null) break block9;
                        boolean oldSeparate = desc.isStoreSeparate();
                        boolean oldBackup = desc.isBackup();
                        boolean oldExternalAllowed = desc.isExternalAllowed();
                        try {
                            desc.setStoreSeparate(true);
                            desc.setBackup(backup);
                            desc.setExternalAllowed(externalAllowed);
                            desc.getStoreFactory().store(null, -2, aContext);
                            break block9;
                        }
                        finally {
                            desc.setStoreSeparate(oldSeparate);
                            desc.setBackup(oldBackup);
                            desc.setBackup(oldExternalAllowed);
                        }
                    }
                    log.error((Object)sm.getString("config.missingContextFile", new Object[]{aContext.getPath()}));
                }
                catch (Exception e) {
                    log.error((Object)sm.getString("config.storeContextError", new Object[]{aContextName}), (Throwable)e);
                }
            } else {
                log.info((Object)sm.getString("config.objectNameNotFound", new Object[]{aContextName}));
            }
        }
    }

    @Override
    public synchronized boolean store(Server aServer) {
        StoreFileMover mover = new StoreFileMover(Bootstrap.getCatalinaBase(), this.getServerFilename(), this.getRegistry().getEncoding());
        try {
            try (PrintWriter writer = mover.getWriter();){
                this.store(writer, -2, aServer);
            }
            mover.move();
            return true;
        }
        catch (Exception e) {
            log.error((Object)sm.getString("config.storeServerError"), (Throwable)e);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized boolean store(Context aContext) {
        try {
            StoreDescription desc = null;
            desc = this.getRegistry().findDescription(aContext.getClass());
            if (desc != null) {
                boolean old = desc.isStoreSeparate();
                try {
                    desc.setStoreSeparate(true);
                    desc.getStoreFactory().store(null, -2, aContext);
                }
                finally {
                    desc.setStoreSeparate(old);
                }
            }
            return true;
        }
        catch (Exception e) {
            log.error((Object)sm.getString("config.storeContextError", new Object[]{aContext.getName()}), (Throwable)e);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void store(PrintWriter aWriter, int indent, Context aContext) throws Exception {
        block5: {
            boolean oldSeparate = true;
            StoreDescription desc = null;
            try {
                desc = this.getRegistry().findDescription(aContext.getClass());
                if (desc != null) {
                    oldSeparate = desc.isStoreSeparate();
                    desc.setStoreSeparate(false);
                    desc.getStoreFactory().store(aWriter, indent, aContext);
                }
                if (desc == null) break block5;
                desc.setStoreSeparate(oldSeparate);
            }
            catch (Throwable throwable) {
                if (desc != null) {
                    desc.setStoreSeparate(oldSeparate);
                } else {
                    log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aContext.getClass()}));
                }
                throw throwable;
            }
        }
        log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aContext.getClass()}));
    }

    @Override
    public void store(PrintWriter aWriter, int indent, Host aHost) throws Exception {
        StoreDescription desc = this.getRegistry().findDescription(aHost.getClass());
        if (desc != null) {
            desc.getStoreFactory().store(aWriter, indent, aHost);
        } else {
            log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aHost.getClass()}));
        }
    }

    @Override
    public void store(PrintWriter aWriter, int indent, Service aService) throws Exception {
        StoreDescription desc = this.getRegistry().findDescription(aService.getClass());
        if (desc != null) {
            desc.getStoreFactory().store(aWriter, indent, aService);
        } else {
            log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aService.getClass()}));
        }
    }

    @Override
    public void store(PrintWriter writer, int indent, Server aServer) throws Exception {
        StoreDescription desc = this.getRegistry().findDescription(aServer.getClass());
        if (desc != null) {
            desc.getStoreFactory().store(writer, indent, aServer);
        } else {
            log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aServer.getClass()}));
        }
    }
}

