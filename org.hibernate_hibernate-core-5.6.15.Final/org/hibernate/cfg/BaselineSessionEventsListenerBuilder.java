/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.SessionEventListener;
import org.hibernate.engine.internal.StatisticalLoggingSessionEventListener;

public class BaselineSessionEventsListenerBuilder {
    private static final SessionEventListener[] EMPTY = new SessionEventListener[0];
    private boolean logSessionMetrics;
    private Class<? extends SessionEventListener> autoListener;

    public BaselineSessionEventsListenerBuilder(boolean logSessionMetrics, Class<? extends SessionEventListener> autoListener) {
        this.logSessionMetrics = logSessionMetrics;
        this.autoListener = autoListener;
    }

    public boolean isLogSessionMetrics() {
        return this.logSessionMetrics;
    }

    @Deprecated
    public void setLogSessionMetrics(boolean logSessionMetrics) {
        this.logSessionMetrics = logSessionMetrics;
    }

    public Class<? extends SessionEventListener> getAutoListener() {
        return this.autoListener;
    }

    @Deprecated
    public void setAutoListener(Class<? extends SessionEventListener> autoListener) {
        this.autoListener = autoListener;
    }

    public List<SessionEventListener> buildBaselineList() {
        SessionEventListener[] sessionEventListeners = this.buildBaseline();
        ArrayList<SessionEventListener> list = new ArrayList<SessionEventListener>(sessionEventListeners.length + 3);
        Collections.addAll(list, sessionEventListeners);
        return list;
    }

    public SessionEventListener[] buildBaseline() {
        boolean addAutoListener;
        boolean addStats = this.logSessionMetrics && StatisticalLoggingSessionEventListener.isLoggingEnabled();
        boolean bl = addAutoListener = this.autoListener != null;
        SessionEventListener[] arr = addStats && addAutoListener ? new SessionEventListener[]{BaselineSessionEventsListenerBuilder.buildStatsListener(), BaselineSessionEventsListenerBuilder.buildAutoListener(this.autoListener)} : (!addStats && !addAutoListener ? EMPTY : (!addStats && addAutoListener ? new SessionEventListener[]{BaselineSessionEventsListenerBuilder.buildAutoListener(this.autoListener)} : new SessionEventListener[]{BaselineSessionEventsListenerBuilder.buildStatsListener()}));
        return arr;
    }

    private static SessionEventListener buildAutoListener(Class<? extends SessionEventListener> autoListener) {
        try {
            return autoListener.newInstance();
        }
        catch (Exception e) {
            throw new HibernateException("Unable to instantiate specified auto SessionEventListener : " + autoListener.getName(), e);
        }
    }

    private static SessionEventListener buildStatsListener() {
        return new StatisticalLoggingSessionEventListener();
    }
}

