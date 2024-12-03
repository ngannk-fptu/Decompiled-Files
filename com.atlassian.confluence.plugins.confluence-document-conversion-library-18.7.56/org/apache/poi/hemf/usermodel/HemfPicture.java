/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.usermodel;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfComment;
import org.apache.poi.hemf.record.emf.HemfHeader;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emf.HemfRecordIterator;
import org.apache.poi.hemf.usermodel.HemfEmbeddedIterator;
import org.apache.poi.hwmf.usermodel.HwmfCharsetAware;
import org.apache.poi.hwmf.usermodel.HwmfEmbedded;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.Units;

@Internal
public class HemfPicture
implements Iterable<HemfRecord>,
GenericRecord {
    private final LittleEndianInputStream stream;
    private final List<HemfRecord> records = new ArrayList<HemfRecord>();
    private boolean isParsed = false;
    private Charset defaultCharset = LocaleUtil.CHARSET_1252;

    public HemfPicture(InputStream is) {
        this(new LittleEndianInputStream(is));
    }

    public HemfPicture(LittleEndianInputStream is) {
        this.stream = is;
    }

    public HemfHeader getHeader() {
        List<HemfRecord> r = this.getRecords();
        if (r.isEmpty()) {
            throw new RecordFormatException("No records could be parsed - your .emf file is invalid");
        }
        return (HemfHeader)r.get(0);
    }

    public List<HemfRecord> getRecords() {
        if (!this.isParsed) {
            this.isParsed = true;
            HemfHeader[] header = new HemfHeader[1];
            new HemfRecordIterator(this.stream).forEachRemaining(r -> {
                if (r instanceof HemfHeader) {
                    header[0] = (HemfHeader)r;
                }
                r.setHeader(header[0]);
                if (r instanceof HwmfCharsetAware) {
                    ((HwmfCharsetAware)((Object)r)).setCharsetProvider(this::getDefaultCharset);
                }
                this.records.add((HemfRecord)r);
            });
        }
        return this.records;
    }

    @Override
    public Iterator<HemfRecord> iterator() {
        return this.getRecords().iterator();
    }

    @Override
    public Spliterator<HemfRecord> spliterator() {
        return this.getRecords().spliterator();
    }

    @Override
    public void forEach(Consumer<? super HemfRecord> action) {
        this.getRecords().forEach(action);
    }

    public Rectangle2D getBounds() {
        Rectangle2D dim = this.getHeader().getFrameRectangle();
        boolean isInvalid = ReluctantRectangle2D.isEmpty(dim);
        if (isInvalid) {
            ReluctantRectangle2D lastDim = new ReluctantRectangle2D();
            this.getInnerBounds(lastDim, new Rectangle2D.Double(), new Rectangle2D.Double());
            if (!((RectangularShape)lastDim).isEmpty()) {
                return lastDim;
            }
        }
        return dim;
    }

    public void getInnerBounds(final Rectangle2D window, final Rectangle2D viewport, final Rectangle2D bounds) {
        HemfRecord.RenderBounds holder = new HemfRecord.RenderBounds(){
            private HemfGraphics.EmfRenderState state = HemfGraphics.EmfRenderState.INITIAL;

            @Override
            public HemfGraphics.EmfRenderState getState() {
                return this.state;
            }

            @Override
            public void setState(HemfGraphics.EmfRenderState state) {
                this.state = state;
            }

            @Override
            public Rectangle2D getWindow() {
                return window;
            }

            @Override
            public Rectangle2D getViewport() {
                return viewport;
            }

            @Override
            public Rectangle2D getBounds() {
                return bounds;
            }
        };
        for (HemfRecord r : this.getRecords()) {
            if (holder.getState() == HemfGraphics.EmfRenderState.EMF_ONLY && r instanceof HemfComment.EmfComment || holder.getState() == HemfGraphics.EmfRenderState.EMFPLUS_ONLY && !(r instanceof HemfComment.EmfComment)) continue;
            try {
                r.calcBounds(holder);
            }
            catch (RuntimeException runtimeException) {}
        }
    }

    public Rectangle2D getBoundsInPoints() {
        return Units.pixelToPoints(this.getHeader().getBoundsRectangle());
    }

    public Dimension2D getSize() {
        Rectangle2D b = this.getBoundsInPoints();
        return new Dimension2DDouble(Math.abs(b.getWidth()), Math.abs(b.getHeight()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void draw(Graphics2D ctx, Rectangle2D graphicsBounds) {
        Shape clip = ctx.getClip();
        AffineTransform at = ctx.getTransform();
        try {
            Rectangle2D b;
            Rectangle2D emfBounds = this.getHeader().getBoundsRectangle();
            ReluctantRectangle2D winBounds = new ReluctantRectangle2D();
            ReluctantRectangle2D viewBounds = new ReluctantRectangle2D();
            Rectangle2D.Double recBounds = new Rectangle2D.Double();
            this.getInnerBounds(winBounds, viewBounds, recBounds);
            Boolean forceHeader = (Boolean)ctx.getRenderingHint(Drawable.EMF_FORCE_HEADER_BOUNDS);
            if (forceHeader == null) {
                forceHeader = false;
            }
            if (forceHeader.booleanValue()) {
                b = emfBounds;
            } else if (((RectangularShape)recBounds).isEmpty()) {
                b = !((RectangularShape)viewBounds).isEmpty() ? viewBounds : (!((RectangularShape)winBounds).isEmpty() ? winBounds : emfBounds);
            } else {
                Optional<Rectangle2D> result = Stream.of(emfBounds, winBounds, viewBounds).min(Comparator.comparingDouble(r -> HemfPicture.diff(r, recBounds)));
                if (result.isPresent()) {
                    b = result.get();
                } else {
                    throw new IllegalStateException("Failed to create Rectangle2D for drawing");
                }
            }
            ctx.translate(graphicsBounds.getCenterX(), graphicsBounds.getCenterY());
            ctx.scale(graphicsBounds.getWidth() / b.getWidth(), graphicsBounds.getHeight() / b.getHeight());
            ctx.translate(-b.getCenterX(), -b.getCenterY());
            HemfGraphics g = new HemfGraphics(ctx, b);
            int idx = 0;
            for (HemfRecord r2 : this.getRecords()) {
                try {
                    g.draw(r2);
                }
                catch (RuntimeException runtimeException) {
                    // empty catch block
                }
                ++idx;
            }
        }
        finally {
            ctx.setTransform(at);
            ctx.setClip(clip);
        }
    }

    private static double diff(Rectangle2D bounds, Rectangle2D target) {
        double d = 0.0;
        for (int i = 0; i < 4; ++i) {
            Function<Rectangle2D, Double> fx = i < 2 ? RectangularShape::getMinX : RectangularShape::getMaxX;
            Function<Rectangle2D, Double> fy = i % 2 == 0 ? RectangularShape::getMinY : RectangularShape::getMaxY;
            d += Point2D.distanceSq(fx.apply(bounds), fy.apply(bounds), fx.apply(target), fy.apply(target));
        }
        return d;
    }

    public Iterable<HwmfEmbedded> getEmbeddings() {
        return () -> new HemfEmbeddedIterator(this);
    }

    @Override
    public List<? extends GenericRecord> getGenericChildren() {
        return this.getRecords();
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }

    public void setDefaultCharset(Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    private static class ReluctantRectangle2D
    extends Rectangle2D.Double {
        private boolean offsetSet = false;
        private boolean rangeSet = false;

        public ReluctantRectangle2D() {
            super(-1.0, -1.0, 0.0, 0.0);
        }

        @Override
        public void setRect(double x, double y, double w, double h) {
            if (this.offsetSet && this.rangeSet) {
                return;
            }
            super.setRect(this.offsetSet ? this.x : x, this.offsetSet ? this.y : y, this.rangeSet ? this.width : w, this.rangeSet ? this.height : h);
            this.offsetSet |= x != -1.0 || y != -1.0;
            this.rangeSet |= w != 0.0 || h != 0.0;
        }

        @Override
        public boolean isEmpty() {
            return ReluctantRectangle2D.isEmpty(this);
        }

        public static boolean isEmpty(Rectangle2D r) {
            double w = Math.rint(r.getWidth());
            double h = Math.rint(r.getHeight());
            return w <= 0.0 || h <= 0.0 || r.getX() == -1.0 && r.getY() == -1.0 || w == 1.0 && h == 1.0;
        }
    }
}

