/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.webhook;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyedExecutor<K> {
    private static final Logger logger = LoggerFactory.getLogger(KeyedExecutor.class);
    private final Executor delegateExecutor;
    private final Set<K> keysInQueue = Collections.newSetFromMap(new ConcurrentHashMap());

    public KeyedExecutor(Executor delegateExecutor) {
        this.delegateExecutor = (Executor)Preconditions.checkNotNull((Object)delegateExecutor);
    }

    public void execute(final Runnable runnable, final K key) {
        if (this.keysInQueue.add(key)) {
            this.delegateExecutor.execute(new Runnable(){

                @Override
                public void run() {
                    if (KeyedExecutor.this.keysInQueue.remove(key)) {
                        runnable.run();
                    } else {
                        logger.debug("Not running runnable {} because it was removed from the queue", (Object)runnable);
                    }
                }
            });
        } else {
            logger.debug("Discarding runnable {} because its key is already in the queue", (Object)runnable);
        }
    }
}

