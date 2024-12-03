/*
 * Decompiled with CFR 0.152.
 */
package brave.handler;

import brave.Span;
import brave.Tags;
import brave.handler.MutableSpanBytesEncoder;
import brave.internal.Nullable;
import brave.internal.RecyclableBuffers;
import brave.internal.codec.IpLiteral;
import brave.internal.codec.JsonWriter;
import brave.internal.collect.UnsafeArrayMap;
import brave.propagation.TraceContext;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public final class MutableSpan
implements Cloneable {
    static final Object[] EMPTY_ARRAY = new Object[0];
    static final MutableSpan EMPTY = new MutableSpan();
    String traceId;
    String localRootId;
    String parentId;
    String id;
    Span.Kind kind;
    int flags;
    long startTimestamp;
    long finishTimestamp;
    String name;
    String localServiceName;
    String localIp;
    String remoteServiceName;
    String remoteIp;
    int localPort;
    int remotePort;
    Throwable error;
    Object[] tags = EMPTY_ARRAY;
    Object[] annotations = EMPTY_ARRAY;
    int tagCount;
    int annotationCount;
    static final MutableSpanBytesEncoder.ZipkinJsonV2 JSON_ENCODER = new MutableSpanBytesEncoder.ZipkinJsonV2(Tags.ERROR);
    static final String THIRTY_TWO_ZEROS;

    public MutableSpan() {
    }

    public MutableSpan(TraceContext context, @Nullable MutableSpan defaults) {
        this(defaults != null ? defaults : EMPTY);
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        this.traceId = context.traceIdString();
        this.localRootId = context.localRootIdString();
        this.parentId = context.parentIdString();
        this.id = context.spanIdString();
        this.flags = 0;
        if (context.debug()) {
            this.setDebug();
        }
        if (context.shared()) {
            this.setShared();
        }
    }

    public MutableSpan(MutableSpan toCopy) {
        if (toCopy == null) {
            throw new NullPointerException("toCopy == null");
        }
        if (toCopy.equals(EMPTY)) {
            return;
        }
        this.traceId = toCopy.traceId;
        this.localRootId = toCopy.localRootId;
        this.parentId = toCopy.parentId;
        this.id = toCopy.id;
        this.kind = toCopy.kind;
        this.flags = toCopy.flags;
        this.startTimestamp = toCopy.startTimestamp;
        this.finishTimestamp = toCopy.finishTimestamp;
        this.name = toCopy.name;
        this.localServiceName = toCopy.localServiceName;
        this.localIp = toCopy.localIp;
        this.localPort = toCopy.localPort;
        this.remoteServiceName = toCopy.remoteServiceName;
        this.remoteIp = toCopy.remoteIp;
        this.remotePort = toCopy.remotePort;
        this.tags = MutableSpan.copy(toCopy.tags);
        this.tagCount = toCopy.tagCount;
        this.annotations = MutableSpan.copy(toCopy.annotations);
        this.annotationCount = toCopy.annotationCount;
        this.error = toCopy.error;
    }

    @Deprecated
    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    public String traceId() {
        return this.traceId;
    }

    public void traceId(String traceId) {
        this.traceId = MutableSpan.normalizeIdField("traceId", traceId, false);
    }

    @Nullable
    public String localRootId() {
        return this.localRootId;
    }

    public void localRootId(String localRootId) {
        this.localRootId = MutableSpan.normalizeIdField("localRootId", localRootId, false);
    }

    @Nullable
    public String parentId() {
        return this.parentId;
    }

    public void parentId(@Nullable String parentId) {
        this.parentId = MutableSpan.normalizeIdField("parentId", parentId, true);
    }

    public String id() {
        return this.id;
    }

    public void id(String id) {
        this.id = MutableSpan.normalizeIdField("id", id, false);
    }

    @Nullable
    public String name() {
        return this.name;
    }

    public void name(@Nullable String name) {
        this.name = name == null || name.isEmpty() ? null : name;
    }

    public long startTimestamp() {
        return this.startTimestamp;
    }

    public void startTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long finishTimestamp() {
        return this.finishTimestamp;
    }

    public void finishTimestamp(long finishTimestamp) {
        this.finishTimestamp = finishTimestamp;
    }

    public Span.Kind kind() {
        return this.kind;
    }

    public void kind(@Nullable Span.Kind kind) {
        this.kind = kind;
    }

    @Nullable
    public String localServiceName() {
        return this.localServiceName;
    }

    public void localServiceName(@Nullable String localServiceName) {
        if (localServiceName == null || localServiceName.isEmpty()) {
            this.localServiceName = null;
        }
        this.localServiceName = localServiceName;
    }

    @Nullable
    public String localIp() {
        return this.localIp;
    }

    public boolean localIp(@Nullable String localIp) {
        this.localIp = IpLiteral.ipOrNull(localIp);
        return localIp != null;
    }

    public int localPort() {
        return this.localPort;
    }

    public void localPort(int localPort) {
        if (localPort > 65535) {
            throw new IllegalArgumentException("invalid port " + localPort);
        }
        if (localPort < 0) {
            localPort = 0;
        }
        this.localPort = localPort;
    }

    @Nullable
    public String remoteServiceName() {
        return this.remoteServiceName;
    }

    public void remoteServiceName(@Nullable String remoteServiceName) {
        if (remoteServiceName == null || remoteServiceName.isEmpty()) {
            this.remoteServiceName = null;
        }
        this.remoteServiceName = remoteServiceName;
    }

    @Nullable
    public String remoteIp() {
        return this.remoteIp;
    }

    public void remoteIp(@Nullable String remoteIp) {
        this.remoteIp = IpLiteral.ipOrNull(remoteIp);
    }

    public int remotePort() {
        return this.remotePort;
    }

    public void remotePort(int remotePort) {
        if (remotePort > 65535) {
            throw new IllegalArgumentException("invalid port " + remotePort);
        }
        if (remotePort < 0) {
            remotePort = 0;
        }
        this.remotePort = remotePort;
    }

    public boolean remoteIpAndPort(@Nullable String remoteIp, int remotePort) {
        if (remoteIp == null) {
            return false;
        }
        this.remoteIp = IpLiteral.ipOrNull(remoteIp);
        if (this.remoteIp == null) {
            return false;
        }
        this.remotePort(remotePort);
        return true;
    }

    public Throwable error() {
        return this.error;
    }

    public void error(@Nullable Throwable error) {
        this.error = error;
    }

    public boolean debug() {
        return (this.flags & 8) == 8;
    }

    public void setDebug() {
        this.flags |= 8;
    }

    public void unsetDebug() {
        this.flags &= 0xFFFFFFF7;
    }

    public boolean shared() {
        return (this.flags & 0x10) == 16;
    }

    public void setShared() {
        this.flags |= 0x10;
    }

    public void unsetShared() {
        this.flags &= 0xFFFFFFEF;
    }

    public int annotationCount() {
        return this.annotationCount;
    }

    public long annotationTimestampAt(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("i < 0");
        }
        if (i >= this.annotationCount) {
            throw new IndexOutOfBoundsException("i >= annotationCount");
        }
        return (Long)this.annotations[i * 2];
    }

    public String annotationValueAt(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("i < 0");
        }
        if (i >= this.annotationCount) {
            throw new IndexOutOfBoundsException("i >= annotationCount");
        }
        return (String)this.annotations[i * 2 + 1];
    }

    public Collection<Map.Entry<Long, String>> annotations() {
        return UnsafeArrayMap.newBuilder().build(this.annotations).entrySet();
    }

    public <T> void forEachAnnotation(AnnotationConsumer<T> annotationConsumer, T target) {
        int length = this.annotationCount * 2;
        for (int i = 0; i < length; i += 2) {
            long timestamp = (Long)this.annotations[i];
            annotationConsumer.accept(target, timestamp, this.annotations[i + 1].toString());
        }
    }

    public void forEachAnnotation(AnnotationUpdater annotationUpdater) {
        int length = this.annotationCount * 2;
        for (int i = 0; i < length; i += 2) {
            String value = this.annotations[i + 1].toString();
            String newValue = annotationUpdater.update((Long)this.annotations[i], value);
            if (newValue != null) {
                MutableSpan.update(this.annotations, i, newValue);
                continue;
            }
            MutableSpan.remove(this.annotations, i);
            length -= 2;
            --this.annotationCount;
            i -= 2;
        }
    }

    public boolean containsAnnotation(String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        int length = this.annotationCount * 2;
        for (int i = 0; i < length; i += 2) {
            if (!value.equals(this.annotations[i + 1])) continue;
            return true;
        }
        return false;
    }

    public void annotate(long timestamp, String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        if (timestamp == 0L) {
            return;
        }
        this.annotations = MutableSpan.add(this.annotations, this.annotationCount * 2, timestamp, value);
        ++this.annotationCount;
    }

    public int tagCount() {
        return this.tagCount;
    }

    public String tagKeyAt(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("i < 0");
        }
        if (i >= this.tagCount) {
            throw new IndexOutOfBoundsException("i >= tagCount");
        }
        return (String)this.tags[i * 2];
    }

    public String tagValueAt(int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException("i < 0");
        }
        if (i >= this.tagCount) {
            throw new IndexOutOfBoundsException("i >= tagCount");
        }
        return (String)this.tags[i * 2 + 1];
    }

    public Map<String, String> tags() {
        return UnsafeArrayMap.newBuilder().build(this.tags);
    }

    @Nullable
    public String tag(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key is empty");
        }
        int length = this.tagCount * 2;
        for (int i = 0; i < length; i += 2) {
            if (!key.equals(this.tags[i])) continue;
            return (String)this.tags[i + 1];
        }
        return null;
    }

    @Nullable
    public String removeTag(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key is empty");
        }
        int length = this.tagCount * 2;
        for (int i = 0; i < length; i += 2) {
            if (!key.equals(this.tags[i])) continue;
            String value = (String)this.tags[i + 1];
            MutableSpan.remove(this.tags, i);
            --this.tagCount;
            return value;
        }
        return null;
    }

    public <T> void forEachTag(TagConsumer<T> tagConsumer, T target) {
        int length = this.tagCount * 2;
        for (int i = 0; i < length; i += 2) {
            tagConsumer.accept(target, (String)this.tags[i], (String)this.tags[i + 1]);
        }
    }

    public void forEachTag(TagUpdater tagUpdater) {
        int length = this.tagCount * 2;
        for (int i = 0; i < length; i += 2) {
            String value = (String)this.tags[i + 1];
            String newValue = tagUpdater.update((String)this.tags[i], value);
            if (newValue != null) {
                MutableSpan.update(this.tags, i, newValue);
                continue;
            }
            MutableSpan.remove(this.tags, i);
            length -= 2;
            --this.tagCount;
            i -= 2;
        }
    }

    public void tag(String key, String value) {
        int i;
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key is empty");
        }
        if (value == null) {
            throw new NullPointerException("value of " + key + " == null");
        }
        int length = this.tagCount * 2;
        for (i = 0; i < length; i += 2) {
            if (!key.equals(this.tags[i])) continue;
            MutableSpan.update(this.tags, i, value);
            return;
        }
        this.tags = MutableSpan.add(this.tags, i, key, value);
        ++this.tagCount;
    }

    public String toString() {
        return new String(JSON_ENCODER.encode(this), JsonWriter.UTF_8);
    }

    public int hashCode() {
        int h = 1000003;
        h ^= this.traceId == null ? 0 : this.traceId.hashCode();
        h *= 1000003;
        h ^= this.localRootId == null ? 0 : this.localRootId.hashCode();
        h *= 1000003;
        h ^= this.parentId == null ? 0 : this.parentId.hashCode();
        h *= 1000003;
        h ^= this.id == null ? 0 : this.id.hashCode();
        h *= 1000003;
        h ^= this.kind == null ? 0 : this.kind.hashCode();
        h *= 1000003;
        h ^= this.flags;
        h *= 1000003;
        h ^= (int)(this.startTimestamp >>> 32 ^ this.startTimestamp);
        h *= 1000003;
        h ^= (int)(this.finishTimestamp >>> 32 ^ this.finishTimestamp);
        h *= 1000003;
        h ^= this.name == null ? 0 : this.name.hashCode();
        h *= 1000003;
        h ^= this.localServiceName == null ? 0 : this.localServiceName.hashCode();
        h *= 1000003;
        h ^= this.localIp == null ? 0 : this.localIp.hashCode();
        h *= 1000003;
        h ^= this.localPort;
        h *= 1000003;
        h ^= this.remoteServiceName == null ? 0 : this.remoteServiceName.hashCode();
        h *= 1000003;
        h ^= this.remoteIp == null ? 0 : this.remoteIp.hashCode();
        h *= 1000003;
        h ^= this.remotePort;
        h *= 1000003;
        h ^= MutableSpan.entriesHashCode(this.tags, this.tagCount);
        h *= 1000003;
        h ^= MutableSpan.entriesHashCode(this.annotations, this.annotationCount);
        h *= 1000003;
        return h ^= this.error == null ? 0 : this.error.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof WeakReference) {
            o = ((WeakReference)o).get();
        }
        if (!(o instanceof MutableSpan)) {
            return false;
        }
        MutableSpan that = (MutableSpan)o;
        return MutableSpan.equal(this.traceId, that.traceId) && MutableSpan.equal(this.localRootId, that.localRootId) && MutableSpan.equal(this.parentId, that.parentId) && MutableSpan.equal(this.id, that.id) && this.kind == that.kind && this.flags == that.flags && this.startTimestamp == that.startTimestamp && this.finishTimestamp == that.finishTimestamp && MutableSpan.equal(this.name, that.name) && MutableSpan.equal(this.localServiceName, that.localServiceName) && MutableSpan.equal(this.localIp, that.localIp) && this.localPort == that.localPort && MutableSpan.equal(this.remoteServiceName, that.remoteServiceName) && MutableSpan.equal(this.remoteIp, that.remoteIp) && this.remotePort == that.remotePort && MutableSpan.entriesEqual(this.tags, this.tagCount, that.tags, that.tagCount) && MutableSpan.entriesEqual(this.annotations, this.annotationCount, that.annotations, that.annotationCount) && MutableSpan.equal(this.error, that.error);
    }

    static Object[] add(Object[] input, int i, Object key, Object value) {
        Object[] result = i == input.length ? Arrays.copyOf(input, i + 2) : input;
        result[i] = key;
        result[i + 1] = value;
        return result;
    }

    static void update(Object[] input, int i, Object value) {
        if (value.equals(input[i + 1])) {
            return;
        }
        input[i + 1] = value;
    }

    static void remove(Object[] input, int i) {
        for (int j = i + 2; j < input.length && input[j] != null; j += 2) {
            input[i] = input[j];
            input[i + 1] = input[j + 1];
            i += 2;
        }
        input[i + 1] = null;
        input[i] = null;
    }

    static Object[] copy(Object[] input) {
        return input.length > 0 ? Arrays.copyOf(input, input.length) : EMPTY_ARRAY;
    }

    static boolean entriesEqual(Object[] left, int leftCount, Object[] right, int rightCount) {
        if (leftCount != rightCount) {
            return false;
        }
        for (int i = 0; i < leftCount * 2; ++i) {
            if (MutableSpan.equal(left[i], right[i])) continue;
            return false;
        }
        return true;
    }

    static int entriesHashCode(Object[] entries, int count) {
        int h = 1000003;
        for (int i = 0; i < count * 2; ++i) {
            h ^= entries[i] == null ? 0 : entries[i].hashCode();
            h *= 1000003;
        }
        return h;
    }

    @Nullable
    static String normalizeIdField(String field, @Nullable String id, boolean isNullable) {
        if (id == null) {
            if (isNullable) {
                return null;
            }
            throw new NullPointerException(field + " == null");
        }
        int length = id.length();
        if (length == 0) {
            if (isNullable) {
                return null;
            }
            throw new IllegalArgumentException(field + " is empty");
        }
        int desiredLength = field.equals("traceId") && length > 16 ? 32 : 16;
        int existingPadding = MutableSpan.validateHexAndReturnPadding(field, id, desiredLength);
        if (desiredLength == 32 && existingPadding >= 16) {
            return id.substring(16);
        }
        return length == desiredLength ? id : MutableSpan.padLeft(id, desiredLength, existingPadding);
    }

    static int validateHexAndReturnPadding(String field, String value, int desiredLength) {
        int length = value.length();
        int zeroPrefix = 0;
        if (length > desiredLength) {
            throw new IllegalArgumentException(field + ".length > " + desiredLength);
        }
        boolean inZeroPrefix = value.charAt(0) == '0';
        for (int i = 0; i < length; ++i) {
            char c = value.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'f')) {
                throw new IllegalArgumentException(field + " should be lower-hex encoded with no prefix");
            }
            if (c != '0') {
                inZeroPrefix = false;
                continue;
            }
            if (!inZeroPrefix) continue;
            ++zeroPrefix;
        }
        if (zeroPrefix == length) {
            throw new IllegalArgumentException(field + " is all zeros");
        }
        return zeroPrefix;
    }

    static String padLeft(String id, int desiredLength, int existingPadding) {
        int length = id.length();
        int remainingPadding = desiredLength < length ? 0 : desiredLength - length - existingPadding;
        char[] data = RecyclableBuffers.parseBuffer();
        THIRTY_TWO_ZEROS.getChars(0, desiredLength, data, 0);
        id.getChars(existingPadding, length - existingPadding, data, remainingPadding);
        return new String(data, 0, desiredLength);
    }

    static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == null ? b == null : a.equals(b);
    }

    static {
        char[] zeros = new char[32];
        Arrays.fill(zeros, '0');
        THIRTY_TWO_ZEROS = new String(zeros);
    }

    public static interface AnnotationUpdater {
        @Nullable
        public String update(long var1, String var3);
    }

    public static interface TagUpdater {
        @Nullable
        public String update(String var1, String var2);
    }

    public static interface AnnotationConsumer<T> {
        public void accept(T var1, long var2, String var4);
    }

    public static interface TagConsumer<T> {
        public void accept(T var1, String var2, String var3);
    }
}

