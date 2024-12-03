/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDCircleAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDSquareAppearanceHandler;

public class PDAnnotationSquareCircle
extends PDAnnotationMarkup {
    public static final String SUB_TYPE_SQUARE = "Square";
    public static final String SUB_TYPE_CIRCLE = "Circle";
    private PDAppearanceHandler customAppearanceHandler;

    public PDAnnotationSquareCircle(String subType) {
        this.setSubtype(subType);
    }

    public PDAnnotationSquareCircle(COSDictionary field) {
        super(field);
    }

    @Override
    public void setInteriorColor(PDColor ic) {
        this.getCOSObject().setItem(COSName.IC, (COSBase)ic.toCOSArray());
    }

    @Override
    public PDColor getInteriorColor() {
        return this.getColor(COSName.IC);
    }

    @Override
    public void setBorderEffect(PDBorderEffectDictionary be) {
        this.getCOSObject().setItem(COSName.BE, (COSObjectable)be);
    }

    @Override
    public PDBorderEffectDictionary getBorderEffect() {
        COSDictionary be = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.BE);
        if (be != null) {
            return new PDBorderEffectDictionary(be);
        }
        return null;
    }

    @Override
    public void setRectDifference(PDRectangle rd) {
        this.getCOSObject().setItem(COSName.RD, (COSObjectable)rd);
    }

    @Override
    public PDRectangle getRectDifference() {
        COSArray rd = (COSArray)this.getCOSObject().getDictionaryObject(COSName.RD);
        if (rd != null) {
            return new PDRectangle(rd);
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
    public void setBorderStyle(PDBorderStyleDictionary bs) {
        this.getCOSObject().setItem(COSName.BS, (COSObjectable)bs);
    }

    @Override
    public PDBorderStyleDictionary getBorderStyle() {
        COSBase bs = this.getCOSObject().getDictionaryObject(COSName.BS);
        if (bs instanceof COSDictionary) {
            return new PDBorderStyleDictionary((COSDictionary)bs);
        }
        return null;
    }

    @Override
    public void setRectDifferences(float difference) {
        this.setRectDifferences(difference, difference, difference, difference);
    }

    @Override
    public void setRectDifferences(float differenceLeft, float differenceTop, float differenceRight, float differenceBottom) {
        COSArray margins = new COSArray();
        margins.add(new COSFloat(differenceLeft));
        margins.add(new COSFloat(differenceTop));
        margins.add(new COSFloat(differenceRight));
        margins.add(new COSFloat(differenceBottom));
        this.getCOSObject().setItem(COSName.RD, (COSBase)margins);
    }

    @Override
    public float[] getRectDifferences() {
        COSBase margin = this.getCOSObject().getItem(COSName.RD);
        if (margin instanceof COSArray) {
            return ((COSArray)margin).toFloatArray();
        }
        return new float[0];
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
            if (SUB_TYPE_CIRCLE.equals(this.getSubtype())) {
                PDCircleAppearanceHandler appearanceHandler = new PDCircleAppearanceHandler(this, document);
                appearanceHandler.generateAppearanceStreams();
            } else if (SUB_TYPE_SQUARE.equals(this.getSubtype())) {
                PDSquareAppearanceHandler appearanceHandler = new PDSquareAppearanceHandler(this, document);
                appearanceHandler.generateAppearanceStreams();
            }
        } else {
            this.customAppearanceHandler.generateAppearanceStreams();
        }
    }
}

