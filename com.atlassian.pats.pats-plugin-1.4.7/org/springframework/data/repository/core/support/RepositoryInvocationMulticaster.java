/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;

interface RepositoryInvocationMulticaster {
    public void notifyListeners(Method var1, Object[] var2, RepositoryMethodInvocationListener.RepositoryMethodInvocation var3);

    public static class DefaultRepositoryInvocationMulticaster
    implements RepositoryInvocationMulticaster {
        private final List<RepositoryMethodInvocationListener> methodInvocationListeners;

        DefaultRepositoryInvocationMulticaster(List<RepositoryMethodInvocationListener> methodInvocationListeners) {
            this.methodInvocationListeners = methodInvocationListeners;
        }

        @Override
        public void notifyListeners(Method method, Object[] args, RepositoryMethodInvocationListener.RepositoryMethodInvocation result) {
            for (RepositoryMethodInvocationListener methodInvocationListener : this.methodInvocationListeners) {
                methodInvocationListener.afterInvocation(result);
            }
        }
    }

    public static enum NoOpRepositoryInvocationMulticaster implements RepositoryInvocationMulticaster
    {
        INSTANCE;


        @Override
        public void notifyListeners(Method method, Object[] args, RepositoryMethodInvocationListener.RepositoryMethodInvocation result) {
        }
    }
}

