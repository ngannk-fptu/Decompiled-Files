/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.DataBarFormatting;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFConditionalFormattingThreshold;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBar;

public class XSSFDataBarFormatting
implements DataBarFormatting {
    IndexedColorMap _colorMap;
    CTDataBar _databar;

    XSSFDataBarFormatting(CTDataBar databar, IndexedColorMap colorMap) {
        this._databar = databar;
        this._colorMap = colorMap;
    }

    @Override
    public boolean isIconOnly() {
        if (this._databar.isSetShowValue()) {
            return !this._databar.getShowValue();
        }
        return false;
    }

    @Override
    public void setIconOnly(boolean only) {
        this._databar.setShowValue(!only);
    }

    @Override
    public boolean isLeftToRight() {
        return true;
    }

    @Override
    public void setLeftToRight(boolean ltr) {
    }

    @Override
    public int getWidthMin() {
        return (int)this._databar.getMinLength();
    }

    @Override
    public void setWidthMin(int width) {
        this._databar.setMinLength(width);
    }

    @Override
    public int getWidthMax() {
        return (int)this._databar.getMaxLength();
    }

    @Override
    public void setWidthMax(int width) {
        this._databar.setMaxLength(width);
    }

    @Override
    public XSSFColor getColor() {
        return XSSFColor.from(this._databar.getColor(), this._colorMap);
    }

    @Override
    public void setColor(Color color) {
        this._databar.setColor(((XSSFColor)color).getCTColor());
    }

    @Override
    public XSSFConditionalFormattingThreshold getMinThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.getCfvoArray(0));
    }

    @Override
    public XSSFConditionalFormattingThreshold getMaxThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.getCfvoArray(1));
    }

    public XSSFConditionalFormattingThreshold createThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.addNewCfvo());
    }
}

