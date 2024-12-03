/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;

public final class Annotation
implements Comparable<Annotation>,
Serializable {
    private static final long serialVersionUID = 0L;
    final long timestamp;
    final String value;

    public static Annotation create(long timestamp, String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        return new Annotation(timestamp, value);
    }

    public long timestamp() {
        return this.timestamp;
    }

    public String value() {
        return this.value;
    }

    @Override
    public int compareTo(Annotation that) {
        int byTimestamp;
        if (this == that) {
            return 0;
        }
        int n = this.timestamp() < that.timestamp() ? -1 : (byTimestamp = this.timestamp() == that.timestamp() ? 0 : 1);
        if (byTimestamp != 0) {
            return byTimestamp;
        }
        return this.value().compareTo(that.value());
    }

    Annotation(long timestamp, String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public String toString() {
        return "Annotation{timestamp=" + this.timestamp + ", value=" + this.value + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Annotation)) {
            return false;
        }
        Annotation that = (Annotation)o;
        return this.timestamp == that.timestamp() && this.value.equals(that.value());
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (int)(this.timestamp >>> 32 ^ this.timestamp);
        h *= 1000003;
        return h ^= this.value.hashCode();
    }

    final Object writeReplace() throws ObjectStreamException {
        return new SerializedForm(this);
    }

    private static final class SerializedForm
    implements Serializable {
        static final long serialVersionUID = 0L;
        final long timestamp;
        final String value;

        SerializedForm(Annotation annotation) {
            this.timestamp = annotation.timestamp;
            this.value = annotation.value;
        }

        Object readResolve() throws ObjectStreamException {
            try {
                return Annotation.create(this.timestamp, this.value);
            }
            catch (IllegalArgumentException e) {
                throw new StreamCorruptedException(e.getMessage());
            }
        }
    }
}

