/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.ThreadLocalCache
 *  com.atlassian.confluence.cache.ThreadLocalCacheAccessor
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.cache.ThreadLocalCache;
import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.plugins.dailysummary.content.ImageDataSource;
import com.atlassian.confluence.plugins.dailysummary.content.RenderedImageDataSource;
import com.atlassian.confluence.plugins.dailysummary.content.popular.SafeImageReader;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.activation.DataSource;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ImageHelper {
    private final ThreadLocalCacheAccessor<CacheKey, BufferedImage> threadLocalCache = ThreadLocalCacheAccessor.newInstance();

    private void display(final Image image) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                JFrame frame = new JFrame();
                JPanel panel = new JPanel();
                frame.setContentPane(panel);
                JLabel label = new JLabel();
                label.setIcon(new ImageIcon(image));
                panel.add(label);
                frame.pack();
                frame.setVisible(true);
                frame.setPreferredSize(new Dimension(300, 400));
            }
        });
    }

    public BufferedImage getCached(String dsName, int height, int minWidth, int maxWidth) {
        return (BufferedImage)this.threadLocalCache.get((Object)new CacheKey(dsName, height, minWidth, maxWidth));
    }

    public BufferedImage getCachedOrResize(DataSource ds, int height, int minWidth, int maxWidth) throws IOException {
        BufferedImage cached = this.getCached(ds.getName(), height, minWidth, maxWidth);
        if (cached != null) {
            return cached;
        }
        BufferedImage image = new SafeImageReader(ds.getInputStream()).read();
        return this.resizeAndCache(image, ds.getName(), height, minWidth, maxWidth);
    }

    public BufferedImage resizeAndCache(BufferedImage image, String dsName, int height, int minWidth, int maxWidth) throws IOException {
        image = this.cropAndResizeImage(image, height, minWidth, maxWidth);
        ThreadLocalCache.put((Object)new CacheKey(dsName, height, minWidth, maxWidth), (Object)image);
        return image;
    }

    public BufferedImage cropAndResizeImage(BufferedImage image, int height, int minWidth, int maxWidth) throws IOException {
        int origImageWidth = image.getWidth();
        int origImageHeight = image.getHeight();
        float ratio = (float)origImageWidth / (float)origImageHeight;
        float minRatio = (float)minWidth / (float)height;
        float maxRatio = (float)maxWidth / (float)height;
        boolean needsCropping = !(minRatio < ratio) || !(ratio < maxRatio);
        int croppedHeight = origImageHeight;
        if (needsCropping) {
            Rectangle cropRec = null;
            if (ratio < minRatio) {
                int totalHeight = (int)((float)origImageHeight * ratio / minRatio);
                int y = (origImageHeight - totalHeight) / 2;
                croppedHeight = origImageHeight - y;
                cropRec = new Rectangle(0, y, origImageWidth, croppedHeight);
            } else if (ratio > maxRatio) {
                int unscaledCroppedWidth = (int)((float)origImageWidth * maxRatio / ratio);
                int x = (origImageWidth - unscaledCroppedWidth) / 2;
                cropRec = new Rectangle(x, 0, unscaledCroppedWidth, origImageHeight);
            }
            if (cropRec != null) {
                image = this.crop(image, cropRec);
            }
        }
        double scale = 1.0;
        if (height > 0 && croppedHeight != height) {
            scale = (double)height / (double)croppedHeight;
            image = this.scaleImage(image, (int)((double)image.getWidth() * scale), (int)((double)image.getHeight() * scale), scale);
        }
        return image;
    }

    public ImageDataSource convertToDataSource(RenderedImage image, String name) throws IOException {
        return new RenderedImageDataSource(name, image, "png");
    }

    private BufferedImage crop(BufferedImage img, Rectangle cropRec) {
        return img.getSubimage(cropRec.x, cropRec.y, cropRec.width, cropRec.height);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BufferedImage scaleImage(BufferedImage source, int newWidth, int newHeight, double scale) {
        BufferedImage result = new BufferedImage(newWidth, newHeight, source.getType());
        Graphics2D g = null;
        try {
            g = result.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.scale(scale, scale);
            g.drawImage((Image)source, 0, 0, null);
        }
        finally {
            if (g != null) {
                g.dispose();
            }
        }
        return result;
    }

    private static class CacheKey {
        private final String dsName;
        private final int height;
        private final int minWidth;
        private final int maxWidth;

        public CacheKey(String dsName, int height, int minWidth, int maxWidth) {
            this.dsName = dsName;
            this.height = height;
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.dsName == null ? 0 : this.dsName.hashCode());
            result = 31 * result + this.height;
            result = 31 * result + this.maxWidth;
            result = 31 * result + this.minWidth;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey)obj;
            if (this.dsName == null ? other.dsName != null : !this.dsName.equals(other.dsName)) {
                return false;
            }
            if (this.height != other.height) {
                return false;
            }
            if (this.maxWidth != other.maxWidth) {
                return false;
            }
            return this.minWidth == other.minWidth;
        }
    }
}

