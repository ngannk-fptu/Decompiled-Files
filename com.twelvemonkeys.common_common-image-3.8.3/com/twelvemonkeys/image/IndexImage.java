/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.io.FileUtil
 *  com.twelvemonkeys.lang.StringUtil
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.BufferedImageFactory;
import com.twelvemonkeys.image.CopyDither;
import com.twelvemonkeys.image.DiffusionDither;
import com.twelvemonkeys.image.ImageConversionException;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.InverseColorMapIndexColorModel;
import com.twelvemonkeys.image.MonochromeColorModel;
import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.lang.StringUtil;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

class IndexImage {
    protected static final int DITHER_MASK = 255;
    public static final int DITHER_DEFAULT = 0;
    public static final int DITHER_NONE = 1;
    public static final int DITHER_DIFFUSION = 2;
    public static final int DITHER_DIFFUSION_ALTSCANS = 3;
    protected static final int COLOR_SELECTION_MASK = 65280;
    public static final int COLOR_SELECTION_DEFAULT = 0;
    public static final int COLOR_SELECTION_FAST = 256;
    public static final int COLOR_SELECTION_QUALITY = 512;
    protected static final int TRANSPARENCY_MASK = 0xFF0000;
    public static final int TRANSPARENCY_DEFAULT = 0;
    public static final int TRANSPARENCY_OPAQUE = 65536;
    public static final int TRANSPARENCY_BITMASK = 131072;
    protected static final int TRANSPARENCY_TRANSLUCENT = 196608;

    private IndexImage() {
    }

    @Deprecated
    public static IndexColorModel getIndexColorModel(Image image, int n, boolean bl) {
        return IndexImage.getIndexColorModel(image, n, bl ? 256 : 512);
    }

    public static IndexColorModel getIndexColorModel(Image image, int n, int n2) throws ImageConversionException {
        IndexColorModel indexColorModel = null;
        RenderedImage renderedImage = null;
        if (image instanceof RenderedImage) {
            renderedImage = (RenderedImage)((Object)image);
            ColorModel colorModel = renderedImage.getColorModel();
            if (colorModel instanceof IndexColorModel && ((IndexColorModel)colorModel).getMapSize() <= n) {
                indexColorModel = (IndexColorModel)colorModel;
            }
        } else {
            BufferedImageFactory bufferedImageFactory = new BufferedImageFactory(image);
            ColorModel colorModel = bufferedImageFactory.getColorModel();
            if (colorModel instanceof IndexColorModel && ((IndexColorModel)colorModel).getMapSize() <= n) {
                indexColorModel = (IndexColorModel)colorModel;
            } else {
                renderedImage = bufferedImageFactory.getBufferedImage();
            }
        }
        if (indexColorModel == null) {
            indexColorModel = IndexImage.createIndexColorModel(ImageUtil.toBuffered(renderedImage), n, n2);
        } else if (!(indexColorModel instanceof InverseColorMapIndexColorModel)) {
            indexColorModel = new InverseColorMapIndexColorModel(indexColorModel);
        }
        return indexColorModel;
    }

