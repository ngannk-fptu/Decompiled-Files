/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import zipkin2.internal.Nullable;

public final class CheckResult {
    public static final CheckResult OK = new CheckResult(true, null);
    final boolean ok;
    final Throwable error;

    public static CheckResult failed(Throwable error) {
        return new CheckResult(false, error);
    }

    public boolean ok() {
        return this.ok;
    }

    @Nullable
    public Throwable error() {
        return this.error;
    }

    CheckResult(boolean ok, @Nullable Throwable error) {
        this.ok = ok;
        this.error = error;
    }

    public String toString() {
        return "CheckResult{ok=" + this.ok + ", error=" + this.error + "}";
    }
}

