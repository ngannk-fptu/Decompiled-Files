/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.view.inline;

import com.atlassian.confluence.content.render.xhtml.editor.inline.EmoticonDisplayMapper;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.view.inline.ViewEmoticonMarshaller;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class XhtmlViewInlineContextConfig {
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private EmoticonDisplayMapper emoticonDisplayMapper;
    @Resource
    private UserI18NBeanFactory userI18NBeanFactory;
    @Resource
    private HrefEvaluator hrefEvaluator;

    XhtmlViewInlineContextConfig() {
    }

    @Bean
    ViewEmoticonMarshaller viewEmoticonMarshaller() {
        return new ViewEmoticonMarshaller(this.xmlFragmentOutputFactory, this.emoticonDisplayMapper, this.userI18NBeanFactory, this.hrefEvaluator);
    }
}

