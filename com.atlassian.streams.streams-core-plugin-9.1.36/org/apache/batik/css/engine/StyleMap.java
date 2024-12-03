/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.Value;

public class StyleMap {
    public static final short IMPORTANT_MASK = 1;
    public static final short COMPUTED_MASK = 2;
    public static final short NULL_CASCADED_MASK = 4;
    public static final short INHERITED_MASK = 8;
    public static final short LINE_HEIGHT_RELATIVE_MASK = 16;
    public static final short FONT_SIZE_RELATIVE_MASK = 32;
    public static final short COLOR_RELATIVE_MASK = 64;
    public static final short PARENT_RELATIVE_MASK = 128;
    public static final short BLOCK_WIDTH_RELATIVE_MASK = 256;
    public static final short BLOCK_HEIGHT_RELATIVE_MASK = 512;
    public static final short BOX_RELATIVE_MASK = 1024;
    public static final short ORIGIN_MASK = -8192;
    public static final short USER_AGENT_ORIGIN = 0;
    public static final short USER_ORIGIN = 8192;
    public static final short NON_CSS_ORIGIN = 16384;
    public static final short AUTHOR_ORIGIN = 24576;
    public static final short INLINE_AUTHOR_ORIGIN = Short.MIN_VALUE;
    public static final short OVERRIDE_ORIGIN = -24576;
    protected Value[] values;
    protected short[] masks;
    protected boolean fixedCascadedValues;

    public StyleMap(int size) {
        this.values = new Value[size];
        this.masks = new short[size];
    }

    public boolean hasFixedCascadedValues() {
        return this.fixedCascadedValues;
    }

    public void setFixedCascadedStyle(boolean b) {
        this.fixedCascadedValues = b;
    }

    public Value getValue(int i) {
        return this.values[i];
    }

    public short getMask(int i) {
        return this.masks[i];
    }

    public boolean isImportant(int i) {
        return (this.masks[i] & 1) != 0;
    }

    public boolean isComputed(int i) {
        return (this.masks[i] & 2) != 0;
    }

    public boolean isNullCascaded(int i) {
        return (this.masks[i] & 4) != 0;
    }

    public boolean isInherited(int i) {
        return (this.masks[i] & 8) != 0;
    }

    public short getOrigin(int i) {
        return (short)(this.masks[i] & 0xFFFFE000);
    }

    public boolean isColorRelative(int i) {
        return (this.masks[i] & 0x40) != 0;
    }

    public boolean isParentRelative(int i) {
        return (this.masks[i] & 0x80) != 0;
    }

    public boolean isLineHeightRelative(int i) {
        return (this.masks[i] & 0x10) != 0;
    }

    public boolean isFontSizeRelative(int i) {
        return (this.masks[i] & 0x20) != 0;
    }

    public boolean isBlockWidthRelative(int i) {
        return (this.masks[i] & 0x100) != 0;
    }

    public boolean isBlockHeightRelative(int i) {
        return (this.masks[i] & 0x200) != 0;
    }

    public void putValue(int i, Value v) {
        this.values[i] = v;
    }

    public void putMask(int i, short m) {
        this.masks[i] = m;
    }

    public void putImportant(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 1);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFFFE);
        }
    }

    public void putOrigin(int i, short val) {
        int n = i;
        this.masks[n] = (short)(this.masks[n] & 0x1FFF);
        int n2 = i;
        this.masks[n2] = (short)(this.masks[n2] | (short)(val & 0xFFFFE000));
    }

    public void putComputed(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 2);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFFFD);
        }
    }

    public void putNullCascaded(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 4);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFFFB);
        }
    }

    public void putInherited(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 8);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFFF7);
        }
    }

    public void putColorRelative(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 0x40);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFFBF);
        }
    }

    public void putParentRelative(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 0x80);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFF7F);
        }
    }

    public void putLineHeightRelative(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 0x10);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFFEF);
        }
    }

    public void putFontSizeRelative(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 0x20);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFFDF);
        }
    }

    public void putBlockWidthRelative(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 0x100);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFEFF);
        }
    }

    public void putBlockHeightRelative(int i, boolean b) {
        if (b) {
            int n = i;
            this.masks[n] = (short)(this.masks[n] | 0x200);
        } else {
            int n = i;
            this.masks[n] = (short)(this.masks[n] & 0xFFFFFDFF);
        }
    }

    public String toString(CSSEngine eng) {
        int nSlots = this.values.length;
        StringBuffer sb = new StringBuffer(nSlots * 8);
        for (int i = 0; i < nSlots; ++i) {
            Value v = this.values[i];
            if (v == null) continue;
            sb.append(eng.getPropertyName(i));
            sb.append(": ");
            sb.append(v);
            if (this.isImportant(i)) {
                sb.append(" !important");
            }
            sb.append(";\n");
        }
        return sb.toString();
    }
}

