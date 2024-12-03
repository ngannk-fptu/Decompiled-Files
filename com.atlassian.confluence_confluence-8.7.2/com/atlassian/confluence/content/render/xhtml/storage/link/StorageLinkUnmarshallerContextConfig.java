/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.storage.link;

import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.link.StoragePlainTextLinkBodyUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageRichTextLinkBodyUnmarshaller;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class StorageLinkUnmarshallerContextConfig {
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private Unmarshaller<ResourceIdentifier> storageResourceIdentifierUnmarshaller;
    @Resource
    private Unmarshaller<EmbeddedImage> storageEmbeddedImageUnmarshaller;
    @Resource
    private ResourceIdentifierContextUtility resourceIdentifierContextUtility;

    StorageLinkUnmarshallerContextConfig() {
    }

    @Bean
    Unmarshaller<Link> storageLinkUnmarshaller() {
        return new StorageLinkUnmarshaller(this.storageResourceIdentifierUnmarshaller, this.createLinkBodyUnmarshallers(), this.xmlEventReaderFactory, this.resourceIdentifierContextUtility);
    }

    private List<Unmarshaller<LinkBody>> createLinkBodyUnmarshallers() {
        return Arrays.asList(new StorageRichTextLinkBodyUnmarshaller(this.storageEmbeddedImageUnmarshaller, this.xmlFragmentOutputFactory, this.xmlEventReaderFactory), new StoragePlainTextLinkBodyUnmarshaller());
    }
}

