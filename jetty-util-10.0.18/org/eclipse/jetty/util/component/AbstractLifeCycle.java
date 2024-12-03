/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.component;

import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Uptime;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="Abstract Implementation of LifeCycle")
public abstract class AbstractLifeCycle
implements LifeCycle {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLifeCycle.class);
    public static final String STOPPED = State.STOPPED.toString();
    public static final String FAILED = State.FAILED.toString();
    public static final String STARTING = State.STARTING.toString();
    public static final String STARTED = State.STARTED.toString();
    public static final String STOPPING = State.STOPPING.toString();
    private final List<EventListener> _eventListener = new CopyOnWriteArrayList<EventListener>();
    private final AutoLock _lock = new AutoLock();
    private volatile State _state = State.STOPPED;

    protected void doStart() throws Exception {
    }

    protected void doStop() throws Exception {
    }

    /*
     * Exception decompiling
     */
    @Override
    public final void start() throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Exception decompiling
     */
    @Override
    public final void stop() throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public boolean isRunning() {
        State state = this._state;
        switch (state) {
            case STARTED: 
            case STARTING: {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isStarted() {
        return this._state == State.STARTED;
    }

    @Override
    public boolean isStarting() {
        return this._state == State.STARTING;
    }

    @Override
    public boolean isStopping() {
        return this._state == State.STOPPING;
    }

    @Override
    public boolean isStopped() {
        return this._state == State.STOPPED;
    }

    @Override
    public boolean isFailed() {
        return this._state == State.FAILED;
    }

    public List<EventListener> getEventListeners() {
        return this._eventListener;
    }

    public void setEventListeners(Collection<EventListener> eventListeners) {
        for (EventListener l : this._eventListener) {
            if (eventListeners.contains(l)) continue;
            this.removeEventListener(l);
        }
        for (EventListener l : eventListeners) {
            if (this._eventListener.contains(l)) continue;
            this.addEventListener(l);
        }
    }

    @Override
    public boolean addEventListener(EventListener listener) {
        if (this._eventListener.contains(listener)) {
            return false;
        }
        this._eventListener.add(listener);
        return true;
    }

    @Override
    public boolean removeEventListener(EventListener listener) {
        return this._eventListener.remove(listener);
    }

    @ManagedAttribute(value="Lifecycle State for this instance", readonly=true)
    public String getState() {
        return this._state.toString();
    }

    public static String getState(LifeCycle lc) {
        if (lc instanceof AbstractLifeCycle) {
            return ((AbstractLifeCycle)lc)._state.toString();
        }
        if (lc.isStarting()) {
            return State.STARTING.toString();
        }
        if (lc.isStarted()) {
            return State.STARTED.toString();
        }
        if (lc.isStopping()) {
            return State.STOPPING.toString();
        }
        if (lc.isStopped()) {
            return State.STOPPED.toString();
        }
        return State.FAILED.toString();
    }

    private void setStarted() {
        if (this._state == State.STARTING) {
            this._state = State.STARTED;
            if (LOG.isDebugEnabled()) {
                LOG.debug("STARTED @{}ms {}", (Object)Uptime.getUptime(), (Object)this);
            }
            for (EventListener listener : this._eventListener) {
                if (!(listener instanceof LifeCycle.Listener)) continue;
                ((LifeCycle.Listener)listener).lifeCycleStarted(this);
            }
        }
    }

    private void setStarting() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("STARTING {}", (Object)this);
        }
        this._state = State.STARTING;
        for (EventListener listener : this._eventListener) {
            if (!(listener instanceof LifeCycle.Listener)) continue;
            ((LifeCycle.Listener)listener).lifeCycleStarting(this);
        }
    }

    private void setStopping() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("STOPPING {}", (Object)this);
        }
        this._state = State.STOPPING;
        for (EventListener listener : this._eventListener) {
            if (!(listener instanceof LifeCycle.Listener)) continue;
            ((LifeCycle.Listener)listener).lifeCycleStopping(this);
        }
    }

    private void setStopped() {
        if (this._state == State.STOPPING) {
            this._state = State.STOPPED;
            if (LOG.isDebugEnabled()) {
                LOG.debug("STOPPED {}", (Object)this);
            }
            for (EventListener listener : this._eventListener) {
                if (!(listener instanceof LifeCycle.Listener)) continue;
                ((LifeCycle.Listener)listener).lifeCycleStopped(this);
            }
        }
    }

    private void setFailed(Throwable th) {
        this._state = State.FAILED;
        if (LOG.isDebugEnabled()) {
            LOG.warn("FAILED {}: {}", new Object[]{this, th, th});
        }
        for (EventListener listener : this._eventListener) {
            if (!(listener instanceof LifeCycle.Listener)) continue;
            ((LifeCycle.Listener)listener).lifeCycleFailure(this, th);
        }
    }

    public String toString() {
        String name = this.getClass().getSimpleName();
        if (StringUtil.isBlank(name) && this.getClass().getSuperclass() != null) {
            name = this.getClass().getSuperclass().getSimpleName();
        }
        return String.format("%s@%x{%s}", name, this.hashCode(), this.getState());
    }

    static enum State {
        STOPPED,
        STARTING,
        STARTED,
        STOPPING,
        FAILED;

    }

    public class StopException
    extends RuntimeException {
    }

    @Deprecated
    public static abstract class AbstractLifeCycleListener
    implements LifeCycle.Listener {
        @Override
        public void lifeCycleFailure(LifeCycle event, Throwable cause) {
        }

        @Override
        public void lifeCycleStarted(LifeCycle event) {
        }

        @Override
        public void lifeCycleStarting(LifeCycle event) {
        }

        @Override
        public void lifeCycleStopped(LifeCycle event) {
        }

        @Override
        public void lifeCycleStopping(LifeCycle event) {
        }
    }
}

