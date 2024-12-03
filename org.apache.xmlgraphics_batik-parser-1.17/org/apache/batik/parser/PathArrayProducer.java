/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.util.LinkedList;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;

public class PathArrayProducer
implements PathHandler {
    protected LinkedList ps;
    protected float[] p;
    protected LinkedList cs;
    protected short[] c;
    protected int cindex;
    protected int pindex;
    protected int ccount;
    protected int pcount;

    public short[] getPathCommands() {
        return this.c;
    }

    public float[] getPathParameters() {
        return this.p;
    }

    @Override
    public void startPath() throws ParseException {
        this.cs = new LinkedList();
        this.c = new short[11];
        this.ps = new LinkedList();
        this.p = new float[11];
        this.ccount = 0;
        this.pcount = 0;
        this.cindex = 0;
        this.pindex = 0;
    }

    @Override
    public void movetoRel(float x, float y) throws ParseException {
        this.command((short)3);
        this.param(x);
        this.param(y);
    }

    @Override
    public void movetoAbs(float x, float y) throws ParseException {
        this.command((short)2);
        this.param(x);
        this.param(y);
    }

    @Override
    public void closePath() throws ParseException {
        this.command((short)1);
    }

    @Override
    public void linetoRel(float x, float y) throws ParseException {
        this.command((short)5);
        this.param(x);
        this.param(y);
    }

    @Override
    public void linetoAbs(float x, float y) throws ParseException {
        this.command((short)4);
        this.param(x);
        this.param(y);
    }

    @Override
    public void linetoHorizontalRel(float x) throws ParseException {
        this.command((short)13);
        this.param(x);
    }

    @Override
    public void linetoHorizontalAbs(float x) throws ParseException {
        this.command((short)12);
        this.param(x);
    }

    @Override
    public void linetoVerticalRel(float y) throws ParseException {
        this.command((short)15);
        this.param(y);
    }

    @Override
    public void linetoVerticalAbs(float y) throws ParseException {
        this.command((short)14);
        this.param(y);
    }

    @Override
    public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
        this.command((short)7);
        this.param(x1);
        this.param(y1);
        this.param(x2);
        this.param(y2);
        this.param(x);
        this.param(y);
    }

    @Override
    public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
        this.command((short)6);
        this.param(x1);
        this.param(y1);
        this.param(x2);
        this.param(y2);
        this.param(x);
        this.param(y);
    }

    @Override
    public void curvetoCubicSmoothRel(float x2, float y2, float x, float y) throws ParseException {
        this.command((short)17);
        this.param(x2);
        this.param(y2);
        this.param(x);
        this.param(y);
    }

    @Override
    public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y) throws ParseException {
        this.command((short)16);
        this.param(x2);
        this.param(y2);
        this.param(x);
        this.param(y);
    }

    @Override
    public void curvetoQuadraticRel(float x1, float y1, float x, float y) throws ParseException {
        this.command((short)9);
        this.param(x1);
        this.param(y1);
        this.param(x);
        this.param(y);
    }

    @Override
    public void curvetoQuadraticAbs(float x1, float y1, float x, float y) throws ParseException {
        this.command((short)8);
        this.param(x1);
        this.param(y1);
        this.param(x);
        this.param(y);
    }

    @Override
    public void curvetoQuadraticSmoothRel(float x, float y) throws ParseException {
        this.command((short)19);
        this.param(x);
        this.param(y);
    }

    @Override
    public void curvetoQuadraticSmoothAbs(float x, float y) throws ParseException {
        this.command((short)18);
        this.param(x);
        this.param(y);
    }

    @Override
    public void arcRel(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
        this.command((short)11);
        this.param(rx);
        this.param(ry);
        this.param(xAxisRotation);
        this.param(largeArcFlag ? 1.0f : 0.0f);
        this.param(sweepFlag ? 1.0f : 0.0f);
        this.param(x);
        this.param(y);
    }

    @Override
    public void arcAbs(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
        this.command((short)10);
        this.param(rx);
        this.param(ry);
        this.param(xAxisRotation);
        this.param(largeArcFlag ? 1.0f : 0.0f);
        this.param(sweepFlag ? 1.0f : 0.0f);
        this.param(x);
        this.param(y);
    }

    protected void command(short val) throws ParseException {
        if (this.cindex == this.c.length) {
            this.cs.add(this.c);
            this.c = new short[this.c.length * 2 + 1];
            this.cindex = 0;
        }
        this.c[this.cindex++] = val;
        ++this.ccount;
    }

    protected void param(float val) throws ParseException {
        if (this.pindex == this.p.length) {
            this.ps.add(this.p);
            this.p = new float[this.p.length * 2 + 1];
            this.pindex = 0;
        }
        this.p[this.pindex++] = val;
        ++this.pcount;
    }

    @Override
    public void endPath() throws ParseException {
        short[] allCommands = new short[this.ccount];
        int pos = 0;
        for (short[] a : this.cs) {
            System.arraycopy(a, 0, allCommands, pos, a.length);
            pos += a.length;
        }
        System.arraycopy(this.c, 0, allCommands, pos, this.cindex);
        this.cs.clear();
        this.c = allCommands;
        float[] allParams = new float[this.pcount];
        pos = 0;
        for (float[] a : this.ps) {
            System.arraycopy(a, 0, allParams, pos, a.length);
            pos += a.length;
        }
        System.arraycopy(this.p, 0, allParams, pos, this.pindex);
        this.ps.clear();
        this.p = allParams;
    }
}

