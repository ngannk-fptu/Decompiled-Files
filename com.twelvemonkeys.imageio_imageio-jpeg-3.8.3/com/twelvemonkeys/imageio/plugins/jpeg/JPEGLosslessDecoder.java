/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Frame;
import com.twelvemonkeys.imageio.plugins.jpeg.HuffmanTable;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader;
import com.twelvemonkeys.imageio.plugins.jpeg.QuantizationTable;
import com.twelvemonkeys.imageio.plugins.jpeg.RestartInterval;
import com.twelvemonkeys.imageio.plugins.jpeg.Scan;
import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

final class JPEGLosslessDecoder {
    private final ImageInputStream input;
    private final JPEGImageReader listenerDelegate;
    private final Frame frame;
    private final List<HuffmanTable> huffTables;
    private final QuantizationTable quantTable;
    private Scan scan;
    private final int[][][] HuffTab = new int[4][2][12800];
    private final int[] IDCT_Source = new int[64];
    private final int[] nBlock = new int[10];
    private final int[][] acTab = new int[10][];
    private final int[][] dcTab = new int[10][];
    private final int[][] qTab = new int[10][];
    private boolean restarting;
    private int marker;
    private int markerIndex;
    private int numComp;
    private int restartInterval;
    private int selection;
    private int xDim;
    private int yDim;
    private int xLoc;
    private int yLoc;
    private int mask;
    private int[][] outputData;
    private static final int[] IDCT_P = new int[]{0, 5, 40, 16, 45, 2, 7, 42, 21, 56, 8, 61, 18, 47, 1, 4, 41, 23, 58, 13, 32, 24, 37, 10, 63, 17, 44, 3, 6, 43, 20, 57, 15, 34, 29, 48, 53, 26, 39, 9, 60, 19, 46, 22, 59, 12, 33, 31, 50, 55, 25, 36, 11, 62, 14, 35, 28, 49, 52, 27, 38, 30, 51, 54};
    private static final int RESTART_MARKER_BEGIN = 65488;
    private static final int RESTART_MARKER_END = 65495;
    private static final int MAX_HUFFMAN_SUBTREE = 50;
    private static final int MSB = Integer.MIN_VALUE;

    int getDimX() {
        return this.xDim;
    }

    int getDimY() {
        return this.yDim;
    }

    JPEGLosslessDecoder(List<Segment> list, ImageInputStream imageInputStream, JPEGImageReader jPEGImageReader) {
        Validate.notNull(list);
        this.frame = this.get(list, Frame.class);
        this.scan = this.get(list, Scan.class);
        QuantizationTable quantizationTable = this.get(list, QuantizationTable.class);
        this.quantTable = quantizationTable != null ? quantizationTable : new QuantizationTable();
        this.huffTables = this.getAll(list, HuffmanTable.class);
        RestartInterval restartInterval = this.get(list, RestartInterval.class);
        this.restartInterval = restartInterval != null ? restartInterval.interval : 0;
        this.input = imageInputStream;
        this.listenerDelegate = jPEGImageReader;
    }

    private <T> List<T> getAll(List<Segment> list, Class<T> clazz) {
        ArrayList<T> arrayList = new ArrayList<T>();
        for (Segment segment : list) {
            if (!clazz.isInstance(segment)) continue;
            arrayList.add(clazz.cast(segment));
        }
        return arrayList;
    }

    private <T> T get(List<Segment> list, Class<T> clazz) {
        for (Segment segment : list) {
            if (!clazz.isInstance(segment)) continue;
            return clazz.cast(segment);
        }
        return null;
    }

