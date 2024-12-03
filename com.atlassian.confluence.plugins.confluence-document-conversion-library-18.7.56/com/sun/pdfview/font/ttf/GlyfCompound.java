/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.Glyf;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class GlyfCompound
extends Glyf {
    private static final int ARG_1_AND_2_ARE_WORDS = 1;
    private static final int ARGS_ARE_XY_VALUES = 2;
    private static final int ROUND_XY_TO_GRID = 4;
    private static final int WE_HAVE_A_SCALE = 8;
    private static final int MORE_COMPONENTS = 32;
    private static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
    private static final int WE_HAVE_A_TWO_BY_TWO = 128;
    private static final int WE_HAVE_INSTRUCTIONS = 256;
    private static final int USE_MY_METRICS = 512;
    private static final int OVERLAP_COMPOUND = 1024;
    private GlyfComponent[] components;
    private byte[] instructions;

    protected GlyfCompound() {
    }

    @Override
    public void setData(ByteBuffer data) {
        ArrayList<GlyfComponent> comps = new ArrayList<GlyfComponent>();
        GlyfComponent cur = null;
        boolean hasInstructions = false;
        do {
            cur = new GlyfComponent();
            cur.flags = data.getShort();
            cur.glyphIndex = data.getShort();
            if ((cur.flags & 1) != 0 && (cur.flags & 2) != 0) {
                cur.e = data.getShort();
                cur.f = data.getShort();
            } else if ((cur.flags & 1) == 0 && (cur.flags & 2) != 0) {
                cur.e = data.get();
                cur.f = data.get();
            } else if ((cur.flags & 1) != 0 && (cur.flags & 2) == 0) {
                cur.compoundPoint = data.getShort();
                cur.componentPoint = data.getShort();
            } else {
                cur.compoundPoint = data.get();
                cur.componentPoint = data.get();
            }
            if ((cur.flags & 8) != 0) {
                cur.d = cur.a = (float)data.getShort() / 16384.0f;
            } else if ((cur.flags & 0x40) != 0) {
                cur.a = (float)data.getShort() / 16384.0f;
                cur.d = (float)data.getShort() / 16384.0f;
            } else if ((cur.flags & 0x80) != 0) {
                cur.a = (float)data.getShort() / 16384.0f;
                cur.b = (float)data.getShort() / 16384.0f;
                cur.c = (float)data.getShort() / 16384.0f;
                cur.d = (float)data.getShort() / 16384.0f;
            }
            if ((cur.flags & 0x100) != 0) {
                hasInstructions = true;
            }
            comps.add(cur);
        } while ((cur.flags & 0x20) != 0);
        GlyfComponent[] componentArray = new GlyfComponent[comps.size()];
        comps.toArray(componentArray);
        this.setComponents(componentArray);
        byte[] instr = null;
        if (hasInstructions) {
            short numInstructions = data.getShort();
            instr = new byte[numInstructions];
            for (int i = 0; i < instr.length; ++i) {
                instr[i] = data.get();
            }
        } else {
            instr = new byte[]{};
        }
        this.setInstructions(instr);
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = super.getData();
        return buf;
    }

    @Override
    public short getLength() {
        short length = super.getLength();
        return length;
    }

    public int getNumComponents() {
        return this.components.length;
    }

    public short getFlag(int index) {
        return this.components[index].flags;
    }

    public short getGlyphIndex(int index) {
        return this.components[index].glyphIndex;
    }

    public double[] getTransform(int index) {
        GlyfComponent gc = this.components[index];
        float m = Math.max(Math.abs(gc.a), Math.abs(gc.b));
        if (Math.abs(Math.abs(gc.a) - Math.abs(gc.c)) < 0.0f) {
            m *= 2.0f;
        }
        float n = Math.max(Math.abs(gc.c), Math.abs(gc.d));
        if (Math.abs(Math.abs(gc.c) - Math.abs(gc.d)) < 0.0f) {
            n *= 2.0f;
        }
        float e = m * gc.e;
        float f = n * gc.f;
        return new double[]{gc.a, gc.b, gc.c, gc.d, e, f};
    }

    public int getCompoundPoint(int index) {
        return this.components[index].compoundPoint;
    }

    public int getComponentPoint(int index) {
        return this.components[index].componentPoint;
    }

    public boolean argsAreWords(int index) {
        return (this.getFlag(index) & 1) != 0;
    }

    public boolean argsAreXYValues(int index) {
        return (this.getFlag(index) & 2) != 0;
    }

    public boolean roundXYToGrid(int index) {
        return (this.getFlag(index) & 4) != 0;
    }

    public boolean hasAScale(int index) {
        return (this.getFlag(index) & 8) != 0;
    }

    protected boolean moreComponents(int index) {
        return (this.getFlag(index) & 0x20) != 0;
    }

    protected boolean hasXYScale(int index) {
        return (this.getFlag(index) & 0x40) != 0;
    }

    protected boolean hasTwoByTwo(int index) {
        return (this.getFlag(index) & 0x80) != 0;
    }

    protected boolean hasInstructions(int index) {
        return (this.getFlag(index) & 0x100) != 0;
    }

    public boolean useMetrics(int index) {
        return (this.getFlag(index) & 0x200) != 0;
    }

    public boolean overlapCompound(int index) {
        return (this.getFlag(index) & 0x400) != 0;
    }

    void setComponents(GlyfComponent[] components) {
        this.components = components;
    }

    public short getNumInstructions() {
        return (short)this.instructions.length;
    }

    public byte getInstruction(int index) {
        return this.instructions[index];
    }

    protected void setInstructions(byte[] instructions) {
        this.instructions = instructions;
    }

    class GlyfComponent {
        short flags;
        short glyphIndex;
        int compoundPoint;
        int componentPoint;
        float a = 1.0f;
        float b = 0.0f;
        float c = 0.0f;
        float d = 1.0f;
        float e = 0.0f;
        float f = 0.0f;

        GlyfComponent() {
        }
    }
}

