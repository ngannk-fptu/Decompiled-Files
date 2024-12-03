/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermInfo;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.IndexOutput;
import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.IOUtils;
import com.atlassian.lucene36.util.UnicodeUtil;
import java.io.Closeable;
import java.io.IOException;

final class TermInfosWriter
implements Closeable {
    public static final int FORMAT = -3;
    public static final int FORMAT_VERSION_UTF8_LENGTH_IN_BYTES = -4;
    public static final int FORMAT_CURRENT = -4;
    private FieldInfos fieldInfos;
    private IndexOutput output;
    private TermInfo lastTi = new TermInfo();
    private long size;
    int indexInterval = 128;
    int skipInterval = 16;
    int maxSkipLevels = 10;
    private long lastIndexPointer;
    private boolean isIndex;
    private byte[] lastTermBytes = new byte[10];
    private int lastTermBytesLength = 0;
    private int lastFieldNumber = -1;
    private TermInfosWriter other;
    private UnicodeUtil.UTF8Result utf8Result = new UnicodeUtil.UTF8Result();
    UnicodeUtil.UTF16Result utf16Result1;
    UnicodeUtil.UTF16Result utf16Result2;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    TermInfosWriter(Directory directory, String segment, FieldInfos fis, int interval) throws IOException {
        this.initialize(directory, segment, fis, interval, false);
        boolean success = false;
        try {
            this.other = new TermInfosWriter(directory, segment, fis, interval, true);
            this.other.other = this;
            return;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this.output, this.other);
            throw throwable;
        }
    }

    private TermInfosWriter(Directory directory, String segment, FieldInfos fis, int interval, boolean isIndex) throws IOException {
        this.initialize(directory, segment, fis, interval, isIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void initialize(Directory directory, String segment, FieldInfos fis, int interval, boolean isi) throws IOException {
        this.indexInterval = interval;
        this.fieldInfos = fis;
        this.isIndex = isi;
        this.output = directory.createOutput(segment + (this.isIndex ? ".tii" : ".tis"));
        boolean success = false;
        try {
            this.output.writeInt(-4);
            this.output.writeLong(0L);
            this.output.writeInt(this.indexInterval);
            this.output.writeInt(this.skipInterval);
            this.output.writeInt(this.maxSkipLevels);
            assert (this.initUTF16Results());
            success = true;
            Object var8_7 = null;
            if (success) return;
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this.output);
            throw throwable;
        }
        IOUtils.closeWhileHandlingException(this.output);
    }

    void add(Term term, TermInfo ti) throws IOException {
        UnicodeUtil.UTF16toUTF8(term.text, 0, term.text.length(), this.utf8Result);
        this.add(this.fieldInfos.fieldNumber(term.field), this.utf8Result.result, this.utf8Result.length, ti);
    }

    private boolean initUTF16Results() {
        this.utf16Result1 = new UnicodeUtil.UTF16Result();
        this.utf16Result2 = new UnicodeUtil.UTF16Result();
        return true;
    }

    private int compareToLastTerm(int fieldNumber, byte[] termBytes, int termBytesLength) {
        int cmp;
        if (this.lastFieldNumber != fieldNumber && ((cmp = this.fieldInfos.fieldName(this.lastFieldNumber).compareTo(this.fieldInfos.fieldName(fieldNumber))) != 0 || this.lastFieldNumber != -1)) {
            return cmp;
        }
        UnicodeUtil.UTF8toUTF16(this.lastTermBytes, 0, this.lastTermBytesLength, this.utf16Result1);
        UnicodeUtil.UTF8toUTF16(termBytes, 0, termBytesLength, this.utf16Result2);
        int len = this.utf16Result1.length < this.utf16Result2.length ? this.utf16Result1.length : this.utf16Result2.length;
        for (int i = 0; i < len; ++i) {
            char ch1 = this.utf16Result1.result[i];
            char ch2 = this.utf16Result2.result[i];
            if (ch1 == ch2) continue;
            return ch1 - ch2;
        }
        if (this.utf16Result1.length == 0 && this.lastFieldNumber == -1) {
            return -1;
        }
        return this.utf16Result1.length - this.utf16Result2.length;
    }

    void add(int fieldNumber, byte[] termBytes, int termBytesLength, TermInfo ti) throws IOException {
        assert (this.compareToLastTerm(fieldNumber, termBytes, termBytesLength) < 0 || this.isIndex && termBytesLength == 0 && this.lastTermBytesLength == 0) : "Terms are out of order: field=" + this.fieldInfos.fieldName(fieldNumber) + " (number " + fieldNumber + ")" + " lastField=" + this.fieldInfos.fieldName(this.lastFieldNumber) + " (number " + this.lastFieldNumber + ")" + " text=" + new String(termBytes, 0, termBytesLength, "UTF-8") + " lastText=" + new String(this.lastTermBytes, 0, this.lastTermBytesLength, "UTF-8");
        assert (ti.freqPointer >= this.lastTi.freqPointer) : "freqPointer out of order (" + ti.freqPointer + " < " + this.lastTi.freqPointer + ")";
        assert (ti.proxPointer >= this.lastTi.proxPointer) : "proxPointer out of order (" + ti.proxPointer + " < " + this.lastTi.proxPointer + ")";
        if (!this.isIndex && this.size % (long)this.indexInterval == 0L) {
            this.other.add(this.lastFieldNumber, this.lastTermBytes, this.lastTermBytesLength, this.lastTi);
        }
        this.writeTerm(fieldNumber, termBytes, termBytesLength);
        this.output.writeVInt(ti.docFreq);
        this.output.writeVLong(ti.freqPointer - this.lastTi.freqPointer);
        this.output.writeVLong(ti.proxPointer - this.lastTi.proxPointer);
        if (ti.docFreq >= this.skipInterval) {
            this.output.writeVInt(ti.skipOffset);
        }
        if (this.isIndex) {
            this.output.writeVLong(this.other.output.getFilePointer() - this.lastIndexPointer);
            this.lastIndexPointer = this.other.output.getFilePointer();
        }
        this.lastFieldNumber = fieldNumber;
        this.lastTi.set(ti);
        ++this.size;
    }

    private void writeTerm(int fieldNumber, byte[] termBytes, int termBytesLength) throws IOException {
        int start;
        int limit;
        int n = limit = termBytesLength < this.lastTermBytesLength ? termBytesLength : this.lastTermBytesLength;
        for (start = 0; start < limit && termBytes[start] == this.lastTermBytes[start]; ++start) {
        }
        int length = termBytesLength - start;
        this.output.writeVInt(start);
        this.output.writeVInt(length);
        this.output.writeBytes(termBytes, start, length);
        this.output.writeVInt(fieldNumber);
        if (this.lastTermBytes.length < termBytesLength) {
            this.lastTermBytes = ArrayUtil.grow(this.lastTermBytes, termBytesLength);
        }
        System.arraycopy(termBytes, start, this.lastTermBytes, start, length);
        this.lastTermBytesLength = termBytesLength;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        Object v1;
        try {
            this.output.seek(4L);
            this.output.writeLong(this.size);
            Object var2_1 = null;
        }
        catch (Throwable throwable) {
            Object v0;
            Object var2_2 = null;
            try {
                this.output.close();
                v0 = null;
            }
            catch (Throwable throwable2) {
                Object var4_6;
                v0 = var4_6 = null;
            }
            if (!this.isIndex) {
                this.other.close();
            }
            throw throwable;
        }
        try {
            this.output.close();
            v1 = null;
        }
        catch (Throwable throwable) {
            Object var4_5;
            v1 = var4_5 = null;
        }
        if (!this.isIndex) {
            this.other.close();
        }
    }
}

