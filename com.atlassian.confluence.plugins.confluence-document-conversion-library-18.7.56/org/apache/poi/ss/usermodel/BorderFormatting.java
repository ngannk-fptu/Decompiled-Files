/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Color;

public interface BorderFormatting {
    public BorderStyle getBorderBottom();

    public BorderStyle getBorderDiagonal();

    public BorderStyle getBorderLeft();

    public BorderStyle getBorderRight();

    public BorderStyle getBorderTop();

    public BorderStyle getBorderVertical();

    public BorderStyle getBorderHorizontal();

    public short getBottomBorderColor();

    public Color getBottomBorderColorColor();

    public short getDiagonalBorderColor();

    public Color getDiagonalBorderColorColor();

    public short getLeftBorderColor();

    public Color getLeftBorderColorColor();

    public short getRightBorderColor();

    public Color getRightBorderColorColor();

    public short getTopBorderColor();

    public Color getTopBorderColorColor();

    public short getVerticalBorderColor();

    public Color getVerticalBorderColorColor();

    public short getHorizontalBorderColor();

    public Color getHorizontalBorderColorColor();

    public void setBorderBottom(BorderStyle var1);

    public void setBorderDiagonal(BorderStyle var1);

    public void setBorderLeft(BorderStyle var1);

    public void setBorderRight(BorderStyle var1);

    public void setBorderTop(BorderStyle var1);

    public void setBorderHorizontal(BorderStyle var1);

    public void setBorderVertical(BorderStyle var1);

    public void setBottomBorderColor(short var1);

    public void setBottomBorderColor(Color var1);

    public void setDiagonalBorderColor(short var1);

    public void setDiagonalBorderColor(Color var1);

    public void setLeftBorderColor(short var1);

    public void setLeftBorderColor(Color var1);

    public void setRightBorderColor(short var1);

    public void setRightBorderColor(Color var1);

    public void setTopBorderColor(short var1);

    public void setTopBorderColor(Color var1);

    public void setHorizontalBorderColor(short var1);

    public void setHorizontalBorderColor(Color var1);

    public void setVerticalBorderColor(short var1);

    public void setVerticalBorderColor(Color var1);
}

