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

public class Scope
implements XmlSerializable {
    private static final Map<String, Scope> scopes = new HashMap<String, Scope>();
    public static final Scope EXCLUSIVE = Scope.create("exclusive", DavConstants.NAMESPACE);
    public static final Scope SHARED = Scope.create("shared", DavConstants.NAMESPACE);
    private final String localName;
    private final Namespace namespace;

    private Scope(String localName, Namespace namespace) {
        this.localName = localName;
        this.namespace = namespace;
    }

    @Override
    public Element toXml(Document document) {
        Element lockScope = DomUtil.createElement(document, "lockscope", DavConstants.NAMESPACE);
        DomUtil.addChildElement(lockScope, this.localName, this.namespace);
        return lockScope;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.localName.hashCode();
        result = 31 * result + this.namespace.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Scope) {
            Scope other = (Scope)obj;
            return this.localName.equals(other.localName) && this.namespace.equals(other.namespace);
        }
        return false;
    }

    public static Scope createFromXml(Element lockScope) {
        if (lockScope != null && "lockscope".equals(lockScope.getLocalName())) {
            lockScope = DomUtil.getFirstChildElement(lockScope);
        }
        if (lockScope == null) {
            throw new IllegalArgumentException("'null' is not a valid lock scope entry.");
        }
        Namespace namespace = Namespace.getNamespace(lockScope.getPrefix(), lockScope.getNamespaceURI());
        return Scope.create(lockScope.getLocalName(), namespace);
    }

    public static Scope create(String localName, Namespace namespace) {
        String key = DomUtil.getExpandedName(localName, namespace);
        if (scopes.containsKey(key)) {
            return scopes.get(key);
        }
        Scope scope = new Scope(localName, namespace);
        scopes.put(key, scope);
        return scope;
    }
}

