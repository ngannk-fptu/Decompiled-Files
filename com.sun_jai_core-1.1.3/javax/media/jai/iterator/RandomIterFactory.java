/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterFallback;
import com.sun.media.jai.iterator.WrapperRI;
import com.sun.media.jai.iterator.WrapperWRI;
import com.sun.media.jai.iterator.WritableRandomIterFallback;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.WritableRandomIter;

public class RandomIterFactory {
    private RandomIterFactory() {
    }

    public static RandomIter create(RenderedImage im, Rectangle bounds) {
        SampleModel sm;
        if (bounds == null) {
            bounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        }
        if ((sm = im.getSampleModel()) instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                default: 
            }
        }
        return new RandomIterFallback(im, bounds);
    }

    public static RandomIter create(Raster ras, Rectangle bounds) {
        WrapperRI im = new WrapperRI(ras);
        return RandomIterFactory.create(im, bounds);
    }

    public static WritableRandomIter createWritable(WritableRenderedImage im, Rectangle bounds) {
        SampleModel sm;
        if (bounds == null) {
            bounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        }
        if ((sm = im.getSampleModel()) instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                default: 
            }
        }
        return new WritableRandomIterFallback(im, bounds);
    }

    public static WritableRandomIter createWritable(WritableRaster ras, Rectangle bounds) {
        WrapperWRI im = new WrapperWRI(ras);
        return RandomIterFactory.createWritable(im, bounds);
    }
}

