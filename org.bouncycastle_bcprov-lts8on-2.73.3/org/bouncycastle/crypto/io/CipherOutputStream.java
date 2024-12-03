/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherIOException;
import org.bouncycastle.crypto.io.InvalidCipherTextIOException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;

public class CipherOutputStream
extends FilterOutputStream {
    private BufferedBlockCipher bufferedBlockCipher;
    private StreamCipher streamCipher;
    private AEADBlockCipher aeadBlockCipher;
    private final byte[] oneByte = new byte[1];
    private byte[] buf;

    public CipherOutputStream(OutputStream os, BufferedBlockCipher cipher) {
        super(os);
        this.bufferedBlockCipher = cipher;
    }

    public CipherOutputStream(OutputStream os, StreamCipher cipher) {
        super(os);
        this.streamCipher = cipher;
    }

    public CipherOutputStream(OutputStream os, AEADBlockCipher cipher) {
        super(os);
        this.aeadBlockCipher = cipher;
    }

    @Override
    public void write(int b) throws IOException {
        this.oneByte[0] = (byte)b;
        if (this.streamCipher != null) {
            this.out.write(this.streamCipher.returnByte((byte)b));
        } else {
            this.write(this.oneByte, 0, 1);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.ensureCapacity(len, false);
        if (this.bufferedBlockCipher != null) {
            int outLen = this.bufferedBlockCipher.processBytes(b, off, len, this.buf, 0);
            if (outLen != 0) {
                this.out.write(this.buf, 0, outLen);
            }
        } else if (this.aeadBlockCipher != null) {
            int outLen = this.aeadBlockCipher.processBytes(b, off, len, this.buf, 0);
            if (outLen != 0) {
                this.out.write(this.buf, 0, outLen);
            }
        } else {
            this.streamCipher.processBytes(b, off, len, this.buf, 0);
            this.out.write(this.buf, 0, len);
        }
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
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        IOException error;
        block13: {
            this.ensureCapacity(0, true);
            error = null;
            try {
                int outLen;
                if (this.bufferedBlockCipher != null) {
                    outLen = this.bufferedBlockCipher.doFinal(this.buf, 0);
                    if (outLen != 0) {
                        this.out.write(this.buf, 0, outLen);
                    }
                } else if (this.aeadBlockCipher != null) {
                    outLen = this.aeadBlockCipher.doFinal(this.buf, 0);
                    if (outLen != 0) {
                        this.out.write(this.buf, 0, outLen);
                    }
                } else if (this.streamCipher != null) {
                    this.streamCipher.reset();
                }
            }
            catch (InvalidCipherTextException e) {
                error = new InvalidCipherTextIOException("Error finalising cipher data", e);
            }
            catch (Exception e) {
                error = new CipherIOException("Error closing stream: ", e);
            }
            try {
                this.flush();
                this.out.close();
            }
            catch (IOException e) {
                if (error != null) break block13;
                error = e;
            }
        }
        if (error != null) {
            throw error;
        }
    }
}

