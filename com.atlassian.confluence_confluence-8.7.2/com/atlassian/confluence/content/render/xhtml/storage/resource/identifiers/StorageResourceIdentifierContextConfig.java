/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.DelegatingResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.MakeRelativeAndDelegateResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageAttachmentResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageBlogPostResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageContentEntityResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StoragePageResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageShortcutResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageSpaceResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageUrlResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageUserResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageUserResourceIdentifierUnmarshaller;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class StorageResourceIdentifierContextConfig {
    @Resource
    private XmlStreamWriterTemplate xmlStreamWriterTemplate;
    @Resource
    private AttachmentManager attachmentManager;
    @Resource
    private ContentEntityManager contentEntityManager;
    @Resource
    private DarkFeaturesManager darkFeaturesManager;
    @Resource
    private ResourceIdentifierContextUtility resourceIdentifierContextUtility;

    StorageResourceIdentifierContextConfig() {
    }

    @Bean
    DelegatingResourceIdentifierMarshaller delegatingStorageResourceIdentifierMarshaller() {
        DelegatingResourceIdentifierMarshaller bean = new DelegatingResourceIdentifierMarshaller();
        bean.setPageResourceIdentifierMarshaller(this.storagePageResourceIdentifierMarshaller());
        bean.setBlogPostResourceIdentifierMarshaller(this.storageBlogPostResourceIdentifierMarshaller());
        bean.setUrlResourceIdentifierMarshaller(this.storageUrlResourceIdentifierMarshaller());
        bean.setShortcutResourceIdentifierMarshaller(this.storageShortcutResourceIdentifierMarshaller());
        bean.setUserResourceIdentifierMarshaller(this.storageUserResourceIdentifierMarshaller());
        bean.setSpaceResourceIdentifierMarshaller(this.storageSpaceResourceIdentifierMarshaller());
        bean.setContentEntityResourceIdentifierMarshaller(this.storageContentEntityResourceIdentifierMarshaller());
        bean.setAttachmentResourceIdentifierMarshaller(this.storageAttachmentResourceIdentifierMarshaller());
        return bean;
    }

    @Bean
    StoragePageResourceIdentifierMarshaller storagePageResourceIdentifierMarshaller() {
        return new StoragePageResourceIdentifierMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    StorageBlogPostResourceIdentifierMarshaller storageBlogPostResourceIdentifierMarshaller() {
        return new StorageBlogPostResourceIdentifierMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    StorageUrlResourceIdentifierMarshaller storageUrlResourceIdentifierMarshaller() {
        return new StorageUrlResourceIdentifierMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    StorageShortcutResourceIdentifierMarshaller storageShortcutResourceIdentifierMarshaller() {
        return new StorageShortcutResourceIdentifierMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    StorageUserResourceIdentifierMarshaller storageUserResourceIdentifierMarshaller() {
        return new StorageUserResourceIdentifierMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    StorageSpaceResourceIdentifierMarshaller storageSpaceResourceIdentifierMarshaller() {
        return new StorageSpaceResourceIdentifierMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    StorageContentEntityResourceIdentifierMarshaller storageContentEntityResourceIdentifierMarshaller() {
        return new StorageContentEntityResourceIdentifierMarshaller(this.xmlStreamWriterTemplate);
    }

    @Bean
    StorageAttachmentResourceIdentifierMarshaller storageAttachmentResourceIdentifierMarshaller() {
        return new StorageAttachmentResourceIdentifierMarshaller(this.xmlStreamWriterTemplate, this.storageAttachmentContainerResourceIdentifierMarshaller(), this.attachmentManager, this.contentEntityManager, this.darkFeaturesManager);
    }

    @Bean
    DelegatingResourceIdentifierMarshaller storageAttachmentContainerResourceIdentifierMarshaller() {
        DelegatingResourceIdentifierMarshaller bean = new DelegatingResourceIdentifierMarshaller();
        bean.setPageResourceIdentifierMarshaller(this.storagePageResourceIdentifierMarshaller());
        bean.setBlogPostResourceIdentifierMarshaller(this.storageBlogPostResourceIdentifierMarshaller());
        bean.setContentEntityResourceIdentifierMarshaller(this.storageContentEntityResourceIdentifierMarshaller());
        return bean;
    }

    @Bean
    StorageUserResourceIdentifierUnmarshaller storageUserResourceIdentifierUnmarshaller() {
        return new StorageUserResourceIdentifierUnmarshaller();
    }

    @Bean
    StorageResourceIdentifierUnmarshaller storageResourceIdentifierUnmarshaller() {
        return new StorageResourceIdentifierUnmarshaller();
    }

    @Bean
    MakeRelativeAndDelegateResourceIdentifierMarshaller storageResourceIdentifierMarshaller() {
        return new MakeRelativeAndDelegateResourceIdentifierMarshaller(this.delegatingStorageResourceIdentifierMarshaller(), this.resourceIdentifierContextUtility);
    }
}

