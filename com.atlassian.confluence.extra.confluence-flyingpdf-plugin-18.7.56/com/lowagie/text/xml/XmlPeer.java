/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml;

import java.util.Map;
import java.util.Properties;
import org.xml.sax.Attributes;

public class XmlPeer {
    protected String tagname;
    protected String customTagname;
    protected Properties attributeAliases = new Properties();
    protected Properties attributeValues = new Properties();
    protected String defaultContent = null;

    public XmlPeer(String name, String alias) {
        this.tagname = name;
        this.customTagname = alias;
    }

    public String getTag() {
        return this.tagname;
    }

    public String getAlias() {
        return this.customTagname;
    }

    public Properties getAttributes(Attributes attrs) {
        Properties attributes = new Properties();
        attributes.putAll((Map<?, ?>)this.attributeValues);
        if (this.defaultContent != null) {
            attributes.put("itext", this.defaultContent);
        }
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); ++i) {
                String attribute = this.getName(attrs.getQName(i));
                attributes.setProperty(attribute, attrs.getValue(i));
            }
        }
        return attributes;
    }

    public void addAlias(String name, String alias) {
        this.attributeAliases.put(alias, name);
    }

    public void addValue(String name, String value) {
        this.attributeValues.put(name, value);
    }

    public void setContent(String content) {
        this.defaultContent = content;
    }

    public String getName(String name) {
        String value = this.attributeAliases.getProperty(name);
        if (value != null) {
            return value;
        }
        return name;
    }

    public Properties getDefaultValues() {
        return this.attributeValues;
    }
}

