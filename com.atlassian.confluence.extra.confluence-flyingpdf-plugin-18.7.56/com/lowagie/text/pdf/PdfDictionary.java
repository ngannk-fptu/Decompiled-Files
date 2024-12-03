/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PdfDictionary
extends PdfObject {
    public static final PdfName FONT = PdfName.FONT;
    public static final PdfName OUTLINES = PdfName.OUTLINES;
    public static final PdfName PAGE = PdfName.PAGE;
    public static final PdfName PAGES = PdfName.PAGES;
    public static final PdfName CATALOG = PdfName.CATALOG;
    private PdfName dictionaryType = null;
    protected Map<PdfName, PdfObject> hashMap = new HashMap<PdfName, PdfObject>();

    public PdfDictionary() {
        super(6);
    }

    public PdfDictionary(PdfName type) {
        this();
        this.dictionaryType = type;
        this.put(PdfName.TYPE, this.dictionaryType);
    }

    @Override
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        os.write(60);
        os.write(60);
        for (PdfName pdfName : this.hashMap.keySet()) {
            PdfObject value = this.hashMap.get(pdfName);
            pdfName.toPdf(writer, os);
            int type = value.type();
            if (type != 5 && type != 6 && type != 4 && type != 3) {
                os.write(32);
            }
            value.toPdf(writer, os);
        }
        os.write(62);
        os.write(62);
    }

    @Override
    public String toString() {
        if (this.get(PdfName.TYPE) == null) {
            return "Dictionary";
        }
        return "Dictionary of type: " + this.get(PdfName.TYPE);
    }

    public void put(PdfName key, PdfObject object) {
        if (object == null || object.isNull()) {
            this.hashMap.remove(key);
        } else {
            this.hashMap.put(key, object);
        }
    }

    public void putEx(PdfName key, PdfObject value) {
        if (value == null) {
            return;
        }
        this.put(key, value);
    }

    public void putAll(PdfDictionary dic) {
        this.hashMap.putAll(dic.hashMap);
    }

    public void remove(PdfName key) {
        this.hashMap.remove(key);
    }

    public PdfObject get(PdfName key) {
        return this.hashMap.get(key);
    }

    public PdfObject getDirectObject(PdfName key) {
        return PdfReader.getPdfObject(this.get(key));
    }

    public Set<PdfName> getKeys() {
        return this.hashMap.keySet();
    }

    public int size() {
        return this.hashMap.size();
    }

    public boolean contains(PdfName key) {
        return this.hashMap.containsKey(key);
    }

    public boolean isFont() {
        return FONT.equals(this.dictionaryType);
    }

    public boolean isPage() {
        return PAGE.equals(this.dictionaryType);
    }

    public boolean isPages() {
        return PAGES.equals(this.dictionaryType);
    }

    public boolean isCatalog() {
        return CATALOG.equals(this.dictionaryType);
    }

    public boolean isOutlineTree() {
        return OUTLINES.equals(this.dictionaryType);
    }

    public void merge(PdfDictionary other) {
        this.hashMap.putAll(other.hashMap);
    }

    public void mergeDifferent(PdfDictionary other) {
        for (PdfName key : other.hashMap.keySet()) {
            if (this.hashMap.containsKey(key)) continue;
            this.hashMap.put(key, other.hashMap.get(key));
        }
    }

    public PdfDictionary getAsDict(PdfName key) {
        PdfDictionary dict = null;
        PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isDictionary()) {
            dict = (PdfDictionary)orig;
        }
        return dict;
    }

    public PdfArray getAsArray(PdfName key) {
        PdfArray array = null;
        PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isArray()) {
            array = (PdfArray)orig;
        }
        return array;
    }

    public PdfStream getAsStream(PdfName key) {
        PdfStream stream = null;
        PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isStream()) {
            stream = (PdfStream)orig;
        }
        return stream;
    }

    public PdfString getAsString(PdfName key) {
        PdfString string = null;
        PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isString()) {
            string = (PdfString)orig;
        }
        return string;
    }

    public PdfNumber getAsNumber(PdfName key) {
        PdfNumber number = null;
        PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isNumber()) {
            number = (PdfNumber)orig;
        }
        return number;
    }

    public PdfName getAsName(PdfName key) {
        PdfName name = null;
        PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isName()) {
            name = (PdfName)orig;
        }
        return name;
    }

    public PdfBoolean getAsBoolean(PdfName key) {
        PdfBoolean bool = null;
        PdfObject orig = this.getDirectObject(key);
        if (orig != null && orig.isBoolean()) {
            bool = (PdfBoolean)orig;
        }
        return bool;
    }

    public PdfIndirectReference getAsIndirectObject(PdfName key) {
        PdfIndirectReference ref = null;
        PdfObject orig = this.get(key);
        if (orig != null && orig.isIndirect()) {
            ref = (PdfIndirectReference)orig;
        }
        return ref;
    }
}

