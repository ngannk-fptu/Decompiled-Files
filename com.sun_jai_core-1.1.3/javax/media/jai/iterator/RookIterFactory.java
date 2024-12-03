/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

import com.sun.media.jai.iterator.RookIterFallback;
import com.sun.media.jai.iterator.WrapperRI;
import com.sun.media.jai.iterator.WrapperWRI;
import com.sun.media.jai.iterator.WritableRookIterFallback;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.iterator.RookIter;
import javax.media.jai.iterator.WritableRookIter;

public class RookIterFactory {
    private RookIterFactory() {
    }

    public static RookIter create(RenderedImage im, Rectangle bounds) {
        SampleModel sm;
        if (bounds == null) {
            bounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        }
        if ((sm = im.getSampleModel()) instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                default: 
            }
        }
        return new RookIterFallback(im, bounds);
    }

    public static RookIter create(Raster ras, Rectangle bounds) {
        WrapperRI im = new WrapperRI(ras);
        return RookIterFactory.create(im, bounds);
    }

    public static WritableRookIter createWritable(WritableRenderedImage im, Rectangle bounds) {
        SampleModel sm;
        if (bounds == null) {
            bounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        }
        if ((sm = im.getSampleModel()) instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                default: 
            }
        }
        return new WritableRookIterFallback(im, bounds);
    }

    public static WritableRookIter createWritable(WritableRaster ras, Rectangle bounds) {
        WrapperWRI im = new WrapperWRI(ras);
        return RookIterFactory.createWritable(im, bounds);
    }
}

