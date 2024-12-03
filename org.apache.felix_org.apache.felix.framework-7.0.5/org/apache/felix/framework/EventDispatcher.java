/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.ServiceRegistry;
import org.apache.felix.framework.util.ListenerInfo;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.ShrinkableCollection;
import org.apache.felix.framework.util.ShrinkableMap;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.AllServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.UnfilteredServiceListener;
import org.osgi.framework.hooks.bundle.EventHook;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.framework.hooks.service.ListenerHook;

public class EventDispatcher {
    private final Logger m_logger;
    private final ServiceRegistry m_registry;
    private Map<BundleContext, List<ListenerInfo>> m_fwkListeners = Collections.EMPTY_MAP;
    private Map<BundleContext, List<ListenerInfo>> m_bndlListeners = Collections.EMPTY_MAP;
    private Map<BundleContext, List<ListenerInfo>> m_syncBndlListeners = Collections.EMPTY_MAP;
    private Map<BundleContext, List<ListenerInfo>> m_svcListeners = Collections.EMPTY_MAP;
    private static Thread m_thread = null;
    private static final String m_threadLock = new String("thread lock");
    private static int m_references = 0;
    private static volatile boolean m_stopping = false;
    private static final List<Request> m_requestList = new ArrayList<Request>();
    private static final List<Request> m_requestPool = new ArrayList<Request>();
    private static final SecureAction m_secureAction = new SecureAction();

