/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;

public class PDBorderStyleDictionary
implements COSObjectable {
    public static final String STYLE_SOLID = "S";
    public static final String STYLE_DASHED = "D";
    public static final String STYLE_BEVELED = "B";
    public static final String STYLE_INSET = "I";
    public static final String STYLE_UNDERLINE = "U";
    private final COSDictionary dictionary;

    public PDBorderStyleDictionary() {
        this.dictionary = new COSDictionary();
    }

    public PDBorderStyleDictionary(COSDictionary dict) {
        this.dictionary = dict;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public void setWidth(float w) {
        if (w == (float)((int)w)) {
            this.getCOSObject().setInt(COSName.W, (int)w);
        } else {
            this.getCOSObject().setFloat(COSName.W, w);
        }
    }

    public float getWidth() {
        if (this.getCOSObject().getDictionaryObject(COSName.W) instanceof COSName) {
            return 0.0f;
        }
        return this.getCOSObject().getFloat(COSName.W, 1.0f);
    }

    public void setStyle(String s) {
        this.getCOSObject().setName(COSName.S, s);
    }

    public String getStyle() {
        return this.getCOSObject().getNameAsString(COSName.S, STYLE_SOLID);
    }

    public void setDashStyle(COSArray dashArray) {
        this.getCOSObject().setItem(COSName.D, (COSBase)dashArray);
    }

    public PDLineDashPattern getDashStyle() {
        COSArray d = (COSArray)this.getCOSObject().getDictionaryObject(COSName.D);
        if (d == null) {
            d = new COSArray();
            d.add(COSInteger.THREE);
            this.getCOSObject().setItem(COSName.D, (COSBase)d);
        }
        return new PDLineDashPattern(d, 0);
    }
}

