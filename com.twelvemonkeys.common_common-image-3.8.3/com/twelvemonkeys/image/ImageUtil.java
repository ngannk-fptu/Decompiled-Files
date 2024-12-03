/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.AffineTransformOp;
import com.twelvemonkeys.image.BrightnessContrastFilter;
import com.twelvemonkeys.image.BufferedImageFactory;
import com.twelvemonkeys.image.DiffusionDither;
import com.twelvemonkeys.image.GrayFilter;
import com.twelvemonkeys.image.IndexImage;
import com.twelvemonkeys.image.ResampleOp;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.IndexColorModel;
import java.awt.image.Kernel;
import java.awt.image.PackedColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public final class ImageUtil {
    public static final int ROTATE_90_CCW = -90;
    public static final int ROTATE_90_CW = 90;
    public static final int ROTATE_180 = 180;
    public static final int FLIP_VERTICAL = -1;
    public static final int FLIP_HORIZONTAL = 1;
    public static final int EDGE_ZERO_FILL = 0;
    public static final int EDGE_NO_OP = 1;
    public static final int EDGE_REFLECT = 2;
    public static final int EDGE_WRAP = 3;
    public static final int DITHER_DEFAULT = 0;
    public static final int DITHER_NONE = 1;
    public static final int DITHER_DIFFUSION = 2;
    public static final int DITHER_DIFFUSION_ALTSCANS = 3;
    public static final int COLOR_SELECTION_DEFAULT = 0;
    public static final int COLOR_SELECTION_FAST = 256;
    public static final int COLOR_SELECTION_QUALITY = 512;
    public static final int TRANSPARENCY_DEFAULT = 0;
    public static final int TRANSPARENCY_OPAQUE = 65536;
    public static final int TRANSPARENCY_BITMASK = 131072;
    protected static final int TRANSPARENCY_TRANSLUCENT = 196608;
    private static final int BI_TYPE_ANY = -1;
    private static boolean VM_SUPPORTS_ACCELERATION = true;
    private static final float[] SHARPEN_MATRIX = new float[]{0.0f, -0.3f, 0.0f, -0.3f, 2.2f, -0.3f, 0.0f, -0.3f, 0.0f};
    private static final Kernel SHARPEN_KERNEL = new Kernel(3, 3, SHARPEN_MATRIX);
    private static final Component NULL_COMPONENT = new Component(){};
    private static MediaTracker sTracker = new MediaTracker(NULL_COMPONENT);
    protected static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
    protected static final Point LOCATION_UPPER_LEFT = new Point(0, 0);
    private static final GraphicsConfiguration DEFAULT_CONFIGURATION = ImageUtil.getDefaultGraphicsConfiguration();

    private static GraphicsConfiguration getDefaultGraphicsConfiguration() {
        try {
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (!graphicsEnvironment.isHeadlessInstance()) {
                return graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
            }
        }
        catch (LinkageError linkageError) {
            VM_SUPPORTS_ACCELERATION = false;
        }
        return null;
    }

    private ImageUtil() {
    }

    public static BufferedImage toBuffered(RenderedImage renderedImage) {
        WritableRaster writableRaster;
        String[] stringArray;
        Hashtable<String, Object> hashtable;
        if (renderedImage instanceof BufferedImage) {
            return (BufferedImage)renderedImage;
        }
        if (renderedImage == null) {
            throw new IllegalArgumentException("original == null");
        }
        String[] stringArray2 = renderedImage.getPropertyNames();
        if (stringArray2 != null && stringArray2.length > 0) {
            hashtable = new Hashtable<String, Object>(stringArray2.length);
            stringArray = stringArray2;
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                String string = stringArray[i];
                hashtable.put(string, renderedImage.getProperty(string));
            }
        } else {
            hashtable = null;
        }
        if ((stringArray = renderedImage.getData()) instanceof WritableRaster) {
            writableRaster = (WritableRaster)stringArray;
        } else {
            writableRaster = stringArray.createCompatibleWritableRaster();
            writableRaster = renderedImage.copyData(writableRaster);
        }
        ColorModel colorModel = renderedImage.getColorModel();
        return new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), hashtable);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BufferedImage toBuffered(RenderedImage renderedImage, int n) {
        if (renderedImage instanceof BufferedImage && ((BufferedImage)renderedImage).getType() == n) {
            return (BufferedImage)renderedImage;
        }
        if (renderedImage == null) {
            throw new IllegalArgumentException("original == null");
        }
        BufferedImage bufferedImage = ImageUtil.createBuffered(renderedImage.getWidth(), renderedImage.getHeight(), n, 3);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        try {
            graphics2D.setComposite(AlphaComposite.Src);
            graphics2D.drawRenderedImage(renderedImage, IDENTITY_TRANSFORM);
        }
        finally {
            graphics2D.dispose();
        }
        return bufferedImage;
    }

    public static BufferedImage toBuffered(BufferedImage bufferedImage, int n) {
        return ImageUtil.toBuffered((RenderedImage)bufferedImage, n);
    }

    public static BufferedImage toBuffered(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        if (image == null) {
            throw new IllegalArgumentException("original == null");
        }
        BufferedImageFactory bufferedImageFactory = new BufferedImageFactory(image);
        return bufferedImageFactory.getBufferedImage();
    }

    public static BufferedImage createCopy(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("image == null");
        }
        ColorModel colorModel = bufferedImage.getColorModel();
        BufferedImage bufferedImage2 = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(bufferedImage.getWidth(), bufferedImage.getHeight()), colorModel.isAlphaPremultiplied(), null);
        ImageUtil.drawOnto(bufferedImage2, bufferedImage);
        return bufferedImage2;
    }

    static WritableRaster createRaster(int n, int n2, Object object, ColorModel colorModel) {
        int n3;
        Object object2;
        DataBuffer dataBuffer = null;
        WritableRaster writableRaster = null;
        if (object instanceof int[]) {
            object2 = (int[])object;
            dataBuffer = new DataBufferInt((int[])object2, ((int[])object2).length);
            n3 = colorModel.getNumComponents();
        } else if (object instanceof short[]) {
            object2 = (short[])object;
            dataBuffer = new DataBufferUShort((short[])object2, ((int[])object2).length);
            n3 = ((int[])object2).length / (n * n2);
        } else if (object instanceof byte[]) {
            object2 = (byte[])object;
            dataBuffer = new DataBufferByte((byte[])object2, ((int[])object2).length);
            n3 = colorModel instanceof IndexColorModel ? 1 : ((int[])object2).length / (n * n2);
        } else {
            n3 = -1;
            writableRaster = colorModel.createCompatibleWritableRaster(n, n2);
            writableRaster.setDataElements(0, 0, n, n2, object);
        }
        if (writableRaster == null) {
            if (colorModel instanceof IndexColorModel && ImageUtil.isIndexedPacked((IndexColorModel)colorModel)) {
                writableRaster = Raster.createPackedRaster(dataBuffer, n, n2, colorModel.getPixelSize(), LOCATION_UPPER_LEFT);
            } else if (colorModel instanceof PackedColorModel) {
                object2 = (PackedColorModel)colorModel;
                writableRaster = Raster.createPackedRaster(dataBuffer, n, n2, n, ((PackedColorModel)object2).getMasks(), LOCATION_UPPER_LEFT);
            } else {
                object2 = new int[n3];
                int n4 = 0;
                while (n4 < n3) {
                    object2[n4++] = n3 - n4;
                }
                writableRaster = Raster.createInterleavedRaster(dataBuffer, n, n2, n * n3, n3, (int[])object2, LOCATION_UPPER_LEFT);
            }
        }
        return writableRaster;
    }

    private static boolean isIndexedPacked(IndexColorModel indexColorModel) {
        return indexColorModel.getPixelSize() == 1 || indexColorModel.getPixelSize() == 2 || indexColorModel.getPixelSize() == 4;
    }

    static WritableRaster createCompatibleWritableRaster(BufferedImage bufferedImage, ColorModel colorModel, int n, int n2) {
        if (colorModel == null || ImageUtil.equals(bufferedImage.getColorModel(), colorModel)) {
            switch (bufferedImage.getType()) {
                case 5: {
                    int[] nArray = new int[]{2, 1, 0};
                    return Raster.createInterleavedRaster(0, n, n2, n * 3, 3, nArray, null);
                }
                case 6: 
                case 7: {
                    int[] nArray = new int[]{3, 2, 1, 0};
                    return Raster.createInterleavedRaster(0, n, n2, n * 4, 4, nArray, null);
                }
                case 0: {
                    SampleModel sampleModel = bufferedImage.getRaster().getSampleModel();
                    if (!(sampleModel instanceof ComponentSampleModel)) break;
                    int[] nArray = ((ComponentSampleModel)sampleModel).getBandOffsets();
                    return Raster.createInterleavedRaster(sampleModel.getDataType(), n, n2, n * nArray.length, nArray.length, nArray, null);
                }
            }
            return bufferedImage.getColorModel().createCompatibleWritableRaster(n, n2);
        }
        return colorModel.createCompatibleWritableRaster(n, n2);
    }

    public static BufferedImage toBuffered(Image image, int n) {
        return ImageUtil.toBuffered(image, n, null);
    }

    private static BufferedImage toBuffered(Image image, int n, IndexColorModel indexColorModel) {
        if (image instanceof BufferedImage && ((BufferedImage)image).getType() == n && (indexColorModel == null || ImageUtil.equals(((BufferedImage)image).getColorModel(), indexColorModel))) {
            return (BufferedImage)image;
        }
        if (image == null) {
            throw new IllegalArgumentException("original == null");
        }
        BufferedImage bufferedImage = indexColorModel == null ? ImageUtil.createBuffered(ImageUtil.getWidth(image), ImageUtil.getHeight(image), n, 3) : new BufferedImage(ImageUtil.getWidth(image), ImageUtil.getHeight(image), n, indexColorModel);
        ImageUtil.drawOnto(bufferedImage, image);
        return bufferedImage;
    }

    static void drawOnto(BufferedImage bufferedImage, Image image) {
        Graphics2D graphics2D = bufferedImage.createGraphics();
        try {
            graphics2D.setComposite(AlphaComposite.Src);
            graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            graphics2D.drawImage(image, 0, 0, null);
        }
        finally {
            graphics2D.dispose();
        }
    }

    public static BufferedImage createFlipped(Image image, int n) {
        AffineTransform affineTransform;
        switch (n) {
            case -1: 
            case 1: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal direction: " + n);
            }
        }
        BufferedImage bufferedImage = ImageUtil.toBuffered(image);
        if (n == 1) {
            affineTransform = AffineTransform.getTranslateInstance(0.0, bufferedImage.getHeight());
            affineTransform.scale(1.0, -1.0);
        } else {
            affineTransform = AffineTransform.getTranslateInstance(bufferedImage.getWidth(), 0.0);
            affineTransform.scale(-1.0, 1.0);
        }
        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, 1);
        return affineTransformOp.filter(bufferedImage, null);
    }

    public static BufferedImage createRotated(Image image, int n) {
        switch (n) {
            case -90: 
            case 90: 
            case 180: {
                return ImageUtil.createRotated(image, Math.toRadians(n));
            }
        }
        throw new IllegalArgumentException("Illegal direction: " + n);
    }

    public static BufferedImage createRotated(Image image, double d) {
        return ImageUtil.createRotated0(ImageUtil.toBuffered(image), d);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static BufferedImage createRotated0(BufferedImage bufferedImage, double d) {
        if (Math.abs(Math.toDegrees(d)) % 360.0 == 0.0) {
            return bufferedImage;
        }
        boolean bl = Math.abs(Math.toDegrees(d)) % 90.0 == 0.0;
        int n = bufferedImage.getWidth();
        int n2 = bufferedImage.getHeight();
        double d2 = Math.abs(Math.sin(d));
        double d3 = Math.abs(Math.cos(d));
        int n3 = (int)Math.floor((double)n * d3 + (double)n2 * d2);
        int n4 = (int)Math.floor((double)n2 * d3 + (double)n * d2);
        AffineTransform affineTransform = AffineTransform.getTranslateInstance((double)(n3 - n) / 2.0, (double)(n4 - n2) / 2.0);
        affineTransform.rotate(d, (double)n / 2.0, (double)n2 / 2.0);
        BufferedImage bufferedImage2 = ImageUtil.createTransparent(n3, n4);
        Graphics2D graphics2D = bufferedImage2.createGraphics();
        try {
            graphics2D.transform(affineTransform);
            if (!bl) {
                graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics2D.setPaint(new TexturePaint(bufferedImage, new Rectangle2D.Float(0.0f, 0.0f, bufferedImage.getWidth(), bufferedImage.getHeight())));
                graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            } else {
                graphics2D.drawImage((Image)bufferedImage, 0, 0, null);
            }
        }
        finally {
            graphics2D.dispose();
        }
        return bufferedImage2;
    }

    public static BufferedImage createScaled(Image image, int n, int n2, int n3) {
        Object object;
        ColorModel colorModel;
        int n4 = -1;
        if (image instanceof RenderedImage) {
            colorModel = ((RenderedImage)((Object)image)).getColorModel();
            if (image instanceof BufferedImage) {
                n4 = ((BufferedImage)image).getType();
            }
        } else {
            object = new BufferedImageFactory(image);
            colorModel = ((BufferedImageFactory)object).getColorModel();
        }
        if (n4 != ((BufferedImage)(object = ImageUtil.createResampled(image, n, n2, n3))).getType() && n4 != -1 || !ImageUtil.equals(((BufferedImage)object).getColorModel(), colorModel)) {
            WritableRaster writableRaster = image instanceof BufferedImage ? ImageUtil.createCompatibleWritableRaster((BufferedImage)image, colorModel, n, n2) : colorModel.createCompatibleWritableRaster(n, n2);
            BufferedImage bufferedImage = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
            if (colorModel instanceof IndexColorModel && n3 == 4) {
                new DiffusionDither((IndexColorModel)colorModel).filter((BufferedImage)object, bufferedImage);
            } else {
                ImageUtil.drawOnto(bufferedImage, (Image)object);
            }
            object = bufferedImage;
        }
        return object;
    }

    private static boolean equals(ColorModel colorModel, ColorModel colorModel2) {
        if (colorModel == colorModel2) {
            return true;
        }
        if (!colorModel.equals(colorModel2)) {
            return false;
        }
        if (colorModel instanceof IndexColorModel) {
            int n;
            IndexColorModel indexColorModel = (IndexColorModel)colorModel;
            IndexColorModel indexColorModel2 = (IndexColorModel)colorModel2;
            int n2 = indexColorModel.getMapSize();
            if (n2 != (n = indexColorModel2.getMapSize())) {
                return false;
            }
            for (int i = 0; i > n2; ++i) {
                if (indexColorModel.getRGB(i) == indexColorModel2.getRGB(i)) continue;
                return false;
            }
            return true;
        }
        return true;
    }

    public static BufferedImage createResampled(Image image, int n, int n2, int n3) {
        BufferedImage bufferedImage = image instanceof BufferedImage ? (BufferedImage)image : ImageUtil.toBuffered(image, 6);
        return ImageUtil.createResampled(bufferedImage, n, n2, n3);
    }

    public static BufferedImage createResampled(RenderedImage renderedImage, int n, int n2, int n3) {
        BufferedImage bufferedImage = renderedImage instanceof BufferedImage ? (BufferedImage)renderedImage : ImageUtil.toBuffered(renderedImage, renderedImage.getColorModel().hasAlpha() ? 6 : 5);
        return ImageUtil.createResampled(bufferedImage, n, n2, n3);
    }

    public static BufferedImage createResampled(BufferedImage bufferedImage, int n, int n2, int n3) {
        return new ResampleOp(n, n2, ImageUtil.convertAWTHints(n3)).filter(bufferedImage, null);
    }

    private static int convertAWTHints(int n) {
        switch (n) {
            case 2: 
            case 8: {
                return 1;
            }
            case 16: {
                return 2;
            }
            case 4: {
                return 13;
            }
        }
        return 9;
    }

    public static IndexColorModel getIndexColorModel(Image image, int n, int n2) {
        return IndexImage.getIndexColorModel(image, n, n2);
    }

    public static BufferedImage createIndexed(Image image) {
        return IndexImage.getIndexedImage(ImageUtil.toBuffered(image), 256, Color.black, 0);
    }

    public static BufferedImage createIndexed(Image image, int n, Color color, int n2) {
        return IndexImage.getIndexedImage(ImageUtil.toBuffered(image), n, color, n2);
    }

    public static BufferedImage createIndexed(Image image, IndexColorModel indexColorModel, Color color, int n) {
        return IndexImage.getIndexedImage(ImageUtil.toBuffered(image), indexColorModel, color, n);
    }

    public static BufferedImage createIndexed(Image image, Image image2, Color color, int n) {
        return IndexImage.getIndexedImage(ImageUtil.toBuffered(image), IndexImage.getIndexColorModel(image2, 255, n), color, n);
    }

    public static BufferedImage sharpen(BufferedImage bufferedImage) {
        return ImageUtil.convolve(bufferedImage, SHARPEN_KERNEL, 2);
    }

    public static BufferedImage sharpen(BufferedImage bufferedImage, float f) {
        if (f == 0.0f) {
            return bufferedImage;
        }
        float[] fArray = new float[]{0.0f, -f, 0.0f, -f, 4.0f * f + 1.0f, -f, 0.0f, -f, 0.0f};
        return ImageUtil.convolve(bufferedImage, new Kernel(3, 3, fArray), 2);
    }

    public static BufferedImage blur(BufferedImage bufferedImage) {
        return ImageUtil.blur(bufferedImage, 1.5f);
    }

    public static BufferedImage blur(BufferedImage bufferedImage, float f) {
        if (f <= 1.0f) {
            return bufferedImage;
        }
        Kernel kernel = ImageUtil.makeKernel(f);
        Kernel kernel2 = new Kernel(kernel.getHeight(), kernel.getWidth(), kernel.getKernelData(null));
        BufferedImage bufferedImage2 = ImageUtil.addBorder(bufferedImage, kernel.getWidth() / 2, kernel2.getHeight() / 2, 2);
        bufferedImage2 = ImageUtil.convolve(bufferedImage2, kernel, 1);
        bufferedImage2 = ImageUtil.convolve(bufferedImage2, kernel2, 1);
        return bufferedImage2.getSubimage(kernel.getWidth() / 2, kernel2.getHeight() / 2, bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    private static Kernel makeKernel(float f) {
        int n;
        int n2 = (int)Math.ceil(f);
        int n3 = n2 * 2 + 1;
        float[] fArray = new float[n3];
        float f2 = f / 3.0f;
        float f3 = 2.0f * f2 * f2;
        float f4 = (float)(Math.PI * 2 * (double)f2);
        float f5 = (float)Math.sqrt(f4);
        float f6 = f * f;
        float f7 = 0.0f;
        int n4 = 0;
        for (n = -n2; n <= n2; ++n) {
            float f8 = n * n;
            fArray[n4] = f8 > f6 ? 0.0f : (float)Math.exp(-f8 / f3) / f5;
            f7 += fArray[n4];
            ++n4;
        }
        n = 0;
        while (n < n3) {
            int n5 = n++;
            fArray[n5] = fArray[n5] / f7;
        }
        return new Kernel(n3, 1, fArray);
    }

    public static BufferedImage convolve(BufferedImage bufferedImage, Kernel kernel, int n) {
        BufferedImage bufferedImage2;
        switch (n) {
            case 2: 
            case 3: {
                bufferedImage2 = ImageUtil.addBorder(bufferedImage, kernel.getWidth() / 2, kernel.getHeight() / 2, n);
                break;
            }
            default: {
                bufferedImage2 = bufferedImage;
            }
        }
        ConvolveOp convolveOp = new ConvolveOp(kernel, n, null);
        BufferedImage bufferedImage3 = null;
        if (bufferedImage2.getType() == 5) {
            bufferedImage3 = ImageUtil.createBuffered(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType(), bufferedImage.getColorModel().getTransparency());
        }
        BufferedImage bufferedImage4 = convolveOp.filter(bufferedImage2, bufferedImage3);
        if (bufferedImage != bufferedImage2) {
            bufferedImage4 = bufferedImage4.getSubimage(kernel.getWidth() / 2, kernel.getHeight() / 2, bufferedImage.getWidth(), bufferedImage.getHeight());
        }
        return bufferedImage4;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static BufferedImage addBorder(BufferedImage bufferedImage, int n, int n2, int n3) {
        int n4 = bufferedImage.getWidth();
        int n5 = bufferedImage.getHeight();
        ColorModel colorModel = bufferedImage.getColorModel();
        WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(n4 + 2 * n, n5 + 2 * n2);
        BufferedImage bufferedImage2 = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
        Graphics2D graphics2D = bufferedImage2.createGraphics();
        try {
            graphics2D.setComposite(AlphaComposite.Src);
            graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            graphics2D.drawImage((Image)bufferedImage, n, n2, null);
            switch (n3) {
                case 2: {
                    graphics2D.drawImage(bufferedImage, n, 0, n + n4, n2, 0, 0, n4, 1, null);
                    graphics2D.drawImage(bufferedImage, -n4 + n, n2, n, n5 + n2, 0, 0, 1, n5, null);
                    graphics2D.drawImage(bufferedImage, n4 + n, n2, 2 * n + n4, n5 + n2, n4 - 1, 0, n4, n5, null);
                    graphics2D.drawImage(bufferedImage, n, n2 + n5, n + n4, 2 * n2 + n5, 0, n5 - 1, n4, n5, null);
                    return bufferedImage2;
                }
                case 3: {
                    graphics2D.drawImage((Image)bufferedImage, -n4 + n, -n5 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n, -n5 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n4 + n, -n5 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, -n4 + n, n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n4 + n, n2, null);
                    graphics2D.drawImage((Image)bufferedImage, -n4 + n, n5 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n, n5 + n2, null);
                    graphics2D.drawImage((Image)bufferedImage, n4 + n, n5 + n2, null);
                    return bufferedImage2;
                }
                default: {
                    throw new IllegalArgumentException("Illegal edge operation " + n3);
                }
            }
        }
        finally {
            graphics2D.dispose();
        }
    }

    public static Image contrast(Image image) {
        return ImageUtil.contrast(image, 0.3f);
    }

    public static Image contrast(Image image, float f) {
        if (f == 0.0f) {
            return image;
        }
        BrightnessContrastFilter brightnessContrastFilter = new BrightnessContrastFilter(0.0f, f);
        return ImageUtil.filter(image, brightnessContrastFilter);
    }

    public static Image brightness(Image image, float f) {
        if (f == 0.0f) {
            return image;
        }
        BrightnessContrastFilter brightnessContrastFilter = new BrightnessContrastFilter(f, 0.0f);
        return ImageUtil.filter(image, brightnessContrastFilter);
    }

    public static Image grayscale(Image image) {
        GrayFilter grayFilter = new GrayFilter();
        return ImageUtil.filter(image, grayFilter);
    }

    public static Image filter(Image image, ImageFilter imageFilter) {
        FilteredImageSource filteredImageSource = new FilteredImageSource(image.getSource(), imageFilter);
        return Toolkit.getDefaultToolkit().createImage(filteredImageSource);
    }

    public static BufferedImage accelerate(Image image) {
        return ImageUtil.accelerate(image, null, DEFAULT_CONFIGURATION);
    }

    public static BufferedImage accelerate(Image image, GraphicsConfiguration graphicsConfiguration) {
        return ImageUtil.accelerate(image, null, graphicsConfiguration);
    }

    static BufferedImage accelerate(Image image, Color color, GraphicsConfiguration graphicsConfiguration) {
        BufferedImage bufferedImage;
        if (image instanceof BufferedImage && (bufferedImage = (BufferedImage)image).getType() != 0 && ImageUtil.equals(bufferedImage.getColorModel(), graphicsConfiguration.getColorModel(bufferedImage.getTransparency()))) {
            return bufferedImage;
        }
        if (image == null) {
            throw new IllegalArgumentException("image == null");
        }
        int n = ImageUtil.getWidth(image);
        int n2 = ImageUtil.getHeight(image);
        BufferedImage bufferedImage2 = ImageUtil.createClear(n, n2, -1, ImageUtil.getTransparency(image), color, graphicsConfiguration);
        ImageUtil.drawOnto(bufferedImage2, image);
        return bufferedImage2;
    }

    private static int getTransparency(Image image) {
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage)image;
            return bufferedImage.getTransparency();
        }
        return 1;
    }

    public static BufferedImage createTransparent(int n, int n2) {
        return ImageUtil.createTransparent(n, n2, -1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BufferedImage createTransparent(int n, int n2, int n3) {
        BufferedImage bufferedImage = ImageUtil.createBuffered(n, n2, n3, 3);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        try {
            graphics2D.setComposite(AlphaComposite.Clear);
            graphics2D.fillRect(0, 0, n, n2);
        }
        finally {
            graphics2D.dispose();
        }
        return bufferedImage;
    }

    public static BufferedImage createClear(int n, int n2, Color color) {
        return ImageUtil.createClear(n, n2, -1, color);
    }

    public static BufferedImage createClear(int n, int n2, int n3, Color color) {
        return ImageUtil.createClear(n, n2, n3, 1, color, DEFAULT_CONFIGURATION);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static BufferedImage createClear(int n, int n2, int n3, int n4, Color color, GraphicsConfiguration graphicsConfiguration) {
        int n5 = color != null ? color.getTransparency() : n4;
        BufferedImage bufferedImage = ImageUtil.createBuffered(n, n2, n3, n5, graphicsConfiguration);
        if (color != null) {
            Graphics2D graphics2D = bufferedImage.createGraphics();
            try {
                graphics2D.setComposite(AlphaComposite.Src);
                graphics2D.setColor(color);
                graphics2D.fillRect(0, 0, n, n2);
            }
            finally {
                graphics2D.dispose();
            }
        }
        return bufferedImage;
    }

    private static BufferedImage createBuffered(int n, int n2, int n3, int n4) {
        return ImageUtil.createBuffered(n, n2, n3, n4, DEFAULT_CONFIGURATION);
    }

    static BufferedImage createBuffered(int n, int n2, int n3, int n4, GraphicsConfiguration graphicsConfiguration) {
        GraphicsEnvironment graphicsEnvironment;
        if (VM_SUPPORTS_ACCELERATION && n3 == -1 && ImageUtil.supportsAcceleration(graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment())) {
            return ImageUtil.getConfiguration(graphicsConfiguration).createCompatibleImage(n, n2, n4);
        }
        return new BufferedImage(n, n2, ImageUtil.getImageType(n3, n4));
    }

    private static GraphicsConfiguration getConfiguration(GraphicsConfiguration graphicsConfiguration) {
        return graphicsConfiguration != null ? graphicsConfiguration : DEFAULT_CONFIGURATION;
    }

    private static int getImageType(int n, int n2) {
        if (n != -1) {
            return n;
        }
        switch (n2) {
            case 1: {
                return 1;
            }
            case 2: 
            case 3: {
                return 2;
            }
        }
        throw new IllegalArgumentException("Unknown transparency type: " + n2);
    }

    private static boolean supportsAcceleration(GraphicsEnvironment graphicsEnvironment) {
        try {
            return !graphicsEnvironment.isHeadlessInstance();
        }
        catch (LinkageError linkageError) {
            VM_SUPPORTS_ACCELERATION = false;
            return false;
        }
    }

    public static int getWidth(Image image) {
        int n = image.getWidth(NULL_COMPONENT);
        if (n < 0) {
            if (!ImageUtil.waitForImage(image)) {
                return -1;
            }
            n = image.getWidth(NULL_COMPONENT);
        }
        return n;
    }

    public static int getHeight(Image image) {
        int n = image.getHeight(NULL_COMPONENT);
        if (n < 0) {
            if (!ImageUtil.waitForImage(image)) {
                return -1;
            }
            n = image.getHeight(NULL_COMPONENT);
        }
        return n;
    }

    public static boolean waitForImage(Image image) {
        return ImageUtil.waitForImages(new Image[]{image}, -1L);
    }

    public static boolean waitForImage(Image image, long l) {
        return ImageUtil.waitForImages(new Image[]{image}, l);
    }

    public static boolean waitForImages(Image[] imageArray) {
        return ImageUtil.waitForImages(imageArray, -1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean waitForImages(Image[] imageArray, long l) {
        boolean bl = true;
        int n = imageArray.length == 1 ? System.identityHashCode(imageArray[0]) : System.identityHashCode(imageArray);
        for (Image image : imageArray) {
            sTracker.addImage(image, n);
            if (!sTracker.checkID(n, false)) continue;
            sTracker.removeImage(image, n);
        }
        try {
            if (l < 0L) {
                sTracker.waitForID(n);
            } else {
                bl = sTracker.waitForID(n, l);
            }
        }
        catch (InterruptedException interruptedException) {
            bl = false;
            return bl;
        }
        finally {
            for (Image image : imageArray) {
                sTracker.removeImage(image, n);
            }
        }
        return bl && !sTracker.isErrorID(n);
    }

    public static boolean hasTransparentPixels(RenderedImage renderedImage, boolean bl) {
        if (renderedImage == null) {
            return false;
        }
        ColorModel colorModel = renderedImage.getColorModel();
        if (!colorModel.hasAlpha()) {
            return false;
        }
        if (colorModel.getTransparency() != 2 && colorModel.getTransparency() != 3) {
            return false;
        }
        Object object = null;
        for (int i = renderedImage.getMinTileY(); i < renderedImage.getNumYTiles(); ++i) {
            for (int j = renderedImage.getMinTileX(); j < renderedImage.getNumXTiles(); ++j) {
                Raster raster = renderedImage.getTile(j, i);
                int n = bl ? Math.max(raster.getWidth() / 10, 1) : 1;
                int n2 = bl ? Math.max(raster.getHeight() / 10, 1) : 1;
                for (int k = 0; k < raster.getHeight(); k += n2) {
                    for (int i2 = 0; i2 < raster.getWidth(); i2 += n) {
                        if (colorModel.getAlpha(object = raster.getDataElements(i2, k, object)) == 255) continue;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Color createTranslucent(Color color, int n) {
        return new Color((n & 0xFF) << 24 | color.getRGB() & 0xFFFFFF, true);
    }

    static int blend(int n, int n2) {
        return (((n ^ n2) & 0xFEFEFEFE) >> 1) + (n & n2);
    }

    public static Color blend(Color color, Color color2) {
        return new Color(ImageUtil.blend(color.getRGB(), color2.getRGB()), true);
    }

    public static Color blend(Color color, Color color2, float f) {
        float f2 = 1.0f - f;
        return new Color(ImageUtil.clamp((float)color.getRed() * f2 + (float)color2.getRed() * f), ImageUtil.clamp((float)color.getGreen() * f2 + (float)color2.getGreen() * f), ImageUtil.clamp((float)color.getBlue() * f2 + (float)color2.getBlue() * f), ImageUtil.clamp((float)color.getAlpha() * f2 + (float)color2.getAlpha() * f));
    }

    private static int clamp(float f) {
        return (int)f;
    }
}

