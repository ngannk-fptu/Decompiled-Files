/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.shading.AxialShadingPaint;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.util.Matrix;

public class PDShadingType2
extends PDShading {
    private COSArray coords = null;
    private COSArray domain = null;
    private COSArray extend = null;

    public PDShadingType2(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType() {
        return 2;
    }

    public COSArray getExtend() {
        if (this.extend == null) {
            this.extend = (COSArray)this.getCOSObject().getDictionaryObject(COSName.EXTEND);
        }
        return this.extend;
    }

    public void setExtend(COSArray newExtend) {
        this.extend = newExtend;
        this.getCOSObject().setItem(COSName.EXTEND, (COSBase)newExtend);
    }

    public COSArray getDomain() {
        if (this.domain == null) {
            this.domain = (COSArray)this.getCOSObject().getDictionaryObject(COSName.DOMAIN);
        }
        return this.domain;
    }

    public void setDomain(COSArray newDomain) {
        this.domain = newDomain;
        this.getCOSObject().setItem(COSName.DOMAIN, (COSBase)newDomain);
    }

    public COSArray getCoords() {
        if (this.coords == null) {
            this.coords = (COSArray)this.getCOSObject().getDictionaryObject(COSName.COORDS);
        }
        return this.coords;
    }

    public void setCoords(COSArray newCoords) {
        this.coords = newCoords;
        this.getCOSObject().setItem(COSName.COORDS, (COSBase)newCoords);
    }

    @Override
    public Paint toPaint(Matrix matrix) {
        return new AxialShadingPaint(this, matrix);
    }
}

