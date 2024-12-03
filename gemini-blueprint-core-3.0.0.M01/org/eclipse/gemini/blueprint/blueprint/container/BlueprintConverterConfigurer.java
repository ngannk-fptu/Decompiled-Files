/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.support.AbstractBeanFactory
 *  org.springframework.core.convert.ConversionService
 */
package org.eclipse.gemini.blueprint.blueprint.container;

import java.util.List;
import org.eclipse.gemini.blueprint.blueprint.container.SpringBlueprintConverterService;
import org.osgi.service.blueprint.container.Converter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.convert.ConversionService;

public class BlueprintConverterConfigurer
implements BeanFactoryAware {
    private final List<Converter> converters;

    public BlueprintConverterConfigurer(List<Converter> converters) {
        this.converters = converters;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (beanFactory instanceof AbstractBeanFactory) {
            AbstractBeanFactory bf = (AbstractBeanFactory)beanFactory;
            ConversionService cs = bf.getConversionService();
            if (cs instanceof SpringBlueprintConverterService) {
                cs = null;
            }
            SpringBlueprintConverterService sbc = new SpringBlueprintConverterService(cs, (ConfigurableBeanFactory)bf);
            sbc.add(this.converters);
            bf.setConversionService((ConversionService)sbc);
        }
    }
}

