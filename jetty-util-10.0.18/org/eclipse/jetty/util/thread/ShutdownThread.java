/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.thread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.PrivilegedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownThread
extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownThread.class);
    private static final ShutdownThread _thread = PrivilegedThreadFactory.newThread(ShutdownThread::new);
    private final AutoLock _lock = new AutoLock();
    private boolean _hooked;
    private final List<LifeCycle> _lifeCycles = new CopyOnWriteArrayList<LifeCycle>();

    private ShutdownThread() {
        super("JettyShutdownThread");
    }

    private void hook() {
        try (AutoLock l = this._lock.lock();){
            if (!this._hooked) {
                Runtime.getRuntime().addShutdownHook(this);
            }
            this._hooked = true;
        }
        catch (Exception e) {
            LOG.trace("IGNORED", (Throwable)e);
            LOG.info("shutdown already commenced");
        }
    }

    private void unhook() {
        try (AutoLock l = this._lock.lock();){
            this._hooked = false;
            Runtime.getRuntime().removeShutdownHook(this);
        }
        catch (Exception e) {
            LOG.trace("IGNORED", (Throwable)e);
            LOG.debug("shutdown already commenced");
        }
    }

    public static ShutdownThread getInstance() {
        return _thread;
    }

    public static void register(LifeCycle ... lifeCycles) {
        try (AutoLock l = ShutdownThread._thread._lock.lock();){
            ShutdownThread._thread._lifeCycles.addAll(Arrays.asList(lifeCycles));
            if (ShutdownThread._thread._lifeCycles.size() > 0) {
                _thread.hook();
            }
        }
    }

    public static void register(int index, LifeCycle ... lifeCycles) {
        try (AutoLock l = ShutdownThread._thread._lock.lock();){
            ShutdownThread._thread._lifeCycles.addAll(index, Arrays.asList(lifeCycles));
            if (ShutdownThread._thread._lifeCycles.size() > 0) {
                _thread.hook();
            }
        }
    }

    public static void deregister(LifeCycle lifeCycle) {
        try (AutoLock l = ShutdownThread._thread._lock.lock();){
            ShutdownThread._thread._lifeCycles.remove(lifeCycle);
            if (ShutdownThread._thread._lifeCycles.size() == 0) {
                _thread.unhook();
            }
        }
    }

    public static boolean isRegistered(LifeCycle lifeCycle) {
        try (AutoLock l = ShutdownThread._thread._lock.lock();){
            boolean bl = ShutdownThread._thread._lifeCycles.contains(lifeCycle);
            return bl;
        }
    }

    @Override
    public void run() {
        for (LifeCycle lifeCycle : ShutdownThread._thread._lifeCycles) {
            try {
                if (lifeCycle.isStarted()) {
                    lifeCycle.stop();
                    LOG.debug("Stopped {}", (Object)lifeCycle);
                }
                if (!(lifeCycle instanceof Destroyable)) continue;
                ((Destroyable)((Object)lifeCycle)).destroy();
                LOG.debug("Destroyed {}", (Object)lifeCycle);
            }
            catch (Exception ex) {
                LOG.debug("Unable to stop", (Throwable)ex);
            }
        }
    }
}

