/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;

public class PdfMediaClipData
extends PdfDictionary {
    PdfMediaClipData(String file, PdfFileSpecification fs, String mimeType) throws IOException {
        this.put(PdfName.TYPE, new PdfName("MediaClip"));
        this.put(PdfName.S, new PdfName("MCD"));
        this.put(PdfName.N, new PdfString("Media clip for " + file));
        this.put(new PdfName("CT"), new PdfString(mimeType));
        PdfDictionary dic = new PdfDictionary();
        dic.put(new PdfName("TF"), new PdfString("TEMPACCESS"));
        this.put(new PdfName("P"), dic);
        this.put(PdfName.D, fs.getReference());
    }
}

