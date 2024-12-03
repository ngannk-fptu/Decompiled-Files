/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Suppliers
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import io.atlassian.fugue.Suppliers;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public final class NamedBeanContextItemProvider
implements ApplicationContextAware,
VelocityContextItemProvider {
    private String[] beanNames;
    private ApplicationContext applicationContext;
    private final Supplier<Map<String, Object>> beanMap = Suppliers.memoize(this::buildBeanMap);

    private Map<String, Object> buildBeanMap() {
        return Arrays.stream(this.beanNames).collect(Collectors.toMap(name -> name, name -> this.applicationContext.getBean(name)));
    }

    @Override
    public Map<String, Object> getContextMap() {
        return this.beanMap.get();
    }

    public void setBeanNames(String[] beanNames) {
        this.beanNames = beanNames;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

