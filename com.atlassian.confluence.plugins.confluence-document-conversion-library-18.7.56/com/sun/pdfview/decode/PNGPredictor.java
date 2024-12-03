/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.decode.Predictor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public class PNGPredictor
extends Predictor {
    public PNGPredictor() {
        super(1);
    }

    @Override
    public ByteBuffer unpredict(ByteBuffer imageData) throws IOException {
        ArrayList<byte[]> rows = new ArrayList<byte[]>();
        byte[] curLine = null;
        byte[] prevLine = null;
        int rowSize = this.getColumns() * this.getColors() * this.getBitsPerComponent();
        rowSize = (int)Math.ceil((double)rowSize / 8.0);
        while (imageData.remaining() >= rowSize + 1) {
            int algorithm = imageData.get() & 0xFF;
            curLine = new byte[rowSize];
            imageData.get(curLine);
            switch (algorithm) {
                case 0: {
                    break;
                }
                case 1: {
                    this.doSubLine(curLine);
                    break;
                }
                case 2: {
                    this.doUpLine(curLine, prevLine);
                    break;
                }
                case 3: {
                    this.doAverageLine(curLine, prevLine);
                    break;
                }
                case 4: {
                    this.doPaethLine(curLine, prevLine);
                }
            }
            rows.add(curLine);
            prevLine = curLine;
        }
        ByteBuffer outBuf = ByteBuffer.allocate(rows.size() * rowSize);
        Iterator i = rows.iterator();
        while (i.hasNext()) {
            outBuf.put((byte[])i.next());
        }
        outBuf.flip();
        return outBuf;
    }

    protected void doSubLine(byte[] curLine) {
        int sub = (int)Math.ceil((double)(this.getBitsPerComponent() * this.getColors()) / 8.0);
        for (int i = 0; i < curLine.length; ++i) {
            int prevIdx = i - sub;
            if (prevIdx < 0) continue;
            int n = i;
            curLine[n] = (byte)(curLine[n] + curLine[prevIdx]);
        }
    }

    protected void doUpLine(byte[] curLine, byte[] prevLine) {
        if (prevLine == null) {
            return;
        }
        for (int i = 0; i < curLine.length; ++i) {
            int n = i;
            curLine[n] = (byte)(curLine[n] + prevLine[i]);
        }
    }

    protected void doAverageLine(byte[] curLine, byte[] prevLine) {
        int sub = (int)Math.ceil((double)(this.getBitsPerComponent() * this.getColors()) / 8.0);
        int i = 0;
        while (i < curLine.length) {
            int raw = 0;
            int prior = 0;
            int prevIdx = i - sub;
            if (prevIdx >= 0) {
                raw = curLine[prevIdx] & 0xFF;
            }
            if (prevLine != null) {
                prior = prevLine[i] & 0xFF;
            }
            int n = i++;
            curLine[n] = (byte)(curLine[n] + (byte)Math.floor((raw + prior) / 2));
        }
    }

    protected void doPaethLine(byte[] curLine, byte[] prevLine) {
        int sub = (int)Math.ceil((double)(this.getBitsPerComponent() * this.getColors()) / 8.0);
        int i = 0;
        while (i < curLine.length) {
            int left = 0;
            int up = 0;
            int upLeft = 0;
            int prevIdx = i - sub;
            if (prevIdx >= 0) {
                left = curLine[prevIdx] & 0xFF;
            }
            if (prevLine != null) {
                up = prevLine[i] & 0xFF;
            }
            if (prevIdx > 0 && prevLine != null) {
                upLeft = prevLine[prevIdx] & 0xFF;
            }
            int n = i++;
            curLine[n] = (byte)(curLine[n] + (byte)this.paeth(left, up, upLeft));
        }
    }

    protected int paeth(int left, int up, int upLeft) {
        int p = left + up - upLeft;
        int pa = Math.abs(p - left);
        int pb = Math.abs(p - up);
        int pc = Math.abs(p - upLeft);
        if (pa <= pb && pa <= pc) {
            return left;
        }
        if (pb <= pc) {
            return up;
        }
        return upLeft;
    }
}

