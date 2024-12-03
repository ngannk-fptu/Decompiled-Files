/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.InternalPropagation;
import brave.internal.Nullable;
import brave.propagation.TraceContext;
import java.util.List;

public class SamplingFlags {
    public static final SamplingFlags EMPTY = new SamplingFlags(0);
    public static final SamplingFlags NOT_SAMPLED = new SamplingFlags(4);
    public static final SamplingFlags SAMPLED = new SamplingFlags(SamplingFlags.NOT_SAMPLED.flags | 2);
    public static final SamplingFlags DEBUG = new SamplingFlags(SamplingFlags.SAMPLED.flags | 8);
    final int flags;
    static final SamplingFlags EMPTY_SAMPLED_LOCAL;
    static final SamplingFlags NOT_SAMPLED_SAMPLED_LOCAL;
    static final SamplingFlags SAMPLED_SAMPLED_LOCAL;
    static final SamplingFlags DEBUG_SAMPLED_LOCAL;

    SamplingFlags(int flags) {
        this.flags = flags;
    }

    @Nullable
    public final Boolean sampled() {
        return (this.flags & 4) == 4 ? Boolean.valueOf((this.flags & 2) == 2) : null;
    }

    public final boolean sampledLocal() {
        return (this.flags & 0x20) == 32;
    }

    public final boolean debug() {
        return SamplingFlags.debug(this.flags);
    }

    public String toString() {
        return SamplingFlags.toString(this.flags);
    }

    static String toString(int flags) {
        StringBuilder result = new StringBuilder();
        if ((flags & 8) == 8) {
            result.append("DEBUG");
        } else if ((flags & 4) == 4) {
            if ((flags & 2) == 2) {
                result.append("SAMPLED_REMOTE");
            } else {
                result.append("NOT_SAMPLED_REMOTE");
            }
        }
        if ((flags & 0x20) == 32) {
            if (result.length() > 0) {
                result.append('|');
            }
            result.append("SAMPLED_LOCAL");
        }
        return result.toString();
    }

    static boolean debug(int flags) {
        return (flags & 8) == 8;
    }

    static int debug(boolean debug, int flags) {
        flags = debug ? (flags |= 0xE) : (flags &= 0xFFFFFFF7);
        return flags;
    }

    static SamplingFlags toSamplingFlags(int flags) {
        switch (flags) {
            case 0: {
                return EMPTY;
            }
            case 4: {
                return NOT_SAMPLED;
            }
            case 6: {
                return SAMPLED;
            }
            case 14: {
                return DEBUG;
            }
            case 32: {
                return EMPTY_SAMPLED_LOCAL;
            }
            case 36: {
                return NOT_SAMPLED_SAMPLED_LOCAL;
            }
            case 38: {
                return SAMPLED_SAMPLED_LOCAL;
            }
            case 46: {
                return DEBUG_SAMPLED_LOCAL;
            }
        }
        assert (false);
        return new SamplingFlags(flags);
    }

    static {
        InternalPropagation.instance = new InternalPropagation(){

            @Override
            public int flags(SamplingFlags flags) {
                return flags.flags;
            }

            @Override
            public TraceContext newTraceContext(int flags, long traceIdHigh, long traceId, long localRootId, long parentId, long spanId, List<Object> extra) {
                return new TraceContext(flags, traceIdHigh, traceId, localRootId, parentId, spanId, extra);
            }

            @Override
            public TraceContext shallowCopy(TraceContext context) {
                return context.shallowCopy();
            }

            @Override
            public TraceContext withExtra(TraceContext context, List<Object> extra) {
                return context.withExtra(extra);
            }

            @Override
            public TraceContext withFlags(TraceContext context, int flags) {
                return context.withFlags(flags);
            }
        };
        EMPTY_SAMPLED_LOCAL = new SamplingFlags(32);
        NOT_SAMPLED_SAMPLED_LOCAL = new SamplingFlags(SamplingFlags.NOT_SAMPLED.flags | 0x20);
        SAMPLED_SAMPLED_LOCAL = new SamplingFlags(SamplingFlags.SAMPLED.flags | 0x20);
        DEBUG_SAMPLED_LOCAL = new SamplingFlags(SamplingFlags.DEBUG.flags | 0x20);
    }

    @Deprecated
    public static final class Builder {
        int flags = 0;

        public Builder sampled(@Nullable Boolean sampled) {
            if (sampled == null) {
                this.flags &= 0xFFFFFFF9;
                return this;
            }
            this.flags = InternalPropagation.sampled(sampled, this.flags);
            return this;
        }

        public Builder debug(boolean debug) {
            this.flags = SamplingFlags.debug(debug, this.flags);
            return this;
        }

        public static SamplingFlags build(@Nullable Boolean sampled) {
            if (sampled != null) {
                return sampled != false ? SAMPLED : NOT_SAMPLED;
            }
            return EMPTY;
        }

        public SamplingFlags build() {
            return SamplingFlags.toSamplingFlags(this.flags);
        }
    }
}

