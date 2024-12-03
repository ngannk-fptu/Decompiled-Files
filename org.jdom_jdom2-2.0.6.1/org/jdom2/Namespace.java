/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jdom2.IllegalNameException;
import org.jdom2.Verifier;

public final class Namespace
implements Serializable {
    private static final ConcurrentMap<String, ConcurrentMap<String, Namespace>> namespacemap = new ConcurrentHashMap<String, ConcurrentMap<String, Namespace>>(512, 0.75f, 64);
    public static final Namespace NO_NAMESPACE = new Namespace("", "");
    public static final Namespace XML_NAMESPACE = new Namespace("xml", "http://www.w3.org/XML/1998/namespace");
    private static final Namespace XMLNS_NAMESPACE = new Namespace("xmlns", "http://www.w3.org/2000/xmlns/");
    private final transient String prefix;
    private final transient String uri;
    private static final long serialVersionUID = 200L;

    public static Namespace getNamespace(String prefix, String uri) {
        String pfx;
        Namespace ns;
        if (uri == null) {
            if (prefix == null || "".equals(prefix)) {
                return NO_NAMESPACE;
            }
            throw new IllegalNameException("", "namespace", "Namespace URIs must be non-null and non-empty Strings");
        }
        ConcurrentMap<String, Namespace> urimap = (ConcurrentHashMap)namespacemap.get(uri);
        if (urimap == null) {
            String reason = Verifier.checkNamespaceURI(uri);
            if (reason != null) {
                throw new IllegalNameException(uri, "Namespace URI", reason);
            }
            urimap = new ConcurrentHashMap();
            ConcurrentMap xmap = namespacemap.putIfAbsent(uri, urimap);
            if (xmap != null) {
                urimap = xmap;
            }
        }
        if ((ns = (Namespace)urimap.get(prefix == null ? "" : prefix)) != null) {
            return ns;
        }
        if ("".equals(uri)) {
            throw new IllegalNameException("", "namespace", "Namespace URIs must be non-null and non-empty Strings");
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(uri)) {
            throw new IllegalNameException(uri, "Namespace URI", "The http://www.w3.org/XML/1998/namespace must be bound to only the 'xml' prefix.");
        }
        if ("http://www.w3.org/2000/xmlns/".equals(uri)) {
            throw new IllegalNameException(uri, "Namespace URI", "The http://www.w3.org/2000/xmlns/ must be bound to only the 'xmlns' prefix.");
        }
        String string = pfx = prefix == null ? "" : prefix;
        if ("xml".equals(pfx)) {
            throw new IllegalNameException(uri, "Namespace prefix", "The prefix xml (any case) can only be bound to only the 'http://www.w3.org/XML/1998/namespace' uri.");
        }
        if ("xmlns".equals(pfx)) {
            throw new IllegalNameException(uri, "Namespace prefix", "The prefix xmlns (any case) can only be bound to only the 'http://www.w3.org/2000/xmlns/' uri.");
        }
        String reason = Verifier.checkNamespacePrefix(pfx);
        if (reason != null) {
            throw new IllegalNameException(pfx, "Namespace prefix", reason);
        }
        ns = new Namespace(pfx, uri);
        Namespace prev = urimap.putIfAbsent(pfx, ns);
        if (prev != null) {
            ns = prev;
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

    private Object writeReplace() {
        return new NamespaceSerializationProxy(this.prefix, this.uri);
    }

    private Object readResolve() throws InvalidObjectException {
        throw new InvalidObjectException("Namespace is serialized through a proxy");
    }

    static {
        ConcurrentHashMap<String, Namespace> nmap = new ConcurrentHashMap<String, Namespace>();
        nmap.put(NO_NAMESPACE.getPrefix(), NO_NAMESPACE);
        namespacemap.put(NO_NAMESPACE.getURI(), nmap);
        ConcurrentHashMap<String, Namespace> xmap = new ConcurrentHashMap<String, Namespace>();
        xmap.put(XML_NAMESPACE.getPrefix(), XML_NAMESPACE);
        namespacemap.put(XML_NAMESPACE.getURI(), xmap);
        ConcurrentHashMap<String, Namespace> xnsmap = new ConcurrentHashMap<String, Namespace>();
        xnsmap.put(XMLNS_NAMESPACE.getPrefix(), XMLNS_NAMESPACE);
        namespacemap.put(XMLNS_NAMESPACE.getURI(), xnsmap);
    }

    private static final class NamespaceSerializationProxy
    implements Serializable {
        private static final long serialVersionUID = 200L;
        private final String pprefix;
        private final String puri;

        public NamespaceSerializationProxy(String pprefix, String puri) {
            this.pprefix = pprefix;
            this.puri = puri;
        }

        private Object readResolve() {
            return Namespace.getNamespace(this.pprefix, this.puri);
        }
    }
}

