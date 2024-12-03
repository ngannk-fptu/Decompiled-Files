/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PropertiesLoaderUtils
 *  org.springframework.util.ClassUtils
 */
package org.springframework.http.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.util.ClassUtils;

final class CodecConfigurerFactory {
    private static final String DEFAULT_CONFIGURERS_PATH = "CodecConfigurer.properties";
    private static final Map<Class<?>, Class<?>> defaultCodecConfigurers = new HashMap(4);

    private CodecConfigurerFactory() {
    }

    public static <T extends CodecConfigurer> T create(Class<T> ifc) {
        Class<?> impl = defaultCodecConfigurers.get(ifc);
        if (impl == null) {
            throw new IllegalStateException("No default codec configurer found for " + ifc);
        }
        return (T)((CodecConfigurer)BeanUtils.instantiateClass(impl));
    }

    static {
        try {
            Properties props = PropertiesLoaderUtils.loadProperties((Resource)new ClassPathResource(DEFAULT_CONFIGURERS_PATH, CodecConfigurerFactory.class));
            for (String ifcName : props.stringPropertyNames()) {
                String implName = props.getProperty(ifcName);
                Class ifc = ClassUtils.forName((String)ifcName, (ClassLoader)CodecConfigurerFactory.class.getClassLoader());
                Class impl = ClassUtils.forName((String)implName, (ClassLoader)CodecConfigurerFactory.class.getClassLoader());
                defaultCodecConfigurers.put(ifc, impl);
            }
        }
        catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

