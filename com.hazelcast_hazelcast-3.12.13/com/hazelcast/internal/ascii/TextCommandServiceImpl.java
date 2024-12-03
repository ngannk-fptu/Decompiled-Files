/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.ascii.AbstractTextCommand;
import com.hazelcast.internal.ascii.NoOpCommandProcessor;
import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextCommandProcessor;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.BulkGetCommandProcessor;
import com.hazelcast.internal.ascii.memcache.DeleteCommandProcessor;
import com.hazelcast.internal.ascii.memcache.EntryConverter;
import com.hazelcast.internal.ascii.memcache.ErrorCommandProcessor;
import com.hazelcast.internal.ascii.memcache.GetCommandProcessor;
import com.hazelcast.internal.ascii.memcache.IncrementCommandProcessor;
import com.hazelcast.internal.ascii.memcache.SetCommandProcessor;
import com.hazelcast.internal.ascii.memcache.SimpleCommandProcessor;
import com.hazelcast.internal.ascii.memcache.Stats;
import com.hazelcast.internal.ascii.memcache.StatsCommandProcessor;
import com.hazelcast.internal.ascii.memcache.TouchCommandProcessor;
import com.hazelcast.internal.ascii.memcache.VersionCommandProcessor;
import com.hazelcast.internal.ascii.rest.HttpDeleteCommandProcessor;
import com.hazelcast.internal.ascii.rest.HttpGetCommandProcessor;
import com.hazelcast.internal.ascii.rest.HttpHeadCommandProcessor;
import com.hazelcast.internal.ascii.rest.HttpPostCommandProcessor;
import com.hazelcast.internal.ascii.rest.RestValue;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.AggregateEndpointManager;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.nio.ascii.TextEncoder;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ThreadUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TextCommandServiceImpl
implements TextCommandService {
    private static final int TEXT_COMMAND_PROCESSOR_SIZE = 100;
    private static final int MILLIS_TO_SECONDS = 1000;
    private static final long WAIT_TIME = 1000L;
    private final TextCommandProcessor[] textCommandProcessors = new TextCommandProcessor[100];
    private final AtomicLong sets = new AtomicLong();
    private final AtomicLong touches = new AtomicLong();
    private final AtomicLong getHits = new AtomicLong();
    private final AtomicLong getMisses = new AtomicLong();
    private final AtomicLong deleteMisses = new AtomicLong();
    private final AtomicLong deleteHits = new AtomicLong();
    private final AtomicLong incrementHits = new AtomicLong();
    private final AtomicLong incrementMisses = new AtomicLong();
    private final AtomicLong decrementHits = new AtomicLong();
    private final AtomicLong decrementMisses = new AtomicLong();
    private final long startTime = Clock.currentTimeMillis();
    private final Node node;
    private final HazelcastInstance hazelcast;
    private final ILogger logger;
    private volatile ResponseThreadRunnable responseThreadRunnable;
    private volatile boolean running = true;
    private final Object mutex = new Object();

    public TextCommandServiceImpl(Node node) {
        this.node = node;
        this.hazelcast = node.hazelcastInstance;
        this.logger = node.getLogger(this.getClass().getName());
        EntryConverter entryConverter = new EntryConverter(this, node.getLogger(EntryConverter.class));
        this.register(TextCommandConstants.TextCommandType.GET, new GetCommandProcessor(this, entryConverter));
        this.register(TextCommandConstants.TextCommandType.BULK_GET, new BulkGetCommandProcessor(this, entryConverter));
        this.register(TextCommandConstants.TextCommandType.SET, new SetCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.APPEND, new SetCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.PREPEND, new SetCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.ADD, new SetCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.REPLACE, new SetCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.GET_END, new NoOpCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.DELETE, new DeleteCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.QUIT, new SimpleCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.STATS, new StatsCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.UNKNOWN, new ErrorCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.VERSION, new VersionCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.TOUCH, new TouchCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.INCREMENT, new IncrementCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.DECREMENT, new IncrementCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.ERROR_CLIENT, new ErrorCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.ERROR_SERVER, new ErrorCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.HTTP_GET, new HttpGetCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.HTTP_POST, new HttpPostCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.HTTP_PUT, new HttpPostCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.HTTP_DELETE, new HttpDeleteCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.HTTP_HEAD, new HttpHeadCommandProcessor(this));
        this.register(TextCommandConstants.TextCommandType.NO_OP, new NoOpCommandProcessor(this));
    }

    protected void register(TextCommandConstants.TextCommandType type, TextCommandProcessor processor) {
        this.textCommandProcessors[type.getValue()] = processor;
    }

    @Override
    public Node getNode() {
        return this.node;
    }

    @Override
    public byte[] toByteArray(Object value) {
        Object data = this.node.getSerializationService().toData(value);
        return data.toByteArray();
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setUptime((int)((Clock.currentTimeMillis() - this.startTime) / 1000L));
        stats.setCmdGet(this.getMisses.get() + this.getHits.get());
        stats.setCmdSet(this.sets.get());
        stats.setCmdTouch(this.touches.get());
        stats.setGetHits(this.getHits.get());
        stats.setGetMisses(this.getMisses.get());
        stats.setDeleteHits(this.deleteHits.get());
        stats.setDeleteMisses(this.deleteMisses.get());
        stats.setIncrHits(this.incrementHits.get());
        stats.setIncrMisses(this.incrementMisses.get());
        stats.setDecrHits(this.decrementHits.get());
        stats.setDecrMisses(this.decrementMisses.get());
        NetworkingService cm = this.node.networkingService;
        EndpointManager mem = cm.getEndpointManager(EndpointQualifier.MEMCACHE);
        int totalText = mem != null ? mem.getActiveConnections().size() : 0;
        AggregateEndpointManager aem = cm.getAggregateEndpointManager();
        stats.setCurrConnections(totalText);
        stats.setTotalConnections(aem.getActiveConnections().size());
        return stats;
    }

    @Override
    public long incrementDeleteHitCount(int inc) {
        return this.deleteHits.addAndGet(inc);
    }

    @Override
    public long incrementDeleteMissCount() {
        return this.deleteMisses.incrementAndGet();
    }

    @Override
    public long incrementGetHitCount() {
        return this.getHits.incrementAndGet();
    }

    @Override
    public long incrementGetMissCount() {
        return this.getMisses.incrementAndGet();
    }

    @Override
    public long incrementSetCount() {
        return this.sets.incrementAndGet();
    }

    @Override
    public long incrementIncHitCount() {
        return this.incrementHits.incrementAndGet();
    }

    @Override
    public long incrementIncMissCount() {
        return this.incrementMisses.incrementAndGet();
    }

    @Override
    public long incrementDecrHitCount() {
        return this.decrementHits.incrementAndGet();
    }

    @Override
    public long incrementDecrMissCount() {
        return this.decrementMisses.incrementAndGet();
    }

    @Override
    public long incrementTouchCount() {
        return this.touches.incrementAndGet();
    }

    @Override
    public void processRequest(TextCommand command) {
        this.startResponseThreadIfNotRunning();
        this.node.nodeEngine.getExecutionService().execute("hz:text", new CommandExecutor(command));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startResponseThreadIfNotRunning() {
        if (this.responseThreadRunnable == null) {
            Object object = this.mutex;
            synchronized (object) {
                if (this.responseThreadRunnable == null) {
                    this.responseThreadRunnable = new ResponseThreadRunnable();
                    String threadNamePrefix = ThreadUtil.createThreadName(this.hazelcast.getName(), "ascii.service.response");
                    Thread thread = new Thread((Runnable)this.responseThreadRunnable, threadNamePrefix);
                    thread.start();
                }
            }
        }
    }

    @Override
    public Object get(String mapName, String key) {
        return this.hazelcast.getMap(mapName).get(key);
    }

    @Override
    public Map<String, Object> getAll(String mapName, Set<String> keys) {
        IMap map = this.hazelcast.getMap(mapName);
        return map.getAll(keys);
    }

    @Override
    public int getAdjustedTTLSeconds(int ttl) {
        if (ttl <= TextCommandConstants.getMonthSeconds()) {
            return ttl;
        }
        return ttl - (int)TimeUnit.MILLISECONDS.toSeconds(Clock.currentTimeMillis());
    }

    @Override
    public byte[] getByteArray(String mapName, String key) {
        Object value = this.hazelcast.getMap(mapName).get(key);
        byte[] result = null;
        if (value != null) {
            if (value instanceof RestValue) {
                RestValue restValue = (RestValue)value;
                result = restValue.getValue();
            } else {
                result = value instanceof byte[] ? (byte[])value : this.toByteArray(value);
            }
        }
        return result;
    }

    @Override
    public Object put(String mapName, String key, Object value) {
        return this.hazelcast.getMap(mapName).put(key, value);
    }

    @Override
    public Object put(String mapName, String key, Object value, int ttlSeconds) {
        return this.hazelcast.getMap(mapName).put(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Object putIfAbsent(String mapName, String key, Object value, int ttlSeconds) {
        return this.hazelcast.getMap(mapName).putIfAbsent(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Object replace(String mapName, String key, Object value) {
        return this.hazelcast.getMap(mapName).replace(key, value);
    }

    @Override
    public void lock(String mapName, String key) throws InterruptedException {
        if (!this.hazelcast.getMap(mapName).tryLock(key, 1L, TimeUnit.MINUTES)) {
            throw new RuntimeException("Memcache client could not get the lock for map: " + mapName + ", key: " + key + " in 1 minute");
        }
    }

    @Override
    public void unlock(String mapName, String key) {
        this.hazelcast.getMap(mapName).unlock(key);
    }

    @Override
    public void deleteAll(String mapName) {
        IMap map = this.hazelcast.getMap(mapName);
        map.clear();
    }

    @Override
    public Object delete(String mapName, String key) {
        return this.hazelcast.getMap(mapName).remove(key);
    }

    @Override
    public boolean offer(String queueName, Object value) {
        return this.hazelcast.getQueue(queueName).offer(value);
    }

    @Override
    public Object poll(String queueName, int seconds) {
        try {
            return this.hazelcast.getQueue(queueName).poll(seconds, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public Object poll(String queueName) {
        return this.hazelcast.getQueue(queueName).poll();
    }

    @Override
    public int size(String queueName) {
        return this.hazelcast.getQueue(queueName).size();
    }

    @Override
    public void sendResponse(TextCommand textCommand) {
        if (!textCommand.shouldReply() || textCommand.getRequestId() == -1L) {
            throw new RuntimeException("Shouldn't reply " + textCommand);
        }
        this.responseThreadRunnable.sendResponse(textCommand);
    }

    @Override
    public void stop() {
        ResponseThreadRunnable rtr = this.responseThreadRunnable;
        if (rtr != null) {
            this.logger.info("Stopping text command service...");
            rtr.stop();
        }
    }

    private class ResponseThreadRunnable
    implements Runnable {
        private final BlockingQueue<TextCommand> blockingQueue = new ArrayBlockingQueue<TextCommand>(200);
        private final Object stopObject = new Object();

        private ResponseThreadRunnable() {
        }

        @SuppressFBWarnings(value={"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
        public void sendResponse(TextCommand textCommand) {
            this.blockingQueue.offer(textCommand);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            while (TextCommandServiceImpl.this.running) {
                try {
                    TextCommand textCommand = this.blockingQueue.take();
                    if (TextCommandConstants.TextCommandType.STOP == textCommand.getType()) {
                        Object object = this.stopObject;
                        synchronized (object) {
                            this.stopObject.notify();
                            continue;
                        }
                    }
                    TextEncoder textWriteHandler = textCommand.getEncoder();
                    textWriteHandler.enqueue(textCommand);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                catch (OutOfMemoryError e) {
                    OutOfMemoryErrorDispatcher.onOutOfMemory(e);
                    throw e;
                }
                catch (Throwable t) {
                    TextCommandServiceImpl.this.logger.severe("Error while processing Memcache or Rest command.", t);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @SuppressFBWarnings(value={"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
        void stop() {
            TextCommandServiceImpl.this.running = false;
            Object object = this.stopObject;
            synchronized (object) {
                try {
                    this.blockingQueue.offer(new AbstractTextCommand(TextCommandConstants.TextCommandType.STOP){

                        @Override
                        public boolean readFrom(ByteBuffer src) {
                            return true;
                        }

                        @Override
                        public boolean writeTo(ByteBuffer dst) {
                            return true;
                        }
                    });
                    this.stopObject.wait(1000L);
                }
                catch (Exception ignored) {
                    EmptyStatement.ignore(ignored);
                }
            }
        }
    }

    class CommandExecutor
    implements Runnable {
        final TextCommand command;

        CommandExecutor(TextCommand command) {
            this.command = command;
        }

        @Override
        public void run() {
            try {
                TextCommandConstants.TextCommandType type = this.command.getType();
                TextCommandProcessor textCommandProcessor = TextCommandServiceImpl.this.textCommandProcessors[type.getValue()];
                textCommandProcessor.handle(this.command);
            }
            catch (Throwable e) {
                TextCommandServiceImpl.this.logger.warning(e);
            }
        }
    }
}

