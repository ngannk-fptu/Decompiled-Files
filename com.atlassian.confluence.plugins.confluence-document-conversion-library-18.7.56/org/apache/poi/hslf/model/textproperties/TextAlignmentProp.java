/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.util.GenericRecordUtil;

public class TextAlignmentProp
extends TextProp {
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    public static final int JUSTIFY = 3;
    public static final int DISTRIBUTED = 4;
    public static final int THAIDISTRIBUTED = 5;
    public static final int JUSTIFYLOW = 6;

    public TextAlignmentProp() {
        super(2, 2048, "alignment");
    }

    public TextAlignmentProp(TextAlignmentProp other) {
        super(other);
    }

    public TextParagraph.TextAlign getTextAlign() {
        switch (this.getValue()) {
            default: {
                return TextParagraph.TextAlign.LEFT;
            }
            case 1: {
                return TextParagraph.TextAlign.CENTER;
            }
            case 2: {
                return TextParagraph.TextAlign.RIGHT;
            }
            case 3: {
                return TextParagraph.TextAlign.JUSTIFY;
            }
            case 4: {
                return TextParagraph.TextAlign.DIST;
            }
            case 5: 
        }
        return TextParagraph.TextAlign.THAI_DIST;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "textAlign", this::getTextAlign);
    }

    @Override
    public TextAlignmentProp copy() {
        return new TextAlignmentProp(this);
    }
}

