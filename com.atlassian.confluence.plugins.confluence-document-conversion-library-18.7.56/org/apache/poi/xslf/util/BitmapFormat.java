/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import javax.imageio.ImageIO;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.util.MFProxy;
import org.apache.poi.xslf.util.OutputFormat;

@Internal
public class BitmapFormat
implements OutputFormat {
    private final String format;
    private BufferedImage img;
    private Graphics2D graphics;

    public BitmapFormat(String format) {
        this.format = format;
    }

    @Override
    public Graphics2D addSlide(double width, double height) {
        int type;
        switch (this.format) {
            case "png": 
            case "gif": {
                type = 2;
                break;
            }
            default: {
                type = 1;
            }
        }
        this.img = new BufferedImage((int)width, (int)height, type);
        this.graphics = this.img.createGraphics();
        this.graphics.setRenderingHint(Drawable.BUFFERED_IMAGE, new WeakReference<BufferedImage>(this.img));
        return this.graphics;
    }

    @Override
    public void writeSlide(MFProxy proxy, File outFile) throws IOException {
        if (!"null".equals(this.format)) {
            ImageIO.write((RenderedImage)this.img, this.format, outFile);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.graphics != null) {
            this.graphics.dispose();
            this.img.flush();
        }
    }
}

