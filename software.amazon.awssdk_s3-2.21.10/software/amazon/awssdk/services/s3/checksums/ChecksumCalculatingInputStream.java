/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 */
package software.amazon.awssdk.services.s3.checksums;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;

@SdkInternalApi
public class ChecksumCalculatingInputStream
extends FilterInputStream {
    private final SdkChecksum checkSum;
    private final InputStream inputStream;
    private boolean endOfStream = false;

    public ChecksumCalculatingInputStream(InputStream in, SdkChecksum cksum) {
        super(in);
        this.inputStream = in;
        this.checkSum = cksum;
    }

    @Override
    public int read() throws IOException {
        int read = -1;
        if (!this.endOfStream) {
            read = this.inputStream.read();
            if (read != -1) {
                this.checkSum.update(read);
            }
            if (read == -1) {
                this.endOfStream = true;
            }
        }
        return read;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (buf == null) {
            throw new NullPointerException();
        }
        int read = -1;
        if (!this.endOfStream) {
            read = this.inputStream.read(buf, off, len);
            if (read != -1) {
                this.checkSum.update(buf, off, read);
            }
            if (read == -1) {
                this.endOfStream = true;
            }
        }
        return read;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.inputStream.reset();
        this.checkSum.reset();
        this.endOfStream = false;
    }

    public byte[] getChecksumBytes() {
        return this.checkSum.getChecksumBytes();
    }
}

