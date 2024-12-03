/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.draw;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hwmf.draw.HwmfGraphicsState;
import org.apache.poi.hwmf.usermodel.HwmfEmbedded;
import org.apache.poi.hwmf.usermodel.HwmfPicture;
import org.apache.poi.sl.draw.BitmapImageRenderer;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.draw.EmbeddedExtractor;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;

public class HwmfImageRenderer
implements ImageRenderer,
EmbeddedExtractor {
    HwmfPicture image;
    double alpha;
    boolean charsetInitialized = false;

    @Override
    public boolean canRender(String contentType) {
        return PictureData.PictureType.WMF.contentType.equalsIgnoreCase(contentType);
    }

    @Override
    public void loadImage(InputStream data, String contentType) throws IOException {
        if (!PictureData.PictureType.WMF.contentType.equals(contentType)) {
            throw new IOException("Invalid picture type");
        }
        this.image = new HwmfPicture(data);
    }

    @Override
    public void loadImage(byte[] data, String contentType) throws IOException {
        if (!PictureData.PictureType.WMF.contentType.equals(contentType)) {
            throw new IOException("Invalid picture type");
        }
        this.image = new HwmfPicture(new ByteArrayInputStream(data));
    }

    @Override
    public Dimension2D getDimension() {
        return Units.pointsToPixel(this.image == null ? new Dimension() : this.image.getSize());
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
        boolean isClipped = true;
        if (clip == null) {
            isClipped = false;
            clip = new Insets(0, 0, 0, 0);
        }
        if (isClipped) {
            graphics.clip(anchor);
        }
        this.image.draw(graphics, HwmfImageRenderer.getOuterBounds(anchor, clip));
        graphicsState.restore(graphics);
        return true;
    }

    @Internal
    public static Rectangle2D getOuterBounds(Rectangle2D anchor, Insets clip) {
        double outerWidth = anchor.getWidth() / ((100000.0 - (double)clip.left - (double)clip.right) / 100000.0);
        double outerHeight = anchor.getHeight() / ((100000.0 - (double)clip.top - (double)clip.bottom) / 100000.0);
        double outerX = anchor.getX() - (double)clip.left / 100000.0 * outerWidth;
        double outerY = anchor.getY() - (double)clip.top / 100000.0 * outerHeight;
        return new Rectangle2D.Double(outerX, outerY, outerWidth, outerHeight);
    }

    @Override
    public GenericRecord getGenericRecord() {
        return this.image;
    }

    @Override
    public Iterable<EmbeddedExtractor.EmbeddedPart> getEmbeddings() {
        return HwmfImageRenderer.getEmbeddings(this.image.getEmbeddings());
    }

    @Internal
    public static Iterable<EmbeddedExtractor.EmbeddedPart> getEmbeddings(Iterable<HwmfEmbedded> embs) {
        return () -> {
            final Iterator embit = embs.iterator();
            final int[] idx = new int[]{1};
            return new Iterator<EmbeddedExtractor.EmbeddedPart>(){

                @Override
                public boolean hasNext() {
                    return embit.hasNext();
                }

                @Override
                public EmbeddedExtractor.EmbeddedPart next() {
                    EmbeddedExtractor.EmbeddedPart ep = new EmbeddedExtractor.EmbeddedPart();
                    HwmfEmbedded emb = (HwmfEmbedded)embit.next();
                    ep.setData(emb::getRawData);
                    int n = idx[0];
                    idx[0] = n + 1;
                    ep.setName("embed_" + n + emb.getEmbeddedType().extension);
                    return ep;
                }
            };
        };
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

