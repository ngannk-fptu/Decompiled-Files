/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public final class LosslessFactory {
    static boolean usePredictorEncoder = true;

    private LosslessFactory() {
    }

    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image) throws IOException {
        PDImageXObject pdImageXObject;
        if (LosslessFactory.isGrayImage(image)) {
            return LosslessFactory.createFromGrayImage(image, document);
        }
        if (usePredictorEncoder && (pdImageXObject = new PredictorEncoder(document, image).encode()) != null) {
            if (pdImageXObject.getColorSpace() == PDDeviceRGB.INSTANCE && pdImageXObject.getBitsPerComponent() < 16 && image.getWidth() * image.getHeight() <= 2500) {
                PDImageXObject pdImageXObjectClassic = LosslessFactory.createFromRGBImage(image, document);
                if (pdImageXObjectClassic.getCOSObject().getLength() < pdImageXObject.getCOSObject().getLength()) {
                    pdImageXObject.getCOSObject().close();
                    return pdImageXObjectClassic;
                }
                pdImageXObjectClassic.getCOSObject().close();
            }
            return pdImageXObject;
        }
        return LosslessFactory.createFromRGBImage(image, document);
    }

    private static boolean isGrayImage(BufferedImage image) {
        if (image.getTransparency() != 1) {
            return false;
        }
        if (image.getType() == 10 && image.getColorModel().getPixelSize() <= 8) {
            return true;
        }
        return image.getType() == 12 && image.getColorModel().getPixelSize() == 1;
    }

    private static PDImageXObject createFromGrayImage(BufferedImage image, PDDocument document) throws IOException {
        int height = image.getHeight();
        int width = image.getWidth();
        int[] rgbLineBuffer = new int[width];
        int bpc = image.getColorModel().getPixelSize();
        ByteArrayOutputStream baos = new ByteArrayOutputStream((width * bpc / 8 + (width * bpc % 8 != 0 ? 1 : 0)) * height);
        MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(baos);
        for (int y = 0; y < height; ++y) {
            for (int pixel : image.getRGB(0, y, width, 1, rgbLineBuffer, 0, width)) {
                mcios.writeBits(pixel & 0xFF, bpc);
            }
            int bitOffset = mcios.getBitOffset();
            if (bitOffset == 0) continue;
            mcios.writeBits(0L, 8 - bitOffset);
        }
        mcios.flush();
        mcios.close();
        return LosslessFactory.prepareImageXObject(document, baos.toByteArray(), image.getWidth(), image.getHeight(), bpc, PDDeviceGray.INSTANCE);
    }

    private static PDImageXObject createFromRGBImage(BufferedImage image, PDDocument document) throws IOException {
        int apbc;
        int height = image.getHeight();
        int width = image.getWidth();
        int[] rgbLineBuffer = new int[width];
        int bpc = 8;
        PDDeviceRGB deviceColorSpace = PDDeviceRGB.INSTANCE;
        byte[] imageData = new byte[width * height * 3];
        int byteIdx = 0;
        int alphaByteIdx = 0;
        int alphaBitPos = 7;
        int transparency = image.getTransparency();
        int n = apbc = transparency == 2 ? 1 : 8;
        byte[] alphaImageData = transparency != 1 ? new byte[(width * apbc / 8 + (width * apbc % 8 != 0 ? 1 : 0)) * height] : new byte[]{};
        for (int y = 0; y < height; ++y) {
            for (int pixel : image.getRGB(0, y, width, 1, rgbLineBuffer, 0, width)) {
                imageData[byteIdx++] = (byte)(pixel >> 16 & 0xFF);
                imageData[byteIdx++] = (byte)(pixel >> 8 & 0xFF);
                imageData[byteIdx++] = (byte)(pixel & 0xFF);
                if (transparency == 2) {
                    int n2 = alphaByteIdx++;
                    alphaImageData[n2] = (byte)(alphaImageData[n2] | (pixel >> 24 & 1) << alphaBitPos);
                    if (--alphaBitPos >= 0) continue;
                    alphaBitPos = 7;
                    continue;
                }
                if (transparency == 1) continue;
                alphaImageData[alphaByteIdx++] = (byte)(pixel >> 24 & 0xFF);
            }
            if (transparency != 2 || alphaBitPos == 7) continue;
            alphaBitPos = 7;
            ++alphaByteIdx;
        }
        PDImageXObject pdImage = LosslessFactory.prepareImageXObject(document, imageData, image.getWidth(), image.getHeight(), bpc, deviceColorSpace);
        if (transparency != 1) {
            PDImageXObject pdMask = LosslessFactory.prepareImageXObject(document, alphaImageData, image.getWidth(), image.getHeight(), apbc, PDDeviceGray.INSTANCE);
            pdImage.getCOSObject().setItem(COSName.SMASK, (COSObjectable)pdMask);
        }
        return pdImage;
    }

    static PDImageXObject prepareImageXObject(PDDocument document, byte[] byteArray, int width, int height, int bitsPerComponent, PDColorSpace initColorSpace) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length / 2);
        Filter filter = FilterFactory.INSTANCE.getFilter(COSName.FLATE_DECODE);
        filter.encode(new ByteArrayInputStream(byteArray), baos, new COSDictionary(), 0);
        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(baos.toByteArray());
        return new PDImageXObject(document, encodedByteStream, COSName.FLATE_DECODE, width, height, bitsPerComponent, initColorSpace);
    }

    private static class PredictorEncoder {
        private final PDDocument document;
        private final BufferedImage image;
        private final int componentsPerPixel;
        private final int transferType;
        private final int bytesPerComponent;
        private final int bytesPerPixel;
        private final int height;
        private final int width;
        private final byte[] dataRawRowNone;
        private final byte[] dataRawRowSub;
        private final byte[] dataRawRowUp;
        private final byte[] dataRawRowAverage;
        private final byte[] dataRawRowPaeth;
        final int imageType;
        final boolean hasAlpha;
        final byte[] alphaImageData;
        final byte[] aValues;
        final byte[] cValues;
        final byte[] bValues;
        final byte[] xValues;
        final byte[] tmpResultValues;

        PredictorEncoder(PDDocument document, BufferedImage image) {
            this.document = document;
            this.image = image;
            this.componentsPerPixel = image.getColorModel().getNumComponents();
            this.transferType = image.getRaster().getTransferType();
            this.bytesPerComponent = this.transferType == 2 || this.transferType == 1 ? 2 : 1;
            this.bytesPerPixel = image.getColorModel().getNumColorComponents() * this.bytesPerComponent;
            this.height = image.getHeight();
            this.width = image.getWidth();
            this.imageType = image.getType();
            this.hasAlpha = image.getColorModel().getNumComponents() != image.getColorModel().getNumColorComponents();
            this.alphaImageData = this.hasAlpha ? new byte[this.width * this.height * this.bytesPerComponent] : null;
            int dataRowByteCount = this.width * this.bytesPerPixel + 1;
            this.dataRawRowNone = new byte[dataRowByteCount];
            this.dataRawRowSub = new byte[dataRowByteCount];
            this.dataRawRowUp = new byte[dataRowByteCount];
            this.dataRawRowAverage = new byte[dataRowByteCount];
            this.dataRawRowPaeth = new byte[dataRowByteCount];
            this.dataRawRowNone[0] = 0;
            this.dataRawRowSub[0] = 1;
            this.dataRawRowUp[0] = 2;
            this.dataRawRowAverage[0] = 3;
            this.dataRawRowPaeth[0] = 4;
            this.aValues = new byte[this.bytesPerPixel];
            this.cValues = new byte[this.bytesPerPixel];
            this.bValues = new byte[this.bytesPerPixel];
            this.xValues = new byte[this.bytesPerPixel];
            this.tmpResultValues = new byte[this.bytesPerPixel];
        }

        PDImageXObject encode() throws IOException {
            Object[] transferRow;
            Object[] prevRow;
            int elementsInRowPerPixel;
            WritableRaster imageRaster = this.image.getRaster();
            block0 : switch (this.imageType) {
                case 0: {
                    switch (imageRaster.getTransferType()) {
                        case 1: {
                            elementsInRowPerPixel = this.componentsPerPixel;
                            prevRow = new short[this.width * elementsInRowPerPixel];
                            transferRow = new short[this.width * elementsInRowPerPixel];
                            break block0;
                        }
                        case 0: {
                            elementsInRowPerPixel = this.componentsPerPixel;
                            prevRow = new byte[this.width * elementsInRowPerPixel];
                            transferRow = new byte[this.width * elementsInRowPerPixel];
                            break block0;
                        }
                    }
                    return null;
                }
                case 5: 
                case 6: {
                    elementsInRowPerPixel = this.componentsPerPixel;
                    prevRow = new byte[this.width * elementsInRowPerPixel];
                    transferRow = new byte[this.width * elementsInRowPerPixel];
                    break;
                }
                case 1: 
                case 2: 
                case 4: {
                    elementsInRowPerPixel = 1;
                    prevRow = new int[this.width * elementsInRowPerPixel];
                    transferRow = new int[this.width * elementsInRowPerPixel];
                    break;
                }
                default: {
                    return null;
                }
            }
            int elementsInTransferRow = this.width * elementsInRowPerPixel;
            ByteArrayOutputStream stream = new ByteArrayOutputStream(this.height * this.width * this.bytesPerPixel / 2);
            Deflater deflater = new Deflater(Filter.getCompressionLevel());
            DeflaterOutputStream zip = new DeflaterOutputStream((OutputStream)stream, deflater);
            int alphaPtr = 0;
            for (int rowNum = 0; rowNum < this.height; ++rowNum) {
                short[] transferRowShort;
                short[] prevRowShort;
                Object[] transferRowInt;
                Object[] prevRowInt;
                byte[] prevRowByte;
                byte[] transferRowByte;
                imageRaster.getDataElements(0, rowNum, this.width, 1, transferRow);
                int writerPtr = 1;
                Arrays.fill(this.aValues, (byte)0);
                Arrays.fill(this.cValues, (byte)0);
                if (transferRow instanceof byte[]) {
                    transferRowByte = (byte[])transferRow;
                    prevRowByte = (byte[])prevRow;
                    prevRowInt = null;
                    transferRowInt = null;
                    prevRowShort = null;
                    transferRowShort = null;
                } else if (transferRow instanceof int[]) {
                    transferRowInt = transferRow;
                    prevRowInt = prevRow;
                    prevRowShort = null;
                    transferRowShort = null;
                    prevRowByte = null;
                    transferRowByte = null;
                } else {
                    transferRowShort = (short[])transferRow;
                    prevRowShort = (short[])prevRow;
                    prevRowInt = null;
                    transferRowInt = null;
                    prevRowByte = null;
                    transferRowByte = null;
                }
                int indexInTransferRow = 0;
                while (indexInTransferRow < elementsInTransferRow) {
                    if (transferRowByte != null) {
                        this.copyImageBytes(transferRowByte, indexInTransferRow, this.xValues, this.alphaImageData, alphaPtr);
                        this.copyImageBytes(prevRowByte, indexInTransferRow, this.bValues, null, 0);
                    } else if (transferRowInt != null) {
                        this.copyIntToBytes((int[])transferRowInt, indexInTransferRow, this.xValues, this.alphaImageData, alphaPtr);
                        this.copyIntToBytes((int[])prevRowInt, indexInTransferRow, this.bValues, null, 0);
                    } else {
                        PredictorEncoder.copyShortsToBytes(transferRowShort, indexInTransferRow, this.xValues, this.alphaImageData, alphaPtr);
                        PredictorEncoder.copyShortsToBytes(prevRowShort, indexInTransferRow, this.bValues, null, 0);
                    }
                    int length = this.xValues.length;
                    for (int bytePtr = 0; bytePtr < length; ++bytePtr) {
                        int x = this.xValues[bytePtr] & 0xFF;
                        int a = this.aValues[bytePtr] & 0xFF;
                        int b = this.bValues[bytePtr] & 0xFF;
                        int c = this.cValues[bytePtr] & 0xFF;
                        this.dataRawRowNone[writerPtr] = (byte)x;
                        this.dataRawRowSub[writerPtr] = PredictorEncoder.pngFilterSub(x, a);
                        this.dataRawRowUp[writerPtr] = PredictorEncoder.pngFilterUp(x, b);
                        this.dataRawRowAverage[writerPtr] = PredictorEncoder.pngFilterAverage(x, a, b);
                        this.dataRawRowPaeth[writerPtr] = PredictorEncoder.pngFilterPaeth(x, a, b, c);
                        ++writerPtr;
                    }
                    System.arraycopy(this.xValues, 0, this.aValues, 0, this.bytesPerPixel);
                    System.arraycopy(this.bValues, 0, this.cValues, 0, this.bytesPerPixel);
                    indexInTransferRow += elementsInRowPerPixel;
                    alphaPtr += this.bytesPerComponent;
                }
                byte[] rowToWrite = this.chooseDataRowToWrite();
                zip.write(rowToWrite, 0, rowToWrite.length);
                Object[] temp = prevRow;
                prevRow = transferRow;
                transferRow = temp;
            }
            zip.close();
            deflater.end();
            return this.preparePredictorPDImage(stream, this.bytesPerComponent * 8);
        }

        private void copyIntToBytes(int[] transferRow, int indexInTranferRow, byte[] targetValues, byte[] alphaImageData, int alphaPtr) {
            int val = transferRow[indexInTranferRow];
            byte b0 = (byte)(val & 0xFF);
            byte b1 = (byte)(val >> 8 & 0xFF);
            byte b2 = (byte)(val >> 16 & 0xFF);
            switch (this.imageType) {
                case 4: {
                    targetValues[0] = b0;
                    targetValues[1] = b1;
                    targetValues[2] = b2;
                    break;
                }
                case 2: {
                    byte b3;
                    targetValues[0] = b2;
                    targetValues[1] = b1;
                    targetValues[2] = b0;
                    if (alphaImageData == null) break;
                    alphaImageData[alphaPtr] = b3 = (byte)(val >> 24 & 0xFF);
                    break;
                }
                case 1: {
                    targetValues[0] = b2;
                    targetValues[1] = b1;
                    targetValues[2] = b0;
                    break;
                }
            }
        }

        private void copyImageBytes(byte[] transferRow, int indexInTranferRow, byte[] targetValues, byte[] alphaImageData, int alphaPtr) {
            System.arraycopy(transferRow, indexInTranferRow, targetValues, 0, targetValues.length);
            if (alphaImageData != null) {
                alphaImageData[alphaPtr] = transferRow[indexInTranferRow + targetValues.length];
            }
        }

        private static void copyShortsToBytes(short[] transferRow, int indexInTranferRow, byte[] targetValues, byte[] alphaImageData, int alphaPtr) {
            int itr = indexInTranferRow;
            for (int i = 0; i < targetValues.length - 1; i += 2) {
                short val = transferRow[itr++];
                targetValues[i] = (byte)(val >> 8 & 0xFF);
                targetValues[i + 1] = (byte)(val & 0xFF);
            }
            if (alphaImageData != null) {
                short alpha = transferRow[itr];
                alphaImageData[alphaPtr] = (byte)(alpha >> 8 & 0xFF);
                alphaImageData[alphaPtr + 1] = (byte)(alpha & 0xFF);
            }
        }

        private PDImageXObject preparePredictorPDImage(ByteArrayOutputStream stream, int bitsPerComponent) throws IOException {
            ICC_Profile profile;
            PDColorSpace pdColorSpace;
            int h = this.image.getHeight();
            int w = this.image.getWidth();
            ColorSpace srcCspace = this.image.getColorModel().getColorSpace();
            int srcCspaceType = srcCspace.getType();
            PDDeviceColorSpace pDDeviceColorSpace = srcCspaceType == 9 ? PDDeviceCMYK.INSTANCE : (pdColorSpace = srcCspaceType == 6 ? PDDeviceGray.INSTANCE : PDDeviceRGB.INSTANCE);
            if (srcCspace instanceof ICC_ColorSpace && (profile = ((ICC_ColorSpace)srcCspace).getProfile()) != ICC_Profile.getInstance(1000)) {
                PDICCBased pdProfile = new PDICCBased(this.document);
                OutputStream outputStream = pdProfile.getPDStream().createOutputStream(COSName.FLATE_DECODE);
                outputStream.write(profile.getData());
                outputStream.close();
                pdProfile.getPDStream().getCOSObject().setInt(COSName.N, srcCspace.getNumComponents());
                pdProfile.getPDStream().getCOSObject().setItem(COSName.ALTERNATE, (COSBase)(srcCspaceType == 6 ? COSName.DEVICEGRAY : (srcCspaceType == 9 ? COSName.DEVICECMYK : COSName.DEVICERGB)));
                pdColorSpace = pdProfile;
            }
            PDImageXObject imageXObject = new PDImageXObject(this.document, new ByteArrayInputStream(stream.toByteArray()), COSName.FLATE_DECODE, w, h, bitsPerComponent, pdColorSpace);
            COSDictionary decodeParms = new COSDictionary();
            decodeParms.setItem(COSName.BITS_PER_COMPONENT, (COSBase)COSInteger.get(bitsPerComponent));
            decodeParms.setItem(COSName.PREDICTOR, (COSBase)COSInteger.get(15L));
            decodeParms.setItem(COSName.COLUMNS, (COSBase)COSInteger.get(w));
            decodeParms.setItem(COSName.COLORS, (COSBase)COSInteger.get(srcCspace.getNumComponents()));
            imageXObject.getCOSObject().setItem(COSName.DECODE_PARMS, (COSBase)decodeParms);
            if (this.image.getTransparency() != 1) {
                PDImageXObject pdMask = LosslessFactory.prepareImageXObject(this.document, this.alphaImageData, this.image.getWidth(), this.image.getHeight(), 8 * this.bytesPerComponent, PDDeviceGray.INSTANCE);
                imageXObject.getCOSObject().setItem(COSName.SMASK, (COSObjectable)pdMask);
            }
            return imageXObject;
        }

        private byte[] chooseDataRowToWrite() {
            byte[] rowToWrite = this.dataRawRowNone;
            long estCompressSum = PredictorEncoder.estCompressSum(this.dataRawRowNone);
            long estCompressSumSub = PredictorEncoder.estCompressSum(this.dataRawRowSub);
            long estCompressSumUp = PredictorEncoder.estCompressSum(this.dataRawRowUp);
            long estCompressSumAvg = PredictorEncoder.estCompressSum(this.dataRawRowAverage);
            long estCompressSumPaeth = PredictorEncoder.estCompressSum(this.dataRawRowPaeth);
            if (estCompressSum > estCompressSumSub) {
                rowToWrite = this.dataRawRowSub;
                estCompressSum = estCompressSumSub;
            }
            if (estCompressSum > estCompressSumUp) {
                rowToWrite = this.dataRawRowUp;
                estCompressSum = estCompressSumUp;
            }
            if (estCompressSum > estCompressSumAvg) {
                rowToWrite = this.dataRawRowAverage;
                estCompressSum = estCompressSumAvg;
            }
            if (estCompressSum > estCompressSumPaeth) {
                rowToWrite = this.dataRawRowPaeth;
            }
            return rowToWrite;
        }

        private static byte pngFilterSub(int x, int a) {
            return (byte)((x & 0xFF) - (a & 0xFF));
        }

        private static byte pngFilterUp(int x, int b) {
            return PredictorEncoder.pngFilterSub(x, b);
        }

        private static byte pngFilterAverage(int x, int a, int b) {
            return (byte)(x - (b + a) / 2);
        }

        private static byte pngFilterPaeth(int x, int a, int b, int c) {
            int p = a + b - c;
            int pa = Math.abs(p - a);
            int pb = Math.abs(p - b);
            int pc = Math.abs(p - c);
            int pr = pa <= pb && pa <= pc ? a : (pb <= pc ? b : c);
            int r = x - pr;
            return (byte)r;
        }

        private static long estCompressSum(byte[] dataRawRowSub) {
            long sum = 0L;
            for (byte aDataRawRowSub : dataRawRowSub) {
                sum += (long)Math.abs(aDataRawRowSub);
            }
            return sum;
        }
    }
}

