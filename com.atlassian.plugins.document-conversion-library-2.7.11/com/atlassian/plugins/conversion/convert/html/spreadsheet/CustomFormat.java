/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.cells.Border
 *  com.aspose.cells.BorderCollection
 *  com.aspose.cells.Font
 *  com.aspose.cells.Style
 */
package com.atlassian.plugins.conversion.convert.html.spreadsheet;

import com.aspose.cells.Border;
import com.aspose.cells.BorderCollection;
import com.aspose.cells.Font;
import com.aspose.cells.Style;

public class CustomFormat {
    private final Style format;
    private String name;
    private boolean isEditFormat;
    private boolean isErrFormat;
    private boolean showGrid;

    public CustomFormat(Style format, boolean showGrid) {
        this.format = format;
        this.showGrid = showGrid;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CustomFormat)) {
            return false;
        }
        CustomFormat format1 = (CustomFormat)other;
        if (this.isInputFormat() && format1.isInputFormat()) {
            return true;
        }
        if (this.isInputFormat() && !format1.isInputFormat()) {
            return false;
        }
        if (!this.isInputFormat() && format1.isInputFormat()) {
            return false;
        }
        if (this.isErrFormat() && format1.isErrFormat()) {
            return true;
        }
        if (this.isErrFormat() && !format1.isErrFormat()) {
            return false;
        }
        if (!this.isErrFormat() && format1.isErrFormat()) {
            return false;
        }
        return this.equalsCellFormat(format1.format);
    }

    public boolean equalsCellFormat(Style format1) {
        Font font = this.format.getFont();
        Font font1 = format1.getFont();
        BorderCollection borders = this.format.getBorders();
        BorderCollection borders1 = format1.getBorders();
        return font.getName().equals(font1.getName()) && font.getColor().toArgb() == font1.getColor().toArgb() && font.getSize() == font1.getSize() && this.format.getPattern() == format1.getPattern() && this.format.getForegroundColor().toArgb() == format1.getForegroundColor().toArgb() && this.compareBorders(borders.getByBorderType(1), borders1.getByBorderType(1)) && this.compareBorders(borders.getByBorderType(4), borders1.getByBorderType(4)) && this.compareBorders(borders.getByBorderType(8), borders1.getByBorderType(8)) && this.compareBorders(borders.getByBorderType(2), borders1.getByBorderType(2)) && font.isBold() == font1.isBold() && font.isItalic() == font1.isItalic() && this.format.getHorizontalAlignment() == format1.getHorizontalAlignment() && this.format.getVerticalAlignment() == format1.getVerticalAlignment();
    }

    private boolean compareBorders(Border border1, Border border2) {
        return border1.getColor().toArgb() == border2.getColor().toArgb() && border1.getLineStyle() == border2.getLineStyle();
    }

    public int hashCode() {
        int hashCode = 37;
        if (this.isInputFormat()) {
            return hashCode;
        }
        if (this.isErrFormat()) {
            return 29;
        }
        BorderCollection borders = this.format.getBorders();
        Border leftBorder = borders.getByBorderType(1);
        Border rightBorder = borders.getByBorderType(2);
        Border topBorder = borders.getByBorderType(4);
        Border bottomBorder = borders.getByBorderType(8);
        hashCode = hashCode * 31 + this.format.getFont().getName().hashCode();
        hashCode = hashCode * 31 + this.format.getFont().getColor().toArgb();
        hashCode = hashCode * 31 + this.format.getFont().getSize();
        hashCode = hashCode * 31 + this.format.getForegroundColor().toArgb();
        hashCode = hashCode * 31 + leftBorder.getColor().toArgb();
        hashCode = hashCode * 31 + leftBorder.getLineStyle();
        hashCode = hashCode * 31 + rightBorder.getColor().toArgb();
        hashCode = hashCode * 31 + rightBorder.getLineStyle();
        hashCode = hashCode * 31 + topBorder.getColor().toArgb();
        hashCode = hashCode * 31 + topBorder.getLineStyle();
        hashCode = hashCode * 31 + bottomBorder.getColor().toArgb();
        hashCode = hashCode * 31 + bottomBorder.getLineStyle();
        hashCode = hashCode * 31 + (this.format.getFont().isBold() ? 1 : 0);
        hashCode = hashCode * 31 + (this.format.getFont().isItalic() ? 1 : 0);
        hashCode = hashCode * 31 + this.format.getHorizontalAlignment();
        hashCode = hashCode * 31 + this.format.getVerticalAlignment();
        return hashCode;
    }

    public String toString() {
        if (this.isInputFormat()) {
            return "*." + this.name + "{ background : #CCFFFF; color : windowtext; font-family : \ufffd\ufffd\ufffd\ufffd; \r\nfont-size : 10.50pt; font-style : normal; font-weight : 400; \r\ntext-align : left; vertical-align : middle }\r\n";
        }
        if (this.isErrFormat()) {
            return "*." + this.name + "{ background : #00FF00; color : #FF0000; font-family : \ufffd\ufffd\ufffd\ufffd; \r\nfont-size : 10.50pt; font-style : normal; font-weight : 400; \r\ntext-align : left; vertical-align : middle }\r\n";
        }
        String fontstyle = "normal";
        String fontweight = "normal";
        if (this.format.getFont().isItalic()) {
            fontstyle = "italic";
        }
        if (this.format.getFont().isBold()) {
            fontweight = "bold";
        }
        int patterFGColor = this.format.getPattern() == 0 ? 0xFFFFFF : this.format.getForegroundColor().toArgb();
        return String.format("*.%s { %s color : #%s; background : #%s; font-family : %s; font-size : %.1fpt; \r\nfont-style : %s; font-weight : %s; padding-left : 1px;\r\npadding-right : 1px; padding-top : 1px; %s}\r\n", this.name, this.border(), this.writeColor(this.format.getFont().getColor().toArgb()), this.writeColor(patterFGColor), this.format.getFont().getName(), this.format.getFont().getDoubleSize(), fontstyle, fontweight, this.align());
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public String getStyleProps() {
        if (this.isInputFormat()) {
            return "background : #CCFFFF; color : windowtext; font-family : \ufffd\ufffd\ufffd\ufffd; \r\nfont-size : 10.50pt; font-style : normal; font-weight : 400; \r\ntext-align : left; vertical-align : middle";
        }
        if (this.isErrFormat()) {
            return "background : #00FF00; color : #FF0000; font-family : \ufffd\ufffd\ufffd\ufffd; \r\nfont-size : 10.50pt; font-style : normal; font-weight : 400; \r\ntext-align : left; vertical-align : middle";
        }
        String fontstyle = "normal";
        String fontweight = "normal";
        Font font = this.format.getFont();
        if (font.isItalic()) {
            fontstyle = "italic";
        }
        if (font.isBold()) {
            fontweight = "bold";
        }
        int patterFGColor = this.format.getPattern() == 0 ? 0xFFFFFF : this.format.getForegroundColor().toArgb();
        return String.format("%s color : #%s; background : #%s; font-family : %s; font-size : %.1fpt; \r\nfont-style : %s; font-weight : %s; padding-left : 1px;\r\npadding-right : 1px; padding-top : 1px; %s", this.border(), this.writeColor(font.getColor().toArgb()), this.writeColor(patterFGColor), font.getName(), font.getDoubleSize(), fontstyle, fontweight, this.align());
    }

    private String align() {
        int hAlign = this.format.getHorizontalAlignment();
        int vAlign = this.format.getVerticalAlignment();
        String align = "";
        switch (hAlign) {
            case 1: {
                align = align + "text-align : left; ";
                break;
            }
            case 2: {
                align = align + "text-align : center; ";
                break;
            }
            case 3: {
                align = align + "text-align : right; ";
                break;
            }
            default: {
                align = align + "text-align : justify; ";
            }
        }
        switch (vAlign) {
            case 0: {
                align = align + "vertical-align : top ";
                break;
            }
            case 1: {
                align = align + "vertical-align : middle ";
                break;
            }
            case 2: {
                align = align + "vertical-align : bottom ";
                break;
            }
            default: {
                align = align + "vertical-align : baseline ";
            }
        }
        return align;
    }

    private String border() {
        BorderCollection borders = this.format.getBorders();
        Border leftborder = borders.getByBorderType(1);
        int leftborderStyle = leftborder.getLineStyle();
        int leftborderColor = leftborder.getColor().toArgb();
        Border topborder = borders.getByBorderType(4);
        int topborderStyle = topborder.getLineStyle();
        int topborderColor = topborder.getColor().toArgb();
        Border rightborder = borders.getByBorderType(2);
        int rightborderStyle = rightborder.getLineStyle();
        int rightborderColor = rightborder.getColor().toArgb();
        Border bottomborder = borders.getByBorderType(8);
        int bottomborderStyle = bottomborder.getLineStyle();
        int bottomborderColor = bottomborder.getColor().toArgb();
        String border = "";
        if (leftborderStyle == 0 && bottomborderStyle == 0 && topborderStyle == 0 && rightborderStyle == 0 && this.showGrid) {
            return "border: 1pt solid #c0c0c0;";
        }
        if (leftborder == topborder && bottomborder == rightborder && leftborder == bottomborder && leftborderColor == topborderColor && bottomborderColor == rightborderColor && leftborderColor == bottomborderColor) {
            return "border : " + this.getLineWidth(topborderStyle, topborderColor) + ";";
        }
        if (bottomborderStyle != 0) {
            border = border + " border-bottom : " + this.getLineWidth(bottomborderStyle, bottomborderColor) + ";";
        } else if (this.showGrid) {
            border = border + " border-bottom: 1pt solid #c0c0c0;";
        }
        if (rightborderStyle != 0) {
            border = border + " border-right : " + this.getLineWidth(rightborderStyle, rightborderColor) + ";";
        } else if (this.showGrid) {
            border = border + " border-right: 1pt solid #c0c0c0;";
        }
        if (topborderStyle != 0) {
            border = border + " border-top : " + this.getLineWidth(topborderStyle, topborderColor) + ";";
        } else if (this.showGrid) {
            border = border + " border-top: 1pt solid #c0c0c0;";
        }
        if (leftborderStyle != 0) {
            border = border + " border-left : " + this.getLineWidth(leftborderStyle, leftborderColor) + ";";
        } else if (this.showGrid) {
            border = border + " border-left: 1pt solid #c0c0c0;";
        }
        return border;
    }

    private String getLineWidth(int border, int rgb) {
        if (border == 1) {
            return "1pt solid #" + this.writeColor(rgb);
        }
        if (border == 2) {
            return "2pt solid #" + this.writeColor(rgb);
        }
        if (border == 3) {
            return "1pt dashed #" + this.writeColor(rgb);
        }
        if (border == 4 || border == 7) {
            return "1pt dotted #" + this.writeColor(rgb);
        }
        if (border == 5) {
            return "2.0pt solid #" + this.writeColor(rgb);
        }
        if (border == 6) {
            return "2.5pt double #" + this.writeColor(rgb);
        }
        if (border == 8) {
            return "1.5pt dashed #" + this.writeColor(rgb);
        }
        if (border == 10) {
            return "1.5pt dot-dash #" + this.writeColor(rgb);
        }
        if (border == 12) {
            return "1.5pt dot-dot-dash #" + this.writeColor(rgb);
        }
        if (border == 13) {
            return "1.5pt dot-dash-slanted #" + this.writeColor(rgb);
        }
        return String.valueOf(border);
    }

    private String writeColor(int rgb) {
        if (rgb == 0) {
            return "000000";
        }
        if (rgb == 0xFFFFFF) {
            return "FFFFFF";
        }
        String colorstr = "";
        colorstr = colorstr + this.writeHex(rgb >> 16 & 0xFF);
        colorstr = colorstr + this.writeHex(rgb >> 8 & 0xFF);
        colorstr = colorstr + this.writeHex(rgb & 0xFF);
        return colorstr;
    }

    private String writeHex(int i) {
        StringBuilder hexStr = new StringBuilder();
        hexStr.append(this.hexChar(i));
        while ((i >>>= 4) != 0) {
            hexStr.insert(0, this.hexChar(i));
        }
        if (hexStr.length() == 1) {
            hexStr.insert(0, "0");
        }
        return hexStr.toString();
    }

    private char hexChar(int k) {
        if ((k %= 16) < 0) {
            k = -k;
        }
        if (k < 10) {
            return (char)(48 + k);
        }
        return (char)(65 + (k - 10));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isInputFormat() {
        return this.isEditFormat;
    }

    public void setInputFormat() {
        this.isEditFormat = true;
    }

    public boolean isErrFormat() {
        return this.isErrFormat;
    }

    public void setErrFormat(boolean errFormat) {
        this.isErrFormat = errFormat;
    }
}

