/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

public class CustomEditorConfigurer
implements BeanFactoryPostProcessor,
Ordered {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private int order = Integer.MAX_VALUE;
    @Nullable
    private PropertyEditorRegistrar[] propertyEditorRegistrars;
    @Nullable
    private Map<Class<?>, Class<? extends PropertyEditor>> customEditors;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
        this.propertyEditorRegistrars = propertyEditorRegistrars;
    }

    public void setCustomEditors(Map<Class<?>, Class<? extends PropertyEditor>> customEditors) {
        this.customEditors = customEditors;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertyEditorRegistrars != null) {
            for (PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
                beanFactory.addPropertyEditorRegistrar(propertyEditorRegistrar);
            }
        }
        if (this.customEditors != null) {
            this.customEditors.forEach(beanFactory::registerCustomEditor);
        }
    }
}

