/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroIdSupplier;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.content.render.xhtml.links.LinkMarshallingFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.macro.DelegatingStorageMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.DelegatingStorageMacroUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroBodyParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV1Marshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV1Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV2Marshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV2Unmarshaller;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class StorageMacroMarshallerContextConfig {
    @Resource
    private XmlOutputFactory xmlOutputFactory;
    @Resource
    private XmlEventReaderFactory xmlEventReaderFactory;
    @Resource
    private MacroMetadataManager macroMetadataManager;
    @Resource
    private Marshaller<ResourceIdentifier> storageResourceIdentifierMarshaller;
    @Resource
    private Unmarshaller<ResourceIdentifier> storageResourceIdentifierUnmarshaller;
    @Resource
    private MacroParameterTypeParser macroParameterTypeParser;
    @Resource
    private MacroIdSupplier macroIdSupplier;
    @Resource
    private MacroSchemaMigrator macroSchemaMigrator;
    @Resource
    private MacroManager xhtmlMacroManager;
    @Resource
    private LinkMarshallingFactory linkMarshallingFactory;
    @Resource
    private MarshallingRegistry marshallingRegistry;
    @Resource
    private StorageMacroBodyParser storageMacroBodyParser;

    StorageMacroMarshallerContextConfig() {
    }

    @Bean
    Marshaller<MacroDefinition> storageMacroMarshaller() {
        return new DelegatingStorageMacroMarshaller(new StorageMacroV1Marshaller(this.xmlOutputFactory), new StorageMacroV2Marshaller(this.xmlOutputFactory, this.macroMetadataManager, this.storageResourceIdentifierMarshaller, this.linkMarshallingFactory.getStorageMarshaller(), this.macroParameterTypeParser, this.macroIdSupplier, this.macroSchemaMigrator, this.xhtmlMacroManager), this.macroMetadataManager, this.xhtmlMacroManager, this.marshallingRegistry);
    }

    @Bean
    Unmarshaller<MacroDefinition> storageMacroUnmarshaller() {
        return new DelegatingStorageMacroUnmarshaller(new StorageMacroV1Unmarshaller(this.xmlEventReaderFactory, this.macroParameterTypeParser, this.storageMacroBodyParser), new StorageMacroV2Unmarshaller(this.xmlEventReaderFactory, this.macroMetadataManager, this.storageResourceIdentifierUnmarshaller, this.linkMarshallingFactory.getStorageUnmarshaller(), this.storageMacroBodyParser, this.xhtmlMacroManager));
    }
}

