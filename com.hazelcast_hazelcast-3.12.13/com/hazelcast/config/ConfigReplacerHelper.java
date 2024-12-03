/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.DomVariableReplacer;
import com.hazelcast.config.replacer.spi.ConfigReplacer;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class ConfigReplacerHelper {
    private ConfigReplacerHelper() {
    }

    static void traverseChildrenAndReplaceVariables(Node root, List<ConfigReplacer> replacers, boolean failFast, DomVariableReplacer variableReplacer) {
        for (ConfigReplacer replacer : replacers) {
            ConfigReplacerHelper.traverseChildrenAndReplaceVariables(root, replacer, failFast, variableReplacer);
        }
    }

    private static void traverseChildrenAndReplaceVariables(Node root, ConfigReplacer replacer, boolean failFast, DomVariableReplacer variableReplacer) {
        NamedNodeMap attributes = root.getAttributes();
        if (attributes != null) {
            for (int k = 0; k < attributes.getLength(); ++k) {
                Node attribute = attributes.item(k);
                variableReplacer.replaceVariables(attribute, replacer, failFast);
            }
        }
        if (root.getNodeValue() != null) {
            variableReplacer.replaceVariables(root, replacer, failFast);
        }
        NodeList childNodes = root.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); ++k) {
            Node child = childNodes.item(k);
            ConfigReplacerHelper.traverseChildrenAndReplaceVariables(child, replacer, failFast, variableReplacer);
        }
    }
}

