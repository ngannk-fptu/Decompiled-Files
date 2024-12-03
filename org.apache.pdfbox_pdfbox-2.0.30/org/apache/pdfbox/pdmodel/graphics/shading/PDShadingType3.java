/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType2;
import org.apache.pdfbox.pdmodel.graphics.shading.RadialShadingPaint;
import org.apache.pdfbox.util.Matrix;

public class PDShadingType3
extends PDShadingType2 {
    public PDShadingType3(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override
    public int getShadingType() {
        return 3;
    }

    @Override
    public Paint toPaint(Matrix matrix) {
        return new RadialShadingPaint(this, matrix);
    }
}

