/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Arrays;

public class JournalingSecureRandom
extends SecureRandom {
    private static byte[] EMPTY_TRANSCRIPT = new byte[0];
    private final SecureRandom base;
    private TranscriptStream tOut = new TranscriptStream();
    private byte[] transcript;
    private int index = 0;

    public JournalingSecureRandom() {
        this(CryptoServicesRegistrar.getSecureRandom());
    }

    public JournalingSecureRandom(SecureRandom random) {
        this.base = random;
        this.transcript = EMPTY_TRANSCRIPT;
    }

    public JournalingSecureRandom(byte[] transcript, SecureRandom random) {
        this.base = random;
        this.transcript = Arrays.clone(transcript);
    }

    @Override
    public final void nextBytes(byte[] bytes) {
        if (this.index >= this.transcript.length) {
            this.base.nextBytes(bytes);
        } else {
            int i;
            for (i = 0; i != bytes.length && this.index < this.transcript.length; ++i) {
                bytes[i] = this.transcript[this.index++];
            }
            if (i != bytes.length) {
                byte[] extra = new byte[bytes.length - i];
                this.base.nextBytes(extra);
                System.arraycopy(extra, 0, bytes, i, extra.length);
            }
        }
        try {
            this.tOut.write(bytes);
        }
        catch (IOException e) {
            throw new IllegalStateException("unable to record transcript: " + e.getMessage());
        }
    }

    public void clear() {
        Arrays.fill(this.transcript, (byte)0);
        this.tOut.clear();
    }

    public void reset() {
        this.index = 0;
        if (this.index == this.transcript.length) {
            this.transcript = this.tOut.toByteArray();
        }
        this.tOut.reset();
    }

    public byte[] getTranscript() {
        return this.tOut.toByteArray();
    }

    public byte[] getFullTranscript() {
        if (this.index == this.transcript.length) {
            return this.tOut.toByteArray();
        }
        return Arrays.clone(this.transcript);
    }

    private static class TranscriptStream
    extends ByteArrayOutputStream {
        private TranscriptStream() {
        }

        public void clear() {
            Arrays.fill(this.buf, (byte)0);
        }
    }
}

