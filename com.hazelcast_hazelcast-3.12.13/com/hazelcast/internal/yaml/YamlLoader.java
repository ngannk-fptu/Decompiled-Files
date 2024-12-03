/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.ReflectiveYamlDocumentLoader;
import com.hazelcast.internal.yaml.YamlDocumentLoader;
import com.hazelcast.internal.yaml.YamlDomBuilder;
import com.hazelcast.internal.yaml.YamlException;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlUtil;
import java.io.InputStream;
import java.io.Reader;

public final class YamlLoader {
    private YamlLoader() {
    }

    public static YamlNode load(InputStream inputStream, String rootName) {
        try {
            YamlDocumentLoader load = YamlLoader.getLoad();
            Object document = load.loadFromInputStream(inputStream);
            return YamlLoader.buildDom(rootName, document);
        }
        catch (Exception ex) {
            throw new YamlException("An error occurred while loading and parsing the YAML stream", ex);
        }
    }

    public static YamlNode load(InputStream inputStream) {
        try {
            YamlDocumentLoader load = YamlLoader.getLoad();
            Object document = load.loadFromInputStream(inputStream);
            return YamlLoader.buildDom(document);
        }
        catch (Exception ex) {
            throw new YamlException("An error occurred while loading and parsing the YAML stream", ex);
        }
    }

    public static YamlNode load(Reader reader, String rootName) {
        try {
            YamlDocumentLoader load = YamlLoader.getLoad();
            Object document = load.loadFromReader(reader);
            return YamlLoader.buildDom(rootName, document);
        }
        catch (Exception ex) {
            throw new YamlException("An error occurred while loading and parsing the YAML stream", ex);
        }
    }

    public static YamlNode load(Reader reader) {
        try {
            YamlDocumentLoader load = YamlLoader.getLoad();
            Object document = load.loadFromReader(reader);
            return YamlLoader.buildDom(document);
        }
        catch (Exception ex) {
            throw new YamlException("An error occurred while loading and parsing the YAML stream", ex);
        }
    }

    public static YamlNode load(String yaml, String rootName) {
        try {
            YamlDocumentLoader load = YamlLoader.getLoad();
            Object document = load.loadFromString(yaml);
            return YamlLoader.buildDom(rootName, document);
        }
        catch (Exception ex) {
            throw new YamlException("An error occurred while loading and parsing the YAML string", ex);
        }
    }

    public static YamlNode load(String yaml) {
        try {
            YamlDocumentLoader loader = YamlLoader.getLoad();
            Object document = loader.loadFromString(yaml);
            return YamlLoader.buildDom(document);
        }
        catch (Exception ex) {
            throw new YamlException("An error occurred while loading and parsing the YAML string", ex);
        }
    }

    private static YamlDocumentLoader getLoad() {
        YamlUtil.ensureRunningOnJava8OrHigher();
        return new ReflectiveYamlDocumentLoader();
    }

    private static YamlNode buildDom(String rootName, Object document) {
        return YamlDomBuilder.build(document, rootName);
    }

    private static YamlNode buildDom(Object document) {
        return YamlDomBuilder.build(document);
    }
}

