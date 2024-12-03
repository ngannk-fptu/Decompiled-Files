/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.view.excerpt;

import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.storage.MacroDefinitionTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.DefaultExcerpter;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.ExcerptHelper;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class XhtmlExcerpterContextConfig {
    @Resource
    XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    HtmlToXmlConverter htmlToXmlConverter;
    @Resource
    SettingsManager settingsManager;
    @Resource
    Renderer viewRenderer;
    @Resource
    Unmarshaller<EmbeddedImage> storageEmbeddedImageUnmarshaller;
    @Resource
    FragmentTransformer storageToEditorFragmentTransformer;
    @Resource
    AttachmentResourceIdentifierResolver attachmentResourceIdentifierResolver;
    @Resource
    DataSourceFactory dataSourceFactory;
    @Resource
    ThumbnailManager thumbnailManager;
    @Resource
    MacroDefinitionTransformer macroDefinitionTransformer;
    @Resource
    ExcerptHelper excerptHelper;
    @Resource
    I18NBeanFactory i18NBeanFactory;
    @Resource
    LocaleManager localeManager;

    XhtmlExcerpterContextConfig() {
    }

    @Bean
    @AvailableToPlugins(interfaces={Excerpter.class})
    DefaultExcerpter excerpter() {
        return new DefaultExcerpter(this.xmlFragmentOutputFactory, this.xmlEventReaderFactory, this.htmlToXmlConverter, this.settingsManager, this.viewRenderer, this.storageEmbeddedImageUnmarshaller, this.storageToEditorFragmentTransformer, this.attachmentResourceIdentifierResolver, this.dataSourceFactory, this.thumbnailManager, this.macroDefinitionTransformer, this.excerptHelper, this.i18NBeanFactory, this.localeManager);
    }
}

