/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.SkippingCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherIOException;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.util.Arrays;

public class CipherInputStream
extends FilterInputStream {
    private static final int INPUT_BUF_SIZE = 2048;
    private SkippingCipher skippingCipher;
    private byte[] inBuf;
    private BufferedBlockCipher bufferedBlockCipher;
    private StreamCipher streamCipher;
    private AEADBlockCipher aeadBlockCipher;
    private byte[] buf;
    private byte[] markBuf;
    private int bufOff;
    private int maxBuf;
    private boolean finalized;
    private long markPosition;
    private int markBufOff;

    public CipherInputStream(InputStream is, BufferedBlockCipher cipher) {
        this(is, cipher, 2048);
    }

    public CipherInputStream(InputStream is, StreamCipher cipher) {
        this(is, cipher, 2048);
    }

    public CipherInputStream(InputStream is, AEADBlockCipher cipher) {
        this(is, cipher, 2048);
    }

    public CipherInputStream(InputStream is, BufferedBlockCipher cipher, int bufSize) {
        super(is);
        this.bufferedBlockCipher = cipher;
        this.inBuf = new byte[bufSize];
        this.skippingCipher = cipher instanceof SkippingCipher ? (SkippingCipher)((Object)cipher) : null;
    }

    public CipherInputStream(InputStream is, StreamCipher cipher, int bufSize) {
        super(is);
        this.streamCipher = cipher;
        this.inBuf = new byte[bufSize];
        this.skippingCipher = cipher instanceof SkippingCipher ? (SkippingCipher)((Object)cipher) : null;
    }

    public CipherInputStream(InputStream is, AEADBlockCipher cipher, int bufSize) {
        super(is);
        this.aeadBlockCipher = cipher;
        this.inBuf = new byte[bufSize];
        this.skippingCipher = cipher instanceof SkippingCipher ? (SkippingCipher)((Object)cipher) : null;
    }

    private int nextChunk() throws IOException {
        if (this.finalized) {
            return -1;
        }
        this.bufOff = 0;
        this.maxBuf = 0;
        while (this.maxBuf == 0) {
            int read = this.in.read(this.inBuf);
            if (read == -1) {
                this.finaliseCipher();
                if (this.maxBuf == 0) {
                    return -1;
                }
                return this.maxBuf;
            }
            try {
                this.ensureCapacity(read, false);
                if (this.bufferedBlockCipher != null) {
                    this.maxBuf = this.bufferedBlockCipher.processBytes(this.inBuf, 0, read, this.buf, 0);
                    continue;
                }
                if (this.aeadBlockCipher != null) {
                    this.maxBuf = this.aeadBlockCipher.processBytes(this.inBuf, 0, read, this.buf, 0);
                    continue;
                }
                this.streamCipher.processBytes(this.inBuf, 0, read, this.buf, 0);
                this.maxBuf = read;
            }
            catch (Exception e) {
                throw new CipherIOException("Error processing stream ", e);
            }
        }
        return this.maxBuf;
    }

    private void finaliseCipher() throws IOException {
        try {
            this.finalized = true;
            this.ensureCapacity(0, true);
            this.maxBuf = this.bufferedBlockCipher != null ? this.bufferedBlockCipher.doFinal(this.buf, 0) : (this.aeadBlockCipher != null ? this.aeadBlockCipher.doFinal(this.buf, 0) : 0);
        }
        catch (InvalidCipherTextException e) {
            throw new InvalidCipherTextIOException("Error finalising cipher", e);
        }
        catch (Exception e) {
            throw new IOException("Error finalising cipher " + e);
        }
    }

    @Override
    public int read() throws IOException {
        if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
            return -1;
        }
        return this.buf[this.bufOff++] & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.bufOff >= this.maxBuf && this.nextChunk() < 0) {
            return -1;
        }
        int toSupply = Math.min(len, this.available());
        System.arraycopy(this.buf, this.bufOff, b, off, toSupply);
        this.bufOff += toSupply;
        return toSupply;
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        if (this.skippingCipher != null) {
            long cSkip;
            int avail = this.available();
            if (n <= (long)avail) {
                this.bufOff = (int)((long)this.bufOff + n);
                return n;
            }
            this.bufOff = this.maxBuf;
            long skip = this.in.skip(n - (long)avail);
            if (skip != (cSkip = this.skippingCipher.skip(skip))) {
                throw new IOException("Unable to skip cipher " + skip + " bytes.");
            }
            return skip + (long)avail;
        }
        int skip = (int)Math.min(n, (long)this.available());
        this.bufOff += skip;
        return skip;
    }

    @Override
    public int available() throws IOException {
        return this.maxBuf - this.bufOff;
    }

    private void ensureCapacity(int updateSize, boolean finalOutput) {
        int bufLen = updateSize;
        if (finalOutput) {
            if (this.bufferedBlockCipher != null) {
                bufLen = this.bufferedBlockCipher.getOutputSize(updateSize);
            } else if (this.aeadBlockCipher != null) {
                bufLen = this.aeadBlockCipher.getOutputSize(updateSize);
            }
        } else if (this.bufferedBlockCipher != null) {
            bufLen = this.bufferedBlockCipher.getUpdateOutputSize(updateSize);
        } else if (this.aeadBlockCipher != null) {
            bufLen = this.aeadBlockCipher.getUpdateOutputSize(updateSize);
        }
        if (this.buf == null || this.buf.length < bufLen) {
            this.buf = new byte[bufLen];
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.in.close();
        }
        finally {
            if (!this.finalized) {
                this.finaliseCipher();
            }
        }
        this.bufOff = 0;
        this.maxBuf = 0;
        this.markBufOff = 0;
        this.markPosition = 0L;
        if (this.markBuf != null) {
            Arrays.fill(this.markBuf, (byte)0);
            this.markBuf = null;
        }
        if (this.buf != null) {
            Arrays.fill(this.buf, (byte)0);
            this.buf = null;
        }
        Arrays.fill(this.inBuf, (byte)0);
    }

    @Override
    public void mark(int readlimit) {
        this.in.mark(readlimit);
        if (this.skippingCipher != null) {
            this.markPosition = this.skippingCipher.getPosition();
        }
        if (this.buf != null) {
            this.markBuf = new byte[this.buf.length];
            System.arraycopy(this.buf, 0, this.markBuf, 0, this.buf.length);
        }
        this.markBufOff = this.bufOff;
    }

    @Override
    public void reset() throws IOException {
        if (this.skippingCipher == null) {
            throw new IOException("cipher must implement SkippingCipher to be used with reset()");
        }
        this.in.reset();
        this.skippingCipher.seekTo(this.markPosition);
        if (this.markBuf != null) {
            this.buf = this.markBuf;
        }
        this.bufOff = this.markBufOff;
    }

    @Override
    public boolean markSupported() {
        if (this.skippingCipher != null) {
            return this.in.markSupported();
        }
        return false;
    }
}

