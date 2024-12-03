/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

import com.hazelcast.instance.Node;
import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.memcache.Stats;
import java.util.Map;
import java.util.Set;

public interface TextCommandService {
    public boolean offer(String var1, Object var2);

    public Object poll(String var1, int var2);

    public Object poll(String var1);

    public void processRequest(TextCommand var1);

    public void sendResponse(TextCommand var1);

    public Object get(String var1, String var2);

    public Map<String, Object> getAll(String var1, Set<String> var2);

    public byte[] getByteArray(String var1, String var2);

    public Object put(String var1, String var2, Object var3);

    public Object put(String var1, String var2, Object var3, int var4);

    public Object putIfAbsent(String var1, String var2, Object var3, int var4);

    public Object replace(String var1, String var2, Object var3);

    public void lock(String var1, String var2) throws InterruptedException;

    public void unlock(String var1, String var2);

    public int getAdjustedTTLSeconds(int var1);

    public long incrementDeleteHitCount(int var1);

    public long incrementDeleteMissCount();

    public long incrementGetHitCount();

    public long incrementGetMissCount();

    public long incrementSetCount();

    public long incrementIncHitCount();

    public long incrementIncMissCount();

    public long incrementDecrHitCount();

    public long incrementDecrMissCount();

    public long incrementTouchCount();

    public int size(String var1);

    public Object delete(String var1, String var2);

    public void deleteAll(String var1);

    public Stats getStats();

    public Node getNode();

    public byte[] toByteArray(Object var1);

    public void stop();
}