    private static IndexColorModel createIndexColorModel(BufferedImage bufferedImage, int n, int n2) {
        int n3;
        int n4;
        Object object;
        Object object2;
        int n5;
        int n6;
        boolean bl = IndexImage.isTransparent(n2);
        if (bl) {
            --n;
        }
        int n7 = bufferedImage.getWidth();
        int n8 = bufferedImage.getHeight();
        List[] listArray = new List[4096];
        int n9 = 1;
        if (IndexImage.isFast(n2)) {
            n9 += n7 * n8 / 16384;
        }
        int n10 = 0;
        for (n6 = 0; n6 < n7; ++n6) {
            block1: for (n5 = n6 % n9; n5 < n8; n5 += n9) {
                ++n10;
                int n11 = bufferedImage.getRGB(n6, n5) & 0xFFFFFF;
                int n12 = (n11 & 0xF00000) >>> 12 | (n11 & 0xF000) >>> 8 | (n11 & 0xF0) >>> 4;
                object2 = listArray[n12];
                if (object2 == null) {
                    object2 = new ArrayList();
                    object2.add(new Counter(n11));
                    listArray[n12] = object2;
                    continue;
                }
                object = object2.iterator();
                while (object.hasNext()) {
                    if (!((Counter)object.next()).add(n11)) continue;
                    continue block1;
                }
                object2.add(new Counter(n11));
            }
        }
        n6 = 1;
        n5 = 0;
        Cube[] cubeArray = new Cube[n];
        cubeArray[0] = new Cube(listArray, n10);
        while (n6 < n) {
            while (cubeArray[n5].isDone() && ++n5 != n6) {
            }
            if (n5 == n6) break;
            object2 = cubeArray[n5];
            object = ((Cube)object2).split();
            if (object == null) continue;
            if (((Cube)object).count > ((Cube)object2).count) {
                Object object3 = object2;
                object2 = object;
                object = object3;
            }
            int n13 = n5;
            n4 = ((Cube)object2).count;
            for (n3 = n5 + 1; n3 < n6 && cubeArray[n3].count >= n4; ++n3) {
                cubeArray[n13++] = cubeArray[n3];
            }
            cubeArray[n13++] = object2;
            n4 = ((Cube)object).count;
            while (n13 < n6 && cubeArray[n13].count >= n4) {
                ++n13;
            }
            System.arraycopy(cubeArray, n13, cubeArray, n13 + 1, n6 - n13);
            cubeArray[n13] = object;
            ++n6;
        }
        object2 = new byte[bl ? n6 + 1 : n6];
        object = new byte[bl ? n6 + 1 : n6];
        byte[] byArray = new byte[bl ? n6 + 1 : n6];
        for (n4 = 0; n4 < n6; ++n4) {
            n3 = cubeArray[n4].averageColor();
            object2[n4] = (byte)(n3 >> 16 & 0xFF);
            object[n4] = (byte)(n3 >> 8 & 0xFF);
            byArray[n4] = (byte)(n3 & 0xFF);
        }
        n4 = 8;
        InverseColorMapIndexColorModel inverseColorMapIndexColorModel = bl ? new InverseColorMapIndexColorModel(n4, ((Object)object2).length, (byte[])object2, (byte[])object, byArray, ((Object)object2).length - 1) : new InverseColorMapIndexColorModel(n4, ((Object)object2).length, (byte[])object2, (byte[])object, byArray);
        return inverseColorMapIndexColorModel;
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage) {
        return IndexImage.getIndexedImage(bufferedImage, 256, 0);
    }

    private static boolean isFast(int n) {
        return (n & 0xFF00) != 512;
    }

