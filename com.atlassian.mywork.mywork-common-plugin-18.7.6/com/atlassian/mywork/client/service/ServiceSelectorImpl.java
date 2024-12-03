/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.base.Function
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceListener
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.fugue.Option;
import com.atlassian.mywork.service.HostService;
import com.atlassian.mywork.service.NotificationService;
import com.atlassian.mywork.service.ServiceSelector;
import com.atlassian.mywork.service.SystemStatusService;
import com.atlassian.mywork.util.Executors;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Function;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class ServiceSelectorImpl
implements ServiceSelector,
LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(ServiceSelectorImpl.class);
    private static final char FILTER_BEGIN = '(';
    private static final char FILTER_END = ')';
    private static final String FILTER_AND_CONSTRAINT = "(&";
    private static final String EQUALS = "=";
    private final PluginSettings pluginSettings;
    private final HostService hostService;
    private final BundleContext bundleContext;
    private ServiceListener hostAvailableListener;
    private volatile boolean hostAvailable = false;

    public ServiceSelectorImpl(PluginSettingsFactory pluginSettingsFactory, HostService hostService, SystemStatusService systemStatusService, BundleContext bundleContext) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.hostService = hostService;
        this.bundleContext = bundleContext;
        this.enableHost(hostService, systemStatusService);
    }

    public void onStart() {
        this.hostAvailableListener = new ServiceListener(){

            public void serviceChanged(ServiceEvent event) {
                switch (event.getType()) {
                    case 1: {
                        ServiceSelectorImpl.this.hostAvailable = true;
                        break;
                    }
                    case 4: {
                        ServiceSelectorImpl.this.hostAvailable = false;
                    }
                }
            }
        };
        ServiceSelectorImpl.addServiceListener(this.bundleContext, this.hostAvailableListener, ServiceSelectorImpl.unifyFilter(NotificationService.class, "(type=local)"));
    }

    private static String unifyFilter(Class clazz, String filter) {
        boolean moreThenOneClass;
        boolean filterHasText = StringUtils.hasText((String)filter);
        String[] items = new String[]{clazz.getName()};
        int itemName = items.length;
        for (int i = 0; i < items.length; ++i) {
            if (items[i] != null) continue;
            --itemName;
        }
        if (itemName == 0) {
            if (filterHasText) {
                return filter;
            }
            throw new IllegalArgumentException("at least one parameter has to be not-null");
        }
        if (filterHasText && (filter.charAt(0) != '(' || filter.charAt(filter.length() - 1) != ')')) {
            throw new IllegalArgumentException("invalid filter: " + filter);
        }
        StringBuffer buffer = new StringBuffer();
        if (filterHasText) {
            buffer.append(FILTER_AND_CONSTRAINT);
        }
        boolean bl = moreThenOneClass = itemName > 1;
        if (moreThenOneClass) {
            buffer.append(FILTER_AND_CONSTRAINT);
        }
        for (int i = 0; i < items.length; ++i) {
            if (items[i] == null) continue;
            buffer.append('(');
            buffer.append("objectClass");
            buffer.append(EQUALS);
            buffer.append(items[i]);
            buffer.append(')');
        }
        if (moreThenOneClass) {
            buffer.append(')');
        }
        if (filterHasText) {
            buffer.append(filter);
            buffer.append(')');
        }
        return buffer.toString();
    }

    private static void addServiceListener(BundleContext context, ServiceListener listener, String filter) {
        try {
            context.addServiceListener(listener, filter);
            ServiceReference[] alreadyRegistered = context.getServiceReferences((String)null, filter);
            if (alreadyRegistered != null) {
                for (int i = 0; i < alreadyRegistered.length; ++i) {
                    listener.serviceChanged(new ServiceEvent(1, alreadyRegistered[i]));
                }
            }
        }
        catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid filter", e);
        }
    }

    private void enableHost(final HostService hostService, SystemStatusService systemStatusService) {
        final ExecutorService myWorkHostChecker = Executors.newSingleThreadExecutor("myWorkHostChecker");
        systemStatusService.runWhenCompletelyUp(new Runnable(){

            @Override
            public void run() {
                try {
                    ServiceSelector.Target target = ServiceSelectorImpl.this.getTarget();
                    if (target == ServiceSelector.Target.REMOTE || target == ServiceSelector.Target.AUTO) {
                        log.debug("Target is " + target + ": Enabling HostService");
                        hostService.enable();
                    }
                }
                finally {
                    myWorkHostChecker.shutdown();
                }
            }
        }, myWorkHostChecker);
    }

    public Object createInstance(String clazz, final Object local, final Object remote) throws ClassNotFoundException {
        if (!local.getClass().getClassLoader().loadClass(clazz).isAssignableFrom(local.getClass())) {
            throw new IllegalArgumentException("local object " + local.getClass().getName() + " does not implement interface " + clazz);
        }
        if (!remote.getClass().getClassLoader().loadClass(clazz).isAssignableFrom(remote.getClass())) {
            throw new IllegalArgumentException("remote object " + local.getClass().getName() + " does not implement interface " + clazz);
        }
        return Proxy.newProxyInstance(local.getClass().getClassLoader(), new Class[]{local.getClass().getClassLoader().loadClass(clazz)}, new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                ServiceSelector.Target target = ServiceSelectorImpl.this.getEffectiveTarget();
                log.debug("Delegating to " + target);
                try {
                    if (target == ServiceSelector.Target.LOCAL) {
                        return method.invoke(local, args);
                    }
                    if (target == ServiceSelector.Target.REMOTE) {
                        return method.invoke(remote, args);
                    }
                    return null;
                }
                catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        });
    }

    @Override
    public ServiceSelector.Target getEffectiveTarget() {
        ServiceSelector.Target target = this.getTarget();
        ServiceSelector.Target effectiveTarget = this.findEffectiveTarget(target);
        log.trace("Target = {}, EffectiveTarget = {}", (Object)target, (Object)effectiveTarget);
        return effectiveTarget;
    }

    private ServiceSelector.Target findEffectiveTarget(ServiceSelector.Target target) {
        if (this.isHostAvailable() && target == ServiceSelector.Target.LOCAL) {
            return ServiceSelector.Target.LOCAL;
        }
        if (this.isClientAvailable() && (target == ServiceSelector.Target.REMOTE || target == ServiceSelector.Target.AUTO)) {
            return ServiceSelector.Target.REMOTE;
        }
        return ServiceSelector.Target.NONE;
    }

    @Override
    public ServiceSelector.Target getTarget() {
        return (ServiceSelector.Target)((Object)Option.option((Object)((String)this.pluginSettings.get("com.atlassian.mywork.target"))).map((Function)new Function<String, ServiceSelector.Target>(){

            public ServiceSelector.Target apply(String targetSetting) {
                return ServiceSelector.Target.valueOf(targetSetting);
            }
        }).getOrElse((Object)(this.isHostAvailable() ? ServiceSelector.Target.LOCAL : ServiceSelector.Target.AUTO)));
    }

    @Override
    public void setTarget(ServiceSelector.Target target, ApplicationId host) {
        log.debug("Setting hostTarget to Target: {}, ApplicationId: {}", (Object)target, (Object)host);
        if (target == ServiceSelector.Target.REMOTE && host == null) {
            throw new IllegalArgumentException("Host should be specified when setting target to REMOTE");
        }
        this.pluginSettings.put("com.atlassian.mywork.target", (Object)target.name());
        if (target != ServiceSelector.Target.REMOTE && target != ServiceSelector.Target.AUTO) {
            this.hostService.disable();
        }
        this.hostService.setSelectedHost((ApplicationId)(target == ServiceSelector.Target.REMOTE ? host : null));
        if (target == ServiceSelector.Target.REMOTE || target == ServiceSelector.Target.AUTO) {
            this.hostService.enable();
        }
    }

    @Override
    public boolean isHostAvailable() {
        log.trace("HostAvailable = {}", (Object)this.hostAvailable);
        return this.hostAvailable;
    }

    private boolean isClientAvailable() {
        boolean clientAvailable = this.hostService.getRegisteredHost().isDefined();
        log.trace("ClientAvailable: {}", (Object)clientAvailable);
        return clientAvailable;
    }

    public void onStop() {
        if (this.bundleContext != null && this.hostAvailableListener != null) {
            try {
                this.bundleContext.removeServiceListener(this.hostAvailableListener);
            }
            catch (IllegalStateException e) {
                if (log.isDebugEnabled()) {
                    log.debug("IllegalStateException while invoking bundleContext.removeServiceListener()", (Throwable)e);
                }
                log.warn("IllegalStateException while invoking bundleContext.removeServiceListener(). Turn on debug logging for " + log.getName() + " to see the full stacktrace");
            }
        }
    }
}

