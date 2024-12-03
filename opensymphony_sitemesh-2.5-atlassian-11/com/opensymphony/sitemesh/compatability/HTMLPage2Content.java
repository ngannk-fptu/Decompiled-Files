/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.sitemesh.compatability;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.sitemesh.Content;
import java.io.IOException;
import java.io.Writer;

public class HTMLPage2Content
implements Content {
    private final HTMLPage page;

    public HTMLPage2Content(HTMLPage page) {
        this.page = page;
    }

    public void writeOriginal(Writer out) throws IOException {
        this.page.writePage(out);
    }

    public void writeBody(Writer out) throws IOException {
        this.page.writeBody(out);
    }

    public void writeHead(Writer out) throws IOException {
        this.page.writeHead(out);
    }

    public String getTitle() {
        return this.page.getTitle();
    }

    public String getProperty(String name) {
        return this.page.getProperty(name);
    }

    public String[] getPropertyKeys() {
        return this.page.getPropertyKeys();
    }

    public void addProperty(String name, String value) {
        this.page.addProperty(name, value);
    }
}

