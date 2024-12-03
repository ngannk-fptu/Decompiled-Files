/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.parser.AbstractHTMLPage;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public final class FastPage
extends AbstractHTMLPage {
    private String head;
    private String body;

    public FastPage(SitemeshBuffer sitemeshBuffer, Map sitemeshProps, Map htmlProps, Map metaProps, Map bodyProps, String title, String head, String body, boolean frameSet) {
        super(sitemeshBuffer);
        this.head = head;
        this.body = body;
        this.setFrameSet(frameSet);
        this.addAttributeList("", htmlProps);
        this.addAttributeList("page.", sitemeshProps);
        this.addAttributeList("body.", bodyProps);
        this.addAttributeList("meta.", metaProps);
        this.addProperty("title", title);
    }

    public void writeHead(Writer out) throws IOException {
        out.write(this.head);
    }

    public void writeBody(Writer out) throws IOException {
        out.write(this.body);
    }

    private void addAttributeList(String prefix, Map attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return;
        }
        for (Map.Entry entry : attributes.entrySet()) {
            String name = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (value == null || value.trim().length() <= 0) continue;
            this.addProperty(prefix + name, value);
        }
    }

    public String getBody() {
        return this.body;
    }

    public String getHead() {
        return this.head;
    }
}

