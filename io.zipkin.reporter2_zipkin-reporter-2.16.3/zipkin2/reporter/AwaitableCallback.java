/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.Callback
 */
package zipkin2.reporter;

import java.util.concurrent.CountDownLatch;
import zipkin2.Callback;

public final class AwaitableCallback
implements Callback<Void> {
    final CountDownLatch countDown = new CountDownLatch(1);
    Throwable throwable;

    public void await() {
        try {
            this.countDown.await();
            Throwable result = this.throwable;
            if (result == null) {
                return;
            }
            if (result instanceof Error) {
                throw (Error)result;
            }
            if (result instanceof RuntimeException) {
                throw (RuntimeException)result;
            }
            throw new RuntimeException(result);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    public void onSuccess(Void ignored) {
        this.countDown.countDown();
    }

    public void onError(Throwable t) {
        this.throwable = t;
        this.countDown.countDown();
    }
}