    int[][] decode() throws IOException {
        int n = 0;
        this.xLoc = 0;
        this.yLoc = 0;
        int n2 = this.input.readUnsignedShort();
        if (n2 != 65496) {
            throw new IIOException("Not a JPEG file, does not start with 0xFFD8");
        }
        for (HuffmanTable componentArray : this.huffTables) {
            componentArray.buildHuffTables(this.HuffTab);
        }
        this.quantTable.enhanceTables();
        n2 = this.input.readUnsignedShort();
        while (true) {
            Object object;
            int n3;
            if (n2 != 65498) {
                this.input.skipBytes(this.input.readUnsignedShort() - 2);
                n2 = this.input.readUnsignedShort();
                continue;
            }
            int n4 = this.frame.samplePrecision;
            this.mask = n4 == 8 ? 255 : 65535;
            Frame.Component[] componentArray = this.frame.components;
            this.scan = this.readScan();
            this.numComp = this.scan.components.length;
            this.selection = this.scan.spectralSelStart;
            Scan.Component[] componentArray2 = this.scan.components;
            for (n3 = 0; n3 < this.numComp; ++n3) {
                object = this.getComponentSpec(componentArray, componentArray2[n3].scanCompSel);
                this.qTab[n3] = this.quantTable.qTable(((Frame.Component)object).qtSel);
                this.nBlock[n3] = ((Frame.Component)object).vSub * ((Frame.Component)object).hSub;
                int n5 = componentArray2[n3].dcTabSel;
                int n6 = componentArray2[n3].acTabSel;
                if (this.useACForDC(n5)) {
                    this.processWarningOccured("Lossless JPEG with no DC tables encountered. Assuming only tables present to be DC tables.");
                    this.dcTab[n3] = this.HuffTab[n5][1];
                    this.acTab[n3] = this.HuffTab[n6][0];
                    continue;
                }
                this.dcTab[n3] = this.HuffTab[n5][0];
                this.acTab[n3] = this.HuffTab[n6][1];
            }
            this.xDim = this.frame.samplesPerLine;
            this.yDim = this.frame.lines;
            this.outputData = new int[this.numComp][];
            for (n3 = 0; n3 < this.numComp; ++n3) {
                this.outputData[n3] = new int[this.xDim * this.yDim];
            }
            int[] nArray = new int[this.numComp];
            for (int i = 0; i < this.numComp; ++i) {
                nArray[i] = 1 << n4 - 1;
            }
            object = new int[this.numComp];
            ++n;
            do {
                int[] nArray2 = new int[1];
                int[] nArray3 = new int[1];
                System.arraycopy(nArray, 0, object, 0, this.numComp);
                if (this.restartInterval == 0) {
                    n2 = this.decode((int[])object, nArray2, nArray3);
                    while (n2 == 0 && this.xLoc < this.xDim && this.yLoc < this.yDim) {
                        this.output((int[])object);
                        n2 = this.decode((int[])object, nArray2, nArray3);
                    }
                    break;
                }
                for (int i = 0; i < this.restartInterval; ++i) {
                    this.restarting = i == 0;
                    n2 = this.decode((int[])object, nArray2, nArray3);
                    this.output((int[])object);
                    if (n2 != 0) break;
                }
                if (n2 != 0) continue;
                if (this.markerIndex != 0) {
                    n2 = 0xFF00 | this.marker;
                    this.markerIndex = 0;
                    continue;
                }
                n2 = this.input.readUnsignedShort();
            } while (n2 >= 65488 && n2 <= 65495);
            if (n2 == 65500 && n == 1) {
                this.readNumber();
                n2 = this.input.readUnsignedShort();
            }
            if (n2 == 65497 || this.xLoc >= this.xDim || this.yLoc >= this.yDim || n != 0) break;
        }
        return this.outputData;
    }

    private void processWarningOccured(String string) {
        this.listenerDelegate.processWarningOccurred(string);
    }

