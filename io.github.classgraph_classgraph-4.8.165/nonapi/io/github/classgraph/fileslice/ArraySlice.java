/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.fileslice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.fileslice.Slice;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessArrayReader;
import nonapi.io.github.classgraph.fileslice.reader.RandomAccessReader;

public class ArraySlice
extends Slice {
    public byte[] arr;

    private ArraySlice(ArraySlice parentSlice, long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler) {
        super(parentSlice, offset, length, isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
        this.arr = parentSlice.arr;
    }

    public ArraySlice(byte[] arr, boolean isDeflatedZipEntry, long inflatedLengthHint, NestedJarHandler nestedJarHandler) {
        super(arr.length, isDeflatedZipEntry, inflatedLengthHint, nestedJarHandler);
        this.arr = arr;
    }

    @Override
    public Slice slice(long offset, long length, boolean isDeflatedZipEntry, long inflatedLengthHint) {
        if (this.isDeflatedZipEntry) {
            throw new IllegalArgumentException("Cannot slice a deflated zip entry");
        }
        return new ArraySlice(this, offset, length, isDeflatedZipEntry, inflatedLengthHint, this.nestedJarHandler);
    }

    @Override
    public byte[] load() throws IOException {
        if (this.isDeflatedZipEntry) {
            try (InputStream inputStream = this.open();){
                byte[] byArray = NestedJarHandler.readAllBytesAsArray(inputStream, this.inflatedLengthHint);
                return byArray;
            }
        }
        if (this.sliceStartPos == 0L && this.sliceLength == (long)this.arr.length) {
            return this.arr;
        }
        return Arrays.copyOfRange(this.arr, (int)this.sliceStartPos, (int)(this.sliceStartPos + this.sliceLength));
    }

    @Override
    public RandomAccessReader randomAccessReader() {
        return new RandomAccessArrayReader(this.arr, (int)this.sliceStartPos, (int)this.sliceLength);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

