/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractDomVariableReplacer;
import com.hazelcast.config.replacer.spi.ConfigReplacer;
import org.w3c.dom.Node;

class XmlDomVariableReplacer
extends AbstractDomVariableReplacer {
    XmlDomVariableReplacer() {
    }

    @Override
    public void replaceVariables(Node node, ConfigReplacer replacer, boolean failFast) {
        this.replaceVariableInNodeValue(node, replacer, failFast);
    }
}

