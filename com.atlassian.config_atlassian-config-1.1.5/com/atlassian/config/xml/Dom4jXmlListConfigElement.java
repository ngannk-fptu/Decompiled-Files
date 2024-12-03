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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;

public class Dom4jXmlListConfigElement
extends Dom4jXmlConfigElement<List<String>> {
    public Dom4jXmlListConfigElement(String name, Element context, AbstractDom4jXmlConfigurationPersister config) {
        super(name, context, config);
    }

    @Override
    public Class<List<String>> getObjectClass() {
        return List.class;
    }

    @Override
    public void saveConfig(List<String> list) throws ConfigurationException {
        Element listElement = this.getOrMakeElement(this.getPropertyName());
        for (String item : list) {
            Element itemElement = listElement.addElement("item");
            if (this.isUsingCData()) {
                itemElement.addCDATA(item);
                continue;
            }
            itemElement.setText(item);
        }
    }

    @Override
    public List<String> loadConfig() throws ConfigurationException {
        Element element = (Element)this.getContext().selectSingleNode(this.getPropertyName());
        ArrayList<String> list = new ArrayList<String>();
        Iterator iterator = element.elementIterator();
        while (iterator.hasNext()) {
            String item = (String)this.getConfiguration().getConfigElement(String.class, "item", iterator.next());
            list.add(item);
        }
        return list;
    }
}

