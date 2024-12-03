/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.session;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Manager;
import org.apache.catalina.Store;
import org.apache.catalina.session.ManagerBase;
import org.apache.catalina.session.PersistentManagerBase;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.catalina.util.LifecycleBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.tomcat.util.res.StringManager;

public abstract class StoreBase
extends LifecycleBase
implements Store {
    protected static final String storeName = "StoreBase";
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    protected static final StringManager sm = StringManager.getManager(StoreBase.class);
    protected Manager manager;

    public String getStoreName() {
        return storeName;
    }

    @Override
    public void setManager(Manager manager) {
        Manager oldManager = this.manager;
        this.manager = manager;
        this.support.firePropertyChange("manager", oldManager, this.manager);
    }

    @Override
    public Manager getManager() {
        return this.manager;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String[] expiredKeys() throws IOException {
        return this.keys();
    }

    public void processExpires() {
        String[] keys = null;
        if (!this.getState().isAvailable()) {
            return;
        }
        try {
            keys = this.expiredKeys();
        }
        catch (IOException e) {
            this.manager.getContext().getLogger().error((Object)"Error getting keys", (Throwable)e);
            return;
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug((Object)(this.getStoreName() + ": processExpires check number of " + keys.length + " sessions"));
        }
        long timeNow = System.currentTimeMillis();
        for (String key : keys) {
            try {
                int timeIdle;
                StandardSession session = (StandardSession)this.load(key);
                if (session == null || (timeIdle = (int)((timeNow - session.getThisAccessedTime()) / 1000L)) < session.getMaxInactiveInterval()) continue;
                if (this.manager.getContext().getLogger().isDebugEnabled()) {
                    this.manager.getContext().getLogger().debug((Object)(this.getStoreName() + ": processExpires expire store session " + key));
                }
                boolean isLoaded = false;
                if (this.manager instanceof PersistentManagerBase) {
                    isLoaded = ((PersistentManagerBase)this.manager).isLoaded(key);
                } else {
                    try {
                        if (this.manager.findSession(key) != null) {
                            isLoaded = true;
                        }
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
                if (isLoaded) {
                    session.recycle();
                } else {
                    session.expire();
                }
                this.remove(key);
            }
            catch (Exception e) {
                this.manager.getContext().getLogger().error((Object)("Session: " + key + "; "), (Throwable)e);
                try {
                    this.remove(key);
                }
                catch (IOException e2) {
                    this.manager.getContext().getLogger().error((Object)"Error removing key", (Throwable)e2);
                }
            }
        }
    }

    protected ObjectInputStream getObjectInputStream(InputStream is) throws IOException {
        CustomObjectInputStream ois;
        BufferedInputStream bis = new BufferedInputStream(is);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (this.manager instanceof ManagerBase) {
            ManagerBase managerBase = (ManagerBase)this.manager;
            ois = new CustomObjectInputStream(bis, classLoader, this.manager.getContext().getLogger(), managerBase.getSessionAttributeValueClassNamePattern(), managerBase.getWarnOnSessionAttributeFilterFailure());
        } else {
            ois = new CustomObjectInputStream(bis, classLoader);
        }
        return ois;
    }

    @Override
    protected void initInternal() {
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        this.setState(LifecycleState.STARTING);
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        this.setState(LifecycleState.STOPPING);
    }

    @Override
    protected void destroyInternal() {
    }

    public String toString() {
        return ToStringUtil.toString((Object)this, this.manager);
    }
}

