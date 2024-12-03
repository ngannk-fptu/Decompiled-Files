/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PRIndirectReference
extends PdfIndirectReference {
    protected PdfReader reader;

    public PRIndirectReference(PdfReader reader, int number, int generation) {
        this.type = 10;
        this.number = number;
        this.generation = generation;
        this.reader = reader;
    }

    public PRIndirectReference(PdfReader reader, int number) {
        this(reader, number, 0);
    }

    @Override
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        int n = writer.getNewObjectNumber(this.reader, this.number, this.generation);
        os.write(PdfEncodings.convertToBytes(new StringBuffer().append(n).append(" 0 R").toString(), null));
    }

    public PdfReader getReader() {
        return this.reader;
    }

    public void setNumber(int number, int generation) {
        this.number = number;
        this.generation = generation;
    }
}

