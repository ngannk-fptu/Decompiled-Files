/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.storage.link;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkMarshaller;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class StorageLinkMarshallerContextConfig {
    @Resource
    private XmlStreamWriterTemplate xmlStreamWriterTemplate;
    @Resource
    private Marshaller<ResourceIdentifier> storageResourceIdentifierMarshaller;
    @Resource
    private Marshaller<LinkBody> storageLinkBodyMarshaller;
    @Resource
    private MarshallingRegistry marshallingRegistry;

    StorageLinkMarshallerContextConfig() {
    }

    @Bean
    Marshaller<Link> storageLinkMarshaller() {
        return new StorageLinkMarshaller(this.xmlStreamWriterTemplate, this.storageResourceIdentifierMarshaller, this.storageLinkBodyMarshaller, this.marshallingRegistry);
    }
}

