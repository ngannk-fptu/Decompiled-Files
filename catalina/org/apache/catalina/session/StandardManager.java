/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 */
package org.apache.catalina.session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.Session;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;

public class StandardManager
extends ManagerBase {
    private final Log log = LogFactory.getLog(StandardManager.class);
    protected static final String name = "StandardManager";
    protected String pathname = "SESSIONS.ser";

    @Override
    public String getName() {
        return name;
    }

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        String oldPathname = this.pathname;
        this.pathname = pathname;
        this.support.firePropertyChange("pathname", oldPathname, this.pathname);
    }

    @Override
    public void load() throws ClassNotFoundException, IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedDoLoad());
            }
            catch (PrivilegedActionException ex) {
                Exception exception = ex.getException();
                if (exception instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException)exception;
                }
                if (exception instanceof IOException) {
                    throw (IOException)exception;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Unreported exception in load() ", (Throwable)exception);
                }
            }
        } else {
            this.doLoad();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void doLoad() throws ClassNotFoundException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Start: Loading persisted sessions");
        }
        this.sessions.clear();
        File file = this.file();
        if (file == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("standardManager.loading", new Object[]{this.pathname}));
        }
        Loader loader = null;
        ClassLoader classLoader = null;
        Log logger = null;
        try (FileInputStream fis = new FileInputStream(file.getAbsolutePath());
             BufferedInputStream bis = new BufferedInputStream(fis);){
            Context c = this.getContext();
            loader = c.getLoader();
            logger = c.getLogger();
            if (loader != null) {
                classLoader = loader.getClassLoader();
            }
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }
            Map map = this.sessions;
            synchronized (map) {
                block32: {
                    try {
                        try (CustomObjectInputStream ois = new CustomObjectInputStream(bis, classLoader, logger, this.getSessionAttributeValueClassNamePattern(), this.getWarnOnSessionAttributeFilterFailure());){
                            Integer count = (Integer)ois.readObject();
                            int n = count;
                            if (this.log.isDebugEnabled()) {
                                this.log.debug((Object)("Loading " + n + " persisted sessions"));
                            }
                            for (int i = 0; i < n; ++this.sessionCounter, ++i) {
                                StandardSession session = this.getNewSession();
                                session.readObjectData(ois);
                                session.setManager(this);
                                this.sessions.put(session.getIdInternal(), session);
                                session.activate();
                                if (session.isValidInternal()) continue;
                                session.setValid(true);
                                session.expire();
                            }
                        }
                        if (!file.exists() || file.delete()) break block32;
                    }
                    catch (Throwable throwable) {
                        if (file.exists() && !file.delete()) {
                            this.log.warn((Object)sm.getString("standardManager.deletePersistedFileFail", new Object[]{file}));
                        }
                        throw throwable;
                    }
                    this.log.warn((Object)sm.getString("standardManager.deletePersistedFileFail", new Object[]{file}));
                }
            }
        }
        catch (FileNotFoundException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)"No persisted data file found");
            }
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Finish: Loading persisted sessions");
        }
    }

    @Override
    public void unload() throws IOException {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                AccessController.doPrivileged(new PrivilegedDoUnload());
            }
            catch (PrivilegedActionException ex) {
                Exception exception = ex.getException();
                if (exception instanceof IOException) {
                    throw (IOException)exception;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)"Unreported exception in unLoad()", (Throwable)exception);
                }
            }
        } else {
            this.doUnload();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doUnload() throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("standardManager.unloading.debug"));
        }
        if (this.sessions.isEmpty()) {
            this.log.debug((Object)sm.getString("standardManager.unloading.nosessions"));
            return;
        }
        File file = this.file();
        if (file == null) {
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)sm.getString("standardManager.unloading", new Object[]{this.pathname}));
        }
        ArrayList<StandardSession> list = new ArrayList<StandardSession>();
        try (FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos);){
            Map map = this.sessions;
            synchronized (map) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Unloading " + this.sessions.size() + " sessions"));
                }
                oos.writeObject(this.sessions.size());
                for (Session s : this.sessions.values()) {
                    StandardSession session = (StandardSession)s;
                    list.add(session);
                    session.passivate();
                    session.writeObjectData(oos);
                }
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Expiring " + list.size() + " persisted sessions"));
        }
        for (StandardSession session : list) {
            try {
                session.expire(false);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
            }
            finally {
                session.recycle();
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Unloading complete");
        }
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            this.load();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log.error((Object)sm.getString("standardManager.managerLoad"), t);
        }
        this.setState(LifecycleState.STARTING);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        Session[] sessions;
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)"Stopping");
        }
        this.setState(LifecycleState.STOPPING);
        try {
            this.unload();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.log.error((Object)sm.getString("standardManager.managerUnload"), t);
        }
        for (Session session : sessions = this.findSessions()) {
            try {
                if (!session.isValid()) continue;
                session.expire();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
            }
            finally {
                session.recycle();
            }
        }
        super.stopInternal();
    }

    protected File file() {
        Context context;
        ServletContext servletContext;
        File tempdir;
        if (this.pathname == null || this.pathname.length() == 0) {
            return null;
        }
        File file = new File(this.pathname);
        if (!file.isAbsolute() && (tempdir = (File)(servletContext = (context = this.getContext()).getServletContext()).getAttribute("javax.servlet.context.tempdir")) != null) {
            file = new File(tempdir, this.pathname);
        }
        return file;
    }

    private class PrivilegedDoLoad
    implements PrivilegedExceptionAction<Void> {
        PrivilegedDoLoad() {
        }

        @Override
        public Void run() throws Exception {
            StandardManager.this.doLoad();
            return null;
        }
    }

    private class PrivilegedDoUnload
    implements PrivilegedExceptionAction<Void> {
        PrivilegedDoUnload() {
        }

        @Override
        public Void run() throws Exception {
            StandardManager.this.doUnload();
            return null;
        }
    }
}

