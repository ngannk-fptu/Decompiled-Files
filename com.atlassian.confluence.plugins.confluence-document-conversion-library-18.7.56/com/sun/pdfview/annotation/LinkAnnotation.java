/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.annotation;

import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.action.GoToAction;
import com.sun.pdfview.action.PDFAction;
import com.sun.pdfview.annotation.PDFAnnotation;
import java.io.IOException;

public class LinkAnnotation
extends PDFAnnotation {
    private PDFAction action = null;

    public LinkAnnotation(PDFObject annotObject) throws IOException {
        super(annotObject, PDFAnnotation.ANNOTATION_TYPE.LINK);
        PDFObject actionObj = annotObject.getDictRef("A");
        if (actionObj != null) {
            this.action = PDFAction.getAction(actionObj, annotObject.getRoot());
        } else {
            PDFObject dest = annotObject.getDictRef("Dest");
            if (dest == null) {
                dest = annotObject.getDictRef("DEST");
            }
            if (dest != null) {
                this.action = new GoToAction(PDFDestination.getDestination(dest, annotObject.getRoot()));
            } else {
                throw new PDFParseException("Could not parse link annotation (no Action or Destination found): " + annotObject.toString());
            }
        }
    }

    public PDFAction getAction() {
        return this.action;
    }
}

