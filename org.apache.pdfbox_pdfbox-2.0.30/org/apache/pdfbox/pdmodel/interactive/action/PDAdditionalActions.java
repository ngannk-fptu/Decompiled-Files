/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;

public class PDAdditionalActions
implements COSObjectable {
    private final COSDictionary actions;

    public PDAdditionalActions() {
        this.actions = new COSDictionary();
    }

    public PDAdditionalActions(COSDictionary a) {
        this.actions = a;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.actions;
    }

    public PDAction getF() {
        return PDActionFactory.createAction((COSDictionary)this.actions.getDictionaryObject(COSName.F));
    }

    public void setF(PDAction action) {
        this.actions.setItem(COSName.F, (COSObjectable)action);
    }
}

