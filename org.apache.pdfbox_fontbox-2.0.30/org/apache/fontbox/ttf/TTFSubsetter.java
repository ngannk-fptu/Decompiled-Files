/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.MaximumProfileTable;
import org.apache.fontbox.ttf.NameRecord;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.ttf.WGL4Names;

public final class TTFSubsetter {
    private static final Log LOG = LogFactory.getLog(TTFSubsetter.class);
    private static final byte[] PAD_BUF = new byte[]{0, 0, 0};
    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
    private final TrueTypeFont ttf;
    private final CmapLookup unicodeCmap;
    private final SortedMap<Integer, Integer> uniToGID;
    private final List<String> keepTables;
    private final SortedSet<Integer> glyphIds;
    private String prefix;
    private boolean hasAddedCompoundReferences;

    public TTFSubsetter(TrueTypeFont ttf) throws IOException {
        this(ttf, null);
    }

    public TTFSubsetter(TrueTypeFont ttf, List<String> tables) throws IOException {
        this.ttf = ttf;
        this.keepTables = tables;
        this.uniToGID = new TreeMap<Integer, Integer>();
        this.glyphIds = new TreeSet<Integer>();
        this.unicodeCmap = ttf.getUnicodeCmapLookup();
        this.glyphIds.add(0);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void add(int unicode) {
        int gid = this.unicodeCmap.getGlyphId(unicode);
        if (gid != 0) {
            this.uniToGID.put(unicode, gid);
            this.glyphIds.add(gid);
        }
    }

    public void addAll(Set<Integer> unicodeSet) {
        for (int unicode : unicodeSet) {
            this.add(unicode);
        }
    }

    public Map<Integer, Integer> getGIDMap() throws IOException {
        this.addCompoundReferences();
        HashMap<Integer, Integer> newToOld = new HashMap<Integer, Integer>();
        int newGID = 0;
        Iterator iterator = this.glyphIds.iterator();
        while (iterator.hasNext()) {
            int oldGID = (Integer)iterator.next();
            newToOld.put(newGID, oldGID);
            ++newGID;
        }
        return newToOld;
    }

    private long writeFileHeader(DataOutputStream out, int nTables) throws IOException {
        out.writeInt(65536);
        out.writeShort(nTables);
        int mask = Integer.highestOneBit(nTables);
        int searchRange = mask * 16;
        out.writeShort(searchRange);
        int entrySelector = this.log2(mask);
        out.writeShort(entrySelector);
        int last = 16 * nTables - searchRange;
        out.writeShort(last);
        return 65536L + this.toUInt32(nTables, searchRange) + this.toUInt32(entrySelector, last);
    }

    private long writeTableHeader(DataOutputStream out, String tag, long offset, byte[] bytes) throws IOException {
        long checksum = 0L;
        int n = bytes.length;
        for (int nup = 0; nup < n; ++nup) {
            checksum += ((long)bytes[nup] & 0xFFL) << 24 - nup % 4 * 8;
        }
        byte[] tagbytes = tag.getBytes("US-ASCII");
        out.write(tagbytes, 0, 4);
        out.writeInt((int)(checksum &= 0xFFFFFFFFL));
        out.writeInt((int)offset);
        out.writeInt(bytes.length);
        return this.toUInt32(tagbytes) + checksum + checksum + offset + (long)bytes.length;
    }

    private void writeTableBody(OutputStream os, byte[] bytes) throws IOException {
        int n = bytes.length;
        os.write(bytes);
        if (n % 4 != 0) {
            os.write(PAD_BUF, 0, 4 - n % 4);
        }
    }

    private byte[] buildHeadTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        HeaderTable h = this.ttf.getHeader();
        this.writeFixed(out, h.getVersion());
        this.writeFixed(out, h.getFontRevision());
        this.writeUint32(out, 0L);
        this.writeUint32(out, h.getMagicNumber());
        this.writeUint16(out, h.getFlags());
        this.writeUint16(out, h.getUnitsPerEm());
        this.writeLongDateTime(out, h.getCreated());
        this.writeLongDateTime(out, h.getModified());
        this.writeSInt16(out, h.getXMin());
        this.writeSInt16(out, h.getYMin());
        this.writeSInt16(out, h.getXMax());
        this.writeSInt16(out, h.getYMax());
        this.writeUint16(out, h.getMacStyle());
        this.writeUint16(out, h.getLowestRecPPEM());
        this.writeSInt16(out, h.getFontDirectionHint());
        this.writeSInt16(out, (short)1);
        this.writeSInt16(out, h.getGlyphDataFormat());
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildHheaTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        HorizontalHeaderTable h = this.ttf.getHorizontalHeader();
        this.writeFixed(out, h.getVersion());
        this.writeSInt16(out, h.getAscender());
        this.writeSInt16(out, h.getDescender());
        this.writeSInt16(out, h.getLineGap());
        this.writeUint16(out, h.getAdvanceWidthMax());
        this.writeSInt16(out, h.getMinLeftSideBearing());
        this.writeSInt16(out, h.getMinRightSideBearing());
        this.writeSInt16(out, h.getXMaxExtent());
        this.writeSInt16(out, h.getCaretSlopeRise());
        this.writeSInt16(out, h.getCaretSlopeRun());
        this.writeSInt16(out, h.getReserved1());
        this.writeSInt16(out, h.getReserved2());
        this.writeSInt16(out, h.getReserved3());
        this.writeSInt16(out, h.getReserved4());
        this.writeSInt16(out, h.getReserved5());
        this.writeSInt16(out, h.getMetricDataFormat());
        int hmetrics = this.glyphIds.subSet(0, h.getNumberOfHMetrics()).size();
        if (this.glyphIds.last() >= h.getNumberOfHMetrics() && !this.glyphIds.contains(h.getNumberOfHMetrics() - 1)) {
            ++hmetrics;
        }
        this.writeUint16(out, hmetrics);
        out.flush();
        return bos.toByteArray();
    }

