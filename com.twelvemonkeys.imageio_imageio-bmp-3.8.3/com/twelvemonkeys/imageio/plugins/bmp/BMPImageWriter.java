/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.DIBImageWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public final class BMPImageWriter
extends DIBImageWriter {
    protected BMPImageWriter(ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new BMPImageWriteParam(this.getLocale());
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return null;
    }

    public IIOMetadata convertImageMetadata(IIOMetadata iIOMetadata, ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return null;
    }

    public void write(IIOMetadata iIOMetadata, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        this.assertOutput();
        if (iIOImage == null) {
            throw new IllegalArgumentException("image may not be null");
        }
        if (iIOImage.hasRaster()) {
            throw new UnsupportedOperationException("image has a Raster!");
        }
        this.imageOutput.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        this.clearAbortRequest();
        this.processImageStarted(0);
        if (imageWriteParam == null) {
            imageWriteParam = this.getDefaultWriteParam();
        }
        boolean bl = imageWriteParam instanceof BMPImageWriteParam && ((BMPImageWriteParam)imageWriteParam).isTopDown();
        int n = 0;
        BufferedImage bufferedImage = (BufferedImage)iIOImage.getRenderedImage();
        int n2 = bufferedImage.getHeight();
        int n3 = bufferedImage.getWidth();
        int n4 = 40;
        boolean bl2 = n4 == 40 && (n == 3 || n == 6);
        this.writeFileHeader(n4, 14 + n4 + n3 * n2 * 4, bl2);
        this.writeDIBHeader(n4, bufferedImage.getWidth(), bufferedImage.getHeight(), bl, bufferedImage.getColorModel().getPixelSize(), n);
        if (bl2) {
            this.imageOutput.writeInt(255);
            this.imageOutput.writeInt(65280);
            this.imageOutput.writeInt(0xFF0000);
            this.imageOutput.writeInt(-16777216);
        }
        this.writeUncompressed(bl, bufferedImage, n2, n3);
        this.processImageComplete();
    }

    private void writeFileHeader(int n, int n2, boolean bl) throws IOException {
        this.imageOutput.writeShort(19778);
        this.imageOutput.writeInt(n2 + (bl ? 16 : 0));
        this.imageOutput.writeShort(0);
        this.imageOutput.writeShort(0);
        this.imageOutput.writeInt(14 + n + (bl ? 16 : 0));
    }

    public static void main(String[] stringArray) throws IOException {
        File file = new File(stringArray[0]);
        File file2 = new File(stringArray[0].replace('.', '_') + "_copy.bmp");
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(file2);){
            BMPImageWriter bMPImageWriter = new BMPImageWriter(null);
            ((DIBImageWriter)bMPImageWriter).setOutput(imageOutputStream);
            bMPImageWriter.write(ImageIO.read(file));
        }
    }
}

