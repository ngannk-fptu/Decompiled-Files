/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.IntegerSequence;
import javax.media.jai.JaiI18N;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;

public abstract class PointOpImage
extends OpImage {
    private boolean isDisposed = false;
    private boolean areFieldsInitialized = false;
    private boolean checkInPlaceOperation = false;
    private boolean isInPlaceEnabled = false;
    private WritableRenderedImage source0AsWritableRenderedImage;
    private OpImage source0AsOpImage;
    private boolean source0IsWritableRenderedImage;
    private boolean sameBounds;
    private boolean sameTileGrid;

    private static ImageLayout layoutHelper(ImageLayout layout, Vector sources, Map config) {
        int numSources = sources.size();
        if (numSources < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic5"));
        }
        RenderedImage source0 = (RenderedImage)sources.get(0);
        Rectangle isect = new Rectangle(source0.getMinX(), source0.getMinY(), source0.getWidth(), source0.getHeight());
        Rectangle rect = new Rectangle();
        for (int i = 1; i < numSources; ++i) {
            RenderedImage s = (RenderedImage)sources.get(i);
            rect.setBounds(s.getMinX(), s.getMinY(), s.getWidth(), s.getHeight());
            isect = isect.intersection(rect);
        }
        if (isect.isEmpty()) {
            throw new IllegalArgumentException(JaiI18N.getString("PointOpImage0"));
        }
        if (layout == null) {
            layout = new ImageLayout(isect.x, isect.y, isect.width, isect.height);
        } else {
            Rectangle r;
            if (!(layout = (ImageLayout)layout.clone()).isValid(1)) {
                layout.setMinX(isect.x);
            }
            if (!layout.isValid(2)) {
                layout.setMinY(isect.y);
            }
            if (!layout.isValid(4)) {
                layout.setWidth(isect.width);
            }
            if (!layout.isValid(8)) {
                layout.setHeight(isect.height);
            }
            if ((r = new Rectangle(layout.getMinX(null), layout.getMinY(null), layout.getWidth(null), layout.getHeight(null))).isEmpty()) {
                throw new IllegalArgumentException(JaiI18N.getString("PointOpImage1"));
            }
            if (!isect.contains(r)) {
                throw new IllegalArgumentException(JaiI18N.getString("PointOpImage2"));
            }
        }
        if (numSources > 1 && !layout.isValid(256)) {
            SampleModel sm0;
            SampleModel sm = source0.getSampleModel();
            ColorModel cm = source0.getColorModel();
            int dtype0 = PointOpImage.getAppropriateDataType(sm);
            int bands0 = PointOpImage.getBandCount(sm, cm);
            int dtype = dtype0;
            int bands = bands0;
            for (int i = 1; i < numSources; ++i) {
                RenderedImage source = (RenderedImage)sources.get(i);
                sm = source.getSampleModel();
                cm = source.getColorModel();
                int sourceBands = PointOpImage.getBandCount(sm, cm);
                dtype = PointOpImage.mergeTypes(dtype, PointOpImage.getPixelType(sm));
                bands = Math.min(bands, sourceBands);
            }
            if (dtype == -1 && bands > 1) {
                dtype = 0;
            }
            if (dtype != (sm0 = source0.getSampleModel()).getDataType() || bands != sm0.getNumBands()) {
                int tw = layout.getTileWidth(source0);
                int th = layout.getTileHeight(source0);
                SampleModel sampleModel = dtype == -1 ? new MultiPixelPackedSampleModel(0, tw, th, 1) : RasterFactory.createPixelInterleavedSampleModel(dtype, tw, th, bands);
                layout.setSampleModel(sampleModel);
                if (cm != null && !JDKWorkarounds.areCompatibleDataModels(sampleModel, cm)) {
                    cm = ImageUtil.getCompatibleColorModel(sampleModel, config);
                    layout.setColorModel(cm);
                }
            }
        }
        return layout;
    }

    private static int getPixelType(SampleModel sampleModel) {
        return ImageUtil.isBinary(sampleModel) ? -1 : sampleModel.getDataType();
    }

    private static int getBandCount(SampleModel sampleModel, ColorModel colorModel) {
        if (ImageUtil.isBinary(sampleModel)) {
            return 1;
        }
        if (colorModel instanceof IndexColorModel) {
            return colorModel.getNumComponents();
        }
        return sampleModel.getNumBands();
    }

    private static int getAppropriateDataType(SampleModel sampleModel) {
        int dataType;
        int retVal = dataType = sampleModel.getDataType();
        if (ImageUtil.isBinary(sampleModel)) {
            retVal = -1;
        } else if (dataType == 1 || dataType == 3) {
            boolean canUseBytes = true;
            boolean canUseShorts = true;
            int[] ss = sampleModel.getSampleSize();
            for (int i = 0; i < ss.length; ++i) {
                if (ss[i] > 16) {
                    canUseBytes = false;
                    canUseShorts = false;
                    break;
                }
                if (ss[i] <= 8) continue;
                canUseBytes = false;
            }
            if (canUseBytes) {
                retVal = 0;
            } else if (canUseShorts) {
                retVal = 1;
            }
        }
        return retVal;
    }

    private static int mergeTypes(int type0, int type1) {
        if (type0 == type1) {
            return type0;
        }
        int type = type1;
        switch (type0) {
            case -1: 
            case 0: {
                break;
            }
            case 2: {
                if (type1 == 0) {
                    type = 2;
                    break;
                }
                if (type1 != 1) break;
                type = 3;
                break;
            }
            case 1: {
                if (type1 == 0) {
                    type = 1;
                    break;
                }
                if (type1 != 2) break;
                type = 3;
                break;
            }
            case 3: {
                if (type1 != 0 && type1 != 2 && type1 != 1) break;
                type = 3;
                break;
            }
            case 4: {
                if (type1 == 5) break;
                type = 4;
                break;
            }
            case 5: {
                type = 5;
            }
        }
        return type;
    }

    public PointOpImage(Vector sources, ImageLayout layout, Map configuration, boolean cobbleSources) {
        super(PointOpImage.checkSourceVector(sources, true), PointOpImage.layoutHelper(layout, sources, configuration), configuration, cobbleSources);
    }

    public PointOpImage(RenderedImage source, ImageLayout layout, Map configuration, boolean cobbleSources) {
        this(PointOpImage.vectorize(source), layout, configuration, cobbleSources);
    }

    public PointOpImage(RenderedImage source0, RenderedImage source1, ImageLayout layout, Map configuration, boolean cobbleSources) {
        this(PointOpImage.vectorize(source0, source1), layout, configuration, cobbleSources);
    }

    public PointOpImage(RenderedImage source0, RenderedImage source1, RenderedImage source2, ImageLayout layout, Map configuration, boolean cobbleSources) {
        this(PointOpImage.vectorize(source0, source1, source2), layout, configuration, cobbleSources);
    }

    private synchronized void initializeFields() {
        if (this.areFieldsInitialized) {
            return;
        }
        PlanarImage source0 = this.getSource(0);
        if (this.checkInPlaceOperation) {
            Vector source0Sinks = source0.getSinks();
            boolean bl = this.isInPlaceEnabled = source0 != null && this.getTileGridXOffset() == source0.getTileGridXOffset() && this.getTileGridYOffset() == source0.getTileGridYOffset() && this.getBounds().equals(source0.getBounds()) && source0 instanceof OpImage && this.hasCompatibleSampleModel(source0) && (source0Sinks == null || source0Sinks.size() <= 1);
            if (this.isInPlaceEnabled && !((OpImage)source0).computesUniqueTiles()) {
                this.isInPlaceEnabled = false;
            }
            if (this.isInPlaceEnabled) {
                try {
                    Method getTileMethod = source0.getClass().getMethod("getTile", Integer.TYPE, Integer.TYPE);
                    Class<?> opImageClass = Class.forName("javax.media.jai.OpImage");
                    Class<?> declaringClass = getTileMethod.getDeclaringClass();
                    if (!declaringClass.equals(opImageClass)) {
                        this.isInPlaceEnabled = false;
                    }
                }
                catch (ClassNotFoundException e) {
                    this.isInPlaceEnabled = false;
                }
                catch (NoSuchMethodException e) {
                    this.isInPlaceEnabled = false;
                }
            }
            if (this.isInPlaceEnabled) {
                this.source0IsWritableRenderedImage = source0 instanceof WritableRenderedImage;
                if (this.source0IsWritableRenderedImage) {
                    this.source0AsWritableRenderedImage = (WritableRenderedImage)((Object)source0);
                } else {
                    this.source0AsOpImage = (OpImage)source0;
                }
            }
            this.checkInPlaceOperation = false;
        }
        int numSources = this.getNumSources();
        this.sameBounds = true;
        this.sameTileGrid = true;
        for (int i = 0; i < numSources && (this.sameBounds || this.sameTileGrid); ++i) {
            PlanarImage source = this.getSource(i);
            if (this.sameBounds) {
                boolean bl = this.sameBounds = this.sameBounds && this.minX == source.minX && this.minY == source.minY && this.width == source.width && this.height == source.height;
            }
            if (!this.sameTileGrid) continue;
            this.sameTileGrid = this.sameTileGrid && this.tileGridXOffset == source.tileGridXOffset && this.tileGridYOffset == source.tileGridYOffset && this.tileWidth == source.tileWidth && this.tileHeight == source.tileHeight;
        }
        this.areFieldsInitialized = true;
    }

    private boolean hasCompatibleSampleModel(PlanarImage src) {
        boolean isCompatible;
        SampleModel srcSM = src.getSampleModel();
        int numBands = this.sampleModel.getNumBands();
        boolean bl = isCompatible = srcSM.getTransferType() == this.sampleModel.getTransferType() && srcSM.getWidth() == this.sampleModel.getWidth() && srcSM.getHeight() == this.sampleModel.getHeight() && srcSM.getNumBands() == numBands && srcSM.getClass().equals(this.sampleModel.getClass());
        if (isCompatible) {
            if (this.sampleModel instanceof ComponentSampleModel) {
                ComponentSampleModel smSrc = (ComponentSampleModel)srcSM;
                ComponentSampleModel smDst = (ComponentSampleModel)this.sampleModel;
                isCompatible = isCompatible && smSrc.getPixelStride() == smDst.getPixelStride() && smSrc.getScanlineStride() == smDst.getScanlineStride();
                int[] biSrc = smSrc.getBankIndices();
                int[] biDst = smDst.getBankIndices();
                int[] boSrc = smSrc.getBandOffsets();
                int[] boDst = smDst.getBandOffsets();
                for (int b = 0; b < numBands && isCompatible; ++b) {
                    isCompatible = isCompatible && biSrc[b] == biDst[b] && boSrc[b] == boDst[b];
                }
            } else if (this.sampleModel instanceof SinglePixelPackedSampleModel) {
                SinglePixelPackedSampleModel smSrc = (SinglePixelPackedSampleModel)srcSM;
                SinglePixelPackedSampleModel smDst = (SinglePixelPackedSampleModel)this.sampleModel;
                isCompatible = isCompatible && smSrc.getScanlineStride() == smDst.getScanlineStride();
                int[] bmSrc = smSrc.getBitMasks();
                int[] bmDst = smDst.getBitMasks();
                for (int b = 0; b < numBands && isCompatible; ++b) {
                    isCompatible = isCompatible && bmSrc[b] == bmDst[b];
                }
            } else if (this.sampleModel instanceof MultiPixelPackedSampleModel) {
                MultiPixelPackedSampleModel smSrc = (MultiPixelPackedSampleModel)srcSM;
                MultiPixelPackedSampleModel smDst = (MultiPixelPackedSampleModel)this.sampleModel;
                isCompatible = isCompatible && smSrc.getPixelBitStride() == smDst.getPixelBitStride() && smSrc.getScanlineStride() == smDst.getScanlineStride() && smSrc.getDataBitOffset() == smDst.getDataBitOffset();
            } else {
                isCompatible = false;
            }
        }
        return isCompatible;
    }

    protected void permitInPlaceOperation() {
        Object inPlaceProperty = null;
        try {
            inPlaceProperty = AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    String name = "javax.media.jai.PointOpImage.InPlace";
                    return System.getProperty(name);
                }
            });
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        this.checkInPlaceOperation = inPlaceProperty == null || !(inPlaceProperty instanceof String) || !((String)inPlaceProperty).equalsIgnoreCase("false");
    }

    protected boolean isColormapOperation() {
        return false;
    }

    public Raster computeTile(int tileX, int tileY) {
        int boundsMaxY;
        int boundsMaxX;
        boolean recyclingSource0Tile;
        if (!this.cobbleSources) {
            return super.computeTile(tileX, tileY);
        }
        this.initializeFields();
        WritableRaster dest = null;
        if (this.isInPlaceEnabled) {
            if (this.source0IsWritableRenderedImage) {
                dest = this.source0AsWritableRenderedImage.getWritableTile(tileX, tileY);
            } else {
                Raster raster = this.source0AsOpImage.getTileFromCache(tileX, tileY);
                if (raster == null) {
                    try {
                        raster = this.source0AsOpImage.computeTile(tileX, tileY);
                        if (raster instanceof WritableRaster) {
                            dest = (WritableRaster)raster;
                        }
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                }
            }
        }
        boolean bl = recyclingSource0Tile = dest != null;
        if (!recyclingSource0Tile) {
            Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
            dest = this.createWritableRaster(this.sampleModel, org);
        }
        if (this.isColormapOperation()) {
            if (!recyclingSource0Tile) {
                PlanarImage src = this.getSource(0);
                Raster srcTile = null;
                Rectangle srcRect = null;
                Rectangle dstRect = dest.getBounds();
                if (this.sameTileGrid) {
                    srcTile = this.getSource(0).getTile(tileX, tileY);
                } else if (dstRect.intersects(src.getBounds())) {
                    srcTile = src.getData(dstRect);
                } else {
                    return dest;
                }
                srcRect = srcTile.getBounds();
                if (!dstRect.contains(srcRect)) {
                    srcRect = dstRect.intersection(srcRect);
                    srcTile = srcTile.createChild(srcTile.getMinX(), srcTile.getMinY(), srcRect.width, srcRect.height, srcRect.x, srcRect.y, null);
                }
                JDKWorkarounds.setRect(dest, srcTile, 0, 0);
            }
            return dest;
        }
        int destMinX = dest.getMinX();
        int destMinY = dest.getMinY();
        int destMaxX = destMinX + dest.getWidth();
        int destMaxY = destMinY + dest.getHeight();
        Rectangle bounds = this.getBounds();
        if (destMinX < bounds.x) {
            destMinX = bounds.x;
        }
        if (destMaxX > (boundsMaxX = bounds.x + bounds.width)) {
            destMaxX = boundsMaxX;
        }
        if (destMinY < bounds.y) {
            destMinY = bounds.y;
        }
        if (destMaxY > (boundsMaxY = bounds.y + bounds.height)) {
            destMaxY = boundsMaxY;
        }
        int numSrcs = this.getNumSources();
        if (recyclingSource0Tile && numSrcs == 1) {
            Raster[] sources = new Raster[]{dest};
            Rectangle destRect = new Rectangle(destMinX, destMinY, destMaxX - destMinX, destMaxY - destMinY);
            this.computeRect(sources, dest, destRect);
        } else if (recyclingSource0Tile && this.sameBounds && this.sameTileGrid) {
            Raster[] sources = new Raster[numSrcs];
            sources[0] = dest;
            for (int i = 1; i < numSrcs; ++i) {
                sources[i] = this.getSource(i).getTile(tileX, tileY);
            }
            Rectangle destRect = new Rectangle(destMinX, destMinY, destMaxX - destMinX, destMaxY - destMinY);
            this.computeRect(sources, dest, destRect);
        } else {
            if (!this.sameBounds) {
                int i;
                int n = i = recyclingSource0Tile ? 1 : 0;
                while (i < numSrcs) {
                    bounds = this.getSource(i).getBounds();
                    if (destMinX < bounds.x) {
                        destMinX = bounds.x;
                    }
                    if (destMaxX > (boundsMaxX = bounds.x + bounds.width)) {
                        destMaxX = boundsMaxX;
                    }
                    if (destMinY < bounds.y) {
                        destMinY = bounds.y;
                    }
                    if (destMaxY > (boundsMaxY = bounds.y + bounds.height)) {
                        destMaxY = boundsMaxY;
                    }
                    if (destMinX >= destMaxX || destMinY >= destMaxY) {
                        return dest;
                    }
                    ++i;
                }
            }
            Rectangle destRect = new Rectangle(destMinX, destMinY, destMaxX - destMinX, destMaxY - destMinY);
            Raster[] sources = new Raster[numSrcs];
            if (this.sameTileGrid) {
                int i;
                if (recyclingSource0Tile) {
                    sources[0] = dest;
                }
                int n = i = recyclingSource0Tile ? 1 : 0;
                while (i < numSrcs) {
                    sources[i] = this.getSource(i).getTile(tileX, tileY);
                    ++i;
                }
                this.computeRect(sources, dest, destRect);
            } else {
                int i;
                IntegerSequence xSplits = new IntegerSequence(destMinX, destMaxX);
                xSplits.insert(destMinX);
                xSplits.insert(destMaxX);
                IntegerSequence ySplits = new IntegerSequence(destMinY, destMaxY);
                ySplits.insert(destMinY);
                ySplits.insert(destMaxY);
                int n = i = recyclingSource0Tile ? 1 : 0;
                while (i < numSrcs) {
                    PlanarImage s = this.getSource(i);
                    s.getSplits(xSplits, ySplits, destRect);
                    ++i;
                }
                Rectangle subRect = new Rectangle();
                ySplits.startEnumeration();
                int y1 = ySplits.nextElement();
                while (ySplits.hasMoreElements()) {
                    int y2 = ySplits.nextElement();
                    int h = y2 - y1;
                    xSplits.startEnumeration();
                    int x1 = xSplits.nextElement();
                    while (xSplits.hasMoreElements()) {
                        int i2;
                        int x2 = xSplits.nextElement();
                        int w = x2 - x1;
                        if (recyclingSource0Tile) {
                            sources[0] = dest;
                        }
                        int n2 = i2 = recyclingSource0Tile ? 1 : 0;
                        while (i2 < numSrcs) {
                            PlanarImage s = this.getSource(i2);
                            int tx = s.XToTileX(x1);
                            int ty = s.YToTileY(y1);
                            sources[i2] = s.getTile(tx, ty);
                            ++i2;
                        }
                        subRect.x = x1;
                        subRect.y = y1;
                        subRect.width = w;
                        subRect.height = h;
                        this.computeRect(sources, dest, subRect);
                        x1 = x2;
                    }
                    y1 = y2;
                }
            }
        }
        if (recyclingSource0Tile && this.source0IsWritableRenderedImage) {
            this.source0AsWritableRenderedImage.releaseWritableTile(tileX, tileY);
        }
        return dest;
    }

    public final Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        return new Rectangle(sourceRect);
    }

    public final Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        return new Rectangle(destRect);
    }

    public synchronized void dispose() {
        if (this.isDisposed) {
            return;
        }
        this.isDisposed = true;
        if (this.cache != null && this.isInPlaceEnabled && this.tileRecycler != null) {
            this.cache.removeTiles(this);
        }
        super.dispose();
    }
}

