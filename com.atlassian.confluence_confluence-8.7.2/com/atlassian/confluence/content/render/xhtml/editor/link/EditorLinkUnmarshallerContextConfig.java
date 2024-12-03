/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.editor.link;

import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEntityExpander;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.editor.link.EditorLinkBodyUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.link.EditorLinkUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.Link;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EditorLinkUnmarshallerContextConfig {
    @Resource
    private IdAndTypeResourceIdentifierResolver idAndTypeResourceIdentifierResolver;
    @Resource
    private Unmarshaller<ResourceIdentifier> idAndTypeAnalyzingResourceIdentifierUnmarshaller;
    @Resource
    private Unmarshaller<ResourceIdentifier> actualLinkStateAnalyzingResourceIdentifierUnmarshaller;
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private Unmarshaller<EmbeddedImage> editorEmbeddedImageResourceUnmarshaller;
    @Resource
    private XmlEntityExpander xmlEntityExpander;
    @Resource
    private DarkFeaturesManager darkFeaturesManager;

    EditorLinkUnmarshallerContextConfig() {
    }

    @Bean
    Unmarshaller<Link> editorLinkUnmarshaller() {
        return new EditorLinkUnmarshaller(new EditorLinkBodyUnmarshaller(this.editorEmbeddedImageResourceUnmarshaller, this.xmlFragmentOutputFactory, this.xmlEventReaderFactory, this.xmlEntityExpander), this.actualLinkStateAnalyzingResourceIdentifierUnmarshaller, this.idAndTypeAnalyzingResourceIdentifierUnmarshaller, this.idAndTypeResourceIdentifierResolver, this.darkFeaturesManager);
    }
}

