/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;

public class PDAnnotationWidget
extends PDAnnotation {
    public static final String SUB_TYPE = "Widget";

    public PDAnnotationWidget() {
        this.getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public PDAnnotationWidget(COSDictionary field) {
        super(field);
        this.getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public String getHighlightingMode() {
        return this.getCOSObject().getNameAsString(COSName.H, "I");
    }

    public void setHighlightingMode(String highlightingMode) {
        if (!(highlightingMode == null || "N".equals(highlightingMode) || "I".equals(highlightingMode) || "O".equals(highlightingMode) || "P".equals(highlightingMode) || "T".equals(highlightingMode))) {
            throw new IllegalArgumentException("Valid values for highlighting mode are 'N', 'N', 'O', 'P' or 'T'");
        }
        this.getCOSObject().setName(COSName.H, highlightingMode);
    }

    public PDAppearanceCharacteristicsDictionary getAppearanceCharacteristics() {
        COSBase mk = this.getCOSObject().getDictionaryObject(COSName.MK);
        if (mk instanceof COSDictionary) {
            return new PDAppearanceCharacteristicsDictionary((COSDictionary)mk);
        }
        return null;
    }

    public void setAppearanceCharacteristics(PDAppearanceCharacteristicsDictionary appearanceCharacteristics) {
        this.getCOSObject().setItem(COSName.MK, (COSObjectable)appearanceCharacteristics);
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

    public PDAnnotationAdditionalActions getActions() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.AA);
        if (base instanceof COSDictionary) {
            return new PDAnnotationAdditionalActions((COSDictionary)base);
        }
        return null;
    }

    public void setActions(PDAnnotationAdditionalActions actions) {
        this.getCOSObject().setItem(COSName.AA, (COSObjectable)actions);
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

    public void setParent(PDTerminalField field) {
        if (this.getCOSObject().equals(field.getCOSObject())) {
            throw new IllegalArgumentException("setParent() is not to be called for a field that shares a dictionary with its only widget");
        }
        this.getCOSObject().setItem(COSName.PARENT, (COSObjectable)field);
    }
}