    static boolean isTransparent(int n) {
        return (n & 0x20000) != 0 || (n & 0x30000) != 0;
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, Image image, Color color, int n) throws ImageConversionException {
        return IndexImage.getIndexedImage(bufferedImage, IndexImage.getIndexColorModel(image, 256, n), color, n);
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, int n, Color color, int n2) {
        IndexColorModel indexColorModel = color != null ? IndexImage.getIndexColorModel((Image)IndexImage.createSolid(bufferedImage, color), n, n2) : IndexImage.getIndexColorModel((Image)bufferedImage, n, n2);
        if ((n2 & 0xFF) != 1 && indexColorModel.getMapSize() < n) {
            n2 = n2 & 0xFFFFFF00 | 1;
        }
        return IndexImage.getIndexedImage(bufferedImage, indexColorModel, color, n2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, IndexColorModel indexColorModel, Color color, int n) {
        int n2 = bufferedImage.getWidth();
        int n3 = bufferedImage.getHeight();
        boolean bl = IndexImage.isTransparent(n) && bufferedImage.getColorModel().getTransparency() != 1 && indexColorModel.getTransparency() != 1;
        BufferedImage bufferedImage2 = bufferedImage;
        if (color != null) {
            bufferedImage2 = IndexImage.createSolid(bufferedImage, color);
        }
        BufferedImage bufferedImage3 = indexColorModel.getMapSize() > 2 ? new BufferedImage(n2, n3, 13, indexColorModel) : new BufferedImage(n2, n3, 12, indexColorModel);
        switch (n & 0xFF) {
            case 2: 
            case 3: {
                DiffusionDither diffusionDither = new DiffusionDither(indexColorModel);
                if ((n & 0xFF) == 3) {
                    diffusionDither.setAlternateScans(true);
                }
                diffusionDither.filter(bufferedImage2, bufferedImage3);
                break;
            }
            case 1: {
                CopyDither copyDither = new CopyDither(indexColorModel);
                copyDither.filter(bufferedImage2, bufferedImage3);
                break;
            }
            default: {
                Graphics2D graphics2D = bufferedImage3.createGraphics();
                try {
                    RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                    graphics2D.setRenderingHints(renderingHints);
                    graphics2D.drawImage((Image)bufferedImage2, 0, 0, null);
                    break;
                }
                finally {
                    graphics2D.dispose();
                }
            }
        }
        if (bl) {
            IndexImage.applyAlpha(bufferedImage3, bufferedImage);
        }
        return bufferedImage3;
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, int n, int n2) {
        return IndexImage.getIndexedImage(bufferedImage, n, null, n2);
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, IndexColorModel indexColorModel, int n) {
        return IndexImage.getIndexedImage(bufferedImage, indexColorModel, null, n);
    }

    public static BufferedImage getIndexedImage(BufferedImage bufferedImage, Image image, int n) {
        return IndexImage.getIndexedImage(bufferedImage, image, null, n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static BufferedImage createSolid(BufferedImage bufferedImage, Color color) {
        BufferedImage bufferedImage2 = new BufferedImage(bufferedImage.getColorModel(), bufferedImage.copyData(null), bufferedImage.isAlphaPremultiplied(), null);
        Graphics2D graphics2D = bufferedImage2.createGraphics();
        try {
            graphics2D.setColor(color);
            graphics2D.setComposite(AlphaComposite.DstOver);
            graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        }
        finally {
            graphics2D.dispose();
        }
        return bufferedImage2;
    }

    private static void applyAlpha(BufferedImage bufferedImage, BufferedImage bufferedImage2) {
        for (int i = 0; i < bufferedImage2.getHeight(); ++i) {
            for (int j = 0; j < bufferedImage2.getWidth(); ++j) {
                if ((bufferedImage2.getRGB(j, i) >> 24 & 0xFF) >= 64) continue;
                bufferedImage.setRGB(j, i, 0xFFFFFF);
            }
        }
    }

    public static void main(String[] stringArray) {
        IndexColorModel indexColorModel;
        BufferedImage bufferedImage;
        Object object;
        File file;
        Object object2;
        Object object3;
        int n = 0;
        int n2 = -1;
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        int n3 = 256;
        String string = null;
        String string2 = null;
        String string3 = null;
        Color color = null;
        boolean bl4 = false;
        String string4 = null;
        boolean bl5 = false;
        while (n < stringArray.length && stringArray[n].charAt(0) == '-' && stringArray[n].length() >= 2) {
            if (stringArray[n].charAt(1) == 's' || stringArray[n].equals("--speedtest")) {
                if (stringArray.length > ++n && stringArray[n].charAt(0) != '-') {
                    try {
                        n2 = Integer.parseInt(stringArray[n++]);
                        continue;
                    }
                    catch (NumberFormatException numberFormatException) {
                        bl5 = true;
                        break;
                    }
                }
                n2 = 10;
                continue;
            }
            if (stringArray[n].charAt(1) == 'w' || stringArray[n].equals("--overwrite")) {
                bl = true;
                ++n;
                continue;
            }
            if (stringArray[n].charAt(1) == 'c' || stringArray[n].equals("--colors")) {
                ++n;
                try {
                    n3 = Integer.parseInt(stringArray[n++]);
                    continue;
                }
                catch (NumberFormatException numberFormatException) {
                    bl5 = true;
                    break;
                }
            }
            if (stringArray[n].charAt(1) == 'g' || stringArray[n].equals("--grayscale")) {
                ++n;
                bl3 = true;
                continue;
            }
            if (stringArray[n].charAt(1) == 'm' || stringArray[n].equals("--monochrome")) {
                ++n;
                n3 = 2;
                bl2 = true;
                continue;
            }
            if (stringArray[n].charAt(1) == 'd' || stringArray[n].equals("--dither")) {
                int n4 = ++n;
                ++n;
                string = stringArray[n4];
                continue;
            }
            if (stringArray[n].charAt(1) == 'p' || stringArray[n].equals("--palette")) {
                int n5 = ++n;
                ++n;
                string4 = stringArray[n5];
                continue;
            }
            if (stringArray[n].charAt(1) == 'q' || stringArray[n].equals("--quality")) {
                int n6 = ++n;
                ++n;
                string2 = stringArray[n6];
                continue;
            }
            if (stringArray[n].charAt(1) == 'b' || stringArray[n].equals("--bgcolor")) {
                ++n;
                try {
                    color = StringUtil.toColor((String)stringArray[n++]);
                    continue;
                }
                catch (Exception exception) {
                    bl5 = true;
                    break;
                }
            }
            if (stringArray[n].charAt(1) == 't' || stringArray[n].equals("--transparency")) {
                ++n;
                bl4 = true;
                continue;
            }
            if (stringArray[n].charAt(1) == 'f' || stringArray[n].equals("--outputformat")) {
                int n7 = ++n;
                ++n;
                string3 = StringUtil.toLowerCase((String)stringArray[n7]);
                continue;
            }
            if (stringArray[n].charAt(1) == 'h' || stringArray[n].equals("--help")) {
                ++n;
                bl5 = true;
                continue;
            }
            System.err.println("Unknown option \"" + stringArray[n++] + "\"");
        }
        if (bl5 || stringArray.length < n + 1) {
            System.err.println("Usage: IndexImage [--help|-h] [--speedtest|-s <integer>] [--bgcolor|-b <color>] [--colors|-c <integer> | --grayscale|g | --monochrome|-m | --palette|-p <file>] [--dither|-d (default|diffusion|none)] [--quality|-q (default|high|low)] [--transparency|-t] [--outputformat|-f (gif|jpeg|png|wbmp|...)] [--overwrite|-w] <input> [<output>]");
            System.err.print("Input format names: ");
            object3 = ImageIO.getReaderFormatNames();
            for (int i = 0; i < ((String[])object3).length; ++i) {
                System.err.print((String)object3[i] + (i + 1 < ((Object)object3).length ? ", " : "\n"));
            }
            System.err.print("Output format names: ");
            object2 = ImageIO.getWriterFormatNames();
            for (int i = 0; i < ((String[])object2).length; ++i) {
                System.err.print((String)object2[i] + (i + 1 < ((Object)object2).length ? ", " : "\n"));
            }
            System.exit(5);
        }
        if (!((File)(object3 = new File(stringArray[n++]))).exists()) {
            System.err.println("File \"" + ((File)object3).getAbsolutePath() + "\" does not exist!");
            System.exit(5);
        }
        object2 = null;
        if (string4 != null && !((File)(object2 = new File(string4))).exists()) {
            System.err.println("File \"" + ((File)object3).getAbsolutePath() + "\" does not exist!");
            System.exit(5);
        }
        if (n < stringArray.length) {
            file = new File(stringArray[n]);
            if (string3 == null) {
                string3 = FileUtil.getExtension((File)file);
            }
        } else {
            object = FileUtil.getBasename((File)object3);
            if (string3 == null) {
                string3 = "png";
            }
            file = new File((String)object + '.' + string3);
        }
        if (!bl && file.exists()) {
            System.err.println("The file \"" + file.getAbsolutePath() + "\" allready exists!");
            System.exit(5);
        }
        object = null;
        BufferedImage bufferedImage2 = null;
        try {
            object = ImageIO.read((File)object3);
            if (object == null) {
                System.err.println("No reader for image: \"" + ((File)object3).getAbsolutePath() + "\"!");
                System.exit(5);
            }
            if (object2 != null && (bufferedImage2 = ImageIO.read((File)object2)) == null) {
                System.err.println("No reader for image: \"" + ((File)object2).getAbsolutePath() + "\"!");
                System.exit(5);
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace(System.err);
            System.exit(5);
        }
        int n8 = 0;
        if ("DIFFUSION".equalsIgnoreCase(string)) {
            n8 |= 2;
        } else if ("DIFFUSION_ALTSCANS".equalsIgnoreCase(string)) {
            n8 |= 3;
        } else if ("NONE".equalsIgnoreCase(string)) {
            n8 |= 1;
        }
        if ("HIGH".equalsIgnoreCase(string2)) {
            n8 |= 0x200;
        } else if ("LOW".equalsIgnoreCase(string2)) {
            n8 |= 0x100;
        }
        if (bl4) {
            n8 |= 0x20000;
        }
        if (color != null && bufferedImage2 == null) {
            bufferedImage2 = IndexImage.createSolid((BufferedImage)object, color);
        }
        long l = 0L;
        if (n2 > 0) {
            System.out.println("Measuring speed!");
            l = System.currentTimeMillis();
        }
        if (bl2) {
            bufferedImage = IndexImage.getIndexedImage((BufferedImage)object, MonochromeColorModel.getInstance(), color, n8);
            indexColorModel = MonochromeColorModel.getInstance();
        } else if (bl3) {
            object = ImageUtil.toBuffered(ImageUtil.grayscale((Image)object));
            indexColorModel = IndexImage.getIndexColorModel((Image)object, n3, n8);
            bufferedImage = IndexImage.getIndexedImage((BufferedImage)object, indexColorModel, color, n8);
            if (n2 > 0) {
                indexColorModel = IndexImage.getIndexColorModel((Image)bufferedImage, n3, n8);
            }
        } else if (bufferedImage2 != null) {
            indexColorModel = IndexImage.getIndexColorModel((Image)bufferedImage2, n3, n8);
            bufferedImage = IndexImage.getIndexedImage(ImageUtil.toBuffered((BufferedImage)object, 2), indexColorModel, color, n8);
        } else {
            object = ImageUtil.toBuffered((BufferedImage)object, 2);
            indexColorModel = IndexImage.getIndexColorModel((Image)object, n3, n8);
            bufferedImage = IndexImage.getIndexedImage((BufferedImage)object, indexColorModel, color, n8);
        }
        if (n2 > 0) {
            System.out.println("Color selection + dither: " + (System.currentTimeMillis() - l) + " ms");
        }
        try {
            if (!ImageIO.write((RenderedImage)bufferedImage, string3, file)) {
                System.err.println("No writer for format: \"" + string3 + "\"!");
            }
        }
        catch (IOException iOException) {
            iOException.printStackTrace(System.err);
        }
        if (n2 > 0) {
            System.out.println("Measuring speed!");
            for (int i = 0; i < 10; ++i) {
                IndexImage.getIndexedImage((BufferedImage)object, indexColorModel, color, n8);
            }
            long l2 = 0L;
            for (int i = 0; i < n2; ++i) {
                l = System.currentTimeMillis();
                IndexImage.getIndexedImage((BufferedImage)object, indexColorModel, color, n8);
                l2 += System.currentTimeMillis() - l;
                System.out.print('.');
                if ((i + 1) % 10 != 0) continue;
                System.out.println("\nAverage (after " + (i + 1) + " iterations): " + l2 / (long)(i + 1) + "ms");
            }
            System.out.println("\nDither only:");
            System.out.println("Total time (" + n2 + " invocations): " + l2 + "ms");
            System.out.println("Average: " + l2 / (long)n2 + "ms");
        }
    }

    private static class Cube {
        int[] min = new int[]{0, 0, 0};
        int[] max = new int[]{255, 255, 255};
        boolean done = false;
        List<Counter>[] colors = null;
        int count = 0;
        static final int RED = 0;
        static final int GRN = 1;
        static final int BLU = 2;

        public Cube(List<Counter>[] listArray, int n) {
            this.colors = listArray;
            this.count = n;
        }

        public boolean isDone() {
            return this.done;
        }

        public Cube split() {
            int n;
            int n2;
            int n3;
            int n4 = this.max[0] - this.min[0] + 1;
            int n5 = this.max[1] - this.min[1] + 1;
            int n6 = this.max[2] - this.min[2] + 1;
            if (n4 >= n5) {
                n3 = 1;
                if (n4 >= n6) {
                    n2 = 0;
                    n = 2;
                } else {
                    n2 = 2;
                    n = 0;
                }
            } else if (n5 >= n6) {
                n2 = 1;
                n3 = 0;
                n = 2;
            } else {
                n2 = 2;
                n3 = 0;
                n = 1;
            }
            Cube cube = this.splitChannel(n2, n3, n);
            if (cube != null) {
                return cube;
            }
            cube = this.splitChannel(n3, n2, n);
            if (cube != null) {
                return cube;
            }
            cube = this.splitChannel(n, n2, n3);
            if (cube != null) {
                return cube;
            }
            this.done = true;
            return null;
        }

        public Cube splitChannel(int n, int n2, int n3) {
            int n4;
            int n5;
            int n6;
            int n7;
            int n8;
            if (this.min[n] == this.max[n]) {
                return null;
            }
            int n9 = (2 - n) * 4;
            int n10 = (2 - n2) * 4;
            int n11 = (2 - n3) * 4;
            int n12 = this.count / 2;
            int[] nArray = new int[256];
            int n13 = 0;
            int[] nArray2 = new int[]{this.min[0] >> 4, this.min[1] >> 4, this.min[2] >> 4};
            int[] nArray3 = new int[]{this.max[0] >> 4, this.max[1] >> 4, this.max[2] >> 4};
            int n14 = this.min[0];
            int n15 = this.min[1];
            int n16 = this.min[2];
            int n17 = this.max[0];
            int n18 = this.max[1];
            int n19 = this.max[2];
            int[] nArray4 = new int[]{0, 0, 0};
            for (n8 = nArray2[n]; n8 <= nArray3[n]; ++n8) {
                n7 = n8 << n9;
                for (n6 = nArray2[n2]; n6 <= nArray3[n2]; ++n6) {
                    n5 = n7 | n6 << n10;
                    for (n4 = nArray2[n3]; n4 <= nArray3[n3]; ++n4) {
                        int n20 = n5 | n4 << n11;
                        List<Counter> list = this.colors[n20];
                        if (list == null) continue;
                        for (Counter counter : list) {
                            int n21 = counter.val;
                            nArray4[0] = (n21 & 0xFF0000) >> 16;
                            nArray4[1] = (n21 & 0xFF00) >> 8;
                            nArray4[2] = n21 & 0xFF;
                            if (nArray4[0] < n14 || nArray4[0] > n17 || nArray4[1] < n15 || nArray4[1] > n18 || nArray4[2] < n16 || nArray4[2] > n19) continue;
                            int n22 = nArray4[n];
                            nArray[n22] = nArray[n22] + counter.count;
                            n13 += counter.count;
                        }
                    }
                }
                if (n13 >= n12) break;
            }
            n13 = 0;
            n8 = -1;
            n7 = this.min[n];
            n6 = this.max[n];
            for (n5 = this.min[n]; n5 <= this.max[n]; ++n5) {
                n4 = nArray[n5];
                if (n4 == 0) {
                    if (n13 != 0 || n5 >= this.max[n]) continue;
                    this.min[n] = n5 + 1;
                    continue;
                }
                if (n13 + n4 < n12) {
                    n8 = n5;
                    n13 += n4;
                    continue;
                }
                if (n12 - n13 <= n13 + n4 - n12) {
                    if (n8 == -1) {
                        if (n4 == this.count) {
                            this.max[n] = n5;
                            return null;
                        }
                        n7 = n5;
                        n6 = n5 + 1;
                        break;
                    }
                    n7 = n8;
                    n6 = n5;
                    break;
                }
                if (n5 == this.max[n]) {
                    if (n4 == this.count) {
                        return null;
                    }
                    n7 = n8;
                    n6 = n5;
                    break;
                }
                n13 += n4;
                n7 = n5;
                n6 = n5 + 1;
                break;
            }
            Cube cube = new Cube(this.colors, n13);
            this.count -= n13;
            cube.min[n] = this.min[n];
            cube.max[n] = n7;
            this.min[n] = n6;
            cube.min[n2] = this.min[n2];
            cube.max[n2] = this.max[n2];
            cube.min[n3] = this.min[n3];
            cube.max[n3] = this.max[n3];
            return cube;
        }

        public int averageColor() {
            if (this.count == 0) {
                return 0;
            }
            float f = 0.0f;
            float f2 = 0.0f;
            float f3 = 0.0f;
            int n = this.min[0];
            int n2 = this.min[1];
            int n3 = this.min[2];
            int n4 = this.max[0];
            int n5 = this.max[1];
            int n6 = this.max[2];
            int[] nArray = new int[]{n >> 4, n2 >> 4, n3 >> 4};
            int[] nArray2 = new int[]{n4 >> 4, n5 >> 4, n6 >> 4};
            for (int i = nArray[0]; i <= nArray2[0]; ++i) {
                int n7 = i << 8;
                for (int j = nArray[1]; j <= nArray2[1]; ++j) {
                    int n8 = n7 | j << 4;
                    for (int k = nArray[2]; k <= nArray2[2]; ++k) {
                        int n9 = n8 | k;
                        List<Counter> list = this.colors[n9];
                        if (list == null) continue;
                        for (Counter counter : list) {
                            int n10 = counter.val;
                            int n11 = (n10 & 0xFF0000) >> 16;
                            int n12 = (n10 & 0xFF00) >> 8;
                            int n13 = n10 & 0xFF;
                            if (n11 < n || n11 > n4 || n12 < n2 || n12 > n5 || n13 < n3 || n13 > n6) continue;
                            float f4 = (float)counter.count / (float)this.count;
                            f += (float)n11 * f4;
                            f2 += (float)n12 * f4;
                            f3 += (float)n13 * f4;
                        }
                    }
                }
            }
            return (int)(f + 0.5f) << 16 | (int)(f2 + 0.5f) << 8 | (int)(f3 + 0.5f);
        }
    }

    private static class Counter {
        public int val;
        public int count = 1;

        public Counter(int n) {
            this.val = n;
        }

        public boolean add(int n) {
            if (this.val != n) {
                return false;
            }
            ++this.count;
            return true;
        }
    }
}

