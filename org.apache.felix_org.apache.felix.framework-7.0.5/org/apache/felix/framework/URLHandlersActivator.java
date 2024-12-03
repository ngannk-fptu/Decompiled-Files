/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.net.ContentHandler;
import java.util.Map;
import java.util.Set;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.URLHandlers;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.url.URLStreamHandlerService;

class URLHandlersActivator
implements BundleActivator {
    private final Map m_configMap;
    private final Felix m_framework;

    public URLHandlersActivator(Map configMap, Felix framework) {
        this.m_configMap = configMap;
        this.m_framework = framework;
    }

    @Override
    public void start(BundleContext context) {
        boolean enable;
        boolean bl = this.m_configMap.get("felix.service.urlhandlers") == null ? true : (enable = !this.m_configMap.get("felix.service.urlhandlers").equals("false"));
        if (enable) {
            this.m_framework.setURLHandlersActivator(this);
        }
        URLHandlers.registerFrameworkInstance(this.m_framework, enable);
    }

    @Override
    public void stop(BundleContext context) {
        URLHandlers.unregisterFrameworkInstance(this.m_framework);
        this.m_framework.setURLHandlersActivator(null);
    }

    protected Object getStreamHandlerService(String protocol) {
        return this.get(this.m_framework.getHookRegistry().getHooks(URLStreamHandlerService.class), "url.handler.protocol", protocol);
    }

    protected Object getContentHandlerService(String mimeType) {
        return this.get(this.m_framework.getHookRegistry().getHooks(ContentHandler.class), "url.content.mimetype", mimeType);
    }

    private <S> S get(Set<ServiceReference<S>> hooks, String key, String value) {
        Object service = null;
        if (!hooks.isEmpty()) {
            for (ServiceReference<S> ref : hooks) {
                Object values = ref.getProperty(key);
                if (values instanceof String[]) {
                    for (int valueIdx = 0; valueIdx < ((String[])values).length && service == null; ++valueIdx) {
                        if (!value.equals(((String[])values)[valueIdx])) continue;
                        return this.m_framework.getService(this.m_framework, ref, false);
                    }
                    continue;
                }
                if (!value.equals(values)) continue;
                return this.m_framework.getService(this.m_framework, ref, false);
            }
        }
        return null;
    }
}

