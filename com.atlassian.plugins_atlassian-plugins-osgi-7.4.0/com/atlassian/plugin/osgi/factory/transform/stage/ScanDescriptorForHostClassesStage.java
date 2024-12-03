/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  com.atlassian.plugin.util.PluginUtils
 *  org.dom4j.Attribute
 *  org.dom4j.DocumentHelper
 *  org.dom4j.XPath
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.atlassian.plugin.util.PluginUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanDescriptorForHostClassesStage
implements TransformStage {
    private static final Logger log = LoggerFactory.getLogger(ScanDescriptorForHostClassesStage.class);

    @Override
    public void execute(TransformContext context) {
        XPath xpath = DocumentHelper.createXPath((String)"//@class");
        List nodes = xpath.selectNodes((Object)context.getDescriptorDocument());
        List attributes = nodes.stream().filter(Attribute.class::isInstance).map(Attribute.class::cast).collect(Collectors.toList());
        HashMap<HostComponentRegistration, RequiredComponentRegistration> requiredRegistrationMap = new HashMap<HostComponentRegistration, RequiredComponentRegistration>();
        for (Attribute attr : attributes) {
            String className = attr.getValue();
            this.scanForHostComponents(context, className, requiredRegistrationMap);
            int dotpos = className.lastIndexOf(46);
            if (dotpos <= -1) continue;
            String pkg = className.substring(0, dotpos);
            String pkgPath = pkg.replace('.', '/') + '/';
            if (!context.getSystemExports().isExported(pkg)) continue;
            if (context.getPluginArtifact().doesResourceExist(pkgPath)) {
                if (!PluginUtils.isAtlassianDevMode()) continue;
                log.warn("The plugin '{}' uses a package '{}' that is also exported by the application. It is highly recommended that the plugin use its own packages.", (Object)context.getPluginArtifact(), (Object)pkg);
                continue;
            }
            context.getExtraImports().add(pkg);
        }
        requiredRegistrationMap.values().forEach(context::addRequiredHostComponent);
    }

    private void declareRequiredHostComponent(HostComponentRegistration hostComponent, Class<?> requiredInterface, Map<HostComponentRegistration, RequiredComponentRegistration> requiredRegistrationMap) {
        RequiredComponentRegistration registration = requiredRegistrationMap.computeIfAbsent(hostComponent, RequiredComponentRegistration::new);
        registration.addInterface(requiredInterface);
    }

    private void scanForHostComponents(TransformContext context, String className, Map<HostComponentRegistration, RequiredComponentRegistration> requiredRegistrationMap) {
        Class cls;
        if (className != null && className.contains(":")) {
            return;
        }
        LinkedHashMap<Class<?>[], HostComponentRegistration> hostComponentInterfaces = new LinkedHashMap<Class<?>[], HostComponentRegistration>();
        for (HostComponentRegistration registration : context.getHostComponentRegistrations()) {
            Class<?>[] classArray = registration.getMainInterfaceClasses();
            int n = classArray.length;
            for (int i = 0; i < n; ++i) {
                Class<?>[] cls2 = classArray[i];
                hostComponentInterfaces.put(cls2, registration);
            }
        }
        try {
            cls = ClassLoaderUtils.loadClass((String)className, this.getClass());
        }
        catch (ClassNotFoundException e) {
            return;
        }
        for (Constructor<?> constructor : cls.getConstructors()) {
            for (Class<?> ctorParam : constructor.getParameterTypes()) {
                if (!hostComponentInterfaces.containsKey(ctorParam)) continue;
                this.declareRequiredHostComponent((HostComponentRegistration)hostComponentInterfaces.get(ctorParam), ctorParam, requiredRegistrationMap);
            }
        }
        for (Executable executable : cls.getMethods()) {
            if (!((Method)executable).getName().startsWith("set") || ((Method)executable).getParameterTypes().length != 1 || !hostComponentInterfaces.containsKey(((Method)executable).getParameterTypes()[0])) continue;
            Class<?> parameterType = ((Method)executable).getParameterTypes()[0];
            this.declareRequiredHostComponent((HostComponentRegistration)hostComponentInterfaces.get(parameterType), parameterType, requiredRegistrationMap);
        }
    }

    private static class RequiredComponentRegistration
    implements HostComponentRegistration {
        private final HostComponentRegistration delegate;
        private final Set<Class<?>> interfaces;

        RequiredComponentRegistration(HostComponentRegistration delegate) {
            this.delegate = delegate;
            this.interfaces = new HashSet();
        }

        void addInterface(Class<?> implementedInterface) {
            if (!Arrays.asList(this.delegate.getMainInterfaceClasses()).contains(implementedInterface)) {
                throw new IllegalArgumentException(implementedInterface.getName() + " is not an interface of the host component " + this.delegate.getInstance().getClass().getName());
            }
            this.interfaces.add(implementedInterface);
        }

        @Override
        public Dictionary<String, String> getProperties() {
            return this.delegate.getProperties();
        }

        @Override
        public String[] getMainInterfaces() {
            return (String[])this.interfaces.stream().map(Class::getName).toArray(String[]::new);
        }

        @Override
        public Object getInstance() {
            return this.delegate.getInstance();
        }

        @Override
        public Class<?>[] getMainInterfaceClasses() {
            return this.interfaces.toArray(new Class[0]);
        }
    }
}

