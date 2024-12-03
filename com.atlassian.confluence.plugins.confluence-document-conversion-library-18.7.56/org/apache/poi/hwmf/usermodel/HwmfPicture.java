/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.usermodel;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hwmf.draw.HwmfDrawProperties;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.draw.HwmfGraphicsState;
import org.apache.poi.hwmf.record.HwmfHeader;
import org.apache.poi.hwmf.record.HwmfPlaceableHeader;
import org.apache.poi.hwmf.record.HwmfRecord;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.hwmf.record.HwmfWindowing;
import org.apache.poi.hwmf.usermodel.HwmfCharsetAware;
import org.apache.poi.hwmf.usermodel.HwmfEmbedded;
import org.apache.poi.hwmf.usermodel.HwmfEmbeddedIterator;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.Units;

public class HwmfPicture
implements Iterable<HwmfRecord>,
GenericRecord {
    public static final int DEFAULT_MAX_RECORD_LENGTH = 100000000;
    public static int MAX_RECORD_LENGTH = 100000000;
    private static final Logger LOG = LogManager.getLogger(HwmfPicture.class);
    final List<HwmfRecord> records = new ArrayList<HwmfRecord>();
    final HwmfPlaceableHeader placeableHeader;
    final HwmfHeader header;
    private Charset defaultCharset = LocaleUtil.CHARSET_1252;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public HwmfPicture(InputStream inputStream) throws IOException {
        try (LittleEndianInputStream leis = new LittleEndianInputStream(inputStream);){
            this.placeableHeader = HwmfPlaceableHeader.readHeader(leis);
            this.header = new HwmfHeader(leis);
            while (true) {
                long skipped;
                short recordFunction;
                long recordSize;
                try {
                    long recordSizeLong = leis.readUInt() * 2L;
                    if (recordSizeLong > Integer.MAX_VALUE) {
                        throw new RecordFormatException("record size can't be > 2147483647");
                    }
                    if (recordSizeLong < 0L) {
                        throw new RecordFormatException("record size can't be < 0");
                    }
                    recordSize = (int)recordSizeLong;
                    recordFunction = leis.readShort();
                }
                catch (Exception e) {
                    LOG.atError().log("unexpected eof - wmf file was truncated");
                    break;
                }
                int consumedSize = 6;
                HwmfRecordType wrt = HwmfRecordType.getById(recordFunction);
                if (wrt == null) {
                    throw new IOException("unexpected record type: " + recordFunction);
                }
                if (wrt == HwmfRecordType.eof) {
                    break;
                }
                if (wrt.constructor == null) {
                    throw new IOException("unsupported record type: " + recordFunction);
                }
                HwmfRecord wr = wrt.constructor.get();
                this.records.add(wr);
                int remainingSize = (int)(recordSize - (long)(consumedSize += wr.init(leis, recordSize, recordFunction)));
                if (remainingSize < 0) {
                    throw new RecordFormatException("read too many bytes. record size: " + recordSize + "; comsumed size: " + consumedSize);
                }
                if (remainingSize > 0 && (skipped = IOUtils.skipFully(leis, remainingSize)) != (long)remainingSize) {
                    throw new RecordFormatException("Tried to skip " + remainingSize + " but skipped: " + skipped);
                }
                if (!(wr instanceof HwmfCharsetAware)) continue;
                ((HwmfCharsetAware)((Object)wr)).setCharsetProvider(this::getDefaultCharset);
            }
        }
    }

    public List<HwmfRecord> getRecords() {
        return Collections.unmodifiableList(this.records);
    }

    public void draw(Graphics2D ctx) {
        Dimension2D dim = this.getSize();
        int width = Units.pointsToPixel(dim.getWidth());
        int height = Units.pointsToPixel(dim.getHeight());
        Rectangle2D.Double bounds = new Rectangle2D.Double(0.0, 0.0, width, height);
        this.draw(ctx, bounds);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void draw(Graphics2D ctx, Rectangle2D graphicsBounds) {
        HwmfGraphicsState state = new HwmfGraphicsState();
        state.backup(ctx);
        try {
            Rectangle2D wmfBounds = this.getBounds();
            Rectangle2D innerBounds = this.getInnnerBounds();
            if (innerBounds == null) {
                innerBounds = wmfBounds;
            }
            ctx.translate(graphicsBounds.getCenterX(), graphicsBounds.getCenterY());
            ctx.scale(graphicsBounds.getWidth() / innerBounds.getWidth(), graphicsBounds.getHeight() / innerBounds.getHeight());
            ctx.translate(-innerBounds.getCenterX(), -innerBounds.getCenterY());
            HwmfGraphics g = new HwmfGraphics(ctx, innerBounds);
            HwmfDrawProperties prop = g.getProperties();
            prop.setViewportOrg(innerBounds.getX(), innerBounds.getY());
            prop.setViewportExt(innerBounds.getWidth(), innerBounds.getHeight());
            int idx = 0;
            for (HwmfRecord r : this.records) {
                Shape ctxClip;
                prop = g.getProperties();
                Shape propClip = prop.getClip();
                if (!Objects.equals(propClip, ctxClip = ctx.getClip())) {
                    int n = 5;
                }
                r.draw(g);
                ++idx;
            }
        }
        finally {
            state.restore(ctx);
        }
    }

    public Rectangle2D getBounds() {
        if (this.placeableHeader != null) {
            return this.placeableHeader.getBounds();
        }
        Rectangle2D inner = this.getInnnerBounds();
        if (inner != null) {
            return inner;
        }
        throw new RuntimeException("invalid wmf file - window records are incomplete.");
    }

    public Rectangle2D getInnnerBounds() {
        HwmfWindowing.WmfSetWindowOrg wOrg = null;
        HwmfWindowing.WmfSetWindowExt wExt = null;
        for (HwmfRecord r : this.getRecords()) {
            if (r instanceof HwmfWindowing.WmfSetWindowOrg) {
                wOrg = (HwmfWindowing.WmfSetWindowOrg)r;
            } else if (r instanceof HwmfWindowing.WmfSetWindowExt) {
                wExt = (HwmfWindowing.WmfSetWindowExt)r;
            }
            if (wOrg == null || wExt == null) continue;
            return new Rectangle2D.Double(wOrg.getX(), wOrg.getY(), wExt.getSize().getWidth(), wExt.getSize().getHeight());
        }
        return null;
    }

    public HwmfPlaceableHeader getPlaceableHeader() {
        return this.placeableHeader;
    }

    public HwmfHeader getHeader() {
        return this.header;
    }

    public Rectangle2D getBoundsInPoints() {
        double inch = this.placeableHeader == null ? 1440.0 : (double)this.placeableHeader.getUnitsPerInch();
        Rectangle2D bounds = this.getBounds();
        double coeff = 72.0 / inch;
        return AffineTransform.getScaleInstance(coeff, coeff).createTransformedShape(bounds).getBounds2D();
    }

    public Dimension2D getSize() {
        Rectangle2D bounds = this.getBoundsInPoints();
        return new Dimension2DDouble(bounds.getWidth(), bounds.getHeight());
    }

    public Iterable<HwmfEmbedded> getEmbeddings() {
        return () -> new HwmfEmbeddedIterator(this);
    }

    @Override
    public Iterator<HwmfRecord> iterator() {
        return this.getRecords().iterator();
    }

    @Override
    public Spliterator<HwmfRecord> spliterator() {
        return this.getRecords().spliterator();
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }

    @Override
    public List<? extends GenericRecord> getGenericChildren() {
        return this.getRecords();
    }

    public void setDefaultCharset(Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }
}

