/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class GenericXmlApplicationContext
extends GenericApplicationContext {
    private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

    public GenericXmlApplicationContext() {
    }

    public GenericXmlApplicationContext(Resource ... resources) {
        this.load(resources);
        this.refresh();
    }

    public GenericXmlApplicationContext(String ... resourceLocations) {
        this.load(resourceLocations);
        this.refresh();
    }

    public GenericXmlApplicationContext(Class<?> relativeClass, String ... resourceNames) {
        this.load(relativeClass, resourceNames);
        this.refresh();
    }

    public final XmlBeanDefinitionReader getReader() {
        return this.reader;
    }

    public void setValidating(boolean validating) {
        this.reader.setValidating(validating);
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment2) {
        super.setEnvironment(environment2);
        this.reader.setEnvironment(this.getEnvironment());
    }

    public void load(Resource ... resources) {
        this.reader.loadBeanDefinitions(resources);
    }

    public void load(String ... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }

    public void load(Class<?> relativeClass, String ... resourceNames) {
        Resource[] resources = new Resource[resourceNames.length];
        for (int i = 0; i < resourceNames.length; ++i) {
            resources[i] = new ClassPathResource(resourceNames[i], relativeClass);
        }
        this.load(resources);
    }
}

