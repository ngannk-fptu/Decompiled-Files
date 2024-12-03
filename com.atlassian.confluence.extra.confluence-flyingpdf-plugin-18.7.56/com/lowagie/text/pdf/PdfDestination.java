/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import java.util.StringTokenizer;

public class PdfDestination
extends PdfArray {
    public static final int XYZ = 0;
    public static final int FIT = 1;
    public static final int FITH = 2;
    public static final int FITV = 3;
    public static final int FITR = 4;
    public static final int FITB = 5;
    public static final int FITBH = 6;
    public static final int FITBV = 7;
    private boolean status = false;

    public PdfDestination(int type) {
        if (type == 5) {
            this.add(PdfName.FITB);
        } else {
            this.add(PdfName.FIT);
        }
    }

    public PdfDestination(int type, float parameter) {
        super(new PdfNumber(parameter));
        switch (type) {
            default: {
                this.addFirst(PdfName.FITH);
                break;
            }
            case 3: {
                this.addFirst(PdfName.FITV);
                break;
            }
            case 6: {
                this.addFirst(PdfName.FITBH);
                break;
            }
            case 7: {
                this.addFirst(PdfName.FITBV);
            }
        }
    }

    public PdfDestination(int type, float left, float top, float zoom) {
        super(PdfName.XYZ);
        if (left < 0.0f) {
            this.add(PdfNull.PDFNULL);
        } else {
            this.add(new PdfNumber(left));
        }
        if (top < 0.0f) {
            this.add(PdfNull.PDFNULL);
        } else {
            this.add(new PdfNumber(top));
        }
        this.add(new PdfNumber(zoom));
    }

    public PdfDestination(int type, float left, float bottom, float right, float top) {
        super(PdfName.FITR);
        this.add(new PdfNumber(left));
        this.add(new PdfNumber(bottom));
        this.add(new PdfNumber(right));
        this.add(new PdfNumber(top));
    }

    public PdfDestination(String dest) {
        StringTokenizer tokens = new StringTokenizer(dest);
        if (tokens.hasMoreTokens()) {
            this.add(new PdfName(tokens.nextToken()));
        }
        while (tokens.hasMoreTokens()) {
            this.add(new PdfNumber(tokens.nextToken()));
        }
    }

    public boolean hasPage() {
        return this.status;
    }

    public boolean addPage(PdfIndirectReference page) {
        if (!this.status) {
            this.addFirst(page);
            this.status = true;
            return true;
        }
        return false;
    }
}

