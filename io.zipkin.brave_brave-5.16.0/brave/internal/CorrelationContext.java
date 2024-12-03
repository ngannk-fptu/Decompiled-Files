/*
 * Decompiled with CFR 0.152.
 */
package brave.internal;

import brave.internal.Nullable;

public interface CorrelationContext {
    @Nullable
    public String getValue(String var1);

    public boolean update(String var1, @Nullable String var2);
}

