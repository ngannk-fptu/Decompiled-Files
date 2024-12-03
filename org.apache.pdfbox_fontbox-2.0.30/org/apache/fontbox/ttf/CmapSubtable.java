/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.TTFDataStream;

public class CmapSubtable
implements CmapLookup {
    private static final Log LOG = LogFactory.getLog(CmapSubtable.class);
    private static final long LEAD_OFFSET = 55232L;
    private static final long SURROGATE_OFFSET = -56613888L;
    private int platformId;
    private int platformEncodingId;
    private long subTableOffset;
    private int[] glyphIdToCharacterCode;
    private final Map<Integer, List<Integer>> glyphIdToCharacterCodeMultiple = new HashMap<Integer, List<Integer>>();
    private Map<Integer, Integer> characterCodeToGlyphId = Collections.emptyMap();

    void initData(TTFDataStream data) throws IOException {
        this.platformId = data.readUnsignedShort();
        this.platformEncodingId = data.readUnsignedShort();
        this.subTableOffset = data.readUnsignedInt();
    }

    void initSubtable(CmapTable cmap, int numGlyphs, TTFDataStream data) throws IOException {
        data.seek(cmap.getOffset() + this.subTableOffset);
        int subtableFormat = data.readUnsignedShort();
        if (subtableFormat < 8) {
            long length = data.readUnsignedShort();
            long version = data.readUnsignedShort();
        } else {
            data.readUnsignedShort();
            long length = data.readUnsignedInt();
            long version = data.readUnsignedInt();
        }
        switch (subtableFormat) {
            case 0: {
                this.processSubtype0(data);
                break;
            }
            case 2: {
                this.processSubtype2(data, numGlyphs);
                break;
            }
            case 4: {
                this.processSubtype4(data, numGlyphs);
                break;
            }
            case 6: {
                this.processSubtype6(data, numGlyphs);
                break;
            }
            case 8: {
                this.processSubtype8(data, numGlyphs);
                break;
            }
            case 10: {
                this.processSubtype10(data, numGlyphs);
                break;
            }
            case 12: {
                this.processSubtype12(data, numGlyphs);
                break;
            }
            case 13: {
                this.processSubtype13(data, numGlyphs);
                break;
            }
            case 14: {
                this.processSubtype14(data, numGlyphs);
                break;
            }
            default: {
                throw new IOException("Unknown cmap format:" + subtableFormat);
            }
        }
    }

    void processSubtype8(TTFDataStream data, int numGlyphs) throws IOException {
        int[] is32 = data.readUnsignedByteArray(8192);
        long nbGroups = data.readUnsignedInt();
        if (nbGroups > 65536L) {
            throw new IOException("CMap ( Subtype8 ) is invalid");
        }
        this.glyphIdToCharacterCode = this.newGlyphIdToCharacterCode(numGlyphs);
        this.characterCodeToGlyphId = new HashMap<Integer, Integer>(numGlyphs);
        if (numGlyphs == 0) {
            LOG.warn((Object)"subtable has no glyphs");
            return;
        }
        for (long i = 0L; i < nbGroups; ++i) {
            long firstCode = data.readUnsignedInt();
            long endCode = data.readUnsignedInt();
            long startGlyph = data.readUnsignedInt();
            if (firstCode > endCode || 0L > firstCode) {
                throw new IOException("Range invalid");
            }
            for (long j = firstCode; j <= endCode; ++j) {
                int currentCharCode;
                if (j > Integer.MAX_VALUE) {
                    throw new IOException("[Sub Format 8] Invalid character code " + j);
                }
                if ((int)j / 8 >= is32.length) {
                    throw new IOException("[Sub Format 8] Invalid character code " + j);
                }
                if ((is32[(int)j / 8] & 1 << (int)j % 8) == 0) {
                    currentCharCode = (int)j;
                } else {
                    long lead = 55232L + (j >> 10);
                    long trail = 56320L + (j & 0x3FFL);
                    long codepoint = (lead << 10) + trail + -56613888L;
                    if (codepoint > Integer.MAX_VALUE) {
                        throw new IOException("[Sub Format 8] Invalid character code " + codepoint);
                    }
                    currentCharCode = (int)codepoint;
                }
                long glyphIndex = startGlyph + (j - firstCode);
                if (glyphIndex > (long)numGlyphs || glyphIndex > Integer.MAX_VALUE) {
                    throw new IOException("CMap contains an invalid glyph index");
                }
                this.glyphIdToCharacterCode[(int)glyphIndex] = currentCharCode;
                this.characterCodeToGlyphId.put(currentCharCode, (int)glyphIndex);
            }
        }
    }

    void processSubtype10(TTFDataStream data, int numGlyphs) throws IOException {
        long startCode = data.readUnsignedInt();
        long numChars = data.readUnsignedInt();
        if (numChars > Integer.MAX_VALUE) {
            throw new IOException("Invalid number of Characters");
        }
        if (startCode < 0L || startCode > 0x10FFFFL || startCode + numChars > 0x10FFFFL || startCode + numChars >= 55296L && startCode + numChars <= 57343L) {
            throw new IOException("Invalid character codes, " + String.format("startCode: 0x%X, numChars: %d", startCode, numChars));
        }
    }

    void processSubtype12(TTFDataStream data, int numGlyphs) throws IOException {
        int maxGlyphId = 0;
        long nbGroups = data.readUnsignedInt();
        this.glyphIdToCharacterCode = this.newGlyphIdToCharacterCode(numGlyphs);
        this.characterCodeToGlyphId = new HashMap<Integer, Integer>(numGlyphs);
        if (numGlyphs == 0) {
            LOG.warn((Object)"subtable has no glyphs");
            return;
        }
        block0: for (long i = 0L; i < nbGroups; ++i) {
            long firstCode = data.readUnsignedInt();
            long endCode = data.readUnsignedInt();
            long startGlyph = data.readUnsignedInt();
            if (firstCode < 0L || firstCode > 0x10FFFFL || firstCode >= 55296L && firstCode <= 57343L) {
                throw new IOException("Invalid character code " + String.format("0x%X", firstCode));
            }
            if (endCode > 0L && endCode < firstCode || endCode > 0x10FFFFL || endCode >= 55296L && endCode <= 57343L) {
                throw new IOException("Invalid character code " + String.format("0x%X", endCode));
            }
            for (long j = 0L; j <= endCode - firstCode; ++j) {
                long glyphIndex = startGlyph + j;
                if (glyphIndex >= (long)numGlyphs) {
                    LOG.warn((Object)"Format 12 cmap contains an invalid glyph index");
                    continue block0;
                }
                if (firstCode + j > 0x10FFFFL) {
                    LOG.warn((Object)"Format 12 cmap contains character beyond UCS-4");
                }
                maxGlyphId = Math.max(maxGlyphId, (int)glyphIndex);
                this.characterCodeToGlyphId.put((int)(firstCode + j), (int)glyphIndex);
            }
        }
        this.buildGlyphIdToCharacterCodeLookup(maxGlyphId);
    }

    void processSubtype13(TTFDataStream data, int numGlyphs) throws IOException {
        long nbGroups = data.readUnsignedInt();
        this.glyphIdToCharacterCode = this.newGlyphIdToCharacterCode(numGlyphs);
        this.characterCodeToGlyphId = new HashMap<Integer, Integer>(numGlyphs);
        if (numGlyphs == 0) {
            LOG.warn((Object)"subtable has no glyphs");
            return;
        }
        for (long i = 0L; i < nbGroups; ++i) {
            long firstCode = data.readUnsignedInt();
            long endCode = data.readUnsignedInt();
            long glyphId = data.readUnsignedInt();
            if (glyphId > (long)numGlyphs) {
                LOG.warn((Object)"Format 13 cmap contains an invalid glyph index");
                break;
            }
            if (firstCode < 0L || firstCode > 0x10FFFFL || firstCode >= 55296L && firstCode <= 57343L) {
                throw new IOException("Invalid character code " + String.format("0x%X", firstCode));
            }
            if (endCode > 0L && endCode < firstCode || endCode > 0x10FFFFL || endCode >= 55296L && endCode <= 57343L) {
                throw new IOException("Invalid character code " + String.format("0x%X", endCode));
            }
            for (long j = 0L; j <= endCode - firstCode; ++j) {
                if (firstCode + j > Integer.MAX_VALUE) {
                    throw new IOException("Character Code greater than Integer.MAX_VALUE");
                }
                if (firstCode + j > 0x10FFFFL) {
                    LOG.warn((Object)"Format 13 cmap contains character beyond UCS-4");
                }
                this.glyphIdToCharacterCode[(int)glyphId] = (int)(firstCode + j);
                this.characterCodeToGlyphId.put((int)(firstCode + j), (int)glyphId);
            }
        }
    }

    void processSubtype14(TTFDataStream data, int numGlyphs) throws IOException {
        LOG.warn((Object)"Format 14 cmap table is not supported and will be ignored");
    }

    void processSubtype6(TTFDataStream data, int numGlyphs) throws IOException {
        int firstCode = data.readUnsignedShort();
        int entryCount = data.readUnsignedShort();
        if (entryCount == 0) {
            return;
        }
        this.characterCodeToGlyphId = new HashMap<Integer, Integer>(numGlyphs);
        int[] glyphIdArray = data.readUnsignedShortArray(entryCount);
        int maxGlyphId = 0;
        for (int i = 0; i < entryCount; ++i) {
            maxGlyphId = Math.max(maxGlyphId, glyphIdArray[i]);
            this.characterCodeToGlyphId.put(firstCode + i, glyphIdArray[i]);
        }
        this.buildGlyphIdToCharacterCodeLookup(maxGlyphId);
    }

    void processSubtype4(TTFDataStream data, int numGlyphs) throws IOException {
        int segCountX2 = data.readUnsignedShort();
        int segCount = segCountX2 / 2;
        int searchRange = data.readUnsignedShort();
        int entrySelector = data.readUnsignedShort();
        int rangeShift = data.readUnsignedShort();
        int[] endCount = data.readUnsignedShortArray(segCount);
        int reservedPad = data.readUnsignedShort();
        int[] startCount = data.readUnsignedShortArray(segCount);
        int[] idDelta = data.readUnsignedShortArray(segCount);
        long idRangeOffsetPosition = data.getCurrentPosition();
        int[] idRangeOffset = data.readUnsignedShortArray(segCount);
        this.characterCodeToGlyphId = new HashMap<Integer, Integer>(numGlyphs);
        int maxGlyphId = 0;
        for (int i = 0; i < segCount; ++i) {
            int start = startCount[i];
            int end = endCount[i];
            int delta = idDelta[i];
            int rangeOffset = idRangeOffset[i];
            long segmentRangeOffset = idRangeOffsetPosition + (long)i * 2L + (long)rangeOffset;
            if (start == 65535 || end == 65535) continue;
            for (int j = start; j <= end; ++j) {
                if (rangeOffset == 0) {
                    int glyphid = j + delta & 0xFFFF;
                    maxGlyphId = Math.max(glyphid, maxGlyphId);
                    this.characterCodeToGlyphId.put(j, glyphid);
                    continue;
                }
                long glyphOffset = segmentRangeOffset + (long)(j - start) * 2L;
                data.seek(glyphOffset);
                int glyphIndex = data.readUnsignedShort();
                if (glyphIndex == 0) continue;
                glyphIndex = glyphIndex + delta & 0xFFFF;
                maxGlyphId = Math.max(glyphIndex, maxGlyphId);
                this.characterCodeToGlyphId.put(j, glyphIndex);
            }
        }
        if (this.characterCodeToGlyphId.isEmpty()) {
            LOG.warn((Object)"cmap format 4 subtable is empty");
            return;
        }
        this.buildGlyphIdToCharacterCodeLookup(maxGlyphId);
    }

    private void buildGlyphIdToCharacterCodeLookup(int maxGlyphId) {
        this.glyphIdToCharacterCode = this.newGlyphIdToCharacterCode(maxGlyphId + 1);
        for (Map.Entry<Integer, Integer> entry : this.characterCodeToGlyphId.entrySet()) {
            if (this.glyphIdToCharacterCode[entry.getValue()] == -1) {
                this.glyphIdToCharacterCode[entry.getValue().intValue()] = entry.getKey();
                continue;
            }
            List<Integer> mappedValues = this.glyphIdToCharacterCodeMultiple.get(entry.getValue());
            if (mappedValues == null) {
                mappedValues = new ArrayList<Integer>(2);
                this.glyphIdToCharacterCodeMultiple.put(entry.getValue(), mappedValues);
                mappedValues.add(this.glyphIdToCharacterCode[entry.getValue()]);
                this.glyphIdToCharacterCode[entry.getValue().intValue()] = Integer.MIN_VALUE;
            }
            mappedValues.add(entry.getKey());
        }
    }

    void processSubtype2(TTFDataStream data, int numGlyphs) throws IOException {
        int[] subHeaderKeys = new int[256];
        int maxSubHeaderIndex = 0;
        for (int i = 0; i < 256; ++i) {
            subHeaderKeys[i] = data.readUnsignedShort();
            maxSubHeaderIndex = Math.max(maxSubHeaderIndex, subHeaderKeys[i] / 8);
        }
        SubHeader[] subHeaders = new SubHeader[maxSubHeaderIndex + 1];
        for (int i = 0; i <= maxSubHeaderIndex; ++i) {
            int firstCode = data.readUnsignedShort();
            int entryCount = data.readUnsignedShort();
            short idDelta = data.readSignedShort();
            int idRangeOffset = data.readUnsignedShort() - (maxSubHeaderIndex + 1 - i - 1) * 8 - 2;
            subHeaders[i] = new SubHeader(firstCode, entryCount, idDelta, idRangeOffset);
        }
        long startGlyphIndexOffset = data.getCurrentPosition();
        this.glyphIdToCharacterCode = this.newGlyphIdToCharacterCode(numGlyphs);
        this.characterCodeToGlyphId = new HashMap<Integer, Integer>(numGlyphs);
        if (numGlyphs == 0) {
            LOG.warn((Object)"subtable has no glyphs");
            return;
        }
        for (int i = 0; i <= maxSubHeaderIndex; ++i) {
            SubHeader sh = subHeaders[i];
            int firstCode = sh.getFirstCode();
            int idRangeOffset = sh.getIdRangeOffset();
            short idDelta = sh.getIdDelta();
            int entryCount = sh.getEntryCount();
            data.seek(startGlyphIndexOffset + (long)idRangeOffset);
            for (int j = 0; j < entryCount; ++j) {
                int charCode = i;
                charCode = (charCode << 8) + (firstCode + j);
                int p = data.readUnsignedShort();
                if (p > 0 && (p = (p + idDelta) % 65536) < 0) {
                    p += 65536;
                }
                if (p >= numGlyphs) {
                    LOG.warn((Object)("glyphId " + p + " for charcode " + charCode + " ignored, numGlyphs is " + numGlyphs));
                    continue;
                }
                this.glyphIdToCharacterCode[p] = charCode;
                this.characterCodeToGlyphId.put(charCode, p);
            }
        }
    }

    void processSubtype0(TTFDataStream data) throws IOException {
        byte[] glyphMapping = data.read(256);
        this.glyphIdToCharacterCode = this.newGlyphIdToCharacterCode(256);
        this.characterCodeToGlyphId = new HashMap<Integer, Integer>(glyphMapping.length);
        for (int i = 0; i < glyphMapping.length; ++i) {
            int glyphIndex = glyphMapping[i] & 0xFF;
            this.glyphIdToCharacterCode[glyphIndex] = i;
            this.characterCodeToGlyphId.put(i, glyphIndex);
        }
    }

    private int[] newGlyphIdToCharacterCode(int size) {
        int[] gidToCode = new int[size];
        Arrays.fill(gidToCode, -1);
        return gidToCode;
    }

    public int getPlatformEncodingId() {
        return this.platformEncodingId;
    }

    public void setPlatformEncodingId(int platformEncodingIdValue) {
        this.platformEncodingId = platformEncodingIdValue;
    }

    public int getPlatformId() {
        return this.platformId;
    }

    public void setPlatformId(int platformIdValue) {
        this.platformId = platformIdValue;
    }

    @Override
    public int getGlyphId(int characterCode) {
        Integer glyphId = this.characterCodeToGlyphId.get(characterCode);
        return glyphId == null ? 0 : glyphId;
    }

    @Deprecated
    public Integer getCharacterCode(int gid) {
        List<Integer> mappedValues;
        int code = this.getCharCode(gid);
        if (code == -1) {
            return null;
        }
        if (code == Integer.MIN_VALUE && (mappedValues = this.glyphIdToCharacterCodeMultiple.get(gid)) != null) {
            return mappedValues.get(0);
        }
        return code;
    }

    private int getCharCode(int gid) {
        if (gid < 0 || this.glyphIdToCharacterCode == null || gid >= this.glyphIdToCharacterCode.length) {
            return -1;
        }
        return this.glyphIdToCharacterCode[gid];
    }

    @Override
    public List<Integer> getCharCodes(int gid) {
        int code = this.getCharCode(gid);
        if (code == -1) {
            return null;
        }
        ArrayList<Integer> codes = null;
        if (code == Integer.MIN_VALUE) {
            List<Integer> mappedValues = this.glyphIdToCharacterCodeMultiple.get(gid);
            if (mappedValues != null) {
                codes = new ArrayList<Integer>(mappedValues);
                Collections.sort(codes);
            }
        } else {
            codes = new ArrayList<Integer>(1);
            codes.add(code);
        }
        return codes;
    }

    public String toString() {
        return "{" + this.getPlatformId() + " " + this.getPlatformEncodingId() + "}";
    }

    private static class SubHeader {
        private final int firstCode;
        private final int entryCount;
        private final short idDelta;
        private final int idRangeOffset;

        private SubHeader(int firstCodeValue, int entryCountValue, short idDeltaValue, int idRangeOffsetValue) {
            this.firstCode = firstCodeValue;
            this.entryCount = entryCountValue;
            this.idDelta = idDeltaValue;
            this.idRangeOffset = idRangeOffsetValue;
        }

        private int getFirstCode() {
            return this.firstCode;
        }

        private int getEntryCount() {
            return this.entryCount;
        }

        private short getIdDelta() {
            return this.idDelta;
        }

        private int getIdRangeOffset() {
            return this.idRangeOffset;
        }
    }
}

