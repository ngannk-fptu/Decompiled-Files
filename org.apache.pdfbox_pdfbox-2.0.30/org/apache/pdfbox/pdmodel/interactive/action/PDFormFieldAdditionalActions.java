/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;

public class PDFormFieldAdditionalActions
implements COSObjectable {
    private final COSDictionary actions;

    public PDFormFieldAdditionalActions() {
        this.actions = new COSDictionary();
    }

    public PDFormFieldAdditionalActions(COSDictionary a) {
        this.actions = a;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.actions;
    }

    public PDAction getK() {
        COSDictionary k = (COSDictionary)this.actions.getDictionaryObject(COSName.K);
        PDAction retval = null;
        if (k != null) {
            retval = PDActionFactory.createAction(k);
        }
        return retval;
    }

    public void setK(PDAction k) {
        this.actions.setItem(COSName.K, (COSObjectable)k);
    }

    public PDAction getF() {
        COSDictionary f = (COSDictionary)this.actions.getDictionaryObject(COSName.F);
        PDAction retval = null;
        if (f != null) {
            retval = PDActionFactory.createAction(f);
        }
        return retval;
    }

    public void setF(PDAction f) {
        this.actions.setItem(COSName.F, (COSObjectable)f);
    }

    public PDAction getV() {
        COSDictionary v = (COSDictionary)this.actions.getDictionaryObject(COSName.V);
        PDAction retval = null;
        if (v != null) {
            retval = PDActionFactory.createAction(v);
        }
        return retval;
    }

    public void setV(PDAction v) {
        this.actions.setItem(COSName.V, (COSObjectable)v);
    }

    public PDAction getC() {
        COSDictionary c = (COSDictionary)this.actions.getDictionaryObject(COSName.C);
        PDAction retval = null;
        if (c != null) {
            retval = PDActionFactory.createAction(c);
        }
        return retval;
    }

    public void setC(PDAction c) {
        this.actions.setItem(COSName.C, (COSObjectable)c);
    }
}

