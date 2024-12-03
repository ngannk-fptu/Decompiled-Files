/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.image.ImageUtil
 *  com.twelvemonkeys.imageio.ImageWriterBase
 *  com.twelvemonkeys.imageio.color.ColorProfiles
 *  com.twelvemonkeys.imageio.metadata.Directory
 *  com.twelvemonkeys.imageio.metadata.Entry
 *  com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry
 *  com.twelvemonkeys.imageio.metadata.tiff.TIFFWriter
 *  com.twelvemonkeys.imageio.stream.SubImageOutputStream
 *  com.twelvemonkeys.imageio.util.IIOUtil
 *  com.twelvemonkeys.imageio.util.ProgressListenerBase
 *  com.twelvemonkeys.io.enc.Encoder
 *  com.twelvemonkeys.io.enc.EncoderStream
 *  com.twelvemonkeys.io.enc.PackBitsEncoder
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.imageio.ImageWriterBase;
import com.twelvemonkeys.imageio.color.ColorProfiles;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFWriter;
import com.twelvemonkeys.imageio.plugins.tiff.CCITTFaxEncoderStream;
import com.twelvemonkeys.imageio.plugins.tiff.HorizontalDifferencingStream;
import com.twelvemonkeys.imageio.plugins.tiff.LZWEncoder;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageMetadata;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReader;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriteParam;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriterSpi;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFStreamMetadata;
import com.twelvemonkeys.imageio.stream.SubImageOutputStream;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.imageio.util.ProgressListenerBase;
import com.twelvemonkeys.io.enc.Encoder;
import com.twelvemonkeys.io.enc.EncoderStream;
import com.twelvemonkeys.io.enc.PackBitsEncoder;
import com.twelvemonkeys.lang.Validate;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.IIOParam;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public final class TIFFImageWriter
extends ImageWriterBase {
    private boolean writingSequence = false;
    private int sequenceIndex = 0;
    private TIFFWriter sequenceTIFFWriter = null;
    private long sequenceLastIFDPos = -1L;

    TIFFImageWriter(ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
    }

    public void setOutput(Object object) {
        super.setOutput(object);
    }

    public void write(IIOMetadata iIOMetadata, IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        this.prepareWriteSequence(iIOMetadata);
        this.writeToSequence(iIOImage, imageWriteParam);
        this.endWriteSequence();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long writePage(int n, IIOImage iIOImage, ImageWriteParam imageWriteParam, TIFFWriter tIFFWriter, long l) throws IOException {
        long l2;
        long l3;
        int[] nArray;
        int[] nArray2;
        RenderedImage renderedImage = iIOImage.getRenderedImage();
        SampleModel sampleModel = renderedImage.getSampleModel();
        ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(renderedImage);
        TIFFImageMetadata tIFFImageMetadata = iIOImage.getMetadata() != null ? this.convertImageMetadata(iIOImage.getMetadata(), imageTypeSpecifier, imageWriteParam) : this.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        int n2 = sampleModel.getNumBands();
        int n3 = this.computePixelSize(sampleModel);
        if (sampleModel instanceof ComponentSampleModel) {
            nArray2 = ((ComponentSampleModel)sampleModel).getBandOffsets();
            nArray = null;
        } else if (sampleModel instanceof SinglePixelPackedSampleModel) {
            nArray = ((SinglePixelPackedSampleModel)sampleModel).getBitOffsets();
            nArray2 = null;
        } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
            nArray = null;
            nArray2 = new int[]{0};
        } else {
            throw new IllegalArgumentException("Unknown bit/bandOffsets for sample model: " + sampleModel);
        }
        short s = tIFFWriter.offsetSize() == 4 ? (short)4 : 16;
        LinkedHashMap<Integer, Entry> linkedHashMap = new LinkedHashMap<Integer, Entry>();
        Directory directory = tIFFImageMetadata.getIFD();
        for (Entry entry : directory) {
            linkedHashMap.put((Integer)entry.getIdentifier(), entry);
        }
        linkedHashMap.put(256, (Entry)new TIFFEntry(256, (Object)renderedImage.getWidth()));
        linkedHashMap.put(257, (Entry)new TIFFEntry(257, (Object)renderedImage.getHeight()));
        linkedHashMap.put(278, (Entry)new TIFFEntry(278, (Object)renderedImage.getHeight()));
        linkedHashMap.put(279, (Entry)new TIFFEntry(279, s, (Object)-1));
        linkedHashMap.put(273, (Entry)new TIFFEntry(273, s, (Object)-1));
        long l4 = -1L;
        int n4 = ((Number)((Entry)linkedHashMap.get(259)).getValue()).intValue();
        if (n4 == 1) {
            l3 = this.imageOutput.getStreamPosition();
            l2 = tIFFWriter.computeIFDSize(linkedHashMap.values());
            long l5 = l3 + (long)tIFFWriter.offsetSize() + l2 + (long)tIFFWriter.offsetSize();
            long l6 = (long)renderedImage.getHeight() * (((long)renderedImage.getWidth() * (long)n3 + 7L) / 8L);
            linkedHashMap.put(273, (Entry)new TIFFEntry(273, s, (Object)l5));
            linkedHashMap.put(279, (Entry)new TIFFEntry(279, s, (Object)l6));
            long l7 = tIFFWriter.writeIFD(linkedHashMap.values(), this.imageOutput);
            l4 = this.imageOutput.getStreamPosition();
            if (l3 > l) {
                this.imageOutput.seek(l);
                tIFFWriter.writeOffset(this.imageOutput, l7);
                this.imageOutput.seek(l4);
            }
            tIFFWriter.writeOffset(this.imageOutput, 0L);
        } else {
            tIFFWriter.writeOffset(this.imageOutput, 0L);
        }
        l3 = this.imageOutput.getStreamPosition();
        if (n4 == 7) {
            Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("JPEG");
            if (!iterator.hasNext()) {
                throw new IIOException("No JPEG ImageWriter found!");
            }
            ImageWriter imageWriter = iterator.next();
            try {
                imageWriter.setOutput(new SubImageOutputStream(this.imageOutput));
                ListenerDelegate listenerDelegate = new ListenerDelegate(n);
                imageWriter.addIIOWriteProgressListener((IIOWriteProgressListener)((Object)listenerDelegate));
                imageWriter.addIIOWriteWarningListener(listenerDelegate);
                imageWriter.write(null, iIOImage, this.copyParams(imageWriteParam, imageWriter));
            }
            finally {
                imageWriter.dispose();
            }
        } else {
            this.writeImageData(this.createCompressorStream(renderedImage, imageWriteParam, linkedHashMap), n, renderedImage, n2, nArray2, nArray);
        }
        l2 = this.imageOutput.getStreamPosition() - l3;
        if (n4 != 1) {
            linkedHashMap.put(273, (Entry)new TIFFEntry(273, s, (Object)l3));
            linkedHashMap.put(279, (Entry)new TIFFEntry(279, s, (Object)l2));
            long l8 = tIFFWriter.writeIFD(linkedHashMap.values(), this.imageOutput);
            l4 = this.imageOutput.getStreamPosition();
            this.imageOutput.seek(l);
            tIFFWriter.writeOffset(this.imageOutput, l8);
            this.imageOutput.seek(l4);
            tIFFWriter.writeOffset(this.imageOutput, 0L);
        }
        return l4;
    }

    private ImageWriteParam copyParams(ImageWriteParam imageWriteParam, ImageWriter imageWriter) {
        if (imageWriteParam == null) {
            return null;
        }
        ImageWriteParam imageWriteParam2 = imageWriter.getDefaultWriteParam();
        imageWriteParam2.setSourceSubsampling(imageWriteParam.getSourceXSubsampling(), imageWriteParam.getSourceYSubsampling(), imageWriteParam.getSubsamplingXOffset(), imageWriteParam.getSubsamplingYOffset());
        imageWriteParam2.setSourceRegion(imageWriteParam.getSourceRegion());
        imageWriteParam2.setSourceBands(imageWriteParam.getSourceBands());
        imageWriteParam2.setCompressionMode(imageWriteParam.getCompressionMode());
        if (imageWriteParam.getCompressionMode() == 2) {
            imageWriteParam2.setCompressionQuality(imageWriteParam.getCompressionQuality());
        }
        return imageWriteParam2;
    }

    private int computePixelSize(SampleModel sampleModel) {
        int n = 0;
        for (int i = 0; i < sampleModel.getNumBands(); ++i) {
            n += sampleModel.getSampleSize(i);
        }
        return n;
    }

    private DataOutput createCompressorStream(RenderedImage renderedImage, ImageWriteParam imageWriteParam, Map<Integer, Entry> map) {
        int n = (Integer)map.get(277).getValue();
        short s = ((short[])map.get(258).getValue())[0];
        int n2 = ((Number)map.get(259).getValue()).intValue();
        switch (n2) {
            case 1: {
                return this.imageOutput;
            }
            case 32773: {
                OutputStream outputStream = IIOUtil.createStreamAdapter((ImageOutputStream)this.imageOutput);
                outputStream = new EncoderStream(outputStream, (Encoder)new PackBitsEncoder(), true);
                return new DataOutputStream(outputStream);
            }
            case 8: 
            case 32946: {
                int n3 = 1;
                if (imageWriteParam.getCompressionMode() == 2) {
                    n3 = 9 - Math.round(8.0f * imageWriteParam.getCompressionQuality());
                }
                OutputStream outputStream = IIOUtil.createStreamAdapter((ImageOutputStream)this.imageOutput);
                outputStream = new DeflaterOutputStream(outputStream, new Deflater(n3), 1024);
                if (map.containsKey(317) && map.get(317).getValue().equals(2)) {
                    outputStream = new HorizontalDifferencingStream(outputStream, renderedImage.getTileWidth(), n, s, this.imageOutput.getByteOrder());
                }
                return new DataOutputStream(outputStream);
            }
            case 5: {
                OutputStream outputStream = IIOUtil.createStreamAdapter((ImageOutputStream)this.imageOutput);
                outputStream = new EncoderStream(outputStream, (Encoder)new LZWEncoder(((long)renderedImage.getTileWidth() * (long)n * (long)s + 7L) / 8L * (long)renderedImage.getTileHeight()));
                if (map.containsKey(317) && map.get(317).getValue().equals(2)) {
                    outputStream = new HorizontalDifferencingStream(outputStream, renderedImage.getTileWidth(), n, s, this.imageOutput.getByteOrder());
                }
                return new DataOutputStream(outputStream);
            }
            case 2: 
            case 3: 
            case 4: {
                Entry entry;
                if (renderedImage.getSampleModel().getNumBands() != 1 || renderedImage.getSampleModel().getSampleSize(0) != 1) {
                    throw new IllegalArgumentException("CCITT compressions supports 1 sample/pixel, 1 bit/sample only");
                }
                long l = 0L;
                if (n2 != 2) {
                    entry = map.get(n2 == 3 ? 292 : 293);
                    l = ((Number)entry.getValue()).longValue();
                }
                int n4 = (Integer)((entry = map.get(266)) != null ? entry.getValue() : Integer.valueOf(1));
                OutputStream outputStream = IIOUtil.createStreamAdapter((ImageOutputStream)this.imageOutput);
                outputStream = new CCITTFaxEncoderStream(outputStream, renderedImage.getTileWidth(), renderedImage.getTileHeight(), n2, n4, l);
                return new DataOutputStream(outputStream);
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported TIFF compression: %d", n2));
    }

    private int getPhotometricInterpretation(ColorModel colorModel, int n) {
        if (colorModel.getPixelSize() == 1) {
            if (colorModel instanceof IndexColorModel) {
                if (colorModel.getRGB(0) == -1 && colorModel.getRGB(1) == -16777216) {
                    return 0;
                }
                if (colorModel.getRGB(0) != -16777216 || colorModel.getRGB(1) != -1) {
                    return 3;
                }
            }
            return 1;
        }
        if (colorModel instanceof IndexColorModel) {
            return 3;
        }
        switch (colorModel.getColorSpace().getType()) {
            case 6: {
                return 1;
            }
            case 5: {
                return n == 7 ? 6 : 2;
            }
            case 9: {
                return 5;
            }
        }
        throw new IllegalArgumentException("Can't determine PhotometricInterpretation for color model: " + colorModel);
    }

    private short[] createColorMap(IndexColorModel indexColorModel, int n) {
        short[] sArray = new short[(int)(3.0 * Math.pow(2.0, n))];
        for (int i = 0; i < indexColorModel.getMapSize(); ++i) {
            int n2 = indexColorModel.getRGB(i);
            sArray[i] = (short)this.upScale(n2 >> 16 & 0xFF);
            sArray[i + sArray.length / 3] = (short)this.upScale(n2 >> 8 & 0xFF);
            sArray[i + 2 * sArray.length / 3] = (short)this.upScale(n2 & 0xFF);
        }
        return sArray;
    }

    private int upScale(int n) {
        return 257 * n;
    }

    private short[] asShortArray(int[] nArray) {
        short[] sArray = new short[nArray.length];
        for (int i = 0; i < sArray.length; ++i) {
            sArray[i] = (short)nArray[i];
        }
        return sArray;
    }

    private void writeImageData(DataOutput dataOutput, int n, RenderedImage renderedImage, int n2, int[] nArray, int[] nArray2) throws IOException {
        this.processImageStarted(n);
        int n3 = renderedImage.getMinTileY();
        int n4 = n3 + renderedImage.getNumYTiles();
        int n5 = renderedImage.getMinTileX();
        int n6 = n5 + renderedImage.getNumXTiles();
        int n7 = renderedImage.getTileHeight();
        int n8 = renderedImage.getTileWidth();
        int n9 = renderedImage.getSampleModel().getSampleSize(0);
        int n10 = renderedImage.getSampleModel().getNumBands();
        for (int i = n3; i < n4; ++i) {
            block6: for (int j = n5; j < n6; ++j) {
                Raster raster = renderedImage.getTile(j, i);
                int n11 = raster.getMinX() - raster.getSampleModelTranslateX();
                int n12 = raster.getMinY() - raster.getSampleModelTranslateY();
                int n13 = (raster.getSampleModel().getWidth() * n9 + 7) / 8;
                DataBuffer dataBuffer = raster.getDataBuffer();
                switch (dataBuffer.getDataType()) {
                    case 0: {
                        int n14;
                        int n15;
                        int n16;
                        int n17;
                        int n18;
                        int n19;
                        int n20 = (n8 * n9 + 7) / 8;
                        int n21 = n11 % 8;
                        WritableRaster writableRaster = n21 != 0 ? raster.createCompatibleWritableRaster(raster.getWidth(), 1) : null;
                        DataBuffer dataBuffer2 = n21 != 0 ? writableRaster.getDataBuffer() : null;
                        for (n19 = 0; n19 < dataBuffer.getNumBanks(); ++n19) {
                            for (n18 = n12; n18 < n7 + n12; ++n18) {
                                n17 = n18 * n13 * n10;
                                if (n21 != 0) {
                                    writableRaster.setDataElements(0, 0, raster.createChild(0, n18 - n12, raster.getWidth(), 1, 0, 0, null));
                                }
                                for (n16 = n11; n16 < n20 + n11; ++n16) {
                                    n15 = n17 + n16 * n10;
                                    for (n14 = 0; n14 < n10; ++n14) {
                                        if (n9 == 8 || n21 == 0) {
                                            dataOutput.writeByte((byte)(dataBuffer.getElem(n19, n15 + nArray[n14]) & 0xFF));
                                            continue;
                                        }
                                        dataOutput.writeByte((byte)(dataBuffer2.getElem(n19, n16 - n11 + nArray[n14]) & 0xFF));
                                    }
                                }
                                this.flushStream(dataOutput);
                            }
                        }
                        continue block6;
                    }
                    case 1: 
                    case 2: {
                        int n14;
                        int n15;
                        int n16;
                        int n17;
                        int n18;
                        int n19 = n13 / 2;
                        if (n2 == 1) {
                            for (n18 = 0; n18 < dataBuffer.getNumBanks(); ++n18) {
                                for (n17 = n12; n17 < n7 + n12; ++n17) {
                                    n16 = n17 * n19;
                                    for (n15 = n11; n15 < n8 + n11; ++n15) {
                                        n14 = n16 + n15;
                                        dataOutput.writeShort((short)(dataBuffer.getElem(n18, n14) & 0xFFFF));
                                    }
                                    this.flushStream(dataOutput);
                                }
                            }
                            continue block6;
                        }
                        throw new IllegalArgumentException("Not implemented for data type: " + dataBuffer.getDataType());
                    }
                    case 3: {
                        int n22;
                        int n14;
                        int n15;
                        int n16;
                        int n17;
                        int n18 = n13 / 4;
                        if (1 == n2) {
                            for (n17 = 0; n17 < dataBuffer.getNumBanks(); ++n17) {
                                for (n16 = n12; n16 < n7 + n12; ++n16) {
                                    n15 = n16 * n18;
                                    for (n14 = n11; n14 < n8 + n11; ++n14) {
                                        n22 = n15 + n14;
                                        dataOutput.writeInt(dataBuffer.getElem(n17, n22));
                                    }
                                    this.flushStream(dataOutput);
                                }
                            }
                            continue block6;
                        }
                        for (n17 = 0; n17 < dataBuffer.getNumBanks(); ++n17) {
                            for (n16 = 0; n16 < n7; ++n16) {
                                n15 = n16 * n8;
                                for (n14 = 0; n14 < n8; ++n14) {
                                    n22 = n15 + n14;
                                    int n23 = dataBuffer.getElem(n17, n22);
                                    for (int k = 0; k < n10; ++k) {
                                        dataOutput.writeByte((byte)(n23 >> nArray2[k] & 0xFF));
                                    }
                                }
                                this.flushStream(dataOutput);
                            }
                        }
                        continue block6;
                    }
                    default: {
                        throw new IllegalArgumentException("Not implemented for data type: " + dataBuffer.getDataType());
                    }
                }
            }
            this.flushStream(dataOutput);
            this.processImageProgress(100.0f * (float)(i + 1) / (float)n4);
        }
        if (dataOutput instanceof DataOutputStream) {
            DataOutputStream dataOutputStream = (DataOutputStream)dataOutput;
            dataOutputStream.close();
        }
        this.processImageComplete();
    }

    private void flushStream(DataOutput dataOutput) throws IOException {
        if (dataOutput instanceof DataOutputStream) {
            DataOutputStream dataOutputStream = (DataOutputStream)dataOutput;
            dataOutputStream.flush();
        }
    }

    public TIFFImageMetadata getDefaultImageMetadata(ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        return this.initMeta(null, imageTypeSpecifier, imageWriteParam);
    }

    public TIFFImageMetadata convertImageMetadata(IIOMetadata iIOMetadata, ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        Directory directory;
        Validate.notNull((Object)iIOMetadata, (String)"inData");
        Validate.notNull((Object)imageTypeSpecifier, (String)"imageType");
        if (iIOMetadata instanceof TIFFImageMetadata) {
            directory = ((TIFFImageMetadata)((Object)iIOMetadata)).getIFD();
        } else {
            TIFFImageMetadata tIFFImageMetadata;
            block6: {
                tIFFImageMetadata = new TIFFImageMetadata(Collections.emptySet());
                try {
                    if (Arrays.asList(iIOMetadata.getMetadataFormatNames()).contains("com_sun_media_imageio_plugins_tiff_image_1.0")) {
                        tIFFImageMetadata.setFromTree("com_sun_media_imageio_plugins_tiff_image_1.0", iIOMetadata.getAsTree("com_sun_media_imageio_plugins_tiff_image_1.0"));
                        break block6;
                    }
                    if (iIOMetadata.isStandardMetadataFormatSupported()) {
                        tIFFImageMetadata.setFromTree("javax_imageio_1.0", iIOMetadata.getAsTree("javax_imageio_1.0"));
                        break block6;
                    }
                    return null;
                }
                catch (IIOInvalidTreeException iIOInvalidTreeException) {
                    this.processWarningOccurred(this.sequenceIndex, "Could not convert image meta data: " + iIOInvalidTreeException.getMessage());
                }
            }
            directory = tIFFImageMetadata.getIFD();
        }
        return this.initMeta(directory, imageTypeSpecifier, imageWriteParam);
    }

    private TIFFImageMetadata initMeta(Directory directory, ImageTypeSpecifier imageTypeSpecifier, ImageWriteParam imageWriteParam) {
        Object object;
        Validate.notNull((Object)imageTypeSpecifier, (String)"imageType");
        LinkedHashMap<Integer, Entry> linkedHashMap = new LinkedHashMap<Integer, Entry>(directory != null ? directory.size() + 10 : 20);
        linkedHashMap.put(305, (Entry)new TIFFEntry(305, (Object)("TwelveMonkeys ImageIO TIFF writer " + this.originatingProvider.getVersion())));
        linkedHashMap.put(274, (Entry)new TIFFEntry(274, (Object)1));
        this.mergeSafeMetadata(directory, linkedHashMap);
        ColorModel colorModel = imageTypeSpecifier.getColorModel();
        SampleModel sampleModel = imageTypeSpecifier.getSampleModel();
        int n = sampleModel.getNumBands();
        int n2 = this.computePixelSize(sampleModel);
        linkedHashMap.put(258, (Entry)new TIFFEntry(258, (Object)this.asShortArray(sampleModel.getSampleSize())));
        int n3 = (imageWriteParam == null || imageWriteParam.getCompressionMode() == 3) && directory != null && directory.getEntryById((Object)259) != null ? ((Number)directory.getEntryById((Object)259).getValue()).intValue() : TIFFImageWriteParam.getCompressionType(imageWriteParam);
        linkedHashMap.put(259, (Entry)new TIFFEntry(259, (Object)n3));
        int n4 = this.getPhotometricInterpretation(colorModel, n3);
        linkedHashMap.put(262, (Entry)new TIFFEntry(262, 3, (Object)n4));
        if (n > colorModel.getNumColorComponents()) {
            if (colorModel.hasAlpha()) {
                linkedHashMap.put(338, (Entry)new TIFFEntry(338, (Object)(colorModel.isAlphaPremultiplied() ? 1 : 2)));
            } else {
                linkedHashMap.put(338, (Entry)new TIFFEntry(338, (Object)0));
            }
        }
        switch (n3) {
            case 5: 
            case 8: 
            case 32946: {
                if (n2 < 8) break;
                linkedHashMap.put(317, (Entry)new TIFFEntry(317, (Object)2));
                break;
            }
            case 3: {
                Object object2 = object = directory != null ? directory.getEntryById((Object)292) : null;
                if (object == null) {
                    object = new TIFFEntry(292, (Object)1L);
                }
                linkedHashMap.put(292, (Entry)object);
                break;
            }
            case 4: {
                Entry entry;
                Entry entry2 = entry = directory != null ? directory.getEntryById((Object)293) : null;
                if (entry == null) {
                    entry = new TIFFEntry(293, (Object)0L);
                }
                linkedHashMap.put(293, entry);
                break;
            }
        }
        if (n4 == 3 && colorModel instanceof IndexColorModel) {
            linkedHashMap.put(320, (Entry)new TIFFEntry(320, (Object)this.createColorMap((IndexColorModel)colorModel, sampleModel.getSampleSize(0))));
            linkedHashMap.put(277, (Entry)new TIFFEntry(277, (Object)1));
        } else {
            linkedHashMap.put(277, (Entry)new TIFFEntry(277, (Object)n));
            object = colorModel.getColorSpace();
            if (object instanceof ICC_ColorSpace && !((ColorSpace)object).isCS_sRGB() && !ColorProfiles.isCS_GRAY((ICC_Profile)((ICC_ColorSpace)object).getProfile())) {
                linkedHashMap.put(34675, (Entry)new TIFFEntry(34675, (Object)((ICC_ColorSpace)object).getProfile().getData()));
            }
        }
        if (sampleModel.getDataType() == 2) {
            linkedHashMap.put(339, (Entry)new TIFFEntry(339, (Object)2));
        }
        return new TIFFImageMetadata(linkedHashMap.values());
    }

    private void mergeSafeMetadata(Directory directory, Map<Integer, Entry> map) {
        if (directory == null) {
            return;
        }
        block3: for (Entry entry : directory) {
            int n = (Integer)entry.getIdentifier();
            switch (n) {
                case 254: 
                case 255: 
                case 269: 
                case 270: 
                case 271: 
                case 272: 
                case 274: 
                case 282: 
                case 283: 
                case 285: 
                case 286: 
                case 287: 
                case 296: 
                case 297: 
                case 305: 
                case 306: 
                case 315: 
                case 316: 
                case 700: 
                case 33432: 
                case 33723: 
                case 34377: 
                case 34665: 
                case 34853: 
                case 37724: 
                case 40965: 
                case 50255: {
                    map.put(n, entry);
                    continue block3;
                }
            }
            if (n >= 1000 && n < 50706) {
                map.put(n, entry);
                continue;
            }
            if (n > 50780 && n < 65000) {
                map.put(n, entry);
                continue;
            }
            if (n < 65000 || n > 65535) continue;
            map.put(n, entry);
        }
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam imageWriteParam) {
        return super.getDefaultStreamMetadata(imageWriteParam);
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata iIOMetadata, ImageWriteParam imageWriteParam) {
        return super.convertStreamMetadata(iIOMetadata, imageWriteParam);
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new TIFFImageWriteParam();
    }

    public boolean canWriteSequence() {
        return true;
    }

    public void prepareWriteSequence(IIOMetadata iIOMetadata) throws IOException {
        if (this.writingSequence) {
            throw new IllegalStateException("sequence writing has already been started!");
        }
        this.assertOutput();
        TIFFStreamMetadata.configureStreamByteOrder(iIOMetadata, this.imageOutput);
        this.writingSequence = true;
        this.sequenceTIFFWriter = new TIFFWriter(this.isBigTIFF() ? 8 : 4);
        this.sequenceTIFFWriter.writeTIFFHeader(this.imageOutput);
        this.sequenceLastIFDPos = this.imageOutput.getStreamPosition();
    }

    private boolean isBigTIFF() throws IOException {
        return "bigtiff".equalsIgnoreCase(this.getFormatName());
    }

    public void writeToSequence(IIOImage iIOImage, ImageWriteParam imageWriteParam) throws IOException {
        if (!this.writingSequence) {
            throw new IllegalStateException("prepareWriteSequence() must be called before writeToSequence()!");
        }
        if (this.sequenceIndex > 0) {
            this.imageOutput.flushBefore(this.sequenceLastIFDPos);
            this.imageOutput.seek(this.imageOutput.length());
        }
        this.sequenceLastIFDPos = this.writePage(this.sequenceIndex++, iIOImage, imageWriteParam, this.sequenceTIFFWriter, this.sequenceLastIFDPos);
    }

    public void endWriteSequence() throws IOException {
        if (!this.writingSequence) {
            throw new IllegalStateException("prepareWriteSequence() must be called before endWriteSequence()!");
        }
        this.writingSequence = false;
        this.sequenceIndex = 0;
        this.sequenceTIFFWriter = null;
        this.sequenceLastIFDPos = -1L;
        this.imageOutput.flush();
    }

    protected void resetMembers() {
        super.resetMembers();
        this.writingSequence = false;
        this.sequenceIndex = 0;
        this.sequenceTIFFWriter = null;
        this.sequenceLastIFDPos = -1L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] stringArray) throws IOException {
        BufferedImage bufferedImage;
        Object object;
        Object object2;
        Object object3;
        Object object4;
        int n;
        int n2 = 0;
        int n3 = stringArray.length > n2 + 1 ? Integer.parseInt(stringArray[n2++]) : -1;
        int n4 = n = stringArray.length > n2 + 1 ? Integer.parseInt(stringArray[n2++]) : 0;
        if (stringArray.length <= n2) {
            System.err.println("No file specified");
            System.exit(1);
        }
        File file = new File(stringArray[n2++]);
        Object object5 = ImageIO.createImageInputStream(file);
        Object object6 = null;
        try {
            object4 = ImageIO.getImageReaders(object5);
            if (!object4.hasNext()) {
                System.err.println("No reader for: " + file);
                System.exit(1);
            }
            object3 = object4.next();
            ((ImageReader)object3).setInput(object5);
            object2 = ((ImageReader)object3).getDefaultReadParam();
            ((ImageReadParam)object2).setDestinationType(((ImageReader)object3).getRawImageType(0));
            if (((IIOParam)object2).getDestinationType() == null) {
                object = ((ImageReader)object3).getImageTypes(0);
                while (object.hasNext()) {
                    ImageTypeSpecifier imageTypeSpecifier = object.next();
                    if (imageTypeSpecifier.getColorModel().getColorSpace().getType() != 9) continue;
                    ((ImageReadParam)object2).setDestinationType(imageTypeSpecifier);
                }
            }
            System.err.println("param.getDestinationType(): " + ((IIOParam)object2).getDestinationType());
            bufferedImage = ((ImageReader)object3).read(0, (ImageReadParam)object2);
        }
        catch (Throwable throwable) {
            object6 = throwable;
            throw throwable;
        }
        finally {
            if (object5 != null) {
                if (object6 != null) {
                    try {
                        object5.close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)object6).addSuppressed(throwable);
                    }
                } else {
                    object5.close();
                }
            }
        }
        System.err.println("original: " + bufferedImage);
        if (n3 <= 0 || n3 == bufferedImage.getType()) {
            object5 = bufferedImage;
        } else if (n3 == 13) {
            object5 = ImageUtil.createIndexed((Image)bufferedImage, (int)256, null, (int)259);
        } else {
            object5 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), n3);
            object6 = ((BufferedImage)object5).createGraphics();
            try {
                ((Graphics)object6).drawImage(bufferedImage, 0, 0, null);
            }
            finally {
                ((Graphics)object6).dispose();
            }
        }
        bufferedImage = null;
        object6 = File.createTempFile(file.getName().replace('.', '-'), ".tif");
        System.err.println("output: " + object6);
        object4 = new TIFFImageWriter((ImageWriterSpi)((Object)new TIFFImageWriterSpi()));
        object3 = ImageIO.createImageOutputStream(object6);
        object2 = null;
        try {
            ((TIFFImageWriter)((Object)object4)).setOutput(object3);
            object = ((TIFFImageWriter)((Object)object4)).getDefaultWriteParam();
            ((ImageWriteParam)object).setCompressionMode(2);
            ((ImageWriteParam)object).setCompressionType(((ImageWriteParam)object).getCompressionTypes()[n]);
            System.err.println("compression: " + ((ImageWriteParam)object).getLocalizedCompressionTypeName());
            long l = System.currentTimeMillis();
            ((TIFFImageWriter)((Object)object4)).write(null, new IIOImage((RenderedImage)object5, null, null), (ImageWriteParam)object);
            System.err.println("Write time: " + (System.currentTimeMillis() - l) + " ms");
        }
        catch (Throwable throwable) {
            object2 = throwable;
            throw throwable;
        }
        finally {
            if (object3 != null) {
                if (object2 != null) {
                    try {
                        object3.close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)object2).addSuppressed(throwable);
                    }
                } else {
                    object3.close();
                }
            }
        }
        System.err.println("output.length: " + ((File)object6).length());
        object5 = null;
        object3 = ImageIO.read((File)object6);
        System.err.println("read: " + object3);
        TIFFImageReader.showIt((BufferedImage)object3, ((File)object6).getName());
    }

    private class ListenerDelegate
    extends ProgressListenerBase
    implements IIOWriteWarningListener {
        private final int imageIndex;

        public ListenerDelegate(int n) {
            this.imageIndex = n;
        }

        public void imageComplete(ImageWriter imageWriter) {
            TIFFImageWriter.this.processImageComplete();
        }

        public void imageProgress(ImageWriter imageWriter, float f) {
            TIFFImageWriter.this.processImageProgress(f);
        }

        public void imageStarted(ImageWriter imageWriter, int n) {
            TIFFImageWriter.this.processImageStarted(this.imageIndex);
        }

        public void thumbnailComplete(ImageWriter imageWriter) {
            TIFFImageWriter.this.processThumbnailComplete();
        }

        public void thumbnailProgress(ImageWriter imageWriter, float f) {
            TIFFImageWriter.this.processThumbnailProgress(f);
        }

        public void thumbnailStarted(ImageWriter imageWriter, int n, int n2) {
            TIFFImageWriter.this.processThumbnailStarted(this.imageIndex, n2);
        }

        public void writeAborted(ImageWriter imageWriter) {
            TIFFImageWriter.this.processWriteAborted();
        }

        @Override
        public void warningOccurred(ImageWriter imageWriter, int n, String string) {
            TIFFImageWriter.this.processWarningOccurred(this.imageIndex, string);
        }
    }
}

