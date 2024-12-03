/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.state;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.PDFontSetting;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDSoftMask;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingIntent;

public class PDExtendedGraphicsState
implements COSObjectable {
    private final COSDictionary dict;

    public PDExtendedGraphicsState() {
        this.dict = new COSDictionary();
        this.dict.setItem(COSName.TYPE, (COSBase)COSName.EXT_G_STATE);
    }

    public PDExtendedGraphicsState(COSDictionary dictionary) {
        this.dict = dictionary;
    }

    public void copyIntoGraphicsState(PDGraphicsState gs) throws IOException {
        for (COSName key : this.dict.keySet()) {
            if (key.equals(COSName.LW)) {
                gs.setLineWidth(this.defaultIfNull(this.getLineWidth(), 1.0f));
                continue;
            }
            if (key.equals(COSName.LC)) {
                gs.setLineCap(this.getLineCapStyle());
                continue;
            }
            if (key.equals(COSName.LJ)) {
                gs.setLineJoin(this.getLineJoinStyle());
                continue;
            }
            if (key.equals(COSName.ML)) {
                gs.setMiterLimit(this.defaultIfNull(this.getMiterLimit(), 10.0f));
                continue;
            }
            if (key.equals(COSName.D)) {
                gs.setLineDashPattern(this.getLineDashPattern());
                continue;
            }
            if (key.equals(COSName.RI)) {
                gs.setRenderingIntent(this.getRenderingIntent());
                continue;
            }
            if (key.equals(COSName.OPM)) {
                gs.setOverprintMode(this.defaultIfNull(this.getOverprintMode(), 0.0f));
                continue;
            }
            if (key.equals(COSName.OP)) {
                gs.setOverprint(this.getStrokingOverprintControl());
                continue;
            }
            if (key.equals(COSName.OP_NS)) {
                gs.setNonStrokingOverprint(this.getNonStrokingOverprintControl());
                continue;
            }
            if (key.equals(COSName.FONT)) {
                PDFontSetting setting = this.getFontSetting();
                if (setting == null) continue;
                gs.getTextState().setFont(setting.getFont());
                gs.getTextState().setFontSize(setting.getFontSize());
                continue;
            }
            if (key.equals(COSName.FL)) {
                gs.setFlatness(this.defaultIfNull(this.getFlatnessTolerance(), 1.0f));
                continue;
            }
            if (key.equals(COSName.SM)) {
                gs.setSmoothness(this.defaultIfNull(this.getSmoothnessTolerance(), 0.0f));
                continue;
            }
            if (key.equals(COSName.SA)) {
                gs.setStrokeAdjustment(this.getAutomaticStrokeAdjustment());
                continue;
            }
            if (key.equals(COSName.CA)) {
                gs.setAlphaConstant(this.defaultIfNull(this.getStrokingAlphaConstant(), 1.0f));
                continue;
            }
            if (key.equals(COSName.CA_NS)) {
                gs.setNonStrokeAlphaConstant(this.defaultIfNull(this.getNonStrokingAlphaConstant(), 1.0f));
                continue;
            }
            if (key.equals(COSName.AIS)) {
                gs.setAlphaSource(this.getAlphaSourceFlag());
                continue;
            }
            if (key.equals(COSName.TK)) {
                gs.getTextState().setKnockoutFlag(this.getTextKnockoutFlag());
                continue;
            }
            if (key.equals(COSName.SMASK)) {
                PDSoftMask softmask = this.getSoftMask();
                if (softmask != null) {
                    softmask.setInitialTransformationMatrix(gs.getCurrentTransformationMatrix().clone());
                }
                gs.setSoftMask(softmask);
                continue;
            }
            if (key.equals(COSName.BM)) {
                gs.setBlendMode(this.getBlendMode());
                continue;
            }
            if (key.equals(COSName.TR)) {
                if (this.dict.containsKey(COSName.TR2)) continue;
                gs.setTransfer(this.getTransfer());
                continue;
            }
            if (!key.equals(COSName.TR2)) continue;
            gs.setTransfer(this.getTransfer2());
        }
    }

    private float defaultIfNull(Float standardValue, float defaultValue) {
        return standardValue != null ? standardValue.floatValue() : defaultValue;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    public Float getLineWidth() {
        return this.getFloatItem(COSName.LW);
    }

    public void setLineWidth(Float width) {
        this.setFloatItem(COSName.LW, width);
    }

    public int getLineCapStyle() {
        return this.dict.getInt(COSName.LC);
    }

    public void setLineCapStyle(int style) {
        this.dict.setInt(COSName.LC, style);
    }

    public int getLineJoinStyle() {
        return this.dict.getInt(COSName.LJ);
    }

    public void setLineJoinStyle(int style) {
        this.dict.setInt(COSName.LJ, style);
    }

    public Float getMiterLimit() {
        return this.getFloatItem(COSName.ML);
    }

    public void setMiterLimit(Float miterLimit) {
        this.setFloatItem(COSName.ML, miterLimit);
    }

    public PDLineDashPattern getLineDashPattern() {
        PDLineDashPattern retval = null;
        COSBase dp = this.dict.getDictionaryObject(COSName.D);
        if (dp instanceof COSArray && ((COSArray)dp).size() == 2) {
            COSBase dashArray = ((COSArray)dp).getObject(0);
            COSBase phase = ((COSArray)dp).getObject(1);
            if (dashArray instanceof COSArray && phase instanceof COSNumber) {
                retval = new PDLineDashPattern((COSArray)dashArray, ((COSNumber)phase).intValue());
            }
        }
        return retval;
    }

    public void setLineDashPattern(PDLineDashPattern dashPattern) {
        this.dict.setItem(COSName.D, dashPattern.getCOSObject());
    }

    public RenderingIntent getRenderingIntent() {
        String ri = this.dict.getNameAsString("RI");
        if (ri != null) {
            return RenderingIntent.fromString(ri);
        }
        return null;
    }

    public void setRenderingIntent(String ri) {
        this.dict.setName("RI", ri);
    }

    public boolean getStrokingOverprintControl() {
        return this.dict.getBoolean(COSName.OP, false);
    }

    public void setStrokingOverprintControl(boolean op) {
        this.dict.setBoolean(COSName.OP, op);
    }

    public boolean getNonStrokingOverprintControl() {
        return this.dict.getBoolean(COSName.OP_NS, this.getStrokingOverprintControl());
    }

    public void setNonStrokingOverprintControl(boolean op) {
        this.dict.setBoolean(COSName.OP_NS, op);
    }

    public Float getOverprintMode() {
        return this.getFloatItem(COSName.OPM);
    }

    public void setOverprintMode(Float overprintMode) {
        if (overprintMode == null) {
            this.dict.removeItem(COSName.OPM);
        } else {
            this.dict.setInt(COSName.OPM, overprintMode.intValue());
        }
    }

    public PDFontSetting getFontSetting() {
        PDFontSetting setting = null;
        COSBase base = this.dict.getDictionaryObject(COSName.FONT);
        if (base instanceof COSArray) {
            COSArray font = (COSArray)base;
            setting = new PDFontSetting(font);
        }
        return setting;
    }

    public void setFontSetting(PDFontSetting fs) {
        this.dict.setItem(COSName.FONT, (COSObjectable)fs);
    }

    public Float getFlatnessTolerance() {
        return this.getFloatItem(COSName.FL);
    }

    public void setFlatnessTolerance(Float flatness) {
        this.setFloatItem(COSName.FL, flatness);
    }

    public Float getSmoothnessTolerance() {
        return this.getFloatItem(COSName.SM);
    }

    public void setSmoothnessTolerance(Float smoothness) {
        this.setFloatItem(COSName.SM, smoothness);
    }

    public boolean getAutomaticStrokeAdjustment() {
        return this.dict.getBoolean(COSName.SA, false);
    }

    public void setAutomaticStrokeAdjustment(boolean sa) {
        this.dict.setBoolean(COSName.SA, sa);
    }

    public Float getStrokingAlphaConstant() {
        return this.getFloatItem(COSName.CA);
    }

    public void setStrokingAlphaConstant(Float alpha) {
        this.setFloatItem(COSName.CA, alpha);
    }

    public Float getNonStrokingAlphaConstant() {
        return this.getFloatItem(COSName.CA_NS);
    }

    public void setNonStrokingAlphaConstant(Float alpha) {
        this.setFloatItem(COSName.CA_NS, alpha);
    }

    public boolean getAlphaSourceFlag() {
        return this.dict.getBoolean(COSName.AIS, false);
    }

    public void setAlphaSourceFlag(boolean alpha) {
        this.dict.setBoolean(COSName.AIS, alpha);
    }

    public BlendMode getBlendMode() {
        return BlendMode.getInstance(this.dict.getDictionaryObject(COSName.BM));
    }

    public void setBlendMode(BlendMode bm) {
        this.dict.setItem(COSName.BM, (COSBase)BlendMode.getCOSName(bm));
    }

    public PDSoftMask getSoftMask() {
        if (!this.dict.containsKey(COSName.SMASK)) {
            return null;
        }
        return PDSoftMask.create(this.dict.getDictionaryObject(COSName.SMASK));
    }

    public boolean getTextKnockoutFlag() {
        return this.dict.getBoolean(COSName.TK, true);
    }

    public void setTextKnockoutFlag(boolean tk) {
        this.dict.setBoolean(COSName.TK, tk);
    }

    private Float getFloatItem(COSName key) {
        Float retval = null;
        COSBase base = this.dict.getDictionaryObject(key);
        if (base instanceof COSNumber) {
            COSNumber value = (COSNumber)base;
            retval = Float.valueOf(value.floatValue());
        }
        return retval;
    }

    private void setFloatItem(COSName key, Float value) {
        if (value == null) {
            this.dict.removeItem(key);
        } else {
            this.dict.setItem(key, (COSBase)new COSFloat(value.floatValue()));
        }
    }

    public COSBase getTransfer() {
        COSBase base = this.dict.getDictionaryObject(COSName.TR);
        if (base instanceof COSArray && ((COSArray)base).size() != 4) {
            return null;
        }
        return base;
    }

    public void setTransfer(COSBase transfer) {
        this.dict.setItem(COSName.TR, transfer);
    }

    public COSBase getTransfer2() {
        COSBase base = this.dict.getDictionaryObject(COSName.TR2);
        if (base instanceof COSArray && ((COSArray)base).size() != 4) {
            return null;
        }
        return base;
    }

    public void setTransfer2(COSBase transfer2) {
        this.dict.setItem(COSName.TR2, transfer2);
    }
}

