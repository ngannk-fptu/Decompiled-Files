/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.utils;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class ExceptionHandlingUtils {
    private ExceptionHandlingUtils() {
    }

    public static void tryCatch(Runnable executable, Consumer<Throwable> errorNotifier) {
        try {
            executable.run();
        }
        catch (Throwable throwable) {
            errorNotifier.accept(throwable);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T tryCatchFinally(Callable<T> executable, Consumer<Throwable> errorNotifier, Runnable cleanupExecutable) {
        try {
            T t = executable.call();
            return t;
        }
        catch (Throwable throwable) {
            errorNotifier.accept(throwable);
        }
        finally {
            cleanupExecutable.run();
        }
        return null;
    }
}

