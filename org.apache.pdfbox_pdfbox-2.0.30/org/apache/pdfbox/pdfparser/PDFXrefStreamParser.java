/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdfparser.BaseParser;
import org.apache.pdfbox.pdfparser.InputStreamSource;
import org.apache.pdfbox.pdfparser.XrefTrailerResolver;

public class PDFXrefStreamParser
extends BaseParser {
    private final XrefTrailerResolver xrefTrailerResolver;
    private final int[] w = new int[3];
    private ObjectNumbers objectNumbers = null;

    public PDFXrefStreamParser(COSStream stream, COSDocument document, XrefTrailerResolver resolver) throws IOException {
        super(new InputStreamSource(stream.createInputStream()));
        this.document = document;
        this.xrefTrailerResolver = resolver;
        try {
            this.initParserValues(stream);
        }
        catch (IOException exception) {
            this.close();
            throw exception;
        }
    }

    private void initParserValues(COSStream stream) throws IOException {
        COSArray wArray = stream.getCOSArray(COSName.W);
        if (wArray == null) {
            throw new IOException("/W array is missing in Xref stream");
        }
        if (wArray.size() != 3) {
            throw new IOException("Wrong number of values for /W array in XRef: " + Arrays.toString(this.w));
        }
        for (int i = 0; i < 3; ++i) {
            this.w[i] = wArray.getInt(i, 0);
        }
        if (this.w[0] < 0 || this.w[1] < 0 || this.w[2] < 0) {
            throw new IOException("Incorrect /W array in XRef: " + Arrays.toString(this.w));
        }
        COSArray indexArray = stream.getCOSArray(COSName.INDEX);
        if (indexArray == null) {
            indexArray = new COSArray();
            indexArray.add(COSInteger.ZERO);
            indexArray.add(COSInteger.get(stream.getInt(COSName.SIZE, 0)));
        }
        if (indexArray.size() == 0 || indexArray.size() % 2 == 1) {
            throw new IOException("Wrong number of values for /Index array in XRef: " + Arrays.toString(this.w));
        }
        this.objectNumbers = new ObjectNumbers(indexArray);
    }

    private void close() throws IOException {
        if (this.seqSource != null) {
            this.seqSource.close();
        }
        this.document = null;
    }

    public void parse() throws IOException {
        byte[] currLine = new byte[this.w[0] + this.w[1] + this.w[2]];
        while (!this.seqSource.isEOF() && this.objectNumbers.hasNext()) {
            int type;
            this.seqSource.read(currLine);
            long objID = this.objectNumbers.next();
            int n = type = this.w[0] == 0 ? 1 : (int)this.parseValue(currLine, 0, this.w[0]);
            if (type == 0) continue;
            long offset = this.parseValue(currLine, this.w[0], this.w[1]);
            int genNum = type == 1 ? (int)this.parseValue(currLine, this.w[0] + this.w[1], this.w[2]) : 0;
            COSObjectKey objKey = new COSObjectKey(objID, genNum);
            if (type == 1) {
                this.xrefTrailerResolver.setXRef(objKey, offset);
                continue;
            }
            this.xrefTrailerResolver.setXRef(objKey, -offset);
        }
        this.close();
    }

    private long parseValue(byte[] data, int start, int length) {
        long value = 0L;
        for (int i = 0; i < length; ++i) {
            value += ((long)data[i + start] & 0xFFL) << (length - i - 1) * 8;
        }
        return value;
    }

    private static class ObjectNumbers
    implements Iterator<Long> {
        private final long[] start;
        private final long[] end;
        private int currentRange = 0;
        private long currentEnd = 0L;
        private long currentNumber = 0L;

        private ObjectNumbers(COSArray indexArray) throws IOException {
            this.start = new long[indexArray.size() / 2];
            this.end = new long[this.start.length];
            int counter = 0;
            Iterator<COSBase> indexIter = indexArray.iterator();
            while (indexIter.hasNext()) {
                COSBase base = indexIter.next();
                if (!(base instanceof COSInteger)) {
                    throw new IOException("Xref stream must have integer in /Index array");
                }
                long startValue = ((COSInteger)base).longValue();
                if (!indexIter.hasNext()) break;
                base = indexIter.next();
                if (!(base instanceof COSInteger)) {
                    throw new IOException("Xref stream must have integer in /Index array");
                }
                long sizeValue = ((COSInteger)base).longValue();
                this.start[counter] = startValue;
                this.end[counter] = startValue + sizeValue;
                ++counter;
            }
            this.currentNumber = this.start[0];
            this.currentEnd = this.end[0];
        }

        @Override
        public boolean hasNext() {
            if (this.start.length == 1) {
                return this.currentNumber < this.currentEnd;
            }
            return this.currentRange < this.start.length - 1 || this.currentNumber < this.currentEnd;
        }

        @Override
        public Long next() {
            if (this.currentNumber < this.currentEnd) {
                return this.currentNumber++;
            }
            if (this.currentRange >= this.start.length - 1) {
                throw new NoSuchElementException();
            }
            this.currentNumber = this.start[++this.currentRange];
            this.currentEnd = this.end[this.currentRange];
            return this.currentNumber++;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

