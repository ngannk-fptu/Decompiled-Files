/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PdfArray
extends PdfObject {
    protected List<PdfObject> arrayList = new ArrayList<PdfObject>();

    public PdfArray() {
        super(5);
    }

    public PdfArray(PdfObject object) {
        this();
        this.arrayList.add(object);
    }

    public PdfArray(float[] values) {
        this();
        this.add(values);
    }

    public PdfArray(int[] values) {
        this();
        this.add(values);
    }

    public PdfArray(List<? extends PdfObject> pdfObjectList) {
        this();
        if (pdfObjectList != null) {
            this.arrayList.addAll(pdfObjectList);
        }
    }

    public PdfArray(PdfArray array) {
        this(array.getElements());
    }

    @Override
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        PdfObject object;
        os.write(91);
        Iterator<PdfObject> i = this.arrayList.iterator();
        if (i.hasNext()) {
            object = i.next();
            if (object == null) {
                object = PdfNull.PDFNULL;
            }
            object.toPdf(writer, os);
        }
        while (i.hasNext()) {
            int type;
            object = i.next();
            if (object == null) {
                object = PdfNull.PDFNULL;
            }
            if ((type = object.type()) != 5 && type != 6 && type != 4 && type != 3) {
                os.write(32);
            }
            object.toPdf(writer, os);
        }
        os.write(93);
    }

    @Override
    public String toString() {
        return this.arrayList.toString();
    }

    public PdfObject set(int idx, PdfObject obj) {
        return this.arrayList.set(idx, obj);
    }

    public PdfObject remove(int idx) {
        return this.arrayList.remove(idx);
    }

    @Deprecated
    public List<PdfObject> getArrayList() {
        return this.getElements();
    }

    public List<PdfObject> getElements() {
        return new ArrayList<PdfObject>(this.arrayList);
    }

    public int size() {
        return this.arrayList.size();
    }

    public boolean isEmpty() {
        return this.arrayList.isEmpty();
    }

    public boolean add(PdfObject object) {
        return this.arrayList.add(object);
    }

    public boolean add(float[] values) {
        for (float value : values) {
            this.arrayList.add(new PdfNumber(value));
        }
        return true;
    }

    public boolean add(int[] values) {
        for (int value : values) {
            this.arrayList.add(new PdfNumber(value));
        }
        return true;
    }

    public void add(int index, PdfObject element) {
        this.arrayList.add(index, element);
    }

    public void addFirst(PdfObject object) {
        this.arrayList.add(0, object);
    }

    public boolean contains(PdfObject object) {
        return this.arrayList.contains(object);
    }

    public ListIterator<PdfObject> listIterator() {
        return this.arrayList.listIterator();
    }

    public PdfObject getPdfObject(int idx) {
        return this.arrayList.get(idx);
    }

    public PdfObject getDirectObject(int idx) {
        return PdfReader.getPdfObject(this.getPdfObject(idx));
    }

    public PdfDictionary getAsDict(int idx) {
        PdfDictionary dict = null;
        PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isDictionary()) {
            dict = (PdfDictionary)orig;
        }
        return dict;
    }

    public PdfArray getAsArray(int idx) {
        PdfArray array = null;
        PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isArray()) {
            array = (PdfArray)orig;
        }
        return array;
    }

    public PdfStream getAsStream(int idx) {
        PdfStream stream = null;
        PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isStream()) {
            stream = (PdfStream)orig;
        }
        return stream;
    }

    public PdfString getAsString(int idx) {
        PdfString string = null;
        PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isString()) {
            string = (PdfString)orig;
        }
        return string;
    }

    public PdfNumber getAsNumber(int idx) {
        PdfNumber number = null;
        PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isNumber()) {
            number = (PdfNumber)orig;
        }
        return number;
    }

    public PdfName getAsName(int idx) {
        PdfName name = null;
        PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isName()) {
            name = (PdfName)orig;
        }
        return name;
    }

    public PdfBoolean getAsBoolean(int idx) {
        PdfBoolean bool = null;
        PdfObject orig = this.getDirectObject(idx);
        if (orig != null && orig.isBoolean()) {
            bool = (PdfBoolean)orig;
        }
        return bool;
    }

    public PdfIndirectReference getAsIndirectObject(int idx) {
        PdfIndirectReference ref = null;
        PdfObject orig = this.getPdfObject(idx);
        if (orig != null && orig.isIndirect()) {
            ref = (PdfIndirectReference)orig;
        }
        return ref;
    }
}

