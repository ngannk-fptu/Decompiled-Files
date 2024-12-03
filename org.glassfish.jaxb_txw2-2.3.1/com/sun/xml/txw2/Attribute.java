/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2;

final class Attribute {
    final String nsUri;
    final String localName;
    Attribute next;
    final StringBuilder value = new StringBuilder();

    Attribute(String nsUri, String localName) {
        assert (nsUri != null && localName != null);
        this.nsUri = nsUri;
        this.localName = localName;
    }

    boolean hasName(String nsUri, String localName) {
        return this.localName.equals(localName) && this.nsUri.equals(nsUri);
    }
}

