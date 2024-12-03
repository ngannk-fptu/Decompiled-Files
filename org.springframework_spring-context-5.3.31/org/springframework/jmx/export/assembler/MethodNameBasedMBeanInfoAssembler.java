/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.jmx.export.assembler.AbstractConfigurableMBeanInfoAssembler;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class MethodNameBasedMBeanInfoAssembler
extends AbstractConfigurableMBeanInfoAssembler {
    @Nullable
    private Set<String> managedMethods;
    @Nullable
    private Map<String, Set<String>> methodMappings;

    public void setManagedMethods(String ... methodNames) {
        this.managedMethods = new HashSet<String>(Arrays.asList(methodNames));
    }

    public void setMethodMappings(Properties mappings) {
        this.methodMappings = new HashMap<String, Set<String>>();
        Enumeration<Object> en = mappings.keys();
        while (en.hasMoreElements()) {
            String beanKey = (String)en.nextElement();
            String[] methodNames = StringUtils.commaDelimitedListToStringArray((String)mappings.getProperty(beanKey));
            this.methodMappings.put(beanKey, new HashSet<String>(Arrays.asList(methodNames)));
        }
    }

    @Override
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return this.isMatch(method, beanKey);
    }

    @Override
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return this.isMatch(method, beanKey);
    }

    @Override
    protected boolean includeOperation(Method method, String beanKey) {
        return this.isMatch(method, beanKey);
    }

    protected boolean isMatch(Method method, String beanKey) {
        Set<String> methodNames;
        if (this.methodMappings != null && (methodNames = this.methodMappings.get(beanKey)) != null) {
            return methodNames.contains(method.getName());
        }
        return this.managedMethods != null && this.managedMethods.contains(method.getName());
    }
}

