/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.crt.checksums.CRC32
 *  software.amazon.awssdk.utils.ClassLoaderHelper
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.checksums;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.Checksum;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.checksums.CRC32;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkChecksum;
import software.amazon.awssdk.http.auth.aws.internal.signer.checksums.SdkCrc32Checksum;
import software.amazon.awssdk.utils.ClassLoaderHelper;

@SdkInternalApi
public class Crc32Checksum
implements SdkChecksum {
    private static final String CRT_CLASSPATH_FOR_CRC32 = "software.amazon.awssdk.crt.checksums.CRC32";
    private Checksum crc32 = Crc32Checksum.isCrtAvailable() ? new CRC32() : SdkCrc32Checksum.create();
    private Checksum lastMarkedCrc32;

    private static boolean isCrtAvailable() {
        try {
            ClassLoaderHelper.loadClass((String)CRT_CLASSPATH_FOR_CRC32, (boolean)false, (Class[])new Class[0]);
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
        return Arrays.copyOfRange(Crc32Checksum.longToByte(this.crc32.getValue()), 4, 8);
    }

    @Override
    public void mark(int readLimit) {
        this.lastMarkedCrc32 = this.cloneChecksum(this.crc32);
    }

    @Override
    public void update(int b) {
        this.crc32.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        this.crc32.update(b, off, len);
    }

    @Override
    public long getValue() {
        return this.crc32.getValue();
    }

    @Override
    public void reset() {
        if (this.lastMarkedCrc32 == null) {
            this.crc32.reset();
        } else {
            this.crc32 = this.cloneChecksum(this.lastMarkedCrc32);
        }
    }

    private Checksum cloneChecksum(Checksum checksum) {
        if (checksum instanceof CRC32) {
            return (Checksum)((CRC32)checksum).clone();
        }
        if (checksum instanceof SdkCrc32Checksum) {
            return ((SdkCrc32Checksum)checksum).clone();
        }
        throw new IllegalStateException("Unsupported checksum");
    }
}

