/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadataBuilder
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import com.atlassian.confluence.macro.browser.beans.MacroMetadataBuilder;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetMacroMetadata;

public class GadgetMacroMetadataBuilder
extends MacroMetadataBuilder {
    private String extendedDescription;
    private boolean nonHiddenUserPrefs;
    private boolean needsConfig;

    GadgetMacroMetadataBuilder() {
    }

    public GadgetMacroMetadataBuilder setNonHiddenUserPrefs(boolean nonHiddenUserPrefs) {
        this.nonHiddenUserPrefs = nonHiddenUserPrefs;
        return this;
    }

    public GadgetMacroMetadataBuilder setNeedsConfig(boolean needsConfig) {
        this.needsConfig = needsConfig;
        return this;
    }

    public GadgetMacroMetadataBuilder setAlternativeDescription(String alternativeDescription) {
        this.extendedDescription = alternativeDescription;
        return this;
    }

    String getExtendedDescription() {
        return this.extendedDescription;
    }

    boolean isNonHiddenUserPrefs() {
        return this.nonHiddenUserPrefs;
    }

    boolean isNeedsConfig() {
        return this.needsConfig;
    }

    public GadgetMacroMetadata build() {
        return new GadgetMacroMetadata(this);
    }
}

