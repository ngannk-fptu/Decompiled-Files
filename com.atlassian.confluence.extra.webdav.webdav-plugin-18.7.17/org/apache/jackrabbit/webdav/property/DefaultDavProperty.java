/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.property;

import java.util.List;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DefaultDavProperty<T>
extends AbstractDavProperty<T> {
    private static Logger log = LoggerFactory.getLogger(DefaultDavProperty.class);
    private final T value;

    public DefaultDavProperty(String name, T value, Namespace namespace, boolean isInvisibleInAllprop) {
        super(DavPropertyName.create(name, namespace), isInvisibleInAllprop);
        this.value = value;
    }

    public DefaultDavProperty(String name, T value, Namespace namespace) {
        this(name, value, namespace, false);
    }

    public DefaultDavProperty(DavPropertyName name, T value, boolean isInvisibleInAllprop) {
        super(name, isInvisibleInAllprop);
        this.value = value;
    }

    public DefaultDavProperty(DavPropertyName name, T value) {
        this(name, value, false);
    }

    @Override
    public T getValue() {
        return this.value;
    }

    public static DefaultDavProperty<?> createFromXml(Element propertyElement) {
        Node n;
        List<Node> c;
        if (propertyElement == null) {
            throw new IllegalArgumentException("Cannot create a new DavProperty from a 'null' element.");
        }
        DavPropertyName name = DavPropertyName.createFromXml(propertyElement);
        DefaultDavProperty<Object> prop = !DomUtil.hasContent(propertyElement) ? new DefaultDavProperty<Object>(name, null, false) : ((c = DomUtil.getContent(propertyElement)).size() == 1 ? ((n = c.get(0)) instanceof Element ? new DefaultDavProperty<Element>(name, (Element)n, false) : new DefaultDavProperty<String>(name, n.getNodeValue(), false)) : new DefaultDavProperty<List<Node>>(name, c, false));
        return prop;
    }
}

