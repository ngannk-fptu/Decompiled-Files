/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.decoder;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.color.ColorConversions;
import org.apache.commons.imaging.common.BinaryFileParser;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.jpeg.JpegUtils;
import org.apache.commons.imaging.formats.jpeg.decoder.Block;
import org.apache.commons.imaging.formats.jpeg.decoder.Dct;
import org.apache.commons.imaging.formats.jpeg.decoder.JpegInputStream;
import org.apache.commons.imaging.formats.jpeg.decoder.YCbCrConverter;
import org.apache.commons.imaging.formats.jpeg.decoder.ZigZag;
import org.apache.commons.imaging.formats.jpeg.segments.DhtSegment;
import org.apache.commons.imaging.formats.jpeg.segments.DqtSegment;
import org.apache.commons.imaging.formats.jpeg.segments.SofnSegment;
import org.apache.commons.imaging.formats.jpeg.segments.SosSegment;

public class JpegDecoder
extends BinaryFileParser
implements JpegUtils.Visitor {
    private final DqtSegment.QuantizationTable[] quantizationTables = new DqtSegment.QuantizationTable[4];
    private final DhtSegment.HuffmanTable[] huffmanDCTables = new DhtSegment.HuffmanTable[4];
    private final DhtSegment.HuffmanTable[] huffmanACTables = new DhtSegment.HuffmanTable[4];
    private SofnSegment sofnSegment;
    private SosSegment sosSegment;
    private final float[][] scaledQuantizationTables = new float[4][];
    private BufferedImage image;
    private ImageReadException imageReadException;
    private IOException ioException;
    private final int[] zz = new int[64];
    private final int[] blockInt = new int[64];
    private final float[] block = new float[64];

    @Override
    public boolean beginSOS() {
        return true;
    }

    @Override
    public void visitSOS(int marker, byte[] markerBytes, byte[] imageData) {
        ByteArrayInputStream is = new ByteArrayInputStream(imageData);
        try {
            WritableRaster raster;
            DirectColorModel colorModel;
            int segmentLength = BinaryFunctions.read2Bytes("segmentLength", is, "Not a Valid JPEG File", this.getByteOrder());
            byte[] sosSegmentBytes = BinaryFunctions.readBytes("SosSegment", is, segmentLength - 2, "Not a Valid JPEG File");
            this.sosSegment = new SosSegment(marker, sosSegmentBytes);
            int[] scanPayload = new int[imageData.length - segmentLength];
            for (int payloadReadCount = 0; payloadReadCount < scanPayload.length; ++payloadReadCount) {
                scanPayload[payloadReadCount] = is.read();
            }
            int hMax = 0;
            int vMax = 0;
            for (int i = 0; i < this.sofnSegment.numberOfComponents; ++i) {
                hMax = Math.max(hMax, this.sofnSegment.getComponents((int)i).horizontalSamplingFactor);
                vMax = Math.max(vMax, this.sofnSegment.getComponents((int)i).verticalSamplingFactor);
            }
            int hSize = 8 * hMax;
            int vSize = 8 * vMax;
            int xMCUs = (this.sofnSegment.width + hSize - 1) / hSize;
            int yMCUs = (this.sofnSegment.height + vSize - 1) / vSize;
            Block[] mcu = this.allocateMCUMemory();
            Block[] scaledMCU = new Block[mcu.length];
            for (int i = 0; i < scaledMCU.length; ++i) {
                scaledMCU[i] = new Block(hSize, vSize);
            }
            int[] preds = new int[this.sofnSegment.numberOfComponents];
            if (this.sofnSegment.numberOfComponents == 4) {
                colorModel = new DirectColorModel(24, 0xFF0000, 65280, 255);
                int[] bandMasks = new int[]{0xFF0000, 65280, 255};
                raster = Raster.createPackedRaster(3, this.sofnSegment.width, this.sofnSegment.height, bandMasks, null);
            } else if (this.sofnSegment.numberOfComponents == 3) {
                colorModel = new DirectColorModel(24, 0xFF0000, 65280, 255);
                raster = Raster.createPackedRaster(3, this.sofnSegment.width, this.sofnSegment.height, new int[]{0xFF0000, 65280, 255}, null);
            } else if (this.sofnSegment.numberOfComponents == 1) {
                colorModel = new DirectColorModel(24, 0xFF0000, 65280, 255);
                raster = Raster.createPackedRaster(3, this.sofnSegment.width, this.sofnSegment.height, new int[]{0xFF0000, 65280, 255}, null);
            } else {
                throw new ImageReadException(this.sofnSegment.numberOfComponents + " components are invalid or unsupported");
            }
            DataBuffer dataBuffer = raster.getDataBuffer();
            JpegInputStream[] bitInputStreams = JpegDecoder.splitByRstMarkers(scanPayload);
            int bitInputStreamCount = 0;
            JpegInputStream bitInputStream = bitInputStreams[0];
            for (int y1 = 0; y1 < vSize * yMCUs; y1 += vSize) {
                for (int x1 = 0; x1 < hSize * xMCUs; x1 += hSize) {
                    if (!bitInputStream.hasNext() && ++bitInputStreamCount < bitInputStreams.length) {
                        bitInputStream = bitInputStreams[bitInputStreamCount];
                    }
                    this.readMCU(bitInputStream, preds, mcu);
                    this.rescaleMCU(mcu, hSize, vSize, scaledMCU);
                    int srcRowOffset = 0;
                    int dstRowOffset = y1 * this.sofnSegment.width + x1;
                    for (int y2 = 0; y2 < vSize && y1 + y2 < this.sofnSegment.height; ++y2) {
                        for (int x2 = 0; x2 < hSize && x1 + x2 < this.sofnSegment.width; ++x2) {
                            int Y;
                            if (scaledMCU.length == 4) {
                                int C = scaledMCU[0].samples[srcRowOffset + x2];
                                int M = scaledMCU[1].samples[srcRowOffset + x2];
                                int Y2 = scaledMCU[2].samples[srcRowOffset + x2];
                                int K = scaledMCU[3].samples[srcRowOffset + x2];
                                int rgb = ColorConversions.convertCMYKtoRGB(C, M, Y2, K);
                                dataBuffer.setElem(dstRowOffset + x2, rgb);
                                continue;
                            }
                            if (scaledMCU.length == 3) {
                                Y = scaledMCU[0].samples[srcRowOffset + x2];
                                int Cb = scaledMCU[1].samples[srcRowOffset + x2];
                                int Cr = scaledMCU[2].samples[srcRowOffset + x2];
                                int rgb = YCbCrConverter.convertYCbCrToRGB(Y, Cb, Cr);
                                dataBuffer.setElem(dstRowOffset + x2, rgb);
                                continue;
                            }
                            if (mcu.length == 1) {
                                Y = scaledMCU[0].samples[srcRowOffset + x2];
                                dataBuffer.setElem(dstRowOffset + x2, Y << 16 | Y << 8 | Y);
                                continue;
                            }
                            throw new ImageReadException("Unsupported JPEG with " + mcu.length + " components");
                        }
                        srcRowOffset += hSize;
                        dstRowOffset += this.sofnSegment.width;
                    }
                }
            }
            this.image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), new Properties());
        }
        catch (ImageReadException imageReadEx) {
            this.imageReadException = imageReadEx;
        }
        catch (IOException ioEx) {
            this.ioException = ioEx;
        }
        catch (RuntimeException ex) {
            this.imageReadException = new ImageReadException("Error parsing JPEG", ex);
        }
    }

    @Override
    public boolean visitSegment(int marker, byte[] markerBytes, int segmentLength, byte[] segmentLengthBytes, byte[] segmentData) throws ImageReadException, IOException {
        block11: {
            block12: {
                block10: {
                    int[] sofnSegments = new int[]{65472, 65473, 65474, 65475, 65477, 65478, 65479, 65481, 65482, 65483, 65485, 65486, 65487};
                    if (Arrays.binarySearch(sofnSegments, marker) < 0) break block10;
                    if (marker != 65472) {
                        throw new ImageReadException("Only sequential, baseline JPEGs are supported at the moment");
                    }
                    this.sofnSegment = new SofnSegment(marker, segmentData);
                    break block11;
                }
                if (marker != 65499) break block12;
                DqtSegment dqtSegment = new DqtSegment(marker, segmentData);
                for (int i = 0; i < dqtSegment.quantizationTables.size(); ++i) {
                    DqtSegment.QuantizationTable table = dqtSegment.quantizationTables.get(i);
                    if (0 > table.destinationIdentifier || table.destinationIdentifier >= this.quantizationTables.length) {
                        throw new ImageReadException("Invalid quantization table identifier " + table.destinationIdentifier);
                    }
                    this.quantizationTables[table.destinationIdentifier] = table;
                    int[] quantizationMatrixInt = new int[64];
                    ZigZag.zigZagToBlock(table.getElements(), quantizationMatrixInt);
                    float[] quantizationMatrixFloat = new float[64];
                    for (int j = 0; j < 64; ++j) {
                        quantizationMatrixFloat[j] = quantizationMatrixInt[j];
                    }
                    Dct.scaleDequantizationMatrix(quantizationMatrixFloat);
                    this.scaledQuantizationTables[table.destinationIdentifier] = quantizationMatrixFloat;
                }
                break block11;
            }
            if (marker != 65476) break block11;
            DhtSegment dhtSegment = new DhtSegment(marker, segmentData);
            for (int i = 0; i < dhtSegment.huffmanTables.size(); ++i) {
                DhtSegment.HuffmanTable[] tables;
                DhtSegment.HuffmanTable table = dhtSegment.huffmanTables.get(i);
                if (table.tableClass == 0) {
                    tables = this.huffmanDCTables;
                } else if (table.tableClass == 1) {
                    tables = this.huffmanACTables;
                } else {
                    throw new ImageReadException("Invalid huffman table class " + table.tableClass);
                }
                if (0 > table.destinationIdentifier || table.destinationIdentifier >= tables.length) {
                    throw new ImageReadException("Invalid huffman table identifier " + table.destinationIdentifier);
                }
                tables[table.destinationIdentifier] = table;
            }
        }
        return true;
    }

    private void rescaleMCU(Block[] dataUnits, int hSize, int vSize, Block[] ret) {
        for (int i = 0; i < dataUnits.length; ++i) {
            Block dataUnit = dataUnits[i];
            if (dataUnit.width == hSize && dataUnit.height == vSize) {
                System.arraycopy(dataUnit.samples, 0, ret[i].samples, 0, hSize * vSize);
                continue;
            }
            int hScale = hSize / dataUnit.width;
            int vScale = vSize / dataUnit.height;
            if (hScale == 2 && vScale == 2) {
                int srcRowOffset = 0;
                int dstRowOffset = 0;
                for (int y = 0; y < dataUnit.height; ++y) {
                    for (int x = 0; x < hSize; ++x) {
                        int sample;
                        ret[i].samples[dstRowOffset + x] = sample = dataUnit.samples[srcRowOffset + (x >> 1)];
                        ret[i].samples[dstRowOffset + hSize + x] = sample;
                    }
                    srcRowOffset += dataUnit.width;
                    dstRowOffset += 2 * hSize;
                }
                continue;
            }
            int dstRowOffset = 0;
            for (int y = 0; y < vSize; ++y) {
                for (int x = 0; x < hSize; ++x) {
                    ret[i].samples[dstRowOffset + x] = dataUnit.samples[y / vScale * dataUnit.width + x / hScale];
                }
                dstRowOffset += hSize;
            }
        }
    }

    private Block[] allocateMCUMemory() throws ImageReadException {
        Block[] mcu = new Block[this.sosSegment.numberOfComponents];
        for (int i = 0; i < this.sosSegment.numberOfComponents; ++i) {
            Block fullBlock;
            SosSegment.Component scanComponent = this.sosSegment.getComponents(i);
            SofnSegment.Component frameComponent = null;
            for (int j = 0; j < this.sofnSegment.numberOfComponents; ++j) {
                if (this.sofnSegment.getComponents((int)j).componentIdentifier != scanComponent.scanComponentSelector) continue;
                frameComponent = this.sofnSegment.getComponents(j);
                break;
            }
            if (frameComponent == null) {
                throw new ImageReadException("Invalid component");
            }
            mcu[i] = fullBlock = new Block(8 * frameComponent.horizontalSamplingFactor, 8 * frameComponent.verticalSamplingFactor);
        }
        return mcu;
    }

    private void readMCU(JpegInputStream is, int[] preds, Block[] mcu) throws IOException, ImageReadException {
        for (int i = 0; i < this.sosSegment.numberOfComponents; ++i) {
            SosSegment.Component scanComponent = this.sosSegment.getComponents(i);
            SofnSegment.Component frameComponent = null;
            for (int j = 0; j < this.sofnSegment.numberOfComponents; ++j) {
                if (this.sofnSegment.getComponents((int)j).componentIdentifier != scanComponent.scanComponentSelector) continue;
                frameComponent = this.sofnSegment.getComponents(j);
                break;
            }
            if (frameComponent == null) {
                throw new ImageReadException("Invalid component");
            }
            Block fullBlock = mcu[i];
            for (int y = 0; y < frameComponent.verticalSamplingFactor; ++y) {
                for (int x = 0; x < frameComponent.horizontalSamplingFactor; ++x) {
                    Arrays.fill(this.zz, 0);
                    int t = this.decode(is, this.huffmanDCTables[scanComponent.dcCodingTableSelector]);
                    int diff = this.receive(t, is);
                    diff = this.extend(diff, t);
                    this.zz[0] = preds[i] + diff;
                    preds[i] = this.zz[0];
                    int k = 1;
                    while (true) {
                        int rrrr;
                        int rs = this.decode(is, this.huffmanACTables[scanComponent.acCodingTableSelector]);
                        int ssss = rs & 0xF;
                        int r = rrrr = rs >> 4;
                        if (ssss == 0) {
                            if (r != 15) break;
                            k += 16;
                            continue;
                        }
                        this.zz[k += r] = this.receive(ssss, is);
                        this.zz[k] = this.extend(this.zz[k], ssss);
                        if (k == 63) break;
                        ++k;
                    }
                    int shift = 1 << this.sofnSegment.precision - 1;
                    int max = (1 << this.sofnSegment.precision) - 1;
                    float[] scaledQuantizationTable = this.scaledQuantizationTables[frameComponent.quantTabDestSelector];
                    ZigZag.zigZagToBlock(this.zz, this.blockInt);
                    for (int j = 0; j < 64; ++j) {
                        this.block[j] = (float)this.blockInt[j] * scaledQuantizationTable[j];
                    }
                    Dct.inverseDCT8x8(this.block);
                    int dstRowOffset = 8 * y * 8 * frameComponent.horizontalSamplingFactor + 8 * x;
                    int srcNext = 0;
                    for (int yy = 0; yy < 8; ++yy) {
                        for (int xx = 0; xx < 8; ++xx) {
                            float sample = this.block[srcNext++];
                            int result = (sample += (float)shift) < 0.0f ? 0 : (sample > (float)max ? max : JpegDecoder.fastRound(sample));
                            fullBlock.samples[dstRowOffset + xx] = result;
                        }
                        dstRowOffset += 8 * frameComponent.horizontalSamplingFactor;
                    }
                }
            }
        }
    }

    static JpegInputStream[] splitByRstMarkers(int[] scanPayload) {
        List<Integer> intervalStarts = JpegDecoder.getIntervalStartPositions(scanPayload);
        int intervalCount = intervalStarts.size();
        JpegInputStream[] streams = new JpegInputStream[intervalCount];
        for (int i = 0; i < intervalCount; ++i) {
            int from = intervalStarts.get(i);
            int to = i < intervalCount - 1 ? intervalStarts.get(i + 1) - 2 : scanPayload.length;
            int[] interval = Arrays.copyOfRange(scanPayload, from, to);
            streams[i] = new JpegInputStream(interval);
        }
        return streams;
    }

    static List<Integer> getIntervalStartPositions(int[] scanPayload) {
        ArrayList<Integer> intervalStarts = new ArrayList<Integer>();
        intervalStarts.add(0);
        boolean foundFF = false;
        boolean foundD0toD7 = false;
        for (int pos = 0; pos < scanPayload.length; ++pos) {
            if (foundFF) {
                if (scanPayload[pos] >= 208 && scanPayload[pos] <= 215) {
                    foundD0toD7 = true;
                } else {
                    foundFF = false;
                }
            }
            if (scanPayload[pos] == 255) {
                foundFF = true;
            }
            if (!foundFF || !foundD0toD7) continue;
            intervalStarts.add(pos + 1);
            foundD0toD7 = false;
            foundFF = false;
        }
        return intervalStarts;
    }

    private static int fastRound(float x) {
        return (int)(x + 0.5f);
    }

    private int extend(int v, int t) {
        int vt = 1 << t - 1;
        if (v < vt) {
            vt = (-1 << t) + 1;
            v += vt;
        }
        return v;
    }

    private int receive(int ssss, JpegInputStream is) throws IOException, ImageReadException {
        int v = 0;
        for (int i = 0; i != ssss; ++i) {
            v = (v << 1) + is.nextBit();
        }
        return v;
    }

    private int decode(JpegInputStream is, DhtSegment.HuffmanTable huffmanTable) throws IOException, ImageReadException {
        int i = 1;
        int code = is.nextBit();
        while (code > huffmanTable.getMaxCode(i)) {
            ++i;
            code = code << 1 | is.nextBit();
        }
        int j = huffmanTable.getValPtr(i);
        return huffmanTable.getHuffVal(j += code - huffmanTable.getMinCode(i));
    }

    public BufferedImage decode(ByteSource byteSource) throws IOException, ImageReadException {
        JpegUtils jpegUtils = new JpegUtils();
        jpegUtils.traverseJFIF(byteSource, this);
        if (this.imageReadException != null) {
            throw this.imageReadException;
        }
        if (this.ioException != null) {
            throw this.ioException;
        }
        return this.image;
    }
}

