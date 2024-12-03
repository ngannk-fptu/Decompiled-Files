/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.collections.LazyMap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.confluence.util.collections.LazyMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public final class PrototypeBeanContextItemProvider
implements VelocityContextItemProvider,
ApplicationContextAware {
    private ApplicationContext applicationContext;
    private ImmutableMap<String, Supplier<Object>> suppliers;

    @Override
    public Map<String, Object> getContextMap() {
        return LazyMap.fromSuppliersMap(this.suppliers);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setContextKeyToBeanNameMapping(Map<String, String> contextKeyToBeanName) {
        HashMap suppliers = Maps.newHashMap();
        for (Map.Entry<String, String> entry : contextKeyToBeanName.entrySet()) {
            String contextKey = entry.getKey();
            String beanName = entry.getValue();
            suppliers.put(contextKey, () -> this.applicationContext.getBean(beanName));
        }
        this.suppliers = ImmutableMap.copyOf((Map)suppliers);
    }
}

