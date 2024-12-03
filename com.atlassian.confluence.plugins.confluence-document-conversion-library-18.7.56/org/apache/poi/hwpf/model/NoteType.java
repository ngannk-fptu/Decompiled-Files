/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.util.Internal;

@Internal
public enum NoteType {
    ENDNOTE(46, 47),
    FOOTNOTE(2, 3);

    private final int fibDescriptorsFieldIndex;
    private final int fibTextPositionsFieldIndex;

    private NoteType(int fibDescriptorsFieldIndex, int fibTextPositionsFieldIndex) {
        this.fibDescriptorsFieldIndex = fibDescriptorsFieldIndex;
        this.fibTextPositionsFieldIndex = fibTextPositionsFieldIndex;
    }

    public int getFibDescriptorsFieldIndex() {
        return this.fibDescriptorsFieldIndex;
    }

    public int getFibTextPositionsFieldIndex() {
        return this.fibTextPositionsFieldIndex;
    }
}

