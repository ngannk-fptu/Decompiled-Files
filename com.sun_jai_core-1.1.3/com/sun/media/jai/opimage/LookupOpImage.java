/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.JDKWorkarounds;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ColormapOpImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.LookupTableJAI;

final class LookupOpImage
extends ColormapOpImage {
    protected LookupTableJAI table;

    public LookupOpImage(RenderedImage source, Map config, ImageLayout layout, LookupTableJAI table) {
        super(source, layout, config, true);
        this.table = table;
        SampleModel sm = source.getSampleModel();
        if (this.sampleModel.getTransferType() != table.getDataType() || this.sampleModel.getNumBands() != table.getDestNumBands(sm.getNumBands())) {
            this.sampleModel = table.getDestSampleModel(sm, this.tileWidth, this.tileHeight);
            if (this.colorModel != null && !JDKWorkarounds.areCompatibleDataModels(this.sampleModel, this.colorModel)) {
                this.colorModel = ImageUtil.getCompatibleColorModel(this.sampleModel, config);
            }
        }
        this.permitInPlaceOperation();
        this.initializeColormapOperation();
    }

    protected void transformColormap(byte[][] colormap) {
        for (int b = 0; b < 3; ++b) {
            byte[] map = colormap[b];
            int mapSize = map.length;
            int band = this.table.getNumBands() < 3 ? 0 : b;
            for (int i = 0; i < mapSize; ++i) {
                int result = this.table.lookup(band, map[i] & 0xFF);
                map[i] = ImageUtil.clampByte(result);
            }
        }
    }

    protected void computeRect(Raster[] sources, WritableRaster dest, Rectangle destRect) {
        this.table.lookup(sources[0], dest, destRect);
    }
}

