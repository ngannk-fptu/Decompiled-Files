/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import java.io.IOException;

public enum StandardFonts {
    COURIER(0, 0),
    COURIER_ITALIC(0, 2),
    COURIER_BOLD(0, 1),
    COURIER_BOLDITALIC(0, 3),
    LIBERATION_MONO("com/lowagie/text/pdf/fonts/liberation/LiberationMono-Regular.ttf"),
    LIBERATION_MONO_ITALIC("com/lowagie/text/pdf/fonts/liberation/LiberationMono-Italic.ttf"),
    LIBERATION_MONO_BOLD("com/lowagie/text/pdf/fonts/liberation/LiberationMono-Bold.ttf"),
    LIBERATION_MONO_BOLDITALIC("com/lowagie/text/pdf/fonts/liberation/LiberationMono-BoldItalic.ttf"),
    HELVETICA(1, 0),
    HELVETICA_ITALIC(1, 2),
    HELVETICA_BOLD(1, 1),
    HELVETICA_BOLDITALIC(1, 3),
    LIBERATION_SANS("com/lowagie/text/pdf/fonts/liberation/LiberationSans-Regular.ttf"),
    LIBERATION_SANS_ITALIC("com/lowagie/text/pdf/fonts/liberation/LiberationSans-Italic.ttf"),
    LIBERATION_SANS_BOLD("com/lowagie/text/pdf/fonts/liberation/LiberationSans-Bold.ttf"),
    LIBERATION_SANS_BOLDITALIC("com/lowagie/text/pdf/fonts/liberation/LiberationSans-BoldItalic.ttf"),
    TIMES(2, 0),
    TIMES_ITALIC(2, 2),
    TIMES_BOLD(2, 1),
    TIMES_BOLDITALIC(2, 3),
    LIBERATION_SERIF("com/lowagie/text/pdf/fonts/liberation/LiberationSerif-Regular.ttf"),
    LIBERATION_SERIF_ITALIC("com/lowagie/text/pdf/fonts/liberation/LiberationSerif-Italic.ttf"),
    LIBERATION_SERIF_BOLD("com/lowagie/text/pdf/fonts/liberation/LiberationSerif-Bold.ttf"),
    LIBERATION_SERIF_BOLDITALIC("com/lowagie/text/pdf/fonts/liberation/LiberationSerif-BoldItalic.ttf"),
    SYMBOL(3, -1),
    ZAPFDINGBATS(4, -1);

    private int family;
    private int style;
    private String trueTypeFile;

    private StandardFonts(int family, int style) {
        this.family = family;
        this.style = style;
    }

    private StandardFonts(String trueTypeFile) {
        this.trueTypeFile = trueTypeFile;
    }

    public Font create() throws IOException {
        return this.create(12);
    }

    public Font create(int size) throws IOException {
        Font font;
        if (this.trueTypeFile != null) {
            BaseFont baseFont = BaseFont.createFont(this.trueTypeFile, "Identity-H", false);
            font = new Font(baseFont, (float)size);
        } else {
            font = this.style == -1 ? new Font(this.family, (float)size) : new Font(this.family, (float)size, this.style);
        }
        return font;
    }
}