    public EventDispatcher(Logger logger, ServiceRegistry registry) {
        this.m_logger = logger;
        this.m_registry = registry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startDispatching() {
        String string = m_threadLock;
        synchronized (string) {
            if (m_thread == null || !m_thread.isAlive()) {
                m_stopping = false;
                m_thread = new Thread(new Runnable(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void run() {
                        try {
                            EventDispatcher.run();
                        }
                        finally {
                            String string = m_threadLock;
                            synchronized (string) {
                                m_thread = null;
                                m_stopping = false;
                                m_references = 0;
                                m_threadLock.notifyAll();
                            }
                        }
                    }
                }, "FelixDispatchQueue");
                m_thread.start();
            }
            ++m_references;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopDispatching() {
        Object object = m_threadLock;
        synchronized (object) {
            if (m_thread == null || m_stopping) {
                return;
            }
            if (--m_references > 0) {
                return;
            }
            m_stopping = true;
        }
        object = m_requestList;
        synchronized (object) {
            m_requestList.notify();
        }
        object = m_threadLock;
        synchronized (object) {
            while (m_thread != null) {
                try {
                    m_threadLock.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Filter addListener(BundleContext bc, Class clazz, EventListener l, Filter filter) {
        if (l == null) {
            throw new IllegalArgumentException("Listener is null");
        }
        if (!clazz.isInstance(l)) {
            throw new IllegalArgumentException("Listener not of type " + clazz.getName());
        }
        Filter oldFilter = this.updateListener(bc, clazz, l, filter);
        if (oldFilter != null) {
            return oldFilter;
        }
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            try {
                bc.getBundle();
            }
            catch (IllegalStateException ex) {
                return null;
            }
            Map<BundleContext, List<ListenerInfo>> listeners = null;
            Object acc = null;
            if (clazz == FrameworkListener.class) {
                listeners = this.m_fwkListeners;
            } else if (clazz == BundleListener.class) {
                listeners = SynchronousBundleListener.class.isInstance(l) ? this.m_syncBndlListeners : this.m_bndlListeners;
            } else if (clazz == ServiceListener.class) {
                SecurityManager sm = System.getSecurityManager();
                if (sm != null) {
                    acc = sm.getSecurityContext();
                }
                listeners = this.m_svcListeners;
            } else {
                throw new IllegalArgumentException("Unknown listener: " + l.getClass());
            }
            ListenerInfo info = new ListenerInfo(bc.getBundle(), bc, clazz, l, filter, acc, false);
            listeners = EventDispatcher.addListenerInfo(listeners, info);
            if (clazz == FrameworkListener.class) {
                this.m_fwkListeners = listeners;
            } else if (clazz == BundleListener.class) {
                if (SynchronousBundleListener.class.isInstance(l)) {
                    this.m_syncBndlListeners = listeners;
                } else {
                    this.m_bndlListeners = listeners;
                }
            } else if (clazz == ServiceListener.class) {
                this.m_svcListeners = listeners;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ListenerHook.ListenerInfo removeListener(BundleContext bc, Class clazz, EventListener l) {
        ListenerInfo returnInfo = null;
        if (l == null) {
            throw new IllegalArgumentException("Listener is null");
        }
        if (!clazz.isInstance(l)) {
            throw new IllegalArgumentException("Listener not of type " + clazz.getName());
        }
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            Map<BundleContext, List<ListenerInfo>> listeners = null;
            if (clazz == FrameworkListener.class) {
                listeners = this.m_fwkListeners;
            } else if (clazz == BundleListener.class) {
                listeners = SynchronousBundleListener.class.isInstance(l) ? this.m_syncBndlListeners : this.m_bndlListeners;
            } else if (clazz == ServiceListener.class) {
                listeners = this.m_svcListeners;
            } else {
                throw new IllegalArgumentException("Unknown listener: " + l.getClass());
            }
            int idx = -1;
            block3: for (Map.Entry<BundleContext, List<ListenerInfo>> entry : listeners.entrySet()) {
                List<ListenerInfo> infos = entry.getValue();
                for (int i = 0; i < infos.size(); ++i) {
                    ListenerInfo info = infos.get(i);
                    if (!info.getBundleContext().equals(bc) || info.getListenerClass() != clazz || info.getListener() != l) continue;
                    if (ServiceListener.class == clazz) {
                        returnInfo = new ListenerInfo(infos.get(i), true);
                    }
                    idx = i;
                    continue block3;
                }
            }
            if (idx >= 0) {
                listeners = EventDispatcher.removeListenerInfo(listeners, bc, idx);
            }
            if (clazz == FrameworkListener.class) {
                this.m_fwkListeners = listeners;
            } else if (clazz == BundleListener.class) {
                if (SynchronousBundleListener.class.isInstance(l)) {
                    this.m_syncBndlListeners = listeners;
                } else {
                    this.m_bndlListeners = listeners;
                }
            } else if (clazz == ServiceListener.class) {
                this.m_svcListeners = listeners;
            }
        }
        return returnInfo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeListeners(BundleContext bc) {
        if (bc == null) {
            return;
        }
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            this.m_fwkListeners = EventDispatcher.removeListenerInfos(this.m_fwkListeners, bc);
            this.m_bndlListeners = EventDispatcher.removeListenerInfos(this.m_bndlListeners, bc);
            this.m_syncBndlListeners = EventDispatcher.removeListenerInfos(this.m_syncBndlListeners, bc);
            this.m_svcListeners = EventDispatcher.removeListenerInfos(this.m_svcListeners, bc);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Filter updateListener(BundleContext bc, Class clazz, EventListener l, Filter filter) {
        if (clazz == ServiceListener.class) {
            EventDispatcher eventDispatcher = this;
            synchronized (eventDispatcher) {
                try {
                    bc.getBundle();
                }
                catch (IllegalStateException illegalStateException) {
                    // empty catch block
                }
                List<ListenerInfo> infos = this.m_svcListeners.get(bc);
                for (int i = 0; infos != null && i < infos.size(); ++i) {
                    ListenerInfo info = infos.get(i);
                    if (!info.getBundleContext().equals(bc) || info.getListenerClass() != clazz || info.getListener() != l) continue;
                    Filter oldFilter = info.getParsedFilter();
                    ListenerInfo newInfo = new ListenerInfo(info.getBundle(), info.getBundleContext(), info.getListenerClass(), info.getListener(), filter, info.getSecurityContext(), info.isRemoved());
                    this.m_svcListeners = EventDispatcher.updateListenerInfo(this.m_svcListeners, i, newInfo);
                    return oldFilter;
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Collection<ListenerHook.ListenerInfo> getAllServiceListeners() {
        ArrayList<ListenerHook.ListenerInfo> listeners = new ArrayList<ListenerHook.ListenerInfo>();
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            for (Map.Entry<BundleContext, List<ListenerInfo>> entry : this.m_svcListeners.entrySet()) {
                listeners.addAll((Collection<ListenerHook.ListenerInfo>)entry.getValue());
            }
        }
        return listeners;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireFrameworkEvent(FrameworkEvent event) {
        Map<BundleContext, List<ListenerInfo>> listeners = null;
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            listeners = this.m_fwkListeners;
        }
        EventDispatcher.fireEventAsynchronously(this, 0, listeners, event);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireBundleEvent(BundleEvent event, Felix felix) {
        Map<BundleContext, List<ListenerInfo>> listeners = null;
        Map<BundleContext, List<ListenerInfo>> syncListeners = null;
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            listeners = this.m_bndlListeners;
            syncListeners = this.m_syncBndlListeners;
        }
        Set<BundleContext> whitelist = this.createWhitelistFromHooks(event, felix, listeners, syncListeners, EventHook.class);
        if (whitelist != null) {
            List<ListenerInfo> infos;
            HashMap<BundleContext, List<ListenerInfo>> copy = new HashMap<BundleContext, List<ListenerInfo>>();
            for (BundleContext bc : whitelist) {
                infos = listeners.get(bc);
                if (infos == null) continue;
                copy.put(bc, infos);
            }
            listeners = copy;
            copy = new HashMap();
            for (BundleContext bc : whitelist) {
                infos = syncListeners.get(bc);
                if (infos == null) continue;
                copy.put(bc, infos);
            }
            syncListeners = copy;
        }
        EventDispatcher.fireEventImmediately(this, 1, syncListeners, event, null);
        if (event.getType() != 128 && event.getType() != 256 && event.getType() != 512) {
            EventDispatcher.fireEventAsynchronously(this, 1, listeners, event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireServiceEvent(ServiceEvent event, Dictionary oldProps, Felix felix) {
        Map<BundleContext, List<ListenerInfo>> listeners = null;
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            listeners = this.m_svcListeners;
        }
        listeners = this.filterListenersUsingHooks(event, felix, listeners);
        EventDispatcher.fireEventImmediately(this, 2, listeners, event, oldProps);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<BundleContext, List<ListenerInfo>> filterListenersUsingHooks(ServiceEvent event, Felix felix, Map<BundleContext, List<ListenerInfo>> listeners) {
        Set<ServiceReference<EventListenerHook>> elhs;
        Set<BundleContext> whitelist;
        Set<ServiceReference<org.osgi.framework.hooks.service.EventHook>> ehs = this.m_registry.getHookRegistry().getHooks(org.osgi.framework.hooks.service.EventHook.class);
        if (!ehs.isEmpty() && (whitelist = this.createWhitelistFromHooks(event, felix, listeners, null, org.osgi.framework.hooks.service.EventHook.class)) != null) {
            HashMap<BundleContext, List<ListenerInfo>> copy = new HashMap<BundleContext, List<ListenerInfo>>();
            for (BundleContext bc : whitelist) {
                copy.put(bc, listeners.get(bc));
            }
            listeners = copy;
        }
        if (!(elhs = this.m_registry.getHookRegistry().getHooks(EventListenerHook.class)).isEmpty()) {
            ArrayList systemBundleListeners = null;
            HashMap mutableMap = new HashMap();
            Map<BundleContext, Collection<ListenerHook.ListenerInfo>> shrinkableMap = new HashMap();
            for (Map.Entry<BundleContext, List<ListenerInfo>> entry : listeners.entrySet()) {
                BundleContext bc = entry.getKey();
                ArrayList mutableList = new ArrayList(entry.getValue());
                mutableMap.put(bc, mutableList);
                ArrayList ml = mutableList;
                ShrinkableCollection shrinkableCollection = new ShrinkableCollection(ml);
                shrinkableMap.put(bc, shrinkableCollection);
                if (bc != felix._getBundleContext()) continue;
                systemBundleListeners = new ArrayList(entry.getValue());
            }
            shrinkableMap = new ShrinkableMap(shrinkableMap);
            for (ServiceReference serviceReference : elhs) {
                if (felix == null) continue;
                EventListenerHook elh = null;
                try {
                    elh = (EventListenerHook)this.m_registry.getService(felix, serviceReference, false);
                }
                catch (Exception mutableList) {
                    // empty catch block
                }
                if (elh == null) continue;
                try {
                    m_secureAction.invokeServiceEventListenerHook(elh, event, shrinkableMap);
                }
                catch (Throwable th) {
                    this.m_logger.log(serviceReference, 2, "Problem invoking event hook", th);
                }
                finally {
                    this.m_registry.ungetService(felix, serviceReference, null);
                }
            }
            HashMap<BundleContext, List<ListenerInfo>> newMap = new HashMap<BundleContext, List<ListenerInfo>>();
            for (Map.Entry entry : shrinkableMap.entrySet()) {
                if (((Collection)entry.getValue()).isEmpty()) continue;
                newMap.put((BundleContext)entry.getKey(), (List<ListenerInfo>)mutableMap.get(entry.getKey()));
            }
            if (systemBundleListeners != null) {
                newMap.put(felix._getBundleContext(), systemBundleListeners);
            }
            listeners = newMap;
        }
        return listeners;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> Set<BundleContext> createWhitelistFromHooks(EventObject event, Felix felix, Map<BundleContext, List<ListenerInfo>> listeners1, Map<BundleContext, List<ListenerInfo>> listeners2, Class<T> hookClass) {
        HashSet<BundleContext> whitelist = null;
        Set<ServiceReference<T>> hooks = this.m_registry.getHookRegistry().getHooks(hookClass);
        if (!hooks.isEmpty()) {
            boolean systemBundleListener = false;
            BundleContext systemBundleContext = felix._getBundleContext();
            whitelist = new HashSet<BundleContext>();
            for (Map.Entry<BundleContext, List<ListenerInfo>> entry : listeners1.entrySet()) {
                whitelist.add(entry.getKey());
                if (entry.getKey() != systemBundleContext) continue;
                systemBundleListener = true;
            }
            if (listeners2 != null) {
                for (Map.Entry<BundleContext, List<ListenerInfo>> entry : listeners2.entrySet()) {
                    whitelist.add(entry.getKey());
                    if (entry.getKey() != systemBundleContext) continue;
                    systemBundleListener = true;
                }
            }
            int originalSize = whitelist.size();
            ShrinkableCollection<BundleContext> shrinkable = new ShrinkableCollection<BundleContext>(whitelist);
            for (ServiceReference<T> sr : hooks) {
                if (felix == null) continue;
                Object eh = null;
                try {
                    eh = this.m_registry.getService(felix, sr, false);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (eh == null) continue;
                try {
                    if (eh instanceof org.osgi.framework.hooks.service.EventHook) {
                        m_secureAction.invokeServiceEventHook(eh, (ServiceEvent)event, shrinkable);
                        continue;
                    }
                    if (!(eh instanceof EventHook)) continue;
                    m_secureAction.invokeBundleEventHook(eh, (BundleEvent)event, shrinkable);
                }
                catch (Throwable th) {
                    this.m_logger.log(sr, 2, "Problem invoking event hook", th);
                }
                finally {
                    this.m_registry.ungetService(felix, sr, null);
                }
            }
            if (systemBundleListener && !whitelist.contains(systemBundleContext)) {
                whitelist.add(systemBundleContext);
            }
            if (originalSize == whitelist.size()) {
                whitelist = null;
            }
        }
        return whitelist;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void fireEventAsynchronously(EventDispatcher dispatcher, int type, Map<BundleContext, List<ListenerInfo>> listeners, EventObject event) {
        if (m_stopping || m_thread == null) {
            return;
        }
        Request req = null;
        List<Request> list = m_requestPool;
        synchronized (list) {
            req = m_requestPool.size() > 0 ? m_requestPool.remove(0) : new Request();
        }
        req.m_dispatcher = dispatcher;
        req.m_type = type;
        req.m_listeners = listeners;
        req.m_event = event;
        list = m_requestList;
        synchronized (list) {
            m_requestList.add(req);
            m_requestList.notify();
        }
    }

    private static void fireEventImmediately(EventDispatcher dispatcher, int type, Map<BundleContext, List<ListenerInfo>> listeners, EventObject event, Dictionary oldProps) {
        if (!listeners.isEmpty()) {
            for (Map.Entry<BundleContext, List<ListenerInfo>> entry : listeners.entrySet()) {
                for (ListenerInfo info : entry.getValue()) {
                    Bundle bundle = info.getBundle();
                    EventListener l = info.getListener();
                    Filter filter = info.getParsedFilter();
                    Object acc = info.getSecurityContext();
                    try {
                        if (type == 0) {
                            EventDispatcher.invokeFrameworkListenerCallback(bundle, l, event);
                            continue;
                        }
                        if (type == 1) {
                            EventDispatcher.invokeBundleListenerCallback(bundle, l, event);
                            continue;
                        }
                        if (type != 2) continue;
                        EventDispatcher.invokeServiceListenerCallback(bundle, l, filter, acc, event, oldProps);
                    }
                    catch (Throwable th) {
                        if (type == 0 && ((FrameworkEvent)event).getType() == 2) continue;
                        dispatcher.m_logger.log(bundle, 1, "EventDispatcher: Error during dispatch.", th);
                        dispatcher.fireFrameworkEvent(new FrameworkEvent(2, bundle, th));
                    }
                }
            }
        }
    }

    private static void invokeFrameworkListenerCallback(Bundle bundle, final EventListener l, final EventObject event) {
        if (bundle.getState() == 8 || bundle.getState() == 32) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        ((FrameworkListener)l).frameworkEvent((FrameworkEvent)event);
                        return null;
                    }
                });
            } else {
                ((FrameworkListener)l).frameworkEvent((FrameworkEvent)event);
            }
        }
    }

    private static void invokeBundleListenerCallback(Bundle bundle, final EventListener l, final EventObject event) {
        if (SynchronousBundleListener.class.isAssignableFrom(l.getClass()) && (bundle.getState() == 8 || bundle.getState() == 16 || bundle.getState() == 32) || bundle.getState() == 8 || bundle.getState() == 32) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        ((BundleListener)l).bundleChanged((BundleEvent)event);
                        return null;
                    }
                });
            } else {
                ((BundleListener)l).bundleChanged((BundleEvent)event);
            }
        }
    }

    private static void invokeServiceListenerCallback(Bundle bundle, final EventListener l, Filter filter, Object acc, final EventObject event, Dictionary oldProps) {
        if (bundle.getState() != 8 && bundle.getState() != 16 && bundle.getState() != 32) {
            return;
        }
        ServiceReference<?> ref = ((ServiceEvent)event).getServiceReference();
        boolean hasPermission = true;
        SecurityManager sm = System.getSecurityManager();
        if (acc != null && sm != null) {
            try {
                ServicePermission perm = new ServicePermission(ref, "get");
                sm.checkPermission(perm, acc);
            }
            catch (Exception ex) {
                hasPermission = false;
            }
        }
        if (hasPermission) {
            boolean matched;
            if (l instanceof UnfilteredServiceListener) {
                matched = true;
            } else {
                boolean bl = matched = filter == null || filter.match(((ServiceEvent)event).getServiceReference());
            }
            if (matched) {
                if (l instanceof AllServiceListener || Util.isServiceAssignable(bundle, ((ServiceEvent)event).getServiceReference())) {
                    if (System.getSecurityManager() != null) {
                        AccessController.doPrivileged(new PrivilegedAction(){

                            public Object run() {
                                ((ServiceListener)l).serviceChanged((ServiceEvent)event);
                                return null;
                            }
                        });
                    } else {
                        ((ServiceListener)l).serviceChanged((ServiceEvent)event);
                    }
                }
            } else if (((ServiceEvent)event).getType() == 2 && filter.match(oldProps)) {
                final ServiceEvent se = new ServiceEvent(8, ((ServiceEvent)event).getServiceReference());
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(new PrivilegedAction(){

                        public Object run() {
                            ((ServiceListener)l).serviceChanged(se);
                            return null;
                        }
                    });
                } else {
                    ((ServiceListener)l).serviceChanged(se);
                }
            }
        }
    }

    private static Map<BundleContext, List<ListenerInfo>> addListenerInfo(Map<BundleContext, List<ListenerInfo>> listeners, ListenerInfo info) {
        HashMap<BundleContext, List<ListenerInfo>> copy = new HashMap<BundleContext, List<ListenerInfo>>(listeners);
        ArrayList<ListenerInfo> infos = (ArrayList<ListenerInfo>)copy.remove(info.getBundleContext());
        infos = infos == null ? new ArrayList<ListenerInfo>() : new ArrayList(infos);
        infos.add(info);
        copy.put(info.getBundleContext(), infos);
        return copy;
    }

    private static Map<BundleContext, List<ListenerInfo>> updateListenerInfo(Map<BundleContext, List<ListenerInfo>> listeners, int idx, ListenerInfo info) {
        HashMap<BundleContext, List<ListenerInfo>> copy = new HashMap<BundleContext, List<ListenerInfo>>(listeners);
        ArrayList<ListenerInfo> infos = (ArrayList<ListenerInfo>)copy.remove(info.getBundleContext());
        if (infos != null) {
            infos = new ArrayList<ListenerInfo>(infos);
            infos.set(idx, info);
            copy.put(info.getBundleContext(), infos);
            return copy;
        }
        return listeners;
    }

    private static Map<BundleContext, List<ListenerInfo>> removeListenerInfo(Map<BundleContext, List<ListenerInfo>> listeners, BundleContext bc, int idx) {
        HashMap<BundleContext, List<ListenerInfo>> copy = new HashMap<BundleContext, List<ListenerInfo>>(listeners);
        ArrayList infos = (ArrayList)copy.remove(bc);
        if (infos != null) {
            infos = new ArrayList(infos);
            infos.remove(idx);
            if (!infos.isEmpty()) {
                copy.put(bc, infos);
            }
            return copy;
        }
        return listeners;
    }

    private static Map<BundleContext, List<ListenerInfo>> removeListenerInfos(Map<BundleContext, List<ListenerInfo>> listeners, BundleContext bc) {
        HashMap<BundleContext, List<ListenerInfo>> copy = new HashMap<BundleContext, List<ListenerInfo>>(listeners);
        copy.remove(bc);
        return copy;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void run() {
        Request req = null;
        while (true) {
            List<Request> list = m_requestList;
            synchronized (list) {
                while (m_requestList.isEmpty() && !m_stopping) {
                    try {
                        m_requestList.wait();
                    }
                    catch (InterruptedException interruptedException) {}
                }
                if (m_requestList.isEmpty() && m_stopping) {
                    return;
                }
                req = m_requestList.remove(0);
            }
            EventDispatcher.fireEventImmediately(req.m_dispatcher, req.m_type, req.m_listeners, req.m_event, null);
            list = m_requestPool;
            synchronized (list) {
                req.m_dispatcher = null;
                req.m_type = -1;
                req.m_listeners = null;
                req.m_event = null;
                m_requestPool.add(req);
            }
        }
    }

    private static class Request {
        public static final int FRAMEWORK_EVENT = 0;
        public static final int BUNDLE_EVENT = 1;
        public static final int SERVICE_EVENT = 2;
        public EventDispatcher m_dispatcher = null;
        public int m_type = -1;
        public Map<BundleContext, List<ListenerInfo>> m_listeners = null;
        public EventObject m_event = null;

        private Request() {
        }
    }
}

