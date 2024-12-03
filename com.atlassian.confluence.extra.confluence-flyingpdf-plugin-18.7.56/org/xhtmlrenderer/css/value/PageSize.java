/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.value;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.value.FSCssValue;

public class PageSize {
    public static final PageSize A5 = new PageSize(new FSCssValue(7, "148mm"), new FSCssValue(7, "210mm"));
    public static final PageSize A4 = new PageSize(new FSCssValue(7, "210mm"), new FSCssValue(7, "297mm"));
    public static final PageSize A3 = new PageSize(new FSCssValue(7, "297mm"), new FSCssValue(7, "420mm"));
    public static final PageSize B3 = new PageSize(new FSCssValue(7, "176mm"), new FSCssValue(7, "250mm"));
    public static final PageSize B4 = new PageSize(new FSCssValue(7, "250mm"), new FSCssValue(7, "353mm"));
    public static final PageSize LETTER = new PageSize(new FSCssValue(7, "8.5in"), new FSCssValue(7, "11in"));
    public static final PageSize LEGAL = new PageSize(new FSCssValue(7, "8.5in"), new FSCssValue(7, "14in"));
    public static final PageSize LEDGER = new PageSize(new FSCssValue(7, "11in"), new FSCssValue(7, "17in"));
    private static final Map SIZE_MAP = new HashMap();
    private CSSPrimitiveValue _pageWidth;
    private CSSPrimitiveValue _pageHeight;

    private PageSize(CSSPrimitiveValue width, CSSPrimitiveValue height) {
        this._pageWidth = width;
        this._pageHeight = height;
    }

    private PageSize() {
    }

    public CSSPrimitiveValue getPageHeight() {
        return this._pageHeight;
    }

    public CSSPrimitiveValue getPageWidth() {
        return this._pageWidth;
    }

    public static PageSize resolvePageSize(String pageSize) {
        return (PageSize)SIZE_MAP.get(pageSize);
    }

    static {
        SIZE_MAP.put("A3", A3);
        SIZE_MAP.put("A4", A4);
        SIZE_MAP.put("A5", A5);
        SIZE_MAP.put("B3", B3);
        SIZE_MAP.put("B4", B4);
        SIZE_MAP.put("letter", LETTER);
        SIZE_MAP.put("legal", LEGAL);
        SIZE_MAP.put("ledger", LEDGER);
    }
}

