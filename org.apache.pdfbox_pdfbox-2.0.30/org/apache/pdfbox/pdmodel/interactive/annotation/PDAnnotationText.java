/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDTextAppearanceHandler;

public class PDAnnotationText
extends PDAnnotationMarkup {
    private PDAppearanceHandler customAppearanceHandler;
    public static final String NAME_COMMENT = "Comment";
    public static final String NAME_KEY = "Key";
    public static final String NAME_NOTE = "Note";
    public static final String NAME_HELP = "Help";
    public static final String NAME_NEW_PARAGRAPH = "NewParagraph";
    public static final String NAME_PARAGRAPH = "Paragraph";
    public static final String NAME_INSERT = "Insert";
    public static final String NAME_CIRCLE = "Circle";
    public static final String NAME_CROSS = "Cross";
    public static final String NAME_STAR = "Star";
    public static final String NAME_CHECK = "Check";
    public static final String NAME_RIGHT_ARROW = "RightArrow";
    public static final String NAME_RIGHT_POINTER = "RightPointer";
    public static final String NAME_UP_ARROW = "UpArrow";
    public static final String NAME_UP_LEFT_ARROW = "UpLeftArrow";
    public static final String NAME_CROSS_HAIRS = "CrossHairs";
    public static final String SUB_TYPE = "Text";

    public PDAnnotationText() {
        this.getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public PDAnnotationText(COSDictionary field) {
        super(field);
    }

    public void setOpen(boolean open) {
        this.getCOSObject().setBoolean(COSName.getPDFName("Open"), open);
    }

    public boolean getOpen() {
        return this.getCOSObject().getBoolean(COSName.getPDFName("Open"), false);
    }

    public void setName(String name) {
        this.getCOSObject().setName(COSName.NAME, name);
    }

    public String getName() {
        return this.getCOSObject().getNameAsString(COSName.NAME, NAME_NOTE);
    }

    public String getState() {
        return this.getCOSObject().getString(COSName.STATE);
    }

    public void setState(String state) {
        this.getCOSObject().setString(COSName.STATE, state);
    }

    public String getStateModel() {
        return this.getCOSObject().getString(COSName.STATE_MODEL);
    }

    public void setStateModel(String stateModel) {
        this.getCOSObject().setString(COSName.STATE_MODEL, stateModel);
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
            PDTextAppearanceHandler appearanceHandler = new PDTextAppearanceHandler(this, document);
            appearanceHandler.generateAppearanceStreams();
        } else {
            this.customAppearanceHandler.generateAppearanceStreams();
        }
    }
}

