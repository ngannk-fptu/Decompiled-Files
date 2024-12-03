/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.InternalPropagation;
import brave.internal.Nullable;
import brave.internal.Platform;
import brave.internal.codec.HexCodec;
import brave.internal.collect.Lists;
import brave.propagation.Propagation;
import brave.propagation.SamplingFlags;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.propagation.TraceIdContext;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public final class TraceContext
extends SamplingFlags {
    volatile String traceIdString;
    volatile String parentIdString;
    volatile String localRootIdString;
    volatile String spanIdString;
    final long traceIdHigh;
    final long traceId;
    final long localRootId;
    final long parentId;
    final long spanId;
    final List<Object> extraList;
    volatile int hashCode;

    public static Builder newBuilder() {
        return new Builder();
    }

    public long traceIdHigh() {
        return this.traceIdHigh;
    }

    public long traceId() {
        return this.traceId;
    }

    public long localRootId() {
        return this.localRootId;
    }

    public boolean isLocalRoot() {
        return (this.flags & 0x40) == 64;
    }

    @Nullable
    public final Long parentId() {
        return this.parentId != 0L ? Long.valueOf(this.parentId) : null;
    }

    public long parentIdAsLong() {
        return this.parentId;
    }

    public long spanId() {
        return this.spanId;
    }

    public boolean shared() {
        return (this.flags & 0x10) == 16;
    }

    public List<Object> extra() {
        return this.extraList;
    }

    @Nullable
    public <T> T findExtra(Class<T> type) {
        return TraceContext.findExtra(type, this.extraList);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public String traceIdString() {
        String r = this.traceIdString;
        if (r == null) {
            this.traceIdString = r = TraceIdContext.toTraceIdString(this.traceIdHigh, this.traceId);
        }
        return r;
    }

    @Nullable
    public String parentIdString() {
        String r = this.parentIdString;
        if (r == null && this.parentId != 0L) {
            r = this.parentIdString = HexCodec.toLowerHex(this.parentId);
        }
        return r;
    }

    @Nullable
    public String localRootIdString() {
        String r = this.localRootIdString;
        if (r == null && this.localRootId != 0L) {
            r = this.localRootIdString = HexCodec.toLowerHex(this.localRootId);
        }
        return r;
    }

    public String spanIdString() {
        String r = this.spanIdString;
        if (r == null) {
            r = this.spanIdString = HexCodec.toLowerHex(this.spanId);
        }
        return r;
    }

    @Override
    public String toString() {
        boolean traceHi = this.traceIdHigh != 0L;
        char[] result = new char[(traceHi ? 3 : 2) * 16 + 1];
        int pos = 0;
        if (traceHi) {
            HexCodec.writeHexLong(result, pos, this.traceIdHigh);
            pos += 16;
        }
        HexCodec.writeHexLong(result, pos, this.traceId);
        pos += 16;
        result[pos++] = 47;
        HexCodec.writeHexLong(result, pos, this.spanId);
        return new String(result);
    }

    TraceContext shallowCopy() {
        return new TraceContext(this.flags, this.traceIdHigh, this.traceId, this.localRootId, this.parentId, this.spanId, this.extraList);
    }

    TraceContext withExtra(List<Object> extra) {
        return new TraceContext(this.flags, this.traceIdHigh, this.traceId, this.localRootId, this.parentId, this.spanId, extra);
    }

    TraceContext withFlags(int flags) {
        return new TraceContext(flags, this.traceIdHigh, this.traceId, this.localRootId, this.parentId, this.spanId, this.extraList);
    }

    TraceContext(int flags, long traceIdHigh, long traceId, long localRootId, long parentId, long spanId, List<Object> extraList) {
        super(flags);
        this.traceIdHigh = traceIdHigh;
        this.traceId = traceId;
        this.localRootId = localRootId;
        this.parentId = parentId;
        this.spanId = spanId;
        this.extraList = extraList;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof WeakReference) {
            o = ((WeakReference)o).get();
        }
        if (!(o instanceof TraceContext)) {
            return false;
        }
        TraceContext that = (TraceContext)o;
        return this.traceIdHigh == that.traceIdHigh && this.traceId == that.traceId && this.spanId == that.spanId && (this.flags & 0x10) == (that.flags & 0x10);
    }

    public int hashCode() {
        int h = this.hashCode;
        if (h == 0) {
            h = 1000003;
            h ^= (int)(this.traceIdHigh >>> 32 ^ this.traceIdHigh);
            h *= 1000003;
            h ^= (int)(this.traceId >>> 32 ^ this.traceId);
            h *= 1000003;
            h ^= (int)(this.spanId >>> 32 ^ this.spanId);
            h *= 1000003;
            this.hashCode = h ^= this.flags & 0x10;
        }
        return h;
    }

    static List<Object> ensureExtraAdded(List<Object> extraList, Object extra) {
        if (extra == null) {
            throw new NullPointerException("extra == null");
        }
        int length = extraList.size();
        for (int i = 0; i < length; ++i) {
            if (extra != extraList.get(i)) continue;
            return extraList;
        }
        extraList = Lists.ensureMutable(extraList);
        extraList.add(extra);
        return extraList;
    }

    static <T> T findExtra(Class<T> type, List<Object> extra) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        int length = extra.size();
        for (int i = 0; i < length; ++i) {
            Object nextExtra = extra.get(i);
            if (nextExtra.getClass() != type) continue;
            return (T)nextExtra;
        }
        return null;
    }

    public static final class Builder {
        long traceIdHigh;
        long traceId;
        long parentId;
        long spanId;
        long localRootId;
        int flags;
        List<Object> extraList = Collections.emptyList();

        Builder(TraceContext context) {
            this.traceIdHigh = context.traceIdHigh;
            this.traceId = context.traceId;
            this.localRootId = context.localRootId;
            this.parentId = context.parentId;
            this.spanId = context.spanId;
            this.flags = context.flags;
            this.extraList = context.extraList;
        }

        public Builder traceIdHigh(long traceIdHigh) {
            this.traceIdHigh = traceIdHigh;
            return this;
        }

        public Builder traceId(long traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder parentId(long parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder parentId(@Nullable Long parentId) {
            if (parentId == null) {
                parentId = 0L;
            }
            this.parentId = parentId;
            return this;
        }

        public Builder spanId(long spanId) {
            this.spanId = spanId;
            return this;
        }

        public Builder sampledLocal(boolean sampledLocal) {
            this.flags = sampledLocal ? (this.flags |= 0x20) : (this.flags &= 0xFFFFFFDF);
            return this;
        }

        public Builder sampled(boolean sampled) {
            this.flags = InternalPropagation.sampled(sampled, this.flags);
            return this;
        }

        public Builder sampled(@Nullable Boolean sampled) {
            if (sampled == null) {
                this.flags &= 0xFFFFFFF9;
                return this;
            }
            return this.sampled((boolean)sampled);
        }

        public Builder debug(boolean debug) {
            this.flags = SamplingFlags.debug(debug, this.flags);
            return this;
        }

        public Builder shared(boolean shared) {
            this.flags = shared ? (this.flags |= 0x10) : (this.flags &= 0xFFFFFFEF);
            return this;
        }

        @Deprecated
        public Builder extra(List<Object> extraList) {
            if (extraList == null) {
                throw new NullPointerException("extraList == null");
            }
            for (Object extra : extraList) {
                this.addExtra(extra);
            }
            return this;
        }

        public Builder clearExtra() {
            this.extraList = Collections.emptyList();
            return this;
        }

        public Builder addExtra(Object extra) {
            this.extraList = TraceContext.ensureExtraAdded(this.extraList, extra);
            return this;
        }

        boolean parseTraceId(String traceIdString, Object key) {
            if (Builder.isNull(key, traceIdString)) {
                return false;
            }
            int length = traceIdString.length();
            if (Builder.invalidIdLength(key, length, 32)) {
                return false;
            }
            boolean traceIdHighAllZeros = false;
            boolean traceIdAllZeros = false;
            int traceIdIndex = Math.max(0, length - 16);
            if (traceIdIndex > 0) {
                this.traceIdHigh = HexCodec.lenientLowerHexToUnsignedLong(traceIdString, 0, traceIdIndex);
                if (this.traceIdHigh == 0L && !(traceIdHighAllZeros = Builder.isAllZeros(traceIdString, 0, traceIdIndex))) {
                    Builder.maybeLogNotLowerHex(traceIdString);
                    return false;
                }
            } else {
                traceIdHighAllZeros = true;
            }
            this.traceId = HexCodec.lenientLowerHexToUnsignedLong(traceIdString, traceIdIndex, length);
            if (this.traceId == 0L && !(traceIdAllZeros = Builder.isAllZeros(traceIdString, traceIdIndex, length))) {
                Builder.maybeLogNotLowerHex(traceIdString);
                return false;
            }
            if (traceIdHighAllZeros && traceIdAllZeros) {
                Platform.get().log("Invalid input: traceId was all zeros", null);
            }
            return this.traceIdHigh != 0L || this.traceId != 0L;
        }

        <R, K> boolean parseParentId(Propagation.Getter<R, K> getter, R request, K key) {
            String parentIdString = getter.get(request, key);
            if (parentIdString == null) {
                return true;
            }
            int length = parentIdString.length();
            if (Builder.invalidIdLength(key, length, 16)) {
                return false;
            }
            this.parentId = HexCodec.lenientLowerHexToUnsignedLong(parentIdString, 0, length);
            if (this.parentId != 0L) {
                return true;
            }
            Builder.maybeLogNotLowerHex(parentIdString);
            return false;
        }

        <R, K> boolean parseSpanId(Propagation.Getter<R, K> getter, R request, K key) {
            String spanIdString = getter.get(request, key);
            if (Builder.isNull(key, spanIdString)) {
                return false;
            }
            int length = spanIdString.length();
            if (Builder.invalidIdLength(key, length, 16)) {
                return false;
            }
            this.spanId = HexCodec.lenientLowerHexToUnsignedLong(spanIdString, 0, length);
            if (this.spanId == 0L) {
                if (Builder.isAllZeros(spanIdString, 0, length)) {
                    Platform.get().log("Invalid input: spanId was all zeros", null);
                    return false;
                }
                Builder.maybeLogNotLowerHex(spanIdString);
                return false;
            }
            return true;
        }

        static boolean invalidIdLength(Object key, int length, int max) {
            if (length > 1 && length <= max) {
                return false;
            }
            assert (max == 32 || max == 16);
            Platform.get().log(max == 32 ? "{0} should be a 1 to 32 character lower-hex string with no prefix" : "{0} should be a 1 to 16 character lower-hex string with no prefix", key, null);
            return true;
        }

        static boolean isNull(Object key, String maybeNull) {
            if (maybeNull != null) {
                return false;
            }
            Platform.get().log("{0} was null", key, null);
            return true;
        }

        static boolean isAllZeros(String value, int beginIndex, int endIndex) {
            for (int i = beginIndex; i < endIndex; ++i) {
                if (value.charAt(i) == '0') continue;
                return false;
            }
            return true;
        }

        static void maybeLogNotLowerHex(String notLowerHex) {
            Platform.get().log("{0} is not a lower-hex string", notLowerHex, null);
        }

        public TraceContext build() {
            String missing = "";
            if (this.traceIdHigh == 0L && this.traceId == 0L) {
                missing = missing + " traceId";
            }
            if (this.spanId == 0L) {
                missing = missing + " spanId";
            }
            if (!"".equals(missing)) {
                throw new IllegalArgumentException("Missing:" + missing);
            }
            return new TraceContext(this.flags, this.traceIdHigh, this.traceId, this.localRootId, this.parentId, this.spanId, Lists.ensureImmutable(this.extraList));
        }

        Builder() {
        }
    }

    public static interface Extractor<R> {
        public TraceContextOrSamplingFlags extract(R var1);
    }

    public static interface Injector<R> {
        public void inject(TraceContext var1, R var2);
    }
}

