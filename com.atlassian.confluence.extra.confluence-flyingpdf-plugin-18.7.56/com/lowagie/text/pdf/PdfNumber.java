/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfObject;

public class PdfNumber
extends PdfObject {
    private double value;

    public PdfNumber(String content) {
        super(2);
        try {
            this.value = Double.parseDouble(content.trim());
            this.setContent(content);
        }
        catch (NumberFormatException nfe) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("1.is.not.a.valid.number.2", content, nfe.toString()));
        }
    }

    public PdfNumber(int value) {
        super(2);
        this.value = value;
        this.setContent(String.valueOf(value));
    }

    public PdfNumber(long value) {
        super(2);
        this.value = value;
        this.setContent(String.valueOf(value));
    }

    public PdfNumber(double value) {
        super(2);
        this.value = value;
        this.setContent(ByteBuffer.formatDouble(value));
    }

    public PdfNumber(float value) {
        this((double)value);
    }

    public int intValue() {
        return (int)this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public float floatValue() {
        return (float)this.value;
    }

    public void increment() {
        this.value += 1.0;
        this.setContent(ByteBuffer.formatDouble(this.value));
    }
}

