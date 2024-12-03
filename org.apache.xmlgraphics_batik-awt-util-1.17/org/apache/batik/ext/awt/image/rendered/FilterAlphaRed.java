/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.ColorSpaceHintKey;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;

public class FilterAlphaRed
extends AbstractRed {
    public FilterAlphaRed(CachableRed src) {
        super(src, src.getBounds(), src.getColorModel(), src.getSampleModel(), src.getTileGridXOffset(), src.getTileGridYOffset(), null);
        this.props.put("org.apache.batik.gvt.filter.Colorspace", ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        CachableRed srcRed = (CachableRed)this.getSources().get(0);
        SampleModel sm = srcRed.getSampleModel();
        if (sm.getNumBands() == 1) {
            return srcRed.copyData(wr);
        }
        PadRed.ZeroRecter.zeroRect(wr);
        Raster srcRas = srcRed.getData(wr.getBounds());
        AbstractRed.copyBand(srcRas, srcRas.getNumBands() - 1, wr, wr.getNumBands() - 1);
        return wr;
    }
}

