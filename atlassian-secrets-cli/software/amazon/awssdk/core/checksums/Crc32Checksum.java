/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.checksums;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.zip.Checksum;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.internal.checksums.factory.CrtBasedChecksumProvider;
import software.amazon.awssdk.core.internal.checksums.factory.SdkCrc32;
import software.amazon.awssdk.core.internal.util.HttpChecksumUtils;

@SdkInternalApi
public class Crc32Checksum
implements SdkChecksum {
    private Checksum crc32 = CrtBasedChecksumProvider.createCrc32();
    private Checksum lastMarkedCrc32;
    private final boolean isCrtBasedChecksum;

    public Crc32Checksum() {
        boolean bl = this.isCrtBasedChecksum = this.crc32 != null;
        if (!this.isCrtBasedChecksum) {
            this.crc32 = SdkCrc32.create();
        }
    }

    @Override
    public byte[] getChecksumBytes() {
        return Arrays.copyOfRange(HttpChecksumUtils.longToByte(this.crc32.getValue()), 4, 8);
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
        if (this.isCrtBasedChecksum) {
            try {
                Method method = checksum.getClass().getDeclaredMethod("clone", new Class[0]);
                return (Checksum)method.invoke((Object)checksum, new Object[0]);
            }
            catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Could not clone checksum class " + checksum.getClass(), e);
            }
        }
        return (Checksum)((SdkCrc32)checksum).clone();
    }
}

