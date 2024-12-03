/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  commonj.timers.TimerManager
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.Lifecycle
 *  org.springframework.jndi.JndiLocatorSupport
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.scheduling.commonj;

import commonj.timers.TimerManager;
import javax.naming.NamingException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public abstract class TimerManagerAccessor
extends JndiLocatorSupport
implements InitializingBean,
DisposableBean,
Lifecycle {
    @Nullable
    private TimerManager timerManager;
    @Nullable
    private String timerManagerName;
    private boolean shared = false;

    public void setTimerManager(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    public void setTimerManagerName(String timerManagerName) {
        this.timerManagerName = timerManagerName;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public void afterPropertiesSet() throws NamingException {
        if (this.timerManager == null) {
            if (this.timerManagerName == null) {
                throw new IllegalArgumentException("Either 'timerManager' or 'timerManagerName' must be specified");
            }
            this.timerManager = (TimerManager)this.lookup(this.timerManagerName, TimerManager.class);
        }
    }

    @Nullable
    protected final TimerManager getTimerManager() {
        return this.timerManager;
    }

    protected TimerManager obtainTimerManager() {
        Assert.notNull((Object)this.timerManager, (String)"No TimerManager set");
        return this.timerManager;
    }

    public void start() {
        if (!this.shared) {
            this.obtainTimerManager().resume();
        }
    }

    public void stop() {
        if (!this.shared) {
            this.obtainTimerManager().suspend();
        }
    }

    public boolean isRunning() {
        TimerManager tm = this.obtainTimerManager();
        return !tm.isSuspending() && !tm.isStopping();
    }

    public void destroy() {
        if (this.timerManager != null && !this.shared) {
            this.timerManager.stop();
        }
    }
}

