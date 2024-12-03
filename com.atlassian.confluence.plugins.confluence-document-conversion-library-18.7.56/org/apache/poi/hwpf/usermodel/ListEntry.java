/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.util.Internal;

public final class ListEntry
extends Paragraph {
    @Internal
    ListEntry(PAPX papx, ParagraphProperties properties, Range parent) {
        super(papx, properties, parent);
    }
}

