/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.Color;

public interface FontFormatting {
    public short getEscapementType();

    public void setEscapementType(short var1);

    public short getFontColorIndex();

    public void setFontColorIndex(short var1);

    public Color getFontColor();

    public void setFontColor(Color var1);

    public int getFontHeight();

    public void setFontHeight(int var1);

    public short getUnderlineType();

    public void setUnderlineType(short var1);

    public boolean isBold();

    public boolean isItalic();

    public boolean isStruckout();

    public void setFontStyle(boolean var1, boolean var2);

    public void resetFontStyle();
}

