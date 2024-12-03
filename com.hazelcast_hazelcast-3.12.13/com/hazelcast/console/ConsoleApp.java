/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.console;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.console.DefaultLineReader;
import com.hazelcast.console.Echo;
import com.hazelcast.console.LineReader;
import com.hazelcast.console.SimulateLoadTask;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.Partition;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.memory.MemoryUnit;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.util.Clock;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ConsoleApp
implements EntryListener<Object, Object>,
ItemListener<Object>,
MessageListener<Object> {
    private static final String EXECUTOR_NAMESPACE = "Sample Executor";
    private static final int LOAD_EXECUTORS_COUNT = 16;
    private static final int ONE_HUNDRED = 100;
    private IQueue<Object> queue;
    private ITopic<Object> topic;
    private IMap<Object, Object> map;
    private MultiMap<Object, Object> multiMap;
    private ISet<Object> set;
    private IList<Object> list;
    private IAtomicLong atomicNumber;
    private String namespace = "default";
    private boolean silent;
    private boolean echo;
    private volatile HazelcastInstance hazelcast;
    private volatile LineReader lineReader;
    private volatile boolean running;

    public ConsoleApp(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    public IQueue<Object> getQueue() {
        this.queue = this.hazelcast.getQueue(this.namespace);
        return this.queue;
    }

    public ITopic<Object> getTopic() {
        this.topic = this.hazelcast.getTopic(this.namespace);
        return this.topic;
    }

    public IMap<Object, Object> getMap() {
        this.map = this.hazelcast.getMap(this.namespace);
        return this.map;
    }

    public MultiMap<Object, Object> getMultiMap() {
        this.multiMap = this.hazelcast.getMultiMap(this.namespace);
        return this.multiMap;
    }

    public IAtomicLong getAtomicNumber() {
        this.atomicNumber = this.hazelcast.getAtomicLong(this.namespace);
        return this.atomicNumber;
    }

    public ISet<Object> getSet() {
        this.set = this.hazelcast.getSet(this.namespace);
        return this.set;
    }

    public IList<Object> getList() {
        this.list = this.hazelcast.getList(this.namespace);
        return this.list;
    }

    public void setHazelcast(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
        this.map = null;
        this.list = null;
        this.set = null;
        this.queue = null;
        this.topic = null;
    }

    public void stop() {
        this.running = false;
    }

    public void start() throws Exception {
        this.getMap().size();
        this.getList().size();
        this.getSet().size();
        this.getQueue().size();
        this.getTopic().getLocalTopicStats();
        this.getMultiMap().size();
        this.hazelcast.getExecutorService("default").getLocalExecutorStats();
        for (int i = 1; i <= 16; ++i) {
            this.hazelcast.getExecutorService("Sample Executor " + i).getLocalExecutorStats();
        }
        if (this.lineReader == null) {
            this.lineReader = new DefaultLineReader();
        }
        this.running = true;
        while (this.running) {
            this.print("hazelcast[" + this.namespace + "] > ");
            try {
                String command = this.lineReader.readLine();
                this.handleCommand(command);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void handleCommand(String inputCommand) {
        String command = inputCommand;
        if (command == null) {
            return;
        }
        if ((command = StringUtil.trim(command)).length() == 0) {
            return;
        }
        if (command.contains("__")) {
            this.namespace = command.split("__")[0];
            command = command.substring(command.indexOf("__") + 2);
        }
        if (this.echo) {
            this.handleEcho(command);
        }
        if (command.startsWith("//")) {
            return;
        }
        String first = command;
        int spaceIndex = command.indexOf(32);
        String[] argsSplit = command.split(" ");
        String[] args = new String[argsSplit.length];
        for (int i = 0; i < argsSplit.length; ++i) {
            args[i] = StringUtil.trim(argsSplit[i]);
        }
        if (spaceIndex != -1) {
            first = args[0];
        }
        if (command.startsWith("help")) {
            this.handleHelp(command);
        } else if (first.startsWith("#") && first.length() > 1) {
            int repeat = Integer.parseInt(first.substring(1));
            long started = Clock.currentTimeMillis();
            for (int i = 0; i < repeat; ++i) {
                this.handleCommand(command.substring(first.length()).replaceAll("\\$i", "" + i));
            }
            long elapsedMilliSeconds = Clock.currentTimeMillis() - started;
            if (elapsedMilliSeconds > 0L) {
                this.println(String.format("ops/s = %.2f", (double)repeat * 1000.0 / (double)elapsedMilliSeconds));
            } else {
                this.println("Bingo, all the operations finished in no time!");
            }
        } else if (first.startsWith("&") && first.length() > 1) {
            int fork = Integer.parseInt(first.substring(1));
            final String threadCommand = command.substring(first.length());
            ExecutorService pool = Executors.newFixedThreadPool(fork);
            int i = 0;
            while (i < fork) {
                final int threadID = i++;
                pool.submit(new Runnable(){

                    @Override
                    public void run() {
                        String command = threadCommand;
                        String[] threadArgs = StringUtil.trim(command.replaceAll("\\$t", "" + threadID)).split(" ");
                        if (("m.putmany".equals(threadArgs[0]) || "m.removemany".equals(threadArgs[0])) && threadArgs.length < 4) {
                            command = command + " " + Integer.parseInt(threadArgs[1]) * threadID;
                        }
                        ConsoleApp.this.handleCommand(command);
                    }
                });
            }
            pool.shutdown();
            try {
                pool.awaitTermination(1L, TimeUnit.HOURS);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else if (first.startsWith("@")) {
            this.handleAt(first);
        } else if (command.indexOf(59) != -1) {
            this.handleColon(command);
        } else if ("silent".equals(first)) {
            this.silent = Boolean.parseBoolean(args[1]);
        } else if (StringUtil.equalsIgnoreCase("shutdown", first)) {
            this.handleShutdown();
        } else if ("echo".equals(first)) {
            this.echo = Boolean.parseBoolean(args[1]);
            this.println("echo: " + this.echo);
        } else if ("ns".equals(first)) {
            this.handleNamespace(StringUtil.trim(command.substring(first.length())));
        } else if ("whoami".equals(first)) {
            this.handleWhoami();
        } else if ("who".equals(first)) {
            this.handleWho();
        } else if ("jvm".equals(first)) {
            this.handleJvm();
        } else if (first.contains("ock") && !first.contains(".")) {
            this.handleLock(args);
        } else if (first.contains(".size")) {
            this.handleSize(args);
        } else if (first.contains(".clear")) {
            this.handleClear(args);
        } else if (first.contains(".destroy")) {
            this.handleDestroy(args);
        } else if (first.contains(".iterator")) {
            this.handleIterator(args);
        } else if (first.contains(".contains")) {
            this.handleContains(args);
        } else if (first.contains(".stats")) {
            this.handStats(args);
        } else if ("t.publish".equals(first)) {
            this.handleTopicPublish(args);
        } else if ("q.offer".equals(first)) {
            this.handleQOffer(args);
        } else if ("q.take".equals(first)) {
            this.handleQTake(args);
        } else if ("q.poll".equals(first)) {
            this.handleQPoll(args);
        } else if ("q.peek".equals(first)) {
            this.handleQPeek(args);
        } else if ("q.capacity".equals(first)) {
            this.handleQCapacity(args);
        } else if ("q.offermany".equals(first)) {
            this.handleQOfferMany(args);
        } else if ("q.pollmany".equals(first)) {
            this.handleQPollMany(args);
        } else if ("s.add".equals(first)) {
            this.handleSetAdd(args);
        } else if ("s.remove".equals(first)) {
            this.handleSetRemove(args);
        } else if ("s.addmany".equals(first)) {
            this.handleSetAddMany(args);
        } else if ("s.removemany".equals(first)) {
            this.handleSetRemoveMany(args);
        } else if (first.equals("m.replace")) {
            this.handleMapReplace(args);
        } else if (StringUtil.equalsIgnoreCase(first, "m.putIfAbsent")) {
            this.handleMapPutIfAbsent(args);
        } else if (first.equals("m.putAsync")) {
            this.handleMapPutAsync(args);
        } else if (first.equals("m.getAsync")) {
            this.handleMapGetAsync(args);
        } else if (first.equals("m.put")) {
            this.handleMapPut(args);
        } else if (first.equals("m.get")) {
            this.handleMapGet(args);
        } else if (StringUtil.equalsIgnoreCase(first, "m.getMapEntry")) {
            this.handleMapGetMapEntry(args);
        } else if (first.equals("m.remove")) {
            this.handleMapRemove(args);
        } else if (first.equals("m.delete")) {
            this.handleMapDelete(args);
        } else if (first.equals("m.evict")) {
            this.handleMapEvict(args);
        } else if (first.equals("m.putmany") || StringUtil.equalsIgnoreCase(first, "m.putAll")) {
            this.handleMapPutMany(args);
        } else if (first.equals("m.getmany")) {
            this.handleMapGetMany(args);
        } else if (first.equals("m.removemany")) {
            this.handleMapRemoveMany(args);
        } else if (StringUtil.equalsIgnoreCase(command, "m.localKeys")) {
            this.handleMapLocalKeys();
        } else if (StringUtil.equalsIgnoreCase(command, "m.localSize")) {
            this.handleMapLocalSize();
        } else if (command.equals("m.keys")) {
            this.handleMapKeys();
        } else if (command.equals("m.values")) {
            this.handleMapValues();
        } else if (command.equals("m.entries")) {
            this.handleMapEntries();
        } else if (first.equals("m.lock")) {
            this.handleMapLock(args);
        } else if (StringUtil.equalsIgnoreCase(first, "m.tryLock")) {
            this.handleMapTryLock(args);
        } else if (first.equals("m.unlock")) {
            this.handleMapUnlock(args);
        } else if (first.contains(".addListener")) {
            this.handleAddListener(args);
        } else if (first.equals("m.removeMapListener")) {
            this.handleRemoveListener(args);
        } else if (first.equals("mm.put")) {
            this.handleMultiMapPut(args);
        } else if (first.equals("mm.get")) {
            this.handleMultiMapGet(args);
        } else if (first.equals("mm.remove")) {
            this.handleMultiMapRemove(args);
        } else if (command.equals("mm.keys")) {
            this.handleMultiMapKeys();
        } else if (command.equals("mm.values")) {
            this.handleMultiMapValues();
        } else if (command.equals("mm.entries")) {
            this.handleMultiMapEntries();
        } else if (first.equals("mm.lock")) {
            this.handleMultiMapLock(args);
        } else if (StringUtil.equalsIgnoreCase(first, "mm.tryLock")) {
            this.handleMultiMapTryLock(args);
        } else if (first.equals("mm.unlock")) {
            this.handleMultiMapUnlock(args);
        } else if (first.equals("l.add")) {
            this.handleListAdd(args);
        } else if (first.equals("l.set")) {
            this.handleListSet(args);
        } else if ("l.addmany".equals(first)) {
            this.handleListAddMany(args);
        } else if (first.equals("l.remove")) {
            this.handleListRemove(args);
        } else if (first.equals("l.contains")) {
            this.handleListContains(args);
        } else if ("a.get".equals(first)) {
            this.handleAtomicNumberGet(args);
        } else if ("a.set".equals(first)) {
            this.handleAtomicNumberSet(args);
        } else if ("a.inc".equals(first)) {
            this.handleAtomicNumberInc(args);
        } else if ("a.dec".equals(first)) {
            this.handleAtomicNumberDec(args);
        } else if (first.equals("execute")) {
            this.execute(args);
        } else if (first.equals("partitions")) {
            this.handlePartitions(args);
        } else if (StringUtil.equalsIgnoreCase(first, "executeOnKey")) {
            this.executeOnKey(args);
        } else if (StringUtil.equalsIgnoreCase(first, "executeOnMember")) {
            this.executeOnMember(args);
        } else if (StringUtil.equalsIgnoreCase(first, "executeOnMembers")) {
            this.executeOnMembers(args);
        } else if (StringUtil.equalsIgnoreCase(first, "instances")) {
            this.handleInstances(args);
        } else if (StringUtil.equalsIgnoreCase(first, "quit") || StringUtil.equalsIgnoreCase(first, "exit")) {
            this.handleExit();
        } else if (first.startsWith("e") && first.endsWith(".simulateLoad")) {
            this.handleExecutorSimulate(args);
        } else {
            this.println("type 'help' for help");
        }
    }

    protected void handleShutdown() {
        this.hazelcast.getLifecycleService().shutdown();
    }

    protected void handleExit() {
        System.exit(0);
    }

    private void handleExecutorSimulate(String[] args) {
        String first = args[0];
        int threadCount = Integer.parseInt(first.substring(1, first.indexOf(".")));
        if (threadCount < 1 || threadCount > 16) {
            throw new RuntimeException("threadCount can't be smaller than 1 or larger than 16");
        }
        int taskCount = Integer.parseInt(args[1]);
        int durationSec = Integer.parseInt(args[2]);
        long startMs = System.currentTimeMillis();
        IExecutorService executor = this.hazelcast.getExecutorService("Sample Executor " + threadCount);
        LinkedList futures = new LinkedList();
        LinkedList<Member> members = new LinkedList<Member>(this.hazelcast.getCluster().getMembers());
        int totalThreadCount = this.hazelcast.getCluster().getMembers().size() * threadCount;
        int latchId = 0;
        for (int i = 0; i < taskCount; ++i) {
            Member member = (Member)members.get(i % members.size());
            if (taskCount % totalThreadCount == 0) {
                latchId = taskCount / totalThreadCount;
                this.hazelcast.getCountDownLatch("latch" + latchId).trySetCount(totalThreadCount);
            }
            Future f = executor.submitToMember(new SimulateLoadTask(durationSec, i + 1, "latch" + latchId), member);
            futures.add(f);
        }
        for (Future future : futures) {
            try {
                future.get();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        long durationMs = System.currentTimeMillis() - startMs;
        this.println(String.format("Executed %s tasks in %s ms", taskCount, durationMs));
    }

    private void handleColon(String command) {
        StringTokenizer st = new StringTokenizer(command, ";");
        while (st.hasMoreTokens()) {
            this.handleCommand(st.nextToken());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleAt(String first) {
        if (first.length() == 1) {
            this.println("usage: @<file-name>");
            return;
        }
        File f = new File(first.substring(1));
        this.println("Executing script file " + f.getAbsolutePath());
        if (f.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(f), "UTF-8"));
                String l = br.readLine();
                while (l != null) {
                    this.handleCommand(l);
                    l = br.readLine();
                }
                IOUtil.closeResource(br);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                IOUtil.closeResource(br);
            }
        } else {
            this.println("File not found! " + f.getAbsolutePath());
        }
    }

    private void handleEcho(String command) {
        String threadName = StringUtil.lowerCaseInternal(Thread.currentThread().getName());
        if (!threadName.contains("main")) {
            this.println(" [" + Thread.currentThread().getName() + "] " + command);
        } else {
            this.println(command);
        }
    }

    private void handleNamespace(String namespace) {
        if (!namespace.isEmpty()) {
            this.namespace = namespace;
        }
        this.println("namespace: " + namespace);
    }

    @SuppressFBWarnings(value={"DM_GC"})
    private void handleJvm() {
        System.gc();
        long max = Runtime.getRuntime().maxMemory();
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        this.println("Memory max: " + MemoryUnit.BYTES.toMegaBytes(max) + "MB");
        this.println("Memory free: " + MemoryUnit.BYTES.toMegaBytes(free) + "MB " + (int)(free * 100L / max) + "%");
        this.println("Used Memory:" + MemoryUnit.BYTES.toMegaBytes(total - free) + "MB");
        this.println("# procs: " + RuntimeAvailableProcessors.get());
        this.println("OS info: " + ManagementFactory.getOperatingSystemMXBean().getArch() + " " + ManagementFactory.getOperatingSystemMXBean().getName() + " " + ManagementFactory.getOperatingSystemMXBean().getVersion());
        this.println("JVM: " + ManagementFactory.getRuntimeMXBean().getVmVendor() + " " + ManagementFactory.getRuntimeMXBean().getVmName() + " " + ManagementFactory.getRuntimeMXBean().getVmVersion());
    }

    private void handleWhoami() {
        this.println(this.hazelcast.getCluster().getLocalMember());
    }

    private void handleWho() {
        StringBuilder sb = new StringBuilder("\n\nMembers [");
        Set<Member> members = this.hazelcast.getCluster().getMembers();
        sb.append(members != null ? members.size() : 0);
        sb.append("] {");
        if (members != null) {
            for (Member member : members) {
                sb.append("\n\t").append(member);
            }
        }
        sb.append("\n}\n");
        this.println(sb.toString());
    }

    private void handleAtomicNumberGet(String[] args) {
        this.println(this.getAtomicNumber().get());
    }

    private void handleAtomicNumberSet(String[] args) {
        long v = 0L;
        if (args.length > 1) {
            v = Long.parseLong(args[1]);
        }
        this.getAtomicNumber().set(v);
        this.println(this.getAtomicNumber().get());
    }

    private void handleAtomicNumberInc(String[] args) {
        this.println(this.getAtomicNumber().incrementAndGet());
    }

    private void handleAtomicNumberDec(String[] args) {
        this.println(this.getAtomicNumber().decrementAndGet());
    }

    protected void handlePartitions(String[] args) {
        Set<Partition> partitions = this.hazelcast.getPartitionService().getPartitions();
        HashMap<Member, Integer> partitionCounts = new HashMap<Member, Integer>();
        for (Partition partition : partitions) {
            Member owner = partition.getOwner();
            if (owner != null) {
                Integer count = (Integer)partitionCounts.get(owner);
                int newCount = 1;
                if (count != null) {
                    newCount = count + 1;
                }
                partitionCounts.put(owner, newCount);
            }
            this.println(partition);
        }
        Set entries = partitionCounts.entrySet();
        for (Map.Entry entry : entries) {
            this.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    protected void handleInstances(String[] args) {
        Collection<DistributedObject> distributedObjects = this.hazelcast.getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            this.println(distributedObject);
        }
    }

    protected void handleListContains(String[] args) {
        this.println(this.getList().contains(args[1]));
    }

    protected void handleListRemove(String[] args) {
        int index;
        try {
            index = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        if (index >= 0) {
            this.println(this.getList().remove(index));
        } else {
            this.println(this.getList().remove(args[1]));
        }
    }

    protected void handleListAdd(String[] args) {
        if (args.length == 3) {
            int index = Integer.parseInt(args[1]);
            this.getList().add(index, args[2]);
            this.println("true");
        } else {
            this.println(this.getList().add(args[1]));
        }
    }

    protected void handleListSet(String[] args) {
        int index = Integer.parseInt(args[1]);
        this.println(this.getList().set(index, args[2]));
    }

    protected void handleListAddMany(String[] args) {
        int count = 1;
        if (args.length > 1) {
            count = Integer.parseInt(args[1]);
        }
        int successCount = 0;
        long started = Clock.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            boolean success = this.getList().add("obj" + i);
            if (!success) continue;
            ++successCount;
        }
        long elapsedMillis = Clock.currentTimeMillis() - started;
        this.println("Added " + successCount + " objects.");
        if (elapsedMillis > 0L) {
            this.println("size = " + this.list.size() + ", " + TimeUnit.MILLISECONDS.toSeconds((long)successCount / elapsedMillis) + " evt/s");
        }
    }

    protected void handleMapPut(String[] args) {
        if (args.length == 1) {
            this.println("m.put requires a key and a value. You have not specified either.");
        } else if (args.length == 2) {
            this.println("m.put requires a key and a value. You have only specified the key " + args[1]);
        } else if (args.length > 3) {
            this.println("m.put takes two arguments, a key and a value. You have specified more than two arguments.");
        } else {
            this.println(this.getMap().put(args[1], args[2]));
        }
    }

    protected void handleMapPutAsync(String[] args) {
        try {
            this.println(this.getMap().putAsync((Object)args[1], (Object)args[2]).get());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected void handleMapPutIfAbsent(String[] args) {
        this.println(this.getMap().putIfAbsent(args[1], args[2]));
    }

    protected void handleMapReplace(String[] args) {
        this.println(this.getMap().replace(args[1], args[2]));
    }

    protected void handleMapGet(String[] args) {
        this.println(this.getMap().get(args[1]));
    }

    protected void handleMapGetAsync(String[] args) {
        try {
            this.println(this.getMap().getAsync((Object)args[1]).get());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected void handleMapGetMapEntry(String[] args) {
        this.println(this.getMap().getEntryView(args[1]));
    }

    protected void handleMapRemove(String[] args) {
        this.println(this.getMap().remove(args[1]));
    }

    protected void handleMapDelete(String[] args) {
        this.getMap().delete(args[1]);
        this.println("true");
    }

    protected void handleMapEvict(String[] args) {
        this.println(this.getMap().evict(args[1]));
    }

    protected void handleMapPutMany(String[] args) {
        int count = 1;
        if (args.length > 1) {
            count = Integer.parseInt(args[1]);
        }
        int b = 100;
        byte[] value = new byte[b];
        if (args.length > 2) {
            b = Integer.parseInt(args[2]);
            value = new byte[b];
        }
        int start = this.getMap().size();
        if (args.length > 3) {
            start = Integer.parseInt(args[3]);
        }
        Map<String, byte[]> theMap = MapUtil.createHashMap(count);
        for (int i = 0; i < count; ++i) {
            theMap.put("key" + (start + i), value);
        }
        long started = Clock.currentTimeMillis();
        this.getMap().putAll(theMap);
        long elapsedMillis = Clock.currentTimeMillis() - started;
        if (elapsedMillis > 0L) {
            long addedKiloBytes = (long)count * MemoryUnit.BYTES.toKiloBytes(b);
            this.println("size = " + this.getMap().size() + ", " + TimeUnit.MILLISECONDS.toSeconds((long)count / elapsedMillis) + " evt/s, " + TimeUnit.MILLISECONDS.toSeconds(addedKiloBytes * 8L / elapsedMillis) + " KBit/s, " + addedKiloBytes + " KB added");
        }
    }

    protected void handleMapGetMany(String[] args) {
        int count = 1;
        if (args.length > 1) {
            count = Integer.parseInt(args[1]);
        }
        for (int i = 0; i < count; ++i) {
            this.println(this.getMap().get("key" + i));
        }
    }

    protected void handleMapRemoveMany(String[] args) {
        int count = 1;
        if (args.length > 1) {
            count = Integer.parseInt(args[1]);
        }
        int start = 0;
        if (args.length > 2) {
            start = Integer.parseInt(args[2]);
        }
        long started = Clock.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            this.getMap().remove("key" + (start + i));
        }
        long elapsedMillis = Clock.currentTimeMillis() - started;
        if (elapsedMillis > 0L) {
            this.println("size = " + this.getMap().size() + ", " + TimeUnit.MILLISECONDS.toSeconds((long)count / elapsedMillis) + " evt/s");
        }
    }

    protected void handleMapLock(String[] args) {
        this.getMap().lock(args[1]);
        this.println("true");
    }

    protected void handleMapTryLock(String[] args) {
        boolean locked;
        long time;
        String key = args[1];
        long l = time = args.length > 2 ? Long.parseLong(args[2]) : 0L;
        if (time == 0L) {
            locked = this.getMap().tryLock(key);
        } else {
            try {
                locked = this.getMap().tryLock(key, time, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                locked = false;
            }
        }
        this.println(locked);
    }

    protected void handleMapUnlock(String[] args) {
        this.getMap().unlock(args[1]);
        this.println("true");
    }

    protected void handleMapLocalKeys() {
        Set<Object> set = this.getMap().localKeySet();
        Iterator<Object> it = set.iterator();
        int count = 0;
        while (it.hasNext()) {
            ++count;
            this.println(it.next());
        }
        this.println("Total " + count);
    }

    protected void handleMapLocalSize() {
        this.println("Local Size = " + this.getMap().localKeySet().size());
    }

    protected void handleMapKeys() {
        Set<Object> set = this.getMap().keySet();
        Iterator<Object> it = set.iterator();
        int count = 0;
        while (it.hasNext()) {
            ++count;
            this.println(it.next());
        }
        this.println("Total " + count);
    }

    protected void handleMapEntries() {
        Set<Map.Entry<Object, Object>> set = this.getMap().entrySet();
        Iterator<Map.Entry<Object, Object>> it = set.iterator();
        int count = 0;
        while (it.hasNext()) {
            ++count;
            Map.Entry<Object, Object> entry = it.next();
            this.println(entry.getKey() + ": " + entry.getValue());
        }
        this.println("Total " + count);
    }

    protected void handleMapValues() {
        Collection<Object> set = this.getMap().values();
        Iterator<Object> it = set.iterator();
        int count = 0;
        while (it.hasNext()) {
            ++count;
            this.println(it.next());
        }
        this.println("Total " + count);
    }

    protected void handleMultiMapPut(String[] args) {
        this.println(this.getMultiMap().put(args[1], args[2]));
    }

    protected void handleMultiMapGet(String[] args) {
        this.println(this.getMultiMap().get(args[1]));
    }

    protected void handleMultiMapRemove(String[] args) {
        this.println(this.getMultiMap().remove(args[1]));
    }

    protected void handleMultiMapKeys() {
        Set<Object> set = this.getMultiMap().keySet();
        Iterator<Object> it = set.iterator();
        int count = 0;
        while (it.hasNext()) {
            ++count;
            this.println(it.next());
        }
        this.println("Total " + count);
    }

    protected void handleMultiMapEntries() {
        Set<Map.Entry<Object, Object>> set = this.getMultiMap().entrySet();
        Iterator<Map.Entry<Object, Object>> it = set.iterator();
        int count = 0;
        while (it.hasNext()) {
            ++count;
            Map.Entry<Object, Object> entry = it.next();
            this.println(entry.getKey() + ": " + entry.getValue());
        }
        this.println("Total " + count);
    }

    protected void handleMultiMapValues() {
        Collection<Object> set = this.getMultiMap().values();
        Iterator<Object> it = set.iterator();
        int count = 0;
        while (it.hasNext()) {
            ++count;
            this.println(it.next());
        }
        this.println("Total " + count);
    }

    protected void handleMultiMapLock(String[] args) {
        this.getMultiMap().lock(args[1]);
        this.println("true");
    }

    protected void handleMultiMapTryLock(String[] args) {
        boolean locked;
        long time;
        String key = args[1];
        long l = time = args.length > 2 ? Long.parseLong(args[2]) : 0L;
        if (time == 0L) {
            locked = this.getMultiMap().tryLock(key);
        } else {
            try {
                locked = this.getMultiMap().tryLock(key, time, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                locked = false;
            }
        }
        this.println(locked);
    }

    protected void handleMultiMapUnlock(String[] args) {
        this.getMultiMap().unlock(args[1]);
        this.println("true");
    }

    private void handStats(String[] args) {
        String iteratorStr = args[0];
        if (iteratorStr.startsWith("m.")) {
            this.println(this.getMap().getLocalMapStats());
        } else if (iteratorStr.startsWith("mm.")) {
            this.println(this.getMultiMap().getLocalMultiMapStats());
        } else if (iteratorStr.startsWith("q.")) {
            this.println(this.getQueue().getLocalQueueStats());
        }
    }

    protected void handleLock(String[] args) {
        String lockStr = args[0];
        String key = args[1];
        ILock lock = this.hazelcast.getLock(key);
        if (StringUtil.equalsIgnoreCase(lockStr, "lock")) {
            lock.lock();
            this.println("true");
        } else if (StringUtil.equalsIgnoreCase(lockStr, "unlock")) {
            lock.unlock();
            this.println("true");
        } else if (StringUtil.equalsIgnoreCase(lockStr, "trylock")) {
            String timeout;
            String string = timeout = args.length > 2 ? args[2] : null;
            if (timeout == null) {
                this.println(lock.tryLock());
            } else {
                long time = Long.parseLong(timeout);
                try {
                    this.println(lock.tryLock(time, TimeUnit.SECONDS));
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }
    }

    protected void handleAddListener(String[] args) {
        String first = args[0];
        if (first.startsWith("s.")) {
            this.getSet().addItemListener(this, true);
        } else if (first.startsWith("m.")) {
            if (args.length > 1) {
                this.getMap().addEntryListener((EntryListener)this, (Object)args[1], true);
            } else {
                this.getMap().addEntryListener(this, true);
            }
        } else if (first.startsWith("mm.")) {
            if (args.length > 1) {
                this.getMultiMap().addEntryListener(this, args[1], true);
            } else {
                this.getMultiMap().addEntryListener(this, true);
            }
        } else if (first.startsWith("q.")) {
            this.getQueue().addItemListener(this, true);
        } else if (first.startsWith("t.")) {
            this.getTopic().addMessageListener(this);
        } else if (first.startsWith("l.")) {
            this.getList().addItemListener(this, true);
        }
    }

    protected void handleRemoveListener(String[] args) {
    }

    protected void handleSetAdd(String[] args) {
        this.println(this.getSet().add(args[1]));
    }

    protected void handleSetRemove(String[] args) {
        this.println(this.getSet().remove(args[1]));
    }

    protected void handleSetAddMany(String[] args) {
        int count = 1;
        if (args.length > 1) {
            count = Integer.parseInt(args[1]);
        }
        int successCount = 0;
        long started = Clock.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            boolean success = this.getSet().add("obj" + i);
            if (!success) continue;
            ++successCount;
        }
        long elapsedMillis = Clock.currentTimeMillis() - started;
        this.println("Added " + successCount + " objects.");
        if (elapsedMillis > 0L) {
            this.println("size = " + this.getSet().size() + ", " + TimeUnit.MILLISECONDS.toSeconds((long)successCount / elapsedMillis) + " evt/s");
        }
    }

    protected void handleSetRemoveMany(String[] args) {
        int count = 1;
        if (args.length > 1) {
            count = Integer.parseInt(args[1]);
        }
        int successCount = 0;
        long started = Clock.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            boolean success = this.getSet().remove("obj" + i);
            if (!success) continue;
            ++successCount;
        }
        long elapsedMillis = Clock.currentTimeMillis() - started;
        this.println("Removed " + successCount + " objects.");
        if (elapsedMillis > 0L) {
            this.println("size = " + this.getSet().size() + ", " + TimeUnit.MILLISECONDS.toSeconds((long)successCount / elapsedMillis) + " evt/s");
        }
    }

    protected void handleIterator(String[] args) {
        Iterator<Object> it = null;
        String iteratorStr = args[0];
        if (iteratorStr.startsWith("s.")) {
            it = this.getSet().iterator();
        } else if (iteratorStr.startsWith("m.")) {
            it = this.getMap().keySet().iterator();
        } else if (iteratorStr.startsWith("mm.")) {
            it = this.getMultiMap().keySet().iterator();
        } else if (iteratorStr.startsWith("q.")) {
            it = this.getQueue().iterator();
        } else if (iteratorStr.startsWith("l.")) {
            it = this.getList().iterator();
        }
        if (it != null) {
            boolean remove = false;
            if (args.length > 1) {
                String removeStr = args[1];
                remove = removeStr.equals("remove");
            }
            int count = 1;
            while (it.hasNext()) {
                this.print(count++ + " " + it.next());
                if (remove) {
                    it.remove();
                    this.print(" removed");
                }
                this.println("");
            }
        }
    }

    protected void handleContains(String[] args) {
        String iteratorStr = args[0];
        boolean key = StringUtil.lowerCaseInternal(iteratorStr).endsWith("key");
        String data = args[1];
        boolean result = false;
        if (iteratorStr.startsWith("s.")) {
            result = this.getSet().contains(data);
        } else if (iteratorStr.startsWith("m.")) {
            result = key ? this.getMap().containsKey(data) : this.getMap().containsValue(data);
        } else if (iteratorStr.startsWith("mmm.")) {
            result = key ? this.getMultiMap().containsKey(data) : this.getMultiMap().containsValue(data);
        } else if (iteratorStr.startsWith("q.")) {
            result = this.getQueue().contains(data);
        } else if (iteratorStr.startsWith("l.")) {
            result = this.getList().contains(data);
        }
        this.println("Contains: " + result);
    }

    protected void handleSize(String[] args) {
        int size = 0;
        String iteratorStr = args[0];
        if (iteratorStr.startsWith("s.")) {
            size = this.getSet().size();
        } else if (iteratorStr.startsWith("m.")) {
            size = this.getMap().size();
        } else if (iteratorStr.startsWith("mm.")) {
            size = this.getMultiMap().size();
        } else if (iteratorStr.startsWith("q.")) {
            size = this.getQueue().size();
        } else if (iteratorStr.startsWith("l.")) {
            size = this.getList().size();
        }
        this.println("Size: " + size);
    }

    protected void handleClear(String[] args) {
        String iteratorStr = args[0];
        if (iteratorStr.startsWith("s.")) {
            this.getSet().clear();
        } else if (iteratorStr.startsWith("m.")) {
            this.getMap().clear();
        } else if (iteratorStr.startsWith("mm.")) {
            this.getMultiMap().clear();
        } else if (iteratorStr.startsWith("q.")) {
            this.getQueue().clear();
        } else if (iteratorStr.startsWith("l.")) {
            this.getList().clear();
        }
        this.println("Cleared all.");
    }

    protected void handleDestroy(String[] args) {
        String iteratorStr = args[0];
        if (iteratorStr.startsWith("s.")) {
            this.getSet().destroy();
        } else if (iteratorStr.startsWith("m.")) {
            this.getMap().destroy();
        } else if (iteratorStr.startsWith("mm.")) {
            this.getMultiMap().destroy();
        } else if (iteratorStr.startsWith("q.")) {
            this.getQueue().destroy();
        } else if (iteratorStr.startsWith("l.")) {
            this.getList().destroy();
        } else if (iteratorStr.startsWith("t.")) {
            this.getTopic().destroy();
        }
        this.println("Destroyed!");
    }

    protected void handleQOffer(String[] args) {
        long timeout = 0L;
        if (args.length > 2) {
            timeout = Long.parseLong(args[2]);
        }
        try {
            boolean offered = this.getQueue().offer(args[1], timeout, TimeUnit.SECONDS);
            this.println(offered);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    protected void handleQTake(String[] args) {
        try {
            this.println(this.getQueue().take());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    protected void handleQPoll(String[] args) {
        long timeout = 0L;
        if (args.length > 1) {
            timeout = Long.parseLong(args[1]);
        }
        try {
            this.println(this.getQueue().poll(timeout, TimeUnit.SECONDS));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    protected void handleTopicPublish(String[] args) {
        this.getTopic().publish(args[1]);
    }

    protected void handleQOfferMany(String[] args) {
        int count = 1;
        if (args.length > 1) {
            count = Integer.parseInt(args[1]);
        }
        byte[] value = null;
        if (args.length > 2) {
            value = new byte[Integer.parseInt(args[2])];
        }
        long started = Clock.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            if (value == null) {
                this.getQueue().offer("obj");
                continue;
            }
            this.getQueue().offer(value);
        }
        long elapsedMillis = Clock.currentTimeMillis() - started;
        this.print("size = " + this.getQueue().size() + ", " + TimeUnit.MILLISECONDS.toSeconds((long)count / elapsedMillis) + " evt/s");
        if (value == null) {
            this.println("");
        } else if (elapsedMillis > 0L) {
            int b = Integer.parseInt(args[2]);
            long addedKiloBytes = (long)count * MemoryUnit.BYTES.toKiloBytes(b);
            this.println(", " + TimeUnit.MILLISECONDS.toSeconds(addedKiloBytes * 8L / elapsedMillis) + " KBit/s, " + addedKiloBytes + " KB added");
        }
    }

    protected void handleQPollMany(String[] args) {
        int count = 1;
        if (args.length > 1) {
            count = Integer.parseInt(args[1]);
        }
        int c = 1;
        for (int i = 0; i < count; ++i) {
            Object obj = this.getQueue().poll();
            if (obj instanceof byte[]) {
                this.println(c++ + " " + ((byte[])obj).length);
                continue;
            }
            this.println(c++ + " " + obj);
        }
    }

    protected void handleQPeek(String[] args) {
        this.println(this.getQueue().peek());
    }

    protected void handleQCapacity(String[] args) {
        this.println(this.getQueue().remainingCapacity());
    }

    private void execute(String[] args) {
        this.doExecute(false, false, args);
    }

    private void executeOnKey(String[] args) {
        this.doExecute(true, false, args);
    }

    private void executeOnMember(String[] args) {
        this.doExecute(false, true, args);
    }

    private void doExecute(boolean onKey, boolean onMember, String[] args) {
        try {
            Future<String> future;
            IExecutorService executorService = this.hazelcast.getExecutorService("default");
            Echo callable = new Echo(args[1]);
            if (onKey) {
                String key = args[2];
                future = executorService.submitToKeyOwner(callable, key);
            } else if (onMember) {
                LinkedList<Member> members;
                int memberIndex = Integer.parseInt(args[2]);
                if (memberIndex >= (members = new LinkedList<Member>(this.hazelcast.getCluster().getMembers())).size()) {
                    throw new IndexOutOfBoundsException("Member index: " + memberIndex + " must be smaller than " + members.size());
                }
                Member member = (Member)members.get(memberIndex);
                future = executorService.submitToMember(callable, member);
            } else {
                future = executorService.submit(callable);
            }
            this.println("Result: " + future.get());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void executeOnMembers(String[] args) {
        try {
            IExecutorService executorService = this.hazelcast.getExecutorService("default");
            Echo task = new Echo(args[1]);
            Map<Member, Future<String>> results = executorService.submitToAllMembers(task);
            for (Future<String> f : results.values()) {
                this.println(f.get());
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void entryAdded(EntryEvent event) {
        this.println(event);
    }

    @Override
    public void entryRemoved(EntryEvent event) {
        this.println(event);
    }

    @Override
    public void entryUpdated(EntryEvent event) {
        this.println(event);
    }

    @Override
    public void entryEvicted(EntryEvent event) {
        this.println(event);
    }

    @Override
    public void mapEvicted(MapEvent event) {
        this.println(event);
    }

    @Override
    public void mapCleared(MapEvent event) {
        this.println(event);
    }

    @Override
    public void itemAdded(ItemEvent itemEvent) {
        this.println("Item added = " + itemEvent.getItem());
    }

    @Override
    public void itemRemoved(ItemEvent itemEvent) {
        this.println("Item removed = " + itemEvent.getItem());
    }

    @Override
    public void onMessage(Message msg) {
        this.println("Topic received = " + msg.getMessageObject());
    }

    private void handleHelp(String command) {
        boolean silentBefore = this.silent;
        this.silent = false;
        this.println("Commands:");
        this.printGeneralCommands();
        this.printQueueCommands();
        this.printSetCommands();
        this.printLockCommands();
        this.printMapCommands();
        this.printMulitiMapCommands();
        this.printListCommands();
        this.printAtomicLongCommands();
        this.printExecutorServiceCommands();
        this.silent = silentBefore;
    }

    private void printGeneralCommands() {
        this.println("-- General commands");
        this.println("echo true|false                      //turns on/off echo of commands (default false)");
        this.println("silent true|false                    //turns on/off silent of command output (default false)");
        this.println("#<number> <command>                  //repeats <number> time <command>, replace $i in <command> with current iteration (0..<number-1>)");
        this.println("&<number> <command>                  //forks <number> threads to execute <command>, replace $t in <command> with current thread number (0..<number-1>");
        this.println("     When using #x or &x, is is advised to use silent true as well.");
        this.println("     When using &x with m.putmany and m.removemany, each thread will get a different share of keys unless a start key index is specified");
        this.println("jvm                                  //displays info about the runtime");
        this.println("who                                  //displays info about the cluster");
        this.println("whoami                               //displays info about this cluster member");
        this.println("ns <string>                          //switch the namespace for using the distributed queue/map/set/list <string> (defaults to \"default\"");
        this.println("@<file>                              //executes the given <file> script. Use '//' for comments in the script");
        this.println("");
    }

    private void printQueueCommands() {
        this.println("-- Queue commands");
        this.println("q.offer <string>                     //adds a string object to the queue");
        this.println("q.poll                               //takes an object from the queue");
        this.println("q.offermany <number> [<size>]        //adds indicated number of string objects to the queue ('obj<i>' or byte[<size>]) ");
        this.println("q.pollmany <number>                  //takes indicated number of objects from the queue");
        this.println("q.iterator [remove]                  //iterates the queue, remove if specified");
        this.println("q.size                               //size of the queue");
        this.println("q.clear                              //clears the queue");
        this.println("");
    }

    private void printSetCommands() {
        this.println("-- Set commands");
        this.println("s.add <string>                       //adds a string object to the set");
        this.println("s.remove <string>                    //removes the string object from the set");
        this.println("s.addmany <number>                   //adds indicated number of string objects to the set ('obj<i>')");
        this.println("s.removemany <number>                //takes indicated number of objects from the set");
        this.println("s.iterator [remove]                  //iterates the set, removes if specified");
        this.println("s.size                               //size of the set");
        this.println("s.clear                              //clears the set");
        this.println("");
    }

    private void printLockCommands() {
        this.println("-- Lock commands");
        this.println("lock <key>                           //same as Hazelcast.getLock(key).lock()");
        this.println("tryLock <key>                        //same as Hazelcast.getLock(key).tryLock()");
        this.println("tryLock <key> <time>                 //same as tryLock <key> with timeout in seconds");
        this.println("unlock <key>                         //same as Hazelcast.getLock(key).unlock()");
        this.println("");
    }

    private void printMapCommands() {
        this.println("-- Map commands");
        this.println("m.put <key> <value>                  //puts an entry to the map");
        this.println("m.remove <key>                       //removes the entry of given key from the map");
        this.println("m.get <key>                          //returns the value of given key from the map");
        this.println("m.putmany <number> [<size>] [<index>]//puts indicated number of entries to the map ('key<i>':byte[<size>], <index>+(0..<number>)");
        this.println("m.removemany <number> [<index>]      //removes indicated number of entries from the map ('key<i>', <index>+(0..<number>)");
        this.println("     When using &x with m.putmany and m.removemany, each thread will get a different share of keys unless a start key <index> is specified");
        this.println("m.keys                               //iterates the keys of the map");
        this.println("m.values                             //iterates the values of the map");
        this.println("m.entries                            //iterates the entries of the map");
        this.println("m.iterator [remove]                  //iterates the keys of the map, remove if specified");
        this.println("m.size                               //size of the map");
        this.println("m.localSize                          //local size of the map");
        this.println("m.clear                              //clears the map");
        this.println("m.destroy                            //destroys the map");
        this.println("m.lock <key>                         //locks the key");
        this.println("m.tryLock <key>                      //tries to lock the key and returns immediately");
        this.println("m.tryLock <key> <time>               //tries to lock the key within given seconds");
        this.println("m.unlock <key>                       //unlocks the key");
        this.println("m.stats                              //shows the local stats of the map");
        this.println("");
    }

    private void printMulitiMapCommands() {
        this.println("-- MultiMap commands");
        this.println("mm.put <key> <value>                  //puts an entry to the multimap");
        this.println("mm.get <key>                          //returns the value of given key from the multimap");
        this.println("mm.remove <key>                       //removes the entry of given key from the multimap");
        this.println("mm.size                               //size of the multimap");
        this.println("mm.clear                              //clears the multimap");
        this.println("mm.destroy                            //destroys the multimap");
        this.println("mm.iterator [remove]                  //iterates the keys of the multimap, remove if specified");
        this.println("mm.keys                               //iterates the keys of the multimap");
        this.println("mm.values                             //iterates the values of the multimap");
        this.println("mm.entries                            //iterates the entries of the multimap");
        this.println("mm.lock <key>                         //locks the key");
        this.println("mm.tryLock <key>                      //tries to lock the key and returns immediately");
        this.println("mm.tryLock <key> <time>               //tries to lock the key within given seconds");
        this.println("mm.unlock <key>                       //unlocks the key");
        this.println("mm.stats                              //shows the local stats of the multimap");
        this.println("");
    }

    private void printExecutorServiceCommands() {
        this.println("-- Executor Service commands:");
        this.println("execute <echo-input>                            //executes an echo task on random member");
        this.println("executeOnKey <echo-input> <key>                  //executes an echo task on the member that owns the given key");
        this.println("executeOnMember <echo-input> <memberIndex>         //executes an echo task on the member with given index");
        this.println("executeOnMembers <echo-input>                      //executes an echo task on all of the members");
        this.println("e<threadcount>.simulateLoad <task-count> <delaySeconds>        //simulates load on executor with given number of thread (e1..e16)");
        this.println("");
    }

    private void printAtomicLongCommands() {
        this.println("-- IAtomicLong commands:");
        this.println("a.get");
        this.println("a.set <long>");
        this.println("a.inc");
        this.println("a.dec");
        this.print("");
    }

    private void printListCommands() {
        this.println("-- List commands:");
        this.println("l.add <string>");
        this.println("l.add <index> <string>");
        this.println("l.contains <string>");
        this.println("l.remove <string>");
        this.println("l.remove <index>");
        this.println("l.set <index> <string>");
        this.println("l.iterator [remove]");
        this.println("l.size");
        this.println("l.clear");
        this.print("");
    }

    public void println(Object obj) {
        if (!this.silent) {
            System.out.println(obj);
        }
    }

    public void print(Object obj) {
        if (!this.silent) {
            System.out.print(obj);
        }
    }

    public static void main(String[] args) throws Exception {
        Config config;
        try {
            config = new FileSystemXmlConfig("hazelcast.xml");
        }
        catch (FileNotFoundException e) {
            config = new Config();
        }
        for (int i = 1; i <= 16; ++i) {
            config.addExecutorConfig(new ExecutorConfig("Sample Executor " + i).setPoolSize(i));
        }
        ConsoleApp consoleApp = new ConsoleApp(Hazelcast.newHazelcastInstance(config));
        consoleApp.start();
    }
}

