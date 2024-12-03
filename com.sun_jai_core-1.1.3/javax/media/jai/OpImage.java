/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.AreaOpImage;
import javax.media.jai.ColorModelFactory;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.TileCache;
import javax.media.jai.TileComputationListener;
import javax.media.jai.TileRecycler;
import javax.media.jai.TileRequest;
import javax.media.jai.TileScheduler;

public abstract class OpImage
extends PlanarImage {
    public static final int OP_COMPUTE_BOUND = 1;
    public static final int OP_IO_BOUND = 2;
    public static final int OP_NETWORK_BOUND = 3;
    private static final int LAYOUT_MASK_ALL = 1023;
    protected transient TileCache cache;
    protected Object tileCacheMetric;
    private transient TileScheduler scheduler = JAI.getDefaultInstance().getTileScheduler();
    private boolean isSunTileScheduler = false;
    protected boolean cobbleSources;
    private boolean isDisposed = false;
    private boolean isCachedTileRecyclingEnabled = false;
    protected TileRecycler tileRecycler;
    private RasterFormatTag[] formatTags = null;

    private static ImageLayout layoutHelper(ImageLayout layout, Vector sources, Map config) {
        Dimension defaultTileSize;
        SampleModel derivedSM;
        RenderedImage im;
        ImageLayout il = layout;
        if (sources != null) {
            OpImage.checkSourceVector(sources, true);
        }
        RenderedImage renderedImage = im = sources != null && sources.size() > 0 && sources.firstElement() instanceof RenderedImage ? (RenderedImage)sources.firstElement() : null;
        if (im != null) {
            if (layout == null) {
                il = layout = new ImageLayout(im);
                il.unsetValid(512);
            } else {
                il = new ImageLayout(layout.getMinX(im), layout.getMinY(im), layout.getWidth(im), layout.getHeight(im), layout.getTileGridXOffset(im), layout.getTileGridYOffset(im), layout.getTileWidth(im), layout.getTileHeight(im), layout.getSampleModel(im), null);
            }
            if (layout.isValid(512) && layout.getColorModel(null) == null) {
                il.setColorModel(null);
            } else if (il.getSampleModel(null) != null) {
                ColorModel cmSource;
                SampleModel sm = il.getSampleModel(null);
                ColorModel cmLayout = layout.getColorModel(null);
                if (cmLayout != null) {
                    if (JDKWorkarounds.areCompatibleDataModels(sm, cmLayout)) {
                        il.setColorModel(cmLayout);
                    } else if (layout.getSampleModel(null) == null) {
                        il.setColorModel(cmLayout);
                        derivedSM = cmLayout.createCompatibleSampleModel(il.getTileWidth(null), il.getTileHeight(null));
                        il.setSampleModel(derivedSM);
                    }
                }
                if (!il.isValid(512) && !OpImage.setColorModelFromFactory(sm, sources, config, il) && (cmSource = im.getColorModel()) != null && JDKWorkarounds.areCompatibleDataModels(sm, cmSource)) {
                    if (cmSource != null && cmSource instanceof IndexColorModel && config != null && config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL) && ((Boolean)config.get(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)).booleanValue()) {
                        ColorModel newCM = PlanarImage.getDefaultColorModel(sm.getDataType(), cmSource.getNumComponents());
                        SampleModel newSM = newCM != null ? newCM.createCompatibleSampleModel(il.getTileWidth(null), il.getTileHeight(null)) : RasterFactory.createPixelInterleavedSampleModel(sm.getDataType(), il.getTileWidth(null), il.getTileHeight(null), cmSource.getNumComponents());
                        il.setSampleModel(newSM);
                        if (newCM != null) {
                            il.setColorModel(newCM);
                        }
                    } else {
                        il.setColorModel(cmSource);
                    }
                }
            } else if (il.getSampleModel(null) == null) {
                il.setColorModel(layout.getColorModel(im));
            }
        } else if (il != null && (il = (ImageLayout)layout.clone()).getColorModel(null) != null && il.getSampleModel(null) == null) {
            int smHeight;
            int smWidth = il.getTileWidth(null);
            if (smWidth == 0) {
                smWidth = 512;
            }
            if ((smHeight = il.getTileHeight(null)) == 0) {
                smHeight = 512;
            }
            derivedSM = il.getColorModel(null).createCompatibleSampleModel(smWidth, smHeight);
            il.setSampleModel(derivedSM);
        }
        if (il != null && !il.isValid(512) && il.getSampleModel(null) != null && !OpImage.setColorModelFromFactory(il.getSampleModel(null), sources, config, il)) {
            ColorModel cm = null;
            SampleModel srcSM = il.getSampleModel(null);
            if (im != null && im.getColorModel() != null && im.getColorModel() instanceof IndexColorModel && config != null && config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL) && ((Boolean)config.get(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)).booleanValue()) {
                IndexColorModel icm = (IndexColorModel)im.getColorModel();
                cm = PlanarImage.getDefaultColorModel(srcSM.getDataType(), icm.getNumComponents());
                SampleModel newSM = cm != null ? cm.createCompatibleSampleModel(srcSM.getWidth(), srcSM.getHeight()) : RasterFactory.createPixelInterleavedSampleModel(srcSM.getDataType(), srcSM.getWidth(), srcSM.getHeight(), icm.getNumComponents());
                il.setSampleModel(newSM);
            } else {
                cm = ImageUtil.getCompatibleColorModel(il.getSampleModel(null), config);
            }
            if (cm != null) {
                il.setColorModel(cm);
            }
        }
        if (layout != null && il != null && !layout.isValid(192) && (defaultTileSize = JAI.getDefaultTileSize()) != null) {
            if (!layout.isValid(64)) {
                if (il.getTileWidth(null) <= 0) {
                    il.setTileWidth(defaultTileSize.width);
                } else {
                    int numX = OpImage.XToTileX(il.getMinX(null) + il.getWidth(null) - 1, il.getTileGridXOffset(null), il.getTileWidth(null)) - OpImage.XToTileX(il.getMinX(null), il.getTileGridXOffset(null), il.getTileWidth(null)) + 1;
                    if (numX <= 1 && il.getWidth(null) >= 2 * defaultTileSize.width) {
                        il.setTileWidth(defaultTileSize.width);
                    }
                }
            }
            if (!layout.isValid(128)) {
                if (il.getTileHeight(null) <= 0) {
                    il.setTileHeight(defaultTileSize.height);
                } else {
                    int numY = OpImage.YToTileY(il.getMinY(null) + il.getHeight(null) - 1, il.getTileGridYOffset(null), il.getTileHeight(null)) - OpImage.YToTileY(il.getMinY(null), il.getTileGridYOffset(null), il.getTileHeight(null)) + 1;
                    if (numY <= 1 && il.getHeight(null) >= 2 * defaultTileSize.height) {
                        il.setTileHeight(defaultTileSize.height);
                    }
                }
            }
        }
        if ((layout == null || !layout.isValid(64)) && il.isValid(68) && il.getTileWidth(null) > il.getWidth(null)) {
            il.setTileWidth(il.getWidth(null));
        }
        if ((layout == null || !layout.isValid(128)) && il.isValid(136) && il.getTileHeight(null) > il.getHeight(null)) {
            il.setTileHeight(il.getHeight(null));
        }
        return il;
    }

    private static boolean setColorModelFromFactory(SampleModel sampleModel, Vector sources, Map config, ImageLayout layout) {
        ColorModelFactory cmf;
        ColorModel cm;
        boolean isColorModelSet = false;
        if (config != null && config.containsKey(JAI.KEY_COLOR_MODEL_FACTORY) && (cm = (cmf = (ColorModelFactory)config.get(JAI.KEY_COLOR_MODEL_FACTORY)).createColorModel(sampleModel, sources, config)) != null && JDKWorkarounds.areCompatibleDataModels(sampleModel, cm)) {
            layout.setColorModel(cm);
            isColorModelSet = true;
        }
        return isColorModelSet;
    }

    public OpImage(Vector sources, ImageLayout layout, Map configuration, boolean cobbleSources) {
        super(OpImage.layoutHelper(layout, sources, configuration), sources, configuration);
        if (configuration != null) {
            Object recyclerValue;
            Object schedulerConfig;
            Object cacheConfig = configuration.get(JAI.KEY_TILE_CACHE);
            if (cacheConfig != null && cacheConfig instanceof TileCache && ((TileCache)cacheConfig).getMemoryCapacity() > 0L) {
                this.cache = (TileCache)cacheConfig;
            }
            if ((schedulerConfig = configuration.get(JAI.KEY_TILE_SCHEDULER)) != null && schedulerConfig instanceof TileScheduler) {
                this.scheduler = (TileScheduler)schedulerConfig;
            }
            try {
                Class<?> sunScheduler = Class.forName("com.sun.media.jai.util.SunTileScheduler");
                this.isSunTileScheduler = sunScheduler.isInstance(this.scheduler);
            }
            catch (Exception e) {
                // empty catch block
            }
            this.tileCacheMetric = configuration.get(JAI.KEY_TILE_CACHE_METRIC);
            Object recyclingEnabledValue = configuration.get(JAI.KEY_CACHED_TILE_RECYCLING_ENABLED);
            if (recyclingEnabledValue instanceof Boolean) {
                this.isCachedTileRecyclingEnabled = (Boolean)recyclingEnabledValue;
            }
            if ((recyclerValue = configuration.get(JAI.KEY_TILE_RECYCLER)) instanceof TileRecycler) {
                this.tileRecycler = (TileRecycler)recyclerValue;
            }
        }
        this.cobbleSources = cobbleSources;
    }

    protected static Vector vectorize(RenderedImage image) {
        if (image == null) {
            throw new IllegalArgumentException(JaiI18N.getString("OpImage3"));
        }
        Vector<RenderedImage> v = new Vector<RenderedImage>(1);
        v.addElement(image);
        return v;
    }

    protected static Vector vectorize(RenderedImage image1, RenderedImage image2) {
        if (image1 == null || image2 == null) {
            throw new IllegalArgumentException(JaiI18N.getString("OpImage3"));
        }
        Vector<RenderedImage> v = new Vector<RenderedImage>(2);
        v.addElement(image1);
        v.addElement(image2);
        return v;
    }

    protected static Vector vectorize(RenderedImage image1, RenderedImage image2, RenderedImage image3) {
        if (image1 == null || image2 == null || image3 == null) {
            throw new IllegalArgumentException(JaiI18N.getString("OpImage3"));
        }
        Vector<RenderedImage> v = new Vector<RenderedImage>(3);
        v.addElement(image1);
        v.addElement(image2);
        v.addElement(image3);
        return v;
    }

    static Vector checkSourceVector(Vector sources, boolean checkElements) {
        if (sources == null) {
            throw new IllegalArgumentException(JaiI18N.getString("OpImage2"));
        }
        if (checkElements) {
            int numSources = sources.size();
            for (int i = 0; i < numSources; ++i) {
                if (sources.get(i) != null) continue;
                throw new IllegalArgumentException(JaiI18N.getString("OpImage3"));
            }
        }
        return sources;
    }

    public TileCache getTileCache() {
        return this.cache;
    }

    public void setTileCache(TileCache cache) {
        if (this.cache != null) {
            this.cache.removeTiles(this);
        }
        this.cache = cache;
    }

    protected Raster getTileFromCache(int tileX, int tileY) {
        return this.cache != null ? this.cache.getTile(this, tileX, tileY) : null;
    }

    protected void addTileToCache(int tileX, int tileY, Raster tile) {
        if (this.cache != null) {
            this.cache.add(this, tileX, tileY, tile, this.tileCacheMetric);
        }
    }

    public Object getTileCacheMetric() {
        return this.tileCacheMetric;
    }

    public Raster getTile(int tileX, int tileY) {
        Raster tile = null;
        if (tileX >= this.getMinTileX() && tileX <= this.getMaxTileX() && tileY >= this.getMinTileY() && tileY <= this.getMaxTileY() && (tile = this.getTileFromCache(tileX, tileY)) == null) {
            try {
                tile = this.scheduler.scheduleTile(this, tileX, tileY);
            }
            catch (OutOfMemoryError e) {
                if (this.cache != null) {
                    this.cache.flush();
                    System.gc();
                }
                tile = this.scheduler.scheduleTile(this, tileX, tileY);
            }
            this.addTileToCache(tileX, tileY, tile);
        }
        return tile;
    }

    public Raster computeTile(int tileX, int tileY) {
        WritableRaster dest = this.createWritableRaster(this.sampleModel, new Point(this.tileXToX(tileX), this.tileYToY(tileY)));
        Rectangle destRect = this.getTileRect(tileX, tileY);
        int numSources = this.getNumSources();
        if (this.cobbleSources) {
            int i;
            Raster[] rasterSources = new Raster[numSources];
            for (i = 0; i < numSources; ++i) {
                PlanarImage source = this.getSource(i);
                Rectangle srcRect = this.mapDestRect(destRect, i);
                rasterSources[i] = srcRect != null && srcRect.isEmpty() ? null : source.getData(srcRect);
            }
            this.computeRect(rasterSources, dest, destRect);
            for (i = 0; i < numSources; ++i) {
                PlanarImage source;
                Raster sourceData = rasterSources[i];
                if (sourceData == null || !(source = this.getSourceImage(i)).overlapsMultipleTiles(sourceData.getBounds())) continue;
                this.recycleTile(sourceData);
            }
        } else {
            PlanarImage[] imageSources = new PlanarImage[numSources];
            for (int i = 0; i < numSources; ++i) {
                imageSources[i] = this.getSource(i);
            }
            this.computeRect(imageSources, dest, destRect);
        }
        return dest;
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        String className = this.getClass().getName();
        throw new RuntimeException(className + " " + JaiI18N.getString("OpImage0"));
    }

    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        String className = this.getClass().getName();
        throw new RuntimeException(className + " " + JaiI18N.getString("OpImage1"));
    }

    public Point[] getTileDependencies(int tileX, int tileY, int sourceIndex) {
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        Rectangle rect = this.getTileRect(tileX, tileY);
        if (rect.isEmpty()) {
            return null;
        }
        PlanarImage src = this.getSource(sourceIndex);
        Rectangle srcRect = this.mapDestRect(rect, sourceIndex);
        int minTileX = src.XToTileX(srcRect.x);
        int maxTileX = src.XToTileX(srcRect.x + srcRect.width - 1);
        int minTileY = src.YToTileY(srcRect.y);
        int maxTileY = src.YToTileY(srcRect.y + srcRect.height - 1);
        minTileX = Math.max(minTileX, src.getMinTileX());
        maxTileX = Math.min(maxTileX, src.getMaxTileX());
        minTileY = Math.max(minTileY, src.getMinTileY());
        maxTileY = Math.min(maxTileY, src.getMaxTileY());
        int numXTiles = maxTileX - minTileX + 1;
        int numYTiles = maxTileY - minTileY + 1;
        if (numXTiles <= 0 || numYTiles <= 0) {
            return null;
        }
        Point[] ret = new Point[numYTiles * numXTiles];
        int i = 0;
        for (int y = minTileY; y <= maxTileY; ++y) {
            for (int x = minTileX; x <= maxTileX; ++x) {
                ret[i++] = new Point(x, y);
            }
        }
        return ret;
    }

    public Raster[] getTiles(Point[] tileIndices) {
        Raster[] tiles;
        block5: {
            int count;
            boolean[] computeTiles;
            int numTiles;
            block6: {
                int i;
                if (tileIndices == null) {
                    throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
                }
                numTiles = tileIndices.length;
                tiles = new Raster[numTiles];
                computeTiles = new boolean[numTiles];
                int minTileX = this.getMinTileX();
                int maxTileX = this.getMaxTileX();
                int minTileY = this.getMinTileY();
                int maxTileY = this.getMaxTileY();
                count = 0;
                for (i = 0; i < numTiles; ++i) {
                    int tileX = tileIndices[i].x;
                    int tileY = tileIndices[i].y;
                    if (tileX < minTileX || tileX > maxTileX || tileY < minTileY || tileY > maxTileY) continue;
                    tiles[i] = this.getTileFromCache(tileX, tileY);
                    if (tiles[i] != null) continue;
                    computeTiles[i] = true;
                    ++count;
                }
                if (count <= 0) break block5;
                if (count != numTiles) break block6;
                tiles = this.scheduler.scheduleTiles(this, tileIndices);
                if (this.cache == null || this.cache == null) break block5;
                for (i = 0; i < numTiles; ++i) {
                    this.cache.add(this, tileIndices[i].x, tileIndices[i].y, tiles[i], this.tileCacheMetric);
                }
                break block5;
            }
            Point[] indices = new Point[count];
            count = 0;
            for (int i = 0; i < numTiles; ++i) {
                if (!computeTiles[i]) continue;
                indices[count++] = tileIndices[i];
            }
            Raster[] newTiles = this.scheduler.scheduleTiles(this, indices);
            count = 0;
            for (int i = 0; i < numTiles; ++i) {
                if (!computeTiles[i]) continue;
                tiles[i] = newTiles[count++];
                this.addTileToCache(tileIndices[i].x, tileIndices[i].y, tiles[i]);
            }
        }
        return tiles;
    }

    private static TileComputationListener[] prependListener(TileComputationListener[] listeners, TileComputationListener listener) {
        if (listeners == null) {
            return new TileComputationListener[]{listener};
        }
        TileComputationListener[] newListeners = new TileComputationListener[listeners.length + 1];
        newListeners[0] = listener;
        System.arraycopy(listeners, 0, newListeners, 1, listeners.length);
        return newListeners;
    }

    public TileRequest queueTiles(Point[] tileIndices) {
        if (tileIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        TileComputationListener[] tileListeners = this.getTileComputationListeners();
        if (!this.isSunTileScheduler) {
            TCL localListener = new TCL(this);
            tileListeners = OpImage.prependListener(tileListeners, localListener);
        }
        return this.scheduler.scheduleTiles(this, tileIndices, tileListeners);
    }

    public void cancelTiles(TileRequest request, Point[] tileIndices) {
        if (request == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic4"));
        }
        this.scheduler.cancelTiles(request, tileIndices);
    }

    public void prefetchTiles(Point[] tileIndices) {
        if (tileIndices == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (tileIndices == null) {
            return;
        }
        this.scheduler.prefetchTiles(this, tileIndices);
    }

    public Point2D mapDestPoint(Point2D destPt, int sourceIndex) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        Rectangle destRect = new Rectangle((int)destPt.getX(), (int)destPt.getY(), 1, 1);
        Rectangle sourceRect = this.mapDestRect(destRect, sourceIndex);
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation((double)sourceRect.x + ((double)sourceRect.width - 1.0) / 2.0, (double)sourceRect.y + ((double)sourceRect.height - 1.0) / 2.0);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt, int sourceIndex) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IndexOutOfBoundsException(JaiI18N.getString("Generic1"));
        }
        Rectangle sourceRect = new Rectangle((int)sourcePt.getX(), (int)sourcePt.getY(), 1, 1);
        Rectangle destRect = this.mapSourceRect(sourceRect, sourceIndex);
        if (destRect == null) {
            return null;
        }
        Point2D pt = (Point2D)sourcePt.clone();
        pt.setLocation((double)destRect.x + ((double)destRect.width - 1.0) / 2.0, (double)destRect.y + ((double)destRect.height - 1.0) / 2.0);
        return pt;
    }

    public abstract Rectangle mapSourceRect(Rectangle var1, int var2);

    public abstract Rectangle mapDestRect(Rectangle var1, int var2);

    public int getOperationComputeType() {
        return 1;
    }

    public boolean computesUniqueTiles() {
        return true;
    }

    public synchronized void dispose() {
        if (this.isDisposed) {
            return;
        }
        this.isDisposed = true;
        if (this.cache != null) {
            Raster[] tiles;
            if (this.isCachedTileRecyclingEnabled && this.tileRecycler != null && (tiles = this.cache.getTiles(this)) != null) {
                int numTiles = tiles.length;
                for (int i = 0; i < numTiles; ++i) {
                    this.tileRecycler.recycleTile(tiles[i]);
                }
            }
            this.cache.removeTiles(this);
        }
        super.dispose();
    }

    public boolean hasExtender(int sourceIndex) {
        if (sourceIndex != 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (this instanceof AreaOpImage) {
            return ((AreaOpImage)this).getBorderExtender() != null;
        }
        if (this instanceof GeometricOpImage) {
            return ((GeometricOpImage)this).getBorderExtender() != null;
        }
        return false;
    }

    public static int getExpandedNumBands(SampleModel sampleModel, ColorModel colorModel) {
        if (colorModel instanceof IndexColorModel) {
            return colorModel.getNumComponents();
        }
        return sampleModel.getNumBands();
    }

    protected synchronized RasterFormatTag[] getFormatTags() {
        if (this.formatTags == null) {
            RenderedImage[] sourceArray = new RenderedImage[this.getNumSources()];
            if (sourceArray.length > 0) {
                this.getSources().toArray(sourceArray);
            }
            this.formatTags = RasterAccessor.findCompatibleTags(sourceArray, this);
        }
        return this.formatTags;
    }

    public TileRecycler getTileRecycler() {
        return this.tileRecycler;
    }

    protected final WritableRaster createTile(int tileX, int tileY) {
        return this.createWritableRaster(this.sampleModel, new Point(this.tileXToX(tileX), this.tileYToY(tileY)));
    }

    protected void recycleTile(Raster tile) {
        if (tile == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.tileRecycler != null) {
            this.tileRecycler.recycleTile(tile);
        }
    }

    private class TCL
    implements TileComputationListener {
        OpImage opImage;

        private TCL(OpImage opImage) {
            this.opImage = opImage;
        }

        public void tileComputed(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Raster tile) {
            if (image == this.opImage) {
                OpImage.this.addTileToCache(tileX, tileY, tile);
            }
        }

        public void tileCancelled(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY) {
        }

        public void tileComputationFailure(Object eventSource, TileRequest[] requests, PlanarImage image, int tileX, int tileY, Throwable situation) {
        }
    }
}

