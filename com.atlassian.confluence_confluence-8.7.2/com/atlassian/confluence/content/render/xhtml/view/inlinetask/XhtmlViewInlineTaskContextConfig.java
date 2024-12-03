/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.view.inlinetask;

import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.view.inlinetask.ViewInlineTaskMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.inlinetask.ViewInlineTaskUnmarshaller;
import com.atlassian.confluence.core.ContentPropertyManager;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class XhtmlViewInlineTaskContextConfig {
    @Resource
    private XMLOutputFactory xmlOutputFactory;
    @Resource
    private MarshallingRegistry registry;
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    private ContentPropertyManager contentPropertyManager;

    XhtmlViewInlineTaskContextConfig() {
    }

    @Bean
    ViewInlineTaskMarshaller viewInlineTaskMarshaller() {
        return new ViewInlineTaskMarshaller(this.xmlOutputFactory, this.registry);
    }

    @Bean
    ViewInlineTaskUnmarshaller viewInlineTaskUnmarshaller() {
        return new ViewInlineTaskUnmarshaller(this.xmlEventReaderFactory, this.contentPropertyManager);
    }
}

