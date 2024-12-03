/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.iterators.IteratorIterable
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.sl.draw;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;

public class BitmapImageRenderer
implements ImageRenderer {
    private static final Logger LOG = LogManager.getLogger(BitmapImageRenderer.class);
    private static final ImageLoader[] IMAGE_LOADERS = new ImageLoader[]{BitmapImageRenderer::loadColored, BitmapImageRenderer::loadGrayScaled, BitmapImageRenderer::loadTruncated};
    private static final String UNSUPPORTED_IMAGE_TYPE = "Unsupported Image Type";
    private static final PictureData.PictureType[] ALLOWED_TYPES = new PictureData.PictureType[]{PictureData.PictureType.JPEG, PictureData.PictureType.PNG, PictureData.PictureType.BMP, PictureData.PictureType.GIF};
    protected BufferedImage img;
    private boolean doCache;
    private byte[] cachedImage;
    private String cachedContentType;

    @Override
    public boolean canRender(String contentType) {
        return Stream.of(ALLOWED_TYPES).anyMatch(t -> t.contentType.equalsIgnoreCase(contentType));
    }

    @Override
    public void loadImage(InputStream data, String contentType) throws IOException {
        InputStream in = data;
        if (this.doCache) {
            try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
                IOUtils.copy(data, (OutputStream)bos);
                this.cachedImage = bos.toByteArray();
                this.cachedContentType = contentType;
                in = bos.toInputStream();
            }
        }
        this.img = BitmapImageRenderer.readImage(in, contentType);
    }

    @Override
    public void loadImage(byte[] data, String contentType) throws IOException {
        if (data == null) {
            return;
        }
        if (this.doCache) {
            this.cachedImage = (byte[])data.clone();
            this.cachedContentType = contentType;
        }
        this.img = BitmapImageRenderer.readImage((InputStream)new UnsynchronizedByteArrayInputStream(data), contentType);
    }

    private static BufferedImage readImage(InputStream data, String contentType) throws IOException {
        IOException lastException = null;
        BufferedImage img = null;
        try (MemoryCacheImageInputStream iis = new MemoryCacheImageInputStream(data);){
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            while (img == null && iter.hasNext()) {
                lastException = null;
                ImageReader reader = iter.next();
                ImageReadParam param = reader.getDefaultReadParam();
                for (ImageLoader il : IMAGE_LOADERS) {
                    iis.reset();
                    iis.mark();
                    try {
                        img = il.load(reader, iis, param);
                        if (img == null) continue;
                        break;
                    }
                    catch (IOException e) {
                        lastException = e;
                        if (!UNSUPPORTED_IMAGE_TYPE.equals(e.getMessage())) continue;
                        break;
                    }
                    catch (RuntimeException e) {
                        lastException = new IOException("ImageIO runtime exception", e);
                    }
                }
                reader.dispose();
            }
        }
        if (img == null) {
            if (lastException != null) {
                throw lastException;
            }
            LOG.atWarn().log("Content-type: {} is not supported. Image ignored.", (Object)contentType);
            return null;
        }
        if (img.getColorModel().hasAlpha()) {
            return img;
        }
        BufferedImage argbImg = new BufferedImage(img.getWidth(), img.getHeight(), 2);
        Graphics g = argbImg.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return argbImg;
    }

    private static BufferedImage loadColored(ImageReader reader, ImageInputStream iis, ImageReadParam param) throws IOException {
        reader.setInput(iis, false, true);
        return reader.read(0, param);
    }

    private static BufferedImage loadGrayScaled(ImageReader reader, ImageInputStream iis, ImageReadParam param) throws IOException {
        IteratorIterable specs = new IteratorIterable(reader.getImageTypes(0));
        StreamSupport.stream(specs.spliterator(), false).filter(its -> its.getBufferedImageType() == 10).findFirst().ifPresent(param::setDestinationType);
        reader.setInput(iis, false, true);
        return reader.read(0, param);
    }

    private static BufferedImage loadTruncated(ImageReader reader, ImageInputStream iis, ImageReadParam param) throws IOException {
        reader.setInput(iis, false, true);
        int height = reader.getHeight(0);
        int width = reader.getWidth(0);
        Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);
        if (!imageTypes.hasNext()) {
            return null;
        }
        ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();
        BufferedImage img = imageTypeSpecifier.createBufferedImage(width, height);
        param.setDestination(img);
        try {
            reader.read(0, param);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (img.getColorModel().hasAlpha()) {
            return img;
        }
        int y = BitmapImageRenderer.findTruncatedBlackBox(img, width, height);
        if (y >= height) {
            return img;
        }
        BufferedImage argbImg = new BufferedImage(width, height, 2);
        Graphics2D g = argbImg.createGraphics();
        g.clipRect(0, 0, width, y);
        g.drawImage((Image)img, 0, 0, null);
        g.dispose();
        img.flush();
        return argbImg;
    }

    private static int findTruncatedBlackBox(BufferedImage img, int width, int height) {
        for (int h = height - 1; h > 0; --h) {
            for (int w = width - 1; w > 0; w -= width / 10) {
                int p = img.getRGB(w, h);
                if (p == -16777216) continue;
                return h + 1;
            }
        }
        return 0;
    }

    @Override
    public BufferedImage getImage() {
        return this.img;
    }

    @Override
    public BufferedImage getImage(Dimension2D dim) {
        if (this.img == null) {
            return null;
        }
        double w_old = this.img.getWidth();
        double h_old = this.img.getHeight();
        double w_new = dim.getWidth();
        double h_new = dim.getHeight();
        if (w_old == w_new && h_old == h_new) {
            return this.img;
        }
        BufferedImage scaled = new BufferedImage((int)w_new, (int)h_new, 2);
        AffineTransform at = new AffineTransform();
        at.scale(w_new / w_old, h_new / h_old);
        AffineTransformOp scaleOp = new AffineTransformOp(at, 2);
        scaleOp.filter(this.img, scaled);
        return scaled;
    }

    @Override
    public Rectangle2D getBounds() {
        return this.img == null ? new Rectangle2D.Double() : new Rectangle2D.Double(0.0, 0.0, this.img.getWidth(), this.img.getHeight());
    }

    @Override
    public void setAlpha(double alpha) {
        this.img = BitmapImageRenderer.setAlpha(this.img, alpha);
    }

    public static BufferedImage setAlpha(BufferedImage image, double alpha) {
        if (image == null) {
            return new BufferedImage(1, 1, 2);
        }
        if (alpha == 0.0) {
            return image;
        }
        float[] scalefactors = new float[]{1.0f, 1.0f, 1.0f, (float)alpha};
        float[] offsets = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        RescaleOp op = new RescaleOp(scalefactors, offsets, null);
        return op.filter(image, null);
    }

    @Override
    public boolean drawImage(Graphics2D graphics, Rectangle2D anchor) {
        return this.drawImage(graphics, anchor, null);
    }

    @Override
    public boolean drawImage(Graphics2D graphics, Rectangle2D anchor, Insets clip) {
        if (this.img == null) {
            return false;
        }
        boolean isClipped = true;
        if (clip == null) {
            isClipped = false;
            clip = new Insets(0, 0, 0, 0);
        }
        int iw = this.img.getWidth();
        int ih = this.img.getHeight();
        double cw = (double)(100000 - clip.left - clip.right) / 100000.0;
        double ch = (double)(100000 - clip.top - clip.bottom) / 100000.0;
        double sx = anchor.getWidth() / ((double)iw * cw);
        double sy = anchor.getHeight() / ((double)ih * ch);
        double tx = anchor.getX() - (double)iw * sx * (double)clip.left / 100000.0;
        double ty = anchor.getY() - (double)ih * sy * (double)clip.top / 100000.0;
        AffineTransform at = new AffineTransform(sx, 0.0, 0.0, sy, tx, ty);
        Shape clipOld = graphics.getClip();
        if (isClipped) {
            graphics.clip(anchor.getBounds2D());
        }
        graphics.drawRenderedImage(this.img, at);
        graphics.setClip(clipOld);
        return true;
    }

    @Override
    public Rectangle2D getNativeBounds() {
        return new Rectangle2D.Double(0.0, 0.0, this.img.getWidth(), this.img.getHeight());
    }

    @Override
    public void setCacheInput(boolean enable) {
        this.doCache = enable;
        if (!enable) {
            this.cachedContentType = null;
            this.cachedImage = null;
        }
    }

    @Override
    public byte[] getCachedImage() {
        return this.cachedImage;
    }

    @Override
    public String getCachedContentType() {
        return this.cachedContentType;
    }

    private static interface ImageLoader {
        public BufferedImage load(ImageReader var1, ImageInputStream var2, ImageReadParam var3) throws IOException;
    }
}

