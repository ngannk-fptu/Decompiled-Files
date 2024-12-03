/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.pattern;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;

public class PDTilingPattern
extends PDAbstractPattern
implements PDContentStream {
    public static final int PAINT_COLORED = 1;
    public static final int PAINT_UNCOLORED = 2;
    public static final int TILING_CONSTANT_SPACING = 1;
    public static final int TILING_NO_DISTORTION = 2;
    public static final int TILING_CONSTANT_SPACING_FASTER_TILING = 3;
    private final ResourceCache resourceCache;

    public PDTilingPattern() {
        super(new COSStream());
        this.getCOSObject().setName(COSName.TYPE, COSName.PATTERN.getName());
        this.getCOSObject().setInt(COSName.PATTERN_TYPE, 1);
        this.setResources(new PDResources());
        this.resourceCache = null;
    }

    public PDTilingPattern(COSDictionary dictionary) {
        this(dictionary, null);
    }

    public PDTilingPattern(COSDictionary dictionary, ResourceCache resourceCache) {
        super(dictionary);
        this.resourceCache = resourceCache;
    }

    @Override
    public int getPatternType() {
        return 1;
    }

    @Override
    public void setPaintType(int paintType) {
        this.getCOSObject().setInt(COSName.PAINT_TYPE, paintType);
    }

    public int getPaintType() {
        return this.getCOSObject().getInt(COSName.PAINT_TYPE, 0);
    }

    public void setTilingType(int tilingType) {
        this.getCOSObject().setInt(COSName.TILING_TYPE, tilingType);
    }

    public int getTilingType() {
        return this.getCOSObject().getInt(COSName.TILING_TYPE, 0);
    }

    public void setXStep(float xStep) {
        this.getCOSObject().setFloat(COSName.X_STEP, xStep);
    }

    public float getXStep() {
        return this.getCOSObject().getFloat(COSName.X_STEP, 0.0f);
    }

    public void setYStep(float yStep) {
        this.getCOSObject().setFloat(COSName.Y_STEP, yStep);
    }

    public float getYStep() {
        return this.getCOSObject().getFloat(COSName.Y_STEP, 0.0f);
    }

    public PDStream getContentStream() {
        return new PDStream((COSStream)this.getCOSObject());
    }

    @Override
    public InputStream getContents() throws IOException {
        COSDictionary dict = this.getCOSObject();
        if (dict instanceof COSStream) {
            return ((COSStream)this.getCOSObject()).createInputStream();
        }
        return null;
    }

    @Override
    public PDResources getResources() {
        PDResources retval = null;
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.RESOURCES);
        if (base instanceof COSDictionary) {
            retval = new PDResources((COSDictionary)base);
        }
        return retval;
    }

    public final void setResources(PDResources resources) {
        this.getCOSObject().setItem(COSName.RESOURCES, (COSObjectable)resources);
    }

    @Override
    public PDRectangle getBBox() {
        PDRectangle retval = null;
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.BBOX);
        if (base instanceof COSArray) {
            retval = new PDRectangle((COSArray)base);
        }
        return retval;
    }

    public void setBBox(PDRectangle bbox) {
        if (bbox == null) {
            this.getCOSObject().removeItem(COSName.BBOX);
        } else {
            this.getCOSObject().setItem(COSName.BBOX, (COSBase)bbox.getCOSArray());
        }
    }
}