    private boolean useACForDC(int n) {
        if (this.isLossless()) {
            for (HuffmanTable huffmanTable : this.huffTables) {
                if (huffmanTable.isPresent(n, 0) || !huffmanTable.isPresent(n, 1)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isLossless() {
        switch (this.frame.marker) {
            case 65475: 
            case 65479: 
            case 65483: 
            case 65487: {
                return true;
            }
        }
        return false;
    }

    private Frame.Component getComponentSpec(Frame.Component[] componentArray, int n) {
        for (Frame.Component component : componentArray) {
            if (component.id != n) continue;
            return component;
        }
        throw new IllegalArgumentException("No such component id: " + n);
    }

    private Scan readScan() throws IOException {
        int n = this.input.readUnsignedShort();
        return Scan.read(this.input, n);
    }

    private int decode(int[] nArray, int[] nArray2, int[] nArray3) throws IOException {
        if (this.numComp == 1) {
            return this.decodeSingle(nArray, nArray2, nArray3);
        }
        if (this.numComp == 3) {
            return this.decodeRGB(nArray, nArray2, nArray3);
        }
        return this.decodeAny(nArray, nArray2, nArray3);
    }

    private int decodeSingle(int[] nArray, int[] nArray2, int[] nArray3) throws IOException {
        if (this.restarting) {
            this.restarting = false;
            nArray[0] = 1 << this.frame.samplePrecision - 1;
        } else {
            int[] nArray4 = this.outputData[0];
            switch (this.selection) {
                case 2: {
                    nArray[0] = this.getPreviousY(nArray4);
                    break;
                }
                case 3: {
                    nArray[0] = this.getPreviousXY(nArray4);
                    break;
                }
                case 4: {
                    nArray[0] = this.getPreviousX(nArray4) + this.getPreviousY(nArray4) - this.getPreviousXY(nArray4);
                    break;
                }
                case 5: {
                    nArray[0] = this.getPreviousX(nArray4) + (this.getPreviousY(nArray4) - this.getPreviousXY(nArray4) >> 1);
                    break;
                }
                case 6: {
                    nArray[0] = this.getPreviousY(nArray4) + (this.getPreviousX(nArray4) - this.getPreviousXY(nArray4) >> 1);
                    break;
                }
                case 7: {
                    nArray[0] = (int)(((long)this.getPreviousX(nArray4) + (long)this.getPreviousY(nArray4)) / 2L);
                    break;
                }
                default: {
                    nArray[0] = this.getPreviousX(nArray4);
                }
            }
        }
        for (int i = 0; i < this.nBlock[0]; ++i) {
            int n = this.getHuffmanValue(this.dcTab[0], nArray2, nArray3);
            if (n >= 65280) {
                return n;
            }
            int n2 = this.getn(nArray, n, nArray2, nArray3);
            int n3 = n2 >> 8;
            if (n3 >= 65488 && n3 <= 65495) {
                return n3;
            }
            nArray[0] = nArray[0] + n2;
        }
        return 0;
    }

    private int decodeRGB(int[] nArray, int[] nArray2, int[] nArray3) throws IOException {
        int[] nArray4 = this.outputData[0];
        int[] nArray5 = this.outputData[1];
        int[] nArray6 = this.outputData[2];
        switch (this.selection) {
            case 2: {
                nArray[0] = this.getPreviousY(nArray4);
                nArray[1] = this.getPreviousY(nArray5);
                nArray[2] = this.getPreviousY(nArray6);
                break;
            }
            case 3: {
                nArray[0] = this.getPreviousXY(nArray4);
                nArray[1] = this.getPreviousXY(nArray5);
                nArray[2] = this.getPreviousXY(nArray6);
                break;
            }
            case 4: {
                nArray[0] = this.getPreviousX(nArray4) + this.getPreviousY(nArray4) - this.getPreviousXY(nArray4);
                nArray[1] = this.getPreviousX(nArray5) + this.getPreviousY(nArray5) - this.getPreviousXY(nArray5);
                nArray[2] = this.getPreviousX(nArray6) + this.getPreviousY(nArray6) - this.getPreviousXY(nArray6);
                break;
            }
            case 5: {
                nArray[0] = this.getPreviousX(nArray4) + (this.getPreviousY(nArray4) - this.getPreviousXY(nArray4) >> 1);
                nArray[1] = this.getPreviousX(nArray5) + (this.getPreviousY(nArray5) - this.getPreviousXY(nArray5) >> 1);
                nArray[2] = this.getPreviousX(nArray6) + (this.getPreviousY(nArray6) - this.getPreviousXY(nArray6) >> 1);
                break;
            }
            case 6: {
                nArray[0] = this.getPreviousY(nArray4) + (this.getPreviousX(nArray4) - this.getPreviousXY(nArray4) >> 1);
                nArray[1] = this.getPreviousY(nArray5) + (this.getPreviousX(nArray5) - this.getPreviousXY(nArray5) >> 1);
                nArray[2] = this.getPreviousY(nArray6) + (this.getPreviousX(nArray6) - this.getPreviousXY(nArray6) >> 1);
                break;
            }
            case 7: {
                nArray[0] = (int)(((long)this.getPreviousX(nArray4) + (long)this.getPreviousY(nArray4)) / 2L);
                nArray[1] = (int)(((long)this.getPreviousX(nArray5) + (long)this.getPreviousY(nArray5)) / 2L);
                nArray[2] = (int)(((long)this.getPreviousX(nArray6) + (long)this.getPreviousY(nArray6)) / 2L);
                break;
            }
            default: {
                nArray[0] = this.getPreviousX(nArray4);
                nArray[1] = this.getPreviousX(nArray5);
                nArray[2] = this.getPreviousX(nArray6);
            }
        }
        return this.decode0(nArray, nArray2, nArray3);
    }

    private int decodeAny(int[] nArray, int[] nArray2, int[] nArray3) throws IOException {
        for (int i = 0; i < this.outputData.length; ++i) {
            int n;
            int[] nArray4 = this.outputData[i];
            switch (this.selection) {
                case 2: {
                    n = this.getPreviousY(nArray4);
                    break;
                }
                case 3: {
                    n = this.getPreviousXY(nArray4);
                    break;
                }
                case 4: {
                    n = this.getPreviousX(nArray4) + this.getPreviousY(nArray4) - this.getPreviousXY(nArray4);
                    break;
                }
                case 5: {
                    n = this.getPreviousX(nArray4) + (this.getPreviousY(nArray4) - this.getPreviousXY(nArray4) >> 1);
                    break;
                }
                case 6: {
                    n = this.getPreviousY(nArray4) + (this.getPreviousX(nArray4) - this.getPreviousXY(nArray4) >> 1);
                    break;
                }
                case 7: {
                    n = (int)(((long)this.getPreviousX(nArray4) + (long)this.getPreviousY(nArray4)) / 2L);
                    break;
                }
                default: {
                    n = this.getPreviousX(nArray4);
                }
            }
            nArray[i] = n;
        }
        return this.decode0(nArray, nArray2, nArray3);
    }

    private int decode0(int[] nArray, int[] nArray2, int[] nArray3) throws IOException {
        for (int i = 0; i < this.numComp; ++i) {
            int[] nArray4 = this.qTab[i];
            int[] nArray5 = this.acTab[i];
            int[] nArray6 = this.dcTab[i];
            block1: for (int j = 0; j < this.nBlock[i]; ++j) {
                Arrays.fill(this.IDCT_Source, 0);
                int n = this.getHuffmanValue(nArray6, nArray2, nArray3);
                if (n >= 65280) {
                    return n;
                }
                nArray[i] = this.IDCT_Source[0] = nArray[i] + this.getn(nArray3, n, nArray2, nArray3);
                this.IDCT_Source[0] = this.IDCT_Source[0] * nArray4[0];
                for (int k = 1; k < 64; ++k) {
                    n = this.getHuffmanValue(nArray5, nArray2, nArray3);
                    if (n >= 65280) {
                        return n;
                    }
                    k += n >> 4;
                    if ((n & 0xF) == 0) {
                        if (n >> 4 != 0) continue;
                        continue block1;
                    }
                    this.IDCT_Source[JPEGLosslessDecoder.IDCT_P[k]] = this.getn(nArray3, n & 0xF, nArray2, nArray3) * nArray4[k];
                }
            }
        }
        return 0;
    }

    private int getHuffmanValue(int[] nArray, int[] nArray2, int[] nArray3) throws IOException {
        int n;
        if (nArray3[0] < 8) {
            nArray2[0] = nArray2[0] << 8;
            n = this.input.readUnsignedByte();
            if (n == 255) {
                this.marker = this.input.readUnsignedByte();
                if (this.marker != 0) {
                    this.markerIndex = 9;
                }
            }
            nArray2[0] = nArray2[0] | n;
        } else {
            nArray3[0] = nArray3[0] - 8;
        }
        int n2 = nArray[nArray2[0] >> nArray3[0]];
        if ((n2 & Integer.MIN_VALUE) != 0) {
            if (this.markerIndex != 0) {
                this.markerIndex = 0;
                return 0xFF00 | this.marker;
            }
            nArray2[0] = nArray2[0] & 65535 >> 16 - nArray3[0];
            nArray2[0] = nArray2[0] << 8;
            n = this.input.readUnsignedByte();
            if (n == 255) {
                this.marker = this.input.readUnsignedByte();
                if (this.marker != 0) {
                    this.markerIndex = 9;
                }
            }
            nArray2[0] = nArray2[0] | n;
            n2 = nArray[(n2 & 0xFF) * 256 + (nArray2[0] >> nArray3[0])];
            nArray3[0] = nArray3[0] + 8;
        }
        nArray3[0] = nArray3[0] + (8 - (n2 >> 8));
        if (nArray3[0] < 0) {
            throw new IIOException("index=" + nArray3[0] + " temp=" + nArray2[0] + " code=" + n2 + " in HuffmanValue()");
        }
        if (nArray3[0] < this.markerIndex) {
            this.markerIndex = 0;
            return 0xFF00 | this.marker;
        }
        nArray2[0] = nArray2[0] & 65535 >> 16 - nArray3[0];
        return n2 & 0xFF;
    }

    private int getn(int[] nArray, int n, int[] nArray2, int[] nArray3) throws IOException {
        int n2;
        if (n == 0) {
            return 0;
        }
        if (n == 16) {
            if (nArray[0] >= 0) {
                return Short.MIN_VALUE;
            }
            return 32768;
        }
        nArray3[0] = nArray3[0] - n;
        if (nArray3[0] >= 0) {
            if (nArray3[0] < this.markerIndex && !this.isLastPixel()) {
                this.markerIndex = 0;
                return (0xFF00 | this.marker) << 8;
            }
            n2 = nArray2[0] >> nArray3[0];
            nArray2[0] = nArray2[0] & 65535 >> 16 - nArray3[0];
        } else {
            nArray2[0] = nArray2[0] << 8;
            int n3 = this.input.readUnsignedByte();
            if (n3 == 255) {
                this.marker = this.input.readUnsignedByte();
                if (this.marker != 0) {
                    this.markerIndex = 9;
                }
            }
            nArray2[0] = nArray2[0] | n3;
            nArray3[0] = nArray3[0] + 8;
            if (nArray3[0] < 0) {
                if (this.markerIndex != 0) {
                    this.markerIndex = 0;
                    return (0xFF00 | this.marker) << 8;
                }
                nArray2[0] = nArray2[0] << 8;
                n3 = this.input.readUnsignedByte();
                if (n3 == 255) {
                    this.marker = this.input.readUnsignedByte();
                    if (this.marker != 0) {
                        this.markerIndex = 9;
                    }
                }
                nArray2[0] = nArray2[0] | n3;
                nArray3[0] = nArray3[0] + 8;
            }
            if (nArray3[0] < 0) {
                throw new IOException("index=" + nArray3[0] + " in getn()");
            }
            if (nArray3[0] < this.markerIndex) {
                this.markerIndex = 0;
                return (0xFF00 | this.marker) << 8;
            }
            n2 = nArray2[0] >> nArray3[0];
            nArray2[0] = nArray2[0] & 65535 >> 16 - nArray3[0];
        }
        if (n2 < 1 << n - 1) {
            n2 += (-1 << n) + 1;
        }
        return n2;
    }

    private int getPreviousX(int[] nArray) {
        if (this.xLoc > 0) {
            return nArray[this.yLoc * this.xDim + this.xLoc - 1];
        }
        if (this.yLoc > 0) {
            return this.getPreviousY(nArray);
        }
        return 1 << this.frame.samplePrecision - 1;
    }

    private int getPreviousXY(int[] nArray) {
        if (this.xLoc > 0 && this.yLoc > 0) {
            return nArray[(this.yLoc - 1) * this.xDim + this.xLoc - 1];
        }
        return this.getPreviousY(nArray);
    }

    private int getPreviousY(int[] nArray) {
        if (this.yLoc > 0) {
            return nArray[(this.yLoc - 1) * this.xDim + this.xLoc];
        }
        return this.getPreviousX(nArray);
    }

    private boolean isLastPixel() {
        return this.xLoc == this.xDim - 1 && this.yLoc == this.yDim - 1;
    }

    private void output(int[] nArray) {
        if (this.numComp == 1) {
            this.outputSingle(nArray);
        } else if (this.numComp == 3) {
            this.outputRGB(nArray);
        } else {
            this.outputAny(nArray);
        }
    }

    private void outputSingle(int[] nArray) {
        if (this.xLoc < this.xDim && this.yLoc < this.yDim) {
            this.outputData[0][this.yLoc * this.xDim + this.xLoc] = this.mask & nArray[0];
            ++this.xLoc;
            if (this.xLoc >= this.xDim) {
                ++this.yLoc;
                this.xLoc = 0;
            }
        }
    }

    private void outputRGB(int[] nArray) {
        if (this.xLoc < this.xDim && this.yLoc < this.yDim) {
            int n = this.yLoc * this.xDim + this.xLoc;
            this.outputData[0][n] = nArray[0];
            this.outputData[1][n] = nArray[1];
            this.outputData[2][n] = nArray[2];
            ++this.xLoc;
            if (this.xLoc >= this.xDim) {
                ++this.yLoc;
                this.xLoc = 0;
            }
        }
    }

    private void outputAny(int[] nArray) {
        if (this.xLoc < this.xDim && this.yLoc < this.yDim) {
            int n = this.yLoc * this.xDim + this.xLoc;
            for (int i = 0; i < this.outputData.length; ++i) {
                this.outputData[i][n] = nArray[i];
            }
            ++this.xLoc;
            if (this.xLoc >= this.xDim) {
                ++this.yLoc;
                this.xLoc = 0;
            }
        }
    }

    private int readNumber() throws IOException {
        int n = this.input.readUnsignedShort();
        if (n != 4) {
            throw new IOException("ERROR: Define number format throw new IOException [Ld!=4]");
        }
        return this.input.readUnsignedShort();
    }

    int getNumComponents() {
        return this.numComp;
    }

    int getPrecision() {
        return this.frame.samplePrecision;
    }
}

