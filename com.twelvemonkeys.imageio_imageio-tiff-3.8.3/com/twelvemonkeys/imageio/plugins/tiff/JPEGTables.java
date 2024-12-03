/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.metadata.jpeg.JPEGQuality
 *  com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment
 *  com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEGQuality;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegmentUtil;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageInputStream;

class JPEGTables {
    private static final int DHT_LENGTH = 16;
    private static final Map<Integer, List<String>> SEGMENT_IDS = JPEGTables.createSegmentIdsMap();
    private JPEGQTable[] qTables;
    private JPEGHuffmanTable[] dcHTables;
    private JPEGHuffmanTable[] acHTables;
    private final List<JPEGSegment> segments;

    private static Map<Integer, List<String>> createSegmentIdsMap() {
        HashMap<Integer, Object> hashMap = new HashMap<Integer, Object>();
        hashMap.put(65499, null);
        hashMap.put(65476, null);
        return Collections.unmodifiableMap(hashMap);
    }

    public JPEGTables(ImageInputStream imageInputStream) throws IOException {
        this.segments = JPEGSegmentUtil.readSegments((ImageInputStream)imageInputStream, SEGMENT_IDS);
    }

    public JPEGQTable[] getQTables() throws IOException {
        if (this.qTables == null) {
            this.qTables = JPEGQuality.getQTables(this.segments);
        }
        return this.qTables;
    }

    private void getHuffmanTables() throws IOException {
        if (this.dcHTables == null || this.acHTables == null) {
            ArrayList<JPEGHuffmanTable> arrayList = new ArrayList<JPEGHuffmanTable>();
            ArrayList<JPEGHuffmanTable> arrayList2 = new ArrayList<JPEGHuffmanTable>();
            for (JPEGSegment jPEGSegment : this.segments) {
                int n;
                if (jPEGSegment.marker() != 65476) continue;
                DataInputStream dataInputStream = new DataInputStream(jPEGSegment.data());
                for (int i = 0; i < jPEGSegment.length(); i += n) {
                    int n2 = dataInputStream.read();
                    ++i;
                    int n3 = n2 & 0xF;
                    int n4 = n2 >> 4;
                    if (n4 > 1) {
                        throw new IIOException("Bad DHT type: " + n4);
                    }
                    if (n3 >= 4) {
                        throw new IIOException("Bad DHT table index: " + n3);
                    }
                    if (n4 == 0 ? arrayList.size() > n3 : arrayList2.size() > n3) {
                        throw new IIOException("Duplicate DHT table index: " + n3);
                    }
                    short[] sArray = new short[16];
                    for (n = 0; n < 16; ++n) {
                        sArray[n] = (short)dataInputStream.readUnsignedByte();
                    }
                    i += sArray.length;
                    n = 0;
                    for (short s : sArray) {
                        n += s;
                    }
                    short[] sArray2 = new short[n];
                    for (int j = 0; j < n; ++j) {
                        sArray2[j] = (short)dataInputStream.readUnsignedByte();
                    }
                    JPEGHuffmanTable jPEGHuffmanTable = new JPEGHuffmanTable(sArray, sArray2);
                    if (n4 == 0) {
                        arrayList.add(n3, jPEGHuffmanTable);
                        continue;
                    }
                    arrayList2.add(n3, jPEGHuffmanTable);
                }
            }
            this.dcHTables = arrayList.toArray(new JPEGHuffmanTable[arrayList.size()]);
            this.acHTables = arrayList2.toArray(new JPEGHuffmanTable[arrayList2.size()]);
        }
    }

    public JPEGHuffmanTable[] getDCHuffmanTables() throws IOException {
        this.getHuffmanTables();
        return this.dcHTables;
    }

    public JPEGHuffmanTable[] getACHuffmanTables() throws IOException {
        this.getHuffmanTables();
        return this.acHTables;
    }
}

