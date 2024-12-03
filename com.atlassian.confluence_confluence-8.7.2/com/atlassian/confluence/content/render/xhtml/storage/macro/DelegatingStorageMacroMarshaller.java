/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.MigrationAware;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV1Marshaller;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroV2Marshaller;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.count.MacroCounter;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public class DelegatingStorageMacroMarshaller
implements Marshaller<MacroDefinition>,
MigrationAware {
    private static final String MACRO_FORMAT_MIGRATED_CONTEXT_PROPERTY = "macro-format-migrated";
    private final StorageMacroV1Marshaller v1Marshaller;
    private final StorageMacroV2Marshaller v2Marshaller;
    private final MacroMetadataManager macroMetadataManager;
    private final MacroManager macroManager;

    public DelegatingStorageMacroMarshaller(StorageMacroV1Marshaller v1Marshaller, StorageMacroV2Marshaller v2Marshaller, MacroMetadataManager macroMetadataManager, MacroManager macroManager) {
        this.v1Marshaller = v1Marshaller;
        this.v2Marshaller = v2Marshaller;
        this.macroMetadataManager = macroMetadataManager;
        this.macroManager = macroManager;
    }

    public DelegatingStorageMacroMarshaller(StorageMacroV1Marshaller v1Marshaller, StorageMacroV2Marshaller v2Marshaller, MacroMetadataManager macroMetadataManager, MacroManager macroManager, MarshallingRegistry marshallingRegistry) {
        this(v1Marshaller, v2Marshaller, macroMetadataManager, macroManager);
        marshallingRegistry.register(this, MacroDefinition.class, MarshallingType.STORAGE);
    }

    @Override
    public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        MacroCounter macroCounter;
        String macroName = macroDefinition.getName();
        MacroMetadata metadata = this.macroMetadataManager.getMacroMetadataByName(macroName);
        if (conversionContext != null && (macroCounter = (MacroCounter)conversionContext.getProperty("macroCounter")) != null) {
            macroCounter.addMacroUsage(macroDefinition, this.macroManager.getMacroByName(macroName));
        }
        if (metadata == null) {
            return this.v1Marshaller.marshal(macroDefinition, conversionContext);
        }
        Streamable result = this.v2Marshaller.marshal(macroDefinition, conversionContext);
        if (!"2".equals(macroDefinition.getStorageVersion())) {
            conversionContext.setProperty(MACRO_FORMAT_MIGRATED_CONTEXT_PROPERTY, true);
        }
        return result;
    }

    @Override
    public boolean wasMigrationPerformed(ConversionContext conversionContext) {
        return conversionContext.getProperty(MACRO_FORMAT_MIGRATED_CONTEXT_PROPERTY) == Boolean.TRUE;
    }
}

