/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.security.access.method;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractFallbackMethodSecurityMetadataSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@Deprecated
public class MapBasedMethodSecurityMetadataSource
extends AbstractFallbackMethodSecurityMetadataSource
implements BeanClassLoaderAware {
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    protected final Map<RegisteredMethod, List<ConfigAttribute>> methodMap = new HashMap<RegisteredMethod, List<ConfigAttribute>>();
    private final Map<RegisteredMethod, String> nameMap = new HashMap<RegisteredMethod, String>();

    public MapBasedMethodSecurityMetadataSource() {
    }

    public MapBasedMethodSecurityMetadataSource(Map<String, List<ConfigAttribute>> methodMap) {
        for (Map.Entry<String, List<ConfigAttribute>> entry : methodMap.entrySet()) {
            this.addSecureMethod(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Class<?> clazz) {
        return null;
    }

    @Override
    protected Collection<ConfigAttribute> findAttributes(Method method, Class<?> targetClass) {
        if (targetClass == null) {
            return null;
        }
        return this.findAttributesSpecifiedAgainst(method, targetClass);
    }

    private List<ConfigAttribute> findAttributesSpecifiedAgainst(Method method, Class<?> clazz) {
        RegisteredMethod registeredMethod = new RegisteredMethod(method, clazz);
        if (this.methodMap.containsKey(registeredMethod)) {
            return this.methodMap.get(registeredMethod);
        }
        if (clazz.getSuperclass() != null) {
            return this.findAttributesSpecifiedAgainst(method, clazz.getSuperclass());
        }
        return null;
    }

    private void addSecureMethod(String name, List<ConfigAttribute> attr) {
        int lastDotIndex = name.lastIndexOf(".");
        Assert.isTrue((lastDotIndex != -1 ? 1 : 0) != 0, () -> "'" + name + "' is not a valid method name: format is FQN.methodName");
        String methodName = name.substring(lastDotIndex + 1);
        Assert.hasText((String)methodName, () -> "Method not found for '" + name + "'");
        String typeName = name.substring(0, lastDotIndex);
        Class type = ClassUtils.resolveClassName((String)typeName, (ClassLoader)this.beanClassLoader);
        this.addSecureMethod(type, methodName, attr);
    }

    public void addSecureMethod(Class<?> javaType, String mappedName, List<ConfigAttribute> attr) {
        String name = javaType.getName() + '.' + mappedName;
        this.logger.debug((Object)LogMessage.format((String)"Request to add secure method [%s] with attributes [%s]", (Object)name, attr));
        Method[] methods = javaType.getMethods();
        ArrayList<Method> matchingMethods = new ArrayList<Method>();
        for (Method method : methods) {
            if (!method.getName().equals(mappedName) && !this.isMatch(method.getName(), mappedName)) continue;
            matchingMethods.add(method);
        }
        Assert.notEmpty(matchingMethods, () -> "Couldn't find method '" + mappedName + "' on '" + javaType + "'");
        this.registerAllMatchingMethods(javaType, attr, name, matchingMethods);
    }

    private void registerAllMatchingMethods(Class<?> javaType, List<ConfigAttribute> attr, String name, List<Method> matchingMethods) {
        for (Method method : matchingMethods) {
            RegisteredMethod registeredMethod = new RegisteredMethod(method, javaType);
            String regMethodName = this.nameMap.get(registeredMethod);
            if (regMethodName == null || !regMethodName.equals(name) && regMethodName.length() <= name.length()) {
                if (regMethodName != null) {
                    this.logger.debug((Object)LogMessage.format((String)"Replacing attributes for secure method [%s]: current name [%s] is more specific than [%s]", (Object)method, (Object)name, (Object)regMethodName));
                }
                this.nameMap.put(registeredMethod, name);
                this.addSecureMethod(registeredMethod, attr);
                continue;
            }
            this.logger.debug((Object)LogMessage.format((String)"Keeping attributes for secure method [%s]: current name [%s] is not more specific than [%s]", (Object)method, (Object)name, (Object)regMethodName));
        }
    }

    public void addSecureMethod(Class<?> javaType, Method method, List<ConfigAttribute> attr) {
        RegisteredMethod key = new RegisteredMethod(method, javaType);
        if (this.methodMap.containsKey(key)) {
            this.logger.debug((Object)LogMessage.format((String)"Method [%s] is already registered with attributes [%s]", (Object)method, this.methodMap.get(key)));
            return;
        }
        this.methodMap.put(key, attr);
    }

    private void addSecureMethod(RegisteredMethod method, List<ConfigAttribute> attr) {
        Assert.notNull((Object)method, (String)"RegisteredMethod required");
        Assert.notNull(attr, (String)"Configuration attribute required");
        this.logger.info((Object)LogMessage.format((String)"Adding secure method [%s] with attributes [%s]", (Object)method, attr));
        this.methodMap.put(method, attr);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        HashSet<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();
        this.methodMap.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    private boolean isMatch(String methodName, String mappedName) {
        return mappedName.endsWith("*") && methodName.startsWith(mappedName.substring(0, mappedName.length() - 1)) || mappedName.startsWith("*") && methodName.endsWith(mappedName.substring(1, mappedName.length()));
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        Assert.notNull((Object)beanClassLoader, (String)"Bean class loader required");
        this.beanClassLoader = beanClassLoader;
    }

    public int getMethodMapSize() {
        return this.methodMap.size();
    }

    private static class RegisteredMethod {
        private final Method method;
        private final Class<?> registeredJavaType;

        RegisteredMethod(Method method, Class<?> registeredJavaType) {
            Assert.notNull((Object)method, (String)"Method required");
            Assert.notNull(registeredJavaType, (String)"Registered Java Type required");
            this.method = method;
            this.registeredJavaType = registeredJavaType;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && obj instanceof RegisteredMethod) {
                RegisteredMethod rhs = (RegisteredMethod)obj;
                return this.method.equals(rhs.method) && this.registeredJavaType.equals(rhs.registeredJavaType);
            }
            return false;
        }

        public int hashCode() {
            return this.method.hashCode() * this.registeredJavaType.hashCode();
        }

        public String toString() {
            return "RegisteredMethod[" + this.registeredJavaType.getName() + "; " + this.method + "]";
        }
    }
}

