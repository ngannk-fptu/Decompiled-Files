/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.io.IOException;

@FunctionalInterface
public interface LeaseStrategy {
    public boolean shouldDrop(Throwable var1);

    public static LeaseStrategy dropOnError() {
        return error -> true;
    }

    public static LeaseStrategy retainOnError() {
        return error -> false;
    }

    public static LeaseStrategy retainOnIoError() {
        return error -> {
            Throwable inspect = error;
            do {
                if (!(inspect instanceof IOException)) continue;
                return false;
            } while ((inspect = inspect.getCause()) != null);
            return true;
        };
    }
}

