/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractDomVariableReplacer;
import com.hazelcast.config.replacer.spi.ConfigReplacer;
import com.hazelcast.config.yaml.ElementAdapter;
import com.hazelcast.config.yaml.W3cDomUtil;
import com.hazelcast.internal.yaml.MutableYamlNode;
import org.w3c.dom.Node;

class YamlDomVariableReplacer
extends AbstractDomVariableReplacer {
    YamlDomVariableReplacer() {
    }

    @Override
    public void replaceVariables(Node node, ConfigReplacer replacer, boolean failFast) {
        this.replaceVariableInNodeValue(node, replacer, failFast);
        this.replaceVariableInNodeName(node, replacer, failFast);
    }

    private void replaceVariableInNodeName(Node node, ConfigReplacer replacer, boolean failFast) {
        MutableYamlNode yamlNode;
        String nodeName;
        if (node instanceof ElementAdapter && (nodeName = (yamlNode = W3cDomUtil.getWrappedMutableYamlNode(node)).nodeName()) != null) {
            String replacedName = YamlDomVariableReplacer.replaceValue(node, replacer, failFast, nodeName);
            yamlNode.setNodeName(replacedName);
        }
    }
}

