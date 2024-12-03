/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.util.HashMap;
import org.jdom.IllegalNameException;
import org.jdom.NamespaceKey;
import org.jdom.Verifier;

public final class Namespace {
    private static final String CVS_ID = "@(#) $RCSfile: Namespace.java,v $ $Revision: 1.44 $ $Date: 2008/12/17 23:22:48 $ $Name:  $";
    private static HashMap namespaces;
    public static final Namespace NO_NAMESPACE;
    public static final Namespace XML_NAMESPACE;
    private String prefix;
    private String uri;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Namespace getNamespace(String prefix, String uri) {
        Namespace preexisting;
        if (prefix == null || prefix.trim().equals("")) {
            if (uri == null || uri.trim().equals("")) {
                return NO_NAMESPACE;
            }
            prefix = "";
        } else if (uri == null || uri.trim().equals("")) {
            uri = "";
        }
        NamespaceKey lookup = new NamespaceKey(prefix, uri);
        HashMap hashMap = namespaces;
        synchronized (hashMap) {
            preexisting = (Namespace)namespaces.get(lookup);
        }
        if (preexisting != null) {
            return preexisting;
        }
        String reason = Verifier.checkNamespacePrefix(prefix);
        if (reason != null) {
            throw new IllegalNameException(prefix, "Namespace prefix", reason);
        }
        reason = Verifier.checkNamespaceURI(uri);
        if (reason != null) {
            throw new IllegalNameException(uri, "Namespace URI", reason);
        }
        if (!prefix.equals("") && uri.equals("")) {
            throw new IllegalNameException("", "namespace", "Namespace URIs must be non-null and non-empty Strings");
        }
        if (prefix.equals("xml")) {
            throw new IllegalNameException(prefix, "Namespace prefix", "The xml prefix can only be bound to http://www.w3.org/XML/1998/namespace");
        }
        if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            throw new IllegalNameException(uri, "Namespace URI", "The http://www.w3.org/XML/1998/namespace must be bound to the xml prefix.");
        }
        Namespace ns = new Namespace(prefix, uri);
        HashMap hashMap2 = namespaces;
        synchronized (hashMap2) {
            namespaces.put(lookup, ns);
        }
        return ns;
    }

    public static Namespace getNamespace(String uri) {
        return Namespace.getNamespace("", uri);
    }

    private Namespace(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getURI() {
        return this.uri;
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (ob instanceof Namespace) {
            return this.uri.equals(((Namespace)ob).uri);
        }
        return false;
    }

    public String toString() {
        return "[Namespace: prefix \"" + this.prefix + "\" is mapped to URI \"" + this.uri + "\"]";
    }

    public int hashCode() {
        return this.uri.hashCode();
    }

    static {
        NO_NAMESPACE = new Namespace("", "");
        XML_NAMESPACE = new Namespace("xml", "http://www.w3.org/XML/1998/namespace");
        namespaces = new HashMap(16);
        namespaces.put(new NamespaceKey(NO_NAMESPACE), NO_NAMESPACE);
        namespaces.put(new NamespaceKey(XML_NAMESPACE), XML_NAMESPACE);
    }
}

