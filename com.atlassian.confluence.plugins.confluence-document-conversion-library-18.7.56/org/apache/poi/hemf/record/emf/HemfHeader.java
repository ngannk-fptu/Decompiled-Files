/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.record.emf.HemfDraw;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordType;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

@Internal
public class HemfHeader
implements HemfRecord {
    private final Rectangle2D boundsRectangle = new Rectangle2D.Double();
    private final Rectangle2D frameRectangle = new Rectangle2D.Double();
    private long bytes;
    private long records;
    private int handles;
    private String description;
    private long nPalEntries;
    private boolean hasExtension1;
    private long cbPixelFormat;
    private long offPixelFormat;
    private long bOpenGL;
    private boolean hasExtension2;
    private final Dimension2D deviceDimension = new Dimension2DDouble();
    private final Dimension2D milliDimension = new Dimension2DDouble();
    private final Dimension2D microDimension = new Dimension2DDouble();

    public Rectangle2D getBoundsRectangle() {
        return this.boundsRectangle;
    }

    public Rectangle2D getFrameRectangle() {
        return this.frameRectangle;
    }

    public long getBytes() {
        return this.bytes;
    }

    public long getRecords() {
        return this.records;
    }

    public int getHandles() {
        return this.handles;
    }

    public String getDescription() {
        return this.description;
    }

    public long getNPalEntries() {
        return this.nPalEntries;
    }

    public boolean isHasExtension1() {
        return this.hasExtension1;
    }

    public long getCbPixelFormat() {
        return this.cbPixelFormat;
    }

    public long getOffPixelFormat() {
        return this.offPixelFormat;
    }

    public long getbOpenGL() {
        return this.bOpenGL;
    }

    public boolean isHasExtension2() {
        return this.hasExtension2;
    }

    public Dimension2D getDeviceDimension() {
        return this.deviceDimension;
    }

    public Dimension2D getMilliDimension() {
        return this.milliDimension;
    }

    public Dimension2D getMicroDimension() {
        return this.microDimension;
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public HemfRecordType getEmfRecordType() {
        return HemfRecordType.header;
    }

    @Override
    public long init(LittleEndianInputStream leis, long recordSize, long recordId) throws IOException {
        if (recordId != HemfRecordType.header.id) {
            throw new IOException("Not a valid EMF header. Record type:" + recordId);
        }
        int startIdx = leis.getReadIndex();
        long size = HemfDraw.readRectL(leis, this.boundsRectangle);
        size += HemfDraw.readRectL(leis, this.frameRectangle);
        int recordSignature = leis.readInt();
        if (recordSignature != 1179469088) {
            throw new IOException("bad record signature: " + recordSignature);
        }
        long version = leis.readInt();
        this.bytes = leis.readUInt();
        this.records = leis.readUInt();
        this.handles = leis.readUShort();
        leis.skipFully(2);
        int nDescription = (int)leis.readUInt();
        int offDescription = (int)leis.readUInt();
        this.nPalEntries = leis.readUInt();
        size += 32L;
        size += HemfDraw.readDimensionInt(leis, this.deviceDimension);
        size += HemfDraw.readDimensionInt(leis, this.milliDimension);
        if (nDescription > 0 && offDescription > 0) {
            int skip = (int)((long)offDescription - (size + 8L));
            leis.mark(skip + nDescription * 2);
            leis.skipFully(skip);
            byte[] buf = new byte[(nDescription - 1) * 2];
            leis.readFully(buf);
            this.description = new String(buf, StandardCharsets.UTF_16LE).replace('\u0000', ' ').trim();
            leis.reset();
        }
        if (size + 12L <= recordSize) {
            this.hasExtension1 = true;
            this.cbPixelFormat = leis.readUInt();
            this.offPixelFormat = leis.readUInt();
            this.bOpenGL = leis.readUInt();
            size += 12L;
        }
        if (size + 8L <= recordSize) {
            this.hasExtension2 = true;
            size += HemfDraw.readDimensionInt(leis, this.microDimension);
        }
        return size;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("boundsRectangle", this::getBoundsRectangle);
        m.put("frameRectangle", this::getFrameRectangle);
        m.put("bytes", this::getBytes);
        m.put("records", this::getRecords);
        m.put("handles", this::getHandles);
        m.put("description", this::getDescription);
        m.put("nPalEntries", this::getNPalEntries);
        m.put("hasExtension1", this::isHasExtension1);
        m.put("cbPixelFormat", this::getCbPixelFormat);
        m.put("offPixelFormat", this::getOffPixelFormat);
        m.put("bOpenGL", this::getbOpenGL);
        m.put("hasExtension2", this::isHasExtension2);
        m.put("deviceDimension", this::getDeviceDimension);
        m.put("milliDimension", this::getMilliDimension);
        m.put("microDimension", this::getMicroDimension);
        return Collections.unmodifiableMap(m);
    }
}

