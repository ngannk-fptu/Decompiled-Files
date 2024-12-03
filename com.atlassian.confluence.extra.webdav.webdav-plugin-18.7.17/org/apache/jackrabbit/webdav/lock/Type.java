/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Type
implements XmlSerializable {
    private static Map<String, Type> types = new HashMap<String, Type>();
    public static final Type WRITE = Type.create("write", DavConstants.NAMESPACE);
    private final String localName;
    private final Namespace namespace;
    private int hashCode = -1;

    private Type(String name, Namespace namespace) {
        this.localName = name;
        this.namespace = namespace;
    }

    @Override
    public Element toXml(Document document) {
        Element lockType = DomUtil.createElement(document, "locktype", DavConstants.NAMESPACE);
        DomUtil.addChildElement(lockType, this.localName, this.namespace);
        return lockType;
    }

    public int hashCode() {
        if (this.hashCode == -1) {
            StringBuilder b = new StringBuilder();
            b.append("LockType : {").append(this.namespace).append("}").append(this.localName);
            this.hashCode = b.toString().hashCode();
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Type) {
            Type other = (Type)obj;
            return this.localName.equals(other.localName) && this.namespace.equals(other.namespace);
        }
        return false;
    }

    public static Type createFromXml(Element lockType) {
        if (lockType != null && "locktype".equals(lockType.getLocalName())) {
            lockType = DomUtil.getFirstChildElement(lockType);
        }
        if (lockType == null) {
            throw new IllegalArgumentException("'null' is not valid lock type entry.");
        }
        Namespace namespace = Namespace.getNamespace(lockType.getPrefix(), lockType.getNamespaceURI());
        return Type.create(lockType.getLocalName(), namespace);
    }

    public static Type create(String localName, Namespace namespace) {
        String key = DomUtil.getExpandedName(localName, namespace);
        if (types.containsKey(key)) {
            return types.get(key);
        }
        Type type = new Type(localName, namespace);
        types.put(key, type);
        return type;
    }
}

