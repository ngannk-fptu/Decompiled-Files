/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.FactoryBeanNotInitializedException
 */
package org.springframework.ldap.odm.typeconversion.impl;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.ldap.odm.typeconversion.impl.Converter;
import org.springframework.ldap.odm.typeconversion.impl.ConverterManagerImpl;

public final class ConverterManagerFactoryBean
implements FactoryBean {
    private static final Logger LOG = LoggerFactory.getLogger(ConverterManagerFactoryBean.class);
    private Set<ConverterConfig> converterConfigList = null;

    public void setConverterConfig(Set<ConverterConfig> converterConfigList) {
        this.converterConfigList = converterConfigList;
    }

    public Object getObject() throws Exception {
        if (this.converterConfigList == null) {
            throw new FactoryBeanNotInitializedException("converterConfigList has not been set");
        }
        ConverterManagerImpl result = new ConverterManagerImpl();
        for (ConverterConfig converterConfig : this.converterConfigList) {
            if (converterConfig.fromClasses == null || converterConfig.toClasses == null || converterConfig.converter == null) {
                throw new FactoryBeanNotInitializedException(String.format("All of fromClasses, toClasses and converter must be specified in bean %1$s", converterConfig.toString()));
            }
            for (Class fromClass : converterConfig.fromClasses) {
                for (Class toClass : converterConfig.toClasses) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(String.format("Adding converter from %1$s to %2$s", fromClass, toClass));
                    }
                    result.addConverter(fromClass, converterConfig.syntax, toClass, converterConfig.converter);
                }
            }
        }
        return result;
    }

    public Class<?> getObjectType() {
        return ConverterManagerImpl.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public static final class ConverterConfig {
        private Set<Class<?>> fromClasses = new HashSet();
        private String syntax = null;
        private Set<Class<?>> toClasses = new HashSet();
        private Converter converter = null;

        public void setFromClasses(Set<Class<?>> fromClasses) {
            this.fromClasses = fromClasses;
        }

        public void setToClasses(Set<Class<?>> toClasses) {
            this.toClasses = toClasses;
        }

        public void setSyntax(String syntax) {
            this.syntax = syntax;
        }

        public void setConverter(Converter converter) {
            this.converter = converter;
        }

        public String toString() {
            return String.format("fromClasses=%1$s, syntax=%2$s, toClasses=%3$s, converter=%4$s", this.fromClasses, this.syntax, this.toClasses, this.converter);
        }
    }
}

