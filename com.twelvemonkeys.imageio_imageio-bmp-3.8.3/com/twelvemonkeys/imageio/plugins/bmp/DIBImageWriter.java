/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.ImageWriterBase
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.ImageWriterBase;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.IIOException;
import javax.imageio.spi.ImageWriterSpi;

abstract class DIBImageWriter
extends ImageWriterBase {
    DIBImageWriter(ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
    }

    public void setOutput(Object object) {
        super.setOutput(object);
        if (this.imageOutput != null) {
            this.imageOutput.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        }
    }

    void writeDIBHeader(int n, int n2, int n3, boolean bl, int n4, int n5) throws IOException {
        switch (n) {
            case 40: {
                DIBHeader.BitmapInfoHeader bitmapInfoHeader = new DIBHeader.BitmapInfoHeader();
                bitmapInfoHeader.width = n2;
                bitmapInfoHeader.height = n3;
                bitmapInfoHeader.topDown = bl;
                bitmapInfoHeader.planes = 1;
                bitmapInfoHeader.bitCount = n4;
                bitmapInfoHeader.compression = n5;
                bitmapInfoHeader.colorsUsed = 0;
                bitmapInfoHeader.colorsImportant = 0;
                bitmapInfoHeader.imageSize = bitmapInfoHeader.height * ((bitmapInfoHeader.width * bitmapInfoHeader.bitCount + 31) / 32) * 4;
                bitmapInfoHeader.xPixelsPerMeter = 2835;
                bitmapInfoHeader.yPixelsPerMeter = 2835;
                bitmapInfoHeader.write(this.imageOutput);
                break;
            }
            default: {
                throw new IIOException("Unsupported header size: " + n);
            }
        }
    }

    void writeUncompressed(boolean bl, BufferedImage bufferedImage, int n, int n2) throws IOException {
        if (bufferedImage.getType() != 6) {
            throw new IIOException("Only TYPE_4BYTE_ABGR supported");
        }
        WritableRaster writableRaster = bufferedImage.getRaster();
        WritableRaster writableRaster2 = Raster.createInterleavedRaster(0, n2, 1, n2 * 4, 4, new int[]{2, 1, 0, 3}, null);
        byte[] byArray = ((DataBufferByte)writableRaster2.getDataBuffer()).getData();
        int[] nArray = new int[]{2, 1, 0, 3};
        for (int i = 0; i < n; ++i) {
            int n3 = bl ? i : n - 1 - i;
            writableRaster2.setDataElements(0, 0, writableRaster.createChild(0, n3, n2, 1, 0, 0, nArray));
            this.imageOutput.write(byArray);
            if (this.abortRequested()) {
                this.processWriteAborted();
                break;
            }
            this.processImageProgress(100.0f * (float)i / (float)n);
        }
    }
}

