/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.Serializable;

public class Mapping
implements Serializable {
    private String namespaceURI;
    private String prefix;

    public Mapping(String namespaceURI, String prefix) {
        this.setPrefix(prefix);
        this.setNamespaceURI(namespaceURI);
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI.intern();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix.intern();
    }
}

