/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;

public class PdfCollectionSort
extends PdfDictionary {
    public PdfCollectionSort(String key) {
        super(PdfName.COLLECTIONSORT);
        this.put(PdfName.S, new PdfName(key));
    }

    public PdfCollectionSort(String[] keys) {
        super(PdfName.COLLECTIONSORT);
        PdfArray array = new PdfArray();
        for (String key : keys) {
            array.add(new PdfName(key));
        }
        this.put(PdfName.S, array);
    }

    public void setSortOrder(boolean ascending) {
        PdfObject o = this.get(PdfName.S);
        if (!(o instanceof PdfName)) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.have.to.define.a.boolean.array.for.this.collection.sort.dictionary"));
        }
        this.put(PdfName.A, new PdfBoolean(ascending));
    }

    public void setSortOrder(boolean[] ascending) {
        PdfArray array;
        PdfObject o = this.get(PdfName.S);
        if (o instanceof PdfArray) {
            if (((PdfArray)o).size() != ascending.length) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.number.of.booleans.in.this.array.doesn.t.correspond.with.the.number.of.fields"));
            }
            array = new PdfArray();
            for (boolean b : ascending) {
                array.add(new PdfBoolean(b));
            }
        } else {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.need.a.single.boolean.for.this.collection.sort.dictionary"));
        }
        this.put(PdfName.A, array);
    }
}

