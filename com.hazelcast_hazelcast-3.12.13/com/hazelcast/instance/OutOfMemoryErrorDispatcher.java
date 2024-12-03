/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.OutOfMemoryHandler;
import com.hazelcast.instance.DefaultOutOfMemoryHandler;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@PrivateApi
public final class OutOfMemoryErrorDispatcher {
    private static final int MAX_REGISTERED_INSTANCES = 50;
    private static final HazelcastInstance[] EMPTY_INSTANCES = new HazelcastInstance[0];
    private static final AtomicReference<HazelcastInstance[]> SERVER_INSTANCES_REF = new AtomicReference<HazelcastInstance[]>(EMPTY_INSTANCES);
    private static final AtomicReference<HazelcastInstance[]> CLIENT_INSTANCES_REF = new AtomicReference<HazelcastInstance[]>(EMPTY_INSTANCES);
    private static volatile OutOfMemoryHandler handler = new DefaultOutOfMemoryHandler();
    private static volatile OutOfMemoryHandler clientHandler = new EmptyOutOfMemoryHandler();
    private static final AtomicInteger OUT_OF_MEMORY_ERROR_COUNT = new AtomicInteger();

    private OutOfMemoryErrorDispatcher() {
    }

    static HazelcastInstance[] current() {
        return SERVER_INSTANCES_REF.get();
    }

    public static int getOutOfMemoryErrorCount() {
        return OUT_OF_MEMORY_ERROR_COUNT.get();
    }

    public static void setServerHandler(OutOfMemoryHandler outOfMemoryHandler) {
        handler = outOfMemoryHandler;
    }

    public static void setClientHandler(OutOfMemoryHandler outOfMemoryHandler) {
        clientHandler = outOfMemoryHandler;
    }

    public static void registerServer(HazelcastInstance instance) {
        OutOfMemoryErrorDispatcher.register(SERVER_INSTANCES_REF, instance);
    }

    public static void registerClient(HazelcastInstance instance) {
        OutOfMemoryErrorDispatcher.register(CLIENT_INSTANCES_REF, instance);
    }

    private static void register(AtomicReference<HazelcastInstance[]> ref, HazelcastInstance instance) {
        HazelcastInstance[] newInstances;
        HazelcastInstance[] oldInstances;
        Preconditions.isNotNull(instance, "instance");
        do {
            if ((oldInstances = ref.get()).length == 50) {
                return;
            }
            newInstances = new HazelcastInstance[oldInstances.length + 1];
            System.arraycopy(oldInstances, 0, newInstances, 0, oldInstances.length);
            newInstances[oldInstances.length] = instance;
        } while (!ref.compareAndSet(oldInstances, newInstances));
    }

    public static void deregisterServer(HazelcastInstance instance) {
        OutOfMemoryErrorDispatcher.deregister(SERVER_INSTANCES_REF, instance);
    }

    public static void deregisterClient(HazelcastInstance instance) {
        OutOfMemoryErrorDispatcher.deregister(CLIENT_INSTANCES_REF, instance);
    }

    private static void deregister(AtomicReference<HazelcastInstance[]> ref, HazelcastInstance instance) {
        HazelcastInstance[] newInstances;
        HazelcastInstance[] oldInstances;
        Preconditions.isNotNull(instance, "instance");
        do {
            int indexOf;
            if ((indexOf = OutOfMemoryErrorDispatcher.indexOf(oldInstances = ref.get(), instance)) == -1) {
                return;
            }
            if (oldInstances.length == 1) {
                newInstances = EMPTY_INSTANCES;
                continue;
            }
            newInstances = new HazelcastInstance[oldInstances.length - 1];
            System.arraycopy(oldInstances, 0, newInstances, 0, indexOf);
            if (indexOf >= newInstances.length) continue;
            System.arraycopy(oldInstances, indexOf + 1, newInstances, indexOf, newInstances.length - indexOf);
        } while (!ref.compareAndSet(oldInstances, newInstances));
    }

    private static int indexOf(HazelcastInstance[] instances, HazelcastInstance instance) {
        for (int k = 0; k < instances.length; ++k) {
            if (instance != instances[k]) continue;
            return k;
        }
        return -1;
    }

    public static void clearServers() {
        SERVER_INSTANCES_REF.set(EMPTY_INSTANCES);
    }

    public static void clearClients() {
        CLIENT_INSTANCES_REF.set(EMPTY_INSTANCES);
    }

    public static void inspectOutOfMemoryError(Throwable throwable) {
        if (throwable == null) {
            return;
        }
        if (throwable instanceof OutOfMemoryError) {
            OutOfMemoryErrorDispatcher.onOutOfMemory((OutOfMemoryError)throwable);
        }
    }

    public static void onOutOfMemory(OutOfMemoryError outOfMemoryError) {
        Preconditions.isNotNull(outOfMemoryError, "outOfMemoryError");
        OUT_OF_MEMORY_ERROR_COUNT.incrementAndGet();
        OutOfMemoryHandler h = clientHandler;
        if (h != null && h.shouldHandle(outOfMemoryError)) {
            try {
                HazelcastInstance[] clients = OutOfMemoryErrorDispatcher.removeRegisteredClients();
                h.onOutOfMemory(outOfMemoryError, clients);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
        if ((h = handler) != null && h.shouldHandle(outOfMemoryError)) {
            try {
                HazelcastInstance[] instances = OutOfMemoryErrorDispatcher.removeRegisteredServers();
                h.onOutOfMemory(outOfMemoryError, instances);
            }
            catch (Throwable ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
    }

    private static HazelcastInstance[] removeRegisteredServers() {
        return OutOfMemoryErrorDispatcher.removeRegisteredInstances(SERVER_INSTANCES_REF);
    }

    private static HazelcastInstance[] removeRegisteredClients() {
        return OutOfMemoryErrorDispatcher.removeRegisteredInstances(CLIENT_INSTANCES_REF);
    }

    private static HazelcastInstance[] removeRegisteredInstances(AtomicReference<HazelcastInstance[]> ref) {
        HazelcastInstance[] instances;
        while (!ref.compareAndSet(instances = ref.get(), EMPTY_INSTANCES)) {
        }
        return instances;
    }

    private static class EmptyOutOfMemoryHandler
    extends OutOfMemoryHandler {
        private EmptyOutOfMemoryHandler() {
        }

        @Override
        public void onOutOfMemory(OutOfMemoryError oome, HazelcastInstance[] hazelcastInstances) {
        }

        @Override
        public boolean shouldHandle(OutOfMemoryError oome) {
            return false;
        }
    }
}

