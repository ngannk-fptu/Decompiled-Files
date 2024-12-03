/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder.impl;

import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlNamespace;

public class XmlNamespaceImpl
implements XmlNamespace {
    private String namespaceName;
    private String prefix;

    XmlNamespaceImpl(String namespaceName) {
        if (namespaceName == null) {
            throw new XmlBuilderException("namespace name can not be null");
        }
        this.namespaceName = namespaceName;
    }

    XmlNamespaceImpl(String prefix, String namespaceName) {
        this.prefix = prefix;
        if (namespaceName == null) {
            throw new XmlBuilderException("namespace name can not be null");
        }
        if (prefix != null && prefix.indexOf(58) != -1) {
            throw new XmlBuilderException("prefix '" + prefix + "' for namespace '" + namespaceName + "' can not contain colon (:)");
        }
        this.namespaceName = namespaceName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getNamespaceName() {
        return this.namespaceName;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof XmlNamespace)) {
            return false;
        }
        XmlNamespace otherNamespace = (XmlNamespace)other;
        return this.getNamespaceName().equals(otherNamespace.getNamespaceName());
    }

    public String toString() {
        return "{prefix='" + this.prefix + "',namespaceName='" + this.namespaceName + "'}";
    }
}

