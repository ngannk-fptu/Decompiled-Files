/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.GraphicsUtil
 *  org.apache.batik.gvt.renderer.ConcreteImageRendererFactory
 *  org.apache.batik.gvt.renderer.ImageRenderer
 */
package org.apache.batik.transcoder.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.gvt.renderer.ConcreteImageRendererFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.PaintKey;
import org.w3c.dom.Document;

public abstract class ImageTranscoder
extends SVGAbstractTranscoder {
    public static final TranscodingHints.Key KEY_BACKGROUND_COLOR = new PaintKey();
    public static final TranscodingHints.Key KEY_FORCE_TRANSPARENT_WHITE = new BooleanKey();

    protected ImageTranscoder() {
    }

    @Override
    protected void transcode(Document document, String uri, TranscoderOutput output) throws TranscoderException {
        super.transcode(document, uri, output);
        int w = (int)((double)this.width + 0.5);
        int h = (int)((double)this.height + 0.5);
        ImageRenderer renderer = this.createRenderer();
        renderer.updateOffScreen(w, h);
        renderer.setTransform(this.curTxf);
        renderer.setTree(this.root);
        this.root = null;
        try {
            Rectangle2D.Float raoi = new Rectangle2D.Float(0.0f, 0.0f, this.width, this.height);
            renderer.repaint(this.curTxf.createInverse().createTransformedShape(raoi));
            BufferedImage rend = renderer.getOffScreen();
            renderer = null;
            BufferedImage dest = this.createImage(w, h);
            Graphics2D g2d = GraphicsUtil.createGraphics((BufferedImage)dest);
            if (this.hints.containsKey(KEY_BACKGROUND_COLOR)) {
                Paint bgcolor = (Paint)this.hints.get(KEY_BACKGROUND_COLOR);
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setPaint(bgcolor);
                g2d.fillRect(0, 0, w, h);
            }
            if (rend != null) {
                g2d.drawRenderedImage(rend, new AffineTransform());
            }
            g2d.dispose();
            rend = null;
            this.writeImage(dest, output);
        }
        catch (Exception ex) {
            throw new TranscoderException(ex);
        }
    }

    protected ImageRenderer createRenderer() {
        ConcreteImageRendererFactory rendFactory = new ConcreteImageRendererFactory();
        return rendFactory.createStaticImageRenderer();
    }

    protected void forceTransparentWhite(BufferedImage img, SinglePixelPackedSampleModel sppsm) {
        int w = img.getWidth();
        int h = img.getHeight();
        DataBufferInt biDB = (DataBufferInt)img.getRaster().getDataBuffer();
        int scanStride = sppsm.getScanlineStride();
        int dbOffset = biDB.getOffset();
        int[] pixels = biDB.getBankData()[0];
        int p = dbOffset;
        int adjust = scanStride - w;
        int a = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int pel = 0;
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                pel = pixels[p];
                a = pel >> 24 & 0xFF;
                r = pel >> 16 & 0xFF;
                g = pel >> 8 & 0xFF;
                b = pel & 0xFF;
                r = (255 * (255 - a) + a * r) / 255;
                g = (255 * (255 - a) + a * g) / 255;
                b = (255 * (255 - a) + a * b) / 255;
                pixels[p++] = a << 24 & 0xFF000000 | r << 16 & 0xFF0000 | g << 8 & 0xFF00 | b & 0xFF;
            }
            p += adjust;
        }
    }

    public abstract BufferedImage createImage(int var1, int var2);

    public abstract void writeImage(BufferedImage var1, TranscoderOutput var2) throws TranscoderException;
}

