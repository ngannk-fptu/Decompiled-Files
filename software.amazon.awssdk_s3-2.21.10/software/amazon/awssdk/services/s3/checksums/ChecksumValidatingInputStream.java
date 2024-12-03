/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.checksums.SdkChecksum
 *  software.amazon.awssdk.core.exception.RetryableException
 *  software.amazon.awssdk.http.Abortable
 *  software.amazon.awssdk.utils.BinaryUtils
 */
package software.amazon.awssdk.services.s3.checksums;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.RetryableException;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
public class ChecksumValidatingInputStream
extends InputStream
implements Abortable {
    private static final int CHECKSUM_SIZE = 16;
    private final SdkChecksum checkSum;
    private final InputStream inputStream;
    private long strippedLength;
    private byte[] streamChecksum = new byte[16];
    private long lengthRead = 0L;
    private byte[] computedChecksum;

    public ChecksumValidatingInputStream(InputStream in, SdkChecksum cksum, long streamLength) {
        this.inputStream = in;
        this.checkSum = cksum;
        this.strippedLength = streamLength - 16L;
    }

    @Override
    public int read() throws IOException {
        int read = this.inputStream.read();
        if (read != -1 && this.lengthRead < this.strippedLength) {
            this.checkSum.update(read);
        }
        if (read != -1) {
            ++this.lengthRead;
        }
        if (read != -1 && this.lengthRead == this.strippedLength) {
            int byteRead = -1;
            byteRead = this.inputStream.read();
            while (byteRead != -1 && this.lengthRead < this.strippedLength + 16L) {
                int index = Math.min((int)(this.lengthRead - this.strippedLength), 15);
                this.streamChecksum[index] = (byte)byteRead;
                ++this.lengthRead;
                byteRead = this.inputStream.read();
            }
        }
        if (read == -1) {
            this.validateAndThrow();
        }
        return read;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (buf == null) {
            throw new NullPointerException();
        }
        int read = -1;
        if (this.lengthRead < this.strippedLength) {
            long maxRead = Math.min(Integer.MAX_VALUE, this.strippedLength - this.lengthRead);
            int maxIterRead = (int)Math.min(maxRead, (long)len);
            read = this.inputStream.read(buf, off, maxIterRead);
            int toUpdate = (int)Math.min(this.strippedLength - this.lengthRead, (long)read);
            if (toUpdate > 0) {
                this.checkSum.update(buf, off, toUpdate);
            }
            this.lengthRead += read >= 0 ? (long)read : 0L;
        }
        if (this.lengthRead >= this.strippedLength) {
            int byteRead = 0;
            while ((byteRead = this.inputStream.read()) != -1) {
                int index = Math.min((int)(this.lengthRead - this.strippedLength), 15);
                this.streamChecksum[index] = (byte)byteRead;
                ++this.lengthRead;
            }
            if (read == -1) {
                this.validateAndThrow();
            }
        }
        return read;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.inputStream.reset();
        this.checkSum.reset();
        this.lengthRead = 0L;
        for (int i = 0; i < 16; ++i) {
            this.streamChecksum[i] = 0;
        }
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
            this.computedChecksum = this.checkSum.getChecksumBytes();
        }
        if (!Arrays.equals(this.computedChecksum, this.streamChecksum)) {
            throw RetryableException.create((String)String.format("Data read has a different checksum than expected. Was 0x%s, but expected 0x%s. This commonly means that the data was corrupted between the client and service.", BinaryUtils.toHex((byte[])this.computedChecksum), BinaryUtils.toHex((byte[])this.streamChecksum)));
        }
    }
}

