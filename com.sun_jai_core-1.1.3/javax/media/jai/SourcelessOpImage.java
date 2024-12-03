/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.JaiI18N;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

public abstract class SourcelessOpImage
extends OpImage {
    private static ImageLayout layoutHelper(int minX, int minY, int width, int height, SampleModel sampleModel, ImageLayout il) {
        ImageLayout layout = il == null ? new ImageLayout() : (ImageLayout)il.clone();
        layout.setMinX(minX);
        layout.setMinY(minY);
        layout.setWidth(width);
        layout.setHeight(height);
        layout.setSampleModel(sampleModel);
        if (!layout.isValid(16)) {
            layout.setTileGridXOffset(layout.getMinX(null));
        }
        if (!layout.isValid(32)) {
            layout.setTileGridYOffset(layout.getMinY(null));
        }
        return layout;
    }

    public SourcelessOpImage(ImageLayout layout, Map configuration, SampleModel sampleModel, int minX, int minY, int width, int height) {
        super(null, SourcelessOpImage.layoutHelper(minX, minY, width, height, sampleModel, layout), configuration, false);
    }

    public boolean computesUniqueTiles() {
        return false;
    }

    public Raster computeTile(int tileX, int tileY) {
        Point org = new Point(this.tileXToX(tileX), this.tileYToY(tileY));
        WritableRaster dest = this.createWritableRaster(this.sampleModel, org);
        Rectangle rect = new Rectangle(org.x, org.y, this.sampleModel.getWidth(), this.sampleModel.getHeight());
        Rectangle destRect = rect.intersection(this.getBounds());
        this.computeRect((PlanarImage[])null, dest, destRect);
        return dest;
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        throw new IllegalArgumentException(JaiI18N.getString("SourcelessOpImage0"));
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        throw new IllegalArgumentException(JaiI18N.getString("SourcelessOpImage0"));
    }
}

