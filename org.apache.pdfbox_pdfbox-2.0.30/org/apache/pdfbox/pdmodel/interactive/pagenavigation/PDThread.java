/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.pagenavigation;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDThreadBead;

public class PDThread
implements COSObjectable {
    private COSDictionary thread;

    public PDThread(COSDictionary t) {
        this.thread = t;
    }

    public PDThread() {
        this.thread = new COSDictionary();
        this.thread.setName("Type", "Thread");
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.thread;
    }

    public PDDocumentInformation getThreadInfo() {
        PDDocumentInformation retval = null;
        COSDictionary info = (COSDictionary)this.thread.getDictionaryObject("I");
        if (info != null) {
            retval = new PDDocumentInformation(info);
        }
        return retval;
    }

    public void setThreadInfo(PDDocumentInformation info) {
        this.thread.setItem("I", (COSObjectable)info);
    }

    public PDThreadBead getFirstBead() {
        PDThreadBead retval = null;
        COSDictionary bead = (COSDictionary)this.thread.getDictionaryObject("F");
        if (bead != null) {
            retval = new PDThreadBead(bead);
        }
        return retval;
    }

    public void setFirstBead(PDThreadBead bead) {
        if (bead != null) {
            bead.setThread(this);
        }
        this.thread.setItem("F", (COSObjectable)bead);
    }
}

