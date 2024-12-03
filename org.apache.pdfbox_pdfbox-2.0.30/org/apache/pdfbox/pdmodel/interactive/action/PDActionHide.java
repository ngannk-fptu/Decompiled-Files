/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;

public class PDActionHide
extends PDAction {
    public static final String SUB_TYPE = "Hide";

    public PDActionHide() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionHide(COSDictionary a) {
        super(a);
    }

    public COSBase getT() {
        return this.action.getDictionaryObject(COSName.T);
    }

    public void setT(COSBase t) {
        this.action.setItem(COSName.T, t);
    }

    public boolean getH() {
        return this.action.getBoolean(COSName.H, true);
    }

    public void setH(boolean h) {
        this.action.setItem(COSName.H, (COSBase)COSBoolean.getBoolean(h));
    }
}

