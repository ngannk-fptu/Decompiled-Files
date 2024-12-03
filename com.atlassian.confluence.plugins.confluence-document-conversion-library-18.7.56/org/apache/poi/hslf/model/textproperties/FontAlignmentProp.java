/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.util.GenericRecordUtil;

public class FontAlignmentProp
extends TextProp {
    public static final String NAME = "fontAlign";
    public static final int BASELINE = 0;
    public static final int TOP = 1;
    public static final int CENTER = 2;
    public static final int BOTTOM = 3;

    public FontAlignmentProp() {
        super(2, 65536, NAME);
    }

    public FontAlignmentProp(FontAlignmentProp other) {
        super(other);
    }

    public TextParagraph.FontAlign getFontAlign() {
        switch (this.getValue()) {
            default: {
                return TextParagraph.FontAlign.AUTO;
            }
            case 0: {
                return TextParagraph.FontAlign.BASELINE;
            }
            case 1: {
                return TextParagraph.FontAlign.TOP;
            }
            case 2: {
                return TextParagraph.FontAlign.CENTER;
            }
            case 3: 
        }
        return TextParagraph.FontAlign.BOTTOM;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), NAME, this::getFontAlign);
    }

    @Override
    public FontAlignmentProp copy() {
        return new FontAlignmentProp(this);
    }
}

