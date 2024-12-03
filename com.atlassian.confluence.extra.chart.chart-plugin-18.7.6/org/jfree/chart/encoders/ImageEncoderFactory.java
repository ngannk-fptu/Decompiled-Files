/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.encoders;

import java.util.Hashtable;
import org.jfree.chart.encoders.ImageEncoder;

public class ImageEncoderFactory {
    private static Hashtable encoders = null;

    private static void init() {
        encoders = new Hashtable();
        encoders.put("jpeg", "org.jfree.chart.encoders.SunJPEGEncoderAdapter");
        try {
            Class.forName("javax.imageio.ImageIO");
            Class.forName("org.jfree.chart.encoders.SunPNGEncoderAdapter");
            encoders.put("png", "org.jfree.chart.encoders.SunPNGEncoderAdapter");
            encoders.put("jpeg", "org.jfree.chart.encoders.SunJPEGEncoderAdapter");
        }
        catch (ClassNotFoundException e) {
            encoders.put("png", "org.jfree.chart.encoders.KeypointPNGEncoderAdapter");
        }
    }

    public static void setImageEncoder(String format, String imageEncoderClassName) {
        encoders.put(format, imageEncoderClassName);
    }

    public static ImageEncoder newInstance(String format) {
        ImageEncoder imageEncoder = null;
        String className = (String)encoders.get(format);
        if (className == null) {
            throw new IllegalArgumentException("Unsupported image format - " + format);
        }
        try {
            Class<?> imageEncoderClass = Class.forName(className);
            imageEncoder = (ImageEncoder)imageEncoderClass.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
        return imageEncoder;
    }

    public static ImageEncoder newInstance(String format, float quality) {
        ImageEncoder imageEncoder = ImageEncoderFactory.newInstance(format);
        imageEncoder.setQuality(quality);
        return imageEncoder;
    }

    public static ImageEncoder newInstance(String format, boolean encodingAlpha) {
        ImageEncoder imageEncoder = ImageEncoderFactory.newInstance(format);
        imageEncoder.setEncodingAlpha(encodingAlpha);
        return imageEncoder;
    }

    public static ImageEncoder newInstance(String format, float quality, boolean encodingAlpha) {
        ImageEncoder imageEncoder = ImageEncoderFactory.newInstance(format);
        imageEncoder.setQuality(quality);
        imageEncoder.setEncodingAlpha(encodingAlpha);
        return imageEncoder;
    }

    static {
        ImageEncoderFactory.init();
    }
}

