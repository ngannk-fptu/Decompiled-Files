/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.TrueTypeTable;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TrueTypeFont {
    private int type;
    private SortedMap<String, Object> tables;

    public TrueTypeFont(int type) {
        this.type = type;
        this.tables = Collections.synchronizedSortedMap(new TreeMap());
    }

    public static TrueTypeFont parseFont(byte[] orig) {
        ByteBuffer inBuf = ByteBuffer.wrap(orig);
        return TrueTypeFont.parseFont(inBuf);
    }

    public static TrueTypeFont parseFont(ByteBuffer inBuf) {
        int type = inBuf.getInt();
        short numTables = inBuf.getShort();
        short searchRange = inBuf.getShort();
        short entrySelector = inBuf.getShort();
        short rangeShift = inBuf.getShort();
        TrueTypeFont font = new TrueTypeFont(type);
        TrueTypeFont.parseDirectories(inBuf, numTables, font);
        return font;
    }

    public int getType() {
        return this.type;
    }

    public void addTable(String tagString, ByteBuffer data) {
        this.tables.put(tagString, data);
    }

    public void addTable(String tagString, TrueTypeTable table) {
        this.tables.put(tagString, table);
    }

    public TrueTypeTable getTable(String tagString) {
        Object tableObj = this.tables.get(tagString);
        TrueTypeTable table = null;
        if (tableObj instanceof ByteBuffer) {
            ByteBuffer data = (ByteBuffer)tableObj;
            table = TrueTypeTable.createTable(this, tagString, data);
            this.addTable(tagString, table);
        } else {
            table = (TrueTypeTable)tableObj;
        }
        return table;
    }

    public void removeTable(String tagString) {
        this.tables.remove(tagString);
    }

    public short getNumTables() {
        return (short)this.tables.size();
    }

    public short getSearchRange() {
        double pow2 = Math.floor(Math.log(this.getNumTables()) / Math.log(2.0));
        double maxPower = Math.pow(2.0, pow2);
        return (short)(16.0 * maxPower);
    }

    public short getEntrySelector() {
        double pow2 = Math.floor(Math.log(this.getNumTables()) / Math.log(2.0));
        double maxPower = Math.pow(2.0, pow2);
        return (short)(Math.log(maxPower) / Math.log(2.0));
    }

    public short getRangeShift() {
        double pow2 = Math.floor(Math.log(this.getNumTables()) / Math.log(2.0));
        double maxPower = Math.pow(2.0, pow2);
        return (short)(maxPower * 16.0 - (double)this.getSearchRange());
    }

    public byte[] writeFont() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        buf.putInt(this.getType());
        buf.putShort(this.getNumTables());
        buf.putShort(this.getSearchRange());
        buf.putShort(this.getEntrySelector());
        buf.putShort(this.getRangeShift());
        int curOffset = 12 + this.getNumTables() * 16;
        for (String tagString : this.tables.keySet()) {
            int tag = TrueTypeTable.stringToTag(tagString);
            ByteBuffer data = null;
            Object tableObj = this.tables.get(tagString);
            data = tableObj instanceof TrueTypeTable ? ((TrueTypeTable)tableObj).getData() : (ByteBuffer)tableObj;
            int dataLen = data.remaining();
            buf.putInt(tag);
            buf.putInt(TrueTypeFont.calculateChecksum(tagString, data));
            buf.putInt(curOffset);
            buf.putInt(dataLen);
            buf.mark();
            buf.position(curOffset);
            buf.put(data);
            data.flip();
            buf.reset();
            curOffset += dataLen;
            while (curOffset % 4 > 0) {
                ++curOffset;
            }
        }
        buf.position(curOffset);
        buf.flip();
        this.updateChecksumAdj(buf);
        return buf.array();
    }

    private static int calculateChecksum(String tagString, ByteBuffer data) {
        int sum = 0;
        data.mark();
        if (tagString.equals("head")) {
            data.putInt(8, 0);
        }
        int nlongs = (data.remaining() + 3) / 4;
        while (nlongs-- > 0) {
            if (data.remaining() > 3) {
                sum += data.getInt();
                continue;
            }
            byte b0 = data.remaining() > 0 ? data.get() : (byte)0;
            byte b1 = data.remaining() > 0 ? data.get() : (byte)0;
            byte b2 = data.remaining() > 0 ? data.get() : (byte)0;
            sum += (0xFF & b0) << 24 | (0xFF & b1) << 16 | (0xFF & b2) << 8;
        }
        data.reset();
        return sum;
    }

    private static void parseDirectories(ByteBuffer data, int numTables, TrueTypeFont ttf) {
        for (int i = 0; i < numTables; ++i) {
            int tag = data.getInt();
            String tagString = TrueTypeTable.tagToString(tag);
            int checksum = data.getInt();
            int offset = data.getInt();
            int length = data.getInt();
            data.mark();
            data.position(offset);
            ByteBuffer tableData = data.slice();
            tableData.limit(length);
            int calcChecksum = TrueTypeFont.calculateChecksum(tagString, tableData);
            if (calcChecksum == checksum) {
                ttf.addTable(tagString, tableData);
            } else {
                ttf.addTable(tagString, tableData);
            }
            data.reset();
        }
    }

    private int getLength() {
        int length = 12 + this.getNumTables() * 16;
        for (Object tableObj : this.tables.values()) {
            length = tableObj instanceof TrueTypeTable ? (length += ((TrueTypeTable)tableObj).getLength()) : (length += ((ByteBuffer)tableObj).remaining());
            if (length % 4 == 0) continue;
            length += 4 - length % 4;
        }
        return length;
    }

    private void updateChecksumAdj(ByteBuffer fontData) {
        int checksum = TrueTypeFont.calculateChecksum("", fontData);
        int checksumAdj = -1313820742 - checksum;
        int offset = 12 + this.getNumTables() * 16;
        for (String tagString : this.tables.keySet()) {
            if (tagString.equals("head")) {
                fontData.putInt(offset + 8, checksumAdj);
                return;
            }
            Object tableObj = this.tables.get(tagString);
            offset = tableObj instanceof TrueTypeTable ? (offset += ((TrueTypeTable)tableObj).getLength()) : (offset += ((ByteBuffer)tableObj).remaining());
            if (offset % 4 == 0) continue;
            offset += 4 - offset % 4;
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        System.out.println("Type         : " + this.getType());
        System.out.println("NumTables    : " + this.getNumTables());
        System.out.println("SearchRange  : " + this.getSearchRange());
        System.out.println("EntrySelector: " + this.getEntrySelector());
        System.out.println("RangeShift   : " + this.getRangeShift());
        for (Map.Entry<String, Object> e : this.tables.entrySet()) {
            TrueTypeTable table = null;
            table = e.getValue() instanceof ByteBuffer ? this.getTable(e.getKey()) : (TrueTypeTable)e.getValue();
            System.out.println(table);
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: ");
            System.out.println("    TrueTypeParser <filename>");
            System.exit(-1);
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(args[0], "r");
            int size = (int)raf.length();
            byte[] data = new byte[size];
            raf.readFully(data);
            TrueTypeFont ttp = TrueTypeFont.parseFont(data);
            System.out.println(ttp);
            ByteArrayInputStream fontStream = new ByteArrayInputStream(ttp.writeFont());
            Font font = Font.createFont(0, fontStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

