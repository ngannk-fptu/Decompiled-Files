/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.property;

import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DavPropertyName
implements DavConstants,
XmlSerializable,
PropEntry {
    private static final Map<Namespace, Map<String, DavPropertyName>> cache = new HashMap<Namespace, Map<String, DavPropertyName>>();
    public static final DavPropertyName CREATIONDATE = DavPropertyName.create("creationdate");
    public static final DavPropertyName DISPLAYNAME = DavPropertyName.create("displayname");
    public static final DavPropertyName GETCONTENTLANGUAGE = DavPropertyName.create("getcontentlanguage");
    public static final DavPropertyName GETCONTENTLENGTH = DavPropertyName.create("getcontentlength");
    public static final DavPropertyName GETCONTENTTYPE = DavPropertyName.create("getcontenttype");
    public static final DavPropertyName GETETAG = DavPropertyName.create("getetag");
    public static final DavPropertyName GETLASTMODIFIED = DavPropertyName.create("getlastmodified");
    public static final DavPropertyName LOCKDISCOVERY = DavPropertyName.create("lockdiscovery");
    public static final DavPropertyName RESOURCETYPE = DavPropertyName.create("resourcetype");
    public static final DavPropertyName SOURCE = DavPropertyName.create("source");
    public static final DavPropertyName SUPPORTEDLOCK = DavPropertyName.create("supportedlock");
    public static final DavPropertyName ISCOLLECTION = DavPropertyName.create("iscollection");
    private final String name;
    private final Namespace namespace;

    public static synchronized DavPropertyName create(String name, Namespace namespace) {
        DavPropertyName ret;
        Map<String, DavPropertyName> map = cache.get(namespace);
        if (map == null) {
            map = new HashMap<String, DavPropertyName>();
            cache.put(namespace, map);
        }
        if ((ret = map.get(name)) == null) {
            if (namespace.equals(NAMESPACE)) {
                namespace = NAMESPACE;
            }
            ret = new DavPropertyName(name, namespace);
            map.put(name, ret);
        }
        return ret;
    }

    public static synchronized DavPropertyName create(String name) {
        return DavPropertyName.create(name, NAMESPACE);
    }

    public static synchronized DavPropertyName createFromXml(Element nameElement) {
        if (nameElement == null) {
            throw new IllegalArgumentException("Cannot build DavPropertyName from a 'null' element.");
        }
        String ns = nameElement.getNamespaceURI();
        if (ns == null) {
            return DavPropertyName.create(nameElement.getLocalName(), Namespace.EMPTY_NAMESPACE);
        }
        return DavPropertyName.create(nameElement.getLocalName(), Namespace.getNamespace(nameElement.getPrefix(), ns));
    }

    private DavPropertyName(String name, Namespace namespace) {
        if (name == null || namespace == null) {
            throw new IllegalArgumentException("Name and namespace must not be 'null' for a DavPropertyName.");
        }
        this.name = name;
        this.namespace = namespace;
    }

    public String getName() {
        return this.name;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public int hashCode() {
        return (this.name.hashCode() + this.namespace.hashCode()) % Integer.MAX_VALUE;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DavPropertyName) {
            DavPropertyName propName = (DavPropertyName)obj;
            return this.name.equals(propName.name) && this.namespace.equals(propName.namespace);
        }
        return false;
    }

    public String toString() {
        return DomUtil.getExpandedName(this.name, this.namespace);
    }

    @Override
    public Element toXml(Document document) {
        return DomUtil.createElement(document, this.name, this.namespace);
    }
}

