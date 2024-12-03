/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.action;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.action.PDFAction;
import com.sun.pdfview.action.PdfObjectParseUtil;
import java.io.IOException;

public class UriAction
extends PDFAction {
    private String uri;

    public UriAction(PDFObject obj, PDFObject root) throws IOException {
        super("URI");
        this.uri = PdfObjectParseUtil.parseStringFromDict("URI", obj, true);
    }

    public UriAction(String uri) throws IOException {
        super("URI");
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }
}

