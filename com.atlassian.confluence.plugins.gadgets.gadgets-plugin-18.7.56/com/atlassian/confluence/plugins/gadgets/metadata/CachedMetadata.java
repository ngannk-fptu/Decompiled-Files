/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.Collection;
import java.util.Set;

class CachedMetadata {
    private final Set<URI> gadgetUris;
    private final Collection<MacroMetadata> macroMetadata;

    public CachedMetadata(Set<URI> gadgetUris, Collection<MacroMetadata> macroMetadata) {
        this.gadgetUris = (Set)Preconditions.checkNotNull(gadgetUris);
        this.macroMetadata = (Collection)Preconditions.checkNotNull(macroMetadata);
    }

    boolean matchesGadgetUris(Set<URI> gadgetUris) {
        return this.gadgetUris.equals(gadgetUris);
    }

    public Collection<URI> getGadgetUris() {
        return this.gadgetUris;
    }

    public Collection<MacroMetadata> getMacroMetadata() {
        return this.macroMetadata;
    }
}

