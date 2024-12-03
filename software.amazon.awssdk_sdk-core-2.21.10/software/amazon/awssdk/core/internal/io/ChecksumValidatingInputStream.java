/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.Abortable
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.io;

import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class ChecksumValidatingInputStream
extends InputStream
implements Abortable {
    private final SdkChecksum checkSum;
    private final InputStream inputStream;
    private final String expectedChecksum;
    private String computedChecksum = null;
    private boolean endOfStream = false;

    public ChecksumValidatingInputStream(InputStream inputStream, SdkChecksum sdkChecksum, String expectedChecksum) {
        this.inputStream = inputStream;
        this.checkSum = sdkChecksum;
        this.expectedChecksum = expectedChecksum;
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
                this.validateAndThrow();
            }
        }
        return read;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        Validate.notNull((Object)buf, (String)"buff", (Object[])new Object[0]);
        int read = -1;
        if (!this.endOfStream) {
            read = this.inputStream.read(buf, off, len);
            if (read != -1) {
                this.checkSum.update(buf, off, read);
            }
            if (read == -1) {
                this.endOfStream = true;
                this.validateAndThrow();
            }
        }
        return read;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.inputStream.reset();
        this.checkSum.reset();
    }

    public void abort() {
        if (this.inputStream instanceof Abortable) {
            ((Abortable)this.inputStream).abort();
        }
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    private void validateAndThrow() {
        if (this.computedChecksum == null) {
            this.computedChecksum = BinaryUtils.toBase64((byte[])this.checkSum.getChecksumBytes());
        }
        if (!this.expectedChecksum.equals(this.computedChecksum)) {
            throw SdkClientException.builder().message(String.format("Data read has a different checksum than expected. Was %s, but expected %s", this.computedChecksum, this.expectedChecksum)).build();
        }
    }
}

