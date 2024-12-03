/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.SimpleRenderedImage;
import com.sun.media.jai.codecimpl.util.ImagingException;
import java.awt.Point;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

class GIFImage
extends SimpleRenderedImage {
    private static final int[] INTERLACE_INCREMENT = new int[]{8, 8, 4, 2, -1};
    private static final int[] INTERLACE_OFFSET = new int[]{0, 4, 2, 1, -1};
    private SeekableStream input;
    private boolean interlaceFlag;
    private byte[] block;
    private int blockLength;
    private int bitPos;
    private int nextByte;
    private int initCodeSize;
    private int clearCode;
    private int eofCode;
    private int bitsLeft;
    private int next32Bits;
    private boolean lastBlockFound;
    private int interlacePass;
    private WritableRaster theTile;

    private void skipBlocks() throws IOException {
        int length;
        while ((length = this.input.readUnsignedByte()) != 0) {
            this.input.skipBytes(length);
        }
    }

    GIFImage(SeekableStream input, byte[] globalColorTable) throws IOException {
        int transparentColorIndex;
        boolean transparentColorFlag;
        byte[] localColorTable;
        block12: {
            this.interlaceFlag = false;
            this.block = new byte[255];
            this.blockLength = 0;
            this.bitPos = 0;
            this.nextByte = 0;
            this.next32Bits = 0;
            this.lastBlockFound = false;
            this.interlacePass = 0;
            this.theTile = null;
            this.input = input;
            localColorTable = null;
            transparentColorFlag = false;
            transparentColorIndex = 0;
            try {
                int blockType;
                long startPosition = input.getFilePointer();
                while (true) {
                    if ((blockType = input.readUnsignedByte()) == 44) {
                        input.skipBytes(4);
                        this.width = input.readUnsignedShortLE();
                        this.height = input.readUnsignedShortLE();
                        int idPackedFields = input.readUnsignedByte();
                        boolean localColorTableFlag = (idPackedFields & 0x80) != 0;
                        this.interlaceFlag = (idPackedFields & 0x40) != 0;
                        int numLCTEntries = 1 << (idPackedFields & 7) + 1;
                        if (localColorTableFlag) {
                            localColorTable = new byte[3 * numLCTEntries];
                            input.readFully(localColorTable);
                        } else {
                            localColorTable = null;
                        }
                        break block12;
                    }
                    if (blockType != 33) break;
                    int label = input.readUnsignedByte();
                    if (label == 249) {
                        input.read();
                        int gcePackedFields = input.readUnsignedByte();
                        transparentColorFlag = (gcePackedFields & 1) != 0;
                        input.skipBytes(2);
                        transparentColorIndex = input.readUnsignedByte();
                        input.read();
                        continue;
                    }
                    if (label == 1) {
                        input.skipBytes(13);
                        this.skipBlocks();
                        continue;
                    }
                    if (label == 254) {
                        this.skipBlocks();
                        continue;
                    }
                    if (label == 255) {
                        input.skipBytes(12);
                        this.skipBlocks();
                        continue;
                    }
                    int length = 0;
                    do {
                        length = input.readUnsignedByte();
                        input.skipBytes(length);
                    } while (length > 0);
                }
                throw new IOException(JaiI18N.getString("GIFImage0") + " " + blockType + "!");
            }
            catch (IOException ioe) {
                throw new IOException(JaiI18N.getString("GIFImage1"));
            }
        }
        this.tileGridYOffset = 0;
        this.tileGridXOffset = 0;
        this.minY = 0;
        this.minX = 0;
        this.tileWidth = this.width;
        this.tileHeight = this.height;
        byte[] colorTable = localColorTable != null ? localColorTable : globalColorTable;
        int length = colorTable.length / 3;
        int bits = length == 2 ? 1 : (length == 4 ? 2 : (length == 8 || length == 16 ? 4 : 8));
        int lutLength = 1 << bits;
        byte[] r = new byte[lutLength];
        byte[] g = new byte[lutLength];
        byte[] b = new byte[lutLength];
        int rgbIndex = 0;
        for (int i = 0; i < length; ++i) {
            r[i] = colorTable[rgbIndex++];
            g[i] = colorTable[rgbIndex++];
            b[i] = colorTable[rgbIndex++];
        }
        int[] bitsPerSample = new int[]{bits};
        this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, 1, this.width, new int[]{0});
        this.colorModel = !transparentColorFlag ? (ImageCodec.isIndicesForGrayscale(r, g, b) ? ImageCodec.createComponentColorModel(this.sampleModel) : new IndexColorModel(bits, r.length, r, g, b)) : new IndexColorModel(bits, r.length, r, g, b, transparentColorIndex);
    }

    private void initNext32Bits() {
        this.next32Bits = this.block[0] & 0xFF;
        this.next32Bits |= (this.block[1] & 0xFF) << 8;
        this.next32Bits |= (this.block[2] & 0xFF) << 16;
        this.next32Bits |= this.block[3] << 24;
        this.nextByte = 4;
    }

    private int getCode(int codeSize, int codeMask) throws IOException {
        if (this.bitsLeft <= 0) {
            return this.eofCode;
        }
        int code = this.next32Bits >> this.bitPos & codeMask;
        this.bitPos += codeSize;
        this.bitsLeft -= codeSize;
        while (this.bitPos >= 8 && !this.lastBlockFound) {
            this.next32Bits >>>= 8;
            this.bitPos -= 8;
            if (this.nextByte >= this.blockLength) {
                int nbytes;
                this.blockLength = this.input.readUnsignedByte();
                if (this.blockLength == 0) {
                    this.lastBlockFound = true;
                    if (this.bitsLeft < 0) {
                        return this.eofCode;
                    }
                    return code;
                }
                int off = 0;
                for (int left = this.blockLength; left > 0; left -= nbytes) {
                    nbytes = this.input.read(this.block, off, left);
                    off += nbytes;
                }
                this.bitsLeft += this.blockLength << 3;
                this.nextByte = 0;
            }
            this.next32Bits |= this.block[this.nextByte++] << 24;
        }
        return code;
    }

    private void initializeStringTable(int[] prefix, byte[] suffix, byte[] initial, int[] length) {
        int i;
        int numEntries = 1 << this.initCodeSize;
        for (i = 0; i < numEntries; ++i) {
            prefix[i] = -1;
            suffix[i] = (byte)i;
            initial[i] = (byte)i;
            length[i] = 1;
        }
        for (i = numEntries; i < 4096; ++i) {
            prefix[i] = -1;
            length[i] = 1;
        }
    }

    private Point outputPixels(byte[] string, int len, Point streamPos, byte[] rowBuf) {
        if (this.interlacePass < 0 || this.interlacePass > 3) {
            return streamPos;
        }
        for (int i = 0; i < len; ++i) {
            if (streamPos.x >= this.minX) {
                rowBuf[streamPos.x - this.minX] = string[i];
            }
            ++streamPos.x;
            if (streamPos.x != this.width) continue;
            this.theTile.setDataElements(this.minX, streamPos.y, this.width, 1, rowBuf);
            streamPos.x = 0;
            if (this.interlaceFlag) {
                streamPos.y += INTERLACE_INCREMENT[this.interlacePass];
                if (streamPos.y < this.height) continue;
                ++this.interlacePass;
                if (this.interlacePass > 3) {
                    return streamPos;
                }
                streamPos.y = INTERLACE_OFFSET[this.interlacePass];
                continue;
            }
            ++streamPos.y;
        }
        return streamPos;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public synchronized Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("GIFImage2"));
        }
        if (this.theTile != null) {
            return this.theTile;
        }
        this.theTile = WritableRaster.createWritableRaster(this.sampleModel, this.sampleModel.createDataBuffer(), null);
        streamPos = new Point(0, 0);
        rowBuf = new byte[this.width];
        try {
            this.initCodeSize = this.input.readUnsignedByte();
            left = this.blockLength = this.input.readUnsignedByte();
            off = 0;
            while (left > 0) {
                nbytes = this.input.read(this.block, off, left);
                left -= nbytes;
                off += nbytes;
            }
            this.bitPos = 0;
            this.nextByte = 0;
            this.lastBlockFound = false;
            this.bitsLeft = this.blockLength << 3;
            this.initNext32Bits();
            this.clearCode = 1 << this.initCodeSize;
            this.eofCode = this.clearCode + 1;
            oldCode = 0;
            prefix = new int[4096];
            suffix = new byte[4096];
            initial = new byte[4096];
            length = new int[4096];
            string = new byte[4096];
            this.initializeStringTable(prefix, suffix, initial, length);
            tableIndex = (1 << this.initCodeSize) + 2;
            codeSize = this.initCodeSize + 1;
            codeMask = (1 << codeSize) - 1;
            while (true) {
                block15: {
                    if ((code = this.getCode(codeSize, codeMask)) != this.clearCode) break block15;
                    this.initializeStringTable(prefix, suffix, initial, length);
                    tableIndex = (1 << this.initCodeSize) + 2;
                    codeSize = this.initCodeSize + 1;
                    codeMask = (1 << codeSize) - 1;
                    code = this.getCode(codeSize, codeMask);
                    if (code == this.eofCode) {
                        var17_20 = this.theTile;
                        return var17_20;
                    }
                    ** GOTO lbl58
                }
                if (code == this.eofCode) {
                    var17_21 = this.theTile;
                    return var17_21;
                }
                newSuffixIndex = code < tableIndex ? code : oldCode;
                ti = tableIndex++;
                prefix[ti] = oc = oldCode;
                suffix[ti] = initial[newSuffixIndex];
                initial[ti] = initial[oc];
                length[ti] = length[oc] + 1;
                if (tableIndex == 1 << codeSize && tableIndex < 4096) {
                    codeMask = (1 << ++codeSize) - 1;
                }
lbl58:
                // 4 sources

                c = code;
                len = length[c];
                for (i = len - 1; i >= 0; --i) {
                    string[i] = suffix[c];
                    c = prefix[c];
                }
                this.outputPixels(string, len, streamPos, rowBuf);
                oldCode = code;
                continue;
                break;
            }
            catch (IOException e) {
                message = JaiI18N.getString("GIFImage3");
                ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
            }
        }
        finally {
            return this.theTile;
        }
    }

    public void dispose() {
        this.theTile = null;
    }
}

