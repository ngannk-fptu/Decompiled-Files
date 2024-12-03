/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.storage.embed;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.embed.StorageEmbeddedImageMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.embed.StorageEmbeddedImageUnmarshaller;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class StorageEmbedContextConfig {
    @Resource
    private XmlStreamWriterTemplate xmlStreamWriterTemplate;
    @Resource
    private Marshaller<ResourceIdentifier> storageResourceIdentifierMarshaller;
    @Resource
    private MarshallingRegistry marshallingRegistry;
    @Resource
    private Unmarshaller<ResourceIdentifier> storageResourceIdentifierUnmarshaller;
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;

    StorageEmbedContextConfig() {
    }

    @Bean
    StorageEmbeddedImageMarshaller storageEmbeddedImageMarshaller() {
        return new StorageEmbeddedImageMarshaller(this.xmlStreamWriterTemplate, this.storageResourceIdentifierMarshaller, this.marshallingRegistry);
    }

    @Bean
    StorageEmbeddedImageUnmarshaller storageEmbeddedImageUnmarshaller() {
        return new StorageEmbeddedImageUnmarshaller(this.storageResourceIdentifierUnmarshaller, this.xmlEventReaderFactory);
    }
}

