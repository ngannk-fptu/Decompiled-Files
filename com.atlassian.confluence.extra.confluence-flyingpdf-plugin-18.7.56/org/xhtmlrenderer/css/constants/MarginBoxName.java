/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.constants;

import java.util.HashMap;
import java.util.Map;
import org.xhtmlrenderer.css.constants.IdentValue;

public class MarginBoxName {
    private static final Map ALL = new HashMap();
    private static int _maxAssigned = 0;
    public final int FS_ID;
    private final String _ident;
    private final IdentValue _textAlign;
    private final IdentValue _verticalAlign;
    public static final MarginBoxName TOP_LEFT_CORNER = MarginBoxName.addValue("top-left-corner", IdentValue.RIGHT, IdentValue.MIDDLE);
    public static final MarginBoxName TOP_LEFT = MarginBoxName.addValue("top-left", IdentValue.LEFT, IdentValue.MIDDLE);
    public static final MarginBoxName TOP_CENTER = MarginBoxName.addValue("top-center", IdentValue.CENTER, IdentValue.MIDDLE);
    public static final MarginBoxName TOP_RIGHT = MarginBoxName.addValue("top-right", IdentValue.RIGHT, IdentValue.MIDDLE);
    public static final MarginBoxName TOP_RIGHT_CORNER = MarginBoxName.addValue("top-right-corner", IdentValue.LEFT, IdentValue.MIDDLE);
    public static final MarginBoxName BOTTOM_LEFT_CORNER = MarginBoxName.addValue("bottom-left-corner", IdentValue.RIGHT, IdentValue.MIDDLE);
    public static final MarginBoxName BOTTOM_LEFT = MarginBoxName.addValue("bottom-left", IdentValue.LEFT, IdentValue.MIDDLE);
    public static final MarginBoxName BOTTOM_CENTER = MarginBoxName.addValue("bottom-center", IdentValue.CENTER, IdentValue.MIDDLE);
    public static final MarginBoxName BOTTOM_RIGHT = MarginBoxName.addValue("bottom-right", IdentValue.RIGHT, IdentValue.MIDDLE);
    public static final MarginBoxName BOTTOM_RIGHT_CORNER = MarginBoxName.addValue("bottom-right-corner", IdentValue.LEFT, IdentValue.MIDDLE);
    public static final MarginBoxName LEFT_TOP = MarginBoxName.addValue("left-top", IdentValue.CENTER, IdentValue.TOP);
    public static final MarginBoxName LEFT_MIDDLE = MarginBoxName.addValue("left-middle", IdentValue.CENTER, IdentValue.MIDDLE);
    public static final MarginBoxName LEFT_BOTTOM = MarginBoxName.addValue("left-bottom", IdentValue.CENTER, IdentValue.BOTTOM);
    public static final MarginBoxName RIGHT_TOP = MarginBoxName.addValue("right-top", IdentValue.CENTER, IdentValue.TOP);
    public static final MarginBoxName RIGHT_MIDDLE = MarginBoxName.addValue("right-middle", IdentValue.CENTER, IdentValue.MIDDLE);
    public static final MarginBoxName RIGHT_BOTTOM = MarginBoxName.addValue("right-bottom", IdentValue.CENTER, IdentValue.BOTTOM);
    public static final MarginBoxName FS_PDF_XMP_METADATA = MarginBoxName.addValue("-fs-pdf-xmp-metadata", IdentValue.TOP, IdentValue.LEFT);

    private MarginBoxName(String ident, IdentValue textAlign, IdentValue verticalAlign) {
        this._ident = ident;
        this._textAlign = textAlign;
        this._verticalAlign = verticalAlign;
        this.FS_ID = _maxAssigned++;
    }

    private static final MarginBoxName addValue(String ident, IdentValue textAlign, IdentValue verticalAlign) {
        MarginBoxName val = new MarginBoxName(ident, textAlign, verticalAlign);
        ALL.put(ident, val);
        return val;
    }

    public String toString() {
        return this._ident;
    }

    public static MarginBoxName valueOf(String ident) {
        return (MarginBoxName)ALL.get(ident);
    }

    public int hashCode() {
        return this.FS_ID;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof MarginBoxName)) {
            return false;
        }
        return this.FS_ID == ((MarginBoxName)o).FS_ID;
    }

    public IdentValue getInitialTextAlign() {
        return this._textAlign;
    }

    public IdentValue getInitialVerticalAlign() {
        return this._verticalAlign;
    }
}

