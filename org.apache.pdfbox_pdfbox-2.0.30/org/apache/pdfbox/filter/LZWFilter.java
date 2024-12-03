/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.filter;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.Predictor;

public class LZWFilter
extends Filter {
    private static final Log LOG = LogFactory.getLog(LZWFilter.class);
    public static final long CLEAR_TABLE = 256L;
    public static final long EOD = 257L;
    private static final List<byte[]> INITIAL_CODE_TABLE = LZWFilter.createInitialCodeTable();

    @Override
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        COSDictionary decodeParams = this.getDecodeParams(parameters, index);
        boolean earlyChange = decodeParams.getInt(COSName.EARLY_CHANGE, 1) != 0;
        this.doLZWDecode(encoded, Predictor.wrapPredictor(decoded, decodeParams), earlyChange);
        return new DecodeResult(parameters);
    }

    private void doLZWDecode(InputStream encoded, OutputStream decoded, boolean earlyChange) throws IOException {
        List<Object> codeTable = new ArrayList();
        int chunk = 9;
        MemoryCacheImageInputStream in = new MemoryCacheImageInputStream(encoded);
        long prevCommand = -1L;
        try {
            long nextCommand;
            while ((nextCommand = in.readBits(chunk)) != 257L) {
                byte[] data;
                if (nextCommand == 256L) {
                    chunk = 9;
                    codeTable = LZWFilter.createCodeTable();
                    prevCommand = -1L;
                    continue;
                }
                if (nextCommand < (long)codeTable.size()) {
                    data = (byte[])codeTable.get((int)nextCommand);
                    byte firstByte = data[0];
                    decoded.write(data);
                    if (prevCommand != -1L) {
                        this.checkIndexBounds(codeTable, prevCommand, in);
                        data = (byte[])codeTable.get((int)prevCommand);
                        byte[] newData = Arrays.copyOf(data, data.length + 1);
                        newData[data.length] = firstByte;
                        codeTable.add(newData);
                    }
                } else {
                    this.checkIndexBounds(codeTable, prevCommand, in);
                    data = (byte[])codeTable.get((int)prevCommand);
                    byte[] newData = Arrays.copyOf(data, data.length + 1);
                    newData[data.length] = data[0];
                    decoded.write(newData);
                    codeTable.add(newData);
                }
                chunk = LZWFilter.calculateChunk(codeTable.size(), earlyChange);
                prevCommand = nextCommand;
            }
        }
        catch (EOFException ex) {
            LOG.warn((Object)"Premature EOF in LZW stream, EOD code missing");
        }
        decoded.flush();
    }

    private void checkIndexBounds(List<byte[]> codeTable, long index, MemoryCacheImageInputStream in) throws IOException {
        if (index < 0L) {
            throw new IOException("negative array index: " + index + " near offset " + in.getStreamPosition());
        }
        if (index >= (long)codeTable.size()) {
            throw new IOException("array index overflow: " + index + " >= " + codeTable.size() + " near offset " + in.getStreamPosition());
        }
    }

    @Override
    protected void encode(InputStream rawData, OutputStream encoded, COSDictionary parameters) throws IOException {
        int r;
        List<byte[]> codeTable = LZWFilter.createCodeTable();
        int chunk = 9;
        byte[] inputPattern = null;
        MemoryCacheImageOutputStream out = new MemoryCacheImageOutputStream(encoded);
        out.writeBits(256L, chunk);
        int foundCode = -1;
        while ((r = rawData.read()) != -1) {
            byte by = (byte)r;
            if (inputPattern == null) {
                inputPattern = new byte[]{by};
                foundCode = by & 0xFF;
                continue;
            }
            inputPattern = Arrays.copyOf(inputPattern, inputPattern.length + 1);
            inputPattern[inputPattern.length - 1] = by;
            int newFoundCode = LZWFilter.findPatternCode(codeTable, inputPattern);
            if (newFoundCode == -1) {
                chunk = LZWFilter.calculateChunk(codeTable.size() - 1, true);
                out.writeBits(foundCode, chunk);
                codeTable.add(inputPattern);
                if (codeTable.size() == 4096) {
                    out.writeBits(256L, chunk);
                    codeTable = LZWFilter.createCodeTable();
                }
                inputPattern = new byte[]{by};
                foundCode = by & 0xFF;
                continue;
            }
            foundCode = newFoundCode;
        }
        if (foundCode != -1) {
            chunk = LZWFilter.calculateChunk(codeTable.size() - 1, true);
            out.writeBits(foundCode, chunk);
        }
        chunk = LZWFilter.calculateChunk(codeTable.size(), true);
        out.writeBits(257L, chunk);
        out.writeBits(0L, 7);
        out.flush();
        out.close();
    }

    private static int findPatternCode(List<byte[]> codeTable, byte[] pattern) {
        if (pattern.length == 1) {
            return pattern[0];
        }
        for (int i = 257; i < codeTable.size(); ++i) {
            if (!Arrays.equals(codeTable.get(i), pattern)) continue;
            return i;
        }
        return -1;
    }

    private static List<byte[]> createCodeTable() {
        ArrayList<byte[]> codeTable = new ArrayList<byte[]>(4096);
        codeTable.addAll(INITIAL_CODE_TABLE);
        return codeTable;
    }

    private static List<byte[]> createInitialCodeTable() {
        ArrayList<byte[]> codeTable = new ArrayList<byte[]>(258);
        for (int i = 0; i < 256; ++i) {
            codeTable.add(new byte[]{(byte)(i & 0xFF)});
        }
        codeTable.add(null);
        codeTable.add(null);
        return codeTable;
    }

    private static int calculateChunk(int tabSize, boolean earlyChange) {
        int i = tabSize + (earlyChange ? 1 : 0);
        if (i >= 2048) {
            return 12;
        }
        if (i >= 1024) {
            return 11;
        }
        if (i >= 512) {
            return 10;
        }
        return 9;
    }
}

