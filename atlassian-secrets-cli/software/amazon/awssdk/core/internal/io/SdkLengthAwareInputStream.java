/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.NumericUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class SdkLengthAwareInputStream
extends FilterInputStream {
    private static final Logger LOG = Logger.loggerFor(SdkLengthAwareInputStream.class);
    private long length;
    private long remaining;

    public SdkLengthAwareInputStream(InputStream in, long length) {
        super(in);
        this.remaining = this.length = Validate.isNotNegative(length, "length");
    }

    @Override
    public int read() throws IOException {
        if (!this.hasMoreBytes()) {
            LOG.debug(() -> String.format("Specified InputStream length of %d has been reached. Returning EOF.", this.length));
            return -1;
        }
        int read = super.read();
        if (read != -1) {
            --this.remaining;
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (!this.hasMoreBytes()) {
            LOG.debug(() -> String.format("Specified InputStream length of %d has been reached. Returning EOF.", this.length));
            return -1;
        }
        int read = super.read(b, off, len = Math.min(len, NumericUtils.saturatedCast(this.remaining)));
        if (read > 0) {
            this.remaining -= (long)read;
        }
        return read;
    }

    @Override
    public long skip(long requestedBytesToSkip) throws IOException {
        requestedBytesToSkip = Math.min(requestedBytesToSkip, this.remaining);
        long skippedActual = super.skip(requestedBytesToSkip);
        this.remaining -= skippedActual;
        return skippedActual;
    }

    @Override
    public int available() throws IOException {
        int streamAvailable = super.available();
        return Math.min(streamAvailable, NumericUtils.saturatedCast(this.remaining));
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
        this.length = this.remaining;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        this.remaining = this.length;
    }

    public long remaining() {
        return this.remaining;
    }

    private boolean hasMoreBytes() {
        return this.remaining > 0L;
    }
}

