/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http.timers;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@SdkInternalApi
public class TimeoutThreadPoolBuilder {
    public static ScheduledThreadPoolExecutor buildDefaultTimeoutThreadPool(String name) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5, TimeoutThreadPoolBuilder.getThreadFactory(name));
        TimeoutThreadPoolBuilder.safeSetRemoveOnCancel(executor);
        executor.setKeepAliveTime(5L, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private static ThreadFactory getThreadFactory(final String name) {
        return new ThreadFactory(){
            private int threadCount = 1;

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                if (name != null) {
                    thread.setName(name + "-" + this.threadCount++);
                }
                thread.setPriority(10);
                return thread;
            }
        };
    }

    private static void safeSetRemoveOnCancel(ScheduledThreadPoolExecutor executor) {
        try {
            executor.getClass().getMethod("setRemoveOnCancelPolicy", Boolean.TYPE).invoke((Object)executor, Boolean.TRUE);
        }
        catch (IllegalAccessException e) {
            TimeoutThreadPoolBuilder.throwSetRemoveOnCancelException(e);
        }
        catch (IllegalArgumentException e) {
            TimeoutThreadPoolBuilder.throwSetRemoveOnCancelException(e);
        }
        catch (InvocationTargetException e) {
            TimeoutThreadPoolBuilder.throwSetRemoveOnCancelException(e.getCause());
        }
        catch (NoSuchMethodException e) {
            throw new SdkClientException("The request timeout feature is only available for Java 1.7 and above.");
        }
        catch (SecurityException e) {
            throw new SdkClientException("The request timeout feature needs additional permissions to function.", e);
        }
    }

    private static void throwSetRemoveOnCancelException(Throwable cause) {
        throw new SdkClientException("Unable to setRemoveOnCancelPolicy for request timeout thread pool", cause);
    }
}

