/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.internal;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;

@SdkInternalApi
public class ContentChecksum {
    private final String hash;
    private final SdkChecksum contentFlexibleChecksum;

    public ContentChecksum(String hash, SdkChecksum contentFlexibleChecksum) {
        this.hash = hash;
        this.contentFlexibleChecksum = contentFlexibleChecksum;
    }

    public String contentHash() {
        return this.hash;
    }

    public SdkChecksum contentFlexibleChecksum() {
        return this.contentFlexibleChecksum;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentChecksum that = (ContentChecksum)o;
        return Objects.equals(this.hash, that.hash) && Objects.equals(this.contentFlexibleChecksum, that.contentFlexibleChecksum);
    }

    public int hashCode() {
        int result = this.hash != null ? this.hash.hashCode() : 0;
        result = 31 * result + (this.contentFlexibleChecksum != null ? this.contentFlexibleChecksum.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ContentChecksum{hash='" + this.hash + '\'' + ", contentFlexibleChecksum=" + this.contentFlexibleChecksum + '}';
    }
}

