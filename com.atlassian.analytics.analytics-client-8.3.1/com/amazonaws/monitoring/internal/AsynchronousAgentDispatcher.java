/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.monitoring.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.monitoring.MonitoringEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public class AsynchronousAgentDispatcher {
    private static final Log LOG = LogFactory.getLog(AsynchronousAgentDispatcher.class);
    private static final int QUEUE_SIZE = 4096;
    private static AsynchronousAgentDispatcher instance;
    private final ObjectWriter writer;
    private int refCount = 0;
    private volatile BlockingQueue<WriteTask> tasks;
    private ExecutorService exec;
    private volatile boolean initialized = false;

    private AsynchronousAgentDispatcher() {
        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        }
        catch (LinkageError e) {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
        }
        this.writer = mapper.writer();
    }

    @SdkTestInternalApi
    AsynchronousAgentDispatcher(ObjectWriter writer) {
        this.writer = writer;
    }

    public void addWriteTask(MonitoringEvent event, DatagramChannel channel, int maxSize) {
        if (!this.initialized) {
            throw new IllegalStateException("Dispatcher is not initialized!");
        }
        this.tasks.add(new WriteTask(event, channel, maxSize));
    }

    public synchronized void init() {
        if (!this.initialized) {
            this.tasks = new LinkedBlockingQueue<WriteTask>(4096);
            this.exec = Executors.newSingleThreadExecutor(new ThreadFactory(){

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("CsmAgentAsyncDispatchThread");
                    t.setDaemon(true);
                    return t;
                }
            });
            this.exec.submit(new WriterRunnable());
            this.initialized = true;
        }
        ++this.refCount;
    }

    public synchronized void release() {
        if (this.refCount > 0) {
            --this.refCount;
        }
        if (this.refCount == 0 && this.initialized) {
            this.exec.shutdown();
            this.tasks.clear();
            this.exec = null;
            this.tasks = null;
            this.initialized = false;
        }
    }

    public static synchronized AsynchronousAgentDispatcher getInstance() {
        if (instance == null) {
            instance = new AsynchronousAgentDispatcher();
        }
        return instance;
    }

    private byte[] serialize(MonitoringEvent event) throws IOException {
        return this.writer.writeValueAsBytes(event);
    }

    private static class WriteTask {
        private final MonitoringEvent event;
        private final DatagramChannel channel;
        private final int maxSize;

        public WriteTask(MonitoringEvent event, DatagramChannel channel, int maxSize) {
            this.event = event;
            this.channel = channel;
            this.maxSize = maxSize;
        }
    }

    private class WriterRunnable
    implements Runnable {
        private WriterRunnable() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        WriteTask wt;
                        byte[] eventBytes;
                        if ((eventBytes = AsynchronousAgentDispatcher.this.serialize((wt = (WriteTask)AsynchronousAgentDispatcher.this.tasks.take()).event)).length > wt.maxSize) {
                            if (!LOG.isDebugEnabled()) continue;
                            LOG.debug((Object)("Event exceeds the send maximum event size of " + wt.maxSize + ". Dropping event."));
                            continue;
                        }
                        wt.channel.write(ByteBuffer.wrap(eventBytes));
                    }
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    LOG.debug((Object)"Writer thread interrupted", (Throwable)ie);
                }
                catch (Exception e) {
                    LOG.debug((Object)"Exception thrown while attempting to send event to agent", (Throwable)e);
                    continue;
                }
                break;
            }
        }
    }
}

