/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeanMetadataElement
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.config.ConstructorArgumentValues
 *  org.springframework.beans.factory.config.ConstructorArgumentValues$ValueHolder
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 *  org.springframework.core.Ordered
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.container.support.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class CycleOrderingProcessor
implements BeanFactoryPostProcessor,
Ordered {
    public static final String SYNTHETIC_DEPENDS_ON = "org.eclipse.gemini.blueprint.blueprint.container.support.internal.config.dependson";
    private static final Log log = LogFactory.getLog(CycleOrderingProcessor.class);

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] names;
        boolean trace = log.isTraceEnabled();
        for (String name : names = beanFactory.getBeanDefinitionNames()) {
            BeanDefinition definition = beanFactory.getBeanDefinition(name);
            if (!definition.hasAttribute("org.eclipse.gemini.blueprint.blueprint.config.internal.marker")) continue;
            ConstructorArgumentValues cArgs = definition.getConstructorArgumentValues();
            if (trace) {
                log.trace((Object)("Inspecting cycles for (blueprint) bean " + name));
            }
            this.tag(cArgs.getGenericArgumentValues(), name, definition);
            this.tag(cArgs.getIndexedArgumentValues().values(), name, definition);
        }
    }

    private void tag(Collection<ConstructorArgumentValues.ValueHolder> values, String name, BeanDefinition definition) {
        boolean trace = log.isTraceEnabled();
        for (ConstructorArgumentValues.ValueHolder value : values) {
            Object val = value.getValue();
            if (!(val instanceof BeanMetadataElement) || !(val instanceof RuntimeBeanReference)) continue;
            String beanName = ((RuntimeBeanReference)val).getBeanName();
            if (trace) {
                log.trace((Object)("Adding (cycle breaking) depends-on on " + name + " to " + beanName));
            }
            this.addSyntheticDependsOn(definition, beanName);
        }
    }

    private void addSyntheticDependsOn(BeanDefinition definition, String beanName) {
        if (StringUtils.hasText((String)beanName)) {
            Object[] dependsOn = definition.getDependsOn();
            if (dependsOn != null && dependsOn.length > 0) {
                for (Object dependOn : dependsOn) {
                    if (!beanName.equals(dependOn)) continue;
                    return;
                }
            }
            dependsOn = (String[])ObjectUtils.addObjectToArray((Object[])dependsOn, (Object)beanName);
            definition.setDependsOn((String[])dependsOn);
            ArrayList<String> markers = (ArrayList<String>)definition.getAttribute(SYNTHETIC_DEPENDS_ON);
            if (markers == null) {
                markers = new ArrayList<String>(2);
                definition.setAttribute(SYNTHETIC_DEPENDS_ON, markers);
            }
            markers.add(beanName);
        }
    }

    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

