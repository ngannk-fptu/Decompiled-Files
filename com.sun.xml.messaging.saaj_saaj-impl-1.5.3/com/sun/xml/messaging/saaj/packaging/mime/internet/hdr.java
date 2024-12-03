/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import com.sun.xml.messaging.saaj.packaging.mime.Header;

class hdr
implements Header {
    String name;
    String line;

    hdr(String l) {
        int i = l.indexOf(58);
        this.name = i < 0 ? l.trim() : l.substring(0, i).trim();
        this.line = l;
    }

    hdr(String n, String v) {
        this.name = n;
        this.line = n + ": " + v;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        int j;
        int i = this.line.indexOf(58);
        if (i < 0) {
            return this.line;
        }
        if (this.name.equalsIgnoreCase("Content-Description")) {
            char c;
            for (j = i + 1; j < this.line.length() && ((c = this.line.charAt(j)) == '\t' || c == '\r' || c == '\n'); ++j) {
            }
        } else {
            char c;
            for (j = i + 1; j < this.line.length() && ((c = this.line.charAt(j)) == ' ' || c == '\t' || c == '\r' || c == '\n'); ++j) {
            }
        }
        return this.line.substring(j);
    }
}

