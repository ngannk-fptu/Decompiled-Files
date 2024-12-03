/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.digest.PureJavaCrc32
 */
package org.apache.poi.hsmf.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import org.apache.commons.codec.digest.PureJavaCrc32;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.hsmf.datatypes.ByteChunk;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.ChunkGroup;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.StringUtil;

public final class NameIdChunks
implements ChunkGroup {
    public static final String NAME = "__nameid_version1.0";
    private ByteChunk guidStream;
    private ByteChunk entryStream;
    private ByteChunk stringStream;
    private List<Chunk> allChunks = new ArrayList<Chunk>();

    public Chunk[] getAll() {
        return this.allChunks.toArray(new Chunk[0]);
    }

    @Override
    public Chunk[] getChunks() {
        return this.getAll();
    }

    @Override
    public void record(Chunk chunk) {
        if (chunk.getType() == Types.BINARY) {
            switch (chunk.getChunkId()) {
                case 2: {
                    this.guidStream = (ByteChunk)chunk;
                    break;
                }
                case 3: {
                    this.entryStream = (ByteChunk)chunk;
                    break;
                }
                case 4: {
                    this.stringStream = (ByteChunk)chunk;
                }
            }
        }
        this.allChunks.add(chunk);
    }

    @Override
    public void chunksComplete() {
    }

    public long getPropertyTag(ClassID guid, String name, long id) {
        byte[] entryStreamBytes;
        byte[] byArray = entryStreamBytes = this.entryStream == null ? null : this.entryStream.getValue();
        if (this.guidStream == null || this.entryStream == null || this.stringStream == null || guid == null || entryStreamBytes == null) {
            return 0L;
        }
        LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(entryStreamBytes);
        for (int i = 0; i < entryStreamBytes.length / 8; ++i) {
            long nameOffset = leis.readUInt();
            int guidIndex = leis.readUShort();
            int propertyKind = guidIndex & 1;
            int propertyIndex = leis.readUShort();
            if (!guid.equals(this.getPropertyGUID(guidIndex >>>= 1))) continue;
            String[] propertyName = new String[]{null};
            long[] propertyNameCRC32 = new long[]{-1L};
            long streamID = this.getStreamID(propertyKind, (int)nameOffset, guid, guidIndex, n -> {
                propertyName[0] = n;
            }, c -> {
                propertyNameCRC32[0] = c;
            });
            if (!NameIdChunks.matchesProperty(propertyKind, nameOffset, name, propertyName[0], id)) continue;
            if (propertyKind == 1 && propertyNameCRC32[0] < 0L) {
                return 32768L + (long)propertyIndex;
            }
            return this.getPropertyTag(streamID, nameOffset, propertyNameCRC32[0]);
        }
        return 0L;
    }

    private long getPropertyTag(long streamID, long nameOffset, long propertyNameCRC32) {
        for (Chunk chunk : this.allChunks) {
            byte[] matchChunkBytes;
            if (chunk.getType() != Types.BINARY || (long)chunk.getChunkId() != streamID || (matchChunkBytes = ((ByteChunk)chunk).getValue()) == null) continue;
            LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(matchChunkBytes);
            for (int m = 0; m < matchChunkBytes.length / 8; ++m) {
                long nameCRC = leis.readUInt();
                int matchGuidIndex = leis.readUShort();
                int matchPropertyIndex = leis.readUShort();
                int matchPropertyKind = matchGuidIndex & 1;
                if (nameCRC != (matchPropertyKind == 0 ? nameOffset : propertyNameCRC32)) continue;
                return 32768L + (long)matchPropertyIndex;
            }
        }
        return 0L;
    }

    private ClassID getPropertyGUID(int guidIndex) {
        int guidIndexOffset;
        byte[] guidStreamBytes;
        if (guidIndex == 1) {
            return PropertySetType.PS_MAPI.classID;
        }
        if (guidIndex == 2) {
            return PropertySetType.PS_PUBLIC_STRINGS.classID;
        }
        if (guidIndex >= 3 && (guidStreamBytes = this.guidStream.getValue()).length >= (guidIndexOffset = (guidIndex - 3) * 16) + 16) {
            return new ClassID(guidStreamBytes, guidIndexOffset);
        }
        return null;
    }

    private static boolean matchesProperty(int propertyKind, long nameOffset, String name, String propertyName, long id) {
        return propertyKind == 0 && id >= 0L && id == nameOffset || propertyKind == 1 && name != null && name.equals(propertyName);
    }

    private long getStreamID(int propertyKind, int nameOffset, ClassID guid, int guidIndex, Consumer<String> propertyNameSetter, Consumer<Long> propertyNameCRC32Setter) {
        long nameLength;
        if (propertyKind == 0) {
            return 4096L + (long)((nameOffset ^ guidIndex << 1) % 31);
        }
        byte[] stringBytes = this.stringStream.getValue();
        long propertyNameCRC32 = -1L;
        if (stringBytes.length > nameOffset && (long)stringBytes.length >= (long)(nameOffset + 4) + (nameLength = LittleEndian.getUInt(stringBytes, nameOffset))) {
            int nameStart = nameOffset + 4;
            String propertyName = new String(stringBytes, nameStart, (int)nameLength, StringUtil.UTF16LE);
            if (PropertySetType.PS_INTERNET_HEADERS.classID.equals(guid)) {
                byte[] n = propertyName.toLowerCase(Locale.ROOT).getBytes(StringUtil.UTF16LE);
                propertyNameCRC32 = NameIdChunks.calculateCRC32(n, 0, n.length);
            } else {
                propertyNameCRC32 = NameIdChunks.calculateCRC32(stringBytes, nameStart, (int)nameLength);
            }
            propertyNameSetter.accept(propertyName);
            propertyNameCRC32Setter.accept(propertyNameCRC32);
        }
        return 4096L + (propertyNameCRC32 ^ (long)(guidIndex << 1 | 1)) % 31L;
    }

    private static long calculateCRC32(byte[] buf, int off, int len) {
        PureJavaCrc32 crc = new PureJavaCrc32();
        crc.update(new byte[]{-1, -1, -1, -1}, 0, 4);
        crc.update(buf, off, len);
        return (crc.getValue() ^ 0xFFFFFFFFFFFFFFFFL) & 0xFFFFFFFFL;
    }

    public static enum PredefinedPropertySet {
        PSETID_COMMON("00062008-0000-0000-C000-000000000046"),
        PSETID_ADDRESS("00062004-0000-0000-C000-000000000046"),
        PSETID_APPOINTMENT("00062002-0000-0000-C000-000000000046"),
        PSETID_MEETING("6ED8DA90-450B-101B-98DA-00AA003F1305"),
        PSETID_LOG("0006200A-0000-0000-C000-000000000046"),
        PSETID_MESSAGING("41F28F13-83F4-4114-A584-EEDB5A6B0BFF"),
        PSETID_NOTE("0006200E-0000-0000-C000-000000000046"),
        PSETID_POST_RSS("00062041-0000-0000-C000-000000000046"),
        PSETID_TASK("00062003-0000-0000-C000-000000000046"),
        PSETID_UNIFIED_MESSAGING("4442858E-A9E3-4E80-B900-317A210CC15B"),
        PSETID_AIR_SYNC("71035549-0739-4DCB-9163-00F0580DBBDF"),
        PSETID_SHARING("00062040-0000-0000-C000-000000000046"),
        PSETID_XML_EXTRACTED_ENTITIES("23239608-685D-4732-9C55-4C95CB4E8E33"),
        PSETID_ATTACHMENT("96357F7F-59E1-47D0-99A7-46515C183B54");

        private final ClassID classID;

        private PredefinedPropertySet(String uuid) {
            this.classID = new ClassID(uuid);
        }

        public ClassID getClassID() {
            return this.classID;
        }
    }

    public static enum PropertySetType {
        PS_MAPI("00020328-0000-0000-C000-000000000046"),
        PS_PUBLIC_STRINGS("00020329-0000-0000-C000-000000000046"),
        PS_INTERNET_HEADERS("00020386-0000-0000-C000-000000000046");

        private final ClassID classID;

        private PropertySetType(String uuid) {
            this.classID = new ClassID(uuid);
        }

        public ClassID getClassID() {
            return this.classID;
        }
    }
}

