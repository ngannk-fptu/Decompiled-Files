/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.text;

import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextTabAlignType;

public enum TabAlignment {
    CENTER(STTextTabAlignType.CTR),
    DECIMAL(STTextTabAlignType.DEC),
    LEFT(STTextTabAlignType.L),
    RIGHT(STTextTabAlignType.R);

    final STTextTabAlignType.Enum underlying;
    private static final HashMap<STTextTabAlignType.Enum, TabAlignment> reverse;

    private TabAlignment(STTextTabAlignType.Enum align) {
        this.underlying = align;
    }

    static TabAlignment valueOf(STTextTabAlignType.Enum align) {
        return reverse.get(align);
    }

    static {
        reverse = new HashMap();
        for (TabAlignment value : TabAlignment.values()) {
            reverse.put(value.underlying, value);
        }
    }
}

