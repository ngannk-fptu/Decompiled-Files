/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.util.Messages;

public enum JavaFeature {
    TEXT_BLOCKS(0x3B0000L, Messages.bind(Messages.text_block), new char[0][], false),
    PATTERN_MATCHING_IN_INSTANCEOF(0x3C0000L, Messages.bind(Messages.pattern_matching_instanceof), new char[0][], false),
    RECORDS(0x3C0000L, Messages.bind(Messages.records), new char[][]{TypeConstants.RECORD_RESTRICTED_IDENTIFIER}, false),
    SEALED_CLASSES(0x3C0000L, Messages.bind(Messages.sealed_types), new char[][]{TypeConstants.SEALED, TypeConstants.PERMITS}, true);

    final long compliance;
    final String name;
    final boolean isPreview;
    char[][] restrictedKeywords;

    public boolean isPreview() {
        return this.isPreview;
    }

    public String getName() {
        return this.name;
    }

    public long getCompliance() {
        return this.compliance;
    }

    public char[][] getRestrictedKeywords() {
        return this.restrictedKeywords;
    }

    public boolean isSupported(CompilerOptions options) {
        if (this.isPreview) {
            return options.enablePreviewFeatures;
        }
        return this.getCompliance() <= options.sourceLevel;
    }

    public boolean isSupported(long comp, boolean preview) {
        if (this.isPreview) {
            return preview;
        }
        return this.getCompliance() <= comp;
    }

    private JavaFeature(long compliance, String name, char[][] restrictedKeywords, boolean isPreview) {
        this.compliance = compliance;
        this.name = name;
        this.isPreview = isPreview;
        this.restrictedKeywords = restrictedKeywords;
    }
}

