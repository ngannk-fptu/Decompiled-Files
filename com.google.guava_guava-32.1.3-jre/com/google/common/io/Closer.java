/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import com.google.common.io.ElementTypesAreNonnullByDefault;
import com.google.common.io.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class Closer
implements Closeable {
    private static final Suppressor SUPPRESSOR;
    @VisibleForTesting
    final Suppressor suppressor;
    private final Deque<Closeable> stack = new ArrayDeque<Closeable>(4);
    @CheckForNull
    private Throwable thrown;

    public static Closer create() {
        return new Closer(SUPPRESSOR);
    }

    @VisibleForTesting
    Closer(Suppressor suppressor) {
        this.suppressor = Preconditions.checkNotNull(suppressor);
    }

    @ParametricNullness
    @CanIgnoreReturnValue
    public <C extends Closeable> C register(@ParametricNullness C closeable) {
        if (closeable != null) {
            this.stack.addFirst(closeable);
        }
        return closeable;
    }

    public RuntimeException rethrow(Throwable e) throws IOException {
        Preconditions.checkNotNull(e);
        this.thrown = e;
        Throwables.propagateIfPossible(e, IOException.class);
        throw new RuntimeException(e);
    }

    public <X extends Exception> RuntimeException rethrow(Throwable e, Class<X> declaredType) throws IOException, X {
        Preconditions.checkNotNull(e);
        this.thrown = e;
        Throwables.propagateIfPossible(e, IOException.class);
        Throwables.propagateIfPossible(e, declaredType);
        throw new RuntimeException(e);
    }

    public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable e, Class<X1> declaredType1, Class<X2> declaredType2) throws IOException, X1, X2 {
        Preconditions.checkNotNull(e);
        this.thrown = e;
        Throwables.propagateIfPossible(e, IOException.class);
        Throwables.propagateIfPossible(e, declaredType1, declaredType2);
        throw new RuntimeException(e);
    }

    @Override
    public void close() throws IOException {
        Throwable throwable = this.thrown;
        while (!this.stack.isEmpty()) {
            Closeable closeable = this.stack.removeFirst();
            try {
                closeable.close();
            }
            catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                    continue;
                }
                this.suppressor.suppress(closeable, throwable, e);
            }
        }
        if (this.thrown == null && throwable != null) {
            Throwables.propagateIfPossible(throwable, IOException.class);
            throw new AssertionError((Object)throwable);
        }
    }

    static {
        SuppressingSuppressor suppressingSuppressor = SuppressingSuppressor.tryCreate();
        SUPPRESSOR = suppressingSuppressor == null ? LoggingSuppressor.INSTANCE : suppressingSuppressor;
    }

    @VisibleForTesting
    static final class SuppressingSuppressor
    implements Suppressor {
        private final Method addSuppressed;

        @CheckForNull
        static SuppressingSuppressor tryCreate() {
            Method addSuppressed;
            try {
                addSuppressed = Throwable.class.getMethod("addSuppressed", Throwable.class);
            }
            catch (Throwable e) {
                return null;
            }
            return new SuppressingSuppressor(addSuppressed);
        }

        private SuppressingSuppressor(Method addSuppressed) {
            this.addSuppressed = addSuppressed;
        }

        @Override
        public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed) {
            if (thrown == suppressed) {
                return;
            }
            try {
                this.addSuppressed.invoke((Object)thrown, suppressed);
            }
            catch (Throwable e) {
                LoggingSuppressor.INSTANCE.suppress(closeable, thrown, suppressed);
            }
        }
    }

    @VisibleForTesting
    static final class LoggingSuppressor
    implements Suppressor {
        static final LoggingSuppressor INSTANCE = new LoggingSuppressor();

        LoggingSuppressor() {
        }

        @Override
        public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed) {
            Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + closeable, suppressed);
        }
    }

    @VisibleForTesting
    static interface Suppressor {
        public void suppress(Closeable var1, Throwable var2, Throwable var3);
    }
}

