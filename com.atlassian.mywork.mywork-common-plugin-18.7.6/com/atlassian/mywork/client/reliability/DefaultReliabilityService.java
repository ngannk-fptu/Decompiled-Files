/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.SettableFuture
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.reliability;

import com.atlassian.mywork.client.reliability.ReliabilityService;
import com.atlassian.mywork.client.reliability.UnreliableTask;
import com.atlassian.mywork.client.reliability.UnreliableTaskListener;
import com.atlassian.mywork.client.reliability.UnreliableWorker;
import com.atlassian.mywork.client.schedule.Scheduler;
import com.atlassian.util.concurrent.SettableFuture;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultReliabilityService
implements ReliabilityService {
    private static final Logger log = LoggerFactory.getLogger(DefaultReliabilityService.class);
    private static final int MAX_QUEUE_SIZE = 10000;
    private final LoadingCache<String, PriorityBlockingQueue<TimestampedTask>> queues = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, PriorityBlockingQueue<TimestampedTask>>(){

        public PriorityBlockingQueue<TimestampedTask> load(String key) {
            PriorityBlockingQueue<TimestampedTask> queue = new PriorityBlockingQueue<TimestampedTask>();
            DefaultReliabilityService.this.schedule(queue);
            return queue;
        }
    });
    private volatile boolean queueFull = false;
    private final Scheduler scheduler;
    private final UnreliableWorker worker;

    public DefaultReliabilityService(Scheduler scheduler, UnreliableWorker worker) {
        this.scheduler = scheduler;
        this.worker = worker;
    }

    @Override
    public Future<String> submit(UnreliableTask task) {
        TimestampedTask wrapper = new TimestampedTask(task);
        PriorityBlockingQueue queue = (PriorityBlockingQueue)this.queues.getUnchecked((Object)task.appLinkId);
        if (queue.size() > 10000) {
            if (!this.queueFull) {
                log.warn("Queue has exceeded the maximum size. Tasks cannot be delivered");
            }
            wrapper.future.cancel(false);
            this.queueFull = true;
        } else {
            queue.add(wrapper);
            this.queueFull = false;
        }
        return wrapper.future;
    }

    private void schedule(final PriorityBlockingQueue<TimestampedTask> queue) {
        this.scheduler.schedule(new Scheduler.ScheduleRunnable(){

            @Override
            public void run(final Scheduler.ScheduleCallback callback) {
                TimestampedTask wrapper;
                try {
                    wrapper = (TimestampedTask)queue.take();
                }
                catch (InterruptedException e) {
                    callback.failed();
                    return;
                }
                final UnreliableTask task = wrapper.task;
                DefaultReliabilityService.this.worker.start(task, new UnreliableTaskListener(){

                    @Override
                    public void succeeded(String result) {
                        log.debug("Succeeded task \"{}\" on worker \"{}\"", (Object)task.getTaskData());
                        wrapper.future.set((Object)result);
                        callback.pass();
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        log.warn("Failed task \"" + task.getTaskData() + "\"", throwable);
                        if (queue.size() <= 10000) {
                            queue.add(wrapper);
                        }
                        callback.failed();
                    }

                    @Override
                    public void cancel() {
                        wrapper.future.cancel(false);
                        callback.pass();
                    }
                });
            }
        });
    }

    private static class TimestampedTask
    implements Comparable<TimestampedTask> {
        private final SettableFuture<String> future = new SettableFuture();
        private final long timestamp = new Date().getTime();
        private final UnreliableTask task;

        public TimestampedTask(UnreliableTask task) {
            this.task = task;
        }

        @Override
        public int compareTo(TimestampedTask o) {
            return Long.valueOf(this.timestamp).compareTo(o.timestamp);
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof TimestampedTask)) {
                return false;
            }
            return this.timestamp == ((TimestampedTask)o).timestamp;
        }

        public int hashCode() {
            return Long.valueOf(this.timestamp).hashCode();
        }
    }
}

