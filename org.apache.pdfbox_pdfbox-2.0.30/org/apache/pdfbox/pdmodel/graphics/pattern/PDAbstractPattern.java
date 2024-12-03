/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.pattern;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDShadingPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.util.Matrix;

public abstract class PDAbstractPattern
implements COSObjectable {
    public static final int TYPE_TILING_PATTERN = 1;
    public static final int TYPE_SHADING_PATTERN = 2;
    private final COSDictionary patternDictionary;

    public static PDAbstractPattern create(COSDictionary dictionary) throws IOException {
        return PDAbstractPattern.create(dictionary, null);
    }

    public static PDAbstractPattern create(COSDictionary dictionary, ResourceCache resourceCache) throws IOException {
        PDAbstractPattern pattern;
        int patternType = dictionary.getInt(COSName.PATTERN_TYPE, 0);
        switch (patternType) {
            case 1: {
                pattern = new PDTilingPattern(dictionary, resourceCache);
                break;
            }
            case 2: {
                pattern = new PDShadingPattern(dictionary);
                break;
            }
            default: {
                throw new IOException("Error: Unknown pattern type " + patternType);
            }
        }
        return pattern;
    }

    public PDAbstractPattern() {
        this.patternDictionary = new COSDictionary();
        this.patternDictionary.setName(COSName.TYPE, COSName.PATTERN.getName());
    }

    public PDAbstractPattern(COSDictionary dictionary) {
        this.patternDictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.patternDictionary;
    }

    public void setPaintType(int paintType) {
        this.patternDictionary.setInt(COSName.PAINT_TYPE, paintType);
    }

    public String getType() {
        return COSName.PATTERN.getName();
    }

    public void setPatternType(int patternType) {
        this.patternDictionary.setInt(COSName.PATTERN_TYPE, patternType);
    }

    public abstract int getPatternType();

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
}

