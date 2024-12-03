/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import zipkin2.Annotation;
import zipkin2.Endpoint;
import zipkin2.codec.SpanBytesDecoder;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.internal.HexCodec;
import zipkin2.internal.Nullable;
import zipkin2.internal.RecyclableBuffers;

public final class Span
implements Serializable {
    static final Charset UTF_8 = Charset.forName("UTF-8");
    static final Endpoint EMPTY_ENDPOINT = Endpoint.newBuilder().build();
    static final int FLAG_DEBUG = 2;
    static final int FLAG_DEBUG_SET = 4;
    static final int FLAG_SHARED = 8;
    static final int FLAG_SHARED_SET = 16;
    private static final long serialVersionUID = 0L;
    static final String THIRTY_TWO_ZEROS;
    final String traceId;
    final String parentId;
    final String id;
    final Kind kind;
    final String name;
    final long timestamp;
    final long duration;
    final Endpoint localEndpoint;
    final Endpoint remoteEndpoint;
    final List<Annotation> annotations;
    final Map<String, String> tags;
    final int flags;

    public String traceId() {
        return this.traceId;
    }

    @Nullable
    public String parentId() {
        return this.parentId;
    }

    public String id() {
        return this.id;
    }

    @Nullable
    public Kind kind() {
        return this.kind;
    }

    @Nullable
    public String name() {
        return this.name;
    }

    @Nullable
    public Long timestamp() {
        return this.timestamp > 0L ? Long.valueOf(this.timestamp) : null;
    }

    public long timestampAsLong() {
        return this.timestamp;
    }

    @Nullable
    public Long duration() {
        return this.duration > 0L ? Long.valueOf(this.duration) : null;
    }

    public long durationAsLong() {
        return this.duration;
    }

    @Nullable
    public Endpoint localEndpoint() {
        return this.localEndpoint;
    }

    @Nullable
    public Endpoint remoteEndpoint() {
        return this.remoteEndpoint;
    }

    public List<Annotation> annotations() {
        return this.annotations;
    }

    public Map<String, String> tags() {
        return this.tags;
    }

    @Nullable
    public Boolean debug() {
        return (this.flags & 4) == 4 ? Boolean.valueOf((this.flags & 2) == 2) : null;
    }

    @Nullable
    public Boolean shared() {
        return (this.flags & 0x10) == 16 ? Boolean.valueOf((this.flags & 8) == 8) : null;
    }

    @Nullable
    public String localServiceName() {
        Endpoint localEndpoint = this.localEndpoint();
        return localEndpoint != null ? localEndpoint.serviceName() : null;
    }

    @Nullable
    public String remoteServiceName() {
        Endpoint remoteEndpoint = this.remoteEndpoint();
        return remoteEndpoint != null ? remoteEndpoint.serviceName() : null;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public String toString() {
        return new String(SpanBytesEncoder.JSON_V2.encode(this), UTF_8);
    }

    public static String normalizeTraceId(String traceId) {
        if (traceId == null) {
            throw new NullPointerException("traceId == null");
        }
        int length = traceId.length();
        if (length == 0) {
            throw new IllegalArgumentException("traceId is empty");
        }
        if (length > 32) {
            throw new IllegalArgumentException("traceId.length > 32");
        }
        int zeros = Span.validateHexAndReturnZeroPrefix(traceId);
        if (zeros == length) {
            throw new IllegalArgumentException("traceId is all zeros");
        }
        if (length == 15) {
            throw new RuntimeException("WTF");
        }
        if (length == 32 || length == 16) {
            if (length == 32 && zeros >= 16) {
                return traceId.substring(16);
            }
            return traceId;
        }
        if (length < 16) {
            return Span.padLeft(traceId, 16);
        }
        return Span.padLeft(traceId, 32);
    }

    static String padLeft(String id, int desiredLength) {
        int length = id.length();
        int numZeros = desiredLength - length;
        char[] data = RecyclableBuffers.shortStringBuffer();
        THIRTY_TWO_ZEROS.getChars(0, numZeros, data, 0);
        id.getChars(0, length, data, numZeros);
        return new String(data, 0, desiredLength);
    }

    static String toLowerHex(long v) {
        char[] data = RecyclableBuffers.shortStringBuffer();
        Span.writeHexLong(data, 0, v);
        return new String(data, 0, 16);
    }

    static void writeHexLong(char[] data, int pos, long v) {
        Span.writeHexByte(data, pos + 0, (byte)(v >>> 56 & 0xFFL));
        Span.writeHexByte(data, pos + 2, (byte)(v >>> 48 & 0xFFL));
        Span.writeHexByte(data, pos + 4, (byte)(v >>> 40 & 0xFFL));
        Span.writeHexByte(data, pos + 6, (byte)(v >>> 32 & 0xFFL));
        Span.writeHexByte(data, pos + 8, (byte)(v >>> 24 & 0xFFL));
        Span.writeHexByte(data, pos + 10, (byte)(v >>> 16 & 0xFFL));
        Span.writeHexByte(data, pos + 12, (byte)(v >>> 8 & 0xFFL));
        Span.writeHexByte(data, pos + 14, (byte)(v & 0xFFL));
    }

    static void writeHexByte(char[] data, int pos, byte b) {
        data[pos + 0] = HexCodec.HEX_DIGITS[b >> 4 & 0xF];
        data[pos + 1] = HexCodec.HEX_DIGITS[b & 0xF];
    }

    static int validateHexAndReturnZeroPrefix(String id) {
        int zeros = 0;
        boolean inZeroPrefix = id.charAt(0) == '0';
        int length = id.length();
        for (int i = 0; i < length; ++i) {
            char c = id.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'f')) {
                throw new IllegalArgumentException(id + " should be lower-hex encoded with no prefix");
            }
            if (c != '0') {
                inZeroPrefix = false;
                continue;
            }
            if (!inZeroPrefix) continue;
            ++zeros;
        }
        return zeros;
    }

    static <T extends Comparable<? super T>> List<T> sortedList(@Nullable List<T> in) {
        int i;
        if (in == null || in.isEmpty()) {
            return Collections.emptyList();
        }
        if (in.size() == 1) {
            return Collections.singletonList(in.get(0));
        }
        Object[] array = in.toArray();
        Arrays.sort(array);
        int j = 0;
        for (i = 1; i < array.length; ++i) {
            if (array[i].equals(array[j])) continue;
            array[++j] = array[i];
        }
        List<Object> result = Arrays.asList(i == j + 1 ? array : Arrays.copyOf(array, j + 1));
        return Collections.unmodifiableList(result);
    }

    Span(Builder builder) {
        this.traceId = builder.traceId;
        this.parentId = builder.id.equals(builder.parentId) ? null : builder.parentId;
        this.id = builder.id;
        this.kind = builder.kind;
        this.name = builder.name;
        this.timestamp = builder.timestamp;
        this.duration = builder.duration;
        this.localEndpoint = builder.localEndpoint;
        this.remoteEndpoint = builder.remoteEndpoint;
        this.annotations = Span.sortedList(builder.annotations);
        this.tags = builder.tags == null ? Collections.emptyMap() : new LinkedHashMap<String, String>(builder.tags);
        this.flags = builder.flags;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Span)) {
            return false;
        }
        Span that = (Span)o;
        return this.traceId.equals(that.traceId) && (this.parentId == null ? that.parentId == null : this.parentId.equals(that.parentId)) && this.id.equals(that.id) && (this.kind == null ? that.kind == null : this.kind.equals((Object)that.kind)) && (this.name == null ? that.name == null : this.name.equals(that.name)) && this.timestamp == that.timestamp && this.duration == that.duration && (this.localEndpoint == null ? that.localEndpoint == null : this.localEndpoint.equals(that.localEndpoint)) && (this.remoteEndpoint == null ? that.remoteEndpoint == null : this.remoteEndpoint.equals(that.remoteEndpoint)) && this.annotations.equals(that.annotations) && this.tags.equals(that.tags) && this.flags == that.flags;
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.traceId.hashCode();
        h *= 1000003;
        h ^= this.parentId == null ? 0 : this.parentId.hashCode();
        h *= 1000003;
        h ^= this.id.hashCode();
        h *= 1000003;
        h ^= this.kind == null ? 0 : this.kind.hashCode();
        h *= 1000003;
        h ^= this.name == null ? 0 : this.name.hashCode();
        h *= 1000003;
        h ^= (int)((long)h ^ (this.timestamp >>> 32 ^ this.timestamp));
        h *= 1000003;
        h ^= (int)((long)h ^ (this.duration >>> 32 ^ this.duration));
        h *= 1000003;
        h ^= this.localEndpoint == null ? 0 : this.localEndpoint.hashCode();
        h *= 1000003;
        h ^= this.remoteEndpoint == null ? 0 : this.remoteEndpoint.hashCode();
        h *= 1000003;
        h ^= this.annotations.hashCode();
        h *= 1000003;
        h ^= this.tags.hashCode();
        h *= 1000003;
        return h ^= this.flags;
    }

    final Object writeReplace() throws ObjectStreamException {
        return new SerializedForm(SpanBytesEncoder.PROTO3.encode(this));
    }

    static {
        char[] zeros = new char[32];
        Arrays.fill(zeros, '0');
        THIRTY_TWO_ZEROS = new String(zeros);
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
                return SpanBytesDecoder.PROTO3.decodeOne(this.bytes);
            }
            catch (IllegalArgumentException e) {
                throw new StreamCorruptedException(e.getMessage());
            }
        }
    }

    public static final class Builder {
        String traceId;
        String parentId;
        String id;
        Kind kind;
        String name;
        long timestamp;
        long duration;
        Endpoint localEndpoint;
        Endpoint remoteEndpoint;
        ArrayList<Annotation> annotations;
        TreeMap<String, String> tags;
        int flags = 0;

        public Builder clear() {
            this.traceId = null;
            this.parentId = null;
            this.id = null;
            this.kind = null;
            this.name = null;
            this.timestamp = 0L;
            this.duration = 0L;
            this.localEndpoint = null;
            this.remoteEndpoint = null;
            if (this.annotations != null) {
                this.annotations.clear();
            }
            if (this.tags != null) {
                this.tags.clear();
            }
            this.flags = 0;
            return this;
        }

        public Builder clone() {
            Builder result = new Builder();
            result.traceId = this.traceId;
            result.parentId = this.parentId;
            result.id = this.id;
            result.kind = this.kind;
            result.name = this.name;
            result.timestamp = this.timestamp;
            result.duration = this.duration;
            result.localEndpoint = this.localEndpoint;
            result.remoteEndpoint = this.remoteEndpoint;
            if (this.annotations != null) {
                result.annotations = (ArrayList)this.annotations.clone();
            }
            if (this.tags != null) {
                result.tags = (TreeMap)this.tags.clone();
            }
            result.flags = this.flags;
            return result;
        }

        Builder(Span source) {
            this.traceId = source.traceId;
            this.parentId = source.parentId;
            this.id = source.id;
            this.kind = source.kind;
            this.name = source.name;
            this.timestamp = source.timestamp;
            this.duration = source.duration;
            this.localEndpoint = source.localEndpoint;
            this.remoteEndpoint = source.remoteEndpoint;
            if (!source.annotations.isEmpty()) {
                this.annotations = new ArrayList(source.annotations.size());
                this.annotations.addAll(source.annotations);
            }
            if (!source.tags.isEmpty()) {
                this.tags = new TreeMap();
                this.tags.putAll(source.tags);
            }
            this.flags = source.flags;
        }

        public Builder merge(Span source) {
            if (this.traceId == null) {
                this.traceId = source.traceId;
            }
            if (this.id == null) {
                this.id = source.id;
            }
            if (this.parentId == null) {
                this.parentId = source.parentId;
            }
            if (this.kind == null) {
                this.kind = source.kind;
            }
            if (this.name == null) {
                this.name = source.name;
            }
            if (this.timestamp == 0L) {
                this.timestamp = source.timestamp;
            }
            if (this.duration == 0L) {
                this.duration = source.duration;
            }
            if (this.localEndpoint == null) {
                this.localEndpoint = source.localEndpoint;
            } else if (source.localEndpoint != null) {
                this.localEndpoint = this.localEndpoint.toBuilder().merge(source.localEndpoint).build();
            }
            if (this.remoteEndpoint == null) {
                this.remoteEndpoint = source.remoteEndpoint;
            } else if (source.remoteEndpoint != null) {
                this.remoteEndpoint = this.remoteEndpoint.toBuilder().merge(source.remoteEndpoint).build();
            }
            if (!source.annotations.isEmpty()) {
                if (this.annotations == null) {
                    this.annotations = new ArrayList(source.annotations.size());
                }
                this.annotations.addAll(source.annotations);
            }
            if (!source.tags.isEmpty()) {
                if (this.tags == null) {
                    this.tags = new TreeMap();
                }
                this.tags.putAll(source.tags);
            }
            this.flags |= source.flags;
            return this;
        }

        @Nullable
        public Kind kind() {
            return this.kind;
        }

        @Nullable
        public Endpoint localEndpoint() {
            return this.localEndpoint;
        }

        public Builder traceId(String traceId) {
            this.traceId = Span.normalizeTraceId(traceId);
            return this;
        }

        public Builder traceId(long high, long low) {
            if (high == 0L && low == 0L) {
                throw new IllegalArgumentException("empty trace ID");
            }
            char[] data = RecyclableBuffers.shortStringBuffer();
            int pos = 0;
            if (high != 0L) {
                Span.writeHexLong(data, pos, high);
                pos += 16;
            }
            Span.writeHexLong(data, pos, low);
            this.traceId = new String(data, 0, high != 0L ? 32 : 16);
            return this;
        }

        public Builder parentId(long parentId) {
            this.parentId = parentId != 0L ? Span.toLowerHex(parentId) : null;
            return this;
        }

        public Builder parentId(@Nullable String parentId) {
            if (parentId == null) {
                this.parentId = null;
                return this;
            }
            int length = parentId.length();
            if (length == 0) {
                throw new IllegalArgumentException("parentId is empty");
            }
            if (length > 16) {
                throw new IllegalArgumentException("parentId.length > 16");
            }
            this.parentId = Span.validateHexAndReturnZeroPrefix(parentId) == length ? null : (length < 16 ? Span.padLeft(parentId, 16) : parentId);
            return this;
        }

        public Builder id(long id) {
            if (id == 0L) {
                throw new IllegalArgumentException("empty id");
            }
            this.id = Span.toLowerHex(id);
            return this;
        }

        public Builder id(String id) {
            if (id == null) {
                throw new NullPointerException("id == null");
            }
            int length = id.length();
            if (length == 0) {
                throw new IllegalArgumentException("id is empty");
            }
            if (length > 16) {
                throw new IllegalArgumentException("id.length > 16");
            }
            if (Span.validateHexAndReturnZeroPrefix(id) == 16) {
                throw new IllegalArgumentException("id is all zeros");
            }
            this.id = length < 16 ? Span.padLeft(id, 16) : id;
            return this;
        }

        public Builder kind(@Nullable Kind kind) {
            this.kind = kind;
            return this;
        }

        public Builder name(@Nullable String name) {
            this.name = name == null || name.isEmpty() ? null : name.toLowerCase(Locale.ROOT);
            return this;
        }

        public Builder timestamp(long timestamp) {
            if (timestamp < 0L) {
                timestamp = 0L;
            }
            this.timestamp = timestamp;
            return this;
        }

        public Builder timestamp(@Nullable Long timestamp) {
            if (timestamp == null || timestamp < 0L) {
                timestamp = 0L;
            }
            this.timestamp = timestamp;
            return this;
        }

        public Builder duration(long duration) {
            if (duration < 0L) {
                duration = 0L;
            }
            this.duration = duration;
            return this;
        }

        public Builder duration(@Nullable Long duration) {
            if (duration == null || duration < 0L) {
                duration = 0L;
            }
            this.duration = duration;
            return this;
        }

        public Builder localEndpoint(@Nullable Endpoint localEndpoint) {
            if (EMPTY_ENDPOINT.equals(localEndpoint)) {
                localEndpoint = null;
            }
            this.localEndpoint = localEndpoint;
            return this;
        }

        public Builder remoteEndpoint(@Nullable Endpoint remoteEndpoint) {
            if (EMPTY_ENDPOINT.equals(remoteEndpoint)) {
                remoteEndpoint = null;
            }
            this.remoteEndpoint = remoteEndpoint;
            return this;
        }

        public Builder addAnnotation(long timestamp, String value) {
            if (this.annotations == null) {
                this.annotations = new ArrayList(2);
            }
            this.annotations.add(Annotation.create(timestamp, value));
            return this;
        }

        public Builder clearAnnotations() {
            if (this.annotations == null) {
                return this;
            }
            this.annotations.clear();
            return this;
        }

        public Builder putTag(String key, String value) {
            if (this.tags == null) {
                this.tags = new TreeMap();
            }
            if (key == null) {
                throw new NullPointerException("key == null");
            }
            if (value == null) {
                throw new NullPointerException("value of " + key + " == null");
            }
            this.tags.put(key, value);
            return this;
        }

        public Builder clearTags() {
            if (this.tags == null) {
                return this;
            }
            this.tags.clear();
            return this;
        }

        public Builder debug(boolean debug) {
            this.flags |= 4;
            this.flags = debug ? (this.flags |= 2) : (this.flags &= 0xFFFFFFFD);
            return this;
        }

        public Builder debug(@Nullable Boolean debug) {
            if (debug != null) {
                return this.debug((boolean)debug);
            }
            this.flags &= 0xFFFFFFF9;
            return this;
        }

        public Builder shared(boolean shared) {
            this.flags |= 0x10;
            this.flags = shared ? (this.flags |= 8) : (this.flags &= 0xFFFFFFF7);
            return this;
        }

        public Builder shared(@Nullable Boolean shared) {
            if (shared != null) {
                return this.shared((boolean)shared);
            }
            this.flags &= 0xFFFFFFE7;
            return this;
        }

        public Span build() {
            Logger logger;
            String missing = "";
            if (this.traceId == null) {
                missing = missing + " traceId";
            }
            if (this.id == null) {
                missing = missing + " id";
            }
            if (!"".equals(missing)) {
                throw new IllegalStateException("Missing :" + missing);
            }
            if (this.id.equals(this.parentId)) {
                logger = Logger.getLogger(Span.class.getName());
                if (logger.isLoggable(Level.FINEST)) {
                    logger.fine(String.format("undoing circular dependency: traceId=%s, spanId=%s", this.traceId, this.id));
                }
                this.parentId = null;
            }
            if ((this.flags & 8) == 8 && this.kind == Kind.CLIENT) {
                logger = Logger.getLogger(Span.class.getName());
                if (logger.isLoggable(Level.FINEST)) {
                    logger.fine(String.format("removing shared flag on client: traceId=%s, spanId=%s", this.traceId, this.id));
                }
                this.shared(null);
            }
            return new Span(this);
        }

        Builder() {
        }
    }

    public static enum Kind {
        CLIENT,
        SERVER,
        PRODUCER,
        CONSUMER;

    }
}

