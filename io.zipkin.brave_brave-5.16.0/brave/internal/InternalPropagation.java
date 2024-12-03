/*
 * Decompiled with CFR 0.152.
 */
package brave.internal;

import brave.propagation.SamplingFlags;
import brave.propagation.TraceContext;
import java.util.List;

public abstract class InternalPropagation {
    public static final int FLAG_SAMPLED = 2;
    public static final int FLAG_SAMPLED_SET = 4;
    public static final int FLAG_DEBUG = 8;
    public static final int FLAG_SHARED = 16;
    public static final int FLAG_SAMPLED_LOCAL = 32;
    public static final int FLAG_LOCAL_ROOT = 64;
    public static InternalPropagation instance;

    public abstract int flags(SamplingFlags var1);

    public static int sampled(boolean sampled, int flags) {
        if (sampled) {
            flags |= 6;
        } else {
            flags |= 4;
            flags &= 0xFFFFFFFD;
        }
        return flags;
    }

    public abstract TraceContext newTraceContext(int var1, long var2, long var4, long var6, long var8, long var10, List<Object> var12);

    public abstract TraceContext shallowCopy(TraceContext var1);

    public abstract TraceContext withExtra(TraceContext var1, List<Object> var2);

    public abstract TraceContext withFlags(TraceContext var1, int var2);
}

