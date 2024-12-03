/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.HTMLProcessor;
import com.opensymphony.module.sitemesh.html.State;
import com.opensymphony.module.sitemesh.html.StateTransitionRule;
import com.opensymphony.module.sitemesh.html.rules.BodyTagRule;
import com.opensymphony.module.sitemesh.html.rules.ContentBlockExtractingRule;
import com.opensymphony.module.sitemesh.html.rules.FramesetRule;
import com.opensymphony.module.sitemesh.html.rules.HeadExtractingRule;
import com.opensymphony.module.sitemesh.html.rules.HtmlAttributesRule;
import com.opensymphony.module.sitemesh.html.rules.MSOfficeDocumentPropertiesRule;
import com.opensymphony.module.sitemesh.html.rules.MetaTagRule;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;
import com.opensymphony.module.sitemesh.html.rules.ParameterExtractingRule;
import com.opensymphony.module.sitemesh.html.rules.TitleExtractingRule;
import com.opensymphony.module.sitemesh.parser.TokenizedHTMLPage;
import java.io.IOException;

public class HTMLPageParser
implements PageParser {
    public Page parse(char[] buffer) throws IOException {
        return this.parse(new DefaultSitemeshBuffer(buffer));
    }

    public Page parse(SitemeshBuffer buffer) throws IOException {
        SitemeshBufferFragment.Builder head = SitemeshBufferFragment.builder().setBuffer(buffer).setLength(0);
        SitemeshBufferFragment.Builder body = SitemeshBufferFragment.builder().setBuffer(buffer);
        TokenizedHTMLPage page = new TokenizedHTMLPage(buffer);
        HTMLProcessor processor = new HTMLProcessor(buffer, body);
        State html = processor.defaultState();
        html.addRule(new HeadExtractingRule(head));
        html.addRule(new BodyTagRule(page, body));
        html.addRule(new TitleExtractingRule(page));
        html.addRule(new FramesetRule(page));
        this.addUserDefinedRules(html, page);
        processor.process();
        page.setBody(body.build());
        page.setHead(head.build());
        return page;
    }

    protected void addUserDefinedRules(State html, PageBuilder page) {
        State xml = new State();
        html.addRule(new StateTransitionRule("xml", xml));
        html.addRule(new HtmlAttributesRule(page));
        html.addRule(new MetaTagRule(page));
        html.addRule(new ParameterExtractingRule(page));
        html.addRule(new ContentBlockExtractingRule(page));
        xml.addRule(new MSOfficeDocumentPropertiesRule(page));
    }
}

