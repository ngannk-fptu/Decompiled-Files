/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.html;

import com.lowagie.text.xml.XmlPeer;
import java.util.Map;
import java.util.Properties;
import org.xml.sax.Attributes;

public class HtmlPeer
extends XmlPeer {
    public HtmlPeer(String name, String alias) {
        super(name, alias.toLowerCase());
    }

    @Override
    public void addAlias(String name, String alias) {
        this.attributeAliases.put(alias.toLowerCase(), name);
    }

    @Override
    public Properties getAttributes(Attributes attrs) {
        Properties attributes = new Properties();
        attributes.putAll((Map<?, ?>)this.attributeValues);
        if (this.defaultContent != null) {
            attributes.put("itext", this.defaultContent);
        }
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); ++i) {
                String attribute = this.getName(attrs.getQName(i).toLowerCase());
                String value = attrs.getValue(i);
                attributes.setProperty(attribute, value);
            }
        }
        return attributes;
    }
}

