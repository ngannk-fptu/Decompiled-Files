/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sitemesh.SitemeshBuffer
 *  com.opensymphony.module.sitemesh.SitemeshBufferFragment
 *  com.opensymphony.module.sitemesh.SitemeshBufferFragment$Builder
 *  com.opensymphony.module.sitemesh.html.HTMLProcessor
 *  com.opensymphony.module.sitemesh.html.State
 *  com.opensymphony.module.sitemesh.html.TagRule
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.impl.ExportImageDescriptor;
import com.atlassian.confluence.importexport.impl.ImageProcessingRule;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.HTMLProcessor;
import com.opensymphony.module.sitemesh.html.State;
import com.opensymphony.module.sitemesh.html.TagRule;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class HtmlImageParser {
    public Set<ExportImageDescriptor> parse(SitemeshBuffer sitemeshBuffer, Writer writer, ImageProcessingRule rule) throws IOException {
        SitemeshBufferFragment.Builder output = SitemeshBufferFragment.builder().setBuffer(sitemeshBuffer);
        HTMLProcessor htmlProcessor = new HTMLProcessor(sitemeshBuffer, output);
        State defaultState = htmlProcessor.defaultState();
        defaultState.addRule((TagRule)rule);
        htmlProcessor.process();
        writer.write(output.build().getStringContent());
        return rule.getExtractedUrls();
    }
}

