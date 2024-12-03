/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggerFactory;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class LoggerFactorySupport
implements LoggerFactory {
    final ConcurrentMap<String, ILogger> mapLoggers = new ConcurrentHashMap<String, ILogger>(100);
    final ConstructorFunction<String, ILogger> loggerConstructor = new ConstructorFunction<String, ILogger>(){

        @Override
        public ILogger createNew(String key) {
            return LoggerFactorySupport.this.createLogger(key);
        }
    };

    @Override
    public final ILogger getLogger(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.mapLoggers, name, this.loggerConstructor);
    }

    protected abstract ILogger createLogger(String var1);

    public void clearLoadedLoggers() {
        this.mapLoggers.clear();
    }
}

