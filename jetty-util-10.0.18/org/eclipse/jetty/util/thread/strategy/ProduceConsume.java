/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.thread.strategy;

import java.util.concurrent.Executor;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProduceConsume
implements ExecutionStrategy,
Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ProduceConsume.class);
    private final AutoLock _lock = new AutoLock();
    private final ExecutionStrategy.Producer _producer;
    private final Executor _executor;
    private State _state = State.IDLE;

    public ProduceConsume(ExecutionStrategy.Producer producer, Executor executor) {
        this._producer = producer;
        this._executor = executor;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void produce() {
        lock = this._lock.lock();
        try {
            switch (1.$SwitchMap$org$eclipse$jetty$util$thread$strategy$ProduceConsume$State[this._state.ordinal()]) {
                case 1: {
                    this._state = State.PRODUCE;
                    ** break;
lbl7:
                    // 1 sources

                    break;
                }
                case 2: 
                case 3: {
                    this._state = State.EXECUTE;
                    return;
                }
                default: {
                    throw new IllegalStateException(this._state.toString());
                }
            }
        }
        finally {
            if (lock != null) {
                lock.close();
            }
        }
        while (true) lbl-1000:
        // 5 sources

        {
            task = this._producer.produce();
            if (ProduceConsume.LOG.isDebugEnabled()) {
                ProduceConsume.LOG.debug("{} produced {}", (Object)this._producer, (Object)task);
            }
            if (task == null) {
                lock = this._lock.lock();
                try {
                    switch (1.$SwitchMap$org$eclipse$jetty$util$thread$strategy$ProduceConsume$State[this._state.ordinal()]) {
                        case 1: {
                            throw new IllegalStateException();
                        }
                        case 2: {
                            this._state = State.IDLE;
                            return;
                        }
                        case 3: {
                            this._state = State.PRODUCE;
                        }
                    }
                    throw new IllegalStateException(this._state.toString());
                }
                finally {
                    if (lock == null) ** GOTO lbl-1000
                    lock.close();
                }
                continue;
            }
            task.run();
        }
    }

    @Override
    public void dispatch() {
        this._executor.execute(this);
    }

    @Override
    public void run() {
        this.produce();
    }

    private static enum State {
        IDLE,
        PRODUCE,
        EXECUTE;

    }
}

