/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster
 *  org.eclipse.gemini.blueprint.util.OsgiBundleUtils
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.core.task.SyncTaskExecutor
 *  org.springframework.core.task.TaskExecutor
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor;
import org.eclipse.gemini.blueprint.extender.internal.activator.ApplicationContextConfigurationFactory;
import org.eclipse.gemini.blueprint.extender.internal.activator.OsgiContextProcessor;
import org.eclipse.gemini.blueprint.extender.internal.activator.TypeCompatibilityChecker;
import org.eclipse.gemini.blueprint.extender.internal.activator.VersionMatcher;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.shutdown.ShutdownSorter;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.DependencyWaiterApplicationContextExecutor;
import org.eclipse.gemini.blueprint.extender.internal.support.ExtenderConfiguration;
import org.eclipse.gemini.blueprint.extender.internal.support.OsgiBeanFactoryPostProcessorAdapter;
import org.eclipse.gemini.blueprint.extender.internal.util.concurrent.Counter;
import org.eclipse.gemini.blueprint.extender.internal.util.concurrent.RunnableTimedExecution;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

class LifecycleManager
implements DisposableBean {
    private static final Log log = LogFactory.getLog(LifecycleManager.class);
    private final Map<Long, ConfigurableOsgiBundleApplicationContext> managedContexts = new ConcurrentHashMap<Long, ConfigurableOsgiBundleApplicationContext>(16);
    private Counter contextsStarted = new Counter("contextsStarted");
    private final Timer timer = new Timer("Spring DM Context Creation Timer", true);
    private final TaskExecutor taskExecutor;
    private final OsgiApplicationContextCreator contextCreator;
    private final List<OsgiBeanFactoryPostProcessor> postProcessors;
    private final TaskExecutor shutdownTaskExecutor;
    private final TaskExecutor sameThreadTaskExecutor = new SyncTaskExecutor();
    private final OsgiBundleApplicationContextEventMulticaster multicaster;
    private final ExtenderConfiguration extenderConfiguration;
    private final BundleContext bundleContext;
    private final OsgiContextProcessor processor;
    private final ApplicationContextConfigurationFactory contextConfigurationFactory;
    private final VersionMatcher versionMatcher;
    private final TypeCompatibilityChecker typeChecker;

    LifecycleManager(ExtenderConfiguration extenderConfiguration, VersionMatcher versionMatcher, ApplicationContextConfigurationFactory appCtxCfgFactory, OsgiApplicationContextCreator osgiApplicationContextCreator, OsgiContextProcessor processor, TypeCompatibilityChecker checker, BundleContext context) {
        this.versionMatcher = versionMatcher;
        this.extenderConfiguration = extenderConfiguration;
        this.contextConfigurationFactory = appCtxCfgFactory;
        this.contextCreator = osgiApplicationContextCreator;
        this.processor = processor;
        this.taskExecutor = extenderConfiguration.getTaskExecutor();
        this.shutdownTaskExecutor = extenderConfiguration.getShutdownTaskExecutor();
        this.multicaster = extenderConfiguration.getEventMulticaster();
        this.postProcessors = extenderConfiguration.getPostProcessors();
        this.typeChecker = checker;
        this.bundleContext = context;
    }

    protected void maybeCreateApplicationContextFor(Bundle bundle) {
        String creationType;
        DelegatedExecutionOsgiBundleApplicationContext localApplicationContext;
        boolean debug = log.isDebugEnabled();
        String bundleString = "[" + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + "]";
        Long bundleId = new Long(bundle.getBundleId());
        if (this.managedContexts.containsKey(bundleId)) {
            if (debug) {
                log.debug((Object)("Bundle " + bundleString + " is already managed; ignoring..."));
            }
            return;
        }
        if (!this.versionMatcher.matchVersion(bundle)) {
            return;
        }
        BundleContext localBundleContext = OsgiBundleUtils.getBundleContext((Bundle)bundle);
        if (localBundleContext == null) {
            if (debug) {
                log.debug((Object)("Bundle " + bundleString + " has no bundle context; skipping..."));
            }
            return;
        }
        if (debug) {
            log.debug((Object)("Inspecting bundle " + bundleString));
        }
        try {
            localApplicationContext = this.contextCreator.createApplicationContext(localBundleContext);
        }
        catch (Exception ex) {
            log.error((Object)("Cannot create application context for bundle " + bundleString), (Throwable)ex);
            return;
        }
        if (localApplicationContext == null) {
            log.debug((Object)("No application context created for bundle " + bundleString));
            return;
        }
        if (this.typeChecker != null && !this.typeChecker.isTypeCompatible(localBundleContext)) {
            log.info((Object)("Bundle " + OsgiStringUtils.nullSafeName((Bundle)bundle) + " is not type compatible with extender " + OsgiStringUtils.nullSafeName((Bundle)this.bundleContext.getBundle()) + "; ignoring bundle..."));
            return;
        }
        log.debug((Object)("Bundle " + OsgiStringUtils.nullSafeName((Bundle)bundle) + " is type compatible with extender " + OsgiStringUtils.nullSafeName((Bundle)this.bundleContext.getBundle()) + "; processing bundle..."));
        OsgiBeanFactoryPostProcessorAdapter processingHook = new OsgiBeanFactoryPostProcessorAdapter(localBundleContext, this.postProcessors);
        localApplicationContext.addBeanFactoryPostProcessor((BeanFactoryPostProcessor)processingHook);
        this.managedContexts.put(bundleId, (ConfigurableOsgiBundleApplicationContext)localApplicationContext);
        localApplicationContext.setDelegatedEventMulticaster(this.multicaster);
        ApplicationContextConfiguration config = this.contextConfigurationFactory.createConfiguration(bundle);
        boolean asynch = config.isCreateAsynchronously();
        Runnable contextRefresh = new Runnable(){

            @Override
            public void run() {
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Calling pre-refresh on processor " + LifecycleManager.this.processor));
                }
                LifecycleManager.this.processor.preProcessRefresh((ConfigurableOsgiBundleApplicationContext)localApplicationContext);
                localApplicationContext.refresh();
            }
        };
        TaskExecutor executor = null;
        if (asynch) {
            executor = this.taskExecutor;
            creationType = "Asynchronous";
        } else {
            executor = this.sameThreadTaskExecutor;
            creationType = "Synchronous";
        }
        if (debug) {
            log.debug((Object)(creationType + " context creation for bundle " + bundleString));
        }
        if (config.isWaitForDependencies()) {
            long timeout;
            DependencyWaiterApplicationContextExecutor appCtxExecutor = new DependencyWaiterApplicationContextExecutor(localApplicationContext, !asynch, this.extenderConfiguration.getDependencyFactories());
            if (config.isTimeoutDeclared()) {
                timeout = config.getTimeout();
                if (debug) {
                    log.debug((Object)("Setting bundle-defined, wait-for-dependencies/graceperiod timeout value=" + timeout + " ms, for bundle " + bundleString));
                }
            } else {
                timeout = this.extenderConfiguration.getDependencyWaitTime();
                if (debug) {
                    log.debug((Object)("Setting globally defined wait-for-dependencies/graceperiod timeout value=" + timeout + " ms, for bundle " + bundleString));
                }
            }
            appCtxExecutor.setTimeout(timeout);
            appCtxExecutor.setWatchdog(this.timer);
            appCtxExecutor.setTaskExecutor(executor);
            appCtxExecutor.setMonitoringCounter(this.contextsStarted);
            appCtxExecutor.setDelegatedMulticaster(this.multicaster);
            this.contextsStarted.increment();
        }
        executor.execute(contextRefresh);
    }

    protected void maybeCloseApplicationContextFor(Bundle bundle) {
        ConfigurableOsgiBundleApplicationContext context = this.managedContexts.remove(bundle.getBundleId());
        if (context == null) {
            return;
        }
        this.maybeClose(context);
    }

    protected void maybeClose(final ConfigurableOsgiBundleApplicationContext context) {
        final String displayName = context.getDisplayName();
        Runnable shutdownTask = new Runnable(){
            private final String toString;
            {
                this.toString = "Closing runnable for context " + displayName;
            }

            @Override
            public void run() {
                LifecycleManager.this.closeApplicationContext(context);
            }

            public String toString() {
                return this.toString;
            }
        };
        if (this.extenderConfiguration.shouldShutdownAsynchronously()) {
            RunnableTimedExecution.execute(shutdownTask, this.extenderConfiguration.getShutdownWaitTime(), this.shutdownTaskExecutor);
        } else {
            try {
                shutdownTask.run();
            }
            catch (Exception e) {
                log.error((Object)(displayName + " context shutdown failed."), (Throwable)e);
            }
        }
    }

    private void closeApplicationContext(ConfigurableOsgiBundleApplicationContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Closing application context " + ctx.getDisplayName()));
        }
        if (log.isTraceEnabled()) {
            log.trace((Object)("Calling pre-close on processor " + this.processor));
        }
        this.processor.preProcessClose(ctx);
        try {
            ctx.close();
        }
        finally {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Calling post close on processor " + this.processor));
            }
            this.processor.postProcessClose(ctx);
        }
    }

    public void destroy() {
        this.stopTimer();
        ArrayList<Bundle> bundles = new ArrayList<Bundle>(this.managedContexts.size());
        for (ConfigurableOsgiBundleApplicationContext context : this.managedContexts.values()) {
            bundles.add(context.getBundle());
        }
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("Starting shutdown procedure for bundles " + bundles));
        }
        while (!bundles.isEmpty()) {
            Runnable[] tasks;
            Collection<Bundle> candidates = ShutdownSorter.getBundles(bundles);
            if (debug) {
                log.debug((Object)("Staging shutdown for bundles " + candidates));
            }
            ArrayList<3> taskList = new ArrayList<3>(candidates.size());
            final List<ConfigurableOsgiBundleApplicationContext> closedContexts = Collections.synchronizedList(new ArrayList());
            final Object[] contextClosingDown = new Object[1];
            for (Bundle shutdownBundle : candidates) {
                final ConfigurableOsgiBundleApplicationContext context = this.getManagedContext(shutdownBundle);
                if (context == null) continue;
                closedContexts.add(context);
                taskList.add(new Runnable(){
                    private final String toString;
                    {
                        this.toString = "Closing runnable for context " + context.getDisplayName();
                    }

                    @Override
                    public void run() {
                        contextClosingDown[0] = context;
                        closedContexts.remove(context);
                        LifecycleManager.this.closeApplicationContext(context);
                    }

                    public String toString() {
                        return this.toString;
                    }
                });
            }
            for (Runnable task : tasks = taskList.toArray(new Runnable[taskList.size()])) {
                if (this.extenderConfiguration.shouldShutdownAsynchronously()) {
                    if (!RunnableTimedExecution.execute(task, this.extenderConfiguration.getShutdownWaitTime(), this.shutdownTaskExecutor) || !debug) continue;
                    log.debug((Object)(contextClosingDown[0] + " context did not close successfully; forcing shutdown..."));
                    continue;
                }
                try {
                    task.run();
                }
                catch (Exception e) {
                    log.error((Object)(contextClosingDown[0] + " context close failed."), (Throwable)e);
                }
            }
        }
        this.managedContexts.clear();
        this.stopTaskExecutor();
    }

    public ConfigurableOsgiBundleApplicationContext getManagedContext(Bundle bundle) {
        ConfigurableOsgiBundleApplicationContext context = null;
        try {
            Long id = new Long(bundle.getBundleId());
            context = this.managedContexts.get(id);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
        return context;
    }

    private void stopTaskExecutor() {
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)("Waiting for " + this.contextsStarted + " service dependency listener(s) to stop..."));
        }
        this.contextsStarted.waitForZero(this.extenderConfiguration.getShutdownWaitTime());
        if (!this.contextsStarted.isZero()) {
            if (debug) {
                log.debug((Object)(this.contextsStarted.getValue() + " service dependency listener(s) did not responded in time; forcing them to shutdown..."));
            }
            this.extenderConfiguration.setForceThreadShutdown(true);
        } else {
            log.debug((Object)"All listeners closed");
        }
    }

    private void stopTimer() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Canceling timer tasks");
        }
        this.timer.cancel();
    }
}

