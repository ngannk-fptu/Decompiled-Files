/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel.extensions;

import java.util.Objects;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;

public class XSSFCellBorder {
    private final IndexedColorMap _indexedColorMap;
    private ThemesTable _theme;
    private final CTBorder border;

    public XSSFCellBorder(CTBorder border, ThemesTable theme, IndexedColorMap colorMap) {
        this.border = border;
        this._indexedColorMap = colorMap;
        this._theme = theme;
    }

    public XSSFCellBorder(CTBorder border) {
        this(border, null, null);
    }

    public XSSFCellBorder(CTBorder border, IndexedColorMap colorMap) {
        this(border, null, colorMap);
    }

    public XSSFCellBorder() {
        this(CTBorder.Factory.newInstance(), null, null);
    }

    public void setThemesTable(ThemesTable themes) {
        this._theme = themes;
    }

    @Internal
    public CTBorder getCTBorder() {
        return this.border;
    }

    public BorderStyle getBorderStyle(BorderSide side) {
        CTBorderPr ctBorder = this.getBorder(side);
        STBorderStyle.Enum border = ctBorder == null ? STBorderStyle.NONE : ctBorder.getStyle();
        return BorderStyle.values()[border.intValue() - 1];
    }

    public void setBorderStyle(BorderSide side, BorderStyle style) {
        this.getBorder(side, true).setStyle(STBorderStyle.Enum.forInt(style.ordinal() + 1));
    }

    public XSSFColor getBorderColor(BorderSide side) {
        CTBorderPr borderPr = this.getBorder(side);
        if (borderPr != null && borderPr.isSetColor()) {
            XSSFColor clr = XSSFColor.from(borderPr.getColor(), this._indexedColorMap);
            if (this._theme != null) {
                this._theme.inheritFromThemeAsRequired(clr);
            }
            return clr;
        }
        return null;
    }

    public void setBorderColor(BorderSide side, XSSFColor color) {
        CTBorderPr borderPr = this.getBorder(side, true);
        if (color == null) {
            borderPr.unsetColor();
        } else {
            borderPr.setColor(color.getCTColor());
        }
    }

    private CTBorderPr getBorder(BorderSide side) {
        return this.getBorder(side, false);
    }

    private CTBorderPr getBorder(BorderSide side, boolean ensure) {
        CTBorderPr borderPr;
        switch (side) {
            case TOP: {
                borderPr = this.border.getTop();
                if (!ensure || borderPr != null) break;
                borderPr = this.border.addNewTop();
                break;
            }
            case RIGHT: {
                borderPr = this.border.getRight();
                if (!ensure || borderPr != null) break;
                borderPr = this.border.addNewRight();
                break;
            }
            case BOTTOM: {
                borderPr = this.border.getBottom();
                if (!ensure || borderPr != null) break;
                borderPr = this.border.addNewBottom();
                break;
            }
            case LEFT: {
                borderPr = this.border.getLeft();
                if (!ensure || borderPr != null) break;
                borderPr = this.border.addNewLeft();
                break;
            }
            case DIAGONAL: {
                borderPr = this.border.getDiagonal();
                if (!ensure || borderPr != null) break;
                borderPr = this.border.addNewDiagonal();
                break;
            }
            case VERTICAL: {
                borderPr = this.border.getVertical();
                if (!ensure || borderPr != null) break;
                borderPr = this.border.addNewVertical();
                break;
            }
            case HORIZONTAL: {
                borderPr = this.border.getHorizontal();
                if (!ensure || borderPr != null) break;
                borderPr = this.border.addNewHorizontal();
                break;
            }
            default: {
                throw new IllegalArgumentException("No suitable side specified for the border, had " + (Object)((Object)side));
            }
        }
        return borderPr;
    }

    public int hashCode() {
        return this.border.toString().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof XSSFCellBorder)) {
            return false;
        }
        XSSFCellBorder cf = (XSSFCellBorder)o;
        boolean equal = true;
        for (BorderSide side : BorderSide.values()) {
            if (Objects.equals(this.getBorderColor(side), cf.getBorderColor(side)) && Objects.equals((Object)this.getBorderStyle(side), (Object)cf.getBorderStyle(side))) continue;
            equal = false;
            break;
        }
        if (!equal) {
            return false;
        }
        if (this.border.isSetDiagonalUp() != cf.border.isSetDiagonalUp() || this.border.isSetDiagonalDown() != cf.border.isSetDiagonalDown() || this.border.isSetOutline() != cf.border.isSetOutline()) {
            return false;
        }
        if (this.border.isSetDiagonalUp() && this.border.getDiagonalUp() != cf.border.getDiagonalUp()) {
            return false;
        }
        if (this.border.isSetDiagonalDown() && this.border.getDiagonalDown() != cf.border.getDiagonalDown()) {
            return false;
        }
        return !this.border.isSetOutline() || this.border.getOutline() == cf.border.getOutline();
    }

    public static enum BorderSide {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT,
        DIAGONAL,
        VERTICAL,
        HORIZONTAL;

    }
}

