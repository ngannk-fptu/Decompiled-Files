/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.action;

import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.action.PDFAction;
import com.sun.pdfview.action.PdfObjectParseUtil;
import java.io.IOException;

public class GoToRAction
extends PDFAction {
    private PDFDestination destination;
    private String file;
    private boolean newWindow = false;

    public GoToRAction(PDFObject obj, PDFObject root) throws IOException {
        super("GoToR");
        this.destination = PdfObjectParseUtil.parseDestination("D", obj, root, true);
        this.file = PdfObjectParseUtil.parseStringFromDict("F", obj, true);
        this.newWindow = PdfObjectParseUtil.parseBooleanFromDict("NewWindow", obj, false);
    }

    public GoToRAction(PDFDestination dest, String file, boolean newWindow) {
        super("GoToR");
        this.file = file;
        this.destination = dest;
        this.newWindow = newWindow;
    }

    public PDFDestination getDestination() {
        return this.destination;
    }

    public String getFile() {
        return this.file;
    }

    public boolean isNewWindow() {
        return this.newWindow;
    }
}

