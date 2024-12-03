/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Image;
import java.awt.image.BufferedImage;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.util.ImageUtil;

public abstract class AWTFSImage
implements FSImage {
    private static final FSImage NULL_FS_IMAGE = new NullImage();

    public static FSImage createImage(Image img) {
        if (img == null) {
            return NULL_FS_IMAGE;
        }
        BufferedImage bimg = img instanceof BufferedImage ? ImageUtil.makeCompatible((BufferedImage)img) : ImageUtil.convertToBufferedImage(img, 2);
        return new NewAWTFSImage(bimg);
    }

    protected AWTFSImage() {
    }

    public abstract BufferedImage getImage();

    private static class NullImage
    extends AWTFSImage {
        private static final BufferedImage EMPTY_IMAGE = ImageUtil.createTransparentImage(1, 1);

        private NullImage() {
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public void scale(int width, int height) {
        }

        @Override
        public BufferedImage getImage() {
            return EMPTY_IMAGE;
        }
    }

    static class NewAWTFSImage
    extends AWTFSImage {
        private BufferedImage img;

        public NewAWTFSImage(BufferedImage img) {
            this.img = img;
        }

        @Override
        public int getWidth() {
            return this.img.getWidth(null);
        }

        @Override
        public int getHeight() {
            return this.img.getHeight(null);
        }

        @Override
        public BufferedImage getImage() {
            return this.img;
        }

        @Override
        public void scale(int width, int height) {
            if (width > 0 || height > 0) {
                int currentWith = this.getWidth();
                int currentHeight = this.getHeight();
                int targetWidth = width;
                int targetHeight = height;
                if (targetWidth == -1) {
                    targetWidth = (int)((double)currentWith * ((double)targetHeight / (double)currentHeight));
                }
                if (targetHeight == -1) {
                    targetHeight = (int)((double)currentHeight * ((double)targetWidth / (double)currentWith));
                }
                if (currentWith != targetWidth || currentHeight != targetHeight) {
                    this.img = ImageUtil.getScaledInstance(this.img, targetWidth, targetHeight);
                }
            }
        }
    }
}

