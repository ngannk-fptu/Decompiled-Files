/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.util.promise;

import java.lang.reflect.InvocationTargetException;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Success;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ProviderType
public interface Promise<T> {
    public boolean isDone();

    public T getValue() throws InvocationTargetException, InterruptedException;

    public Throwable getFailure() throws InterruptedException;

    public Promise<T> onResolve(Runnable var1);

    public <R> Promise<R> then(Success<? super T, ? extends R> var1, Failure var2);

    public <R> Promise<R> then(Success<? super T, ? extends R> var1);

    public Promise<T> filter(Predicate<? super T> var1);

    public <R> Promise<R> map(Function<? super T, ? extends R> var1);

    public <R> Promise<R> flatMap(Function<? super T, Promise<? extends R>> var1);

    public Promise<T> recover(Function<Promise<?>, ? extends T> var1);

    public Promise<T> recoverWith(Function<Promise<?>, Promise<? extends T>> var1);

    public Promise<T> fallbackTo(Promise<? extends T> var1);
}

