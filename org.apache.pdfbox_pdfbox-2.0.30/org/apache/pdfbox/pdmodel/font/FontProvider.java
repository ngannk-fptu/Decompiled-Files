/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import java.util.List;
import org.apache.pdfbox.pdmodel.font.FontInfo;

public abstract class FontProvider {
    public abstract String toDebugString();

    public abstract List<? extends FontInfo> getFontInfo();
}

