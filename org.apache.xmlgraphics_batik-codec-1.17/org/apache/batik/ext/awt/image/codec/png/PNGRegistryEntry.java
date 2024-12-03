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
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.ext.awt.image.codec.png;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.codec.png.PNGDecodeParam;
import org.apache.batik.ext.awt.image.codec.png.PNGRed;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.FormatRed;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.ext.awt.image.spi.MagicNumberRegistryEntry;
import org.apache.batik.util.ParsedURL;

public class PNGRegistryEntry
extends MagicNumberRegistryEntry {
    static final byte[] signature = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};

    public PNGRegistryEntry() {
        super("PNG", "png", "image/png", 0, signature);
    }

    public Filter handleStream(InputStream inIS, ParsedURL origURL, boolean needRawData) {
        Object[] errParam;
        String errCode;
        final DeferRable dr = new DeferRable();
        final InputStream is = inIS;
        final boolean raw = needRawData;
        if (origURL != null) {
            errCode = "url.format.unreadable";
            errParam = new Object[]{"PNG", origURL};
        } else {
            errCode = "stream.format.unreadable";
            errParam = new Object[]{"PNG"};
        }
        Thread t = new Thread(){

            @Override
            public void run() {
                Filter filt;
                try {
                    PNGDecodeParam param = new PNGDecodeParam();
                    param.setExpandPalette(true);
                    if (raw) {
                        param.setPerformGammaCorrection(false);
                    } else {
                        param.setPerformGammaCorrection(true);
                        param.setDisplayExponent(2.2f);
                    }
                    PNGRed cr = new PNGRed(is, param);
                    dr.setBounds((Rectangle2D)new Rectangle2D.Double(0.0, 0.0, cr.getWidth(), cr.getHeight()));
                    cr = new Any2sRGBRed((CachableRed)cr);
                    cr = new FormatRed((CachableRed)cr, GraphicsUtil.sRGB_Unpre);
                    WritableRaster wr = (WritableRaster)cr.getData();
                    ColorModel cm = cr.getColorModel();
                    BufferedImage image = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
                    cr = GraphicsUtil.wrap((RenderedImage)image);
                    filt = new RedRable((CachableRed)cr);
                }
                catch (IOException ioe) {
                    filt = ImageTagRegistry.getBrokenLinkImage((Object)((Object)PNGRegistryEntry.this), (String)errCode, (Object[])errParam);
                }
                catch (ThreadDeath td) {
                    Filter filt2 = ImageTagRegistry.getBrokenLinkImage((Object)((Object)PNGRegistryEntry.this), (String)errCode, (Object[])errParam);
                    dr.setSource(filt2);
                    throw td;
                }
                catch (Throwable t) {
                    filt = ImageTagRegistry.getBrokenLinkImage((Object)((Object)PNGRegistryEntry.this), (String)errCode, (Object[])errParam);
                }
                dr.setSource(filt);
            }
        };
        t.start();
        return dr;
    }
}

