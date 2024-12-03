/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.context.OsgiBundleApplicationContextExecutor
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent
 *  org.eclipse.gemini.blueprint.util.OsgiFilterUtils
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.Filter
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.extender.internal.dependencies.startup;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.OsgiBundleApplicationContextExecutor;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.extender.OsgiServiceDependencyFactory;
import org.eclipse.gemini.blueprint.extender.event.BootstrappingDependenciesFailedEvent;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.ContextExecutorAccessor;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.ContextState;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.DependencyServiceManager;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.MandatoryServiceDependency;
import org.eclipse.gemini.blueprint.extender.internal.util.concurrent.Counter;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;
import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

public class DependencyWaiterApplicationContextExecutor
implements OsgiBundleApplicationContextExecutor,
ContextExecutorAccessor {
    private static final Log log = LogFactory.getLog(DependencyWaiterApplicationContextExecutor.class);
    private final Object monitor = new Object();
    private long timeout;
    private Timer watchdog;
    private TimerTask watchdogTask;
    protected DependencyServiceManager dependencyDetector;
    protected final DelegatedExecutionOsgiBundleApplicationContext delegateContext;
    private ContextState state = ContextState.INITIALIZED;
    private TaskExecutor taskExecutor;
    private Counter monitorCounter;
    private final boolean synchronousWait;
    private final Counter waitBarrier = new Counter("syncCounterWait");
    private OsgiBundleApplicationContextEventMulticaster delegatedMulticaster;
    private List<OsgiServiceDependencyFactory> dependencyFactories;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DependencyWaiterApplicationContextExecutor(DelegatedExecutionOsgiBundleApplicationContext delegateContext, boolean syncWait, List<OsgiServiceDependencyFactory> dependencyFactories) {
        this.delegateContext = delegateContext;
        this.delegateContext.setExecutor((OsgiBundleApplicationContextExecutor)this);
        this.synchronousWait = syncWait;
        this.dependencyFactories = dependencyFactories;
        Object object = this.monitor;
        synchronized (object) {
            this.watchdogTask = new WatchDogTask();
        }
    }

    public void refresh() throws BeansException, IllegalStateException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Starting first stage of refresh for " + this.getDisplayName()));
        }
        this.init();
        this.stageOne();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void init() {
        Object object = this.monitor;
        synchronized (object) {
            Assert.notNull((Object)this.watchdog, (String)"watchdog timer required");
            Assert.notNull((Object)this.monitorCounter, (String)" monitorCounter required");
            if (this.state == ContextState.INTERRUPTED || this.state == ContextState.STOPPED) {
                IllegalStateException ex = new IllegalStateException("cannot refresh an interrupted/closed context");
                log.fatal((Object)ex);
                throw ex;
            }
            this.state = ContextState.INITIALIZED;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void stageOne() {
        block18: {
            boolean debug = log.isDebugEnabled();
            boolean skipExceptionEvent = true;
            try {
                if (debug) {
                    log.debug((Object)("Calling preRefresh on " + this.getDisplayName()));
                }
                Object object = this.monitor;
                synchronized (object) {
                    if (this.state != ContextState.INITIALIZED) {
                        this.logWrongState(ContextState.INITIALIZED);
                        return;
                    }
                    this.state = ContextState.RESOLVING_DEPENDENCIES;
                }
                this.delegateContext.startRefresh();
                if (debug) {
                    log.debug((Object)"Pre-refresh completed; determining dependencies...");
                }
                Runnable task = null;
                task = this.synchronousWait ? new Runnable(){

                    @Override
                    public void run() {
                        DependencyWaiterApplicationContextExecutor.this.waitBarrier.decrement();
                    }
                } : new Runnable(){

                    @Override
                    public void run() {
                        DependencyWaiterApplicationContextExecutor.this.stageTwo();
                    }
                };
                skipExceptionEvent = false;
                DependencyServiceManager dl = this.createDependencyServiceListener(task);
                dl.findServiceDependencies();
                skipExceptionEvent = true;
                if (dl.isSatisfied()) {
                    log.info((Object)("No outstanding OSGi service dependencies, completing initialization for " + this.getDisplayName()));
                    this.stageTwo();
                    break block18;
                }
                Object object2 = this.monitor;
                synchronized (object2) {
                    this.dependencyDetector = dl;
                }
                if (debug) {
                    log.debug((Object)("Registering service dependency dependencyDetector for " + this.getDisplayName()));
                }
                this.dependencyDetector.register();
                if (this.synchronousWait) {
                    this.waitBarrier.increment();
                    if (debug) {
                        log.debug((Object)"Synchronous wait-for-dependencies; waiting...");
                    }
                    if (this.waitBarrier.waitForZero(this.timeout)) {
                        this.timeout();
                    } else {
                        this.stageTwo();
                    }
                } else {
                    this.startWatchDog();
                }
            }
            catch (Throwable e) {
                this.fail(e, skipExceptionEvent);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void stageTwo() {
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("Starting stage two for " + this.getDisplayName()));
        }
        Object object = this.monitor;
        synchronized (object) {
            if (this.state != ContextState.RESOLVING_DEPENDENCIES) {
                this.logWrongState(ContextState.RESOLVING_DEPENDENCIES);
                return;
            }
            this.stopWatchDog();
            this.state = ContextState.DEPENDENCIES_RESOLVED;
        }
        this.taskExecutor.execute((Runnable)new CompleteRefreshTask());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        boolean debug = log.isDebugEnabled();
        boolean normalShutdown = false;
        this.stopWatchDog();
        Object object = this.monitor;
        synchronized (object) {
            if (this.state.isDown()) {
                return;
            }
            if (debug) {
                log.debug((Object)("Closing appCtx for " + this.getDisplayName()));
            }
            if (this.state == ContextState.RESOLVING_DEPENDENCIES) {
                if (debug) {
                    log.debug((Object)("Cleaning up appCtx " + this.getDisplayName()));
                }
                if (this.delegateContext.isActive()) {
                    try {
                        this.delegateContext.getBeanFactory().destroySingletons();
                    }
                    catch (Exception ex) {
                        log.trace((Object)"Caught exception while interrupting context refresh ", (Throwable)ex);
                    }
                    this.state = ContextState.INTERRUPTED;
                }
            } else if (this.state == ContextState.DEPENDENCIES_RESOLVED) {
                if (debug) {
                    log.debug((Object)("Shutting down appCtx " + this.getDisplayName() + " once stageTwo() is complete"));
                }
                this.state = ContextState.STOPPED;
                normalShutdown = true;
            } else if (this.state == ContextState.STARTED) {
                if (debug) {
                    log.debug((Object)("Shutting down normally appCtx " + this.getDisplayName()));
                }
                this.state = ContextState.STOPPED;
                normalShutdown = true;
            } else {
                if (debug) {
                    log.debug((Object)"No need to stop context (it hasn't been started yet)");
                }
                this.state = ContextState.INTERRUPTED;
            }
            if (this.dependencyDetector != null) {
                this.dependencyDetector.deregister();
            }
        }
        try {
            if (normalShutdown) {
                this.delegateContext.normalClose();
            }
        }
        catch (Exception ex) {
            log.fatal((Object)("Could not succesfully close context " + this.delegateContext), (Throwable)ex);
        }
        finally {
            this.monitorCounter.decrement();
        }
    }

    @Override
    public void fail(Throwable t) {
        this.fail(t, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fail(Throwable t, boolean skipEvent) {
        this.close();
        StringBuilder buf = new StringBuilder();
        Object object = this.monitor;
        synchronized (object) {
            if (this.dependencyDetector == null || this.dependencyDetector.isSatisfied()) {
                buf.append("none");
            } else {
                Iterator<MandatoryServiceDependency> iterator = this.dependencyDetector.getUnsatisfiedDependencies().keySet().iterator();
                while (iterator.hasNext()) {
                    MandatoryServiceDependency dependency = iterator.next();
                    buf.append(dependency.toString());
                    if (!iterator.hasNext()) continue;
                    buf.append(", ");
                }
            }
        }
        final StringBuilder message = new StringBuilder();
        message.append("Unable to create application context for [");
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    message.append(OsgiStringUtils.nullSafeSymbolicName((Bundle)DependencyWaiterApplicationContextExecutor.this.getBundle()));
                    return null;
                }
            });
        } else {
            message.append(OsgiStringUtils.nullSafeSymbolicName((Bundle)this.getBundle()));
        }
        message.append("], unsatisfied dependencies: ");
        message.append(buf.toString());
        log.error((Object)message.toString(), t);
        if (!skipEvent) {
            this.delegatedMulticaster.multicastEvent((OsgiBundleApplicationContextEvent)new OsgiBundleContextFailedEvent((ApplicationContext)this.delegateContext, this.delegateContext.getBundle(), t));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void timeout() {
        List<OsgiServiceDependencyEvent> events = null;
        String filterAsString = null;
        Object object = this.monitor;
        synchronized (object) {
            if (this.dependencyDetector != null) {
                this.dependencyDetector.deregister();
                events = this.dependencyDetector.getUnsatisfiedDependenciesAsEvents();
                filterAsString = this.dependencyDetector.createUnsatisfiedDependencyFilter();
            }
        }
        Filter filter = filterAsString != null ? OsgiFilterUtils.createFilter(filterAsString) : null;
        log.warn((Object)("Timeout occurred before finding service dependencies for [" + this.delegateContext.getDisplayName() + "]"));
        String bundleName = null;
        bundleName = System.getSecurityManager() != null ? AccessController.doPrivileged(new PrivilegedAction<String>(){

            @Override
            public String run() {
                return OsgiStringUtils.nullSafeSymbolicName((Bundle)DependencyWaiterApplicationContextExecutor.this.getBundle());
            }
        }) : OsgiStringUtils.nullSafeSymbolicName((Bundle)this.getBundle());
        ApplicationContextException e = new ApplicationContextException("Application context initialization for '" + bundleName + "' has timed out waiting for " + filterAsString);
        e.fillInStackTrace();
        this.delegatedMulticaster.multicastEvent((OsgiBundleApplicationContextEvent)new BootstrappingDependenciesFailedEvent((ApplicationContext)this.delegateContext, this.delegateContext.getBundle(), (Throwable)e, events, filter));
        this.fail((Throwable)e, true);
    }

    protected DependencyServiceManager createDependencyServiceListener(Runnable task) {
        return new DependencyServiceManager(this, this.delegateContext, this.dependencyFactories, task, this.timeout);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void startWatchDog() {
        boolean started = false;
        Object object = this.monitor;
        synchronized (object) {
            if (this.watchdogTask != null) {
                started = true;
                this.watchdog.schedule(this.watchdogTask, this.timeout);
            }
        }
        boolean debug = log.isDebugEnabled();
        if (debug) {
            if (started) {
                log.debug((Object)"Asynch wait-for-dependencies started...");
            } else {
                log.debug((Object)"Dependencies satisfied; no need to start a watchdog...");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void stopWatchDog() {
        boolean stopped = false;
        Object object = this.monitor;
        synchronized (object) {
            if (this.watchdogTask != null) {
                this.watchdogTask.cancel();
                this.watchdogTask = null;
                stopped = true;
            }
        }
        if (stopped && log.isDebugEnabled()) {
            log.debug((Object)"Cancelled dependency watchdog...");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setTimeout(long timeout) {
        Object object = this.monitor;
        synchronized (object) {
            this.timeout = timeout;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setTaskExecutor(TaskExecutor taskExec) {
        Object object = this.monitor;
        synchronized (object) {
            this.taskExecutor = taskExec;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Bundle getBundle() {
        Object object = this.monitor;
        synchronized (object) {
            return this.delegateContext.getBundle();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getDisplayName() {
        Object object = this.monitor;
        synchronized (object) {
            return this.delegateContext.getDisplayName();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setWatchdog(Timer watchdog) {
        Object object = this.monitor;
        synchronized (object) {
            this.watchdog = watchdog;
        }
    }

    private void logWrongState(ContextState expected) {
        log.error((Object)("Expecting state (" + (Object)((Object)expected) + ") not (" + (Object)((Object)this.state) + ") for context [" + this.getDisplayName() + "]; assuming an interruption and bailing out"));
    }

    public void setMonitoringCounter(Counter contextsStarted) {
        this.monitorCounter = contextsStarted;
    }

    public void setDelegatedMulticaster(OsgiBundleApplicationContextEventMulticaster multicaster) {
        this.delegatedMulticaster = multicaster;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ContextState getContextState() {
        Object object = this.monitor;
        synchronized (object) {
            return this.state;
        }
    }

    @Override
    public OsgiBundleApplicationContextEventMulticaster getEventMulticaster() {
        return this.delegatedMulticaster;
    }

    private class CompleteRefreshTask
    implements Runnable {
        private CompleteRefreshTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            boolean debug = log.isDebugEnabled();
            if (debug) {
                log.debug((Object)("Completing refresh for " + DependencyWaiterApplicationContextExecutor.this.getDisplayName()));
            }
            Object object = DependencyWaiterApplicationContextExecutor.this.monitor;
            synchronized (object) {
                if (DependencyWaiterApplicationContextExecutor.this.state != ContextState.DEPENDENCIES_RESOLVED) {
                    DependencyWaiterApplicationContextExecutor.this.logWrongState(ContextState.DEPENDENCIES_RESOLVED);
                    return;
                }
            }
            try {
                DependencyWaiterApplicationContextExecutor.this.delegateContext.completeRefresh();
            }
            catch (Throwable th) {
                DependencyWaiterApplicationContextExecutor.this.fail(th, true);
            }
            object = DependencyWaiterApplicationContextExecutor.this.monitor;
            synchronized (object) {
                if (DependencyWaiterApplicationContextExecutor.this.state != ContextState.DEPENDENCIES_RESOLVED) {
                    return;
                }
                DependencyWaiterApplicationContextExecutor.this.state = ContextState.STARTED;
            }
        }
    }

    private class WatchDogTask
    extends TimerTask {
        private WatchDogTask() {
        }

        @Override
        public void run() {
            DependencyWaiterApplicationContextExecutor.this.timeout();
        }
    }
}

