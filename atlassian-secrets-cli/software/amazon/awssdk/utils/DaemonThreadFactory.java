/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.concurrent.ThreadFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class DaemonThreadFactory
implements ThreadFactory {
    private final ThreadFactory delegate;

    public DaemonThreadFactory(ThreadFactory delegate) {
        this.delegate = Validate.notNull(delegate, "delegate must not be null", new Object[0]);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = this.delegate.newThread(runnable);
        thread.setDaemon(true);
        return thread;
    }
}

