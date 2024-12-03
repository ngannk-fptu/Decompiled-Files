/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent
 *  org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitEndedEvent
 *  org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitStartingEvent
 *  org.eclipse.gemini.blueprint.util.OsgiFilterUtils
 *  org.eclipse.gemini.blueprint.util.OsgiListenerUtils
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceListener
 *  org.osgi.framework.ServiceReference
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.ApplicationContext
 */
package org.eclipse.gemini.blueprint.extender.internal.dependencies.startup;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.extender.OsgiServiceDependencyFactory;
import org.eclipse.gemini.blueprint.extender.event.BootstrappingDependenciesEvent;
import org.eclipse.gemini.blueprint.extender.event.BootstrappingDependencyEvent;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.ContextExecutorAccessor;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.ContextState;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.MandatoryServiceDependency;
import org.eclipse.gemini.blueprint.extender.internal.util.PrivilegedUtils;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitEndedEvent;
import org.eclipse.gemini.blueprint.service.importer.event.OsgiServiceDependencyWaitStartingEvent;
import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
import org.eclipse.gemini.blueprint.util.OsgiListenerUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

public class DependencyServiceManager {
    private static final Map<MandatoryServiceDependency, String> UNMODIFIABLE_DEPENDENCY_MAP = Collections.unmodifiableMap(new HashMap(0));
    private static final Log log = LogFactory.getLog(DependencyServiceManager.class);
    private final Object monitor = new Object();
    protected final Map<MandatoryServiceDependency, String> dependencies = new LinkedHashMap<MandatoryServiceDependency, String>();
    protected final Map<MandatoryServiceDependency, String> unsatisfiedDependencies = new LinkedHashMap<MandatoryServiceDependency, String>();
    private final ContextExecutorAccessor contextStateAccessor;
    private final BundleContext bundleContext;
    private final ServiceListener listener;
    private final DelegatedExecutionOsgiBundleApplicationContext context;
    private final Runnable executeIfDone;
    private final long waitTime;
    private List<OsgiServiceDependencyFactory> dependencyFactories;

