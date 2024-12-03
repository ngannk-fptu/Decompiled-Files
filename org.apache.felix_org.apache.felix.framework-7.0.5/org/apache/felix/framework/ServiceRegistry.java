/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.felix.framework.HookRegistry;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.ServiceRegistrationImpl;
import org.apache.felix.framework.capabilityset.CapabilitySet;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.wiring.BundleCapabilityImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.resource.Capability;

public class ServiceRegistry {
    private final Logger m_logger;
    private final AtomicLong m_currentServiceId = new AtomicLong(1L);
    private final ConcurrentMap<Bundle, List<ServiceRegistration<?>>> m_regsMap = new ConcurrentHashMap();
    private final CapabilitySet m_regCapSet = new CapabilitySet(Collections.singletonList("objectClass"), false);
    private final ConcurrentMap<Bundle, UsageCount[]> m_inUseMap = new ConcurrentHashMap<Bundle, UsageCount[]>();
    private final ServiceRegistryCallbacks m_callbacks;
    private final HookRegistry hookRegistry = new HookRegistry();

    public ServiceRegistry(Logger logger, ServiceRegistryCallbacks callbacks) {
        this.m_logger = logger;
        this.m_callbacks = callbacks;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServiceReference<?>[] getRegisteredServices(Bundle bundle) {
        List regs = (List)this.m_regsMap.get(bundle);
        if (regs != null) {
            ArrayList refs = new ArrayList(regs.size());
            List list = regs;
            synchronized (list) {
                for (ServiceRegistration reg : regs) {
                    try {
                        refs.add(reg.getReference());
                    }
                    catch (IllegalStateException illegalStateException) {}
                }
            }
            return refs.toArray(new ServiceReference[refs.size()]);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ServiceRegistration<?> registerService(Bundle bundle, String[] classNames, Object svcObj, Dictionary<?, ?> dict) {
        ServiceRegistrationImpl reg = new ServiceRegistrationImpl(this, bundle, classNames, this.m_currentServiceId.getAndIncrement(), svcObj, dict);
        this.hookRegistry.addHooks(classNames, svcObj, reg.getReference());
        ArrayList<ServiceRegistrationImpl> newRegs = new ArrayList<ServiceRegistrationImpl>();
        ArrayList<ServiceRegistrationImpl> regs = this.m_regsMap.putIfAbsent(bundle, newRegs);
        if (regs == null) {
            regs = newRegs;
        }
        ArrayList<ServiceRegistrationImpl> arrayList = regs;
        synchronized (arrayList) {
            regs.add(reg);
        }
        this.m_regCapSet.addCapability((BundleCapabilityImpl)((Object)reg.getReference()));
        return reg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unregisterService(Bundle bundle, ServiceRegistration<?> reg) {
        this.hookRegistry.removeHooks(reg.getReference());
        List regs = (List)this.m_regsMap.get(bundle);
        if (regs != null) {
            List list = regs;
            synchronized (list) {
                regs.remove(reg);
            }
        }
        this.m_regCapSet.removeCapability((BundleCapabilityImpl)((Object)reg.getReference()));
        if (this.m_callbacks != null) {
            this.m_callbacks.serviceChanged(new ServiceEvent(4, reg.getReference()), null);
        }
        ServiceReference<?> ref = reg.getReference();
        this.ungetServices(ref);
        ((ServiceRegistrationImpl)reg).invalidate();
        this.ungetServices(ref);
        for (Bundle usingBundle : this.m_inUseMap.keySet()) {
            this.flushUsageCount(usingBundle, ref, null);
        }
    }

    private void ungetServices(ServiceReference<?> ref) {
        Bundle[] clients = this.getUsingBundles(ref);
        for (int i = 0; clients != null && i < clients.length; ++i) {
            UsageCount[] usages = (UsageCount[])this.m_inUseMap.get(clients[i]);
            for (int x = 0; usages != null && x < usages.length; ++x) {
                if (!usages[x].m_ref.equals(ref)) continue;
                this.ungetService(clients[i], ref, usages[x].m_prototype ? usages[x].getService() : null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unregisterServices(Bundle bundle) {
        List regs = (List)this.m_regsMap.remove(bundle);
        if (regs != null) {
            ArrayList copyRefs;
            List list = regs;
            synchronized (list) {
                copyRefs = new ArrayList(regs);
            }
            for (ServiceRegistration reg : copyRefs) {
                if (!((ServiceRegistrationImpl)reg).isValid()) continue;
                try {
                    reg.unregister();
                }
                catch (IllegalStateException illegalStateException) {}
            }
        }
    }

    public Collection<Capability> getServiceReferences(String className, SimpleFilter filter) {
        if (className == null && filter == null) {
            filter = new SimpleFilter(null, null, 0);
        } else if (className != null && filter == null) {
            filter = new SimpleFilter("objectClass", className, 4);
        } else if (className != null && filter != null) {
            ArrayList<SimpleFilter> filters = new ArrayList<SimpleFilter>(2);
            filters.add(new SimpleFilter("objectClass", className, 4));
            filters.add(filter);
            filter = new SimpleFilter(null, filters, 1);
        }
        return this.m_regCapSet.match(filter, false);
    }

    public ServiceReference<?>[] getServicesInUse(Bundle bundle) {
        UsageCount[] usages = (UsageCount[])this.m_inUseMap.get(bundle);
        if (usages != null) {
            ServiceReference[] refs = new ServiceReference[usages.length];
            int count = 0;
            for (int i = 0; i < usages.length; ++i) {
                if (usages[i].m_count.get() <= 0L) continue;
                refs[count++] = usages[i].m_ref;
            }
            if (count == usages.length) {
                return refs;
            }
            if (count == 0) {
                return null;
            }
            ServiceReference[] nrefs = new ServiceReference[count];
            System.arraycopy(refs, 0, nrefs, 0, count);
            return nrefs;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <S> S getService(Bundle bundle, ServiceReference<S> ref, boolean isServiceObjects) {
        boolean isPrototype = isServiceObjects && ref.getProperty("service.scope") == "prototype";
        UsageCount usage = null;
        Object svcObj = null;
        ServiceRegistrationImpl reg = ((ServiceRegistrationImpl.ServiceReferenceImpl)ref).getRegistration();
        if (reg.currentThreadMarked()) {
            throw new ServiceException("ServiceFactory.getService() resulted in a cycle.", 2, null);
        }
        try {
            reg.markCurrentThread();
            if (reg.isValid()) {
                usage = this.obtainUsageCount(bundle, ref, null, isPrototype);
                this.incrementToPositiveValue(usage.m_count);
                svcObj = usage.getService();
                if (isServiceObjects) {
                    this.incrementToPositiveValue(usage.m_serviceObjectsCount);
                }
                if (usage != null) {
                    UsageCount existingUsage;
                    ServiceHolder holder = null;
                    while (holder == null) {
                        ServiceHolder h = new ServiceHolder();
                        if (usage.m_svcHolderRef.compareAndSet(null, h)) {
                            holder = h;
                            try {
                                holder.m_service = svcObj = reg.getService(bundle);
                            }
                            finally {
                                holder.m_latch.countDown();
                            }
                        } else {
                            holder = usage.m_svcHolderRef.get();
                            if (holder != null) {
                                boolean interrupted = false;
                                do {
                                    try {
                                        holder.m_latch.await();
                                        if (interrupted) {
                                            Thread.currentThread().interrupt();
                                        }
                                        interrupted = false;
                                    }
                                    catch (InterruptedException e) {
                                        interrupted = true;
                                        Thread.interrupted();
                                    }
                                } while (interrupted);
                                svcObj = holder.m_service;
                            }
                        }
                        if (holder == usage.m_svcHolderRef.get()) continue;
                        holder = null;
                    }
                    if (svcObj != null && isPrototype && (existingUsage = this.obtainUsageCount(bundle, ref, svcObj, null)) != null && existingUsage != usage) {
                        this.flushUsageCount(bundle, ref, usage);
                        usage = existingUsage;
                        this.incrementToPositiveValue(usage.m_count);
                        if (isServiceObjects) {
                            this.incrementToPositiveValue(usage.m_serviceObjectsCount);
                        }
                    }
                }
            }
            reg.unmarkCurrentThread();
            if (!reg.isValid() || svcObj == null) {
                this.flushUsageCount(bundle, ref, usage);
            }
        }
        catch (Throwable throwable) {
            reg.unmarkCurrentThread();
            if (!reg.isValid() || svcObj == null) {
                this.flushUsageCount(bundle, ref, usage);
            }
            throw throwable;
        }
        return (S)svcObj;
    }

    private void incrementToPositiveValue(AtomicLong al) {
        boolean success = false;
        while (!success) {
            long oldVal = al.get();
            long newVal = Math.max(oldVal + 1L, 1L);
            this.checkCountOverflow(newVal);
            success = al.compareAndSet(oldVal, newVal);
        }
    }

    private void checkCountOverflow(long c) {
        if (c == Long.MAX_VALUE) {
            throw new ServiceException("The use count for the service overflowed.", 0, null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean ungetService(Bundle bundle, ServiceReference<?> ref, Object svcObj) {
        ServiceRegistrationImpl reg = ((ServiceRegistrationImpl.ServiceReferenceImpl)ref).getRegistration();
        if (reg.currentThreadMarked()) {
            throw new IllegalStateException("ServiceFactory.ungetService() resulted in a cycle.");
        }
        try {
            reg.markCurrentThread();
            UsageCount usage = this.obtainUsageCount(bundle, ref, svcObj, null);
            if (usage == null) {
                boolean bl = false;
                return bl;
            }
            if (svcObj != null && usage.m_serviceObjectsCount.decrementAndGet() < 0L) {
                boolean bl = false;
                return bl;
            }
            long count = usage.m_count.decrementAndGet();
            try {
                boolean bl;
                if (count <= 0L) {
                    Object svc;
                    ServiceHolder holder = usage.m_svcHolderRef.get();
                    Object object = svc = holder != null ? holder.m_service : null;
                    if (svc != null && usage.m_svcHolderRef.compareAndSet(holder, null) && usage.m_count.get() <= 0L) {
                        usage.m_count.incrementAndGet();
                        try {
                            reg.ungetService(bundle, svc);
                        }
                        finally {
                            usage.m_count.decrementAndGet();
                        }
                    }
                }
                boolean bl2 = bl = count >= 0L;
                if (!reg.isValid()) {
                    usage.m_svcHolderRef.set(null);
                }
                if (!reg.isValid() || count <= 0L && svcObj != null) {
                    this.flushUsageCount(bundle, ref, usage);
                }
                return bl;
            }
            catch (Throwable throwable) {
                if (!reg.isValid()) {
                    usage.m_svcHolderRef.set(null);
                }
                if (!reg.isValid() || count <= 0L && svcObj != null) {
                    this.flushUsageCount(bundle, ref, usage);
                }
                throw throwable;
            }
        }
        finally {
            reg.unmarkCurrentThread();
        }
    }

    public void ungetServices(Bundle bundle) {
        UsageCount[] usages = (UsageCount[])this.m_inUseMap.get(bundle);
        if (usages == null) {
            return;
        }
        for (int i = 0; i < usages.length; ++i) {
            if (usages[i].m_svcHolderRef.get() == null) continue;
            while (this.ungetService(bundle, usages[i].m_ref, usages[i].m_prototype ? usages[i].getService() : null)) {
            }
        }
    }

    public Bundle[] getUsingBundles(ServiceReference<?> ref) {
        Bundle[] bundles = null;
        for (Map.Entry entry : this.m_inUseMap.entrySet()) {
            Bundle bundle = (Bundle)entry.getKey();
            UsageCount[] usages = (UsageCount[])entry.getValue();
            for (int useIdx = 0; useIdx < usages.length; ++useIdx) {
                if (!usages[useIdx].m_ref.equals(ref) || usages[useIdx].m_count.get() <= 0L) continue;
                if (bundles == null) {
                    bundles = new Bundle[]{bundle};
                    continue;
                }
                Bundle[] nbs = new Bundle[bundles.length + 1];
                System.arraycopy(bundles, 0, nbs, 0, bundles.length);
                nbs[bundles.length] = bundle;
                bundles = nbs;
            }
        }
        return bundles;
    }

    void servicePropertiesModified(ServiceRegistration<?> reg, Dictionary<?, ?> oldProps) {
        this.hookRegistry.updateHooks(reg.getReference());
        if (this.m_callbacks != null) {
            this.m_callbacks.serviceChanged(new ServiceEvent(2, reg.getReference()), oldProps);
        }
    }

    public Logger getLogger() {
        return this.m_logger;
    }

    UsageCount obtainUsageCount(Bundle bundle, ServiceReference<?> ref, Object svcObj, Boolean isPrototype) {
        UsageCount usage = null;
        boolean success = false;
        while (!success) {
            UsageCount[] newUsages;
            UsageCount[] usages = (UsageCount[])this.m_inUseMap.get(bundle);
            if (!Boolean.TRUE.equals(isPrototype)) {
                for (int i = 0; usages != null && i < usages.length; ++i) {
                    if (!usages[i].m_ref.equals(ref) || (svcObj != null || usages[i].m_prototype) && usages[i].getService() != svcObj) continue;
                    return usages[i];
                }
            }
            if (isPrototype == null) {
                return null;
            }
            usage = new UsageCount(ref, isPrototype);
            if (usages == null) {
                newUsages = new UsageCount[]{usage};
                success = this.m_inUseMap.putIfAbsent(bundle, newUsages) == null;
                continue;
            }
            newUsages = new UsageCount[usages.length + 1];
            System.arraycopy(usages, 0, newUsages, 0, usages.length);
            newUsages[usages.length] = usage;
            success = this.m_inUseMap.replace(bundle, usages, newUsages);
        }
        return usage;
    }

    void flushUsageCount(Bundle bundle, ServiceReference<?> ref, UsageCount uc) {
        boolean success = false;
        while (!success) {
            UsageCount[] usages;
            UsageCount[] orgUsages = usages = (UsageCount[])this.m_inUseMap.get(bundle);
            for (int i = 0; usages != null && i < usages.length; ++i) {
                if ((uc != null || !usages[i].m_ref.equals(ref)) && uc != usages[i]) continue;
                if (usages.length - 1 == 0) {
                    usages = null;
                    continue;
                }
                UsageCount[] newUsages = new UsageCount[usages.length - 1];
                System.arraycopy(usages, 0, newUsages, 0, i);
                if (i < newUsages.length) {
                    System.arraycopy(usages, i + 1, newUsages, i, newUsages.length - i);
                }
                usages = newUsages;
                --i;
            }
            if (usages == orgUsages) {
                return;
            }
            if (orgUsages == null) continue;
            if (usages != null) {
                success = this.m_inUseMap.replace(bundle, orgUsages, usages);
                continue;
            }
            success = this.m_inUseMap.remove(bundle, orgUsages);
        }
    }

    public HookRegistry getHookRegistry() {
        return this.hookRegistry;
    }

    public static interface ServiceRegistryCallbacks {
        public void serviceChanged(ServiceEvent var1, Dictionary<?, ?> var2);
    }

    static class ServiceHolder {
        final CountDownLatch m_latch = new CountDownLatch(1);
        volatile Object m_service;

        ServiceHolder() {
        }
    }

    static class UsageCount {
        final ServiceReference<?> m_ref;
        final boolean m_prototype;
        final AtomicLong m_count = new AtomicLong();
        final AtomicLong m_serviceObjectsCount = new AtomicLong();
        final AtomicReference<ServiceHolder> m_svcHolderRef = new AtomicReference();

        UsageCount(ServiceReference<?> ref, boolean isPrototype) {
            this.m_ref = ref;
            this.m_prototype = isPrototype;
        }

        Object getService() {
            ServiceHolder sh = this.m_svcHolderRef.get();
            return sh == null ? null : sh.m_service;
        }
    }
}

