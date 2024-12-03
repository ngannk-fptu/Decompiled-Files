/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.utils.DaemonThreadFactory;
import software.amazon.awssdk.utils.NamedThreadFactory;

@SdkProtectedApi
public class ThreadFactoryBuilder {
    private static final int POOL_NUMBER_MAX = 10000;
    private static final AtomicLong POOL_NUMBER = new AtomicLong(0L);
    private String threadNamePrefix = "aws-java-sdk";
    private Boolean daemonThreads = true;

    public ThreadFactoryBuilder threadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        return this;
    }

    public ThreadFactoryBuilder daemonThreads(Boolean daemonThreads) {
        this.daemonThreads = daemonThreads;
        return this;
    }

    @SdkTestInternalApi
    static void resetPoolNumber() {
        POOL_NUMBER.set(0L);
    }

    public ThreadFactory build() {
        String threadNamePrefixWithPoolNumber = this.threadNamePrefix + "-" + POOL_NUMBER.getAndIncrement() % 10000L;
        ThreadFactory result = new NamedThreadFactory(Executors.defaultThreadFactory(), threadNamePrefixWithPoolNumber);
        if (this.daemonThreads.booleanValue()) {
            result = new DaemonThreadFactory(result);
        }
        return result;
    }
}

