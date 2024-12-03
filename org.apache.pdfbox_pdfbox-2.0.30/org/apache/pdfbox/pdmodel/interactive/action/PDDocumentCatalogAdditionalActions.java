/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;

public class PDDocumentCatalogAdditionalActions
implements COSObjectable {
    private final COSDictionary actions;

    public PDDocumentCatalogAdditionalActions() {
        this.actions = new COSDictionary();
    }

    public PDDocumentCatalogAdditionalActions(COSDictionary a) {
        this.actions = a;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.actions;
    }

    public PDAction getWC() {
        COSDictionary wc = (COSDictionary)this.actions.getDictionaryObject("WC");
        PDAction retval = null;
        if (wc != null) {
            retval = PDActionFactory.createAction(wc);
        }
        return retval;
    }

    public void setWC(PDAction wc) {
        this.actions.setItem("WC", (COSObjectable)wc);
    }

    public PDAction getWS() {
        COSDictionary ws = (COSDictionary)this.actions.getDictionaryObject("WS");
        PDAction retval = null;
        if (ws != null) {
            retval = PDActionFactory.createAction(ws);
        }
        return retval;
    }

    public void setWS(PDAction ws) {
        this.actions.setItem("WS", (COSObjectable)ws);
    }

    public PDAction getDS() {
        COSDictionary ds = (COSDictionary)this.actions.getDictionaryObject("DS");
        PDAction retval = null;
        if (ds != null) {
            retval = PDActionFactory.createAction(ds);
        }
        return retval;
    }

    public void setDS(PDAction ds) {
        this.actions.setItem("DS", (COSObjectable)ds);
    }

    public PDAction getWP() {
        COSDictionary wp = (COSDictionary)this.actions.getDictionaryObject("WP");
        PDAction retval = null;
        if (wp != null) {
            retval = PDActionFactory.createAction(wp);
        }
        return retval;
    }

    public void setWP(PDAction wp) {
        this.actions.setItem("WP", (COSObjectable)wp);
    }

    public PDAction getDP() {
        COSDictionary dp = (COSDictionary)this.actions.getDictionaryObject("DP");
        PDAction retval = null;
        if (dp != null) {
            retval = PDActionFactory.createAction(dp);
        }
        return retval;
    }

    public void setDP(PDAction dp) {
        this.actions.setItem("DP", (COSObjectable)dp);
    }
}

