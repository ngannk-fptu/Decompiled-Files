/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;

public class PdfSignature
extends PdfDictionary {
    public PdfSignature(PdfName filter, PdfName subFilter) {
        super(PdfName.SIG);
        this.put(PdfName.FILTER, filter);
        this.put(PdfName.SUBFILTER, subFilter);
    }

    public void setByteRange(int[] range) {
        PdfArray array = new PdfArray();
        for (int i : range) {
            array.add(new PdfNumber(i));
        }
        this.put(PdfName.BYTERANGE, array);
    }

    public void setContents(byte[] contents) {
        this.put(PdfName.CONTENTS, new PdfString(contents).setHexWriting(true));
    }

    public void setCert(byte[] cert) {
        this.put(PdfName.CERT, new PdfString(cert));
    }

    public void setName(String name) {
        this.put(PdfName.NAME, new PdfString(name, "UnicodeBig"));
    }

    public void setDate(PdfDate date) {
        this.put(PdfName.M, date);
    }

    public void setLocation(String name) {
        this.put(PdfName.LOCATION, new PdfString(name, "UnicodeBig"));
    }

    public void setReason(String name) {
        this.put(PdfName.REASON, new PdfString(name, "UnicodeBig"));
    }

    public void setContact(String name) {
        this.put(PdfName.CONTACTINFO, new PdfString(name, "UnicodeBig"));
    }
}

