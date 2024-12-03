/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.components.RendererComponent
 *  com.atlassian.renderer.v2.plugin.RendererComponentFactory
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 */
package com.atlassian.confluence.renderer.plugin;

import com.atlassian.renderer.v2.components.RendererComponent;
import com.atlassian.renderer.v2.plugin.RendererComponentFactory;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class SpringRendererComponentFactory
implements RendererComponentFactory,
BeanFactoryAware {
    private BeanFactory beanFactory;

    public RendererComponent getComponentInstance(Map map) {
        if (!map.containsKey("componentName")) {
            throw new IllegalArgumentException("Required parameter missing: componentName. Parameters are: " + map);
        }
        String componentName = (String)map.get("componentName");
        return (RendererComponent)this.beanFactory.getBean(componentName, RendererComponent.class);
    }

    public void setBeanFactory(@NonNull BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}

