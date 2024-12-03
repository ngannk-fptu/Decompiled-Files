/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n.implementations;

import org.w3c.dom.Attr;

class NameSpaceSymbEntry
implements Cloneable {
    final String prefix;
    final String uri;
    final Attr n;
    String lastrendered = null;
    boolean rendered = false;

    NameSpaceSymbEntry(String name, Attr n, boolean rendered, String prefix) {
        this.uri = name;
        this.rendered = rendered;
        this.n = n;
        this.prefix = prefix;
    }

    public NameSpaceSymbEntry clone() {
        try {
            return (NameSpaceSymbEntry)super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

