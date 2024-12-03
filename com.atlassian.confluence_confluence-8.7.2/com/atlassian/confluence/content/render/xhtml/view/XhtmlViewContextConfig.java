/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.view;

import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.view.TableStylingElementTransformer;
import com.atlassian.confluence.content.render.xhtml.view.ViewHeadingFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.view.ViewHtmlAnchorFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.view.ViewModelToRenderedClassMapper;
import com.atlassian.confluence.content.render.xhtml.view.ViewTableWrappingFragmentTransformer;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import javax.annotation.Resource;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class XhtmlViewContextConfig {
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    private SettingsManager settingsManager;
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private XMLEventFactory xmlEventFactory;
    @Resource
    private HrefEvaluator hrefEvaluator;
    @Resource
    private UserI18NBeanFactory userI18NBeanFactory;
    @Resource
    private DarkFeaturesManager darkFeaturesManager;

    XhtmlViewContextConfig() {
    }

    @Bean
    TableStylingElementTransformer tableStylingElementTransformer() {
        return new TableStylingElementTransformer();
    }

    @Bean
    ViewHeadingFragmentTransformer viewHeadingFragmentTransformer() {
        return new ViewHeadingFragmentTransformer(this.xmlEventReaderFactory);
    }

    @Bean
    ViewHtmlAnchorFragmentTransformer viewHtmlAnchorFragmentTransformer() {
        return new ViewHtmlAnchorFragmentTransformer(this.settingsManager, this.xmlFragmentOutputFactory, this.xmlEventFactory, this.xmlEventReaderFactory, this.hrefEvaluator, this.userI18NBeanFactory, this.darkFeaturesManager);
    }

    @Bean
    ViewTableWrappingFragmentTransformer viewTableWrappingFragmentTransformer() {
        return new ViewTableWrappingFragmentTransformer(this.xmlEventReaderFactory);
    }

    @Bean
    ViewModelToRenderedClassMapper viewModelToRenderedClassMapper() {
        return new ViewModelToRenderedClassMapper();
    }
}

