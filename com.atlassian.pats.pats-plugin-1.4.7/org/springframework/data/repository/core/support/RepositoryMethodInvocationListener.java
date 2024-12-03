/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public interface RepositoryMethodInvocationListener {
    public void afterInvocation(RepositoryMethodInvocation var1);

    public static interface RepositoryMethodInvocationResult {
        public State getState();

        @Nullable
        public Throwable getError();

        public static enum State {
            SUCCESS,
            ERROR,
            CANCELED,
            RUNNING;

        }
    }

    public static class RepositoryMethodInvocation {
        private final long durationNs;
        private final Class<?> repositoryInterface;
        private final Method method;
        private final RepositoryMethodInvocationResult result;

        public RepositoryMethodInvocation(Class<?> repositoryInterface, Method method, RepositoryMethodInvocationResult result, long durationNs) {
            this.durationNs = durationNs;
            this.repositoryInterface = repositoryInterface;
            this.method = method;
            this.result = result;
        }

        public long getDuration(TimeUnit timeUnit) {
            Assert.notNull((Object)((Object)timeUnit), (String)"TimeUnit must not be null");
            return timeUnit.convert(this.durationNs, TimeUnit.NANOSECONDS);
        }

        public Class<?> getRepositoryInterface() {
            return this.repositoryInterface;
        }

        public Method getMethod() {
            return this.method;
        }

        @Nullable
        public RepositoryMethodInvocationResult getResult() {
            return this.result;
        }

        public String toString() {
            return String.format("Invocation %s.%s(%s): %s ms - %s", new Object[]{this.repositoryInterface.getSimpleName(), this.method.getName(), StringUtils.arrayToCommaDelimitedString((Object[])Arrays.stream(this.method.getParameterTypes()).map(Class::getSimpleName).toArray()), this.getDuration(TimeUnit.MILLISECONDS), this.result.getState()});
        }
    }
}

