/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;

public class HwmfBitmap16
implements GenericRecord {
    private final boolean isPartial;
    private int type;
    private int width;
    private int height;
    private int widthBytes;
    private int planes;
    private int bitsPixel;
    private byte[] bitmap;

    public HwmfBitmap16() {
        this(false);
    }

    public HwmfBitmap16(boolean isPartial) {
        this.isPartial = isPartial;
    }

    public int init(LittleEndianInputStream leis) throws IOException {
        this.type = leis.readShort();
        this.width = leis.readShort();
        this.height = leis.readShort();
        this.widthBytes = leis.readShort();
        this.planes = leis.readUByte();
        this.bitsPixel = leis.readUByte();
        int size = 10;
        if (this.isPartial) {
            long skipSize = leis.skip(4L);
            assert (skipSize == 4L);
            skipSize = leis.skip(18L);
            assert (skipSize == 18L);
            size += 22;
        }
        int length = (this.width * this.bitsPixel + 15 >> 4 << 1) * this.height;
        this.bitmap = IOUtils.toByteArray(leis, length);
        return size;
    }

    public BufferedImage getImage() {
        return new BufferedImage(this.width, this.height, 6);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("isPartial", () -> this.isPartial);
        m.put("type", () -> this.type);
        m.put("width", () -> this.width);
        m.put("height", () -> this.height);
        m.put("widthBytes", () -> this.widthBytes);
        m.put("planes", () -> this.planes);
        m.put("bitsPixel", () -> this.bitsPixel);
        m.put("bitmap", () -> this.bitmap);
        return Collections.unmodifiableMap(m);
    }
}

