/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.util.LinkedList;
import org.apache.batik.parser.DefaultLengthListHandler;
import org.apache.batik.parser.ParseException;

public class LengthArrayProducer
extends DefaultLengthListHandler {
    protected LinkedList vs;
    protected float[] v;
    protected LinkedList us;
    protected short[] u;
    protected int index;
    protected int count;
    protected short currentUnit;

    public short[] getLengthTypeArray() {
        return this.u;
    }

    public float[] getLengthValueArray() {
        return this.v;
    }

    @Override
    public void startLengthList() throws ParseException {
        this.us = new LinkedList();
        this.u = new short[11];
        this.vs = new LinkedList();
        this.v = new float[11];
        this.count = 0;
        this.index = 0;
    }

    public void numberValue(float v) throws ParseException {
    }

    @Override
    public void lengthValue(float val) throws ParseException {
        if (this.index == this.v.length) {
            this.vs.add(this.v);
            this.v = new float[this.v.length * 2 + 1];
            this.us.add(this.u);
            this.u = new short[this.u.length * 2 + 1];
            this.index = 0;
        }
        this.v[this.index] = val;
    }

    @Override
    public void startLength() throws ParseException {
        this.currentUnit = 1;
    }

    @Override
    public void endLength() throws ParseException {
        this.u[this.index++] = this.currentUnit;
        ++this.count;
    }

    @Override
    public void em() throws ParseException {
        this.currentUnit = (short)3;
    }

    @Override
    public void ex() throws ParseException {
        this.currentUnit = (short)4;
    }

    @Override
    public void in() throws ParseException {
        this.currentUnit = (short)8;
    }

    @Override
    public void cm() throws ParseException {
        this.currentUnit = (short)6;
    }

    @Override
    public void mm() throws ParseException {
        this.currentUnit = (short)7;
    }

    @Override
    public void pc() throws ParseException {
        this.currentUnit = (short)10;
    }

    @Override
    public void pt() throws ParseException {
        this.currentUnit = (short)9;
    }

    @Override
    public void px() throws ParseException {
        this.currentUnit = (short)5;
    }

    @Override
    public void percentage() throws ParseException {
        this.currentUnit = (short)2;
    }

    @Override
    public void endLengthList() throws ParseException {
        float[] allValues = new float[this.count];
        int pos = 0;
        for (float[] a : this.vs) {
            System.arraycopy(a, 0, allValues, pos, a.length);
            pos += a.length;
        }
        System.arraycopy(this.v, 0, allValues, pos, this.index);
        this.vs.clear();
        this.v = allValues;
        short[] allUnits = new short[this.count];
        pos = 0;
        for (short[] a : this.us) {
            System.arraycopy(a, 0, allUnits, pos, a.length);
            pos += a.length;
        }
        System.arraycopy(this.u, 0, allUnits, pos, this.index);
        this.us.clear();
        this.u = allUnits;
    }
}

