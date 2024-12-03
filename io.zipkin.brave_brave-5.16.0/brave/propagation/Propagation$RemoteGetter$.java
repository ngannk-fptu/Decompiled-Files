/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.Nullable;
import brave.propagation.Propagation;

public class Propagation$RemoteGetter$<R>
implements Propagation.Getter<R, String> {
    @Nullable
    public static /* bridge */ /* synthetic */ String get(Propagation.RemoteGetter this_, Object object, Object object2) {
        return this_.get(object, (String)object2);
    }
}

