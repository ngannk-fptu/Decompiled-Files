/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.HTMLProcessor;
import com.opensymphony.module.sitemesh.html.Tag;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class MultipassReplacementPageParser
implements PageParser {
    private final Page page;
    private final HttpServletResponse response;

    public MultipassReplacementPageParser(Page page, HttpServletResponse response) {
        this.page = page;
        this.response = response;
    }

    public Page parse(char[] buffer) throws IOException {
        return this.parse(new DefaultSitemeshBuffer(buffer));
    }

    public Page parse(SitemeshBuffer sitemeshBuffer) throws IOException {
        SitemeshBufferFragment.Builder builder = SitemeshBufferFragment.builder().setBuffer(sitemeshBuffer);
        HTMLProcessor processor = new HTMLProcessor(sitemeshBuffer, builder);
        processor.addRule(new BasicRule("sitemesh:multipass"){

            public void process(Tag tag) {
                this.currentBuffer().delete(tag.getPosition(), tag.getLength());
                String id = tag.getAttributeValue("id", true);
                if (!MultipassReplacementPageParser.this.page.isPropertySet("_sitemesh.removefrompage." + id)) {
                    this.currentBuffer().insert(tag.getPosition(), MultipassReplacementPageParser.this.page.getProperty(id));
                }
            }
        });
        processor.process();
        builder.build().writeTo(this.response.getWriter());
        return null;
    }
}

