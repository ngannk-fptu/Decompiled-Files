/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.property;

import java.util.Collection;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractDavProperty<T>
implements DavProperty<T> {
    private static Logger log = LoggerFactory.getLogger(AbstractDavProperty.class);
    private final DavPropertyName name;
    private final boolean isInvisibleInAllprop;

    public AbstractDavProperty(DavPropertyName name, boolean isInvisibleInAllprop) {
        this.name = name;
        this.isInvisibleInAllprop = isInvisibleInAllprop;
    }

    public int hashCode() {
        int hashCode = this.getName().hashCode();
        if (this.getValue() != null) {
            hashCode += this.getValue().hashCode();
        }
        return hashCode % Integer.MAX_VALUE;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DavProperty) {
            DavProperty prop = (DavProperty)obj;
            boolean equalName = this.getName().equals(prop.getName());
            boolean equalValue = this.getValue() == null ? prop.getValue() == null : this.getValue().equals(prop.getValue());
            return equalName && equalValue;
        }
        return false;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        Object value = this.getValue();
        if (value != null) {
            if (value instanceof XmlSerializable) {
                elem.appendChild(((XmlSerializable)value).toXml(document));
            } else if (value instanceof Node) {
                Node n = document.importNode((Node)value, true);
                elem.appendChild(n);
            } else if (value instanceof Node[]) {
                for (int i = 0; i < ((Node[])value).length; ++i) {
                    Node n = document.importNode(((Node[])value)[i], true);
                    elem.appendChild(n);
                }
            } else if (value instanceof Collection) {
                for (Object entry : (Collection)value) {
                    if (entry instanceof XmlSerializable) {
                        elem.appendChild(((XmlSerializable)entry).toXml(document));
                        continue;
                    }
                    if (entry instanceof Node) {
                        Node n = document.importNode((Node)entry, true);
                        elem.appendChild(n);
                        continue;
                    }
                    DomUtil.setText(elem, entry.toString());
                }
            } else {
                DomUtil.setText(elem, value.toString());
            }
        }
        return elem;
    }

    @Override
    public DavPropertyName getName() {
        return this.name;
    }

    @Override
    public boolean isInvisibleInAllprop() {
        return this.isInvisibleInAllprop;
    }
}

