/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Branch
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 *  org.dom4j.XPath
 */
package com.atlassian.config.xml;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.xml.AbstractDom4jXmlConfigurationPersister;
import com.atlassian.config.xml.Dom4jXmlConfigElement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.dom4j.Branch;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

public class Dom4jXmlMapConfigElement
extends Dom4jXmlConfigElement<Map> {
    public Dom4jXmlMapConfigElement(String name, Element context, AbstractDom4jXmlConfigurationPersister config) {
        super(name, context, config);
    }

    @Override
    public Class<Map> getObjectClass() {
        return Map.class;
    }

    @Override
    public void saveConfig(Map map) throws ConfigurationException {
        Element node = DocumentHelper.makeElement((Branch)this.getContext(), (String)this.getPropertyName());
        for (Map.Entry entry : map.entrySet()) {
            this.getConfiguration().addConfigElement(entry, "property", node);
        }
    }

    @Override
    public Map loadConfig() throws ConfigurationException {
        XPath xpath = DocumentHelper.createXPath((String)("/" + this.getContext().getName() + "/" + this.getPropertyName()));
        Element element = (Element)xpath.selectSingleNode((Object)this.getContext());
        HashMap map = new HashMap();
        Iterator iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)this.getConfiguration().getConfigElement(Map.Entry.class, "property", (Element)iterator.next());
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}

