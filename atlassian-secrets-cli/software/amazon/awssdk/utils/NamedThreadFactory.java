/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class NamedThreadFactory
implements ThreadFactory {
    private final ThreadFactory delegate;
    private final String namePrefix;
    private final AtomicInteger threadCount = new AtomicInteger(0);

    public NamedThreadFactory(ThreadFactory delegate, String namePrefix) {
        this.delegate = Validate.notNull(delegate, "delegate must not be null", new Object[0]);
        this.namePrefix = Validate.notBlank(namePrefix, "namePrefix must not be blank", new Object[0]);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = this.delegate.newThread(runnable);
        thread.setName(this.namePrefix + "-" + this.threadCount.getAndIncrement());
        return thread;
    }
}

