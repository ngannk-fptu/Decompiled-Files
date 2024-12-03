/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.Glyf;
import java.nio.ByteBuffer;

public class GlyfSimple
extends Glyf {
    private short[] contourEndPts;
    private byte[] instructions;
    private byte[] flags;
    private short[] xCoords;
    private short[] yCoords;

    protected GlyfSimple() {
    }

    @Override
    public void setData(ByteBuffer data) {
        short[] contourEndPts = new short[this.getNumContours()];
        for (int i = 0; i < contourEndPts.length; ++i) {
            contourEndPts[i] = data.getShort();
        }
        this.setContourEndPoints(contourEndPts);
        int numPoints = this.getContourEndPoint(this.getNumContours() - 1) + 1;
        short numInstructions = data.getShort();
        byte[] instructions = new byte[numInstructions];
        for (int i = 0; i < instructions.length; ++i) {
            instructions[i] = data.get();
        }
        this.setInstructions(instructions);
        byte[] flags = new byte[numPoints];
        for (int i = 0; i < flags.length; ++i) {
            flags[i] = data.get();
            if ((flags[i] & 8) == 0) continue;
            byte f = flags[i];
            int n = data.get() & 0xFF;
            for (int c = 0; c < n; ++c) {
                flags[++i] = f;
            }
        }
        this.setFlags(flags);
        short[] xCoords = new short[numPoints];
        for (int i = 0; i < xCoords.length; ++i) {
            if (i > 0) {
                xCoords[i] = xCoords[i - 1];
            }
            if (this.xIsByte(i)) {
                int val = data.get() & 0xFF;
                if (!this.xIsSame(i)) {
                    val = -val;
                }
                int n = i;
                xCoords[n] = (short)(xCoords[n] + val);
                continue;
            }
            if (this.xIsSame(i)) continue;
            int n = i;
            xCoords[n] = (short)(xCoords[n] + data.getShort());
        }
        this.setXCoords(xCoords);
        short[] yCoords = new short[numPoints];
        for (int i = 0; i < yCoords.length; ++i) {
            if (i > 0) {
                yCoords[i] = yCoords[i - 1];
            }
            if (this.yIsByte(i)) {
                int val = data.get() & 0xFF;
                if (!this.yIsSame(i)) {
                    val = -val;
                }
                int n = i;
                yCoords[n] = (short)(yCoords[n] + val);
                continue;
            }
            if (this.yIsSame(i)) continue;
            int n = i;
            yCoords[n] = (short)(yCoords[n] + data.getShort());
        }
        this.setYCoords(yCoords);
    }

    @Override
    public ByteBuffer getData() {
        int i;
        ByteBuffer buf = super.getData();
        for (i = 0; i < this.getNumContours(); ++i) {
            buf.putShort(this.getContourEndPoint(i));
        }
        buf.putShort(this.getNumInstructions());
        for (i = 0; i < this.getNumInstructions(); ++i) {
            buf.put(this.getInstruction(i));
        }
        for (i = 0; i < this.getNumPoints(); ++i) {
            byte r = 0;
            while (i > 0 && this.getFlag(i) == this.getFlag(i - 1)) {
                r = (byte)(r + 1);
                ++i;
            }
            if (r > 0) {
                buf.put(r);
                continue;
            }
            buf.put(this.getFlag(i));
        }
        for (i = 0; i < this.getNumPoints(); ++i) {
            if (this.xIsByte(i)) {
                buf.put((byte)this.getXCoord(i));
                continue;
            }
            if (this.xIsSame(i)) continue;
            buf.putShort(this.getXCoord(i));
        }
        for (i = 0; i < this.getNumPoints(); ++i) {
            if (this.yIsByte(i)) {
                buf.put((byte)this.getYCoord(i));
                continue;
            }
            if (this.yIsSame(i)) continue;
            buf.putShort(this.getYCoord(i));
        }
        return buf;
    }

    @Override
    public short getLength() {
        int i;
        short length = super.getLength();
        length = (short)(length + this.getNumContours() * 2);
        length = (short)(length + (2 + this.getNumInstructions()));
        for (i = 0; i < this.getNumPoints(); ++i) {
            while (i > 0 && this.getFlag(i) == this.getFlag(i - 1)) {
            }
            length = (short)(length + 1);
        }
        for (i = 0; i < this.getNumPoints(); ++i) {
            if (this.xIsByte(i)) {
                length = (short)(length + 1);
            } else if (!this.xIsSame(i)) {
                length = (short)(length + 2);
            }
            if (this.yIsByte(i)) {
                length = (short)(length + 1);
                continue;
            }
            if (this.yIsSame(i)) continue;
            length = (short)(length + 2);
        }
        return length;
    }

    public short getContourEndPoint(int index) {
        return this.contourEndPts[index];
    }

    protected void setContourEndPoints(short[] contourEndPts) {
        this.contourEndPts = contourEndPts;
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

    public short getNumPoints() {
        return (short)this.flags.length;
    }

    public byte getFlag(int pointIndex) {
        return this.flags[pointIndex];
    }

    public boolean onCurve(int pointIndex) {
        return (this.getFlag(pointIndex) & 1) != 0;
    }

    protected boolean xIsByte(int pointIndex) {
        return (this.getFlag(pointIndex) & 2) != 0;
    }

    protected boolean yIsByte(int pointIndex) {
        return (this.getFlag(pointIndex) & 4) != 0;
    }

    protected boolean repeat(int pointIndex) {
        return (this.getFlag(pointIndex) & 8) != 0;
    }

    protected boolean xIsSame(int pointIndex) {
        return (this.getFlag(pointIndex) & 0x10) != 0;
    }

    protected boolean yIsSame(int pointIndex) {
        return (this.getFlag(pointIndex) & 0x20) != 0;
    }

    protected void setFlags(byte[] flags) {
        this.flags = flags;
    }

    public short getXCoord(int pointIndex) {
        return this.xCoords[pointIndex];
    }

    protected void setXCoords(short[] xCoords) {
        this.xCoords = xCoords;
    }

    public short getYCoord(int pointIndex) {
        return this.yCoords[pointIndex];
    }

    protected void setYCoords(short[] yCoords) {
        this.yCoords = yCoords;
    }
}

