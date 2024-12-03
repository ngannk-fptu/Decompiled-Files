/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSStyleSheet;

public interface CSSRule {
    public static final short UNKNOWN_RULE = 0;
    public static final short STYLE_RULE = 1;
    public static final short CHARSET_RULE = 2;
    public static final short IMPORT_RULE = 3;
    public static final short MEDIA_RULE = 4;
    public static final short FONT_FACE_RULE = 5;
    public static final short PAGE_RULE = 6;

    public short getType();

    public String getCssText();

    public void setCssText(String var1) throws DOMException;

    public CSSStyleSheet getParentStyleSheet();

    public CSSRule getParentRule();
}

