/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.action;

import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.action.PDFAction;
import java.io.IOException;

public class GoToAction
extends PDFAction {
    private PDFDestination dest;

    public GoToAction(PDFObject obj, PDFObject root) throws IOException {
        super("GoTo");
        PDFObject destObj = obj.getDictRef("D");
        if (destObj == null) {
            throw new PDFParseException("No destination in GoTo action " + obj);
        }
        this.dest = PDFDestination.getDestination(destObj, root);
    }

    public GoToAction(PDFDestination dest) {
        super("GoTo");
        this.dest = dest;
    }

    public PDFDestination getDestination() {
        return this.dest;
    }
}

