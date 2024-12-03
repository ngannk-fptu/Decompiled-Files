/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public abstract class LifecycleBase
implements Lifecycle {
    private static final Log log = LogFactory.getLog(LifecycleBase.class);
    private static final StringManager sm = StringManager.getManager(LifecycleBase.class);
    private final List<LifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<LifecycleListener>();
    private volatile LifecycleState state = LifecycleState.NEW;
    private boolean throwOnFailure = true;

    public boolean getThrowOnFailure() {
        return this.throwOnFailure;
    }

    public void setThrowOnFailure(boolean throwOnFailure) {
        this.throwOnFailure = throwOnFailure;
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        this.lifecycleListeners.add(listener);
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return this.lifecycleListeners.toArray(new LifecycleListener[0]);
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        this.lifecycleListeners.remove(listener);
    }

    protected void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(this, type, data);
        for (LifecycleListener listener : this.lifecycleListeners) {
            listener.lifecycleEvent(event);
        }
    }

    @Override
    public final synchronized void init() throws LifecycleException {
        if (!this.state.equals((Object)LifecycleState.NEW)) {
            this.invalidTransition("before_init");
        }
        try {
            this.setStateInternal(LifecycleState.INITIALIZING, null, false);
            this.initInternal();
            this.setStateInternal(LifecycleState.INITIALIZED, null, false);
        }
        catch (Throwable t) {
            this.handleSubClassException(t, "lifecycleBase.initFail", this.toString());
        }
    }

    protected abstract void initInternal() throws LifecycleException;

    @Override
    public final synchronized void start() throws LifecycleException {
        if (LifecycleState.STARTING_PREP.equals((Object)this.state) || LifecycleState.STARTING.equals((Object)this.state) || LifecycleState.STARTED.equals((Object)this.state)) {
            if (log.isDebugEnabled()) {
                LifecycleException e = new LifecycleException();
                log.debug((Object)sm.getString("lifecycleBase.alreadyStarted", new Object[]{this.toString()}), (Throwable)e);
            } else if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("lifecycleBase.alreadyStarted", new Object[]{this.toString()}));
            }
            return;
        }
        if (this.state.equals((Object)LifecycleState.NEW)) {
            this.init();
        } else if (this.state.equals((Object)LifecycleState.FAILED)) {
            this.stop();
        } else if (!this.state.equals((Object)LifecycleState.INITIALIZED) && !this.state.equals((Object)LifecycleState.STOPPED)) {
            this.invalidTransition("before_start");
        }
        try {
            this.setStateInternal(LifecycleState.STARTING_PREP, null, false);
            this.startInternal();
            if (this.state.equals((Object)LifecycleState.FAILED)) {
                this.stop();
            } else if (!this.state.equals((Object)LifecycleState.STARTING)) {
                this.invalidTransition("after_start");
            } else {
                this.setStateInternal(LifecycleState.STARTED, null, false);
            }
        }
        catch (Throwable t) {
            this.handleSubClassException(t, "lifecycleBase.startFail", this.toString());
        }
    }

    protected abstract void startInternal() throws LifecycleException;

    @Override
    public final synchronized void stop() throws LifecycleException {
        if (LifecycleState.STOPPING_PREP.equals((Object)this.state) || LifecycleState.STOPPING.equals((Object)this.state) || LifecycleState.STOPPED.equals((Object)this.state)) {
            if (log.isDebugEnabled()) {
                LifecycleException e = new LifecycleException();
                log.debug((Object)sm.getString("lifecycleBase.alreadyStopped", new Object[]{this.toString()}), (Throwable)e);
            } else if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("lifecycleBase.alreadyStopped", new Object[]{this.toString()}));
            }
            return;
        }
        if (this.state.equals((Object)LifecycleState.NEW)) {
            this.state = LifecycleState.STOPPED;
            return;
        }
        if (!this.state.equals((Object)LifecycleState.STARTED) && !this.state.equals((Object)LifecycleState.FAILED)) {
            this.invalidTransition("before_stop");
        }
        try {
            if (this.state.equals((Object)LifecycleState.FAILED)) {
                this.fireLifecycleEvent("before_stop", null);
            } else {
                this.setStateInternal(LifecycleState.STOPPING_PREP, null, false);
            }
            this.stopInternal();
            if (!this.state.equals((Object)LifecycleState.STOPPING) && !this.state.equals((Object)LifecycleState.FAILED)) {
                this.invalidTransition("after_stop");
            }
            this.setStateInternal(LifecycleState.STOPPED, null, false);
        }
        catch (Throwable t) {
            this.handleSubClassException(t, "lifecycleBase.stopFail", this.toString());
        }
        finally {
            if (this instanceof Lifecycle.SingleUse) {
                this.setStateInternal(LifecycleState.STOPPED, null, false);
                this.destroy();
            }
        }
    }

    protected abstract void stopInternal() throws LifecycleException;

    @Override
    public final synchronized void destroy() throws LifecycleException {
        if (LifecycleState.FAILED.equals((Object)this.state)) {
            try {
                this.stop();
            }
            catch (LifecycleException e) {
                log.error((Object)sm.getString("lifecycleBase.destroyStopFail", new Object[]{this.toString()}), (Throwable)e);
            }
        }
        if (LifecycleState.DESTROYING.equals((Object)this.state) || LifecycleState.DESTROYED.equals((Object)this.state)) {
            if (log.isDebugEnabled()) {
                LifecycleException e = new LifecycleException();
                log.debug((Object)sm.getString("lifecycleBase.alreadyDestroyed", new Object[]{this.toString()}), (Throwable)e);
            } else if (log.isInfoEnabled() && !(this instanceof Lifecycle.SingleUse)) {
                log.info((Object)sm.getString("lifecycleBase.alreadyDestroyed", new Object[]{this.toString()}));
            }
            return;
        }
        if (!(this.state.equals((Object)LifecycleState.STOPPED) || this.state.equals((Object)LifecycleState.FAILED) || this.state.equals((Object)LifecycleState.NEW) || this.state.equals((Object)LifecycleState.INITIALIZED))) {
            this.invalidTransition("before_destroy");
        }
        try {
            this.setStateInternal(LifecycleState.DESTROYING, null, false);
            this.destroyInternal();
            this.setStateInternal(LifecycleState.DESTROYED, null, false);
        }
        catch (Throwable t) {
            this.handleSubClassException(t, "lifecycleBase.destroyFail", this.toString());
        }
    }

    protected abstract void destroyInternal() throws LifecycleException;

    @Override
    public LifecycleState getState() {
        return this.state;
    }

    @Override
    public String getStateName() {
        return this.getState().toString();
    }

    protected synchronized void setState(LifecycleState state) throws LifecycleException {
        this.setStateInternal(state, null, true);
    }

    protected synchronized void setState(LifecycleState state, Object data) throws LifecycleException {
        this.setStateInternal(state, data, true);
    }

    private synchronized void setStateInternal(LifecycleState state, Object data, boolean check) throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("lifecycleBase.setState", new Object[]{this, state}));
        }
        if (check) {
            if (state == null) {
                this.invalidTransition("null");
                return;
            }
            if (!(state == LifecycleState.FAILED || this.state == LifecycleState.STARTING_PREP && state == LifecycleState.STARTING || this.state == LifecycleState.STOPPING_PREP && state == LifecycleState.STOPPING || this.state == LifecycleState.FAILED && state == LifecycleState.STOPPING)) {
                this.invalidTransition(state.name());
            }
        }
        this.state = state;
        String lifecycleEvent = state.getLifecycleEvent();
        if (lifecycleEvent != null) {
            this.fireLifecycleEvent(lifecycleEvent, data);
        }
    }

    private void invalidTransition(String type) throws LifecycleException {
        String msg = sm.getString("lifecycleBase.invalidTransition", new Object[]{type, this.toString(), this.state});
        throw new LifecycleException(msg);
    }

    private void handleSubClassException(Throwable t, String key, Object ... args) throws LifecycleException {
        this.setStateInternal(LifecycleState.FAILED, null, false);
        ExceptionUtils.handleThrowable((Throwable)t);
        String msg = sm.getString(key, args);
        if (this.getThrowOnFailure()) {
            if (!(t instanceof LifecycleException)) {
                t = new LifecycleException(msg, t);
            }
            throw (LifecycleException)t;
        }
        log.error((Object)msg, t);
    }
}

