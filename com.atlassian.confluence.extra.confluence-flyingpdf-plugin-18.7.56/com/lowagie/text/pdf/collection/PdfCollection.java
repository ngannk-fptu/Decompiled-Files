/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.collection.PdfCollectionSchema;
import com.lowagie.text.pdf.collection.PdfCollectionSort;

public class PdfCollection
extends PdfDictionary {
    public static final int DETAILS = 0;
    public static final int TILE = 1;
    public static final int HIDDEN = 2;

    public PdfCollection(int type) {
        super(PdfName.COLLECTION);
        switch (type) {
            case 1: {
                this.put(PdfName.VIEW, PdfName.T);
                break;
            }
            case 2: {
                this.put(PdfName.VIEW, PdfName.H);
                break;
            }
            default: {
                this.put(PdfName.VIEW, PdfName.D);
            }
        }
    }

    public void setInitialDocument(String description) {
        this.put(PdfName.D, new PdfString(description, null));
    }

    public void setSchema(PdfCollectionSchema schema) {
        this.put(PdfName.SCHEMA, schema);
    }

    public PdfCollectionSchema getSchema() {
        return (PdfCollectionSchema)this.get(PdfName.SCHEMA);
    }

    public void setSort(PdfCollectionSort sort) {
        this.put(PdfName.SORT, sort);
    }
}

