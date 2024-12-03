/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.wp.usermodel;

public interface CharacterRun {
    public boolean isBold();

    public void setBold(boolean var1);

    public boolean isItalic();

    public void setItalic(boolean var1);

    public boolean isSmallCaps();

    public void setSmallCaps(boolean var1);

    public boolean isCapitalized();

    public void setCapitalized(boolean var1);

    public boolean isStrikeThrough();

    public void setStrikeThrough(boolean var1);

    public boolean isDoubleStrikeThrough();

    public void setDoubleStrikethrough(boolean var1);

    public boolean isShadowed();

    public void setShadow(boolean var1);

    public boolean isEmbossed();

    public void setEmbossed(boolean var1);

    public boolean isImprinted();

    public void setImprinted(boolean var1);

    public int getFontSize();

    public Double getFontSizeAsDouble();

    public void setFontSize(int var1);

    public void setFontSize(double var1);

    public int getCharacterSpacing();

    public void setCharacterSpacing(int var1);

    public int getKerning();

    public void setKerning(int var1);

    public boolean isHighlighted();

    public String getFontName();

    public String text();
}

