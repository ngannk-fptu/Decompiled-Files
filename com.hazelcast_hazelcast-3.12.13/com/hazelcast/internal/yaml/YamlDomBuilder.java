/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.YamlException;
import com.hazelcast.internal.yaml.YamlMappingImpl;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalarImpl;
import com.hazelcast.internal.yaml.YamlSequenceImpl;
import java.util.List;
import java.util.Map;

public final class YamlDomBuilder {
    private YamlDomBuilder() {
    }

    static YamlNode build(Object document, String rootName) {
        Object rootNode;
        if (document == null) {
            throw new YamlException("The provided document is null");
        }
        if (rootName != null && !(document instanceof Map)) {
            throw new YamlException("The provided document is not a Map, and rootName is defined.");
        }
        if (rootName != null) {
            rootNode = ((Map)document).get(rootName);
            if (rootNode == null) {
                throw new YamlException("The required " + rootName + " root node couldn't be found in the document root");
            }
        } else {
            rootNode = document;
        }
        return YamlDomBuilder.buildNode(null, rootName, rootNode);
    }

    public static YamlNode build(Object document) {
        return YamlDomBuilder.build(document, null);
    }

    private static YamlNode buildNode(YamlNode parent, String nodeName, Object sourceNode) {
        if (sourceNode == null) {
            return null;
        }
        if (sourceNode instanceof Map) {
            YamlMappingImpl node = new YamlMappingImpl(parent, nodeName);
            YamlDomBuilder.buildChildren(node, (Map)sourceNode);
            return node;
        }
        if (sourceNode instanceof List) {
            YamlSequenceImpl node = new YamlSequenceImpl(parent, nodeName);
            YamlDomBuilder.buildChildren(node, (List)sourceNode);
            return node;
        }
        if (YamlDomBuilder.isSupportedScalarType(sourceNode)) {
            return YamlDomBuilder.buildScalar(parent, nodeName, sourceNode);
        }
        throw new YamlException("An unsupported scalar type is encountered: " + nodeName + " is an instance of " + sourceNode.getClass().getName() + ". The supported types are String, Integer, Double and Boolean.");
    }

    private static boolean isSupportedScalarType(Object sourceNode) {
        return sourceNode instanceof String || sourceNode instanceof Integer || sourceNode instanceof Double || sourceNode instanceof Boolean;
    }

    private static void buildChildren(YamlMappingImpl parentNode, Map<String, Object> mapNode) {
        for (Map.Entry<String, Object> entry : mapNode.entrySet()) {
            String childNodeName = entry.getKey();
            Object childNodeValue = entry.getValue();
            YamlNode child = YamlDomBuilder.buildNode(parentNode, childNodeName, childNodeValue);
            parentNode.addChild(childNodeName, child);
        }
    }

    private static void buildChildren(YamlSequenceImpl parentNode, List<Object> listNode) {
        for (Object value : listNode) {
            YamlNode child = YamlDomBuilder.buildNode(parentNode, null, value);
            parentNode.addChild(child);
        }
    }

    private static YamlNode buildScalar(YamlNode parent, String nodeName, Object value) {
        return new YamlScalarImpl(parent, nodeName, value);
    }
}

