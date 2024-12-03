/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.InternalPropagation;
import brave.internal.Nullable;
import brave.internal.collect.Lists;
import brave.propagation.SamplingFlags;
import brave.propagation.TraceContext;
import brave.propagation.TraceIdContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TraceContextOrSamplingFlags {
    public static final TraceContextOrSamplingFlags EMPTY = new TraceContextOrSamplingFlags(3, SamplingFlags.EMPTY, Collections.<Object>emptyList());
    public static final TraceContextOrSamplingFlags NOT_SAMPLED = new TraceContextOrSamplingFlags(3, SamplingFlags.NOT_SAMPLED, Collections.<Object>emptyList());
    public static final TraceContextOrSamplingFlags SAMPLED = new TraceContextOrSamplingFlags(3, SamplingFlags.SAMPLED, Collections.<Object>emptyList());
    public static final TraceContextOrSamplingFlags DEBUG = new TraceContextOrSamplingFlags(3, SamplingFlags.DEBUG, Collections.<Object>emptyList());
    final int type;
    final SamplingFlags value;
    final List<Object> extraList;

    public static TraceContextOrSamplingFlags create(TraceContext context) {
        return new TraceContextOrSamplingFlags(1, context, Collections.<Object>emptyList());
    }

    public static TraceContextOrSamplingFlags create(TraceIdContext traceIdContext) {
        return new TraceContextOrSamplingFlags(2, traceIdContext, Collections.<Object>emptyList());
    }

    public static TraceContextOrSamplingFlags create(SamplingFlags flags) {
        if (flags == SamplingFlags.SAMPLED) {
            return SAMPLED;
        }
        if (flags == SamplingFlags.EMPTY) {
            return EMPTY;
        }
        if (flags == SamplingFlags.NOT_SAMPLED) {
            return NOT_SAMPLED;
        }
        if (flags == SamplingFlags.DEBUG) {
            return DEBUG;
        }
        return new TraceContextOrSamplingFlags(3, flags, Collections.<Object>emptyList());
    }

    public static Builder newBuilder(TraceContext context) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        return new Builder(1, context, context.extra());
    }

    public static Builder newBuilder(TraceIdContext traceIdContext) {
        if (traceIdContext == null) {
            throw new NullPointerException("traceIdContext == null");
        }
        return new Builder(2, traceIdContext, Collections.<Object>emptyList());
    }

    public static Builder newBuilder(SamplingFlags flags) {
        if (flags == null) {
            throw new NullPointerException("flags == null");
        }
        return new Builder(3, flags, Collections.<Object>emptyList());
    }

    @Deprecated
    public static Builder newBuilder() {
        return new Builder(0, null, Collections.<Object>emptyList());
    }

    @Nullable
    public Boolean sampled() {
        return this.value.sampled();
    }

    public final boolean sampledLocal() {
        return (this.value.flags & 0x20) == 32;
    }

    @Deprecated
    public TraceContextOrSamplingFlags sampled(@Nullable Boolean sampled) {
        if (sampled != null) {
            return this.sampled((boolean)sampled);
        }
        int flags = this.value.flags;
        flags &= 0xFFFFFFFB;
        if ((flags &= 0xFFFFFFFD) == this.value.flags) {
            return this;
        }
        return this.withFlags(flags);
    }

    public TraceContextOrSamplingFlags sampled(boolean sampled) {
        Boolean thisSampled = this.sampled();
        if (thisSampled != null && thisSampled.equals(sampled)) {
            return this;
        }
        int flags = InternalPropagation.sampled(sampled, this.value.flags);
        if (flags == this.value.flags) {
            return this;
        }
        return this.withFlags(flags);
    }

    @Nullable
    public TraceContext context() {
        return this.type == 1 ? (TraceContext)this.value : null;
    }

    @Nullable
    public TraceIdContext traceIdContext() {
        return this.type == 2 ? (TraceIdContext)this.value : null;
    }

    @Nullable
    public SamplingFlags samplingFlags() {
        return this.type == 3 ? this.value : null;
    }

    public final List<Object> extra() {
        return this.extraList;
    }

    public Builder toBuilder() {
        return new Builder(this.type, this.value, this.effectiveExtra());
    }

    public String toString() {
        String flagsString;
        List<Object> extra = this.effectiveExtra();
        StringBuilder result = new StringBuilder("Extracted{");
        String valueClass = this.value.getClass().getSimpleName();
        result.append(Character.toLowerCase(valueClass.charAt(0)));
        result.append(valueClass, 1, valueClass.length()).append('=').append(this.value);
        if (this.type != 3 && !(flagsString = SamplingFlags.toString(this.value.flags)).isEmpty()) {
            result.append(", samplingFlags=").append(flagsString);
        }
        if (!extra.isEmpty()) {
            result.append(", extra=").append(extra);
        }
        return result.append('}').toString();
    }

    @Deprecated
    public static TraceContextOrSamplingFlags create(@Nullable Boolean sampled, boolean debug) {
        if (debug) {
            return DEBUG;
        }
        if (sampled == null) {
            return EMPTY;
        }
        return sampled != false ? SAMPLED : NOT_SAMPLED;
    }

    TraceContextOrSamplingFlags(int type, SamplingFlags value, List<Object> extraList) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        if (extraList == null) {
            throw new NullPointerException("extra == null");
        }
        this.type = type;
        this.value = value;
        this.extraList = extraList;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TraceContextOrSamplingFlags)) {
            return false;
        }
        TraceContextOrSamplingFlags that = (TraceContextOrSamplingFlags)o;
        return this.type == that.type && this.value.equals(that.value) && this.effectiveExtra().equals(that.effectiveExtra());
    }

    List<Object> effectiveExtra() {
        return this.type == 1 ? ((TraceContext)this.value).extra() : this.extraList;
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.type;
        h *= 1000003;
        h ^= this.value.hashCode();
        h *= 1000003;
        return h ^= this.effectiveExtra().hashCode();
    }

    TraceContextOrSamplingFlags withFlags(int flags) {
        switch (this.type) {
            case 1: {
                TraceContext context = InternalPropagation.instance.withFlags((TraceContext)this.value, flags);
                return new TraceContextOrSamplingFlags(this.type, context, this.extraList);
            }
            case 2: {
                TraceIdContext traceIdContext = this.idContextWithFlags(flags);
                return new TraceContextOrSamplingFlags(this.type, traceIdContext, this.extraList);
            }
            case 3: {
                SamplingFlags samplingFlags = SamplingFlags.toSamplingFlags(flags);
                if (this.extraList.isEmpty()) {
                    return TraceContextOrSamplingFlags.create(samplingFlags);
                }
                return new TraceContextOrSamplingFlags(this.type, samplingFlags, this.extraList);
            }
        }
        throw new AssertionError((Object)"programming error");
    }

    TraceIdContext idContextWithFlags(int flags) {
        TraceIdContext traceIdContext = (TraceIdContext)this.value;
        return new TraceIdContext(flags, traceIdContext.traceIdHigh, traceIdContext.traceId);
    }

    public static final class Builder {
        int type;
        SamplingFlags value;
        List<Object> extraList;
        boolean sampledLocal = false;

        Builder(int type, SamplingFlags value, List<Object> extraList) {
            this.type = type;
            this.value = value;
            this.extraList = extraList;
        }

        @Deprecated
        public Builder context(TraceContext context) {
            return this.copyStateTo(TraceContextOrSamplingFlags.newBuilder(context));
        }

        @Deprecated
        public Builder traceIdContext(TraceIdContext traceIdContext) {
            return this.copyStateTo(TraceContextOrSamplingFlags.newBuilder(traceIdContext));
        }

        @Deprecated
        public Builder samplingFlags(SamplingFlags samplingFlags) {
            return this.copyStateTo(TraceContextOrSamplingFlags.newBuilder(samplingFlags));
        }

        Builder copyStateTo(Builder builder) {
            if (this.sampledLocal) {
                builder.sampledLocal();
            }
            for (Object extra : this.extraList) {
                builder.addExtra(extra);
            }
            return builder;
        }

        public Builder sampledLocal() {
            this.sampledLocal = true;
            return this;
        }

        @Deprecated
        public Builder extra(List<Object> extraList) {
            if (extraList == null) {
                throw new NullPointerException("extraList == null");
            }
            this.extraList = new ArrayList<Object>();
            for (Object extra : extraList) {
                this.addExtra(extra);
            }
            return this;
        }

        public Builder addExtra(Object extra) {
            this.extraList = TraceContext.ensureExtraAdded(this.extraList, extra);
            return this;
        }

        public final TraceContextOrSamplingFlags build() {
            TraceContextOrSamplingFlags result;
            if (this.value == null) {
                throw new IllegalArgumentException("Value unset. Use a non-deprecated newBuilder method instead.");
            }
            if (!this.extraList.isEmpty() && this.type == 1) {
                TraceContext context = (TraceContext)this.value;
                context = InternalPropagation.instance.withExtra(context, Lists.ensureImmutable(this.extraList));
                result = new TraceContextOrSamplingFlags(this.type, context, Collections.<Object>emptyList());
            } else {
                result = new TraceContextOrSamplingFlags(this.type, this.value, Lists.ensureImmutable(this.extraList));
            }
            if (!this.sampledLocal) {
                return result;
            }
            return result.withFlags(this.value.flags | 0x20);
        }
    }
}

