/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.util.StringBuilders
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.logging.log4j.core.config.plugins.visitors;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.core.config.plugins.visitors.AbstractPluginVisitor;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

public class PluginValueVisitor
extends AbstractPluginVisitor<PluginValue> {
    public PluginValueVisitor() {
        super(PluginValue.class);
    }

    @Override
    public Object visit(Configuration configuration, Node node, LogEvent event, StringBuilder log) {
        String name = ((PluginValue)this.annotation).value();
        String elementValue = node.getValue();
        String attributeValue = node.getAttributes().get(name);
        String rawValue = null;
        if (Strings.isNotEmpty((CharSequence)elementValue)) {
            if (Strings.isNotEmpty((CharSequence)attributeValue)) {
                LOGGER.error("Configuration contains {} with both attribute value ({}) AND element value ({}). Please specify only one value. Using the element value.", (Object)node.getName(), (Object)attributeValue, (Object)elementValue);
            }
            rawValue = elementValue;
        } else {
            rawValue = PluginValueVisitor.removeAttributeValue(node.getAttributes(), name, new String[0]);
        }
        String value = ((PluginValue)this.annotation).substitute() ? this.substitutor.replace(event, rawValue) : rawValue;
        StringBuilders.appendKeyDqValue((StringBuilder)log, (String)name, (Object)value);
        return value;
    }
}

