/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.function;

import java.io.IOException;
import java.util.Objects;
import org.apache.commons.io.function.Constants;

@FunctionalInterface
public interface IOTriConsumer<T, U, V> {
    public static <T, U, V> IOTriConsumer<T, U, V> noop() {
        return Constants.IO_TRI_CONSUMER;
    }

    public void accept(T var1, U var2, V var3) throws IOException;

    default public IOTriConsumer<T, U, V> andThen(IOTriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> {
            this.accept(t, u, v);
            after.accept(t, u, v);
        };
    }
}

