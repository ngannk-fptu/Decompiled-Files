/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.GraphicsUtil
 *  org.apache.batik.ext.awt.image.renderable.DeferRable
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.RedRable
 *  org.apache.batik.ext.awt.image.rendered.Any2sRGBRed
 *  org.apache.batik.ext.awt.image.rendered.CachableRed
 *  org.apache.batik.ext.awt.image.rendered.FormatRed
 *  org.apache.batik.ext.awt.image.spi.ImageTagRegistry
 *  org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry
 *  org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry$MagicNumber
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.ext.awt.image.codec.imageio;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.FormatRed;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry;
import org.apache.batik.util.ParsedURL;

public abstract class AbstractImageIORegistryEntry
extends MagicNumberRegistryEntry {
    public AbstractImageIORegistryEntry(String name, String[] exts, String[] mimeTypes, MagicNumberRegistryEntry.MagicNumber[] magicNumbers) {
        super(name, 1100.0f, exts, mimeTypes, magicNumbers);
    }

    public AbstractImageIORegistryEntry(String name, String ext, String mimeType, int offset, byte[] magicNumber) {
        super(name, 1100.0f, ext, mimeType, offset, magicNumber);
    }

    public Filter handleStream(InputStream inIS, ParsedURL origURL, boolean needRawData) {
        Object[] errParam;
        String errCode;
        final DeferRable dr = new DeferRable();
        final InputStream is = inIS;
        if (origURL != null) {
            errCode = "url.format.unreadable";
            errParam = new Object[]{this.getFormatName(), origURL};
        } else {
            errCode = "stream.format.unreadable";
            errParam = new Object[]{this.getFormatName()};
        }
        Thread t = new Thread(){

            @Override
            public void run() {
                Filter filt;
                try {
                    Iterator<ImageReader> iter = ImageIO.getImageReadersByMIMEType(AbstractImageIORegistryEntry.this.getMimeTypes().get(0).toString());
                    if (!iter.hasNext()) {
                        throw new UnsupportedOperationException("No image reader for " + AbstractImageIORegistryEntry.this.getFormatName() + " available!");
                    }
                    ImageReader reader = iter.next();
                    ImageInputStream imageIn = ImageIO.createImageInputStream(is);
                    reader.setInput(imageIn, true);
                    int imageIndex = 0;
                    dr.setBounds((Rectangle2D)new Rectangle2D.Double(0.0, 0.0, reader.getWidth(imageIndex), reader.getHeight(imageIndex)));
                    BufferedImage bi = reader.read(imageIndex);
                    CachableRed cr = GraphicsUtil.wrap((RenderedImage)bi);
                    cr = new Any2sRGBRed(cr);
                    cr = new FormatRed(cr, GraphicsUtil.sRGB_Unpre);
                    WritableRaster wr = (WritableRaster)cr.getData();
                    ColorModel cm = cr.getColorModel();
                    BufferedImage image = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
                    cr = GraphicsUtil.wrap((RenderedImage)image);
                    filt = new RedRable(cr);
                }
                catch (IOException ioe) {
                    filt = ImageTagRegistry.getBrokenLinkImage((Object)((Object)AbstractImageIORegistryEntry.this), (String)errCode, (Object[])errParam);
                }
                catch (ThreadDeath td) {
                    Filter filt2 = ImageTagRegistry.getBrokenLinkImage((Object)((Object)AbstractImageIORegistryEntry.this), (String)errCode, (Object[])errParam);
                    dr.setSource(filt2);
                    throw td;
                }
                catch (Throwable t) {
                    filt = ImageTagRegistry.getBrokenLinkImage((Object)((Object)AbstractImageIORegistryEntry.this), (String)errCode, (Object[])errParam);
                }
                dr.setSource(filt);
            }
        };
        t.start();
        return dr;
    }
}

