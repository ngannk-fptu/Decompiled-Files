/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

public class XWPFHyperlink {
    String id;
    String url;

    public XWPFHyperlink(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return this.id;
    }

    public String getURL() {
        return this.url;
    }
}

