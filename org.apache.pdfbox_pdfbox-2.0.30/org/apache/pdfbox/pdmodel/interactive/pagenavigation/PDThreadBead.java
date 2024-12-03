/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.pagenavigation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDThread;

public class PDThreadBead
implements COSObjectable {
    private final COSDictionary bead;

    public PDThreadBead(COSDictionary b) {
        this.bead = b;
    }

    public PDThreadBead() {
        this.bead = new COSDictionary();
        this.bead.setName("Type", "Bead");
        this.setNextBead(this);
        this.setPreviousBead(this);
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.bead;
    }

    public PDThread getThread() {
        PDThread retval = null;
        COSDictionary dic = (COSDictionary)this.bead.getDictionaryObject("T");
        if (dic != null) {
            retval = new PDThread(dic);
        }
        return retval;
    }

    public void setThread(PDThread thread) {
        this.bead.setItem("T", (COSObjectable)thread);
    }

    public PDThreadBead getNextBead() {
        return new PDThreadBead((COSDictionary)this.bead.getDictionaryObject("N"));
    }

    protected final void setNextBead(PDThreadBead next) {
        this.bead.setItem("N", (COSObjectable)next);
    }

    public PDThreadBead getPreviousBead() {
        return new PDThreadBead((COSDictionary)this.bead.getDictionaryObject("V"));
    }

    protected final void setPreviousBead(PDThreadBead previous) {
        this.bead.setItem("V", (COSObjectable)previous);
    }

    public void appendBead(PDThreadBead append) {
        PDThreadBead nextBead = this.getNextBead();
        nextBead.setPreviousBead(append);
        append.setNextBead(nextBead);
        this.setNextBead(append);
        append.setPreviousBead(this);
    }

    public PDPage getPage() {
        PDPage page = null;
        COSDictionary dic = (COSDictionary)this.bead.getDictionaryObject("P");
        if (dic != null) {
            page = new PDPage(dic);
        }
        return page;
    }

    public void setPage(PDPage page) {
        this.bead.setItem("P", (COSObjectable)page);
    }

    public PDRectangle getRectangle() {
        PDRectangle rect = null;
        COSArray array = (COSArray)this.bead.getDictionaryObject(COSName.R);
        if (array != null) {
            rect = new PDRectangle(array);
        }
        return rect;
    }

    public void setRectangle(PDRectangle rect) {
        this.bead.setItem(COSName.R, (COSObjectable)rect);
    }
}

