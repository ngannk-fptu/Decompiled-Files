/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;

public final class PDColor {
    private static final Log LOG = LogFactory.getLog(PDColor.class);
    private final float[] components;
    private final COSName patternName;
    private final PDColorSpace colorSpace;

    public PDColor(COSArray array, PDColorSpace colorSpace) {
        if (array.size() > 0 && array.get(array.size() - 1) instanceof COSName) {
            this.components = new float[array.size() - 1];
            this.initComponents(array);
            COSBase base = array.get(array.size() - 1);
            if (base instanceof COSName) {
                this.patternName = (COSName)base;
            } else {
                LOG.warn((Object)("pattern name in " + array + " isn't a name, ignored"));
                this.patternName = COSName.getPDFName("Unknown");
            }
        } else {
            this.components = new float[array.size()];
            this.initComponents(array);
            this.patternName = null;
        }
        this.colorSpace = colorSpace;
    }

    private void initComponents(COSArray array) {
        for (int i = 0; i < this.components.length; ++i) {
            COSBase base = array.get(i);
            if (base instanceof COSNumber) {
                this.components[i] = ((COSNumber)base).floatValue();
                continue;
            }
            LOG.warn((Object)("color component " + i + " in " + array + " isn't a number, ignored"));
        }
    }

    public PDColor(float[] components, PDColorSpace colorSpace) {
        this.components = (float[])components.clone();
        this.patternName = null;
        this.colorSpace = colorSpace;
    }

    public PDColor(COSName patternName, PDColorSpace colorSpace) {
        this.components = new float[0];
        this.patternName = patternName;
        this.colorSpace = colorSpace;
    }

    public PDColor(float[] components, COSName patternName, PDColorSpace colorSpace) {
        this.components = (float[])components.clone();
        this.patternName = patternName;
        this.colorSpace = colorSpace;
    }

    public float[] getComponents() {
        if (this.colorSpace instanceof PDPattern || this.colorSpace == null) {
            return (float[])this.components.clone();
        }
        return Arrays.copyOf(this.components, this.colorSpace.getNumberOfComponents());
    }

    public COSName getPatternName() {
        return this.patternName;
    }

    public boolean isPattern() {
        return this.patternName != null;
    }

    public int toRGB() throws IOException {
        float[] floats = this.colorSpace.toRGB(this.components);
        int r = Math.round(floats[0] * 255.0f);
        int g = Math.round(floats[1] * 255.0f);
        int b = Math.round(floats[2] * 255.0f);
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
    }

    public COSArray toCOSArray() {
        COSArray array = new COSArray();
        array.setFloatArray(this.components);
        if (this.patternName != null) {
            array.add(this.patternName);
        }
        return array;
    }

    public PDColorSpace getColorSpace() {
        return this.colorSpace;
    }

    public String toString() {
        return "PDColor{components=" + Arrays.toString(this.components) + ", patternName=" + this.patternName + "}";
    }
}

