/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;

public final class ConstantUtf8
extends Constant {
    private static volatile int considered;
    private static volatile int created;
    private static volatile int hits;
    private static volatile int skipped;
    private static final String SYS_PROP_CACHE_MAX_ENTRIES = "bcel.maxcached";
    private static final String SYS_PROP_CACHE_MAX_ENTRY_SIZE = "bcel.maxcached.size";
    private static final String SYS_PROP_STATISTICS = "bcel.statistics";
    private final String value;

    public static synchronized void clearCache() {
        Cache.CACHE.clear();
    }

    static synchronized void clearStats() {
        created = 0;
        skipped = 0;
        considered = 0;
        hits = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ConstantUtf8 getCachedInstance(String value) {
        if (value.length() > Cache.MAX_ENTRY_SIZE) {
            ++skipped;
            return new ConstantUtf8(value);
        }
        ++considered;
        Class<ConstantUtf8> clazz = ConstantUtf8.class;
        synchronized (ConstantUtf8.class) {
            ConstantUtf8 result = (ConstantUtf8)Cache.CACHE.get(value);
            if (result != null) {
                ++hits;
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return result;
            }
            result = new ConstantUtf8(value);
            Cache.CACHE.put(value, result);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return result;
        }
    }

    public static ConstantUtf8 getInstance(DataInput dataInput) throws IOException {
        return ConstantUtf8.getInstance(dataInput.readUTF());
    }

    public static ConstantUtf8 getInstance(String value) {
        return Cache.isEnabled() ? ConstantUtf8.getCachedInstance(value) : new ConstantUtf8(value);
    }

    static void printStats() {
        String prefix = "[Apache Commons BCEL]";
        System.err.printf("%s Cache hit %,d/%,d, %d skipped.%n", "[Apache Commons BCEL]", hits, considered, skipped);
        System.err.printf("%s Total of %,d ConstantUtf8 objects created.%n", "[Apache Commons BCEL]", created);
        System.err.printf("%s Configuration: %s=%,d, %s=%,d.%n", "[Apache Commons BCEL]", SYS_PROP_CACHE_MAX_ENTRIES, Cache.MAX_ENTRIES, SYS_PROP_CACHE_MAX_ENTRY_SIZE, Cache.MAX_ENTRY_SIZE);
    }

    public ConstantUtf8(ConstantUtf8 constantUtf8) {
        this(constantUtf8.getBytes());
    }

    ConstantUtf8(DataInput dataInput) throws IOException {
        super((byte)1);
        this.value = dataInput.readUTF();
        ++created;
    }

    public ConstantUtf8(String value) {
        super((byte)1);
        this.value = Objects.requireNonNull(value, "value");
        ++created;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantUtf8(this);
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeUTF(this.value);
    }

    public String getBytes() {
        return this.value;
    }

    @Deprecated
    public void setBytes(String bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return super.toString() + "(\"" + Utility.replace(this.value, "\n", "\\n") + "\")";
    }

    static {
        if (Cache.BCEL_STATISTICS) {
            Runtime.getRuntime().addShutdownHook(new Thread(ConstantUtf8::printStats));
        }
    }

    private static class Cache {
        private static final boolean BCEL_STATISTICS = Boolean.getBoolean("bcel.statistics");
        private static final int MAX_ENTRIES = Integer.getInteger("bcel.maxcached", 0);
        private static final int INITIAL_CAPACITY = (int)((double)MAX_ENTRIES / 0.75);
        private static final HashMap<String, ConstantUtf8> CACHE = new LinkedHashMap<String, ConstantUtf8>(INITIAL_CAPACITY, 0.75f, true){
            private static final long serialVersionUID = -8506975356158971766L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ConstantUtf8> eldest) {
                return this.size() > MAX_ENTRIES;
            }
        };
        private static final int MAX_ENTRY_SIZE = Integer.getInteger("bcel.maxcached.size", 200);

        private Cache() {
        }

        static boolean isEnabled() {
            return MAX_ENTRIES > 0 && MAX_ENTRY_SIZE > 0;
        }
    }
}

