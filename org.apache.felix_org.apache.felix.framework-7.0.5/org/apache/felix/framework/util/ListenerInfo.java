/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.util.EventListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.hooks.service.ListenerHook;

public class ListenerInfo
implements ListenerHook.ListenerInfo {
    private final Bundle m_bundle;
    private final BundleContext m_context;
    private final Class m_listenerClass;
    private final EventListener m_listener;
    private final Filter m_filter;
    private final Object m_acc;
    private final boolean m_removed;

    public ListenerInfo(Bundle bundle, BundleContext context, Class listenerClass, EventListener listener, Filter filter, Object acc, boolean removed) {
        this.m_bundle = bundle;
        this.m_context = context;
        this.m_listenerClass = listenerClass;
        this.m_listener = listener;
        this.m_filter = filter;
        this.m_acc = acc;
        this.m_removed = removed;
    }

    public ListenerInfo(ListenerInfo info, boolean removed) {
        this.m_bundle = info.m_bundle;
        this.m_context = info.m_context;
        this.m_listenerClass = info.m_listenerClass;
        this.m_listener = info.m_listener;
        this.m_filter = info.m_filter;
        this.m_acc = info.m_acc;
        this.m_removed = removed;
    }

    public Bundle getBundle() {
        return this.m_bundle;
    }

    @Override
    public BundleContext getBundleContext() {
        return this.m_context;
    }

    public Class getListenerClass() {
        return this.m_listenerClass;
    }

    public EventListener getListener() {
        return this.m_listener;
    }

    public Filter getParsedFilter() {
        return this.m_filter;
    }

    @Override
    public String getFilter() {
        if (this.m_filter != null) {
            return this.m_filter.toString();
        }
        return null;
    }

    public Object getSecurityContext() {
        return this.m_acc;
    }

    @Override
    public boolean isRemoved() {
        return this.m_removed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ListenerInfo)) {
            return false;
        }
        ListenerInfo other = (ListenerInfo)obj;
        return other.m_bundle == this.m_bundle && other.m_context == this.m_context && other.m_listenerClass == this.m_listenerClass && other.m_listener == this.m_listener && (this.m_filter == null ? other.m_filter == null : this.m_filter.equals(other.m_filter));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.m_bundle != null ? this.m_bundle.hashCode() : 0);
        hash = 59 * hash + (this.m_context != null ? this.m_context.hashCode() : 0);
        hash = 59 * hash + (this.m_listenerClass != null ? this.m_listenerClass.hashCode() : 0);
        hash = 59 * hash + (this.m_listener != null ? this.m_listener.hashCode() : 0);
        hash = 59 * hash + (this.m_filter != null ? this.m_filter.hashCode() : 0);
        return hash;
    }
}

