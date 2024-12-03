/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.ImageLayout;
import javax.media.jai.JaiI18N;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

public abstract class UntiledOpImage
extends OpImage {
    private static ImageLayout layoutHelper(ImageLayout layout, Vector sources) {
        if (sources.size() < 1) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic5"));
        }
        RenderedImage source = (RenderedImage)sources.get(0);
        ImageLayout il = layout == null ? new ImageLayout() : (ImageLayout)layout.clone();
        il.setTileGridXOffset(il.getMinX(source));
        il.setTileGridYOffset(il.getMinY(source));
        il.setTileWidth(il.getWidth(source));
        il.setTileHeight(il.getHeight(source));
        return il;
    }

    public UntiledOpImage(Vector sources, Map configuration, ImageLayout layout) {
        super(UntiledOpImage.checkSourceVector(sources, true), UntiledOpImage.layoutHelper(layout, sources), configuration, true);
    }

    public UntiledOpImage(RenderedImage source, Map configuration, ImageLayout layout) {
        super(UntiledOpImage.vectorize(source), UntiledOpImage.layoutHelper(layout, UntiledOpImage.vectorize(source)), configuration, true);
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        return this.getBounds();
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic1"));
        }
        return this.getSource(sourceIndex).getBounds();
    }

    public Raster computeTile(int tileX, int tileY) {
        int i;
        Point org = new Point(this.getMinX(), this.getMinY());
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle destRect = this.getBounds();
        int numSources = this.getNumSources();
        Raster[] rasterSources = new Raster[numSources];
        for (i = 0; i < numSources; ++i) {
            PlanarImage source = this.getSource(i);
            Rectangle srcRect = this.mapDestRect(destRect, i);
            rasterSources[i] = source.getData(srcRect);
        }
        this.computeImage(rasterSources, dest, destRect);
        for (i = 0; i < numSources; ++i) {
            PlanarImage source;
            Raster sourceData = rasterSources[i];
            if (sourceData == null || !(source = this.getSourceImage(i)).overlapsMultipleTiles(sourceData.getBounds())) continue;
            this.recycleTile(sourceData);
        }
        return dest;
    }

    protected abstract void computeImage(Raster[] var1, WritableRaster var2, Rectangle var3);

    public Point[] getTileDependencies(int tileX, int tileY, int sourceIndex) {
        PlanarImage source = this.getSource(sourceIndex);
        int minTileX = source.getMinTileX();
        int minTileY = source.getMinTileY();
        int maxTileX = minTileX + source.getNumXTiles() - 1;
        int maxTileY = minTileY + source.getNumYTiles() - 1;
        Point[] tileDependencies = new Point[(maxTileX - minTileX + 1) * (maxTileY - minTileY + 1)];
        int count = 0;
        for (int ty = minTileY; ty <= maxTileY; ++ty) {
            for (int tx = minTileX; tx <= maxTileX; ++tx) {
                tileDependencies[count++] = new Point(tx, ty);
            }
        }
        return tileDependencies;
    }
}

