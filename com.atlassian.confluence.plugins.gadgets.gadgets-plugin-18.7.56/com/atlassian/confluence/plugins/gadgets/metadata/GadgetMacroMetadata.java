/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadataBuilder
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroMetadataBuilder;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetMacroMetadataBuilder;

public class GadgetMacroMetadata
extends MacroMetadata {
    private final boolean nonHiddenUserPrefs;
    private final boolean needsConfig;
    private final String extendedDescription;

    public static GadgetMacroMetadataBuilder builder() {
        return new GadgetMacroMetadataBuilder();
    }

    GadgetMacroMetadata(GadgetMacroMetadataBuilder builder) {
        super((MacroMetadataBuilder)builder);
        this.nonHiddenUserPrefs = builder.isNonHiddenUserPrefs();
        this.needsConfig = builder.isNeedsConfig();
        this.extendedDescription = builder.getExtendedDescription();
    }

    public boolean isNonHiddenUserPrefs() {
        return this.nonHiddenUserPrefs;
    }

    public boolean isNeedsConfig() {
        return this.needsConfig;
    }

    public String getExtendedDescription() {
        return this.extendedDescription;
    }
}

