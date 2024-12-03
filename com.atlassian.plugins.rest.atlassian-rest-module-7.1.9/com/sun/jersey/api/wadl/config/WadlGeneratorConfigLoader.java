/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.wadl.config;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.wadl.config.WadlGeneratorConfig;
import com.sun.jersey.api.wadl.config.WadlGeneratorDescription;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.security.AccessController;
import java.util.Collections;
import java.util.List;

public class WadlGeneratorConfigLoader {
    public static WadlGeneratorConfig loadWadlGeneratorsFromConfig(ResourceConfig resourceConfig) {
        Object wadlGeneratorConfigProperty = resourceConfig.getProperty("com.sun.jersey.config.property.WadlGeneratorConfig");
        if (wadlGeneratorConfigProperty == null) {
            WadlGeneratorConfig config = new WadlGeneratorConfig(){

                @Override
                public List<WadlGeneratorDescription> configure() {
                    return Collections.EMPTY_LIST;
                }
            };
            return config;
        }
        try {
            Class<WadlGeneratorConfig> configClazz;
            if (wadlGeneratorConfigProperty instanceof WadlGeneratorConfig) {
                return (WadlGeneratorConfig)wadlGeneratorConfigProperty;
            }
            if (wadlGeneratorConfigProperty instanceof Class) {
                configClazz = ((Class)wadlGeneratorConfigProperty).asSubclass(WadlGeneratorConfig.class);
            } else if (wadlGeneratorConfigProperty instanceof String) {
                configClazz = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA((String)wadlGeneratorConfigProperty)).asSubclass(WadlGeneratorConfig.class);
            } else {
                throw new RuntimeException("The property com.sun.jersey.config.property.WadlGeneratorConfig is an invalid type: " + wadlGeneratorConfigProperty.getClass().getName() + " (supported: String, Class<? extends WadlGeneratorConfiguration>, WadlGeneratorConfiguration)");
            }
            WadlGeneratorConfig config = configClazz.newInstance();
            return config;
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load WadlGeneratorConfiguration, check the configuration of com.sun.jersey.config.property.WadlGeneratorConfig", e);
        }
    }
}

