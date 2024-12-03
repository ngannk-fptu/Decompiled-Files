/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HrefProperty
extends AbstractDavProperty<String[]> {
    private static Logger log = LoggerFactory.getLogger(HrefProperty.class);
    private final String[] value;

    public HrefProperty(DavPropertyName name, String value, boolean isInvisibleInAllprop) {
        super(name, isInvisibleInAllprop);
        this.value = new String[]{value};
    }

    public HrefProperty(DavPropertyName name, String[] value, boolean isInvisibleInAllprop) {
        super(name, isInvisibleInAllprop);
        this.value = value;
    }

    public HrefProperty(DavProperty<?> prop) {
        super(prop.getName(), prop.isInvisibleInAllprop());
        if (prop instanceof HrefProperty) {
            this.value = ((HrefProperty)prop).value;
        } else {
            ArrayList<String> hrefList = new ArrayList<String>();
            Object val = prop.getValue();
            if (val instanceof List) {
                for (Object entry : (List)val) {
                    if (entry instanceof Element && "href".equals(((Element)entry).getLocalName())) {
                        String href = DomUtil.getText((Element)entry);
                        if (href != null) {
                            hrefList.add(href);
                            continue;
                        }
                        log.warn("Valid DAV:href element expected instead of " + entry.toString());
                        continue;
                    }
                    log.warn("DAV: href element expected in the content of " + this.getName().toString());
                }
            } else if (val instanceof Element && "href".equals(((Element)val).getLocalName())) {
                String href = DomUtil.getTextTrim((Element)val);
                if (href != null) {
                    hrefList.add(href);
                } else {
                    log.warn("Valid DAV:href element expected instead of " + val.toString());
                }
            }
            this.value = hrefList.toArray(new String[hrefList.size()]);
        }
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        String[] value = this.getValue();
        if (value != null) {
            for (String href : value) {
                elem.appendChild(DomUtil.hrefToXml(href, document));
            }
        }
        return elem;
    }

    @Override
    public String[] getValue() {
        return this.value;
    }

    public List<String> getHrefs() {
        return this.value != null ? Arrays.asList(this.value) : new ArrayList();
    }
}

