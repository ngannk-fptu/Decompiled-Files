/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.collection;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.collection.PdfCollectionField;
import com.lowagie.text.pdf.collection.PdfCollectionSchema;
import java.util.Calendar;

public class PdfCollectionItem
extends PdfDictionary {
    PdfCollectionSchema schema;

    public PdfCollectionItem(PdfCollectionSchema schema) {
        super(PdfName.COLLECTIONITEM);
        this.schema = schema;
    }

    public void addItem(String key, String value) {
        PdfName fieldname = new PdfName(key);
        PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
        this.put(fieldname, field.getValue(value));
    }

    public void addItem(String key, PdfString value) {
        PdfName fieldname = new PdfName(key);
        PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
        if (field.fieldType == 0) {
            this.put(fieldname, value);
        }
    }

    public void addItem(String key, PdfDate d) {
        PdfName fieldname = new PdfName(key);
        PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
        if (field.fieldType == 1) {
            this.put(fieldname, d);
        }
    }

    public void addItem(String key, PdfNumber n) {
        PdfName fieldname = new PdfName(key);
        PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
        if (field.fieldType == 2) {
            this.put(fieldname, n);
        }
    }

    public void addItem(String key, Calendar c) {
        this.addItem(key, new PdfDate(c));
    }

    public void addItem(String key, int i) {
        this.addItem(key, new PdfNumber(i));
    }

    public void addItem(String key, float f) {
        this.addItem(key, new PdfNumber(f));
    }

    public void addItem(String key, double d) {
        this.addItem(key, new PdfNumber(d));
    }

    public void setPrefix(String key, String prefix) {
        PdfName fieldname = new PdfName(key);
        PdfObject o = this.get(fieldname);
        if (o == null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.must.set.a.value.before.adding.a.prefix"));
        }
        PdfDictionary dict = new PdfDictionary(PdfName.COLLECTIONSUBITEM);
        dict.put(PdfName.D, o);
        dict.put(PdfName.P, new PdfString(prefix, "UnicodeBig"));
        this.put(fieldname, dict);
    }
}

