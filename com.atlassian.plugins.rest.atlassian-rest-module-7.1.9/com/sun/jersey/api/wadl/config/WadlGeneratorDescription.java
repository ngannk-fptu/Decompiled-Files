/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.wadl.config;

import com.sun.jersey.server.wadl.WadlGenerator;
import java.util.Properties;

public class WadlGeneratorDescription {
    private Class<? extends WadlGenerator> _generatorClass;
    private Properties _properties;

    public WadlGeneratorDescription() {
    }

    public WadlGeneratorDescription(Class<? extends WadlGenerator> generatorClass, Properties properties) {
        this._generatorClass = generatorClass;
        this._properties = properties;
    }

    public Class<? extends WadlGenerator> getGeneratorClass() {
        return this._generatorClass;
    }

    public void setGeneratorClass(Class<? extends WadlGenerator> generatorClass) {
        this._generatorClass = generatorClass;
    }

    public Properties getProperties() {
        return this._properties;
    }

    public void setProperties(Properties properties) {
        this._properties = properties;
    }
}

