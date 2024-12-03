/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.collection.PdfCollectionField;

public class PdfCollectionSchema
extends PdfDictionary {
    public PdfCollectionSchema() {
        super(PdfName.COLLECTIONSCHEMA);
    }

    public void addField(String name, PdfCollectionField field) {
        this.put(new PdfName(name), field);
    }
}

