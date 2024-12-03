/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageJAI;
import javax.media.jai.ImageLayout;
import javax.media.jai.IntegerSequence;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.RenderedImageAdapter;
import javax.media.jai.SnapshotImage;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileFactory;
import javax.media.jai.TileRequest;
import javax.media.jai.WritablePropertySourceImpl;
import javax.media.jai.WritableRenderedImageAdapter;

public abstract class PlanarImage
implements ImageJAI,
RenderedImage {
    private Object UID;
    protected int minX;
    protected int minY;
    protected int width;
    protected int height;
    private Rectangle bounds = new Rectangle();
    protected int tileGridXOffset;
    protected int tileGridYOffset;
    protected int tileWidth;
    protected int tileHeight;
    protected SampleModel sampleModel = null;
    protected ColorModel colorModel = null;
    protected TileFactory tileFactory = null;
    private Vector sources = null;
    private Vector sinks = null;
    protected PropertyChangeSupportJAI eventManager = null;
    protected WritablePropertySourceImpl properties = null;
    private SnapshotImage snapshot = null;
    private WeakReference weakThis = new WeakReference<PlanarImage>(this);
    private Set tileListeners = null;
    private boolean disposed = false;
    private static final int MIN_ARRAYCOPY_SIZE = 64;

    public PlanarImage() {
        this.eventManager = new PropertyChangeSupportJAI(this);
        this.properties = new WritablePropertySourceImpl(null, null, this.eventManager);
        this.UID = ImageUtil.generateID(this);
    }

    public PlanarImage(ImageLayout layout, Vector sources, Map properties) {
        this();
        if (layout != null) {
            this.setImageLayout(layout);
        }
        if (sources != null) {
            this.setSources(sources);
        }
        if (properties != null) {
            Object factoryValue;
            this.properties.addProperties(properties);
            if (properties.containsKey(JAI.KEY_TILE_FACTORY) && (factoryValue = properties.get(JAI.KEY_TILE_FACTORY)) instanceof TileFactory) {
                this.tileFactory = (TileFactory)factoryValue;
            }
        }
    }

    protected void setImageLayout(ImageLayout layout) {
        if (layout == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (layout.isValid(1)) {
            this.minX = layout.getMinX(null);
        }
        if (layout.isValid(2)) {
            this.minY = layout.getMinY(null);
        }
        if (layout.isValid(4)) {
            this.width = layout.getWidth(null);
        }
        if (layout.isValid(8)) {
            this.height = layout.getHeight(null);
        }
        if (layout.isValid(16)) {
            this.tileGridXOffset = layout.getTileGridXOffset(null);
        }
        if (layout.isValid(32)) {
            this.tileGridYOffset = layout.getTileGridYOffset(null);
        }
        this.tileWidth = layout.isValid(64) ? layout.getTileWidth(null) : this.width;
        this.tileHeight = layout.isValid(128) ? layout.getTileHeight(null) : this.height;
        if (layout.isValid(256)) {
            this.sampleModel = layout.getSampleModel(null);
        }
        if (this.sampleModel != null && this.tileWidth > 0 && this.tileHeight > 0 && (this.sampleModel.getWidth() != this.tileWidth || this.sampleModel.getHeight() != this.tileHeight)) {
            this.sampleModel = this.sampleModel.createCompatibleSampleModel(this.tileWidth, this.tileHeight);
        }
        if (layout.isValid(512)) {
            this.colorModel = layout.getColorModel(null);
        }
        if (this.colorModel != null && this.sampleModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
            throw new IllegalArgumentException(JaiI18N.getString("PlanarImage5"));
        }
    }

    public static PlanarImage wrapRenderedImage(RenderedImage image) {
        if (image == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (image instanceof PlanarImage) {
            return (PlanarImage)image;
        }
        if (image instanceof WritableRenderedImage) {
            return new WritableRenderedImageAdapter((WritableRenderedImage)image);
        }
        return new RenderedImageAdapter(image);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PlanarImage createSnapshot() {
        if (this instanceof WritableRenderedImage) {
            if (this.snapshot == null) {
                PlanarImage planarImage = this;
                synchronized (planarImage) {
                    this.snapshot = new SnapshotImage(this);
                }
            }
            return this.snapshot.createSnapshot();
        }
        return this;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMaxX() {
        return this.getMinX() + this.getWidth();
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxY() {
        return this.getMinY() + this.getHeight();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getNumBands() {
        return this.getSampleModel().getNumBands();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Rectangle getBounds() {
        Rectangle rectangle = this.bounds;
        synchronized (rectangle) {
            this.bounds.setBounds(this.getMinX(), this.getMinY(), this.getWidth(), this.getHeight());
        }
        return this.bounds;
    }

    public int getTileGridXOffset() {
        return this.tileGridXOffset;
    }

    public int getTileGridYOffset() {
        return this.tileGridYOffset;
    }

    public int getTileWidth() {
        return this.tileWidth;
    }

    public int getTileHeight() {
        return this.tileHeight;
    }

    public int getMinTileX() {
        return PlanarImage.XToTileX(this.getMinX(), this.getTileGridXOffset(), this.getTileWidth());
    }

    public int getMaxTileX() {
        return PlanarImage.XToTileX(this.getMinX() + this.getWidth() - 1, this.getTileGridXOffset(), this.getTileWidth());
    }

    public int getNumXTiles() {
        int x = this.getMinX();
        int tx = this.getTileGridXOffset();
        int tw = this.getTileWidth();
        return PlanarImage.XToTileX(x + this.getWidth() - 1, tx, tw) - PlanarImage.XToTileX(x, tx, tw) + 1;
    }

    public int getMinTileY() {
        return PlanarImage.YToTileY(this.getMinY(), this.getTileGridYOffset(), this.getTileHeight());
    }

    public int getMaxTileY() {
        return PlanarImage.YToTileY(this.getMinY() + this.getHeight() - 1, this.getTileGridYOffset(), this.getTileHeight());
    }

    public int getNumYTiles() {
        int y = this.getMinY();
        int ty = this.getTileGridYOffset();
        int th = this.getTileHeight();
        return PlanarImage.YToTileY(y + this.getHeight() - 1, ty, th) - PlanarImage.YToTileY(y, ty, th) + 1;
    }

    public static int XToTileX(int x, int tileGridXOffset, int tileWidth) {
        if ((x -= tileGridXOffset) < 0) {
            x += 1 - tileWidth;
        }
        return x / tileWidth;
    }

    public static int YToTileY(int y, int tileGridYOffset, int tileHeight) {
        if ((y -= tileGridYOffset) < 0) {
            y += 1 - tileHeight;
        }
        return y / tileHeight;
    }

    public int XToTileX(int x) {
        return PlanarImage.XToTileX(x, this.getTileGridXOffset(), this.getTileWidth());
    }

    public int YToTileY(int y) {
        return PlanarImage.YToTileY(y, this.getTileGridYOffset(), this.getTileHeight());
    }

    public static int tileXToX(int tx, int tileGridXOffset, int tileWidth) {
        return tx * tileWidth + tileGridXOffset;
    }

    public static int tileYToY(int ty, int tileGridYOffset, int tileHeight) {
        return ty * tileHeight + tileGridYOffset;
    }

    public int tileXToX(int tx) {
        return PlanarImage.tileXToX(tx, this.getTileGridXOffset(), this.getTileWidth());
    }

    public int tileYToY(int ty) {
        return PlanarImage.tileYToY(ty, this.getTileGridYOffset(), this.getTileHeight());
    }

    public Rectangle getTileRect(int tileX, int tileY) {
        return this.getBounds().intersection(new Rectangle(this.tileXToX(tileX), this.tileYToY(tileY), this.getTileWidth(), this.getTileHeight()));
    }

    public SampleModel getSampleModel() {
        return this.sampleModel;
    }

    public ColorModel getColorModel() {
        return this.colorModel;
    }

    public static ColorModel getDefaultColorModel(int dataType, int numBands) {
        if (dataType < 0 || dataType == 2 || dataType > 5 || numBands < 1 || numBands > 4) {
            return null;
        }
        ColorSpace cs = numBands <= 2 ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000);
        boolean useAlpha = numBands == 2 || numBands == 4;
        int transparency = useAlpha ? 3 : 1;
        return RasterFactory.createComponentColorModel(dataType, cs, useAlpha, false, transparency);
    }

    public static ColorModel createColorModel(SampleModel sm) {
        if (sm == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int bands = sm.getNumBands();
        if (bands < 1 || bands > 4) {
            return null;
        }
        if (sm instanceof ComponentSampleModel) {
            return PlanarImage.getDefaultColorModel(sm.getDataType(), bands);
        }
        if (sm instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
            int[] bitMasks = sppsm.getBitMasks();
            int rmask = 0;
            int gmask = 0;
            int bmask = 0;
            int amask = 0;
            int numBands = bitMasks.length;
            if (numBands <= 2) {
                gmask = bmask = bitMasks[0];
                rmask = bmask;
                if (numBands == 2) {
                    amask = bitMasks[1];
                }
            } else {
                rmask = bitMasks[0];
                gmask = bitMasks[1];
                bmask = bitMasks[2];
                if (numBands == 4) {
                    amask = bitMasks[3];
                }
            }
            int[] sampleSize = sppsm.getSampleSize();
            int bits = 0;
            for (int i = 0; i < sampleSize.length; ++i) {
                bits += sampleSize[i];
            }
            return new DirectColorModel(bits, rmask, gmask, bmask, amask);
        }
        if (ImageUtil.isBinary(sm)) {
            byte[] comp = new byte[]{0, -1};
            return new IndexColorModel(1, 2, comp, comp, comp);
        }
        return null;
    }

    public TileFactory getTileFactory() {
        return this.tileFactory;
    }

    public int getNumSources() {
        return this.sources == null ? 0 : this.sources.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getSources() {
        if (this.getNumSources() == 0) {
            return null;
        }
        Vector vector = this.sources;
        synchronized (vector) {
            return (Vector)this.sources.clone();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PlanarImage getSource(int index) {
        if (this.sources == null) {
            throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("PlanarImage0"));
        }
        Vector vector = this.sources;
        synchronized (vector) {
            return (PlanarImage)this.sources.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setSources(List sourceList) {
        if (sourceList == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int size = sourceList.size();
        Object object = this;
        synchronized (object) {
            if (this.sources != null) {
                Iterator it = this.sources.iterator();
                while (it.hasNext()) {
                    Object src = it.next();
                    if (!(src instanceof PlanarImage)) continue;
                    ((PlanarImage)src).removeSink(this);
                }
            }
            this.sources = new Vector(size);
        }
        object = this.sources;
        synchronized (object) {
            for (int i = 0; i < size; ++i) {
                Object sourceElement = sourceList.get(i);
                if (sourceElement == null) {
                    throw new IllegalArgumentException(JaiI18N.getString("PlanarImage7"));
                }
                this.sources.add(sourceElement instanceof RenderedImage ? PlanarImage.wrapRenderedImage((RenderedImage)sourceElement) : sourceElement);
                if (!(sourceElement instanceof PlanarImage)) continue;
                ((PlanarImage)sourceElement).addSink(this);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeSources() {
        if (this.sources != null) {
            PlanarImage planarImage = this;
            synchronized (planarImage) {
                if (this.sources != null) {
                    Iterator it = this.sources.iterator();
                    while (it.hasNext()) {
                        Object src = it.next();
                        if (!(src instanceof PlanarImage)) continue;
                        ((PlanarImage)src).removeSink(this);
                    }
                }
                this.sources = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PlanarImage getSourceImage(int index) {
        if (this.sources == null) {
            throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("PlanarImage0"));
        }
        Vector vector = this.sources;
        synchronized (vector) {
            return (PlanarImage)this.sources.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getSourceObject(int index) {
        if (this.sources == null) {
            throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("PlanarImage0"));
        }
        Vector vector = this.sources;
        synchronized (vector) {
            return this.sources.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addSource(Object source) {
        Object object;
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sources == null) {
            object = this;
            synchronized (object) {
                this.sources = new Vector();
            }
        }
        object = this.sources;
        synchronized (object) {
            this.sources.add(source instanceof RenderedImage ? PlanarImage.wrapRenderedImage((RenderedImage)source) : source);
        }
        if (source instanceof PlanarImage) {
            ((PlanarImage)source).addSink(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setSource(Object source, int index) {
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sources == null) {
            throw new ArrayIndexOutOfBoundsException(JaiI18N.getString("PlanarImage0"));
        }
        Vector vector = this.sources;
        synchronized (vector) {
            if (index < this.sources.size() && this.sources.get(index) instanceof PlanarImage) {
                this.getSourceImage(index).removeSink(this);
            }
            this.sources.set(index, source instanceof RenderedImage ? PlanarImage.wrapRenderedImage((RenderedImage)source) : source);
        }
        if (source instanceof PlanarImage) {
            ((PlanarImage)source).addSink(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean removeSource(Object source) {
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sources == null) {
            return false;
        }
        Vector vector = this.sources;
        synchronized (vector) {
            if (source instanceof PlanarImage) {
                ((PlanarImage)source).removeSink(this);
            }
            return this.sources.remove(source);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getSinks() {
        Vector v = null;
        if (this.sinks != null) {
            Vector vector = this.sinks;
            synchronized (vector) {
                int size = this.sinks.size();
                v = new Vector(size);
                for (int i = 0; i < size; ++i) {
                    Object o = ((WeakReference)this.sinks.get(i)).get();
                    if (o == null) continue;
                    v.add(o);
                }
            }
            if (v.size() == 0) {
                v = null;
            }
        }
        return v;
    }

    public synchronized boolean addSink(Object sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sinks == null) {
            this.sinks = new Vector();
        }
        boolean result = false;
        result = sink instanceof PlanarImage ? this.sinks.add(((PlanarImage)sink).weakThis) : this.sinks.add(new WeakReference<Object>(sink));
        return result;
    }

    public synchronized boolean removeSink(Object sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sinks == null) {
            return false;
        }
        boolean result = false;
        if (sink instanceof PlanarImage) {
            result = this.sinks.remove(((PlanarImage)sink).weakThis);
        } else {
            Iterator it = this.sinks.iterator();
            while (it.hasNext()) {
                Object referent = ((WeakReference)it.next()).get();
                if (referent == sink) {
                    it.remove();
                    result = true;
                    continue;
                }
                if (referent != null) continue;
                it.remove();
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addSink(PlanarImage sink) {
        Object object;
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sinks == null) {
            object = this;
            synchronized (object) {
                this.sinks = new Vector();
            }
        }
        object = this.sinks;
        synchronized (object) {
            this.sinks.add(sink.weakThis);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean removeSink(PlanarImage sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sinks == null) {
            return false;
        }
        Vector vector = this.sinks;
        synchronized (vector) {
            return this.sinks.remove(sink.weakThis);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeSinks() {
        if (this.sinks != null) {
            PlanarImage planarImage = this;
            synchronized (planarImage) {
                this.sinks = null;
            }
        }
    }

    protected Hashtable getProperties() {
        return (Hashtable)this.properties.getProperties();
    }

    protected void setProperties(Hashtable properties) {
        this.properties.addProperties(properties);
    }

    public Object getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public Class getPropertyClass(String name) {
        return this.properties.getPropertyClass(name);
    }

    public void setProperty(String name, Object value) {
        this.properties.setProperty(name, value);
    }

    public void removeProperty(String name) {
        this.properties.removeProperty(name);
    }

    public String[] getPropertyNames() {
        return this.properties.getPropertyNames();
    }

    public String[] getPropertyNames(String prefix) {
        return PropertyUtil.getPropertyNames(this.getPropertyNames(), prefix);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.eventManager.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.eventManager.addPropertyChangeListener(propertyName.toLowerCase(), listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.eventManager.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.eventManager.removePropertyChangeListener(propertyName.toLowerCase(), listener);
    }

    private synchronized Set getTileComputationListeners(boolean createIfNull) {
        if (createIfNull && this.tileListeners == null) {
            this.tileListeners = Collections.synchronizedSet(new HashSet());
        }
        return this.tileListeners;
    }

    public synchronized void addTileComputationListener(TileComputationListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Set listeners = this.getTileComputationListeners(true);
        listeners.add(listener);
    }

    public synchronized void removeTileComputationListener(TileComputationListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Set listeners = this.getTileComputationListeners(false);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public TileComputationListener[] getTileComputationListeners() {
        Set listeners = this.getTileComputationListeners(false);
        if (listeners == null) {
            return null;
        }
        return listeners.toArray(new TileComputationListener[listeners.size()]);
    }

    public void getSplits(IntegerSequence xSplits, IntegerSequence ySplits, Rectangle rect) {
        if (xSplits == null || ySplits == null || rect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int minTileX = this.XToTileX(rect.x);
        int maxTileX = this.XToTileX(rect.x + rect.width - 1);
        int xTilePos = this.tileXToX(minTileX);
        for (int i = minTileX; i <= maxTileX; ++i) {
            xSplits.insert(xTilePos);
            xTilePos += this.tileWidth;
        }
        int minTileY = this.YToTileY(rect.y);
        int maxTileY = this.YToTileY(rect.y + rect.height - 1);
        int yTilePos = this.tileYToY(minTileY);
        for (int i = minTileY; i <= maxTileY; ++i) {
            ySplits.insert(yTilePos);
            yTilePos += this.tileHeight;
        }
    }

    public Point[] getTileIndices(Rectangle region) {
        if (region == null) {
            region = (Rectangle)this.getBounds().clone();
        } else {
            if (!region.intersects(this.getBounds())) {
                return null;
            }
            if ((region = region.intersection(this.getBounds())).isEmpty()) {
                return null;
            }
        }
        if (region == null) {
            region = this.getBounds();
        } else {
            Rectangle r = new Rectangle(this.getMinX(), this.getMinY(), this.getWidth() + 1, this.getHeight() + 1);
            if (!region.intersects(r)) {
                return null;
            }
            region = region.intersection(r);
        }
        int minTileX = this.XToTileX(region.x);
        int maxTileX = this.XToTileX(region.x + region.width - 1);
        int minTileY = this.YToTileY(region.y);
        int maxTileY = this.YToTileY(region.y + region.height - 1);
        Point[] tileIndices = new Point[(maxTileY - minTileY + 1) * (maxTileX - minTileX + 1)];
        int tileIndexOffset = 0;
        for (int ty = minTileY; ty <= maxTileY; ++ty) {
            for (int tx = minTileX; tx <= maxTileX; ++tx) {
                tileIndices[tileIndexOffset++] = new Point(tx, ty);
            }
        }
        return tileIndices;
    }

    public boolean overlapsMultipleTiles(Rectangle rect) {
        if (rect == null) {
            throw new IllegalArgumentException("rect == null!");
        }
        Rectangle xsect = rect.intersection(this.getBounds());
        return !xsect.isEmpty() && (this.XToTileX(xsect.x) != this.XToTileX(xsect.x + xsect.width - 1) || this.YToTileY(xsect.y) != this.YToTileY(xsect.y + xsect.height - 1));
    }

    protected final WritableRaster createWritableRaster(SampleModel sampleModel, Point location) {
        if (sampleModel == null) {
            throw new IllegalArgumentException("sampleModel == null!");
        }
        return this.tileFactory != null ? this.tileFactory.createTile(sampleModel, location) : RasterFactory.createWritableRaster(sampleModel, location);
    }

    public Raster getData() {
        return this.getData(null);
    }

    /*
     * WARNING - void declaration
     */
    public Raster getData(Rectangle region) {
        void var8_9;
        WritableRaster dstRaster;
        int i;
        Rectangle b = this.getBounds();
        if (region == null) {
            region = b;
        } else if (!region.intersects(b)) {
            throw new IllegalArgumentException(JaiI18N.getString("PlanarImage4"));
        }
        Rectangle xsect = region == b ? region : region.intersection(b);
        int startTileX = this.XToTileX(xsect.x);
        int startTileY = this.YToTileY(xsect.y);
        int endTileX = this.XToTileX(xsect.x + xsect.width - 1);
        int endTileY = this.YToTileY(xsect.y + xsect.height - 1);
        if (startTileX == endTileX && startTileY == endTileY && this.getTileRect(startTileX, startTileY).contains(region)) {
            Raster tile = this.getTile(startTileX, startTileY);
            if (this instanceof WritableRenderedImage) {
                SampleModel sm = tile.getSampleModel();
                if (sm.getWidth() != region.width || sm.getHeight() != region.height) {
                    sm = sm.createCompatibleSampleModel(region.width, region.height);
                }
                WritableRaster destinationRaster = this.createWritableRaster(sm, region.getLocation());
                Raster sourceRaster = tile.getBounds().equals(region) ? tile : tile.createChild(region.x, region.y, region.width, region.height, region.x, region.y, null);
                JDKWorkarounds.setRect(destinationRaster, sourceRaster);
                return destinationRaster;
            }
            return tile.getBounds().equals(region) ? tile : tile.createChild(region.x, region.y, region.width, region.height, region.x, region.y, null);
        }
        SampleModel srcSM = this.getSampleModel();
        int dataType = srcSM.getDataType();
        int nbands = srcSM.getNumBands();
        boolean isBandChild = false;
        ComponentSampleModel csm = null;
        int[] bandOffs = null;
        boolean fastCobblePossible = false;
        if (srcSM instanceof ComponentSampleModel) {
            csm = (ComponentSampleModel)srcSM;
            int ps = csm.getPixelStride();
            boolean isBandInt = ps == 1 && nbands > 1;
            boolean bl = isBandChild = ps > 1 && nbands != ps;
            if (!isBandChild && !isBandInt) {
                bandOffs = csm.getBandOffsets();
                for (i = 0; i < nbands && bandOffs[i] < nbands; ++i) {
                }
                if (i == nbands) {
                    fastCobblePossible = true;
                }
            }
        }
        if (fastCobblePossible) {
            try {
                SampleModel interleavedSM = RasterFactory.createPixelInterleavedSampleModel(dataType, region.width, region.height, nbands, region.width * nbands, bandOffs);
                dstRaster = this.createWritableRaster(interleavedSM, region.getLocation());
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(JaiI18N.getString("PlanarImage2"));
            }
            switch (dataType) {
                case 0: {
                    this.cobbleByte(region, dstRaster);
                    break;
                }
                case 2: {
                    this.cobbleShort(region, dstRaster);
                    break;
                }
                case 1: {
                    this.cobbleUShort(region, dstRaster);
                    break;
                }
                case 3: {
                    this.cobbleInt(region, dstRaster);
                    break;
                }
                case 4: {
                    this.cobbleFloat(region, dstRaster);
                    break;
                }
                case 5: {
                    this.cobbleDouble(region, dstRaster);
                    break;
                }
            }
        } else {
            SampleModel sm = this.sampleModel;
            if (sm.getWidth() != region.width || sm.getHeight() != region.height) {
                sm = sm.createCompatibleSampleModel(region.width, region.height);
            }
            try {
                dstRaster = this.createWritableRaster(sm, region.getLocation());
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(JaiI18N.getString("PlanarImage2"));
            }
            for (int j = startTileY; j <= endTileY; ++j) {
                for (i = startTileX; i <= endTileX; ++i) {
                    Raster tile = this.getTile(i, j);
                    Rectangle subRegion = region.intersection(tile.getBounds());
                    Raster subRaster = tile.createChild(subRegion.x, subRegion.y, subRegion.width, subRegion.height, subRegion.x, subRegion.y, null);
                    if (sm instanceof ComponentSampleModel && isBandChild) {
                        switch (sm.getDataType()) {
                            case 4: {
                                dstRaster.setPixels(subRegion.x, subRegion.y, subRegion.width, subRegion.height, subRaster.getPixels(subRegion.x, subRegion.y, subRegion.width, subRegion.height, new float[nbands * subRegion.width * subRegion.height]));
                                break;
                            }
                            case 5: {
                                dstRaster.setPixels(subRegion.x, subRegion.y, subRegion.width, subRegion.height, subRaster.getPixels(subRegion.x, subRegion.y, subRegion.width, subRegion.height, new double[nbands * subRegion.width * subRegion.height]));
                                break;
                            }
                            default: {
                                dstRaster.setPixels(subRegion.x, subRegion.y, subRegion.width, subRegion.height, subRaster.getPixels(subRegion.x, subRegion.y, subRegion.width, subRegion.height, new int[nbands * subRegion.width * subRegion.height]));
                                break;
                            }
                        }
                        continue;
                    }
                    JDKWorkarounds.setRect(dstRaster, subRaster);
                }
            }
        }
        return var8_9;
    }

    public WritableRaster copyData() {
        return this.copyData(null);
    }

    public WritableRaster copyData(WritableRaster raster) {
        Rectangle region;
        if (raster == null) {
            region = this.getBounds();
            SampleModel sm = this.getSampleModel();
            if (sm.getWidth() != region.width || sm.getHeight() != region.height) {
                sm = sm.createCompatibleSampleModel(region.width, region.height);
            }
            raster = this.createWritableRaster(sm, region.getLocation());
        } else {
            region = raster.getBounds().intersection(this.getBounds());
            if (region.isEmpty()) {
                return raster;
            }
        }
        int startTileX = this.XToTileX(region.x);
        int startTileY = this.YToTileY(region.y);
        int endTileX = this.XToTileX(region.x + region.width - 1);
        int endTileY = this.YToTileY(region.y + region.height - 1);
        SampleModel[] sampleModels = new SampleModel[]{this.getSampleModel()};
        int tagID = RasterAccessor.findCompatibleTag(sampleModels, raster.getSampleModel());
        RasterFormatTag srcTag = new RasterFormatTag(this.getSampleModel(), tagID);
        RasterFormatTag dstTag = new RasterFormatTag(raster.getSampleModel(), tagID);
        for (int ty = startTileY; ty <= endTileY; ++ty) {
            for (int tx = startTileX; tx <= endTileX; ++tx) {
                Raster tile = this.getTile(tx, ty);
                Rectangle subRegion = region.intersection(tile.getBounds());
                RasterAccessor s = new RasterAccessor(tile, subRegion, srcTag, this.getColorModel());
                RasterAccessor d = new RasterAccessor(raster, subRegion, dstTag, null);
                ImageUtil.copyRaster(s, d);
            }
        }
        return raster;
    }

    public void copyExtendedData(WritableRaster dest, BorderExtender extender) {
        if (dest == null || extender == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Rectangle destBounds = dest.getBounds();
        Rectangle imageBounds = this.getBounds();
        if (imageBounds.contains(destBounds)) {
            this.copyData(dest);
            return;
        }
        Rectangle isect = imageBounds.intersection(destBounds);
        if (!isect.isEmpty()) {
            WritableRaster isectRaster = dest.createWritableChild(isect.x, isect.y, isect.width, isect.height, isect.x, isect.y, null);
            this.copyData(isectRaster);
        }
        extender.extend(dest, this);
    }

    public Raster getExtendedData(Rectangle region, BorderExtender extender) {
        if (region == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.getBounds().contains(region)) {
            return this.getData(region);
        }
        if (extender == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        SampleModel destSM = this.getSampleModel();
        if (destSM.getWidth() != region.width || destSM.getHeight() != region.height) {
            destSM = destSM.createCompatibleSampleModel(region.width, region.height);
        }
        WritableRaster dest = this.createWritableRaster(destSM, region.getLocation());
        this.copyExtendedData(dest, extender);
        return dest;
    }

    public BufferedImage getAsBufferedImage(Rectangle rect, ColorModel cm) {
        if (cm == null && (cm = this.getColorModel()) == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PlanarImage6"));
        }
        if (!JDKWorkarounds.areCompatibleDataModels(this.sampleModel, cm)) {
            throw new IllegalArgumentException(JaiI18N.getString("PlanarImage3"));
        }
        rect = rect == null ? this.getBounds() : this.getBounds().intersection(rect);
        SampleModel sm = this.sampleModel.getWidth() != rect.width || this.sampleModel.getHeight() != rect.height ? this.sampleModel.createCompatibleSampleModel(rect.width, rect.height) : this.sampleModel;
        WritableRaster ras = this.createWritableRaster(sm, rect.getLocation());
        this.copyData(ras);
        if (rect.x != 0 || rect.y != 0) {
            ras = RasterFactory.createWritableChild(ras, rect.x, rect.y, rect.width, rect.height, 0, 0, null);
        }
        return new BufferedImage(cm, ras, cm.isAlphaPremultiplied(), null);
    }

    public BufferedImage getAsBufferedImage() {
        return this.getAsBufferedImage(null, null);
    }

    public Graphics getGraphics() {
        throw new IllegalAccessError(JaiI18N.getString("PlanarImage1"));
    }

    public abstract Raster getTile(int var1, int var2);

    public Raster[] getTiles(Point[] tileIndices) {
        if (tileIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int size = tileIndices.length;
        Raster[] tiles = new Raster[size];
        for (int i = 0; i < tileIndices.length; ++i) {
            Point p = tileIndices[i];
            tiles[i] = this.getTile(p.x, p.y);
        }
        return tiles;
    }

    public Raster[] getTiles() {
        return this.getTiles(this.getTileIndices(this.getBounds()));
    }

    public TileRequest queueTiles(Point[] tileIndices) {
        if (tileIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        TileComputationListener[] listeners = this.getTileComputationListeners();
        return JAI.getDefaultInstance().getTileScheduler().scheduleTiles(this, tileIndices, listeners);
    }

    public void cancelTiles(TileRequest request, Point[] tileIndices) {
        if (request == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
        }
        JAI.getDefaultInstance().getTileScheduler().cancelTiles(request, tileIndices);
    }

    public void prefetchTiles(Point[] tileIndices) {
        if (tileIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        JAI.getDefaultInstance().getTileScheduler().prefetchTiles(this, tileIndices);
    }

    public synchronized void dispose() {
        if (this.disposed) {
            return;
        }
        this.disposed = true;
        Vector srcs = this.getSources();
        if (srcs != null) {
            int numSources = srcs.size();
            for (int i = 0; i < numSources; ++i) {
                Object src = srcs.get(i);
                if (!(src instanceof PlanarImage)) continue;
                ((PlanarImage)src).removeSink(this);
            }
        }
    }

    protected void finalize() throws Throwable {
        this.dispose();
    }

    private void printBounds() {
        System.out.println("Bounds: [x=" + this.getMinX() + ", y=" + this.getMinY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + "]");
    }

    private void printTile(int i, int j) {
        int xmin = i * this.getTileWidth() + this.getTileGridXOffset();
        int ymin = j * this.getTileHeight() + this.getTileGridYOffset();
        Rectangle imageBounds = this.getBounds();
        Rectangle tileBounds = new Rectangle(xmin, ymin, this.getTileWidth(), this.getTileHeight());
        tileBounds = tileBounds.intersection(imageBounds);
        Raster tile = this.getTile(i, j);
        Rectangle realTileBounds = new Rectangle(tile.getMinX(), tile.getMinY(), tile.getWidth(), tile.getHeight());
        System.out.println("Tile bounds (actual)   = " + realTileBounds);
        System.out.println("Tile bounds (computed) = " + tileBounds);
        xmin = tileBounds.x;
        ymin = tileBounds.y;
        int xmax = tileBounds.x + tileBounds.width - 1;
        int ymax = tileBounds.y + tileBounds.height - 1;
        int numBands = this.getSampleModel().getNumBands();
        int[] val = new int[numBands];
        for (int pj = ymin; pj <= ymax; ++pj) {
            for (int pi = xmin; pi <= xmax; ++pi) {
                tile.getPixel(pi, pj, val);
                if (numBands == 1) {
                    System.out.print("(" + val[0] + ") ");
                    continue;
                }
                if (numBands != 3) continue;
                System.out.print("(" + val[0] + "," + val[1] + "," + val[2] + ") ");
            }
            System.out.println();
        }
    }

    public String toString() {
        return "PlanarImage[minX=" + this.minX + " minY=" + this.minY + " width=" + this.width + " height=" + this.height + " tileGridXOffset=" + this.tileGridXOffset + " tileGridYOffset=" + this.tileGridYOffset + " tileWidth=" + this.tileWidth + " tileHeight=" + this.tileHeight + " sampleModel=" + this.sampleModel + " colorModel=" + this.colorModel + "]";
    }

    private void cobbleByte(Rectangle bounds, Raster dstRaster) {
        ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
        int startX = this.XToTileX(bounds.x);
        int startY = this.YToTileY(bounds.y);
        int rectXend = bounds.x + bounds.width - 1;
        int rectYend = bounds.y + bounds.height - 1;
        int endX = this.XToTileX(rectXend);
        int endY = this.YToTileY(rectYend);
        DataBufferByte dstDB = (DataBufferByte)dstRaster.getDataBuffer();
        byte[] dst = dstDB.getData();
        int dstPS = dstSM.getPixelStride();
        int dstSS = dstSM.getScanlineStride();
        boolean tileParamsSet = false;
        ComponentSampleModel srcSM = null;
        int srcPS = 0;
        int srcSS = 0;
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                int row;
                int xOrg;
                int yOrg;
                Raster tile = this.getTile(x, y);
                if (tile == null) continue;
                if (!tileParamsSet) {
                    srcSM = (ComponentSampleModel)tile.getSampleModel();
                    srcPS = srcSM.getPixelStride();
                    srcSS = srcSM.getScanlineStride();
                    tileParamsSet = true;
                }
                int srcY1 = yOrg = y * this.tileHeight + this.tileGridYOffset;
                int srcY2 = srcY1 + this.tileHeight - 1;
                if (bounds.y > srcY1) {
                    srcY1 = bounds.y;
                }
                if (rectYend < srcY2) {
                    srcY2 = rectYend;
                }
                int srcH = srcY2 - srcY1 + 1;
                int srcX1 = xOrg = x * this.tileWidth + this.tileGridXOffset;
                int srcX2 = srcX1 + this.tileWidth - 1;
                if (bounds.x > srcX1) {
                    srcX1 = bounds.x;
                }
                if (rectXend < srcX2) {
                    srcX2 = rectXend;
                }
                int srcW = srcX2 - srcX1 + 1;
                int dstX = srcX1 - bounds.x;
                int dstY = srcY1 - bounds.y;
                DataBufferByte srcDB = (DataBufferByte)tile.getDataBuffer();
                byte[] src = srcDB.getData();
                int nsamps = srcW * srcPS;
                boolean useArrayCopy = nsamps >= 64;
                int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
                int yDstIdx = dstY * dstSS + dstX * dstPS;
                if (useArrayCopy) {
                    for (row = 0; row < srcH; ++row) {
                        System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
                        ySrcIdx += srcSS;
                        yDstIdx += dstSS;
                    }
                    continue;
                }
                for (row = 0; row < srcH; ++row) {
                    int xSrcIdx = ySrcIdx;
                    int xDstIdx = yDstIdx;
                    int xEnd = xDstIdx + nsamps;
                    while (xDstIdx < xEnd) {
                        dst[xDstIdx++] = src[xSrcIdx++];
                    }
                    ySrcIdx += srcSS;
                    yDstIdx += dstSS;
                }
            }
        }
    }

    private void cobbleShort(Rectangle bounds, Raster dstRaster) {
        ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
        int startX = this.XToTileX(bounds.x);
        int startY = this.YToTileY(bounds.y);
        int rectXend = bounds.x + bounds.width - 1;
        int rectYend = bounds.y + bounds.height - 1;
        int endX = this.XToTileX(rectXend);
        int endY = this.YToTileY(rectYend);
        DataBufferShort dstDB = (DataBufferShort)dstRaster.getDataBuffer();
        short[] dst = dstDB.getData();
        int dstPS = dstSM.getPixelStride();
        int dstSS = dstSM.getScanlineStride();
        boolean tileParamsSet = false;
        ComponentSampleModel srcSM = null;
        int srcPS = 0;
        int srcSS = 0;
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                int row;
                int xOrg;
                int yOrg;
                Raster tile = this.getTile(x, y);
                if (tile == null) continue;
                if (!tileParamsSet) {
                    srcSM = (ComponentSampleModel)tile.getSampleModel();
                    srcPS = srcSM.getPixelStride();
                    srcSS = srcSM.getScanlineStride();
                    tileParamsSet = true;
                }
                int srcY1 = yOrg = y * this.tileHeight + this.tileGridYOffset;
                int srcY2 = srcY1 + this.tileHeight - 1;
                if (bounds.y > srcY1) {
                    srcY1 = bounds.y;
                }
                if (rectYend < srcY2) {
                    srcY2 = rectYend;
                }
                int srcH = srcY2 - srcY1 + 1;
                int srcX1 = xOrg = x * this.tileWidth + this.tileGridXOffset;
                int srcX2 = srcX1 + this.tileWidth - 1;
                if (bounds.x > srcX1) {
                    srcX1 = bounds.x;
                }
                if (rectXend < srcX2) {
                    srcX2 = rectXend;
                }
                int srcW = srcX2 - srcX1 + 1;
                int dstX = srcX1 - bounds.x;
                int dstY = srcY1 - bounds.y;
                DataBufferShort srcDB = (DataBufferShort)tile.getDataBuffer();
                short[] src = srcDB.getData();
                int nsamps = srcW * srcPS;
                boolean useArrayCopy = nsamps >= 64;
                int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
                int yDstIdx = dstY * dstSS + dstX * dstPS;
                if (useArrayCopy) {
                    for (row = 0; row < srcH; ++row) {
                        System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
                        ySrcIdx += srcSS;
                        yDstIdx += dstSS;
                    }
                    continue;
                }
                for (row = 0; row < srcH; ++row) {
                    int xSrcIdx = ySrcIdx;
                    int xDstIdx = yDstIdx;
                    int xEnd = xDstIdx + nsamps;
                    while (xDstIdx < xEnd) {
                        dst[xDstIdx++] = src[xSrcIdx++];
                    }
                    ySrcIdx += srcSS;
                    yDstIdx += dstSS;
                }
            }
        }
    }

    private void cobbleUShort(Rectangle bounds, Raster dstRaster) {
        ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
        int startX = this.XToTileX(bounds.x);
        int startY = this.YToTileY(bounds.y);
        int rectXend = bounds.x + bounds.width - 1;
        int rectYend = bounds.y + bounds.height - 1;
        int endX = this.XToTileX(rectXend);
        int endY = this.YToTileY(rectYend);
        DataBufferUShort dstDB = (DataBufferUShort)dstRaster.getDataBuffer();
        short[] dst = dstDB.getData();
        int dstPS = dstSM.getPixelStride();
        int dstSS = dstSM.getScanlineStride();
        boolean tileParamsSet = false;
        ComponentSampleModel srcSM = null;
        int srcPS = 0;
        int srcSS = 0;
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                int row;
                int xOrg;
                int yOrg;
                Raster tile = this.getTile(x, y);
                if (tile == null) continue;
                if (!tileParamsSet) {
                    srcSM = (ComponentSampleModel)tile.getSampleModel();
                    srcPS = srcSM.getPixelStride();
                    srcSS = srcSM.getScanlineStride();
                    tileParamsSet = true;
                }
                int srcY1 = yOrg = y * this.tileHeight + this.tileGridYOffset;
                int srcY2 = srcY1 + this.tileHeight - 1;
                if (bounds.y > srcY1) {
                    srcY1 = bounds.y;
                }
                if (rectYend < srcY2) {
                    srcY2 = rectYend;
                }
                int srcH = srcY2 - srcY1 + 1;
                int srcX1 = xOrg = x * this.tileWidth + this.tileGridXOffset;
                int srcX2 = srcX1 + this.tileWidth - 1;
                if (bounds.x > srcX1) {
                    srcX1 = bounds.x;
                }
                if (rectXend < srcX2) {
                    srcX2 = rectXend;
                }
                int srcW = srcX2 - srcX1 + 1;
                int dstX = srcX1 - bounds.x;
                int dstY = srcY1 - bounds.y;
                DataBufferUShort srcDB = (DataBufferUShort)tile.getDataBuffer();
                short[] src = srcDB.getData();
                int nsamps = srcW * srcPS;
                boolean useArrayCopy = nsamps >= 64;
                int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
                int yDstIdx = dstY * dstSS + dstX * dstPS;
                if (useArrayCopy) {
                    for (row = 0; row < srcH; ++row) {
                        System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
                        ySrcIdx += srcSS;
                        yDstIdx += dstSS;
                    }
                    continue;
                }
                for (row = 0; row < srcH; ++row) {
                    int xSrcIdx = ySrcIdx;
                    int xDstIdx = yDstIdx;
                    int xEnd = xDstIdx + nsamps;
                    while (xDstIdx < xEnd) {
                        dst[xDstIdx++] = src[xSrcIdx++];
                    }
                    ySrcIdx += srcSS;
                    yDstIdx += dstSS;
                }
            }
        }
    }

    private void cobbleInt(Rectangle bounds, Raster dstRaster) {
        ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
        int startX = this.XToTileX(bounds.x);
        int startY = this.YToTileY(bounds.y);
        int rectXend = bounds.x + bounds.width - 1;
        int rectYend = bounds.y + bounds.height - 1;
        int endX = this.XToTileX(rectXend);
        int endY = this.YToTileY(rectYend);
        DataBufferInt dstDB = (DataBufferInt)dstRaster.getDataBuffer();
        int[] dst = dstDB.getData();
        int dstPS = dstSM.getPixelStride();
        int dstSS = dstSM.getScanlineStride();
        boolean tileParamsSet = false;
        ComponentSampleModel srcSM = null;
        int srcPS = 0;
        int srcSS = 0;
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                int row;
                int xOrg;
                int yOrg;
                Raster tile = this.getTile(x, y);
                if (tile == null) continue;
                if (!tileParamsSet) {
                    srcSM = (ComponentSampleModel)tile.getSampleModel();
                    srcPS = srcSM.getPixelStride();
                    srcSS = srcSM.getScanlineStride();
                    tileParamsSet = true;
                }
                int srcY1 = yOrg = y * this.tileHeight + this.tileGridYOffset;
                int srcY2 = srcY1 + this.tileHeight - 1;
                if (bounds.y > srcY1) {
                    srcY1 = bounds.y;
                }
                if (rectYend < srcY2) {
                    srcY2 = rectYend;
                }
                int srcH = srcY2 - srcY1 + 1;
                int srcX1 = xOrg = x * this.tileWidth + this.tileGridXOffset;
                int srcX2 = srcX1 + this.tileWidth - 1;
                if (bounds.x > srcX1) {
                    srcX1 = bounds.x;
                }
                if (rectXend < srcX2) {
                    srcX2 = rectXend;
                }
                int srcW = srcX2 - srcX1 + 1;
                int dstX = srcX1 - bounds.x;
                int dstY = srcY1 - bounds.y;
                DataBufferInt srcDB = (DataBufferInt)tile.getDataBuffer();
                int[] src = srcDB.getData();
                int nsamps = srcW * srcPS;
                boolean useArrayCopy = nsamps >= 64;
                int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
                int yDstIdx = dstY * dstSS + dstX * dstPS;
                if (useArrayCopy) {
                    for (row = 0; row < srcH; ++row) {
                        System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
                        ySrcIdx += srcSS;
                        yDstIdx += dstSS;
                    }
                    continue;
                }
                for (row = 0; row < srcH; ++row) {
                    int xSrcIdx = ySrcIdx;
                    int xDstIdx = yDstIdx;
                    int xEnd = xDstIdx + nsamps;
                    while (xDstIdx < xEnd) {
                        dst[xDstIdx++] = src[xSrcIdx++];
                    }
                    ySrcIdx += srcSS;
                    yDstIdx += dstSS;
                }
            }
        }
    }

    private void cobbleFloat(Rectangle bounds, Raster dstRaster) {
        ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
        int startX = this.XToTileX(bounds.x);
        int startY = this.YToTileY(bounds.y);
        int rectXend = bounds.x + bounds.width - 1;
        int rectYend = bounds.y + bounds.height - 1;
        int endX = this.XToTileX(rectXend);
        int endY = this.YToTileY(rectYend);
        DataBuffer dstDB = dstRaster.getDataBuffer();
        float[] dst = DataBufferUtils.getDataFloat(dstDB);
        int dstPS = dstSM.getPixelStride();
        int dstSS = dstSM.getScanlineStride();
        boolean tileParamsSet = false;
        ComponentSampleModel srcSM = null;
        int srcPS = 0;
        int srcSS = 0;
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                int row;
                int xOrg;
                int yOrg;
                Raster tile = this.getTile(x, y);
                if (tile == null) continue;
                if (!tileParamsSet) {
                    srcSM = (ComponentSampleModel)tile.getSampleModel();
                    srcPS = srcSM.getPixelStride();
                    srcSS = srcSM.getScanlineStride();
                    tileParamsSet = true;
                }
                int srcY1 = yOrg = y * this.tileHeight + this.tileGridYOffset;
                int srcY2 = srcY1 + this.tileHeight - 1;
                if (bounds.y > srcY1) {
                    srcY1 = bounds.y;
                }
                if (rectYend < srcY2) {
                    srcY2 = rectYend;
                }
                int srcH = srcY2 - srcY1 + 1;
                int srcX1 = xOrg = x * this.tileWidth + this.tileGridXOffset;
                int srcX2 = srcX1 + this.tileWidth - 1;
                if (bounds.x > srcX1) {
                    srcX1 = bounds.x;
                }
                if (rectXend < srcX2) {
                    srcX2 = rectXend;
                }
                int srcW = srcX2 - srcX1 + 1;
                int dstX = srcX1 - bounds.x;
                int dstY = srcY1 - bounds.y;
                DataBuffer srcDB = tile.getDataBuffer();
                float[] src = DataBufferUtils.getDataFloat(srcDB);
                int nsamps = srcW * srcPS;
                boolean useArrayCopy = nsamps >= 64;
                int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
                int yDstIdx = dstY * dstSS + dstX * dstPS;
                if (useArrayCopy) {
                    for (row = 0; row < srcH; ++row) {
                        System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
                        ySrcIdx += srcSS;
                        yDstIdx += dstSS;
                    }
                    continue;
                }
                for (row = 0; row < srcH; ++row) {
                    int xSrcIdx = ySrcIdx;
                    int xDstIdx = yDstIdx;
                    int xEnd = xDstIdx + nsamps;
                    while (xDstIdx < xEnd) {
                        dst[xDstIdx++] = src[xSrcIdx++];
                    }
                    ySrcIdx += srcSS;
                    yDstIdx += dstSS;
                }
            }
        }
    }

    private void cobbleDouble(Rectangle bounds, Raster dstRaster) {
        ComponentSampleModel dstSM = (ComponentSampleModel)dstRaster.getSampleModel();
        int startX = this.XToTileX(bounds.x);
        int startY = this.YToTileY(bounds.y);
        int rectXend = bounds.x + bounds.width - 1;
        int rectYend = bounds.y + bounds.height - 1;
        int endX = this.XToTileX(rectXend);
        int endY = this.YToTileY(rectYend);
        DataBuffer dstDB = dstRaster.getDataBuffer();
        double[] dst = DataBufferUtils.getDataDouble(dstDB);
        int dstPS = dstSM.getPixelStride();
        int dstSS = dstSM.getScanlineStride();
        boolean tileParamsSet = false;
        ComponentSampleModel srcSM = null;
        int srcPS = 0;
        int srcSS = 0;
        for (int y = startY; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                int row;
                int xOrg;
                int yOrg;
                Raster tile = this.getTile(x, y);
                if (tile == null) continue;
                if (!tileParamsSet) {
                    srcSM = (ComponentSampleModel)tile.getSampleModel();
                    srcPS = srcSM.getPixelStride();
                    srcSS = srcSM.getScanlineStride();
                    tileParamsSet = true;
                }
                int srcY1 = yOrg = y * this.tileHeight + this.tileGridYOffset;
                int srcY2 = srcY1 + this.tileHeight - 1;
                if (bounds.y > srcY1) {
                    srcY1 = bounds.y;
                }
                if (rectYend < srcY2) {
                    srcY2 = rectYend;
                }
                int srcH = srcY2 - srcY1 + 1;
                int srcX1 = xOrg = x * this.tileWidth + this.tileGridXOffset;
                int srcX2 = srcX1 + this.tileWidth - 1;
                if (bounds.x > srcX1) {
                    srcX1 = bounds.x;
                }
                if (rectXend < srcX2) {
                    srcX2 = rectXend;
                }
                int srcW = srcX2 - srcX1 + 1;
                int dstX = srcX1 - bounds.x;
                int dstY = srcY1 - bounds.y;
                DataBuffer srcDB = tile.getDataBuffer();
                double[] src = DataBufferUtils.getDataDouble(srcDB);
                int nsamps = srcW * srcPS;
                boolean useArrayCopy = nsamps >= 64;
                int ySrcIdx = (srcY1 - yOrg) * srcSS + (srcX1 - xOrg) * srcPS;
                int yDstIdx = dstY * dstSS + dstX * dstPS;
                if (useArrayCopy) {
                    for (row = 0; row < srcH; ++row) {
                        System.arraycopy(src, ySrcIdx, dst, yDstIdx, nsamps);
                        ySrcIdx += srcSS;
                        yDstIdx += dstSS;
                    }
                    continue;
                }
                for (row = 0; row < srcH; ++row) {
                    int xSrcIdx = ySrcIdx;
                    int xDstIdx = yDstIdx;
                    int xEnd = xDstIdx + nsamps;
                    while (xDstIdx < xEnd) {
                        dst[xDstIdx++] = src[xSrcIdx++];
                    }
                    ySrcIdx += srcSS;
                    yDstIdx += dstSS;
                }
            }
        }
    }

    public Object getImageID() {
        return this.UID;
    }
}

