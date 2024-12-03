/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 *  org.dom4j.Node
 */
package com.atlassian.config.xml;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.xml.AbstractDom4jXmlConfigurationPersister;
import com.atlassian.config.xml.Dom4jXmlConfigElement;
import org.dom4j.Element;
import org.dom4j.Node;

public class Dom4jXmlStringConfigElement
extends Dom4jXmlConfigElement<String> {
    public Dom4jXmlStringConfigElement(String name, Element context, AbstractDom4jXmlConfigurationPersister config) {
        super(name, context, config);
    }

    @Override
    public Class<String> getObjectClass() {
        return String.class;
    }

    @Override
    public void saveConfig(String object) throws ConfigurationException {
        Element element = this.getOrMakeElement(this.getPropertyName());
        if (this.isUsingCData()) {
            element.addCDATA(object);
        } else {
            element.setText(object);
        }
    }

    @Override
    public String loadConfig() throws ConfigurationException {
        Node n = this.getContext().selectSingleNode(this.getPropertyName());
        if (n != null) {
            return n.getText();
        }
        return this.getContext().getText();
    }
}

