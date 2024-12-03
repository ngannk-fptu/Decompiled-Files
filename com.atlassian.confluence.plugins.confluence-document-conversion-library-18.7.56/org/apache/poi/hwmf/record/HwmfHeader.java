/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfHeader
implements GenericRecord {
    private HwmfHeaderMetaType type;
    private int recordSize;
    private int version;
    private int filesize;
    private int numberOfObjects;
    private long maxRecord;
    private int numberOfMembers;

    public HwmfHeader(LittleEndianInputStream leis) throws IOException {
        this.type = HwmfHeaderMetaType.values()[leis.readUShort() - 1];
        this.recordSize = leis.readUShort();
        int bytesLeft = this.recordSize * 2 - 4;
        this.version = leis.readUShort();
        bytesLeft -= 2;
        this.filesize = leis.readInt();
        bytesLeft -= 4;
        this.numberOfObjects = leis.readUShort();
        bytesLeft -= 2;
        this.maxRecord = leis.readUInt();
        bytesLeft -= 4;
        this.numberOfMembers = leis.readUShort();
        if ((bytesLeft -= 2) > 0) {
            long len = leis.skip(bytesLeft);
            assert (len == (long)bytesLeft);
        }
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("type", () -> this.type);
        m.put("recordSize", () -> this.recordSize);
        m.put("version", () -> this.version);
        m.put("filesize", () -> this.filesize);
        m.put("numberOfObjects", () -> this.numberOfObjects);
        m.put("maxRecord", () -> this.maxRecord);
        m.put("numberOfMembers", () -> this.numberOfMembers);
        return Collections.unmodifiableMap(m);
    }

    public static enum HwmfHeaderMetaType {
        MEMORY_METAFILE,
        DISK_METAFILE;

    }
}

