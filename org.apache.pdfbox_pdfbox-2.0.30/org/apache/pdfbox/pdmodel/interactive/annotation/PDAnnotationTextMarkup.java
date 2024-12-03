/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDHighlightAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDSquigglyAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDStrikeoutAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDUnderlineAppearanceHandler;

public class PDAnnotationTextMarkup
extends PDAnnotationMarkup {
    private PDAppearanceHandler customAppearanceHandler;
    public static final String SUB_TYPE_HIGHLIGHT = "Highlight";
    public static final String SUB_TYPE_UNDERLINE = "Underline";
    public static final String SUB_TYPE_SQUIGGLY = "Squiggly";
    public static final String SUB_TYPE_STRIKEOUT = "StrikeOut";

    private PDAnnotationTextMarkup() {
    }

    public PDAnnotationTextMarkup(String subType) {
        this.setSubtype(subType);
        this.setQuadPoints(new float[0]);
    }

    public PDAnnotationTextMarkup(COSDictionary field) {
        super(field);
    }

    public void setQuadPoints(float[] quadPoints) {
        COSArray newQuadPoints = new COSArray();
        newQuadPoints.setFloatArray(quadPoints);
        this.getCOSObject().setItem(COSName.QUADPOINTS, (COSBase)newQuadPoints);
    }

    public float[] getQuadPoints() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.QUADPOINTS);
        if (base instanceof COSArray) {
            return ((COSArray)base).toFloatArray();
        }
        return null;
    }

    public void setSubtype(String subType) {
        this.getCOSObject().setName(COSName.SUBTYPE, subType);
    }

    @Override
    public String getSubtype() {
        return this.getCOSObject().getNameAsString(COSName.SUBTYPE);
    }

    @Override
    public void setCustomAppearanceHandler(PDAppearanceHandler appearanceHandler) {
        this.customAppearanceHandler = appearanceHandler;
    }

    @Override
    public void constructAppearances() {
        this.constructAppearances(null);
    }

    @Override
    public void constructAppearances(PDDocument document) {
        if (this.customAppearanceHandler == null) {
            PDAbstractAppearanceHandler appearanceHandler = null;
            if (SUB_TYPE_HIGHLIGHT.equals(this.getSubtype())) {
                appearanceHandler = new PDHighlightAppearanceHandler(this, document);
            } else if (SUB_TYPE_SQUIGGLY.equals(this.getSubtype())) {
                appearanceHandler = new PDSquigglyAppearanceHandler(this, document);
            } else if (SUB_TYPE_STRIKEOUT.equals(this.getSubtype())) {
                appearanceHandler = new PDStrikeoutAppearanceHandler(this, document);
            } else if (SUB_TYPE_UNDERLINE.equals(this.getSubtype())) {
                appearanceHandler = new PDUnderlineAppearanceHandler(this, document);
            }
            if (appearanceHandler != null) {
                appearanceHandler.generateAppearanceStreams();
            }
        } else {
            this.customAppearanceHandler.generateAppearanceStreams();
        }
    }
}

