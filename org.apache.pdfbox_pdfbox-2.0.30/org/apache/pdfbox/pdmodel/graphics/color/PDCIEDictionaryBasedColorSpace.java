/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.color.ColorSpace;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDCIEBasedColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDTristimulus;

public abstract class PDCIEDictionaryBasedColorSpace
extends PDCIEBasedColorSpace {
    protected COSDictionary dictionary;
    private static final ColorSpace CIEXYZ = ColorSpace.getInstance(1001);
    protected float wpX = 1.0f;
    protected float wpY = 1.0f;
    protected float wpZ = 1.0f;

    protected PDCIEDictionaryBasedColorSpace(COSName cosName) {
        this.array = new COSArray();
        this.dictionary = new COSDictionary();
        this.array.add(cosName);
        this.array.add(this.dictionary);
        this.fillWhitepointCache(this.getWhitepoint());
    }

    protected PDCIEDictionaryBasedColorSpace(COSArray rgb) {
        this.array = rgb;
        this.dictionary = (COSDictionary)this.array.getObject(1);
        this.fillWhitepointCache(this.getWhitepoint());
    }

    private void fillWhitepointCache(PDTristimulus whitepoint) {
        this.wpX = whitepoint.getX();
        this.wpY = whitepoint.getY();
        this.wpZ = whitepoint.getZ();
    }

    protected float[] convXYZtoRGB(float x, float y, float z) {
        if (x < 0.0f) {
            x = 0.0f;
        }
        if (y < 0.0f) {
            y = 0.0f;
        }
        if (z < 0.0f) {
            z = 0.0f;
        }
        return CIEXYZ.toRGB(new float[]{x, y, z});
    }

    public final PDTristimulus getWhitepoint() {
        COSArray wp = (COSArray)this.dictionary.getDictionaryObject(COSName.WHITE_POINT);
        if (wp == null) {
            wp = new COSArray();
            wp.add(new COSFloat(1.0f));
            wp.add(new COSFloat(1.0f));
            wp.add(new COSFloat(1.0f));
        }
        return new PDTristimulus(wp);
    }

    public final PDTristimulus getBlackPoint() {
        COSArray bp = (COSArray)this.dictionary.getDictionaryObject(COSName.BLACK_POINT);
        if (bp == null) {
            bp = new COSArray();
            bp.add(new COSFloat(0.0f));
            bp.add(new COSFloat(0.0f));
            bp.add(new COSFloat(0.0f));
        }
        return new PDTristimulus(bp);
    }

    public void setWhitePoint(PDTristimulus whitepoint) {
        if (whitepoint == null) {
            throw new IllegalArgumentException("Whitepoint may not be null");
        }
        this.dictionary.setItem(COSName.WHITE_POINT, (COSObjectable)whitepoint);
        this.fillWhitepointCache(whitepoint);
    }

    public void setBlackPoint(PDTristimulus blackpoint) {
        this.dictionary.setItem(COSName.BLACK_POINT, (COSObjectable)blackpoint);
    }
}

