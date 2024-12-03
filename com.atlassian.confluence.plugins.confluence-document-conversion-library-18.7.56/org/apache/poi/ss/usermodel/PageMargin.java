/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum PageMargin {
    LEFT(0),
    RIGHT(1),
    TOP(2),
    BOTTOM(3),
    HEADER(4),
    FOOTER(5);

    private static final Map<Short, PageMargin> PAGE_MARGIN_BY_LEGACY_API_VALUE;
    private final short legacyApiValue;

    private PageMargin(short legacyApiValue) {
        this.legacyApiValue = legacyApiValue;
    }

    public short getLegacyApiValue() {
        return this.legacyApiValue;
    }

    public static PageMargin getByShortValue(short legacyApiValue) {
        return PAGE_MARGIN_BY_LEGACY_API_VALUE.get(legacyApiValue);
    }

    static {
        PAGE_MARGIN_BY_LEGACY_API_VALUE = new HashMap<Short, PageMargin>();
        for (PageMargin margin : PageMargin.values()) {
            PAGE_MARGIN_BY_LEGACY_API_VALUE.put(margin.legacyApiValue, margin);
        }
    }
}