    public DependencyServiceManager(ContextExecutorAccessor executor, DelegatedExecutionOsgiBundleApplicationContext context, List<OsgiServiceDependencyFactory> dependencyFactories, Runnable executeIfDone, long maxWaitTime) {
        this.contextStateAccessor = executor;
        this.context = context;
        this.dependencyFactories = new ArrayList<OsgiServiceDependencyFactory>(8);
        if (dependencyFactories != null) {
            this.dependencyFactories.addAll(dependencyFactories);
        }
        this.waitTime = maxWaitTime;
        this.bundleContext = context.getBundleContext();
        this.listener = new DependencyServiceListener();
        this.executeIfDone = executeIfDone;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void findServiceDependencies() throws Exception {
        try {
            if (System.getSecurityManager() != null) {
                final AccessControlContext acc = this.getAcc();
                PrivilegedUtils.executeWithCustomTCCL(this.context.getClassLoader(), new PrivilegedUtils.UnprivilegedThrowableExecution<Object>(){

                    @Override
                    public Object run() throws Throwable {
                        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){

                            @Override
                            public Object run() throws Exception {
                                DependencyServiceManager.this.doFindDependencies();
                                return null;
                            }
                        }, acc);
                        return null;
                    }
                });
            } else {
                this.doFindDependencies();
            }
        }
        catch (Throwable th) {
            if (th instanceof Exception) {
                throw (Exception)th;
            }
            throw (Error)th;
        }
        Collection<String> unsatisfiedDependencyValues = this.getUnsatisfiedDependencies().values();
        if (log.isDebugEnabled()) {
            int numUnsatisfiedDependencies;
            int numDependencies;
            Object object = this.monitor;
            synchronized (object) {
                numDependencies = this.dependencies.size();
                numUnsatisfiedDependencies = this.unsatisfiedDependencies.size();
            }
            log.debug((Object)(numDependencies + " OSGi service dependencies, " + numUnsatisfiedDependencies + " unsatisfied (for beans " + unsatisfiedDependencyValues + ") in " + this.context.getDisplayName()));
        }
        if (!this.isSatisfied()) {
            log.info((Object)(this.context.getDisplayName() + " is waiting for unsatisfied dependencies [" + unsatisfiedDependencyValues + "]"));
        }
        if (log.isTraceEnabled()) {
            ArrayList<String> dependencyValues;
            Object object = this.monitor;
            synchronized (object) {
                dependencyValues = new ArrayList<String>(this.dependencies.values());
            }
            log.trace((Object)("Total OSGi service dependencies beans " + dependencyValues));
            log.trace((Object)("Unsatified OSGi service dependencies beans " + unsatisfiedDependencyValues));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doFindDependencies() throws Exception {
        ConfigurableListableBeanFactory beanFactory = this.context.getBeanFactory();
        boolean debug = log.isDebugEnabled();
        boolean trace = log.isTraceEnabled();
        if (trace) {
            log.trace((Object)("Looking for dependency factories inside bean factory [" + beanFactory.toString() + "]"));
        }
        Map localFactories = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)beanFactory, OsgiServiceDependencyFactory.class, (boolean)true, (boolean)false);
        if (trace) {
            log.trace((Object)("Discovered local dependency factories: " + localFactories.keySet()));
        }
        this.dependencyFactories.addAll(localFactories.values());
        for (OsgiServiceDependencyFactory dependencyFactory : this.dependencyFactories) {
            Collection<OsgiServiceDependency> discoveredDependencies = null;
            if (trace) {
                log.trace((Object)("Interogating dependency factory " + dependencyFactory));
            }
            try {
                discoveredDependencies = dependencyFactory.getServiceDependencies(this.bundleContext, beanFactory);
            }
            catch (Exception ex) {
                log.warn((Object)("Dependency factory " + dependencyFactory + " threw exception while detecting dependencies for beanFactory " + beanFactory + " in " + this.context.getDisplayName()), (Throwable)ex);
                throw ex;
            }
            if (discoveredDependencies == null) continue;
            for (OsgiServiceDependency dependency : discoveredDependencies) {
                if (!dependency.isMandatory()) continue;
                MandatoryServiceDependency msd = new MandatoryServiceDependency(this.bundleContext, dependency);
                Object object = this.monitor;
                synchronized (object) {
                    this.dependencies.put(msd, dependency.getBeanName());
                }
                if (!msd.isServicePresent()) {
                    log.info((Object)("Adding OSGi service dependency for importer [" + msd.getBeanName() + "] matching OSGi filter [" + msd.filterAsString + "]"));
                    object = this.monitor;
                    synchronized (object) {
                        this.unsatisfiedDependencies.put(msd, dependency.getBeanName());
                        continue;
                    }
                }
                if (!debug) continue;
                log.debug((Object)("OSGi service dependency for importer [" + msd.getBeanName() + "] is already satisfied"));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isSatisfied() {
        Object object = this.monitor;
        synchronized (object) {
            return this.unsatisfiedDependencies.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<MandatoryServiceDependency, String> getUnsatisfiedDependencies() {
        if (this.isSatisfied()) {
            return UNMODIFIABLE_DEPENDENCY_MAP;
        }
        Object object = this.monitor;
        synchronized (object) {
            return Collections.unmodifiableMap(new HashMap<MandatoryServiceDependency, String>(this.unsatisfiedDependencies));
        }
    }

    protected void register() {
        final String filter = this.createDependencyFilter();
        if (log.isDebugEnabled()) {
            log.debug((Object)(this.context.getDisplayName() + " has registered service dependency dependencyDetector with filter: " + filter));
        }
        this.sendInitialBootstrappingEvents(this.getUnsatisfiedDependencies().keySet());
        if (System.getSecurityManager() != null) {
            AccessControlContext acc = this.getAcc();
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    OsgiListenerUtils.addServiceListener((BundleContext)DependencyServiceManager.this.bundleContext, (ServiceListener)DependencyServiceManager.this.listener, (String)filter);
                    return null;
                }
            }, acc);
        } else {
            OsgiListenerUtils.addServiceListener((BundleContext)this.bundleContext, (ServiceListener)this.listener, (String)filter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String createDependencyFilter() {
        Object object = this.monitor;
        synchronized (object) {
            return this.createDependencyFilter(this.dependencies.keySet());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    String createUnsatisfiedDependencyFilter() {
        Object object = this.monitor;
        synchronized (object) {
            return this.createDependencyFilter(this.unsatisfiedDependencies.keySet());
        }
    }

    private String createDependencyFilter(Collection<MandatoryServiceDependency> dependencies) {
        if (dependencies.isEmpty()) {
            return null;
        }
        boolean multiple = dependencies.size() > 1;
        StringBuilder sb = new StringBuilder(dependencies.size() << 7);
        if (multiple) {
            sb.append("(|");
        }
        for (MandatoryServiceDependency dependency : dependencies) {
            sb.append(dependency.filterAsString);
        }
        if (multiple) {
            sb.append(')');
        }
        String filter = sb.toString();
        return filter;
    }

    protected void deregister() {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Deregistering service dependency dependencyDetector for " + this.context.getDisplayName()));
        }
        OsgiListenerUtils.removeServiceListener((BundleContext)this.bundleContext, (ServiceListener)this.listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    List<OsgiServiceDependencyEvent> getUnsatisfiedDependenciesAsEvents() {
        Object object = this.monitor;
        synchronized (object) {
            return this.getUnsatisfiedDependenciesAsEvents(this.unsatisfiedDependencies.keySet());
        }
    }

    private List<OsgiServiceDependencyEvent> getUnsatisfiedDependenciesAsEvents(Collection<MandatoryServiceDependency> deps) {
        ArrayList<OsgiServiceDependencyWaitStartingEvent> dependencies = new ArrayList<OsgiServiceDependencyWaitStartingEvent>(deps.size());
        for (MandatoryServiceDependency entry : deps) {
            OsgiServiceDependencyWaitStartingEvent nestedEvent = new OsgiServiceDependencyWaitStartingEvent((Object)this.context, entry.getServiceDependency(), this.waitTime);
            dependencies.add(nestedEvent);
        }
        return Collections.unmodifiableList(dependencies);
    }

    private void sendDependencyUnsatisfiedEvent(MandatoryServiceDependency dependency) {
        OsgiServiceDependencyWaitStartingEvent nestedEvent = new OsgiServiceDependencyWaitStartingEvent((Object)this.context, dependency.getServiceDependency(), this.waitTime);
        BootstrappingDependencyEvent dependencyEvent = new BootstrappingDependencyEvent((ApplicationContext)this.context, this.context.getBundle(), (OsgiServiceDependencyEvent)nestedEvent);
        this.publishEvent(dependencyEvent);
    }

    private void sendDependencySatisfiedEvent(MandatoryServiceDependency dependency) {
        OsgiServiceDependencyWaitEndedEvent nestedEvent = new OsgiServiceDependencyWaitEndedEvent((Object)this.context, dependency.getServiceDependency(), this.waitTime);
        BootstrappingDependencyEvent dependencyEvent = new BootstrappingDependencyEvent((ApplicationContext)this.context, this.context.getBundle(), (OsgiServiceDependencyEvent)nestedEvent);
        this.publishEvent(dependencyEvent);
    }

    private void sendInitialBootstrappingEvents(Set<MandatoryServiceDependency> deps) {
        List<OsgiServiceDependencyEvent> events = this.getUnsatisfiedDependenciesAsEvents(deps);
        for (OsgiServiceDependencyEvent nestedEvent : events) {
            BootstrappingDependencyEvent dependencyEvent = new BootstrappingDependencyEvent((ApplicationContext)this.context, this.context.getBundle(), nestedEvent);
            this.publishEvent(dependencyEvent);
        }
        String filterAsString = this.createDependencyFilter(deps);
        Filter filter = filterAsString != null ? OsgiFilterUtils.createFilter((String)filterAsString) : null;
        BootstrappingDependenciesEvent event = new BootstrappingDependenciesEvent((ApplicationContext)this.context, this.context.getBundle(), events, filter, this.waitTime);
        this.publishEvent(event);
    }

    private void sendBootstrappingDependenciesEvent(Set<MandatoryServiceDependency> deps) {
        List<OsgiServiceDependencyEvent> events = this.getUnsatisfiedDependenciesAsEvents(deps);
        String filterAsString = this.createDependencyFilter(deps);
        Filter filter = filterAsString != null ? OsgiFilterUtils.createFilter((String)filterAsString) : null;
        BootstrappingDependenciesEvent event = new BootstrappingDependenciesEvent((ApplicationContext)this.context, this.context.getBundle(), events, filter, this.waitTime);
        this.publishEvent(event);
    }

    private void publishEvent(OsgiBundleApplicationContextEvent dependencyEvent) {
        this.contextStateAccessor.getEventMulticaster().multicastEvent(dependencyEvent);
    }

    private AccessControlContext getAcc() {
        AutowireCapableBeanFactory beanFactory = this.context.getAutowireCapableBeanFactory();
        if (beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)beanFactory).getAccessControlContext();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean allDependenciesSatisfied() {
        Object object = this.monitor;
        synchronized (object) {
            return this.unsatisfiedDependencies.isEmpty();
        }
    }

    private class DependencyServiceListener
    implements ServiceListener {
        private DependencyServiceListener() {
        }

        public void serviceChanged(ServiceEvent serviceEvent) {
            boolean trace = log.isTraceEnabled();
            try {
                if (DependencyServiceManager.this.isSatisfied()) {
                    if (trace) {
                        log.trace((Object)("Handling service event, but no unsatisfied dependencies exist for " + DependencyServiceManager.this.context.getDisplayName()));
                    }
                    return;
                }
                ServiceReference ref = serviceEvent.getServiceReference();
                if (trace) {
                    log.trace((Object)("Handling service event [" + OsgiStringUtils.nullSafeToString((ServiceEvent)serviceEvent) + ":" + OsgiStringUtils.nullSafeToString((ServiceReference)ref) + "] for " + DependencyServiceManager.this.context.getDisplayName()));
                }
                this.updateDependencies(serviceEvent);
                ContextState state = DependencyServiceManager.this.contextStateAccessor.getContextState();
                if (state.isResolved()) {
                    DependencyServiceManager.this.deregister();
                    return;
                }
                if (DependencyServiceManager.this.isSatisfied()) {
                    DependencyServiceManager.this.deregister();
                    log.info((Object)("No unsatisfied OSGi service dependencies; completing initialization for " + DependencyServiceManager.this.context.getDisplayName()));
                    DependencyServiceManager.this.executeIfDone.run();
                }
            }
            catch (Throwable th) {
                log.error((Object)("Exception during dependency processing for " + DependencyServiceManager.this.context.getDisplayName()), th);
                DependencyServiceManager.this.contextStateAccessor.fail(th);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void updateDependencies(ServiceEvent serviceEvent) {
            HashSet<MandatoryServiceDependency> mandatoryServiceDependencies;
            boolean trace = log.isTraceEnabled();
            boolean debug = log.isDebugEnabled();
            String referenceToString = null;
            String contextToString = null;
            if (debug) {
                referenceToString = OsgiStringUtils.nullSafeToString((ServiceReference)serviceEvent.getServiceReference());
                contextToString = DependencyServiceManager.this.context.getDisplayName();
            }
            Iterator iterator = DependencyServiceManager.this.monitor;
            synchronized (iterator) {
                mandatoryServiceDependencies = new HashSet<MandatoryServiceDependency>(DependencyServiceManager.this.dependencies.keySet());
            }
            block13: for (MandatoryServiceDependency dependency : mandatoryServiceDependencies) {
                if (dependency.matches(serviceEvent)) {
                    if (trace) {
                        log.trace((Object)(dependency + " matched: " + referenceToString));
                    }
                    switch (serviceEvent.getType()) {
                        case 1: 
                        case 2: {
                            Map<MandatoryServiceDependency, String> unsatisfiedDependenciesSnapshot;
                            String removedDependency;
                            dependency.increment();
                            Object object = DependencyServiceManager.this.monitor;
                            synchronized (object) {
                                removedDependency = DependencyServiceManager.this.unsatisfiedDependencies.remove(dependency);
                            }
                            if (removedDependency != null) {
                                unsatisfiedDependenciesSnapshot = DependencyServiceManager.this.getUnsatisfiedDependencies();
                                if (debug) {
                                    log.debug((Object)("Registered dependency for " + contextToString + "; eliminating " + dependency + ", remaining [" + unsatisfiedDependenciesSnapshot + "]"));
                                }
                                DependencyServiceManager.this.sendDependencySatisfiedEvent(dependency);
                                DependencyServiceManager.this.sendBootstrappingDependenciesEvent(unsatisfiedDependenciesSnapshot.keySet());
                                continue block13;
                            }
                            if (!debug) continue block13;
                            unsatisfiedDependenciesSnapshot = DependencyServiceManager.this.getUnsatisfiedDependencies();
                            log.debug((Object)("Increasing the number of matching services for " + contextToString + "; " + dependency + ", remaining [" + unsatisfiedDependenciesSnapshot + "]"));
                            continue block13;
                        }
                        case 4: {
                            int count = dependency.decrement();
                            if (count == 0) {
                                Object object = DependencyServiceManager.this.monitor;
                                synchronized (object) {
                                    DependencyServiceManager.this.unsatisfiedDependencies.put(dependency, dependency.getBeanName());
                                }
                                Map<MandatoryServiceDependency, String> unsatisfiedDependenciesSnapshot = DependencyServiceManager.this.getUnsatisfiedDependencies();
                                if (debug) {
                                    log.debug((Object)("Unregistered dependency for " + contextToString + " adding " + dependency + "; total unsatisfied [" + unsatisfiedDependenciesSnapshot + "]"));
                                }
                                DependencyServiceManager.this.sendDependencyUnsatisfiedEvent(dependency);
                                DependencyServiceManager.this.sendBootstrappingDependenciesEvent(unsatisfiedDependenciesSnapshot.keySet());
                                continue block13;
                            }
                            if (!debug) continue block13;
                            log.debug((Object)("Decreasing the number of matching services for " + contextToString + "; " + dependency + " still has " + count + " matches left"));
                            continue block13;
                        }
                    }
                    if (!debug) continue;
                    log.debug((Object)("Unknown service event type for: " + dependency));
                    continue;
                }
                if (!trace) continue;
                log.trace((Object)(dependency + " does not match: " + referenceToString));
            }
        }
    }
}

