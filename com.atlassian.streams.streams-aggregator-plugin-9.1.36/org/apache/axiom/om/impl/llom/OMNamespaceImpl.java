/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMNamespace;

public class OMNamespaceImpl
implements OMNamespace {
    private final String prefix;
    private final String uri;

    public OMNamespaceImpl(String uri, String prefix) {
        if (uri == null) {
            throw new IllegalArgumentException("Namespace URI may not be null");
        }
        this.uri = uri;
        this.prefix = prefix;
    }

    public boolean equals(String uri, String prefix) {
        return this.uri.equals(uri) && (this.prefix == null ? prefix == null : this.prefix.equals(prefix));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof OMNamespace)) {
            return false;
        }
        OMNamespace other = (OMNamespace)obj;
        String otherPrefix = other.getPrefix();
        return this.uri.equals(other.getNamespaceURI()) && (this.prefix == null ? otherPrefix == null : this.prefix.equals(otherPrefix));
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getName() {
        return this.uri;
    }

    public String getNamespaceURI() {
        return this.uri;
    }

    public int hashCode() {
        return this.uri.hashCode() ^ (this.prefix != null ? this.prefix.hashCode() : 0);
    }
}

