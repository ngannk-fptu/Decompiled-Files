/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.ImageReaderBase
 *  com.twelvemonkeys.imageio.stream.SubImageInputStream
 *  com.twelvemonkeys.imageio.util.IIOUtil
 *  com.twelvemonkeys.imageio.util.ImageTypeSpecifiers
 */
package com.twelvemonkeys.imageio.plugins.icns;

import com.twelvemonkeys.imageio.ImageReaderBase;
import com.twelvemonkeys.imageio.plugins.icns.ICNS;
import com.twelvemonkeys.imageio.plugins.icns.ICNS1BitColorModel;
import com.twelvemonkeys.imageio.plugins.icns.ICNS4BitColorModel;
import com.twelvemonkeys.imageio.plugins.icns.ICNS8BitColorModel;
import com.twelvemonkeys.imageio.plugins.icns.ICNSImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.icns.ICNSUtil;
import com.twelvemonkeys.imageio.plugins.icns.IconResource;
import com.twelvemonkeys.imageio.plugins.icns.SipsJP2Reader;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.imageio.util.ImageTypeSpecifiers;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public final class ICNSImageReader
extends ImageReaderBase {
    private List<IconResource> icons = new ArrayList<IconResource>();
    private List<IconResource> masks = new ArrayList<IconResource>();
    private IconResource lastResourceRead;
    private int length;

    public ICNSImageReader() {
        this((ImageReaderSpi)((Object)new ICNSImageReaderSpi()));
    }

    ICNSImageReader(ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
    }

    protected void resetMembers() {
        this.length = 0;
        this.lastResourceRead = null;
        this.icons.clear();
        this.masks.clear();
    }

    public int getWidth(int n) throws IOException {
        return this.readIconResource((int)n).size().width;
    }

    public int getHeight(int n) throws IOException {
        return this.readIconResource((int)n).size().height;
    }

    public ImageTypeSpecifier getRawImageType(int n) throws IOException {
        IconResource iconResource = this.readIconResource(n);
        switch (iconResource.depth()) {
            case 1: {
                return ImageTypeSpecifiers.createFromIndexColorModel((IndexColorModel)ICNS1BitColorModel.INSTANCE);
            }
            case 4: {
                return ImageTypeSpecifiers.createFromIndexColorModel((IndexColorModel)ICNS4BitColorModel.INSTANCE);
            }
            case 8: {
                return ImageTypeSpecifiers.createFromIndexColorModel((IndexColorModel)ICNS8BitColorModel.INSTANCE);
            }
            case 32: {
                if (iconResource.isCompressed()) {
                    return ImageTypeSpecifiers.createBanded((ColorSpace)ColorSpace.getInstance(1000), (int[])new int[]{0, 1, 2, 3}, (int[])ICNSImageReader.createBandOffsets(iconResource.size().width * iconResource.size().height), (int)0, (boolean)true, (boolean)false);
                }
                return ImageTypeSpecifiers.createInterleaved((ColorSpace)ColorSpace.getInstance(1000), (int[])new int[]{1, 2, 3, 0}, (int)0, (boolean)true, (boolean)false);
            }
        }
        throw new IllegalStateException(String.format("Unknown bit depth: %d", iconResource.depth()));
    }

    private static int[] createBandOffsets(int n) {
        return new int[]{0, n, 2 * n, 3 * n};
    }

    public Iterator<ImageTypeSpecifier> getImageTypes(int n) throws IOException {
        ImageTypeSpecifier imageTypeSpecifier = this.getRawImageType(n);
        IconResource iconResource = this.readIconResource(n);
        ArrayList<ImageTypeSpecifier> arrayList = new ArrayList<ImageTypeSpecifier>();
        switch (iconResource.depth()) {
            case 1: 
            case 4: 
            case 8: 
            case 32: {
                arrayList.add(ImageTypeSpecifiers.createPacked((ColorSpace)ColorSpace.getInstance(1000), (int)0xFF0000, (int)65280, (int)255, (int)-16777216, (int)3, (boolean)false));
                arrayList.add(ImageTypeSpecifiers.createInterleaved((ColorSpace)ColorSpace.getInstance(1000), (int[])new int[]{3, 2, 1, 0}, (int)0, (boolean)true, (boolean)false));
                break;
            }
            default: {
                throw new IllegalStateException(String.format("Unknown bit depth: %d", iconResource.depth()));
            }
        }
        arrayList.add(imageTypeSpecifier);
        return arrayList.iterator();
    }

    public int getNumImages(boolean bl) throws IOException {
        this.assertInput();
        if (!bl) {
            return -1;
        }
        int n = this.icons.size();
        try {
            while (true) {
                this.readIconResource(n++);
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return this.icons.size();
        }
    }

    public BufferedImage read(int n, ImageReadParam imageReadParam) throws IOException {
        IconResource iconResource = this.readIconResource(n);
        this.imageInput.seek(iconResource.start + 8L);
        if (iconResource.isForeignFormat()) {
            return this.readForeignFormat(n, imageReadParam, iconResource);
        }
        return this.readICNSFormat(n, imageReadParam, iconResource);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BufferedImage readICNSFormat(int n, ImageReadParam imageReadParam, IconResource iconResource) throws IOException {
        Object object;
        int n2;
        byte[] byArray;
        Dimension dimension = iconResource.size();
        int n3 = dimension.width;
        int n4 = dimension.height;
        BufferedImage bufferedImage = ICNSImageReader.getDestination((ImageReadParam)imageReadParam, this.getImageTypes(n), (int)n3, (int)n4);
        ImageTypeSpecifier imageTypeSpecifier = this.getRawImageType(n);
        if (imageTypeSpecifier.getColorModel() instanceof IndexColorModel && imageTypeSpecifier.getBufferedImageType() != bufferedImage.getType()) {
            ICNSImageReader.checkReadParamBandSettings((ImageReadParam)imageReadParam, (int)4, (int)bufferedImage.getSampleModel().getNumBands());
        } else {
            ICNSImageReader.checkReadParamBandSettings((ImageReadParam)imageReadParam, (int)imageTypeSpecifier.getNumBands(), (int)bufferedImage.getSampleModel().getNumBands());
        }
        Rectangle rectangle = new Rectangle();
        Rectangle rectangle2 = new Rectangle();
        ICNSImageReader.computeRegions((ImageReadParam)imageReadParam, (int)n3, (int)n4, (BufferedImage)bufferedImage, (Rectangle)rectangle, (Rectangle)rectangle2);
        this.processImageStarted(n);
        if (iconResource.isCompressed()) {
            byArray = new byte[n3 * n4 * iconResource.depth() / 8];
            n2 = iconResource.length - 8;
            if (n3 >= 128 && n4 >= 128) {
                this.imageInput.skipBytes(4);
                n2 -= 4;
            }
            object = IIOUtil.createStreamAdapter((ImageInputStream)this.imageInput, (long)n2);
            try {
                ICNSUtil.decompress(new DataInputStream((InputStream)object), byArray, 0, byArray.length * 24 / 32);
            }
            finally {
                ((InputStream)object).close();
            }
        } else {
            byArray = new byte[iconResource.length - 8];
            this.imageInput.readFully(byArray);
        }
        if (iconResource.depth() == 1) {
            DataBufferByte dataBufferByte = new DataBufferByte(byArray, byArray.length / 2, 0);
            object = Raster.createPackedRaster(dataBufferByte, n3, n4, iconResource.depth(), null);
            if (bufferedImage.getType() == imageTypeSpecifier.getBufferedImageType() && ((IndexColorModel)bufferedImage.getColorModel()).getMapSize() == 2) {
                bufferedImage.setData((Raster)object);
            } else {
                DataBufferByte dataBufferByte2 = new DataBufferByte(byArray, byArray.length / 2, byArray.length / 2);
                WritableRaster writableRaster = Raster.createPackedRaster(dataBufferByte2, n3, n4, iconResource.depth(), null);
                Graphics2D graphics2D = bufferedImage.createGraphics();
                try {
                    BufferedImage bufferedImage2 = new BufferedImage(imageTypeSpecifier.getColorModel(), (WritableRaster)object, false, null);
                    graphics2D.drawImage((Image)bufferedImage2, 0, 0, null);
                    bufferedImage2 = new BufferedImage(ICNSBitMaskColorModel.INSTANCE, writableRaster, false, null);
                    bufferedImage2.setData(writableRaster);
                    graphics2D.setComposite(AlphaComposite.DstIn);
                    graphics2D.drawImage((Image)bufferedImage2, 0, 0, null);
                }
                finally {
                    graphics2D.dispose();
                }
            }
        } else if (iconResource.depth() <= 8) {
            DataBufferByte dataBufferByte = new DataBufferByte(byArray, byArray.length);
            object = Raster.createPackedRaster(dataBufferByte, n3, n4, iconResource.depth(), null);
            if (bufferedImage.getType() == imageTypeSpecifier.getBufferedImageType()) {
                bufferedImage.setData((Raster)object);
            } else {
                Object object2;
                Graphics2D graphics2D = bufferedImage.createGraphics();
                try {
                    object2 = new BufferedImage(imageTypeSpecifier.getColorModel(), (WritableRaster)object, false, null);
                    graphics2D.drawImage((Image)object2, 0, 0, null);
                }
                finally {
                    graphics2D.dispose();
                }
                this.processImageProgress(50.0f);
                object2 = this.findMaskResource(iconResource);
                if (object2 != null) {
                    Raster raster = this.readMask((IconResource)object2);
                    bufferedImage.getAlphaRaster().setRect(raster);
                }
            }
        } else {
            n2 = byArray.length / 4;
            object = new DataBufferByte(byArray, byArray.length);
            WritableRaster writableRaster = iconResource.isCompressed() ? Raster.createBandedRaster((DataBuffer)object, n3, n4, n3, new int[]{0, 0, 0, 0}, ICNSImageReader.createBandOffsets(n2), null) : Raster.createInterleavedRaster((DataBuffer)object, n3, n4, n3 * 4, 4, new int[]{1, 2, 3, 0}, null);
            bufferedImage.setData(writableRaster);
            this.processImageProgress(75.0f);
            IconResource iconResource2 = this.findMaskResource(iconResource);
            if (iconResource2 != null) {
                Raster raster = this.readMask(iconResource2);
                bufferedImage.getAlphaRaster().setRect(raster);
            } else {
                byte[] byArray2 = new byte[n3 * n4];
                Arrays.fill(byArray2, (byte)-1);
                WritableRaster writableRaster2 = Raster.createBandedRaster(new DataBufferByte(byArray2, byArray2.length), n3, n4, n3, new int[]{0}, new int[]{0}, null);
                bufferedImage.getAlphaRaster().setRect(writableRaster2);
            }
        }
        this.processImageProgress(100.0f);
        if (this.abortRequested()) {
            this.processReadAborted();
        } else {
            this.processImageComplete();
        }
        return bufferedImage;
    }

    private Raster readMask(IconResource iconResource) throws IOException {
        Dimension dimension = iconResource.size();
        int n = dimension.width;
        int n2 = dimension.height;
        byte[] byArray = new byte[n * n2];
        this.imageInput.seek(iconResource.start + 8L);
        if (iconResource.isMaskType()) {
            this.imageInput.readFully(byArray, 0, iconResource.length - 8);
        } else if (iconResource.hasMask()) {
            byte[] byArray2 = new byte[(iconResource.length - 8) / 2];
            this.imageInput.skipBytes(byArray2.length);
            this.imageInput.readFully(byArray2);
            int n3 = 128;
            int n4 = byArray.length;
            for (int i = 0; i < n4; ++i) {
                byArray[i] = (byte)((byArray2[i / 8] & n3) != 0 ? 255 : 0);
                if ((n3 >>= 1) != 0) continue;
                n3 = 128;
            }
        } else {
            throw new IllegalArgumentException(String.format("Not a mask resource: %s", iconResource));
        }
        return Raster.createBandedRaster(new DataBufferByte(byArray, byArray.length), n, n2, n, new int[]{0}, new int[]{0}, null);
    }

    private IconResource findMaskResource(IconResource iconResource) throws IOException {
        try {
            IconResource iconResource2;
            int n = 0;
            while (!(iconResource2 = n < this.masks.size() ? this.masks.get(n++) : this.readNextIconResource()).isMaskType() || !iconResource2.size().equals(iconResource.size())) {
            }
            return iconResource2;
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            for (IconResource iconResource3 : this.icons) {
                if (!iconResource3.hasMask() || !iconResource3.size().equals(iconResource.size())) continue;
                return iconResource3;
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private BufferedImage readForeignFormat(int n, ImageReadParam imageReadParam, IconResource iconResource) throws IOException {
        try (SubImageInputStream subImageInputStream = new SubImageInputStream(this.imageInput, (long)iconResource.length);){
            BufferedImage bufferedImage;
            Object object;
            Object object2;
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(subImageInputStream);
            while (iterator.hasNext()) {
                object2 = iterator.next();
                ((ImageReader)object2).setInput(subImageInputStream);
                try {
                    BufferedImage bufferedImage2 = ((ImageReader)object2).read(0, imageReadParam);
                    return bufferedImage2;
                }
                catch (IOException iOException) {
                    if (subImageInputStream.getFlushedPosition() <= 0L) {
                        subImageInputStream.seek(0L);
                        continue;
                    }
                    subImageInputStream.close();
                    subImageInputStream = new SubImageInputStream(this.imageInput, (long)iconResource.length);
                }
                finally {
                    ((ImageReader)object2).dispose();
                }
            }
            object2 = this.getForeignFormat((ImageInputStream)subImageInputStream);
            if ("JPEG 2000".equals(object2) && SipsJP2Reader.isAvailable()) {
                object = new SipsJP2Reader();
                ((SipsJP2Reader)object).setInput((ImageInputStream)subImageInputStream);
                bufferedImage = ((SipsJP2Reader)object).read(0, imageReadParam);
                if (bufferedImage != null) {
                    BufferedImage bufferedImage3 = bufferedImage;
                    return bufferedImage3;
                }
            }
            this.processWarningOccurred(String.format("Cannot read %s format in type '%s' icon (no reader; installed: %s)", object2, ICNSUtil.intToStr(iconResource.type), Arrays.toString(IIOUtil.getNormalizedReaderFormatNames())));
            object = iconResource.size();
            bufferedImage = ICNSImageReader.getDestination((ImageReadParam)imageReadParam, this.getImageTypes(n), (int)((Dimension)object).width, (int)((Dimension)object).height);
            return bufferedImage;
        }
    }

    private String getForeignFormat(ImageInputStream imageInputStream) throws IOException {
        byte[] byArray = new byte[12];
        try {
            imageInputStream.readFully(byArray);
        }
        finally {
            imageInputStream.seek(0L);
        }
        String string = Arrays.equals(ICNS.PNG_MAGIC, byArray) ? "PNG" : (Arrays.equals(ICNS.JPEG_2000_MAGIC, byArray) ? "JPEG 2000" : "unknown");
        return string;
    }

    private IconResource readIconResource(int n) throws IOException {
        this.checkBounds(n);
        this.readeFileHeader();
        while (this.icons.size() <= n) {
            this.readNextIconResource();
        }
        return this.icons.get(n);
    }

    private IconResource readNextIconResource() throws IOException {
        long l = this.lastResourceRead == null ? 8L : this.lastResourceRead.start + (long)this.lastResourceRead.length;
        this.imageInput.seek(l);
        if (this.imageInput.getStreamPosition() >= (long)this.length) {
            throw new IndexOutOfBoundsException();
        }
        IconResource iconResource = IconResource.read(this.imageInput);
        if (iconResource.isTOC()) {
            int n = (iconResource.length - 8) / 8;
            long l2 = iconResource.start + (long)iconResource.length;
            for (int i = 0; i < n; ++i) {
                iconResource = IconResource.read(l2, this.imageInput);
                l2 += (long)iconResource.length;
                this.addResource(iconResource);
            }
        } else {
            this.addResource(iconResource);
        }
        this.lastResourceRead = iconResource;
        return iconResource;
    }

    private void addResource(IconResource iconResource) {
        if (iconResource.isMaskType()) {
            this.masks.add(iconResource);
        } else if (!iconResource.isUnknownType()) {
            this.icons.add(iconResource);
        }
    }

    private void readeFileHeader() throws IOException {
        this.assertInput();
        if (this.length <= 0) {
            this.imageInput.seek(0L);
            if (this.imageInput.readInt() != 1768124019) {
                throw new IIOException("Not an Apple Icon Image");
            }
            this.length = this.imageInput.readInt();
        }
    }

    public static void main(String[] stringArray) throws IOException {
        int n = 0;
        int n2 = -1;
        if (stringArray[n].charAt(0) == '-') {
            int n3 = ++n;
            ++n;
            n2 = Integer.parseInt(stringArray[n3]);
        }
        int n4 = 0;
        int n5 = 0;
        ICNSImageReader iCNSImageReader = new ICNSImageReader();
        while (n < stringArray.length) {
            File file;
            ImageInputStream imageInputStream;
            if ((imageInputStream = ImageIO.createImageInputStream(file = new File(stringArray[n++]))) == null) {
                System.err.printf("Cannot read: %s\n", file.getAbsolutePath());
                continue;
            }
            try {
                ((ImageReader)((Object)iCNSImageReader)).setInput(imageInputStream);
                int n6 = n2 != -1 ? n2 : 0;
                int n7 = n2 != -1 ? n2 + 1 : ((ImageReader)((Object)iCNSImageReader)).getNumImages(true);
                for (int i = n6; i < n7; ++i) {
                    try {
                        long l = System.currentTimeMillis();
                        BufferedImage bufferedImage = ((ImageReader)((Object)iCNSImageReader)).read(i);
                        ++n4;
                        System.err.println(System.currentTimeMillis() - l + "ms");
                        ICNSImageReader.showIt((BufferedImage)bufferedImage, (String)String.format("%s - %d", file.getName(), i));
                        continue;
                    }
                    catch (IOException iOException) {
                        ++n5;
                        if (iOException.getMessage().contains("JPEG 2000")) {
                            System.err.printf("%s: %s\n", file, iOException.getMessage());
                            continue;
                        }
                        System.err.printf("%s: ", file);
                        iOException.printStackTrace();
                    }
                }
            }
            catch (Exception exception) {
                System.err.printf("%s: ", file);
                exception.printStackTrace();
            }
        }
        System.err.printf("Read %s images (%d skipped) in %d files\n", n4, n5, stringArray.length);
    }

    private static final class ICNSBitMaskColorModel
    extends IndexColorModel {
        static final IndexColorModel INSTANCE = new ICNSBitMaskColorModel();

        private ICNSBitMaskColorModel() {
            super(1, 2, new int[]{0, -1}, 0, true, 0, 0);
        }
    }
}

