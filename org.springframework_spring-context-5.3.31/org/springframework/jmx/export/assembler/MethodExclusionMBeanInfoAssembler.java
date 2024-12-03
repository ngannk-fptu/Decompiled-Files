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

public class MethodExclusionMBeanInfoAssembler
extends AbstractConfigurableMBeanInfoAssembler {
    @Nullable
    private Set<String> ignoredMethods;
    @Nullable
    private Map<String, Set<String>> ignoredMethodMappings;

    public void setIgnoredMethods(String ... ignoredMethodNames) {
        this.ignoredMethods = new HashSet<String>(Arrays.asList(ignoredMethodNames));
    }

    public void setIgnoredMethodMappings(Properties mappings) {
        this.ignoredMethodMappings = new HashMap<String, Set<String>>();
        Enumeration<Object> en = mappings.keys();
        while (en.hasMoreElements()) {
            String beanKey = (String)en.nextElement();
            String[] methodNames = StringUtils.commaDelimitedListToStringArray((String)mappings.getProperty(beanKey));
            this.ignoredMethodMappings.put(beanKey, new HashSet<String>(Arrays.asList(methodNames)));
        }
    }

    @Override
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return this.isNotIgnored(method, beanKey);
    }

    @Override
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return this.isNotIgnored(method, beanKey);
    }

    @Override
    protected boolean includeOperation(Method method, String beanKey) {
        return this.isNotIgnored(method, beanKey);
    }

    protected boolean isNotIgnored(Method method, String beanKey) {
        Set<String> methodNames;
        if (this.ignoredMethodMappings != null && (methodNames = this.ignoredMethodMappings.get(beanKey)) != null) {
            return !methodNames.contains(method.getName());
        }
        if (this.ignoredMethods != null) {
            return !this.ignoredMethods.contains(method.getName());
        }
        return true;
    }
}

