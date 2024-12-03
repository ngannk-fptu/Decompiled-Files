/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.graphics.color.PDCIEDictionaryBasedColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public final class PDCalGray
extends PDCIEDictionaryBasedColorSpace {
    private final PDColor initialColor = new PDColor(new float[]{0.0f}, (PDColorSpace)this);
    private final Map<Float, float[]> map1 = new HashMap<Float, float[]>();

    public PDCalGray() {
        super(COSName.CALGRAY);
    }

    public PDCalGray(COSArray array) {
        super(array);
    }

    @Override
    public String getName() {
        return COSName.CALGRAY.getName();
    }

    @Override
    public int getNumberOfComponents() {
        return 1;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        return new float[]{0.0f, 1.0f};
    }

    @Override
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    @Override
    public float[] toRGB(float[] value) {
        if (this.wpX == 1.0f && this.wpY == 1.0f && this.wpZ == 1.0f) {
            float a = value[0];
            float[] result = this.map1.get(Float.valueOf(a));
            if (result != null) {
                return (float[])result.clone();
            }
            float gamma = this.getGamma();
            float powAG = (float)Math.pow(a, gamma);
            result = this.convXYZtoRGB(powAG, powAG, powAG);
            this.map1.put(Float.valueOf(a), (float[])result.clone());
            return result;
        }
        return new float[]{value[0], value[0], value[0]};
    }

    public float getGamma() {
        float retval = 1.0f;
        COSNumber gamma = (COSNumber)this.dictionary.getDictionaryObject(COSName.GAMMA);
        if (gamma != null) {
            retval = gamma.floatValue();
        }
        return retval;
    }

    public void setGamma(float value) {
        this.dictionary.setItem(COSName.GAMMA, (COSBase)new COSFloat(value));
    }
}

