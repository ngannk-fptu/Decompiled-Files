/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.prepress;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

public class PDBoxStyle
implements COSObjectable {
    public static final String GUIDELINE_STYLE_SOLID = "S";
    public static final String GUIDELINE_STYLE_DASHED = "D";
    private final COSDictionary dictionary;

    public PDBoxStyle() {
        this.dictionary = new COSDictionary();
    }

    public PDBoxStyle(COSDictionary dic) {
        this.dictionary = dic;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public PDColor getGuidelineColor() {
        COSArray colorValues = (COSArray)this.dictionary.getDictionaryObject(COSName.C);
        if (colorValues == null) {
            colorValues = new COSArray();
            colorValues.add(COSInteger.ZERO);
            colorValues.add(COSInteger.ZERO);
            colorValues.add(COSInteger.ZERO);
            this.dictionary.setItem(COSName.C, (COSBase)colorValues);
        }
        return new PDColor(colorValues.toFloatArray(), (PDColorSpace)PDDeviceRGB.INSTANCE);
    }

    public void setGuideLineColor(PDColor color) {
        COSArray values = null;
        if (color != null) {
            values = color.toCOSArray();
        }
        this.dictionary.setItem(COSName.C, (COSBase)values);
    }

    public float getGuidelineWidth() {
        return this.dictionary.getFloat(COSName.W, 1.0f);
    }

    public void setGuidelineWidth(float width) {
        this.dictionary.setFloat(COSName.W, width);
    }

    public String getGuidelineStyle() {
        return this.dictionary.getNameAsString(COSName.S, GUIDELINE_STYLE_SOLID);
    }

    public void setGuidelineStyle(String style) {
        this.dictionary.setName(COSName.S, style);
    }

    public PDLineDashPattern getLineDashPattern() {
        COSArray d = (COSArray)this.dictionary.getDictionaryObject(COSName.D);
        if (d == null) {
            d = new COSArray();
            d.add(COSInteger.THREE);
            this.dictionary.setItem(COSName.D, (COSBase)d);
        }
        COSArray lineArray = new COSArray();
        lineArray.add(d);
        PDLineDashPattern pattern = new PDLineDashPattern(lineArray, 0);
        return pattern;
    }

    public void setLineDashPattern(COSArray dashArray) {
        COSArray array = null;
        if (dashArray != null) {
            array = dashArray;
        }
        this.dictionary.setItem(COSName.D, (COSBase)array);
    }
}

