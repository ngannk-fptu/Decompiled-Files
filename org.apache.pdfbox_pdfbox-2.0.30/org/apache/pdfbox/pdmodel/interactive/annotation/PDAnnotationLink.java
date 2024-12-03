/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDLinkAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;

public class PDAnnotationLink
extends PDAnnotation {
    private PDAppearanceHandler customAppearanceHandler;
    public static final String HIGHLIGHT_MODE_NONE = "N";
    public static final String HIGHLIGHT_MODE_INVERT = "I";
    public static final String HIGHLIGHT_MODE_OUTLINE = "O";
    public static final String HIGHLIGHT_MODE_PUSH = "P";
    public static final String SUB_TYPE = "Link";

    public PDAnnotationLink() {
        this.getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public PDAnnotationLink(COSDictionary field) {
        super(field);
    }

    public PDAction getAction() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.A);
        if (base instanceof COSDictionary) {
            return PDActionFactory.createAction((COSDictionary)base);
        }
        return null;
    }

    public void setAction(PDAction action) {
        this.getCOSObject().setItem(COSName.A, (COSObjectable)action);
    }

    public void setBorderStyle(PDBorderStyleDictionary bs) {
        this.getCOSObject().setItem(COSName.BS, (COSObjectable)bs);
    }

    public PDBorderStyleDictionary getBorderStyle() {
        COSBase bs = this.getCOSObject().getDictionaryObject(COSName.BS);
        if (bs instanceof COSDictionary) {
            return new PDBorderStyleDictionary((COSDictionary)bs);
        }
        return null;
    }

    public PDDestination getDestination() throws IOException {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.DEST);
        return PDDestination.create(base);
    }

    public void setDestination(PDDestination dest) {
        this.getCOSObject().setItem(COSName.DEST, (COSObjectable)dest);
    }

    public String getHighlightMode() {
        return this.getCOSObject().getNameAsString(COSName.H, HIGHLIGHT_MODE_INVERT);
    }

    public void setHighlightMode(String mode) {
        this.getCOSObject().setName(COSName.H, mode);
    }

    public void setPreviousURI(PDActionURI pa) {
        this.getCOSObject().setItem("PA", (COSObjectable)pa);
    }

    public PDActionURI getPreviousURI() {
        COSBase base = this.getCOSObject().getDictionaryObject("PA");
        if (base instanceof COSDictionary) {
            return new PDActionURI((COSDictionary)base);
        }
        return null;
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
            PDLinkAppearanceHandler appearanceHandler = new PDLinkAppearanceHandler(this, document);
            appearanceHandler.generateAppearanceStreams();
        } else {
            this.customAppearanceHandler.generateAppearanceStreams();
        }
    }
}

