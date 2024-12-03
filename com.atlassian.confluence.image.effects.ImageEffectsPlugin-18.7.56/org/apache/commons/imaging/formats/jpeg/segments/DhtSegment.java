/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.jpeg.segments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.jpeg.segments.Segment;

public class DhtSegment
extends Segment {
    public final List<HuffmanTable> huffmanTables;

    public DhtSegment(int marker, byte[] segmentData) throws IOException {
        this(marker, segmentData.length, new ByteArrayInputStream(segmentData));
    }

    public DhtSegment(int marker, int length, InputStream is) throws IOException {
        super(marker, length);
        ArrayList<HuffmanTable> huffmanTables = new ArrayList<HuffmanTable>();
        while (length > 0) {
            int tableClassAndDestinationId = 0xFF & BinaryFunctions.readByte("TableClassAndDestinationId", is, "Not a Valid JPEG File");
            --length;
            int tableClass = tableClassAndDestinationId >> 4 & 0xF;
            int destinationIdentifier = tableClassAndDestinationId & 0xF;
            int[] bits = new int[17];
            int bitsSum = 0;
            for (int i = 1; i < bits.length; ++i) {
                bits[i] = 0xFF & BinaryFunctions.readByte("Li", is, "Not a Valid JPEG File");
                --length;
                bitsSum += bits[i];
            }
            int[] huffVal = new int[bitsSum];
            for (int i = 0; i < bitsSum; ++i) {
                huffVal[i] = 0xFF & BinaryFunctions.readByte("Vij", is, "Not a Valid JPEG File");
                --length;
            }
            huffmanTables.add(new HuffmanTable(tableClass, destinationIdentifier, bits, huffVal));
        }
        this.huffmanTables = Collections.unmodifiableList(huffmanTables);
    }

    @Override
    public String getDescription() {
        return "DHT (" + this.getSegmentType() + ")";
    }

    public static class HuffmanTable {
        public final int tableClass;
        public final int destinationIdentifier;
        private final int[] huffVal;
        private final int[] huffSize = new int[4096];
        private final int[] huffCode;
        private final int[] minCode = new int[17];
        private final int[] maxCode = new int[17];
        private final int[] valPtr = new int[17];

        HuffmanTable(int tableClass, int destinationIdentifier, int[] bits, int[] huffVal) {
            this.tableClass = tableClass;
            this.destinationIdentifier = destinationIdentifier;
            this.huffVal = huffVal;
            int k = 0;
            int i = 1;
            int j = 1;
            int lastK = -1;
            while (true) {
                if (j > bits[i]) {
                    j = 1;
                    if (++i <= 16) continue;
                    break;
                }
                this.huffSize[k] = i;
                ++k;
                ++j;
            }
            this.huffSize[k] = 0;
            lastK = k;
            k = 0;
            int code = 0;
            int si = this.huffSize[0];
            this.huffCode = new int[lastK];
            while (k < lastK) {
                this.huffCode[k] = code++;
                if (this.huffSize[++k] == si) continue;
                if (this.huffSize[k] == 0) break;
                do {
                    code <<= 1;
                } while (this.huffSize[k] != ++si);
            }
            i = 0;
            j = 0;
            while (++i <= 16) {
                if (bits[i] == 0) {
                    this.maxCode[i] = -1;
                    continue;
                }
                this.valPtr[i] = j;
                this.minCode[i] = this.huffCode[j];
                this.maxCode[i] = this.huffCode[j += bits[i] - 1];
                ++j;
            }
        }

        public int getHuffVal(int i) {
            return this.huffVal[i];
        }

        public int getMinCode(int i) {
            return this.minCode[i];
        }

        public int getMaxCode(int i) {
            return this.maxCode[i];
        }

        public int getValPtr(int i) {
            return this.valPtr[i];
        }
    }
}

