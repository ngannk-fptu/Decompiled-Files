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

    public GenericXmlApplicationContext(Resource ... resources2) {
        this.load(resources2);
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

    public void load(Resource ... resources2) {
        this.reader.loadBeanDefinitions(resources2);
    }

    public void load(String ... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }

    public void load(Class<?> relativeClass, String ... resourceNames) {
        Resource[] resources2 = new Resource[resourceNames.length];
        for (int i2 = 0; i2 < resourceNames.length; ++i2) {
            resources2[i2] = new ClassPathResource(resourceNames[i2], relativeClass);
        }
        this.load(resources2);
    }
}

