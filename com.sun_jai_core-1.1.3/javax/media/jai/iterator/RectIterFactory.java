/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.iterator;

import com.sun.media.jai.iterator.RectIterCSMByte;
import com.sun.media.jai.iterator.RectIterCSMFloat;
import com.sun.media.jai.iterator.RectIterFallback;
import com.sun.media.jai.iterator.WrapperRI;
import com.sun.media.jai.iterator.WrapperWRI;
import com.sun.media.jai.iterator.WritableRectIterCSMByte;
import com.sun.media.jai.iterator.WritableRectIterCSMFloat;
import com.sun.media.jai.iterator.WritableRectIterFallback;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.WritableRectIter;

public class RectIterFactory {
    private RectIterFactory() {
    }

    public static RectIter create(RenderedImage im, Rectangle bounds) {
        SampleModel sm;
        if (bounds == null) {
            bounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        }
        if ((sm = im.getSampleModel()) instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                case 0: {
                    return new RectIterCSMByte(im, bounds);
                }
                case 2: {
                    break;
                }
                case 1: {
                    break;
                }
                case 3: {
                    break;
                }
                case 4: {
                    return new RectIterCSMFloat(im, bounds);
                }
            }
        }
        return new RectIterFallback(im, bounds);
    }

    public static RectIter create(Raster ras, Rectangle bounds) {
        WrapperRI im = new WrapperRI(ras);
        return RectIterFactory.create(im, bounds);
    }

    public static WritableRectIter createWritable(WritableRenderedImage im, Rectangle bounds) {
        SampleModel sm;
        if (bounds == null) {
            bounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
        }
        if ((sm = im.getSampleModel()) instanceof ComponentSampleModel) {
            switch (sm.getDataType()) {
                case 0: {
                    return new WritableRectIterCSMByte(im, bounds);
                }
                case 2: {
                    break;
                }
                case 1: {
                    break;
                }
                case 3: {
                    break;
                }
                case 4: {
                    return new WritableRectIterCSMFloat(im, bounds);
                }
            }
        }
        return new WritableRectIterFallback(im, bounds);
    }

    public static WritableRectIter createWritable(WritableRaster ras, Rectangle bounds) {
        WrapperWRI im = new WrapperWRI(ras);
        return RectIterFactory.createWritable(im, bounds);
    }
}

