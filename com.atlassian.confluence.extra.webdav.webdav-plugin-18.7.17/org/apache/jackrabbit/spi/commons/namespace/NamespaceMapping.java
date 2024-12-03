/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.namespace;

import java.util.HashMap;
import java.util.Map;
import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;

public class NamespaceMapping
implements NamespaceResolver {
    private final Map<String, String> prefixToURI = new HashMap<String, String>();
    private final Map<String, String> URIToPrefix = new HashMap<String, String>();
    private final NamespaceResolver base;

    public NamespaceMapping() {
        this.base = null;
    }

    public NamespaceMapping(NamespaceResolver base) {
        this.base = base;
    }

    @Override
    public String getPrefix(String uri) throws NamespaceException {
        if (this.URIToPrefix.containsKey(uri)) {
            return this.URIToPrefix.get(uri);
        }
        if (this.base == null) {
            throw new NamespaceException("No prefix for URI '" + uri + "' declared.");
        }
        return this.base.getPrefix(uri);
    }

    @Override
    public String getURI(String prefix) throws NamespaceException {
        if (this.prefixToURI.containsKey(prefix)) {
            return this.prefixToURI.get(prefix);
        }
        if (this.base == null) {
            throw new NamespaceException("No URI for prefix '" + prefix + "' declared.");
        }
        return this.base.getURI(prefix);
    }

    public boolean hasPrefix(String prefix) {
        return this.prefixToURI.containsKey(prefix);
    }

    public void setMapping(String prefix, String uri) throws NamespaceException {
        if (prefix == null) {
            throw new NamespaceException("Prefix must not be null");
        }
        if (uri == null) {
            throw new NamespaceException("URI must not be null");
        }
        if (this.URIToPrefix.containsKey(uri)) {
            this.prefixToURI.remove(this.URIToPrefix.remove(uri));
        }
        if (this.prefixToURI.containsKey(prefix)) {
            this.URIToPrefix.remove(this.prefixToURI.remove(prefix));
        }
        this.prefixToURI.put(prefix, uri);
        this.URIToPrefix.put(uri, prefix);
    }

    public String removeMapping(String uri) {
        String prefix = this.URIToPrefix.remove(uri);
        if (prefix != null) {
            this.prefixToURI.remove(prefix);
        }
        return prefix;
    }

    public Map<String, String> getPrefixToURIMapping() {
        return new HashMap<String, String>(this.prefixToURI);
    }

    public Map<String, String> getURIToPrefixMapping() {
        return new HashMap<String, String>(this.URIToPrefix);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NamespaceMapping) {
            NamespaceMapping other = (NamespaceMapping)obj;
            return this.getPrefixToURIMapping().equals(other.getPrefixToURIMapping()) && this.getURIToPrefixMapping().equals(other.getURIToPrefixMapping());
        }
        return false;
    }

    public String toString() {
        String s = "";
        for (Map.Entry<String, String> entry : this.prefixToURI.entrySet()) {
            String prefix = entry.getKey();
            String uri = entry.getValue();
            s = s + "'" + prefix + "' == '" + uri + "'\n";
        }
        return s;
    }
}