    private boolean shouldCopyNameRecord(NameRecord nr) {
        return nr.getPlatformId() == 3 && nr.getPlatformEncodingId() == 1 && nr.getLanguageId() == 1033 && nr.getNameId() >= 0 && nr.getNameId() < 7;
    }

    /*
     * WARNING - void declaration
     */
    private byte[] buildNameTable() throws IOException {
        void var9_14;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        NamingTable name = this.ttf.getNaming();
        if (name == null || this.keepTables != null && !this.keepTables.contains("name")) {
            return null;
        }
        List<NameRecord> nameRecords = name.getNameRecords();
        int numRecords = 0;
        for (NameRecord record : nameRecords) {
            if (!this.shouldCopyNameRecord(record)) continue;
            ++numRecords;
        }
        this.writeUint16(out, 0);
        this.writeUint16(out, numRecords);
        this.writeUint16(out, 6 + 12 * numRecords);
        if (numRecords == 0) {
            return null;
        }
        byte[][] names = new byte[numRecords][];
        int j = 0;
        for (NameRecord nameRecord : nameRecords) {
            if (!this.shouldCopyNameRecord(nameRecord)) continue;
            int platform = nameRecord.getPlatformId();
            int encoding = nameRecord.getPlatformEncodingId();
            String charset = "ISO-8859-1";
            if (platform == 3 && encoding == 1) {
                charset = "UTF-16BE";
            } else if (platform == 2) {
                if (encoding == 0) {
                    charset = "US-ASCII";
                } else if (encoding == 1) {
                    charset = "UTF16-BE";
                } else if (encoding == 2) {
                    charset = "ISO-8859-1";
                }
            }
            String value = nameRecord.getString();
            if (nameRecord.getNameId() == 6 && this.prefix != null) {
                value = this.prefix + value;
            }
            names[j] = value.getBytes(charset);
            ++j;
        }
        int offset = 0;
        j = 0;
        for (NameRecord nr : nameRecords) {
            if (!this.shouldCopyNameRecord(nr)) continue;
            this.writeUint16(out, nr.getPlatformId());
            this.writeUint16(out, nr.getPlatformEncodingId());
            this.writeUint16(out, nr.getLanguageId());
            this.writeUint16(out, nr.getNameId());
            this.writeUint16(out, names[j].length);
            this.writeUint16(out, offset);
            offset += names[j].length;
            ++j;
        }
        boolean bl = false;
        while (var9_14 < numRecords) {
            out.write(names[var9_14]);
            ++var9_14;
        }
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildMaxpTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        MaximumProfileTable p = this.ttf.getMaximumProfile();
        this.writeFixed(out, 1.0);
        this.writeUint16(out, this.glyphIds.size());
        if (p.getVersion() >= 1.0f) {
            this.writeUint16(out, p.getMaxPoints());
            this.writeUint16(out, p.getMaxContours());
            this.writeUint16(out, p.getMaxCompositePoints());
            this.writeUint16(out, p.getMaxCompositeContours());
            this.writeUint16(out, p.getMaxZones());
            this.writeUint16(out, p.getMaxTwilightPoints());
            this.writeUint16(out, p.getMaxStorage());
            this.writeUint16(out, p.getMaxFunctionDefs());
            this.writeUint16(out, p.getMaxInstructionDefs());
            this.writeUint16(out, p.getMaxStackElements());
            this.writeUint16(out, p.getMaxSizeOfInstructions());
            this.writeUint16(out, p.getMaxComponentElements());
            this.writeUint16(out, p.getMaxComponentDepth());
        }
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildOS2Table() throws IOException {
        OS2WindowsMetricsTable os2 = this.ttf.getOS2Windows();
        if (os2 == null || this.uniToGID.isEmpty() || this.keepTables != null && !this.keepTables.contains("OS/2")) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        this.writeUint16(out, os2.getVersion());
        this.writeSInt16(out, os2.getAverageCharWidth());
        this.writeUint16(out, os2.getWeightClass());
        this.writeUint16(out, os2.getWidthClass());
        this.writeSInt16(out, os2.getFsType());
        this.writeSInt16(out, os2.getSubscriptXSize());
        this.writeSInt16(out, os2.getSubscriptYSize());
        this.writeSInt16(out, os2.getSubscriptXOffset());
        this.writeSInt16(out, os2.getSubscriptYOffset());
        this.writeSInt16(out, os2.getSuperscriptXSize());
        this.writeSInt16(out, os2.getSuperscriptYSize());
        this.writeSInt16(out, os2.getSuperscriptXOffset());
        this.writeSInt16(out, os2.getSuperscriptYOffset());
        this.writeSInt16(out, os2.getStrikeoutSize());
        this.writeSInt16(out, os2.getStrikeoutPosition());
        this.writeSInt16(out, (short)os2.getFamilyClass());
        out.write(os2.getPanose());
        this.writeUint32(out, 0L);
        this.writeUint32(out, 0L);
        this.writeUint32(out, 0L);
        this.writeUint32(out, 0L);
        out.write(os2.getAchVendId().getBytes("US-ASCII"));
        this.writeUint16(out, os2.getFsSelection());
        this.writeUint16(out, this.uniToGID.firstKey());
        this.writeUint16(out, this.uniToGID.lastKey());
        this.writeUint16(out, os2.getTypoAscender());
        this.writeUint16(out, os2.getTypoDescender());
        this.writeUint16(out, os2.getTypoLineGap());
        this.writeUint16(out, os2.getWinAscent());
        this.writeUint16(out, os2.getWinDescent());
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildLocaTable(long[] newOffsets) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        for (long offset : newOffsets) {
            this.writeUint32(out, offset);
        }
        out.flush();
        return bos.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addCompoundReferences() throws IOException {
        boolean hasNested;
        if (this.hasAddedCompoundReferences) {
            return;
        }
        this.hasAddedCompoundReferences = true;
        GlyphTable g = this.ttf.getGlyph();
        long[] offsets = this.ttf.getIndexToLocation().getOffsets();
        do {
            InputStream is = this.ttf.getOriginalData();
            TreeSet<Integer> glyphIdsToAdd = null;
            try {
                is.skip(g.getOffset());
                long lastOff = 0L;
                for (Integer glyphId : this.glyphIds) {
                    long offset = offsets[glyphId];
                    long len = offsets[glyphId + 1] - offset;
                    is.skip(offset - lastOff);
                    byte[] buf = new byte[(int)len];
                    is.read(buf);
                    if (buf.length >= 2 && buf[0] == -1 && buf[1] == -1) {
                        int flags;
                        int off = 10;
                        do {
                            int ogid;
                            flags = (buf[off] & 0xFF) << 8 | buf[off + 1] & 0xFF;
                            if (!this.glyphIds.contains(ogid = (buf[off += 2] & 0xFF) << 8 | buf[off + 1] & 0xFF)) {
                                if (glyphIdsToAdd == null) {
                                    glyphIdsToAdd = new TreeSet<Integer>();
                                }
                                glyphIdsToAdd.add(ogid);
                            }
                            off += 2;
                            off = (flags & 1) != 0 ? (off += 4) : (off += 2);
                            if ((flags & 0x80) != 0) {
                                off += 8;
                                continue;
                            }
                            if ((flags & 0x40) != 0) {
                                off += 4;
                                continue;
                            }
                            if ((flags & 8) == 0) continue;
                            off += 2;
                        } while ((flags & 0x20) != 0);
                    }
                    lastOff = offsets[glyphId + 1];
                }
            }
            finally {
                is.close();
            }
            boolean bl = hasNested = glyphIdsToAdd != null;
            if (!hasNested) continue;
            this.glyphIds.addAll(glyphIdsToAdd);
        } while (hasNested);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] buildGlyfTable(long[] newOffsets) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GlyphTable g = this.ttf.getGlyph();
        long[] offsets = this.ttf.getIndexToLocation().getOffsets();
        InputStream is = this.ttf.getOriginalData();
        try {
            is.skip(g.getOffset());
            long prevEnd = 0L;
            long newOffset = 0L;
            int newGid = 0;
            for (Integer gid : this.glyphIds) {
                long offset = offsets[gid];
                long length = offsets[gid + 1] - offset;
                newOffsets[newGid++] = newOffset;
                is.skip(offset - prevEnd);
                byte[] buf = new byte[(int)length];
                is.read(buf);
                if (buf.length >= 2 && buf[0] == -1 && buf[1] == -1) {
                    int flags;
                    int off = 10;
                    do {
                        flags = (buf[off] & 0xFF) << 8 | buf[off + 1] & 0xFF;
                        int componentGid = (buf[off += 2] & 0xFF) << 8 | buf[off + 1] & 0xFF;
                        this.glyphIds.add(componentGid);
                        int newComponentGid = this.getNewGlyphId(componentGid);
                        buf[off] = (byte)(newComponentGid >>> 8);
                        buf[off + 1] = (byte)newComponentGid;
                        off += 2;
                        off = (flags & 1) != 0 ? (off += 4) : (off += 2);
                        if ((flags & 0x80) != 0) {
                            off += 8;
                            continue;
                        }
                        if ((flags & 0x40) != 0) {
                            off += 4;
                            continue;
                        }
                        if ((flags & 8) == 0) continue;
                        off += 2;
                    } while ((flags & 0x20) != 0);
                    if ((flags & 0x100) == 256) {
                        int numInstr = (buf[off] & 0xFF) << 8 | buf[off + 1] & 0xFF;
                        off += 2;
                        off += numInstr;
                    }
                    bos.write(buf, 0, off);
                    newOffset += (long)off;
                } else if (buf.length > 0) {
                    bos.write(buf, 0, buf.length);
                    newOffset += (long)buf.length;
                }
                if (newOffset % 4L != 0L) {
                    int len = 4 - (int)(newOffset % 4L);
                    bos.write(PAD_BUF, 0, len);
                    newOffset += (long)len;
                }
                prevEnd = offset + length;
            }
            newOffsets[newGid++] = newOffset;
        }
        finally {
            is.close();
        }
        return bos.toByteArray();
    }

    private int getNewGlyphId(Integer oldGid) {
        return this.glyphIds.headSet(oldGid).size();
    }

    private byte[] buildCmapTable() throws IOException {
        int i;
        Map.Entry<Integer, Integer> lastChar;
        if (this.ttf.getCmap() == null || this.uniToGID.isEmpty() || this.keepTables != null && !this.keepTables.contains("cmap")) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        this.writeUint16(out, 0);
        this.writeUint16(out, 1);
        this.writeUint16(out, 3);
        this.writeUint16(out, 1);
        this.writeUint32(out, 12L);
        Iterator<Map.Entry<Integer, Integer>> it = this.uniToGID.entrySet().iterator();
        Map.Entry<Integer, Integer> prevChar = lastChar = it.next();
        int lastGid = this.getNewGlyphId(lastChar.getValue());
        int[] startCode = new int[this.uniToGID.size() + 1];
        int[] endCode = new int[startCode.length];
        int[] idDelta = new int[startCode.length];
        int segCount = 0;
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> curChar2Gid = it.next();
            int curGid = this.getNewGlyphId(curChar2Gid.getValue());
            if (curChar2Gid.getKey() > 65535) {
                throw new UnsupportedOperationException("non-BMP Unicode character");
            }
            if (curChar2Gid.getKey() != prevChar.getKey() + 1 || curGid - lastGid != curChar2Gid.getKey() - lastChar.getKey()) {
                if (lastGid != 0) {
                    startCode[segCount] = lastChar.getKey();
                    endCode[segCount] = prevChar.getKey();
                    idDelta[segCount] = lastGid - lastChar.getKey();
                    ++segCount;
                } else if (!lastChar.getKey().equals(prevChar.getKey())) {
                    startCode[segCount] = lastChar.getKey() + 1;
                    endCode[segCount] = prevChar.getKey();
                    idDelta[segCount] = lastGid - lastChar.getKey();
                    ++segCount;
                }
                lastGid = curGid;
                lastChar = curChar2Gid;
            }
            prevChar = curChar2Gid;
        }
        startCode[segCount] = lastChar.getKey();
        endCode[segCount] = prevChar.getKey();
        idDelta[segCount] = lastGid - lastChar.getKey();
        startCode[++segCount] = 65535;
        endCode[segCount] = 65535;
        idDelta[segCount] = 1;
        int searchRange = 2 * (int)Math.pow(2.0, this.log2(++segCount));
        this.writeUint16(out, 4);
        this.writeUint16(out, 16 + segCount * 4 * 2);
        this.writeUint16(out, 0);
        this.writeUint16(out, segCount * 2);
        this.writeUint16(out, searchRange);
        this.writeUint16(out, this.log2(searchRange / 2));
        this.writeUint16(out, 2 * segCount - searchRange);
        for (i = 0; i < segCount; ++i) {
            this.writeUint16(out, endCode[i]);
        }
        this.writeUint16(out, 0);
        for (i = 0; i < segCount; ++i) {
            this.writeUint16(out, startCode[i]);
        }
        for (i = 0; i < segCount; ++i) {
            this.writeUint16(out, idDelta[i]);
        }
        for (i = 0; i < segCount; ++i) {
            this.writeUint16(out, 0);
        }
        return bos.toByteArray();
    }

