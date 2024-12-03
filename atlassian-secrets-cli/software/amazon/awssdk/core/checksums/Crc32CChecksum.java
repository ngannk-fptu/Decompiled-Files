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
import software.amazon.awssdk.core.internal.checksums.factory.SdkCrc32C;
import software.amazon.awssdk.core.internal.util.HttpChecksumUtils;

@SdkInternalApi
public class Crc32CChecksum
implements SdkChecksum {
    private Checksum crc32c = CrtBasedChecksumProvider.createCrc32C();
    private Checksum lastMarkedCrc32C;
    private final boolean isCrtBasedChecksum;

    public Crc32CChecksum() {
        boolean bl = this.isCrtBasedChecksum = this.crc32c != null;
        if (!this.isCrtBasedChecksum) {
            this.crc32c = SdkCrc32C.create();
        }
    }

    @Override
    public byte[] getChecksumBytes() {
        return Arrays.copyOfRange(HttpChecksumUtils.longToByte(this.crc32c.getValue()), 4, 8);
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
        if (this.isCrtBasedChecksum) {
            try {
                Method method = checksum.getClass().getDeclaredMethod("clone", new Class[0]);
                return (Checksum)method.invoke((Object)checksum, new Object[0]);
            }
            catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Could not clone checksum class " + checksum.getClass(), e);
            }
        }
        return (Checksum)((SdkCrc32C)checksum).clone();
    }
}

