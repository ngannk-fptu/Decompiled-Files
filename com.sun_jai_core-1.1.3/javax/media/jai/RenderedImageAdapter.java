/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.HashSet;
import javax.media.jai.ImageLayout;
import javax.media.jai.JaiI18N;
import javax.media.jai.PlanarImage;
import javax.media.jai.util.CaselessStringKey;

public class RenderedImageAdapter
extends PlanarImage {
    protected RenderedImage theImage;
    private Rectangle tileIndexBounds;

    static String[] mergePropertyNames(String[] localNames, String[] sourceNames) {
        String[] names = null;
        if (localNames == null || localNames.length == 0) {
            names = sourceNames;
        } else if (sourceNames == null || sourceNames.length == 0) {
            names = localNames;
        } else {
            HashSet<CaselessStringKey> nameSet = new HashSet<CaselessStringKey>((localNames.length + sourceNames.length) / 2);
            int numSourceNames = sourceNames.length;
            for (int i = 0; i < numSourceNames; ++i) {
                nameSet.add(new CaselessStringKey(sourceNames[i]));
            }
            int numLocalNames = localNames.length;
            for (int i = 0; i < numLocalNames; ++i) {
                nameSet.add(new CaselessStringKey(localNames[i]));
            }
            int numNames = nameSet.size();
            CaselessStringKey[] caselessNames = new CaselessStringKey[numNames];
            nameSet.toArray(caselessNames);
            names = new String[numNames];
            for (int i = 0; i < numNames; ++i) {
                names[i] = caselessNames[i].getName();
            }
        }
        if (names != null && names.length == 0) {
            names = null;
        }
        return names;
    }

    public RenderedImageAdapter(RenderedImage im) {
        super(im != null ? new ImageLayout(im) : null, null, null);
        if (im == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.theImage = im;
        this.tileIndexBounds = new Rectangle(this.theImage.getMinTileX(), this.theImage.getMinTileY(), this.theImage.getNumXTiles(), this.theImage.getNumYTiles());
    }

    public final RenderedImage getWrappedImage() {
        return this.theImage;
    }

    public final Raster getTile(int x, int y) {
        return this.tileIndexBounds.contains(x, y) ? this.theImage.getTile(x, y) : null;
    }

    public final Raster getData() {
        return this.theImage.getData();
    }

    public final Raster getData(Rectangle rect) {
        return this.theImage.getData(rect);
    }

    public final WritableRaster copyData(WritableRaster raster) {
        return this.theImage.copyData(raster);
    }

    public final String[] getPropertyNames() {
        return RenderedImageAdapter.mergePropertyNames(super.getPropertyNames(), this.theImage.getPropertyNames());
    }

    public final Object getProperty(String name) {
        Object property = super.getProperty(name);
        if (property == Image.UndefinedProperty) {
            property = this.theImage.getProperty(name);
        }
        return property;
    }

    public final Class getPropertyClass(String name) {
        Object propValue;
        Class<?> propClass = super.getPropertyClass(name);
        if (propClass == null && (propValue = this.getProperty(name)) != Image.UndefinedProperty) {
            propClass = propValue.getClass();
        }
        return propClass;
    }
}

