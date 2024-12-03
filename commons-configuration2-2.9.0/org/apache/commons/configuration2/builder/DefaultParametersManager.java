/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.DefaultParametersHandler;

public class DefaultParametersManager {
    private final Collection<DefaultHandlerData> defaultHandlers = new CopyOnWriteArrayList<DefaultHandlerData>();

    public <T> void registerDefaultsHandler(Class<T> paramsClass, DefaultParametersHandler<? super T> handler) {
        this.registerDefaultsHandler(paramsClass, handler, null);
    }

    public <T> void registerDefaultsHandler(Class<T> paramsClass, DefaultParametersHandler<? super T> handler, Class<?> startClass) {
        if (paramsClass == null) {
            throw new IllegalArgumentException("Parameters class must not be null!");
        }
        if (handler == null) {
            throw new IllegalArgumentException("DefaultParametersHandler must not be null!");
        }
        this.defaultHandlers.add(new DefaultHandlerData(handler, paramsClass, startClass));
    }

    public void unregisterDefaultsHandler(DefaultParametersHandler<?> handler) {
        this.unregisterDefaultsHandler(handler, null);
    }

    public void unregisterDefaultsHandler(DefaultParametersHandler<?> handler, Class<?> startClass) {
        this.defaultHandlers.removeIf(dhd -> dhd.isOccurrence(handler, startClass));
    }

    public void initializeParameters(BuilderParameters params) {
        if (params != null) {
            this.defaultHandlers.forEach(dhd -> dhd.applyHandlerIfMatching(params));
        }
    }

    private static class DefaultHandlerData {
        private final DefaultParametersHandler<?> handler;
        private final Class<?> parameterClass;
        private final Class<?> startClass;

        public DefaultHandlerData(DefaultParametersHandler<?> h, Class<?> cls, Class<?> startCls) {
            this.handler = h;
            this.parameterClass = cls;
            this.startClass = startCls;
        }

        public void applyHandlerIfMatching(BuilderParameters obj) {
            if (this.parameterClass.isInstance(obj) && (this.startClass == null || this.startClass.isInstance(obj))) {
                DefaultParametersHandler<?> handlerUntyped = this.handler;
                handlerUntyped.initializeDefaults(obj);
            }
        }

        public boolean isOccurrence(DefaultParametersHandler<?> h, Class<?> startCls) {
            return h == this.handler && (startCls == null || startCls.equals(this.startClass));
        }
    }
}