    private byte[] buildPostTable() throws IOException {
        PostScriptTable post = this.ttf.getPostScript();
        if (post == null || this.keepTables != null && !this.keepTables.contains("post")) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        this.writeFixed(out, 2.0);
        this.writeFixed(out, post.getItalicAngle());
        this.writeSInt16(out, post.getUnderlinePosition());
        this.writeSInt16(out, post.getUnderlineThickness());
        this.writeUint32(out, post.getIsFixedPitch());
        this.writeUint32(out, post.getMinMemType42());
        this.writeUint32(out, post.getMaxMemType42());
        this.writeUint32(out, post.getMinMemType1());
        this.writeUint32(out, post.getMaxMemType1());
        this.writeUint16(out, this.glyphIds.size());
        LinkedHashMap<String, Integer> names = new LinkedHashMap<String, Integer>();
        Iterator<Object> iterator = this.glyphIds.iterator();
        while (iterator.hasNext()) {
            int gid = (Integer)iterator.next();
            String name = post.getName(gid);
            Integer macId = WGL4Names.MAC_GLYPH_NAMES_INDICES.get(name);
            if (macId != null) {
                this.writeUint16(out, macId);
                continue;
            }
            Integer ordinal = (Integer)names.get(name);
            if (ordinal == null) {
                ordinal = names.size();
                names.put(name, ordinal);
            }
            this.writeUint16(out, 258 + ordinal);
        }
        for (String name : names.keySet()) {
            byte[] buf = name.getBytes(Charset.forName("US-ASCII"));
            this.writeUint8(out, buf.length);
            out.write(buf);
        }
        out.flush();
        return bos.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] buildHmtxTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HorizontalHeaderTable h = this.ttf.getHorizontalHeader();
        HorizontalMetricsTable hm = this.ttf.getHorizontalMetrics();
        InputStream is = this.ttf.getOriginalData();
        int lastgid = h.getNumberOfHMetrics() - 1;
        boolean needLastGidWidth = this.glyphIds.last() > lastgid && !this.glyphIds.contains(lastgid);
        try {
            is.skip(hm.getOffset());
            long lastOffset = 0L;
            for (Integer glyphId : this.glyphIds) {
                long offset;
                if (glyphId <= lastgid) {
                    offset = (long)glyphId.intValue() * 4L;
                    lastOffset = this.copyBytes(is, bos, offset, lastOffset, 4);
                    continue;
                }
                if (needLastGidWidth) {
                    needLastGidWidth = false;
                    offset = (long)lastgid * 4L;
                    lastOffset = this.copyBytes(is, bos, offset, lastOffset, 2);
                }
                offset = (long)h.getNumberOfHMetrics() * 4L + (long)(glyphId - h.getNumberOfHMetrics()) * 2L;
                lastOffset = this.copyBytes(is, bos, offset, lastOffset, 2);
            }
            Object object = bos.toByteArray();
            return object;
        }
        finally {
            is.close();
        }
    }

    private long copyBytes(InputStream is, OutputStream os, long newOffset, long lastOffset, int count) throws IOException {
        long nskip = newOffset - lastOffset;
        if (nskip != is.skip(nskip)) {
            throw new EOFException("Unexpected EOF exception parsing glyphId of hmtx table.");
        }
        byte[] buf = new byte[count];
        if (count != is.read(buf, 0, count)) {
            throw new EOFException("Unexpected EOF exception parsing glyphId of hmtx table.");
        }
        os.write(buf, 0, count);
        return newOffset + (long)count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeToStream(OutputStream os) throws IOException {
        if (this.glyphIds.isEmpty() || this.uniToGID.isEmpty()) {
            LOG.info((Object)"font subset is empty");
        }
        this.addCompoundReferences();
        DataOutputStream out = new DataOutputStream(os);
        try {
            long[] newLoca = new long[this.glyphIds.size() + 1];
            byte[] head = this.buildHeadTable();
            byte[] hhea = this.buildHheaTable();
            byte[] maxp = this.buildMaxpTable();
            byte[] name = this.buildNameTable();
            byte[] os2 = this.buildOS2Table();
            byte[] glyf = this.buildGlyfTable(newLoca);
            byte[] loca = this.buildLocaTable(newLoca);
            byte[] cmap = this.buildCmapTable();
            byte[] hmtx = this.buildHmtxTable();
            byte[] post = this.buildPostTable();
            TreeMap<String, byte[]> tables = new TreeMap<String, byte[]>();
            if (os2 != null) {
                tables.put("OS/2", os2);
            }
            if (cmap != null) {
                tables.put("cmap", cmap);
            }
            tables.put("glyf", glyf);
            tables.put("head", head);
            tables.put("hhea", hhea);
            tables.put("hmtx", hmtx);
            tables.put("loca", loca);
            tables.put("maxp", maxp);
            if (name != null) {
                tables.put("name", name);
            }
            if (post != null) {
                tables.put("post", post);
            }
            for (Map.Entry<String, TTFTable> entry : this.ttf.getTableMap().entrySet()) {
                String tag = entry.getKey();
                TTFTable table = entry.getValue();
                if (tables.containsKey(tag) || this.keepTables != null && !this.keepTables.contains(tag)) continue;
                tables.put(tag, this.ttf.getTableBytes(table));
            }
            long checksum = this.writeFileHeader(out, tables.size());
            long offset = 12L + 16L * (long)tables.size();
            for (Map.Entry entry : tables.entrySet()) {
                checksum += this.writeTableHeader(out, (String)entry.getKey(), offset, (byte[])entry.getValue());
                offset += ((long)((byte[])entry.getValue()).length + 3L) / 4L * 4L;
            }
            checksum = 2981146554L - (checksum & 0xFFFFFFFFL);
            head[8] = (byte)(checksum >>> 24);
            head[9] = (byte)(checksum >>> 16);
            head[10] = (byte)(checksum >>> 8);
            head[11] = (byte)checksum;
            for (byte[] bytes : tables.values()) {
                this.writeTableBody(out, bytes);
            }
        }
        finally {
            out.close();
        }
    }

    private void writeFixed(DataOutputStream out, double f) throws IOException {
        double ip = Math.floor(f);
        double fp = (f - ip) * 65536.0;
        out.writeShort((int)ip);
        out.writeShort((int)fp);
    }

    private void writeUint32(DataOutputStream out, long l) throws IOException {
        out.writeInt((int)l);
    }

    private void writeUint16(DataOutputStream out, int i) throws IOException {
        out.writeShort(i);
    }

    private void writeSInt16(DataOutputStream out, short i) throws IOException {
        out.writeShort(i);
    }

    private void writeUint8(DataOutputStream out, int i) throws IOException {
        out.writeByte(i);
    }

    private void writeLongDateTime(DataOutputStream out, Calendar calendar) throws IOException {
        Calendar cal = Calendar.getInstance((TimeZone)TIMEZONE_UTC.clone());
        cal.set(1904, 0, 1, 0, 0, 0);
        cal.set(14, 0);
        long millisFor1904 = cal.getTimeInMillis();
        long secondsSince1904 = (calendar.getTimeInMillis() - millisFor1904) / 1000L;
        out.writeLong(secondsSince1904);
    }

    private long toUInt32(int high, int low) {
        return ((long)high & 0xFFFFL) << 16 | (long)low & 0xFFFFL;
    }

    private long toUInt32(byte[] bytes) {
        return ((long)bytes[0] & 0xFFL) << 24 | ((long)bytes[1] & 0xFFL) << 16 | ((long)bytes[2] & 0xFFL) << 8 | (long)bytes[3] & 0xFFL;
    }

    private int log2(int num) {
        return (int)Math.floor(Math.log(num) / Math.log(2.0));
    }
}

