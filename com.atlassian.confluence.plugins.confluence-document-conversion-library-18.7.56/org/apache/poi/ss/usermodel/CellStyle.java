/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.util.Removal;

public interface CellStyle {
    public short getIndex();

    public void setDataFormat(short var1);

    public short getDataFormat();

    public String getDataFormatString();

    public void setFont(Font var1);

    public int getFontIndex();

    @Deprecated
    @Removal(version="6.0.0")
    public int getFontIndexAsInt();

    public void setHidden(boolean var1);

    public boolean getHidden();

    public void setLocked(boolean var1);

    public boolean getLocked();

    public void setQuotePrefixed(boolean var1);

    public boolean getQuotePrefixed();

    public void setAlignment(HorizontalAlignment var1);

    public HorizontalAlignment getAlignment();

    public void setWrapText(boolean var1);

    public boolean getWrapText();

    public void setVerticalAlignment(VerticalAlignment var1);

    public VerticalAlignment getVerticalAlignment();

    public void setRotation(short var1);

    public short getRotation();

    public void setIndention(short var1);

    public short getIndention();

    public void setBorderLeft(BorderStyle var1);

    public BorderStyle getBorderLeft();

    public void setBorderRight(BorderStyle var1);

    public BorderStyle getBorderRight();

    public void setBorderTop(BorderStyle var1);

    public BorderStyle getBorderTop();

    public void setBorderBottom(BorderStyle var1);

    public BorderStyle getBorderBottom();

    public void setLeftBorderColor(short var1);

    public short getLeftBorderColor();

    public void setRightBorderColor(short var1);

    public short getRightBorderColor();

    public void setTopBorderColor(short var1);

    public short getTopBorderColor();

    public void setBottomBorderColor(short var1);

    public short getBottomBorderColor();

    public void setFillPattern(FillPatternType var1);

    public FillPatternType getFillPattern();

    public void setFillBackgroundColor(short var1);

    public void setFillBackgroundColor(Color var1);

    public short getFillBackgroundColor();

    public Color getFillBackgroundColorColor();

    public void setFillForegroundColor(short var1);

    public void setFillForegroundColor(Color var1);

    public short getFillForegroundColor();

    public Color getFillForegroundColorColor();

    public void cloneStyleFrom(CellStyle var1);

    public void setShrinkToFit(boolean var1);

    public boolean getShrinkToFit();
}

