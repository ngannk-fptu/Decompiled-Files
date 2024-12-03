/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfObject;

public class PdfBoolean
extends PdfObject {
    public static final PdfBoolean PDFTRUE = new PdfBoolean(true);
    public static final PdfBoolean PDFFALSE = new PdfBoolean(false);
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    private boolean value;

    public PdfBoolean(boolean value) {
        super(1);
        if (value) {
            this.setContent(TRUE);
        } else {
            this.setContent(FALSE);
        }
        this.value = value;
    }

    public PdfBoolean(String value) throws BadPdfFormatException {
        super(1, value);
        if (value.equals(TRUE)) {
            this.value = true;
        } else if (value.equals(FALSE)) {
            this.value = false;
        } else {
            throw new BadPdfFormatException(MessageLocalization.getComposedMessage("the.value.has.to.be.true.of.false.instead.of.1", value));
        }
    }

    public boolean booleanValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value ? TRUE : FALSE;
    }
}

