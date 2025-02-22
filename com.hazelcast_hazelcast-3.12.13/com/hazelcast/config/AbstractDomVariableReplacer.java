/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigReplacerHelper;
import com.hazelcast.config.ConfigurationException;
import com.hazelcast.config.DomVariableReplacer;
import com.hazelcast.config.replacer.spi.ConfigReplacer;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import org.w3c.dom.Node;

abstract class AbstractDomVariableReplacer
implements DomVariableReplacer {
    private static final ILogger LOGGER = Logger.getLogger(ConfigReplacerHelper.class);

    AbstractDomVariableReplacer() {
    }

    protected static String replaceValue(Node node, ConfigReplacer replacer, boolean failFast, String value) {
        StringBuilder sb = new StringBuilder(value);
        String replacerPrefix = "$" + replacer.getPrefix() + "{";
        int endIndex = -1;
        int startIndex = sb.indexOf(replacerPrefix);
        while (startIndex > -1) {
            endIndex = sb.indexOf("}", startIndex);
            if (endIndex == -1) {
                LOGGER.warning("Bad variable syntax. Could not find a closing curly bracket '}' for prefix " + replacerPrefix + " on node: " + node.getLocalName());
                break;
            }
            String variable = sb.substring(startIndex + replacerPrefix.length(), endIndex);
            String variableReplacement = replacer.getReplacement(variable);
            if (variableReplacement != null) {
                sb.replace(startIndex, endIndex + 1, variableReplacement);
                endIndex = startIndex + variableReplacement.length();
            } else {
                AbstractDomVariableReplacer.handleMissingVariable(sb.substring(startIndex, endIndex + 1), node.getLocalName(), failFast);
            }
            startIndex = sb.indexOf(replacerPrefix, endIndex);
        }
        return sb.toString();
    }

    void replaceVariableInNodeValue(Node node, ConfigReplacer replacer, boolean failFast) {
        String value = node.getNodeValue();
        if (value != null) {
            String replacedValue = AbstractDomVariableReplacer.replaceValue(node, replacer, failFast, value);
            node.setNodeValue(replacedValue);
        }
    }

    private static void handleMissingVariable(String variable, String nodeName, boolean failFast) throws ConfigurationException {
        String message = String.format("Could not find a replacement for '%s' on node '%s'", variable, nodeName);
        if (failFast) {
            throw new ConfigurationException(message);
        }
        LOGGER.warning(message);
    }
}

