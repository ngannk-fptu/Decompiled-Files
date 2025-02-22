/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.function;

import java.io.IOException;
import org.apache.commons.io.function.IOBiConsumer;
import org.apache.commons.io.function.IOBiFunction;
import org.apache.commons.io.function.IOComparator;
import org.apache.commons.io.function.IOConsumer;
import org.apache.commons.io.function.IOFunction;
import org.apache.commons.io.function.IOPredicate;
import org.apache.commons.io.function.IORunnable;
import org.apache.commons.io.function.IOSupplier;

final class Erase {
    static <T, U> void accept(IOBiConsumer<T, U> consumer, T t, U u) {
        try {
            consumer.accept(t, u);
        }
        catch (IOException ex) {
            Erase.rethrow(ex);
        }
    }

    static <T> void accept(IOConsumer<T> consumer, T t) {
        try {
            consumer.accept(t);
        }
        catch (IOException ex) {
            Erase.rethrow(ex);
        }
    }

    static <T, U, R> R apply(IOBiFunction<? super T, ? super U, ? extends R> mapper, T t, U u) {
        try {
            return mapper.apply(t, u);
        }
        catch (IOException e) {
            throw Erase.rethrow(e);
        }
    }

    static <T, R> R apply(IOFunction<? super T, ? extends R> mapper, T t) {
        try {
            return mapper.apply(t);
        }
        catch (IOException e) {
            throw Erase.rethrow(e);
        }
    }

    static <T> int compare(IOComparator<? super T> comparator, T t, T u) {
        try {
            return comparator.compare(t, u);
        }
        catch (IOException e) {
            throw Erase.rethrow(e);
        }
    }

    static <T> T get(IOSupplier<T> supplier) {
        try {
            return supplier.get();
        }
        catch (IOException e) {
            throw Erase.rethrow(e);
        }
    }

    static <T extends Throwable> RuntimeException rethrow(Throwable throwable) throws T {
        throw throwable;
    }

    static void run(IORunnable runnable) {
        try {
            runnable.run();
        }
        catch (IOException e) {
            throw Erase.rethrow(e);
        }
    }

    static <T> boolean test(IOPredicate<? super T> predicate, T t) {
        try {
            return predicate.test(t);
        }
        catch (IOException e) {
            throw Erase.rethrow(e);
        }
    }

    private Erase() {
    }
}

