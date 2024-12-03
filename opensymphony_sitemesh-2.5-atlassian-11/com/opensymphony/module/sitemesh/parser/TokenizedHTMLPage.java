/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;
import com.opensymphony.module.sitemesh.parser.AbstractHTMLPage;
import java.io.IOException;
import java.io.Writer;

public class TokenizedHTMLPage
extends AbstractHTMLPage
implements PageBuilder {
    private SitemeshBufferFragment body;
    private SitemeshBufferFragment head;

    public TokenizedHTMLPage(SitemeshBuffer sitemeshBuffer) {
        super(sitemeshBuffer);
        this.addProperty("title", "");
    }

    public void setBody(SitemeshBufferFragment body) {
        this.body = body;
    }

    public void setHead(SitemeshBufferFragment head) {
        this.head = head;
    }

    public void writeHead(Writer out) throws IOException {
        if (out instanceof SitemeshWriter) {
            ((SitemeshWriter)((Object)out)).writeSitemeshBufferFragment(this.head);
        } else {
            this.head.writeTo(out);
        }
    }

    public void writeBody(Writer out) throws IOException {
        if (out instanceof SitemeshWriter) {
            ((SitemeshWriter)((Object)out)).writeSitemeshBufferFragment(this.body);
        } else {
            this.body.writeTo(out);
        }
    }

    public String getHead() {
        return this.head.getStringContent();
    }

    public String getBody() {
        return this.body.getStringContent();
    }
}

