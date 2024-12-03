/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import org.jdom.Namespace;

final class NamespaceKey {
    private static final String CVS_ID = "@(#) $RCSfile: NamespaceKey.java,v $ $Revision: 1.2 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";
    private String prefix;
    private String uri;
    private int hash;

    public NamespaceKey(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
        this.hash = prefix.hashCode();
    }

    public NamespaceKey(Namespace namespace) {
        this(namespace.getPrefix(), namespace.getURI());
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (ob instanceof NamespaceKey) {
            NamespaceKey other = (NamespaceKey)ob;
            return this.prefix.equals(other.prefix) && this.uri.equals(other.uri);
        }
        return false;
    }

    public int hashCode() {
        return this.hash;
    }

    public String toString() {
        return "[NamespaceKey: prefix \"" + this.prefix + "\" is mapped to URI \"" + this.uri + "\"]";
    }
}

