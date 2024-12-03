/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.pattern;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public class PDShadingPattern
extends PDAbstractPattern {
    private PDExtendedGraphicsState extendedGraphicsState;
    private PDShading shading;

    public PDShadingPattern() {
        this.getCOSObject().setInt(COSName.PATTERN_TYPE, 2);
    }

    public PDShadingPattern(COSDictionary resourceDictionary) {
        super(resourceDictionary);
    }

    @Override
    public int getPatternType() {
        return 2;
    }

    public PDExtendedGraphicsState getExtendedGraphicsState() {
        COSBase base;
        if (this.extendedGraphicsState == null && (base = this.getCOSObject().getDictionaryObject(COSName.EXT_G_STATE)) instanceof COSDictionary) {
            this.extendedGraphicsState = new PDExtendedGraphicsState((COSDictionary)base);
        }
        return this.extendedGraphicsState;
    }

    public void setExtendedGraphicsState(PDExtendedGraphicsState extendedGraphicsState) {
        this.extendedGraphicsState = extendedGraphicsState;
        this.getCOSObject().setItem(COSName.EXT_G_STATE, (COSObjectable)extendedGraphicsState);
    }

    public PDShading getShading() throws IOException {
        COSBase base;
        if (this.shading == null && (base = this.getCOSObject().getDictionaryObject(COSName.SHADING)) instanceof COSDictionary) {
            this.shading = PDShading.create((COSDictionary)base);
        }
        return this.shading;
    }

    public void setShading(PDShading shadingResources) {
        this.shading = shadingResources;
        this.getCOSObject().setItem(COSName.SHADING, (COSObjectable)shadingResources);
    }
}

