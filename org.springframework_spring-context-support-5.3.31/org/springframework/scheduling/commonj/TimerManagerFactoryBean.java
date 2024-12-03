/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  commonj.timers.Timer
 *  commonj.timers.TimerManager
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.Lifecycle
 *  org.springframework.lang.Nullable
 */
package org.springframework.scheduling.commonj;

import commonj.timers.Timer;
import commonj.timers.TimerManager;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.commonj.ScheduledTimerListener;
import org.springframework.scheduling.commonj.TimerManagerAccessor;

@Deprecated
public class TimerManagerFactoryBean
extends TimerManagerAccessor
implements FactoryBean<TimerManager>,
InitializingBean,
DisposableBean,
Lifecycle {
    @Nullable
    private ScheduledTimerListener[] scheduledTimerListeners;
    @Nullable
    private List<Timer> timers;

    public void setScheduledTimerListeners(ScheduledTimerListener[] scheduledTimerListeners) {
        this.scheduledTimerListeners = scheduledTimerListeners;
    }

    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.scheduledTimerListeners != null) {
            this.timers = new ArrayList<Timer>(this.scheduledTimerListeners.length);
            TimerManager timerManager = this.obtainTimerManager();
            for (ScheduledTimerListener scheduledTask : this.scheduledTimerListeners) {
                Timer timer = scheduledTask.isOneTimeTask() ? timerManager.schedule(scheduledTask.getTimerListener(), scheduledTask.getDelay()) : (scheduledTask.isFixedRate() ? timerManager.scheduleAtFixedRate(scheduledTask.getTimerListener(), scheduledTask.getDelay(), scheduledTask.getPeriod()) : timerManager.schedule(scheduledTask.getTimerListener(), scheduledTask.getDelay(), scheduledTask.getPeriod()));
                this.timers.add(timer);
            }
        }
    }

    @Nullable
    public TimerManager getObject() {
        return this.getTimerManager();
    }

    public Class<? extends TimerManager> getObjectType() {
        TimerManager timerManager = this.getTimerManager();
        return timerManager != null ? timerManager.getClass() : TimerManager.class;
    }

    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() {
        if (this.timers != null) {
            for (Timer timer : this.timers) {
                try {
                    timer.cancel();
                }
                catch (Throwable ex) {
                    this.logger.debug((Object)"Could not cancel CommonJ Timer", ex);
                }
            }
            this.timers.clear();
        }
        super.destroy();
    }
}

