/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Objects;
import org.apache.commons.configuration2.Configuration;

class ImmutableConfigurationInvocationHandler
implements InvocationHandler {
    private final Configuration wrappedConfiguration;

    public ImmutableConfigurationInvocationHandler(Configuration configuration) {
        this.wrappedConfiguration = Objects.requireNonNull(configuration, "configuration");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return ImmutableConfigurationInvocationHandler.handleResult(method.invoke((Object)this.wrappedConfiguration, args));
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private static Object handleResult(Object result) {
        if (result instanceof Iterator) {
            return new ImmutableIterator((Iterator)result);
        }
        return result;
    }

    private static class ImmutableIterator
    implements Iterator<Object> {
        private final Iterator<?> wrappedIterator;

        public ImmutableIterator(Iterator<?> it) {
            this.wrappedIterator = it;
        }

        @Override
        public boolean hasNext() {
            return this.wrappedIterator.hasNext();
        }

        @Override
        public Object next() {
            return this.wrappedIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove() operation not supported!");
        }
    }
}

