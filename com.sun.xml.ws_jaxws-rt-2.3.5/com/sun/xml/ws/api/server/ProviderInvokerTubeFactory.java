/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.server.provider.AsyncProviderInvokerTube;
import com.sun.xml.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.xml.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.ws.server.provider.SyncProviderInvokerTube;
import com.sun.xml.ws.util.ServiceFinder;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ProviderInvokerTubeFactory<T> {
    private static final ProviderInvokerTubeFactory DEFAULT = new DefaultProviderInvokerTubeFactory();
    private static final Logger logger = Logger.getLogger(ProviderInvokerTubeFactory.class.getName());

    protected abstract ProviderInvokerTube<T> doCreate(@NotNull Class<T> var1, @NotNull Invoker var2, @NotNull ProviderArgumentsBuilder<?> var3, boolean var4);

    public static <T> ProviderInvokerTube<T> create(@Nullable ClassLoader classLoader, @NotNull Container container, @NotNull Class<T> implType, @NotNull Invoker invoker, @NotNull ProviderArgumentsBuilder<?> argsBuilder, boolean isAsync) {
        for (ProviderInvokerTubeFactory factory : ServiceFinder.find(ProviderInvokerTubeFactory.class, classLoader, container)) {
            ProviderInvokerTube<T> tube = factory.doCreate(implType, invoker, argsBuilder, isAsync);
            if (tube == null) continue;
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{factory.getClass(), tube});
            }
            return tube;
        }
        return DEFAULT.createDefault(implType, invoker, argsBuilder, isAsync);
    }

    protected ProviderInvokerTube<T> createDefault(@NotNull Class<T> implType, @NotNull Invoker invoker, @NotNull ProviderArgumentsBuilder<?> argsBuilder, boolean isAsync) {
        return isAsync ? new AsyncProviderInvokerTube(invoker, argsBuilder) : new SyncProviderInvokerTube(invoker, argsBuilder);
    }

    private static class DefaultProviderInvokerTubeFactory<T>
    extends ProviderInvokerTubeFactory<T> {
        private DefaultProviderInvokerTubeFactory() {
        }

        @Override
        public ProviderInvokerTube<T> doCreate(@NotNull Class<T> implType, @NotNull Invoker invoker, @NotNull ProviderArgumentsBuilder<?> argsBuilder, boolean isAsync) {
            return this.createDefault(implType, invoker, argsBuilder, isAsync);
        }
    }
}

