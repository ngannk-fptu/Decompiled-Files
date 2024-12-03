/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.util.concurrent.LazyReference
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.macro.LazyLoadingMacroWrapper;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.ResourceAware;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.MacroRegistrationHelper;
import com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.util.concurrent.LazyReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XhtmlMacroManager
implements MacroManager {
    private static final Logger log = LoggerFactory.getLogger(XhtmlMacroManager.class);
    public static final String RESOURCE_PREFIX = "/download/resources/";
    private final Map<String, Macro> macros = new ConcurrentHashMap<String, Macro>();
    private final MacroRegistrationHelper registrationHelper;

    public XhtmlMacroManager(EventPublisher eventPublisher) {
        this.registrationHelper = new MacroRegistrationHelper(XhtmlMacroModuleDescriptor.class, (MacroManager)this, eventPublisher);
    }

    @Override
    public Macro getMacroByName(@NonNull String macroName) {
        Macro macro = this.macros.get(macroName);
        if (macro instanceof LazyLoadingMacroWrapper) {
            return ((LazyLoadingMacroWrapper)macro).getMacro();
        }
        return macro;
    }

    public Map<String, Macro> getMacros() {
        return Collections.unmodifiableMap(this.macros);
    }

    @Override
    public void registerMacro(@NonNull String name, @NonNull Macro macro) {
        log.debug("Registering macro: {}", (Object)name);
        this.macros.put(name, macro);
    }

    @Override
    public void unregisterMacro(@NonNull String name) {
        log.debug("Unregistering macro: {}", (Object)name);
        this.macros.remove(name);
    }

    @PluginEventListener
    public void pluginModuleEnabled(PluginModuleEnabledEvent event) {
        this.registrationHelper.pluginModuleEnabled(event.getModule());
    }

    @PluginEventListener
    public void pluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.registrationHelper.pluginModuleDisabled(event.getModule());
    }

    @Override
    @Deprecated
    public LazyReference<Macro> createLazyMacroReference(final ModuleDescriptor<?> moduleDescriptor) {
        return new LazyReference<Macro>(){

            protected Macro create() throws Exception {
                Macro macro = ((XhtmlMacroModuleDescriptor)moduleDescriptor).getModule();
                if (!(macro instanceof ResourceAware)) {
                    macro = XhtmlMacroManager.createResourceAwareMacroProxy(macro);
                }
                ((ResourceAware)((Object)macro)).setResourcePath(XhtmlMacroManager.RESOURCE_PREFIX + HtmlUtil.urlEncode(moduleDescriptor.getCompleteKey()));
                return macro;
            }
        };
    }

    public void setPluginEventManager(PluginEventManager pluginEventManager) {
        pluginEventManager.register((Object)this);
    }

    public static Macro unwrapMacroProxy(Macro proxy) {
        InvocationHandler ih;
        if (Proxy.isProxyClass(proxy.getClass()) && (ih = Proxy.getInvocationHandler(proxy)) instanceof ResourceAwareMacroInvocationHandler) {
            return ((ResourceAwareMacroInvocationHandler)ih).macro;
        }
        return proxy;
    }

    private static Macro createResourceAwareMacroProxy(Macro macro) {
        Set<Class<?>> interfaces = XhtmlMacroManager.getAllInterfaces(macro.getClass());
        interfaces.add(ResourceAware.class);
        return (Macro)Proxy.newProxyInstance(macro.getClass().getClassLoader(), interfaces.toArray(macro.getClass().getInterfaces()), (InvocationHandler)new ResourceAwareMacroInvocationHandler(macro));
    }

    private static Set<Class<?>> getInterfaces(Class<?> aClass) {
        if (aClass == null) {
            return Collections.emptySet();
        }
        HashSet interfaces = new HashSet(Arrays.asList(aClass.getInterfaces()));
        for (Class<?> iface : aClass.getInterfaces()) {
            interfaces.addAll(XhtmlMacroManager.getInterfaces(iface));
        }
        return interfaces;
    }

    private static Set<Class<?>> getAllInterfaces(Class<?> aClass) {
        if (aClass == null) {
            return Collections.emptySet();
        }
        Set<Class<?>> interfaces = XhtmlMacroManager.getInterfaces(aClass);
        interfaces.addAll(XhtmlMacroManager.getAllInterfaces(aClass.getSuperclass()));
        return interfaces;
    }

    private static class ResourceAwareMacroInvocationHandler
    implements InvocationHandler {
        private Macro macro;
        private String resourcePath;

        public ResourceAwareMacroInvocationHandler(Macro macro) {
            this.macro = macro;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("setResourcePath") && args.length == 1 && args[0] instanceof String) {
                this.resourcePath = (String)args[0];
                return null;
            }
            if (method.getName().equals("getResourcePath") && args.length == 0) {
                return this.resourcePath;
            }
            try {
                Method macroMethod = this.macro.getClass().getMethod(method.getName(), method.getParameterTypes());
                return macroMethod.invoke((Object)this.macro, args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        }
    }
}

