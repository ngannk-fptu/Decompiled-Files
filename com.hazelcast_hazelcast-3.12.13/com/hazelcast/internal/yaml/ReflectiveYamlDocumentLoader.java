/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.YamlDocumentLoader;
import com.hazelcast.internal.yaml.YamlException;
import com.hazelcast.util.Preconditions;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class ReflectiveYamlDocumentLoader
implements YamlDocumentLoader {
    private final Object load;
    private final Method loadFromInputStream;
    private final Method loadFromReader;
    private final Method loadFromString;

    ReflectiveYamlDocumentLoader() {
        try {
            Class<?> loadSettingsClass = Class.forName("com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings");
            Class<?> loadSettingsBuilderClass = Class.forName("com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettingsBuilder");
            Method loadSettingsBuilderMethod = loadSettingsClass.getMethod("builder", new Class[0]);
            Method buildLoadSettingsMethod = loadSettingsBuilderClass.getMethod("build", new Class[0]);
            Object loadSettingsBuilder = loadSettingsBuilderMethod.invoke(null, new Object[0]);
            Object loadSettings = buildLoadSettingsMethod.invoke(loadSettingsBuilder, new Object[0]);
            Class<?> loadClass = Class.forName("com.hazelcast.org.snakeyaml.engine.v2.api.Load");
            Constructor<?> constructor = loadClass.getConstructor(loadSettingsClass);
            this.load = constructor.newInstance(loadSettings);
            this.loadFromInputStream = loadClass.getMethod("loadFromInputStream", InputStream.class);
            this.loadFromReader = loadClass.getMethod("loadFromReader", Reader.class);
            this.loadFromString = loadClass.getMethod("loadFromString", String.class);
        }
        catch (Exception e) {
            throw new YamlException("An error occurred while creating the SnakeYaml Load class", e);
        }
    }

    @Override
    public Object loadFromInputStream(InputStream yamlStream) {
        Preconditions.checkNotNull(yamlStream, "The provided InputStream to load the YAML from must not be null");
        try {
            return this.loadFromInputStream.invoke(this.load, yamlStream);
        }
        catch (Exception e) {
            throw new YamlException("Couldn't load YAML document from the provided InputStream", e);
        }
    }

    @Override
    public Object loadFromReader(Reader yamlReader) {
        Preconditions.checkNotNull(yamlReader, "The provided Reader to load the YAML from must not be null");
        try {
            return this.loadFromReader.invoke(this.load, yamlReader);
        }
        catch (Exception e) {
            throw new YamlException("Couldn't load YAML document from the provided Reader", e);
        }
    }

    @Override
    public Object loadFromString(String yaml) {
        Preconditions.checkNotNull(yaml, "The provided String to load the YAML from must not be null");
        try {
            return this.loadFromString.invoke(this.load, yaml);
        }
        catch (Exception e) {
            throw new YamlException("Couldn't load YAML document from the provided String", e);
        }
    }
}

