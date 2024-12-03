/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 */
package com.atlassian.config.xml;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.xml.AbstractDom4jXmlConfigurationPersister;
import com.atlassian.config.xml.Dom4jXmlConfigElement;
import java.util.AbstractMap;
import java.util.Map;
import org.dom4j.Element;

public class Dom4jXmlMapEntryConfigElement
extends Dom4jXmlConfigElement<Map.Entry> {
    public Dom4jXmlMapEntryConfigElement(String name, Element context, AbstractDom4jXmlConfigurationPersister config) {
        super(name, context, config);
    }

    @Override
    public Class<Map.Entry> getObjectClass() {
        return Map.Entry.class;
    }

    @Override
    public void saveConfig(Map.Entry entry) throws ConfigurationException {
        String name = entry.getKey().toString();
        if (entry.getValue() == null) {
            return;
        }
        Element element = (Element)this.getContext().selectSingleNode(this.getPropertyName() + "[@name='" + name + "']");
        if (element == null) {
            element = this.getContext().addElement(this.getPropertyName());
            element.addAttribute("name", name);
        } else {
            element.clearContent();
        }
        if (this.isUsingCData()) {
            element.addCDATA(entry.getValue().toString());
        } else {
            element.setText(entry.getValue().toString());
        }
    }

    @Override
    public Map.Entry loadConfig() throws ConfigurationException {
        String key = this.getContext().attribute("name").getValue();
        if (key == null) {
            throw new ConfigurationException("The attribute 'name' must be specified for element: " + this.getPropertyName());
        }
        return new AbstractMap.SimpleEntry<String, String>(key, this.getContext().getText());
    }
}

