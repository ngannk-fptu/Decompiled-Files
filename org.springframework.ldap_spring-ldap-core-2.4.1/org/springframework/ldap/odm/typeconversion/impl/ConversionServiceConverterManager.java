/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.ldap.odm.typeconversion.impl;

import javax.naming.Name;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.ldap.odm.typeconversion.ConverterManager;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class ConversionServiceConverterManager
implements ConverterManager {
    private GenericConversionService conversionService;
    private static final String DEFAULT_CONVERSION_SERVICE_CLASS = "org.springframework.core.convert.support.DefaultConversionService";

    public ConversionServiceConverterManager(GenericConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ConversionServiceConverterManager() {
        ClassLoader defaultClassLoader = ClassUtils.getDefaultClassLoader();
        if (ClassUtils.isPresent((String)DEFAULT_CONVERSION_SERVICE_CLASS, (ClassLoader)defaultClassLoader)) {
            try {
                Class clazz = ClassUtils.forName((String)DEFAULT_CONVERSION_SERVICE_CLASS, (ClassLoader)defaultClassLoader);
                this.conversionService = (GenericConversionService)clazz.newInstance();
            }
            catch (Exception e) {
                ReflectionUtils.handleReflectionException((Exception)e);
            }
        } else {
            this.conversionService = new GenericConversionService();
        }
        this.prePopulateWithNameConverter();
    }

    private void prePopulateWithNameConverter() {
        this.conversionService.addConverter((Converter)new StringToNameConverter());
    }

    @Override
    public boolean canConvert(Class<?> fromClass, String syntax, Class<?> toClass) {
        return this.conversionService.canConvert(fromClass, toClass);
    }

    @Override
    public <T> T convert(Object source, String syntax, Class<T> toClass) {
        return (T)this.conversionService.convert(source, toClass);
    }

    public static final class StringToNameConverter
    implements Converter<String, Name> {
        public Name convert(String source) {
            if (source == null) {
                return null;
            }
            return LdapUtils.newLdapName(source);
        }
    }

    public static final class NameToStringConverter
    implements Converter<Name, String> {
        public String convert(Name source) {
            if (source == null) {
                return null;
            }
            return source.toString();
        }
    }
}

