/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.UnmarshalMarshalFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroIdSupplier;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.content.render.xhtml.links.LinkMarshallingFactory;
import com.atlassian.confluence.content.render.xhtml.migration.DelegatingMigrationAwareFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.macro.AlwaysTransformMacroBody;
import com.atlassian.confluence.content.render.xhtml.storage.macro.DefaultStorageMacroBodyParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.DelegatingStorageMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.DelegatingStorageMacroUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroBodyParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV1Marshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV1Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV2Marshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV2Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public class StorageMacroFragmentTransformerFactory {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlOutputFactory;
    private final MacroParameterTypeParser macroParameterTypeParser;
    private final MacroMetadataManager macroMetadataManager;
    private final LinkMarshallingFactory linkMarshallingFactory;
    private final MacroManager macroManager;
    private final StorageMacroBodyParser storageMacroBodyParser;
    private final MacroIdSupplier macroIdSupplier;
    private final MacroSchemaMigrator macroSchemaMigrator;

    public StorageMacroFragmentTransformerFactory(XmlEventReaderFactory xmlEventReaderFactory, XmlOutputFactory xmlOutputFactory, MacroParameterTypeParser macroParameterTypeParser, MacroMetadataManager macroMetadataManager, LinkMarshallingFactory linkMarshallingFactory, MacroManager macroManager, StorageMacroBodyParser storageMacroBodyParser, MacroIdSupplier macroIdSupplier, MacroSchemaMigrator macroSchemaMigrator) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.macroParameterTypeParser = macroParameterTypeParser;
        this.macroMetadataManager = macroMetadataManager;
        this.linkMarshallingFactory = linkMarshallingFactory;
        this.macroManager = macroManager;
        this.storageMacroBodyParser = storageMacroBodyParser;
        this.macroIdSupplier = macroIdSupplier;
        this.macroSchemaMigrator = macroSchemaMigrator;
    }

    private StorageMacroV1Unmarshaller createStorageMacroV1Unmarshaller() {
        return new StorageMacroV1Unmarshaller(this.xmlEventReaderFactory, this.macroParameterTypeParser, this.storageMacroBodyParser);
    }

    private StorageMacroV2Unmarshaller createStorageMacroV2Unmarshaller(Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller) {
        return new StorageMacroV2Unmarshaller(this.xmlEventReaderFactory, this.macroMetadataManager, resourceIdentifierUnmarshaller, this.linkMarshallingFactory.getStorageUnmarshaller(), new DefaultStorageMacroBodyParser(new AlwaysTransformMacroBody(), null, this.xmlOutputFactory), this.macroManager);
    }

    private StorageMacroV1Marshaller createStorageMacroV1Marshaller() {
        return new StorageMacroV1Marshaller(this.xmlOutputFactory);
    }

    private StorageMacroV2Marshaller createStorageMacroV2Marshaller(Marshaller<ResourceIdentifier> resourceIdentifierMarshaller) {
        return new StorageMacroV2Marshaller(this.xmlOutputFactory, this.macroMetadataManager, resourceIdentifierMarshaller, this.linkMarshallingFactory.getStorageMarshaller(), this.macroParameterTypeParser, this.macroIdSupplier, this.macroSchemaMigrator, this.macroManager);
    }

    public FragmentTransformer createStorageMacroFragmentTransformer(Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller, Marshaller<ResourceIdentifier> resourceIdentifierMarshaller) {
        DelegatingStorageMacroUnmarshaller unmarshaller = new DelegatingStorageMacroUnmarshaller(this.createStorageMacroV1Unmarshaller(), this.createStorageMacroV2Unmarshaller(resourceIdentifierUnmarshaller));
        DelegatingStorageMacroMarshaller migrationAwareMarshaller = new DelegatingStorageMacroMarshaller(this.createStorageMacroV1Marshaller(), this.createStorageMacroV2Marshaller(resourceIdentifierMarshaller), this.macroMetadataManager, this.macroManager);
        return new DelegatingMigrationAwareFragmentTransformer(new UnmarshalMarshalFragmentTransformer<MacroDefinition>(unmarshaller, migrationAwareMarshaller), migrationAwareMarshaller);
    }
}

