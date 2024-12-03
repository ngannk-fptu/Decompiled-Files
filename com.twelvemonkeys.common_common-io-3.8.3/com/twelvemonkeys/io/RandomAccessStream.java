/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.Seekable;
import com.twelvemonkeys.io.SeekableInputStream;
import com.twelvemonkeys.io.SeekableOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

public abstract class RandomAccessStream
implements Seekable,
DataInput,
DataOutput {
    SeekableInputStream inputView = null;
    SeekableOutputStream outputView = null;

    public int read() throws IOException {
        try {
            return this.readByte() & 0xFF;
        }
        catch (EOFException eOFException) {
            return -1;
        }
    }

    public int read(byte[] byArray, int n, int n2) throws IOException {
        int n3;
        if (byArray == null) {
            throw new NullPointerException("bytes == null");
        }
        if (n < 0 || n > byArray.length || n2 < 0 || n + n2 > byArray.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (n2 == 0) {
            return 0;
        }
        int n4 = this.read();
        if (n4 == -1) {
            return -1;
        }
        byArray[n] = (byte)n4;
        try {
            for (n3 = 1; n3 < n2 && (n4 = this.read()) != -1; ++n3) {
                byArray[n + n3] = (byte)n4;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return n3;
    }

    public final int read(byte[] byArray) throws IOException {
        return this.read(byArray, 0, byArray != null ? byArray.length : 1);
    }

    public final SeekableInputStream asInputStream() {
        if (this.inputView == null) {
            this.inputView = new InputStreamView(this);
        }
        return this.inputView;
    }

    public final SeekableOutputStream asOutputStream() {
        if (this.outputView == null) {
            this.outputView = new OutputStreamView(this);
        }
        return this.outputView;
    }

    static final class OutputStreamView
    extends SeekableOutputStream {
        private final RandomAccessStream mStream;

        public OutputStreamView(RandomAccessStream randomAccessStream) {
            if (randomAccessStream == null) {
                throw new IllegalArgumentException("stream == null");
            }
            this.mStream = randomAccessStream;
        }

        @Override
        public boolean isCached() {
            return this.mStream.isCached();
        }

        @Override
        public boolean isCachedFile() {
            return this.mStream.isCachedFile();
        }

        @Override
        public boolean isCachedMemory() {
            return this.mStream.isCachedMemory();
        }

        @Override
        protected void closeImpl() throws IOException {
            this.mStream.close();
        }

        @Override
        protected void flushBeforeImpl(long l) throws IOException {
            this.mStream.flushBefore(l);
        }

        @Override
        protected void seekImpl(long l) throws IOException {
            this.mStream.seek(l);
        }

        @Override
        public void write(int n) throws IOException {
            this.mStream.write(n);
        }

        @Override
        public void write(byte[] byArray, int n, int n2) throws IOException {
            this.mStream.write(byArray, n, n2);
        }
    }

    static final class InputStreamView
    extends SeekableInputStream {
        private final RandomAccessStream mStream;

        public InputStreamView(RandomAccessStream randomAccessStream) {
            if (randomAccessStream == null) {
                throw new IllegalArgumentException("stream == null");
            }
            this.mStream = randomAccessStream;
        }

        @Override
        public boolean isCached() {
            return this.mStream.isCached();
        }

        @Override
        public boolean isCachedFile() {
            return this.mStream.isCachedFile();
        }

        @Override
        public boolean isCachedMemory() {
            return this.mStream.isCachedMemory();
        }

        @Override
        protected void closeImpl() throws IOException {
            this.mStream.close();
        }

        @Override
        protected void flushBeforeImpl(long l) throws IOException {
            this.mStream.flushBefore(l);
        }

        @Override
        protected void seekImpl(long l) throws IOException {
            this.mStream.seek(l);
        }

        @Override
        public int read() throws IOException {
            return this.mStream.read();
        }

        @Override
        public int read(byte[] byArray, int n, int n2) throws IOException {
            return this.mStream.read(byArray, n, n2);
        }
    }
}

