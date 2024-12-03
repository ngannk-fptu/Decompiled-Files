/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;

public class PDAnnotationPopup
extends PDAnnotation {
    public static final String SUB_TYPE = "Popup";

    public PDAnnotationPopup() {
        this.getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public PDAnnotationPopup(COSDictionary field) {
        super(field);
    }

    public void setOpen(boolean open) {
        this.getCOSObject().setBoolean("Open", open);
    }

    public boolean getOpen() {
        return this.getCOSObject().getBoolean("Open", false);
    }

    public void setParent(PDAnnotationMarkup annot) {
        this.getCOSObject().setItem(COSName.PARENT, (COSBase)annot.getCOSObject());
    }

    public PDAnnotationMarkup getParent() {
        PDAnnotationMarkup am = null;
        try {
            am = (PDAnnotationMarkup)PDAnnotation.createAnnotation(this.getCOSObject().getDictionaryObject(COSName.PARENT, COSName.P));
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return am;
    }
}

