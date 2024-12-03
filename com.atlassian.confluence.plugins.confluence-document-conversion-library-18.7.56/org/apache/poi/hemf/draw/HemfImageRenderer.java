/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hemf.draw;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.usermodel.HemfPicture;
import org.apache.poi.hwmf.draw.HwmfGraphicsState;
import org.apache.poi.hwmf.draw.HwmfImageRenderer;
import org.apache.poi.sl.draw.BitmapImageRenderer;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.draw.EmbeddedExtractor;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Units;

public class HemfImageRenderer
implements ImageRenderer,
EmbeddedExtractor {
    HemfPicture image;
    double alpha;
    boolean charsetInitialized = false;

    @Override
    public boolean canRender(String contentType) {
        return PictureData.PictureType.EMF.contentType.equalsIgnoreCase(contentType);
    }

    @Override
    public void loadImage(InputStream data, String contentType) throws IOException {
        if (!PictureData.PictureType.EMF.contentType.equals(contentType)) {
            throw new IOException("Invalid picture type");
        }
        this.image = new HemfPicture(data);
    }

    @Override
    public void loadImage(byte[] data, String contentType) throws IOException {
        if (!PictureData.PictureType.EMF.contentType.equals(contentType)) {
            throw new IOException("Invalid picture type");
        }
        this.image = new HemfPicture((InputStream)new UnsynchronizedByteArrayInputStream(data));
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public BufferedImage getImage() {
        return this.getImage(this.getDimension());
    }

    @Override
    public BufferedImage getImage(Dimension2D dim) {
        if (this.image == null) {
            return new BufferedImage(1, 1, 2);
        }
        BufferedImage bufImg = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), 2);
        Graphics2D g = bufImg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        this.image.draw(g, new Rectangle2D.Double(0.0, 0.0, dim.getWidth(), dim.getHeight()));
        g.dispose();
        return BitmapImageRenderer.setAlpha(bufImg, this.alpha);
    }

    @Override
    public boolean drawImage(Graphics2D graphics, Rectangle2D anchor) {
        return this.drawImage(graphics, anchor, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean drawImage(Graphics2D graphics, Rectangle2D anchor, Insets clip) {
        if (this.image == null) {
            return false;
        }
        Charset cs = (Charset)graphics.getRenderingHint(Drawable.DEFAULT_CHARSET);
        if (cs != null && !this.charsetInitialized) {
            this.setDefaultCharset(cs);
        }
        HwmfGraphicsState graphicsState = new HwmfGraphicsState();
        graphicsState.backup(graphics);
        try {
            if (clip != null) {
                graphics.clip(anchor);
            } else {
                clip = new Insets(0, 0, 0, 0);
            }
            this.image.draw(graphics, HwmfImageRenderer.getOuterBounds(anchor, clip));
        }
        finally {
            graphicsState.restore(graphics);
        }
        return true;
    }

    @Override
    public GenericRecord getGenericRecord() {
        return this.image;
    }

    @Override
    public Iterable<EmbeddedExtractor.EmbeddedPart> getEmbeddings() {
        return HwmfImageRenderer.getEmbeddings(this.image.getEmbeddings());
    }

    @Override
    public Rectangle2D getNativeBounds() {
        return this.image.getBounds();
    }

    @Override
    public Rectangle2D getBounds() {
        return Units.pointsToPixel(this.image == null ? new Rectangle2D.Double() : this.image.getBoundsInPoints());
    }

    @Override
    public void setDefaultCharset(Charset defaultCharset) {
        this.image.setDefaultCharset(defaultCharset);
        this.charsetInitialized = true;
    }
}

