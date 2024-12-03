/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Container
 *  org.apache.catalina.Context
 *  org.apache.catalina.Engine
 *  org.apache.catalina.Host
 *  org.apache.catalina.LifecycleException
 *  org.apache.catalina.tribes.Member
 *  org.apache.catalina.util.ContextName
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.ha.ClusterDeployer;
import org.apache.catalina.ha.ClusterListener;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.catalina.ha.deploy.FileChangeListener;
import org.apache.catalina.ha.deploy.FileMessage;
import org.apache.catalina.ha.deploy.FileMessageFactory;
import org.apache.catalina.ha.deploy.UndeployMessage;
import org.apache.catalina.ha.deploy.WarWatcher;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.util.ContextName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class FarmWarDeployer
extends ClusterListener
implements ClusterDeployer,
FileChangeListener {
    private static final Log log = LogFactory.getLog(FarmWarDeployer.class);
    private static final StringManager sm = StringManager.getManager(FarmWarDeployer.class);
    protected boolean started = false;
    protected final HashMap<String, FileMessageFactory> fileFactories = new HashMap();
    protected String deployDir;
    private File deployDirFile = null;
    protected String tempDir;
    private File tempDirFile = null;
    protected String watchDir;
    private File watchDirFile = null;
    protected boolean watchEnabled = false;
    protected WarWatcher watcher = null;
    private int count = 0;
    protected int processDeployFrequency = 2;
    protected File configBase = null;
    protected Host host = null;
    protected MBeanServer mBeanServer = null;
    protected ObjectName oname = null;
    protected int maxValidTime = 300;

    @Override
    public void start() throws Exception {
        if (this.started) {
            return;
        }
        Container hcontainer = this.getCluster().getContainer();
        if (!(hcontainer instanceof Host)) {
            log.error((Object)sm.getString("farmWarDeployer.hostOnly"));
            return;
        }
        this.host = (Host)hcontainer;
        Container econtainer = this.host.getParent();
        if (!(econtainer instanceof Engine)) {
            log.error((Object)sm.getString("farmWarDeployer.hostParentEngine", new Object[]{this.host.getName()}));
            return;
        }
        Engine engine = (Engine)econtainer;
        String hostname = null;
        hostname = this.host.getName();
        try {
            this.oname = new ObjectName(engine.getName() + ":type=Deployer,host=" + hostname);
        }
        catch (Exception e) {
            log.error((Object)sm.getString("farmWarDeployer.mbeanNameFail", new Object[]{engine.getName(), hostname}), (Throwable)e);
            return;
        }
        if (this.watchEnabled) {
            this.watcher = new WarWatcher(this, this.getWatchDirFile());
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("farmWarDeployer.watchDir", new Object[]{this.getWatchDir()}));
            }
        }
        this.configBase = this.host.getConfigBaseFile();
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        this.started = true;
        this.count = 0;
        this.getCluster().addClusterListener(this);
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("farmWarDeployer.started"));
        }
    }

    @Override
    public void stop() throws LifecycleException {
        this.started = false;
        this.getCluster().removeClusterListener(this);
        this.count = 0;
        if (this.watcher != null) {
            this.watcher.clear();
            this.watcher = null;
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("farmWarDeployer.stopped"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void messageReceived(ClusterMessage msg) {
        block24: {
            try {
                if (msg instanceof FileMessage) {
                    FileMessageFactory factory;
                    FileMessage fmsg = (FileMessage)msg;
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("farmWarDeployer.msgRxDeploy", new Object[]{fmsg.getContextName(), fmsg.getFileName()}));
                    }
                    if (!(factory = this.getFactory(fmsg)).writeMessage(fmsg)) break block24;
                    String name = factory.getFile().getName();
                    if (!name.endsWith(".war")) {
                        name = name + ".war";
                    }
                    File deployable = new File(this.getDeployDirFile(), name);
                    try {
                        String contextName = fmsg.getContextName();
                        if (this.tryAddServiced(contextName)) {
                            try {
                                this.remove(contextName);
                                if (!factory.getFile().renameTo(deployable)) {
                                    log.error((Object)sm.getString("farmWarDeployer.renameFail", new Object[]{factory.getFile(), deployable}));
                                }
                            }
                            finally {
                                this.removeServiced(contextName);
                            }
                            this.check(contextName);
                            if (log.isDebugEnabled()) {
                                log.debug((Object)sm.getString("farmWarDeployer.deployEnd", new Object[]{contextName}));
                            }
                            break block24;
                        }
                        log.error((Object)sm.getString("farmWarDeployer.servicingDeploy", new Object[]{contextName, name}));
                        break block24;
                    }
                    catch (Exception ex) {
                        log.error((Object)sm.getString("farmWarDeployer.fileMessageError"), (Throwable)ex);
                        break block24;
                    }
                    finally {
                        this.removeFactory(fmsg);
                    }
                }
                if (!(msg instanceof UndeployMessage)) break block24;
                try {
                    UndeployMessage umsg = (UndeployMessage)msg;
                    String contextName = umsg.getContextName();
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("farmWarDeployer.msgRxUndeploy", new Object[]{contextName}));
                    }
                    if (this.tryAddServiced(contextName)) {
                        try {
                            this.remove(contextName);
                        }
                        finally {
                            this.removeServiced(contextName);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug((Object)sm.getString("farmWarDeployer.undeployEnd", new Object[]{contextName}));
                        }
                        break block24;
                    }
                    log.error((Object)sm.getString("farmWarDeployer.servicingUndeploy", new Object[]{contextName}));
                }
                catch (Exception ex) {
                    log.error((Object)sm.getString("farmWarDeployer.undeployMessageError"), (Throwable)ex);
                }
            }
            catch (IOException x) {
                log.error((Object)sm.getString("farmWarDeployer.msgIoe"), (Throwable)x);
            }
        }
    }

    public synchronized FileMessageFactory getFactory(FileMessage msg) throws FileNotFoundException, IOException {
        File writeToFile = new File(this.getTempDirFile(), msg.getFileName());
        FileMessageFactory factory = this.fileFactories.get(msg.getFileName());
        if (factory == null) {
            factory = FileMessageFactory.getInstance(writeToFile, true);
            factory.setMaxValidTime(this.maxValidTime);
            this.fileFactories.put(msg.getFileName(), factory);
        }
        return factory;
    }

    public void removeFactory(FileMessage msg) {
        this.fileFactories.remove(msg.getFileName());
    }

    @Override
    public boolean accept(ClusterMessage msg) {
        return msg instanceof FileMessage || msg instanceof UndeployMessage;
    }

    @Override
    public void install(String contextName, File webapp) throws IOException {
        Member[] members = this.getCluster().getMembers();
        if (members.length == 0) {
            return;
        }
        Member localMember = this.getCluster().getLocalMember();
        FileMessageFactory factory = FileMessageFactory.getInstance(webapp, false);
        FileMessage msg = new FileMessage(localMember, webapp.getName(), contextName);
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("farmWarDeployer.sendStart", new Object[]{contextName, webapp}));
        }
        msg = factory.readMessage(msg);
        while (msg != null) {
            for (Member member : members) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("farmWarDeployer.sendFragment", new Object[]{contextName, webapp, member}));
                }
                this.getCluster().send(msg, member);
            }
            msg = factory.readMessage(msg);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("farmWarDeployer.sendEnd", new Object[]{contextName, webapp}));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(String contextName, boolean undeploy) throws IOException {
        block11: {
            if (this.getCluster().getMembers().length > 0) {
                if (log.isInfoEnabled()) {
                    log.info((Object)sm.getString("farmWarDeployer.removeStart", new Object[]{contextName}));
                }
                Member localMember = this.getCluster().getLocalMember();
                UndeployMessage msg = new UndeployMessage(localMember, System.currentTimeMillis(), "Undeploy:" + contextName + ":" + System.currentTimeMillis(), contextName);
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("farmWarDeployer.removeTxMsg", new Object[]{contextName}));
                }
                this.cluster.send(msg);
            }
            if (undeploy) {
                try {
                    if (this.tryAddServiced(contextName)) {
                        try {
                            this.remove(contextName);
                        }
                        finally {
                            this.removeServiced(contextName);
                        }
                        this.check(contextName);
                        break block11;
                    }
                    log.error((Object)sm.getString("farmWarDeployer.removeFailRemote", new Object[]{contextName}));
                }
                catch (Exception ex) {
                    log.error((Object)sm.getString("farmWarDeployer.removeFailLocal", new Object[]{contextName}), (Throwable)ex);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void fileModified(File newWar) {
        try {
            File deployWar = new File(this.getDeployDirFile(), newWar.getName());
            ContextName cn = new ContextName(deployWar.getName(), true);
            if (deployWar.exists() && deployWar.lastModified() > newWar.lastModified()) {
                if (log.isInfoEnabled()) {
                    log.info((Object)sm.getString("farmWarDeployer.alreadyDeployed", new Object[]{cn.getName()}));
                }
                return;
            }
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("farmWarDeployer.modInstall", new Object[]{cn.getName(), deployWar.getAbsolutePath()}));
            }
            if (this.tryAddServiced(cn.getName())) {
                try {
                    this.copy(newWar, deployWar);
                }
                finally {
                    this.removeServiced(cn.getName());
                }
                this.check(cn.getName());
            } else {
                log.error((Object)sm.getString("farmWarDeployer.servicingDeploy", new Object[]{cn.getName(), deployWar.getName()}));
            }
            this.install(cn.getName(), deployWar);
        }
        catch (Exception x) {
            log.error((Object)sm.getString("farmWarDeployer.modInstallFail"), (Throwable)x);
        }
    }

    @Override
    public void fileRemoved(File removeWar) {
        try {
            ContextName cn = new ContextName(removeWar.getName(), true);
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("farmWarDeployer.removeLocal", new Object[]{cn.getName()}));
            }
            this.remove(cn.getName(), true);
        }
        catch (Exception x) {
            log.error((Object)sm.getString("farmWarDeployer.removeLocalFail"), (Throwable)x);
        }
    }

    protected void remove(String contextName) throws Exception {
        Context context = (Context)this.host.findChild(contextName);
        if (context != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("farmWarDeployer.undeployLocal", new Object[]{contextName}));
            }
            context.stop();
            String baseName = context.getBaseName();
            File war = new File(this.host.getAppBaseFile(), baseName + ".war");
            File dir = new File(this.host.getAppBaseFile(), baseName);
            File xml = new File(this.configBase, baseName + ".xml");
            if (war.exists()) {
                if (!war.delete()) {
                    log.error((Object)sm.getString("farmWarDeployer.deleteFail", new Object[]{war}));
                }
            } else if (dir.exists()) {
                this.undeployDir(dir);
            } else if (!xml.delete()) {
                log.error((Object)sm.getString("farmWarDeployer.deleteFail", new Object[]{xml}));
            }
        }
    }

    protected void undeployDir(File dir) {
        String[] files = dir.list();
        if (files == null) {
            files = new String[]{};
        }
        for (String s : files) {
            File file = new File(dir, s);
            if (file.isDirectory()) {
                this.undeployDir(file);
                continue;
            }
            if (file.delete()) continue;
            log.error((Object)sm.getString("farmWarDeployer.deleteFail", new Object[]{file}));
        }
        if (!dir.delete()) {
            log.error((Object)sm.getString("farmWarDeployer.deleteFail", new Object[]{dir}));
        }
    }

    @Override
    public void backgroundProcess() {
        if (this.started) {
            if (this.watchEnabled) {
                this.count = (this.count + 1) % this.processDeployFrequency;
                if (this.count == 0) {
                    this.watcher.check();
                }
            }
            this.removeInvalidFileFactories();
        }
    }

    protected void check(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        this.mBeanServer.invoke(this.oname, "check", params, signature);
    }

    @Deprecated
    protected boolean isServiced(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "isServiced", params, signature);
        return result;
    }

    @Deprecated
    protected void addServiced(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        this.mBeanServer.invoke(this.oname, "addServiced", params, signature);
    }

    protected boolean tryAddServiced(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        Boolean result = (Boolean)this.mBeanServer.invoke(this.oname, "tryAddServiced", params, signature);
        return result;
    }

    protected void removeServiced(String name) throws Exception {
        Object[] params = new String[]{name};
        String[] signature = new String[]{"java.lang.String"};
        this.mBeanServer.invoke(this.oname, "removeServiced", params, signature);
    }

    public boolean equals(Object listener) {
        return super.equals(listener);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String getDeployDir() {
        return this.deployDir;
    }

    public File getDeployDirFile() {
        File dir;
        if (this.deployDirFile != null) {
            return this.deployDirFile;
        }
        this.deployDirFile = dir = this.getAbsolutePath(this.getDeployDir());
        return dir;
    }

    public void setDeployDir(String deployDir) {
        this.deployDir = deployDir;
    }

    public String getTempDir() {
        return this.tempDir;
    }

    public File getTempDirFile() {
        File dir;
        if (this.tempDirFile != null) {
            return this.tempDirFile;
        }
        this.tempDirFile = dir = this.getAbsolutePath(this.getTempDir());
        return dir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String getWatchDir() {
        return this.watchDir;
    }

    public File getWatchDirFile() {
        File dir;
        if (this.watchDirFile != null) {
            return this.watchDirFile;
        }
        this.watchDirFile = dir = this.getAbsolutePath(this.getWatchDir());
        return dir;
    }

    public void setWatchDir(String watchDir) {
        this.watchDir = watchDir;
    }

    public boolean isWatchEnabled() {
        return this.watchEnabled;
    }

    public boolean getWatchEnabled() {
        return this.watchEnabled;
    }

    public void setWatchEnabled(boolean watchEnabled) {
        this.watchEnabled = watchEnabled;
    }

    public int getProcessDeployFrequency() {
        return this.processDeployFrequency;
    }

    public void setProcessDeployFrequency(int processExpiresFrequency) {
        if (processExpiresFrequency <= 0) {
            return;
        }
        this.processDeployFrequency = processExpiresFrequency;
    }

    public int getMaxValidTime() {
        return this.maxValidTime;
    }

    public void setMaxValidTime(int maxValidTime) {
        this.maxValidTime = maxValidTime;
    }

    protected boolean copy(File from, File to) {
        try {
            if (!to.exists() && !to.createNewFile()) {
                log.error((Object)sm.getString("fileNewFail", new Object[]{to}));
                return false;
            }
        }
        catch (IOException e) {
            log.error((Object)sm.getString("farmWarDeployer.fileCopyFail", new Object[]{from, to}), (Throwable)e);
            return false;
        }
        try (FileInputStream is = new FileInputStream(from);
             FileOutputStream os = new FileOutputStream(to, false);){
            int len;
            byte[] buf = new byte[4096];
            while ((len = is.read(buf)) >= 0) {
                os.write(buf, 0, len);
            }
        }
        catch (IOException e) {
            log.error((Object)sm.getString("farmWarDeployer.fileCopyFail", new Object[]{from, to}), (Throwable)e);
            return false;
        }
        return true;
    }

    protected void removeInvalidFileFactories() {
        String[] fileNames;
        for (String fileName : fileNames = this.fileFactories.keySet().toArray(new String[0])) {
            FileMessageFactory factory = this.fileFactories.get(fileName);
            if (factory.isValid()) continue;
            this.fileFactories.remove(fileName);
        }
    }

    private File getAbsolutePath(String path) {
        File dir = new File(path);
        if (!dir.isAbsolute()) {
            dir = new File(this.getCluster().getContainer().getCatalinaBase(), dir.getPath());
        }
        try {
            dir = dir.getCanonicalFile();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return dir;
    }
}

