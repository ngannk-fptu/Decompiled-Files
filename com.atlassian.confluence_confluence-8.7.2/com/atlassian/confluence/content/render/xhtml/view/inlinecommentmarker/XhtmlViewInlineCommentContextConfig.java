/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.view.inlinecommentmarker;

import com.atlassian.confluence.content.render.xhtml.view.inlinecommentmarker.ViewInlineCommentMarkerMarshaller;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class XhtmlViewInlineCommentContextConfig {
    @Resource
    private XMLOutputFactory xmlOutputFactory;

    XhtmlViewInlineCommentContextConfig() {
    }

    @Bean
    ViewInlineCommentMarkerMarshaller viewInlineCommentMarkerMarshaller() {
        return new ViewInlineCommentMarkerMarshaller(this.xmlOutputFactory);
    }
}

