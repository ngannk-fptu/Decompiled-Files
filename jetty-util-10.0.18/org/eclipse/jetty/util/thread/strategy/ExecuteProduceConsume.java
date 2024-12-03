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
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteProduceConsume
implements ExecutionStrategy,
Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ExecuteProduceConsume.class);
    private final AutoLock _lock = new AutoLock();
    private final Runnable _runProduce = new RunProduce();
    private final ExecutionStrategy.Producer _producer;
    private final Executor _executor;
    private boolean _idle = true;
    private boolean _execute;
    private boolean _producing;
    private boolean _pending;

    public ExecuteProduceConsume(ExecutionStrategy.Producer producer, Executor executor) {
        this._producer = producer;
        this._executor = executor;
    }

    @Override
    public void produce() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} execute", (Object)this);
        }
        boolean produce = false;
        try (AutoLock lock = this._lock.lock();){
            if (this._idle) {
                if (this._producing) {
                    throw new IllegalStateException();
                }
                this._producing = true;
                produce = true;
                this._idle = false;
            } else {
                this._execute = true;
            }
        }
        if (produce) {
            this.produceConsume();
        }
    }

    @Override
    public void dispatch() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} spawning", (Object)this);
        }
        boolean dispatch = false;
        try (AutoLock lock = this._lock.lock();){
            if (this._idle) {
                dispatch = true;
            } else {
                this._execute = true;
            }
        }
        if (dispatch) {
            this._executor.execute(this._runProduce);
        }
    }

    @Override
    public void run() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} run", (Object)this);
        }
        boolean produce = false;
        try (AutoLock lock = this._lock.lock();){
            this._pending = false;
            if (!this._idle && !this._producing) {
                this._producing = true;
                produce = true;
            }
        }
        if (produce) {
            this.produceConsume();
        }
    }

    private void produceConsume() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} produce enter", (Object)this);
        }
        while (true) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} producing", (Object)this);
            }
            Runnable task = this._producer.produce();
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} produced {}", (Object)this, (Object)task);
            }
            boolean dispatch = false;
            AutoLock lock = this._lock.lock();
            try {
                this._producing = false;
                if (task == null) {
                    if (this._execute) {
                        this._idle = false;
                        this._producing = true;
                        this._execute = false;
                        continue;
                    }
                    this._idle = true;
                    break;
                }
                if (!this._pending) {
                    this._pending = Invocable.getInvocationType(task) != Invocable.InvocationType.NON_BLOCKING;
                    dispatch = this._pending;
                }
                this._execute = false;
            }
            finally {
                if (lock == null) continue;
                lock.close();
                continue;
            }
            if (dispatch) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} dispatch", (Object)this);
                }
                this._executor.execute(this);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} run {}", (Object)this, (Object)task);
            }
            task.run();
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} ran {}", (Object)this, (Object)task);
            }
            lock = this._lock.lock();
            try {
                if (this._producing || this._idle) break;
                this._producing = true;
                continue;
            }
            finally {
                if (lock == null) continue;
                lock.close();
                continue;
            }
            break;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} produce exit", (Object)this);
        }
    }

    public Boolean isIdle() {
        try (AutoLock lock = this._lock.lock();){
            Boolean bl = this._idle;
            return bl;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EPC ");
        try (AutoLock lock = this._lock.lock();){
            builder.append(this._idle ? "Idle/" : "");
            builder.append(this._producing ? "Prod/" : "");
            builder.append(this._pending ? "Pend/" : "");
            builder.append(this._execute ? "Exec/" : "");
        }
        builder.append(this._producer);
        return builder.toString();
    }

    private class RunProduce
    implements Runnable {
        private RunProduce() {
        }

        @Override
        public void run() {
            ExecuteProduceConsume.this.produce();
        }
    }
}

