/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;

class PdfResources
extends PdfDictionary {
    PdfResources() {
    }

    void add(PdfName key, PdfDictionary resource) {
        if (resource.size() == 0) {
            return;
        }
        PdfDictionary dic = this.getAsDict(key);
        if (dic == null) {
            this.put(key, resource);
        } else {
            dic.putAll(resource);
        }
    }
}

