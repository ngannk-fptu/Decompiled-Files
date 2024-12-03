/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import org.xml.sax.Attributes;

public class NullAttributes
implements Attributes {
    public static final NullAttributes singleton = new NullAttributes();

    public int getLength() {
        return 0;
    }

    public String getURI(int index) {
        return null;
    }

    public String getLocalName(int index) {
        return null;
    }

    public String getQName(int index) {
        return null;
    }

    public String getType(int index) {
        return null;
    }

    public String getValue(int index) {
        return null;
    }

    public int getIndex(String uri, String localName) {
        return -1;
    }

    public int getIndex(String qName) {
        return -1;
    }

    public String getType(String uri, String localName) {
        return null;
    }

    public String getType(String qName) {
        return null;
    }

    public String getValue(String uri, String localName) {
        return null;
    }

    public String getValue(String qName) {
        return null;
    }
}

