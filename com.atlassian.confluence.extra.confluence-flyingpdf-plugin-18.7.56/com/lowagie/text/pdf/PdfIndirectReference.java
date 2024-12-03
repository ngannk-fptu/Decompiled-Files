/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfObject;

public class PdfIndirectReference
extends PdfObject {
    protected int number;
    protected int generation = 0;

    protected PdfIndirectReference() {
        super(0);
    }

    PdfIndirectReference(int type, int number, int generation) {
        super(0, number + " " + generation + " R");
        this.number = number;
        this.generation = generation;
    }

    PdfIndirectReference(int type, int number) {
        this(type, number, 0);
    }

    public int getNumber() {
        return this.number;
    }

    public int getGeneration() {
        return this.generation;
    }

    @Override
    public String toString() {
        return new StringBuffer().append(this.number).append(" ").append(this.generation).append(" R").toString();
    }
}

