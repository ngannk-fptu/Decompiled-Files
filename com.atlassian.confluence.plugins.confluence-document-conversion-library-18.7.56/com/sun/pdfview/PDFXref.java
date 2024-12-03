/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFObject;
import java.lang.ref.SoftReference;

public class PDFXref {
    private int id;
    private int generation;
    private boolean compressed;
    private SoftReference<PDFObject> reference = null;

    public PDFXref(int id, int gen) {
        this(id, gen, false);
    }

    public PDFXref(int id, int gen, boolean compressed) {
        this.id = id;
        this.generation = gen;
        this.compressed = compressed;
    }

    public PDFXref(byte[] line) {
        if (line == null) {
            this.id = -1;
            this.generation = -1;
        } else {
            this.id = Integer.parseInt(new String(line, 0, 10));
            this.generation = Integer.parseInt(new String(line, 11, 5));
        }
        this.compressed = false;
    }

    public int getFilePos() {
        return this.id;
    }

    public int getGeneration() {
        return this.generation;
    }

    public int getID() {
        return this.id;
    }

    public boolean getCompressed() {
        return this.compressed;
    }

    public PDFObject getObject() {
        if (this.reference != null) {
            return this.reference.get();
        }
        return null;
    }

    public void setObject(PDFObject obj) {
        this.reference = new SoftReference<PDFObject>(obj);
    }
}

