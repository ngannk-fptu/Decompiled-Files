/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdfparser.PDFXRef;
import org.apache.pdfbox.pdfwriter.COSWriterXRefEntry;

public class PDFXRefStream
implements PDFXRef {
    private static final int ENTRY_OBJSTREAM = 2;
    private static final int ENTRY_NORMAL = 1;
    private static final int ENTRY_FREE = 0;
    private final Map<Long, Object> streamData;
    private final Set<Long> objectNumbers;
    private final COSStream stream;
    private long size = -1L;

    @Deprecated
    public PDFXRefStream() {
        this.stream = new COSStream();
        this.streamData = new TreeMap<Long, Object>();
        this.objectNumbers = new TreeSet<Long>();
    }

    public PDFXRefStream(COSDocument cosDocument) {
        this.stream = cosDocument.createCOSStream();
        this.streamData = new TreeMap<Long, Object>();
        this.objectNumbers = new TreeSet<Long>();
    }

    public COSStream getStream() throws IOException {
        this.stream.setItem(COSName.TYPE, (COSBase)COSName.XREF);
        if (this.size == -1L) {
            throw new IllegalArgumentException("size is not set in xrefstream");
        }
        this.stream.setLong(COSName.SIZE, this.size);
        List<Long> indexEntry = this.getIndexEntry();
        COSArray indexAsArray = new COSArray();
        for (Long i : indexEntry) {
            indexAsArray.add(COSInteger.get(i));
        }
        this.stream.setItem(COSName.INDEX, (COSBase)indexAsArray);
        int[] wEntry = this.getWEntry();
        COSArray wAsArray = new COSArray();
        for (int j : wEntry) {
            wAsArray.add(COSInteger.get(j));
        }
        this.stream.setItem(COSName.W, (COSBase)wAsArray);
        OutputStream outputStream = this.stream.createOutputStream(COSName.FLATE_DECODE);
        this.writeStreamData(outputStream, wEntry);
        outputStream.flush();
        outputStream.close();
        Set<COSName> keySet = this.stream.keySet();
        for (COSName cosName : keySet) {
            if (COSName.ROOT.equals(cosName) || COSName.INFO.equals(cosName) || COSName.PREV.equals(cosName) || COSName.ENCRYPT.equals(cosName)) continue;
            COSBase dictionaryObject = this.stream.getDictionaryObject(cosName);
            dictionaryObject.setDirect(true);
        }
        return this.stream;
    }

    public void addTrailerInfo(COSDictionary trailerDict) {
        Set<Map.Entry<COSName, COSBase>> entrySet = trailerDict.entrySet();
        for (Map.Entry<COSName, COSBase> entry : entrySet) {
            COSName key = entry.getKey();
            if (!COSName.INFO.equals(key) && !COSName.ROOT.equals(key) && !COSName.ENCRYPT.equals(key) && !COSName.ID.equals(key) && !COSName.PREV.equals(key)) continue;
            this.stream.setItem(key, entry.getValue());
        }
    }

    public void addEntry(COSWriterXRefEntry entry) {
        this.objectNumbers.add(entry.getKey().getNumber());
        if (entry.isFree()) {
            FreeReference value = new FreeReference();
            value.nextGenNumber = entry.getKey().getGeneration();
            value.nextFree = entry.getKey().getNumber();
            this.streamData.put(value.nextFree, value);
        } else {
            NormalReference value = new NormalReference();
            value.genNumber = entry.getKey().getGeneration();
            value.offset = entry.getOffset();
            this.streamData.put(entry.getKey().getNumber(), value);
        }
    }

    private int[] getWEntry() {
        long[] wMax = new long[3];
        for (Object entry : this.streamData.values()) {
            if (entry instanceof FreeReference) {
                FreeReference free = (FreeReference)entry;
                wMax[0] = Math.max(wMax[0], 0L);
                wMax[1] = Math.max(wMax[1], free.nextFree);
                wMax[2] = Math.max(wMax[2], (long)free.nextGenNumber);
                continue;
            }
            if (entry instanceof NormalReference) {
                NormalReference ref = (NormalReference)entry;
                wMax[0] = Math.max(wMax[0], 1L);
                wMax[1] = Math.max(wMax[1], ref.offset);
                wMax[2] = Math.max(wMax[2], (long)ref.genNumber);
                continue;
            }
            if (entry instanceof ObjectStreamReference) {
                ObjectStreamReference objStream = (ObjectStreamReference)entry;
                wMax[0] = Math.max(wMax[0], 2L);
                wMax[1] = Math.max(wMax[1], objStream.offset);
                wMax[2] = Math.max(wMax[2], objStream.objectNumberOfObjectStream);
                continue;
            }
            throw new RuntimeException("unexpected reference type");
        }
        int[] w = new int[3];
        for (int i = 0; i < w.length; ++i) {
            while (wMax[i] > 0L) {
                int n = i;
                w[n] = w[n] + 1;
                int n2 = i;
                wMax[n2] = wMax[n2] >> 8;
            }
        }
        return w;
    }

    public void setSize(long streamSize) {
        this.size = streamSize;
    }

    private List<Long> getIndexEntry() {
        LinkedList<Long> linkedList = new LinkedList<Long>();
        Long first = null;
        Long length = null;
        TreeSet<Long> objNumbers = new TreeSet<Long>();
        objNumbers.add(0L);
        objNumbers.addAll(this.objectNumbers);
        for (Long objNumber : objNumbers) {
            if (first == null) {
                first = objNumber;
                length = 1L;
            }
            if (first + length == objNumber) {
                length = length + 1L;
            }
            if (first + length >= objNumber) continue;
            linkedList.add(first);
            linkedList.add(length);
            first = objNumber;
            length = 1L;
        }
        linkedList.add(first);
        linkedList.add(length);
        return linkedList;
    }

    private void writeNumber(OutputStream os, long number, int bytes) throws IOException {
        int i;
        byte[] buffer = new byte[bytes];
        for (i = 0; i < bytes; ++i) {
            buffer[i] = (byte)(number & 0xFFL);
            number >>= 8;
        }
        for (i = 0; i < bytes; ++i) {
            os.write(buffer[bytes - i - 1]);
        }
    }

    private void writeStreamData(OutputStream os, int[] w) throws IOException {
        this.writeNumber(os, 0L, w[0]);
        this.writeNumber(os, 0L, w[1]);
        this.writeNumber(os, 65535L, w[2]);
        for (Object entry : this.streamData.values()) {
            if (entry instanceof FreeReference) {
                FreeReference free = (FreeReference)entry;
                this.writeNumber(os, 0L, w[0]);
                this.writeNumber(os, free.nextFree, w[1]);
                this.writeNumber(os, free.nextGenNumber, w[2]);
                continue;
            }
            if (entry instanceof NormalReference) {
                NormalReference ref = (NormalReference)entry;
                this.writeNumber(os, 1L, w[0]);
                this.writeNumber(os, ref.offset, w[1]);
                this.writeNumber(os, ref.genNumber, w[2]);
                continue;
            }
            if (entry instanceof ObjectStreamReference) {
                ObjectStreamReference objStream = (ObjectStreamReference)entry;
                this.writeNumber(os, 2L, w[0]);
                this.writeNumber(os, objStream.offset, w[1]);
                this.writeNumber(os, objStream.objectNumberOfObjectStream, w[2]);
                continue;
            }
            throw new RuntimeException("unexpected reference type");
        }
    }

    @Override
    public COSObject getObject(int objectNumber) {
        return null;
    }

    static class FreeReference {
        int nextGenNumber;
        long nextFree;

        FreeReference() {
        }
    }

    static class NormalReference {
        int genNumber;
        long offset;

        NormalReference() {
        }
    }

    static class ObjectStreamReference {
        long objectNumberOfObjectStream;
        long offset;

        ObjectStreamReference() {
        }
    }
}

