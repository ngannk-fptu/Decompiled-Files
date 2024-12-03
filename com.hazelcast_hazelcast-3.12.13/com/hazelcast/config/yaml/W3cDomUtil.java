/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.config.yaml.ElementAdapter;
import com.hazelcast.config.yaml.EmptyNodeList;
import com.hazelcast.config.yaml.SingletonNodeList;
import com.hazelcast.internal.yaml.MutableYamlNode;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import com.hazelcast.internal.yaml.YamlUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class W3cDomUtil {
    private W3cDomUtil() {
    }

    public static Node asW3cNode(YamlNode yamlNode) {
        if (yamlNode == null) {
            return null;
        }
        return new ElementAdapter(yamlNode);
    }

    public static YamlMapping getWrappedYamlMapping(Node node) {
        W3cDomUtil.checkNodeIsElementAdapter(node);
        return W3cDomUtil.asYamlType(node, YamlMapping.class);
    }

    public static YamlSequence getWrappedYamlSequence(Node node) {
        W3cDomUtil.checkNodeIsElementAdapter(node);
        return W3cDomUtil.asYamlType(node, YamlSequence.class);
    }

    public static YamlScalar getWrappedYamlScalar(Node node) {
        W3cDomUtil.checkNodeIsElementAdapter(node);
        return W3cDomUtil.asYamlType(node, YamlScalar.class);
    }

    public static MutableYamlNode getWrappedMutableYamlNode(Node node) {
        W3cDomUtil.checkNodeIsElementAdapter(node);
        return W3cDomUtil.asYamlType(node, MutableYamlNode.class);
    }

    static NodeList asNodeList(Node node) {
        if (node == null) {
            return EmptyNodeList.emptyNodeList();
        }
        return new SingletonNodeList(node);
    }

    private static <T extends YamlNode> T asYamlType(Node node, Class<T> type) {
        return (T)((YamlNode)YamlUtil.asType(((ElementAdapter)node).getYamlNode(), type));
    }

    private static void checkNodeIsElementAdapter(Node node) {
        if (!(node instanceof ElementAdapter)) {
            throw new IllegalArgumentException(String.format("The provided node is not an instance of ElementAdapter, it is a %s", node.getClass().getName()));
        }
    }
}

