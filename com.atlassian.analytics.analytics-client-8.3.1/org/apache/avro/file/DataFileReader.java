/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.utils.IOUtils
 */
package org.apache.avro.file;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.avro.InvalidAvroMagicException;
import org.apache.avro.file.DataFileConstants;
import org.apache.avro.file.DataFileReader12;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.FileReader;
import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.commons.compress.utils.IOUtils;

public class DataFileReader<D>
extends DataFileStream<D>
implements FileReader<D> {
    private SeekableInputStream sin;
    private long blockStart;
    private int[] partialMatchTable;

    public static <D> FileReader<D> openReader(File file, DatumReader<D> reader) throws IOException {
        SeekableFileInput input = new SeekableFileInput(file);
        try {
            return DataFileReader.openReader(input, reader);
        }
        catch (Throwable e) {
            IOUtils.closeQuietly((Closeable)input);
            throw e;
        }
    }

    public static <D> FileReader<D> openReader(SeekableInput in, DatumReader<D> reader) throws IOException {
        if (in.length() < (long)DataFileConstants.MAGIC.length) {
            throw new InvalidAvroMagicException("Not an Avro data file");
        }
        byte[] magic = new byte[DataFileConstants.MAGIC.length];
        in.seek(0L);
        int offset = 0;
        int length = magic.length;
        while (length > 0) {
            int bytesRead = in.read(magic, offset, length);
            if (bytesRead < 0) {
                throw new EOFException("Unexpected EOF with " + length + " bytes remaining to read");
            }
            length -= bytesRead;
            offset += bytesRead;
        }
        if (Arrays.equals(DataFileConstants.MAGIC, magic)) {
            return new DataFileReader<D>(in, reader, magic);
        }
        if (Arrays.equals(DataFileReader12.MAGIC, magic)) {
            return new DataFileReader12<D>(in, reader);
        }
        throw new InvalidAvroMagicException("Not an Avro data file");
    }

    public static <D> DataFileReader<D> openReader(SeekableInput in, DatumReader<D> reader, DataFileStream.Header header, boolean sync) throws IOException {
        DataFileReader<D> dreader = new DataFileReader<D>(in, reader, header);
        if (sync) {
            dreader.sync(in.tell());
        } else {
            dreader.seek(in.tell());
        }
        return dreader;
    }

    public DataFileReader(File file, DatumReader<D> reader) throws IOException {
        this(new SeekableFileInput(file), reader, true, null);
    }

    public DataFileReader(SeekableInput sin, DatumReader<D> reader) throws IOException {
        this(sin, reader, false, null);
    }

    private DataFileReader(SeekableInput sin, DatumReader<D> reader, byte[] magic) throws IOException {
        this(sin, reader, false, magic);
    }

    protected DataFileReader(SeekableInput sin, DatumReader<D> reader, boolean closeOnError, byte[] magic) throws IOException {
        super(reader);
        try {
            this.sin = new SeekableInputStream(sin);
            this.initialize(this.sin, magic);
            this.blockFinished();
        }
        catch (Throwable e) {
            if (closeOnError) {
                IOUtils.closeQuietly((Closeable)sin);
            }
            throw e;
        }
    }

    protected DataFileReader(SeekableInput sin, DatumReader<D> reader, DataFileStream.Header header) throws IOException {
        super(reader);
        this.sin = new SeekableInputStream(sin);
        this.initialize(header);
    }

    public void seek(long position) throws IOException {
        this.sin.seek(position);
        this.vin = DecoderFactory.get().binaryDecoder(this.sin, this.vin);
        this.datumIn = null;
        this.blockRemaining = 0L;
        this.blockStart = position;
    }

    @Override
    public void sync(long position) throws IOException {
        this.seek(position);
        if (position == 0L && this.getMeta("avro.sync") != null) {
            this.initialize(this.sin, null);
            return;
        }
        if (this.partialMatchTable == null) {
            this.partialMatchTable = this.computePartialMatchTable(this.getHeader().sync);
        }
        byte[] sync = this.getHeader().sync;
        InputStream in = this.vin.inputStream();
        int[] pm = this.partialMatchTable;
        long i = 0L;
        int b = in.read();
        int j = 0;
        while (b != -1) {
            byte cb = (byte)b;
            while (j > 0 && sync[j] != cb) {
                j = pm[j - 1];
            }
            if (sync[j] == cb) {
                ++j;
            }
            if (j == 16) {
                this.blockStart = position + i + 1L;
                return;
            }
            b = in.read();
            ++i;
        }
        this.blockStart = this.sin.tell();
    }

    private int[] computePartialMatchTable(byte[] pattern) {
        int[] pm = new int[pattern.length];
        int i = 1;
        int len = 0;
        while (i < pattern.length) {
            if (pattern[i] == pattern[len]) {
                pm[i++] = ++len;
                continue;
            }
            if (len > 0) {
                len = pm[len - 1];
                continue;
            }
            ++i;
        }
        return pm;
    }

    @Override
    protected void blockFinished() throws IOException {
        this.blockStart = this.sin.tell() - (long)this.vin.inputStream().available();
    }

    public long previousSync() {
        return this.blockStart;
    }

    @Override
    public boolean pastSync(long position) throws IOException {
        return this.blockStart >= position + 16L || this.blockStart >= this.sin.length();
    }

    @Override
    public long tell() throws IOException {
        return this.sin.tell();
    }

    static class SeekableInputStream
    extends InputStream
    implements SeekableInput {
        private final byte[] oneByte = new byte[1];
        private SeekableInput in;

        SeekableInputStream(SeekableInput in) throws IOException {
            this.in = in;
        }

        @Override
        public void seek(long p) throws IOException {
            if (p < 0L) {
                throw new IOException("Illegal seek: " + p);
            }
            this.in.seek(p);
        }

        @Override
        public long tell() throws IOException {
            return this.in.tell();
        }

        @Override
        public long length() throws IOException {
            return this.in.length();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.in.read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.in.read(b, off, len);
        }

        @Override
        public int read() throws IOException {
            int n = this.read(this.oneByte, 0, 1);
            if (n == 1) {
                return this.oneByte[0] & 0xFF;
            }
            return n;
        }

        @Override
        public long skip(long skip) throws IOException {
            long position = this.in.tell();
            long length = this.in.length();
            long remaining = length - position;
            if (remaining > skip) {
                this.in.seek(skip);
                return this.in.tell() - position;
            }
            this.in.seek(remaining);
            return this.in.tell() - position;
        }

        @Override
        public void close() throws IOException {
            this.in.close();
            super.close();
        }

        @Override
        public int available() throws IOException {
            long remaining = this.in.length() - this.in.tell();
            return remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
        }
    }
}

