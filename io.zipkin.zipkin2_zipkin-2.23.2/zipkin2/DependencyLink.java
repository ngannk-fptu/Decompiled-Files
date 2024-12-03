/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.util.Locale;
import zipkin2.codec.DependencyLinkBytesDecoder;
import zipkin2.codec.DependencyLinkBytesEncoder;

public final class DependencyLink
implements Serializable {
    static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final long serialVersionUID = 0L;
    final String parent;
    final String child;
    final long callCount;
    final long errorCount;

    public static Builder newBuilder() {
        return new Builder();
    }

    public String parent() {
        return this.parent;
    }

    public String child() {
        return this.child;
    }

    public long callCount() {
        return this.callCount;
    }

    public long errorCount() {
        return this.errorCount;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public String toString() {
        return new String(DependencyLinkBytesEncoder.JSON_V1.encode(this), UTF_8);
    }

    DependencyLink(Builder builder) {
        this.parent = builder.parent;
        this.child = builder.child;
        this.callCount = builder.callCount;
        this.errorCount = builder.errorCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DependencyLink)) {
            return false;
        }
        DependencyLink that = (DependencyLink)o;
        return this.parent.equals(that.parent) && this.child.equals(that.child) && this.callCount == that.callCount && this.errorCount == that.errorCount;
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.parent.hashCode();
        h *= 1000003;
        h ^= this.child.hashCode();
        h *= 1000003;
        h ^= (int)(this.callCount >>> 32 ^ this.callCount);
        h *= 1000003;
        return h ^= (int)(this.errorCount >>> 32 ^ this.errorCount);
    }

    final Object writeReplace() throws ObjectStreamException {
        return new SerializedForm(DependencyLinkBytesEncoder.JSON_V1.encode(this));
    }

    private static final class SerializedForm
    implements Serializable {
        private static final long serialVersionUID = 0L;
        final byte[] bytes;

        SerializedForm(byte[] bytes) {
            this.bytes = bytes;
        }

        Object readResolve() throws ObjectStreamException {
            try {
                return DependencyLinkBytesDecoder.JSON_V1.decodeOne(this.bytes);
            }
            catch (IllegalArgumentException e) {
                throw new StreamCorruptedException(e.getMessage());
            }
        }
    }

    public static final class Builder {
        String parent;
        String child;
        long callCount;
        long errorCount;

        Builder() {
        }

        Builder(DependencyLink source) {
            this.parent = source.parent;
            this.child = source.child;
            this.callCount = source.callCount;
            this.errorCount = source.errorCount;
        }

        public Builder parent(String parent) {
            if (parent == null) {
                throw new NullPointerException("parent == null");
            }
            this.parent = parent.toLowerCase(Locale.ROOT);
            return this;
        }

        public Builder child(String child) {
            if (child == null) {
                throw new NullPointerException("child == null");
            }
            this.child = child.toLowerCase(Locale.ROOT);
            return this;
        }

        public Builder callCount(long callCount) {
            this.callCount = callCount;
            return this;
        }

        public Builder errorCount(long errorCount) {
            this.errorCount = errorCount;
            return this;
        }

        public DependencyLink build() {
            String missing = "";
            if (this.parent == null) {
                missing = missing + " parent";
            }
            if (this.child == null) {
                missing = missing + " child";
            }
            if (!"".equals(missing)) {
                throw new IllegalStateException("Missing :" + missing);
            }
            return new DependencyLink(this);
        }
    }
}

