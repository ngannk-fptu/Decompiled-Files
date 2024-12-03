/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PdfDashPattern
extends PdfArray {
    private float dash = -1.0f;
    private float gap = -1.0f;
    private float phase = -1.0f;

    public PdfDashPattern() {
    }

    public PdfDashPattern(float dash) {
        super(new PdfNumber(dash));
        this.dash = dash;
    }

    public PdfDashPattern(float dash, float gap) {
        super(new PdfNumber(dash));
        this.add(new PdfNumber(gap));
        this.dash = dash;
        this.gap = gap;
    }

    public PdfDashPattern(float dash, float gap, float phase) {
        super(new PdfNumber(dash));
        this.add(new PdfNumber(gap));
        this.dash = dash;
        this.gap = gap;
        this.phase = phase;
    }

    public void add(float n) {
        this.add(new PdfNumber(n));
    }

    @Override
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        os.write(91);
        if (this.dash >= 0.0f) {
            new PdfNumber(this.dash).toPdf(writer, os);
            if (this.gap >= 0.0f) {
                os.write(32);
                new PdfNumber(this.gap).toPdf(writer, os);
            }
        }
        os.write(93);
        if (this.phase >= 0.0f) {
            os.write(32);
            new PdfNumber(this.phase).toPdf(writer, os);
        }
    }
}

