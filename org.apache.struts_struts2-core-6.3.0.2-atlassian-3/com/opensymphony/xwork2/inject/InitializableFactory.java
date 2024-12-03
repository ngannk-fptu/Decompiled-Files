/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.Initializable;
import com.opensymphony.xwork2.inject.InternalContext;
import com.opensymphony.xwork2.inject.InternalFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class InitializableFactory<T>
implements InternalFactory<T> {
    private static final Logger LOG = LogManager.getLogger(InitializableFactory.class);
    private InternalFactory<T> internalFactory;

    private InitializableFactory(InternalFactory<T> internalFactory) {
        this.internalFactory = internalFactory;
    }

    public static <T> InternalFactory<T> wrapIfNeeded(InternalFactory<T> internalFactory) {
        if (Initializable.class.isAssignableFrom(internalFactory.type())) {
            return new InitializableFactory<T>(internalFactory);
        }
        return internalFactory;
    }

    @Override
    public T create(InternalContext context) {
        T instance = this.internalFactory.create(context);
        if (Initializable.class.isAssignableFrom(instance.getClass())) {
            ((Initializable)Initializable.class.cast(instance)).init();
        } else {
            LOG.error("Class {} is not marked as {}!", (Object)this.internalFactory.getClass().getName(), (Object)Initializable.class.getName());
        }
        return instance;
    }

    @Override
    public Class<? extends T> type() {
        return this.internalFactory.type();
    }
}

