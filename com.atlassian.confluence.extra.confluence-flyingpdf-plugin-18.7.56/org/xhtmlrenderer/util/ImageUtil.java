/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.DownscaleQuality;
import org.xhtmlrenderer.util.ScalingOptions;
import org.xhtmlrenderer.util.XRLog;

public class ImageUtil {
    private static final Map qual = new HashMap();

    public static void clearImage(BufferedImage image, Color bgColor) {
        Graphics2D g2d = (Graphics2D)image.getGraphics();
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
    }

    public static void clearImage(BufferedImage image) {
        ImageUtil.clearImage(image, Color.WHITE);
    }

    public static BufferedImage makeCompatible(BufferedImage bimg) {
        BufferedImage cimg = null;
        if (GraphicsEnvironment.isHeadless()) {
            cimg = ImageUtil.createCompatibleBufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getTransparency());
        } else {
            GraphicsConfiguration gc = ImageUtil.getGraphicsConfiguration();
            if (bimg.getColorModel().equals(gc.getColorModel())) {
                return bimg;
            }
            cimg = gc.createCompatibleImage(bimg.getWidth(), bimg.getHeight(), bimg.getTransparency());
        }
        Graphics cg = cimg.getGraphics();
        cg.drawImage(bimg, 0, 0, null);
        cg.dispose();
        return cimg;
    }

    public static BufferedImage createCompatibleBufferedImage(int width, int height, int biType) {
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (ge.isHeadlessInstance()) {
            bimage = new BufferedImage(width, height, biType);
        } else {
            GraphicsConfiguration gc = ImageUtil.getGraphicsConfiguration();
            int type = biType == 2 || biType == 3 ? 3 : 1;
            bimage = gc.createCompatibleImage(width, height, type);
        }
        return bimage;
    }

    private static GraphicsConfiguration getGraphicsConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        return gc;
    }

    public static BufferedImage createCompatibleBufferedImage(int width, int height) {
        return ImageUtil.createCompatibleBufferedImage(width, height, 2);
    }

    public static BufferedImage getScaledInstance(ScalingOptions opt, BufferedImage orgImage) {
        int h;
        int w = orgImage.getWidth(null);
        if (opt.sizeMatches(w, h = orgImage.getHeight(null))) {
            return orgImage;
        }
        w = opt.getTargetWidth() <= 0 ? w : opt.getTargetWidth();
        h = opt.getTargetHeight() <= 0 ? h : opt.getTargetHeight();
        Scaler scaler = (Scaler)qual.get(opt.getDownscalingHint());
        opt.setTargetWidth(w);
        opt.setTargetHeight(h);
        return scaler.getScaledInstance(orgImage, opt);
    }

    public static BufferedImage getScaledInstance(BufferedImage orgImage, int targetWidth, int targetHeight) {
        String downscaleQuality = Configuration.valueFor("xr.image.scale", DownscaleQuality.HIGH_QUALITY.asString());
        DownscaleQuality quality = DownscaleQuality.forString(downscaleQuality, DownscaleQuality.HIGH_QUALITY);
        Object hint = Configuration.valueFromClassConstant("xr.image.render-quality", RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        ScalingOptions opt = new ScalingOptions(targetWidth, targetHeight, 2, quality, hint);
        return ImageUtil.getScaledInstance(opt, orgImage);
    }

    public static List scaleMultiple(ScalingOptions opt, BufferedImage img, List dimensions) {
        ArrayList<BufferedImage> scaledImages = new ArrayList<BufferedImage>(dimensions.size());
        for (Dimension dim : dimensions) {
            opt.setTargetDimensions(dim);
            BufferedImage scaled = ImageUtil.getScaledInstance(opt, img);
            scaledImages.add(scaled);
        }
        return scaledImages;
    }

    public static BufferedImage convertToBufferedImage(Image awtImg, int type) {
        BufferedImage bimg;
        if (awtImg instanceof BufferedImage) {
            bimg = (BufferedImage)awtImg;
        } else {
            bimg = ImageUtil.createCompatibleBufferedImage(awtImg.getWidth(null), awtImg.getHeight(null), type);
            Graphics2D g = bimg.createGraphics();
            g.drawImage(awtImg, 0, 0, null, null);
            g.dispose();
        }
        return bimg;
    }

    public static BufferedImage createTransparentImage(int width, int height) {
        BufferedImage bi = ImageUtil.createCompatibleBufferedImage(width, height, 2);
        Graphics2D g2d = bi.createGraphics();
        Color transparent = new Color(0, 0, 0, 0);
        g2d.setColor(transparent);
        g2d.setComposite(AlphaComposite.Src);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return bi;
    }

    public static boolean isEmbeddedBase64Image(String uri) {
        return uri != null && uri.startsWith("data:image/");
    }

    public static byte[] getEmbeddedBase64Image(String imageDataUri) {
        int b64Index = imageDataUri.indexOf("base64,");
        if (b64Index != -1) {
            String b64encoded = imageDataUri.substring(b64Index + "base64,".length());
            return Base64.getDecoder().decode(b64encoded);
        }
        XRLog.load(Level.SEVERE, "Embedded XHTML images must be encoded in base 64.");
        return null;
    }

    public static BufferedImage loadEmbeddedBase64Image(String imageDataUri) {
        try {
            byte[] buffer = ImageUtil.getEmbeddedBase64Image(imageDataUri);
            if (buffer != null) {
                return ImageIO.read(new ByteArrayInputStream(buffer));
            }
        }
        catch (IOException ex) {
            XRLog.exception("Can't read XHTML embedded image", ex);
        }
        return null;
    }

    static {
        qual.put(DownscaleQuality.FAST, new OldScaler());
        qual.put(DownscaleQuality.HIGH_QUALITY, new HighQualityScaler());
        qual.put(DownscaleQuality.LOW_QUALITY, new FastScaler());
        qual.put(DownscaleQuality.AREA, new AreaAverageScaler());
    }

    static class HighQualityScaler
    implements Scaler {
        HighQualityScaler() {
        }

        @Override
        public BufferedImage getScaledInstance(BufferedImage img, ScalingOptions opt) {
            int h;
            int w;
            int imgw = img.getWidth(null);
            int imgh = img.getHeight(null);
            if (opt.getTargetWidth() < imgw && opt.getTargetHeight() < imgh) {
                w = imgw;
                h = imgh;
            } else {
                w = opt.getTargetWidth();
                h = opt.getTargetHeight();
            }
            BufferedImage scaled = img;
            do {
                if (w > opt.getTargetWidth() && (w /= 2) < opt.getTargetWidth()) {
                    w = opt.getTargetWidth();
                }
                if (h > opt.getTargetHeight() && (h /= 2) < opt.getTargetHeight()) {
                    h = opt.getTargetHeight();
                }
                BufferedImage tmp = ImageUtil.createCompatibleBufferedImage(w, h, img.getType());
                Graphics2D g2 = tmp.createGraphics();
                opt.applyRenderingHints(g2);
                g2.drawImage(scaled, 0, 0, w, h, null);
                g2.dispose();
                scaled = tmp;
            } while (w != opt.getTargetWidth() || h != opt.getTargetHeight());
            return scaled;
        }
    }

    static class FastScaler
    implements Scaler {
        FastScaler() {
        }

        @Override
        public BufferedImage getScaledInstance(BufferedImage img, ScalingOptions opt) {
            int w = opt.getTargetWidth();
            int h = opt.getTargetHeight();
            BufferedImage scaled = ImageUtil.createCompatibleBufferedImage(w, h, img.getType());
            Graphics2D g2 = scaled.createGraphics();
            opt.applyRenderingHints(g2);
            g2.drawImage(img, 0, 0, w, h, null);
            g2.dispose();
            return scaled;
        }
    }

    static class AreaAverageScaler
    extends AbstractFastScaler {
        AreaAverageScaler() {
        }

        @Override
        protected int getImageScalingMethod() {
            return 16;
        }
    }

    static class OldScaler
    extends AbstractFastScaler {
        OldScaler() {
        }

        @Override
        protected int getImageScalingMethod() {
            return 2;
        }
    }

    static abstract class AbstractFastScaler
    implements Scaler {
        AbstractFastScaler() {
        }

        @Override
        public BufferedImage getScaledInstance(BufferedImage img, ScalingOptions opt) {
            Image scaled = img.getScaledInstance(opt.getTargetWidth(), opt.getTargetHeight(), this.getImageScalingMethod());
            return ImageUtil.convertToBufferedImage(scaled, img.getType());
        }

        protected abstract int getImageScalingMethod();
    }

    static interface Scaler {
        public BufferedImage getScaledInstance(BufferedImage var1, ScalingOptions var2);
    }
}

