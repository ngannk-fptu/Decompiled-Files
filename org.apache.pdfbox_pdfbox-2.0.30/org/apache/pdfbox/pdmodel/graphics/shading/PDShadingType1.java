/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.shading.Type1ShadingPaint;
import org.apache.pdfbox.util.Matrix;

public class PDShadingType1
extends PDShading {
    private COSArray domain = null;

    public PDShadingType1(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType() {
        return 1;
    }

    public Matrix getMatrix() {
        return Matrix.createMatrix(this.getCOSObject().getDictionaryObject(COSName.MATRIX));
    }

    public void setMatrix(AffineTransform transform) {
        COSArray matrix = new COSArray();
        double[] values = new double[6];
        transform.getMatrix(values);
        for (double v : values) {
            matrix.add(new COSFloat((float)v));
        }
        this.getCOSObject().setItem(COSName.MATRIX, (COSBase)matrix);
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

    @Override
    public Paint toPaint(Matrix matrix) {
        return new Type1ShadingPaint(this, matrix);
    }
}

