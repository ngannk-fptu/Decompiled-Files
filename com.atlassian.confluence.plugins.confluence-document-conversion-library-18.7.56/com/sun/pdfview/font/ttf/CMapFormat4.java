/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.CMap;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class CMapFormat4
extends CMap {
    public SortedMap<Segment, Object> segments = Collections.synchronizedSortedMap(new TreeMap());

    protected CMapFormat4(short language) {
        super((short)4, language);
        char[] map = new char[]{'\u0000'};
        this.addSegment((short)-1, (short)-1, map);
    }

    public void addSegment(short startCode, short endCode, char[] map) {
        if (map.length != endCode - startCode + 1) {
            throw new IllegalArgumentException("Wrong number of entries in map");
        }
        Segment s = new Segment(startCode, endCode, true);
        this.segments.remove(s);
        this.segments.put(s, map);
    }

    public void addSegment(short startCode, short endCode, short idDelta) {
        Segment s = new Segment(startCode, endCode, false);
        this.segments.remove(s);
        this.segments.put(s, new Integer(idDelta));
    }

    public void removeSegment(short startCode, short endCode) {
        Segment s = new Segment(startCode, endCode, true);
        this.segments.remove(s);
    }

    @Override
    public short getLength() {
        short size = 16;
        size = (short)(size + this.segments.size() * 8);
        for (Segment s : this.segments.keySet()) {
            if (!s.hasMap) continue;
            char[] map = (char[])this.segments.get(s);
            size = (short)(size + map.length * 2);
        }
        return size;
    }

    @Override
    public byte map(byte src) {
        char c = this.map((char)src);
        if (c < '\uffffff80' || c > '\u007f') {
            return 0;
        }
        return (byte)c;
    }

    @Override
    public char map(char src) {
        for (Segment s : this.segments.keySet()) {
            if (s.endCode < src) continue;
            if (s.startCode <= src) {
                if (s.hasMap) {
                    char[] map = (char[])this.segments.get(s);
                    return map[src - s.startCode];
                }
                Integer idDelta = (Integer)this.segments.get(s);
                return (char)(src + idDelta);
            }
            return '\u0000';
        }
        return '\u0000';
    }

    @Override
    public char reverseMap(short glyphID) {
        for (Segment s : this.segments.keySet()) {
            if (s.hasMap) {
                char[] map = (char[])this.segments.get(s);
                for (int c = 0; c < map.length; ++c) {
                    if (map[c] != glyphID) continue;
                    return (char)(s.startCode + c);
                }
                continue;
            }
            Integer idDelta = (Integer)this.segments.get(s);
            int start = s.startCode + idDelta;
            int end = s.endCode + idDelta;
            if (glyphID < start || glyphID > end) continue;
            return (char)(glyphID - idDelta);
        }
        return '\u0000';
    }

    @Override
    public void setData(int length, ByteBuffer data) {
        int i;
        int segCount = data.getShort() / 2;
        short searchRange = data.getShort();
        short entrySelector = data.getShort();
        short rangeShift = data.getShort();
        short[] endCodes = new short[segCount];
        short[] startCodes = new short[segCount];
        short[] idDeltas = new short[segCount];
        short[] idRangeOffsets = new short[segCount];
        int glyphArrayPos = 16 + 8 * segCount;
        for (i = 0; i < segCount; ++i) {
            endCodes[i] = data.getShort();
        }
        data.getShort();
        for (i = 0; i < segCount; ++i) {
            startCodes[i] = data.getShort();
        }
        for (i = 0; i < segCount; ++i) {
            idDeltas[i] = data.getShort();
        }
        for (i = 0; i < segCount; ++i) {
            idRangeOffsets[i] = data.getShort();
            if (idRangeOffsets[i] <= 0) {
                this.addSegment(startCodes[i], endCodes[i], idDeltas[i]);
                continue;
            }
            int offset = data.position() - 2 + idRangeOffsets[i];
            int size = endCodes[i] - startCodes[i] + 1;
            char[] map = new char[size];
            data.mark();
            for (int c = 0; c < size; ++c) {
                data.position(offset + c * 2);
                map[c] = data.getChar();
            }
            data.reset();
            this.addSegment(startCodes[i], endCodes[i], map);
        }
    }

    @Override
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        buf.putShort(this.getFormat());
        buf.putShort(this.getLength());
        buf.putShort(this.getLanguage());
        buf.putShort((short)(this.getSegmentCount() * 2));
        buf.putShort(this.getSearchRange());
        buf.putShort(this.getEntrySelector());
        buf.putShort(this.getRangeShift());
        for (Segment s : this.segments.keySet()) {
            buf.putShort((short)s.endCode);
        }
        buf.putShort((short)0);
        for (Segment s : this.segments.keySet()) {
            buf.putShort((short)s.startCode);
        }
        for (Segment s : this.segments.keySet()) {
            if (!s.hasMap) {
                Integer idDelta = (Integer)this.segments.get(s);
                buf.putShort(idDelta.shortValue());
                continue;
            }
            buf.putShort((short)0);
        }
        int glyphArrayOffset = 16 + 8 * this.getSegmentCount();
        for (Segment s : this.segments.keySet()) {
            if (s.hasMap) {
                buf.putShort((short)(glyphArrayOffset - buf.position()));
                buf.mark();
                buf.position(glyphArrayOffset);
                char[] map = (char[])this.segments.get(s);
                for (int c = 0; c < map.length; ++c) {
                    buf.putChar(map[c]);
                }
                buf.reset();
                glyphArrayOffset += map.length * 2;
                continue;
            }
            buf.putShort((short)0);
        }
        buf.position(glyphArrayOffset);
        buf.flip();
        return buf;
    }

    public short getSegmentCount() {
        return (short)this.segments.size();
    }

    public short getSearchRange() {
        double pow = Math.floor(Math.log(this.getSegmentCount()) / Math.log(2.0));
        double pow2 = Math.pow(2.0, pow);
        return (short)(2.0 * pow2);
    }

    public short getEntrySelector() {
        int sr2 = this.getSearchRange() / 2;
        return (short)(Math.log(sr2) / Math.log(2.0));
    }

    public short getRangeShift() {
        return (short)(2 * this.getSegmentCount() - this.getSearchRange());
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "        ";
        buf.append(super.toString());
        buf.append(indent + "SegmentCount : " + this.getSegmentCount() + "\n");
        buf.append(indent + "SearchRange  : " + this.getSearchRange() + "\n");
        buf.append(indent + "EntrySelector: " + this.getEntrySelector() + "\n");
        buf.append(indent + "RangeShift   : " + this.getRangeShift() + "\n");
        for (Segment s : this.segments.keySet()) {
            buf.append(indent);
            buf.append("Segment: " + Integer.toHexString(s.startCode));
            buf.append("-" + Integer.toHexString(s.endCode) + " ");
            buf.append("hasMap: " + s.hasMap + " ");
            if (!s.hasMap) {
                buf.append("delta: " + this.segments.get(s));
            }
            buf.append("\n");
        }
        return buf.toString();
    }

    class Segment
    implements Comparable {
        int endCode;
        int startCode;
        boolean hasMap;

        public Segment(short startCode, short endCode, boolean hasMap) {
            this.endCode = 0xFFFF & endCode;
            this.startCode = 0xFFFF & startCode;
            this.hasMap = hasMap;
        }

        public boolean equals(Object o) {
            return this.compareTo(o) == 0;
        }

        public int compareTo(Object o) {
            if (!(o instanceof Segment)) {
                return -1;
            }
            Segment s = (Segment)o;
            if (s.endCode >= this.startCode && s.endCode <= this.endCode || s.startCode >= this.startCode && s.startCode <= this.endCode) {
                return 0;
            }
            if (this.endCode > s.endCode) {
                return 1;
            }
            if (this.endCode < s.endCode) {
                return -1;
            }
            return 0;
        }
    }
}

