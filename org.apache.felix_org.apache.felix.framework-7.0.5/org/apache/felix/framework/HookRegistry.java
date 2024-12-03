/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.net.ContentHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.felix.framework.ServiceRegistrationImpl;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.bundle.CollisionHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClassListener;
import org.osgi.service.url.URLStreamHandlerService;

public class HookRegistry {
    private static final Map<String, Class<?>> HOOK_CLASSES = new HashMap();
    private final Map<String, SortedSet<ServiceReference<?>>> m_allHooks = new ConcurrentHashMap();
    private final WeakHashMap<ServiceReference<?>, ServiceReference<?>> m_blackList = new WeakHashMap();

    private static void addHookClass(Class<?> c) {
        HOOK_CLASSES.put(c.getName(), c);
    }

    static boolean isHook(String[] classNames, Class<?> hookClass, Object svcObj) {
        for (String serviceName : classNames) {
            if (!serviceName.equals(hookClass.getName())) continue;
            if (svcObj instanceof ServiceFactory) {
                return true;
            }
            if (!hookClass.isAssignableFrom(svcObj.getClass())) continue;
            return true;
        }
        return false;
    }

    private boolean isHook(String serviceName, Object svcObj) {
        Class<?> hookClass = HOOK_CLASSES.get(serviceName);
        if (hookClass != null) {
            if (svcObj instanceof ServiceFactory) {
                return true;
            }
            if (hookClass.isAssignableFrom(svcObj.getClass())) {
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addHooks(String[] classNames, Object svcObj, ServiceReference<?> ref) {
        for (String serviceName : classNames) {
            if (!this.isHook(serviceName, svcObj)) continue;
            Map<String, SortedSet<ServiceReference<?>>> map = this.m_allHooks;
            synchronized (map) {
                SortedSet<ServiceReference<?>> hooks = this.m_allHooks.get(serviceName);
                hooks = hooks == null ? new TreeSet(Collections.reverseOrder()) : new TreeSet(hooks);
                hooks.add(ref);
                this.m_allHooks.put(serviceName, hooks);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateHooks(ServiceReference<?> ref) {
        String[] classNames;
        Object svcObj = ((ServiceRegistrationImpl.ServiceReferenceImpl)ref).getRegistration().getService();
        for (String serviceName : classNames = (String[])ref.getProperty("objectClass")) {
            if (!this.isHook(serviceName, svcObj)) continue;
            Map<String, SortedSet<ServiceReference<?>>> map = this.m_allHooks;
            synchronized (map) {
                SortedSet<ServiceReference<?>> hooks = this.m_allHooks.get(serviceName);
                if (hooks != null) {
                    TreeSet newHooks = new TreeSet(Collections.reverseOrder());
                    for (ServiceReference serviceReference : hooks) {
                        newHooks.add(serviceReference);
                    }
                    this.m_allHooks.put(serviceName, newHooks);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeHooks(ServiceReference<?> ref) {
        String[] classNames;
        Object svcObj = ((ServiceRegistrationImpl.ServiceReferenceImpl)ref).getRegistration().getService();
        for (String serviceName : classNames = (String[])ref.getProperty("objectClass")) {
            if (!this.isHook(serviceName, svcObj)) continue;
            Map<String, SortedSet<ServiceReference<?>>> map = this.m_allHooks;
            synchronized (map) {
                SortedSet<ServiceReference<?>> hooks = this.m_allHooks.get(serviceName);
                if (hooks != null) {
                    hooks = new TreeSet(hooks);
                    hooks.remove(ref);
                    this.m_allHooks.put(serviceName, hooks);
                }
            }
        }
        WeakHashMap<ServiceReference<?>, ServiceReference<?>> weakHashMap = this.m_blackList;
        synchronized (weakHashMap) {
            this.m_blackList.remove(ref);
        }
    }

    public <S> Set<ServiceReference<S>> getHooks(Class<S> hookClass) {
        Set hooks = this.m_allHooks.get(hookClass.getName());
        if (hooks != null) {
            return hooks;
        }
        return Collections.emptySet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isHookBlackListed(ServiceReference<?> sr) {
        WeakHashMap<ServiceReference<?>, ServiceReference<?>> weakHashMap = this.m_blackList;
        synchronized (weakHashMap) {
            return this.m_blackList.containsKey(sr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void blackListHook(ServiceReference<?> sr) {
        WeakHashMap<ServiceReference<?>, ServiceReference<?>> weakHashMap = this.m_blackList;
        synchronized (weakHashMap) {
            this.m_blackList.put(sr, sr);
        }
    }

    static {
        HookRegistry.addHookClass(CollisionHook.class);
        HookRegistry.addHookClass(org.osgi.framework.hooks.bundle.FindHook.class);
        HookRegistry.addHookClass(org.osgi.framework.hooks.bundle.EventHook.class);
        HookRegistry.addHookClass(EventHook.class);
        HookRegistry.addHookClass(EventListenerHook.class);
        HookRegistry.addHookClass(FindHook.class);
        HookRegistry.addHookClass(ListenerHook.class);
        HookRegistry.addHookClass(WeavingHook.class);
        HookRegistry.addHookClass(WovenClassListener.class);
        HookRegistry.addHookClass(ResolverHookFactory.class);
        HookRegistry.addHookClass(URLStreamHandlerService.class);
        HookRegistry.addHookClass(ContentHandler.class);
    }
}

