/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.crypto;

import com.amazonaws.internal.SdkFilterInputStream;
import com.amazonaws.services.s3.internal.crypto.CipherLite;
import com.amazonaws.services.s3.internal.crypto.v1.S3CryptoScheme;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class CipherLiteInputStream
extends SdkFilterInputStream {
    private static final int MAX_RETRY = 1000;
    private static final int DEFAULT_IN_BUFFER_SIZE = 512;
    private CipherLite cipherLite;
    private final boolean multipart;
    private final boolean lastMultiPart;
    private boolean eof;
    private byte[] bufin;
    private byte[] bufout;
    private int curr_pos;
    private int max_pos;

    public CipherLiteInputStream(InputStream is, CipherLite cipherLite) {
        this(is, cipherLite, 512, false, false);
    }

    public CipherLiteInputStream(InputStream is, CipherLite c, int buffsize) {
        this(is, c, buffsize, false, false);
    }

    public CipherLiteInputStream(InputStream is, CipherLite c, int buffsize, boolean multipart, boolean lastMultiPart) {
        super(is);
        if (lastMultiPart && !multipart) {
            throw new IllegalArgumentException("lastMultiPart can only be true if multipart is true");
        }
        this.multipart = multipart;
        this.lastMultiPart = lastMultiPart;
        this.cipherLite = c;
        if (buffsize <= 0 || buffsize % 512 != 0) {
            throw new IllegalArgumentException("buffsize (" + buffsize + ") must be a positive multiple of " + 512);
        }
        this.bufin = new byte[buffsize];
    }

    public CipherLiteInputStream(InputStream is) {
        this(is, CipherLite.Null, 512, false, false);
    }

    @Override
    public int read() throws IOException {
        if (!this.readNextChunk()) {
            return -1;
        }
        return this.bufout[this.curr_pos++] & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] buf, int off, int target_len) throws IOException {
        if (!this.readNextChunk()) {
            return -1;
        }
        if (target_len <= 0) {
            return 0;
        }
        int len = this.max_pos - this.curr_pos;
        if (target_len < len) {
            len = target_len;
        }
        System.arraycopy(this.bufout, this.curr_pos, buf, off, len);
        this.curr_pos += len;
        return len;
    }

    private boolean readNextChunk() throws IOException {
        if (this.curr_pos >= this.max_pos) {
            int len;
            if (this.eof) {
                return false;
            }
            int count = 0;
            do {
                if (count > 1000) {
                    throw new IOException("exceeded maximum number of attempts to read next chunk of data");
                }
                len = this.nextChunk();
                if (this.bufout != null) continue;
                ++count;
            } while (len == 0);
            if (len == -1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long skip(long n) throws IOException {
        this.abortIfNeeded();
        int available = this.max_pos - this.curr_pos;
        if (n > (long)available) {
            n = available;
        }
        if (n < 0L) {
            return 0L;
        }
        this.curr_pos = (int)((long)this.curr_pos + n);
        return n;
    }

    @Override
    public int available() {
        this.abortIfNeeded();
        return this.max_pos - this.curr_pos;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
        if (!this.multipart && !S3CryptoScheme.isAesGcm(this.cipherLite.getCipherAlgorithm())) {
            try {
                this.cipherLite.doFinal();
            }
            catch (BadPaddingException badPaddingException) {
            }
            catch (IllegalBlockSizeException illegalBlockSizeException) {
                // empty catch block
            }
        }
        this.max_pos = 0;
        this.curr_pos = 0;
        this.abortIfNeeded();
    }

    @Override
    public boolean markSupported() {
        this.abortIfNeeded();
        return this.in.markSupported() && this.cipherLite.markSupported();
    }

    @Override
    public void mark(int readlimit) {
        this.abortIfNeeded();
        this.in.mark(readlimit);
        this.cipherLite.mark();
    }

    @Override
    public void reset() throws IOException {
        this.abortIfNeeded();
        this.in.reset();
        this.cipherLite.reset();
        this.resetInternal();
    }

    final void resetInternal() {
        this.max_pos = 0;
        this.curr_pos = 0;
        this.eof = false;
    }

    private int nextChunk() throws IOException {
        this.abortIfNeeded();
        if (this.eof) {
            return -1;
        }
        this.bufout = null;
        int len = this.in.read(this.bufin);
        if (len == -1) {
            block7: {
                this.eof = true;
                if (!this.multipart || this.lastMultiPart) {
                    try {
                        this.bufout = this.cipherLite.doFinal();
                        if (this.bufout == null) {
                            return -1;
                        }
                        this.curr_pos = 0;
                        this.max_pos = this.bufout.length;
                        return this.max_pos;
                    }
                    catch (IllegalBlockSizeException illegalBlockSizeException) {
                    }
                    catch (BadPaddingException e) {
                        if (!S3CryptoScheme.isAesGcm(this.cipherLite.getCipherAlgorithm())) break block7;
                        throw new SecurityException(e);
                    }
                }
            }
            return -1;
        }
        this.bufout = this.cipherLite.update(this.bufin, 0, len);
        this.curr_pos = 0;
        this.max_pos = this.bufout == null ? 0 : this.bufout.length;
        return this.max_pos;
    }

    void renewCipherLite() {
        this.cipherLite = this.cipherLite.recreate();
    }
}

