/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.crt.checksums.CRC32C
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.checksums;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.Checksum;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.checksums.CRC32C;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkCrc32CChecksum;
import software.amazon.awssdk.utils.ClassLoaderHelper;

@SdkInternalApi
public class Crc32CChecksum
implements SdkChecksum {
    private static final String CRT_CLASSPATH_FOR_CRC32C = "software.amazon.awssdk.crt.checksums.CRC32C";
    private Checksum crc32c = Crc32CChecksum.isCrtAvailable() ? new CRC32C() : SdkCrc32CChecksum.create();
    private Checksum lastMarkedCrc32C;

    private static boolean isCrtAvailable() {
        try {
            ClassLoaderHelper.loadClass(CRT_CLASSPATH_FOR_CRC32C, false, new Class[0]);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    private static byte[] longToByte(Long input) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(input);
        return buffer.array();
    }

    @Override
    public byte[] getChecksumBytes() {
        return Arrays.copyOfRange(Crc32CChecksum.longToByte(this.crc32c.getValue()), 4, 8);
    }

    @Override
    public void mark(int readLimit) {
        this.lastMarkedCrc32C = this.cloneChecksum(this.crc32c);
    }

    @Override
    public void update(int b) {
        this.crc32c.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        this.crc32c.update(b, off, len);
    }

    @Override
    public long getValue() {
        return this.crc32c.getValue();
    }

    @Override
    public void reset() {
        if (this.lastMarkedCrc32C == null) {
            this.crc32c.reset();
        } else {
            this.crc32c = this.cloneChecksum(this.lastMarkedCrc32C);
        }
    }

    private Checksum cloneChecksum(Checksum checksum) {
        if (checksum instanceof CRC32C) {
            return (Checksum)((CRC32C)checksum).clone();
        }
        if (checksum instanceof SdkCrc32CChecksum) {
            return ((SdkCrc32CChecksum)checksum).clone();
        }
        throw new IllegalStateException("Unsupported checksum");
    }
}

