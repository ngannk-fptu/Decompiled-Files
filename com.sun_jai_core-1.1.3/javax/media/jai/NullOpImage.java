/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Hashtable;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.PointOpImage;
import javax.media.jai.TileCache;

public class NullOpImage
extends PointOpImage {
    protected int computeType;

    private static ImageLayout layoutHelper(RenderedImage source, ImageLayout layout) {
        ImageLayout il = new ImageLayout(source);
        if (layout != null && layout.isValid(512)) {
            ColorModel colorModel = layout.getColorModel(null);
            if (JDKWorkarounds.areCompatibleDataModels(source.getSampleModel(), colorModel)) {
                il.setColorModel(colorModel);
            }
        }
        return il;
    }

    public NullOpImage(RenderedImage source, ImageLayout layout, Map configuration, int computeType) {
        super(PlanarImage.wrapRenderedImage(source).createSnapshot(), NullOpImage.layoutHelper(source, layout), configuration, false);
        if (computeType != 1 && computeType != 2 && computeType != 3) {
            throw new IllegalArgumentException(JaiI18N.getString("NullOpImage0"));
        }
        this.computeType = computeType;
    }

    public NullOpImage(RenderedImage source, TileCache cache, int computeType, ImageLayout layout) {
        this(source, layout, (Map)(cache != null ? new RenderingHints(JAI.KEY_TILE_CACHE, cache) : null), computeType);
    }

    public Raster computeTile(int tileX, int tileY) {
        return this.getSource(0).getTile(tileX, tileY);
    }

    public boolean computesUniqueTiles() {
        return false;
    }

    protected synchronized Hashtable getProperties() {
        return this.getSource(0).getProperties();
    }

    protected synchronized void setProperties(Hashtable properties) {
        this.getSource(0).setProperties(properties);
    }

    public String[] getPropertyNames() {
        return this.getSource(0).getPropertyNames();
    }

    public String[] getPropertyNames(String prefix) {
        return this.getSource(0).getPropertyNames(prefix);
    }

    public Class getPropertyClass(String name) {
        return this.getSource(0).getPropertyClass(name);
    }

    public Object getProperty(String name) {
        return this.getSource(0).getProperty(name);
    }

    public void setProperty(String name, Object value) {
        this.getSource(0).setProperty(name, value);
    }

    public void removeProperty(String name) {
        this.getSource(0).removeProperty(name);
    }

    public int getOperationComputeType() {
        return this.computeType;
    }
}

