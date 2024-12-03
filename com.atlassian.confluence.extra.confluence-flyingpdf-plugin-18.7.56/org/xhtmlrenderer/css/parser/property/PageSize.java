/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.parser.PropertyValue;

public class PageSize {
    public static final PageSize A5 = new PageSize(new PropertyValue(7, 148.0f, "148mm"), new PropertyValue(7, 210.0f, "210mm"));
    public static final PageSize A4 = new PageSize(new PropertyValue(7, 210.0f, "210mm"), new PropertyValue(7, 297.0f, "297mm"));
    public static final PageSize A3 = new PageSize(new PropertyValue(7, 297.0f, "297mm"), new PropertyValue(7, 420.0f, "420mm"));
    public static final PageSize B3 = new PageSize(new PropertyValue(7, 176.0f, "176mm"), new PropertyValue(7, 250.0f, "250mm"));
    public static final PageSize B4 = new PageSize(new PropertyValue(7, 250.0f, "250mm"), new PropertyValue(7, 353.0f, "353mm"));
    public static final PageSize LETTER = new PageSize(new PropertyValue(8, 8.5f, "8.5in"), new PropertyValue(8, 11.0f, "11in"));
    public static final PageSize LEGAL = new PageSize(new PropertyValue(8, 8.5f, "8.5in"), new PropertyValue(8, 14.0f, "14in"));
    public static final PageSize LEDGER = new PageSize(new PropertyValue(8, 11.0f, "11in"), new PropertyValue(8, 17.0f, "17in"));
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

    public static PageSize getPageSize(String pageSize) {
        return (PageSize)SIZE_MAP.get(pageSize);
    }

    static {
        SIZE_MAP.put("a3", A3);
        SIZE_MAP.put("a4", A4);
        SIZE_MAP.put("a5", A5);
        SIZE_MAP.put("b3", B3);
        SIZE_MAP.put("b4", B4);
        SIZE_MAP.put("letter", LETTER);
        SIZE_MAP.put("legal", LEGAL);
        SIZE_MAP.put("ledger", LEDGER);
    }
}

