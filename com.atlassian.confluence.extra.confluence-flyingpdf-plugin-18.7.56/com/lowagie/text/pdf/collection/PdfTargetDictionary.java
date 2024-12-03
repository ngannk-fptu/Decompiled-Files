/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;

public class PdfTargetDictionary
extends PdfDictionary {
    public PdfTargetDictionary(PdfTargetDictionary nested) {
        this.put(PdfName.R, PdfName.P);
        if (nested != null) {
            this.setAdditionalPath(nested);
        }
    }

    public PdfTargetDictionary(boolean child) {
        if (child) {
            this.put(PdfName.R, PdfName.C);
        } else {
            this.put(PdfName.R, PdfName.P);
        }
    }

    public void setEmbeddedFileName(String target) {
        this.put(PdfName.N, new PdfString(target, null));
    }

    public void setFileAttachmentPagename(String name) {
        this.put(PdfName.P, new PdfString(name, null));
    }

    public void setFileAttachmentPage(int page) {
        this.put(PdfName.P, new PdfNumber(page));
    }

    public void setFileAttachmentName(String name) {
        this.put(PdfName.A, new PdfString(name, "UnicodeBig"));
    }

    public void setFileAttachmentIndex(int annotation) {
        this.put(PdfName.A, new PdfNumber(annotation));
    }

    public void setAdditionalPath(PdfTargetDictionary nested) {
        this.put(PdfName.T, nested);
    }
}

