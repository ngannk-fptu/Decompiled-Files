/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.InternalPropagation;
import brave.internal.Nullable;
import brave.internal.RecyclableBuffers;
import brave.internal.codec.HexCodec;
import brave.propagation.SamplingFlags;

public final class TraceIdContext
extends SamplingFlags {
    volatile String traceIdString;
    final long traceIdHigh;
    final long traceId;

    public static Builder newBuilder() {
        return new Builder();
    }

    public long traceIdHigh() {
        return this.traceIdHigh;
    }

    public long traceId() {
        return this.traceId;
    }

    public String traceIdString() {
        String r = this.traceIdString;
        if (r == null) {
            this.traceIdString = r = TraceIdContext.toTraceIdString(this.traceIdHigh, this.traceId);
        }
        return r;
    }

    static String toTraceIdString(long traceIdHigh, long traceId) {
        if (traceIdHigh != 0L) {
            char[] result = RecyclableBuffers.parseBuffer();
            HexCodec.writeHexLong(result, 0, traceIdHigh);
            HexCodec.writeHexLong(result, 16, traceId);
            return new String(result, 0, 32);
        }
        return HexCodec.toLowerHex(traceId);
    }

    public Builder toBuilder() {
        Builder result = new Builder();
        result.flags = this.flags;
        result.traceIdHigh = this.traceIdHigh;
        result.traceId = this.traceId;
        return result;
    }

    @Override
    public String toString() {
        boolean traceHi = this.traceIdHigh != 0L;
        char[] result = new char[traceHi ? 32 : 16];
        int pos = 0;
        if (traceHi) {
            HexCodec.writeHexLong(result, pos, this.traceIdHigh);
            pos += 16;
        }
        HexCodec.writeHexLong(result, pos, this.traceId);
        return new String(result);
    }

    TraceIdContext(int flags, long traceIdHigh, long traceId) {
        super(flags);
        this.traceIdHigh = traceIdHigh;
        this.traceId = traceId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TraceIdContext)) {
            return false;
        }
        TraceIdContext that = (TraceIdContext)o;
        return this.traceIdHigh == that.traceIdHigh && this.traceId == that.traceId;
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (int)(this.traceIdHigh >>> 32 ^ this.traceIdHigh);
        h *= 1000003;
        return h ^= (int)(this.traceId >>> 32 ^ this.traceId);
    }

    public static final class Builder {
        long traceIdHigh;
        long traceId;
        int flags;

        public Builder traceIdHigh(long traceIdHigh) {
            this.traceIdHigh = traceIdHigh;
            return this;
        }

        public Builder traceId(long traceId) {
            this.traceId = traceId;
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

        public final TraceIdContext build() {
            if (this.traceId == 0L) {
                throw new IllegalStateException("Missing: traceId");
            }
            return new TraceIdContext(this.flags, this.traceIdHigh, this.traceId);
        }

        Builder() {
        }
    }
}

