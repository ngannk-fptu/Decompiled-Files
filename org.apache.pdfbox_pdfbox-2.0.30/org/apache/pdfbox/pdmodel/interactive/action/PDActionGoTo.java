/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class PDActionGoTo
extends PDAction {
    public static final String SUB_TYPE = "GoTo";

    public PDActionGoTo() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionGoTo(COSDictionary a) {
        super(a);
    }

    public PDDestination getDestination() throws IOException {
        return PDDestination.create(this.getCOSObject().getDictionaryObject(COSName.D));
    }

    public void setDestination(PDDestination d) {
        COSBase page;
        PDPageDestination pageDest;
        COSArray destArray;
        if (d instanceof PDPageDestination && (destArray = (pageDest = (PDPageDestination)d).getCOSObject()).size() >= 1 && !((page = destArray.getObject(0)) instanceof COSDictionary)) {
            throw new IllegalArgumentException("Destination of a GoTo action must be a page dictionary object");
        }
        this.getCOSObject().setItem(COSName.D, (COSObjectable)d);
    }
}

