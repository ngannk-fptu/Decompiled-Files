/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.persistentmap;

import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PersistentMap<V>
extends AbstractMap<String, V>
implements Closeable {
    static final JSONCodec codec = new JSONCodec();
    final File dir;
    final File data;
    final RandomAccessFile lockFile;
    final Map<String, SoftReference<V>> cache = new HashMap<String, SoftReference<V>>();
    boolean inited = false;
    boolean closed = false;
    Type type;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PersistentMap(File dir, Type type) throws Exception {
        this.dir = dir;
        this.type = type;
        IO.mkdirs(dir);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("PersistentMap cannot create directory " + dir);
        }
        if (!dir.canWrite()) {
            throw new IllegalArgumentException("PersistentMap cannot write directory " + dir);
        }
        File f = new File(dir, "lock");
        this.lockFile = new RandomAccessFile(f, "rw");
        FileLock lock = this.lock();
        try {
            this.data = new File(dir, "data").getAbsoluteFile();
            IO.mkdirs(this.data);
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("PersistentMap cannot create data directory " + dir);
            }
            if (!this.data.canWrite()) {
                throw new IllegalArgumentException("PersistentMap cannot write data directory " + this.data);
            }
        }
        finally {
            this.unlock(lock);
        }
    }

    public PersistentMap(File dir, Class<V> type) throws Exception {
        this(dir, (Type)type);
    }

    public PersistentMap(File dir, Class<V> type, Map<String, V> map) throws Exception {
        this(dir, (Type)type);
        this.putAll(map);
    }

    public PersistentMap(File dir, Type type, Map<String, V> map) throws Exception {
        this(dir, type);
        this.putAll(map);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void init() {
        if (this.inited) {
            return;
        }
        if (this.closed) {
            throw new IllegalStateException("PersistentMap " + this.dir + " is already closed");
        }
        try {
            this.inited = true;
            FileLock lock = this.lock();
            try {
                for (File file : this.data.listFiles()) {
                    this.cache.put(file.getName(), null);
                }
            }
            finally {
                this.unlock(lock);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Map.Entry<String, V>> entrySet() {
        return new AbstractSet<Map.Entry<String, V>>(){

            @Override
            public int size() {
                PersistentMap.this.init();
                return PersistentMap.this.cache.size();
            }

            @Override
            public Iterator<Map.Entry<String, V>> iterator() {
                PersistentMap.this.init();
                return new Iterator<Map.Entry<String, V>>(){
                    Iterator<Map.Entry<String, SoftReference<V>>> it;
                    Map.Entry<String, SoftReference<V>> entry;
                    {
                        this.it = PersistentMap.this.cache.entrySet().iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    @Override
                    public Map.Entry<String, V> next() {
                        try {
                            this.entry = this.it.next();
                            SoftReference ref = this.entry.getValue();
                            Object value = null;
                            if (ref != null) {
                                value = ref.get();
                            }
                            if (value == null) {
                                File file = new File(PersistentMap.this.data, this.entry.getKey());
                                value = codec.dec().from(file).get(PersistentMap.this.type);
                                this.entry.setValue(new SoftReference<Object>(value));
                            }
                            final Object v = value;
                            return new Map.Entry<String, V>(){

                                @Override
                                public String getKey() {
                                    return entry.getKey();
                                }

                                @Override
                                public V getValue() {
                                    return v;
                                }

                                @Override
                                public V setValue(V value) {
                                    return PersistentMap.this.put(entry.getKey(), value);
                                }
                            };
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void remove() {
                        PersistentMap.this.remove(this.entry.getKey());
                    }
                };
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public V put(String key, V value) {
        this.init();
        try {
            V old = null;
            SoftReference<V> ref = this.cache.get(key);
            if (ref != null) {
                old = ref.get();
            }
            FileLock lock = this.lock();
            try {
                File file = new File(this.data, key);
                codec.enc().to(file).put(value);
                this.cache.put(key, new SoftReference<V>(value));
                V v = old;
                return v;
            }
            finally {
                this.unlock(lock);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FileLock lock() throws IOException, InterruptedException {
        int count = 400;
        while (true) {
            try {
                FileLock lock = this.lockFile.getChannel().lock();
                if (!lock.isValid()) {
                    System.err.println("Ouch, got invalid lock " + this.dir + " " + Thread.currentThread().getName());
                    return null;
                }
                return lock;
            }
            catch (OverlappingFileLockException e) {
                if (count-- > 0) {
                    TimeUnit.MILLISECONDS.sleep(5L);
                    continue;
                }
                throw new RuntimeException("Could not obtain lock");
            }
            break;
        }
    }

    private void unlock(FileLock lock) throws IOException {
        if (lock == null || !lock.isValid()) {
            System.err.println("Ouch, invalid lock was used " + this.dir + " " + Thread.currentThread().getName());
            return;
        }
        lock.release();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public V remove(String key) {
        try {
            this.init();
            FileLock lock = this.lock();
            try {
                File file = new File(this.data, key);
                IO.deleteWithException(file);
                V v = this.cache.remove(key).get();
                return v;
            }
            finally {
                this.unlock(lock);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        this.init();
        try {
            FileLock lock = this.lock();
            try {
                IO.deleteWithException(this.data);
                this.cache.clear();
                IO.mkdirs(this.data);
            }
            finally {
                this.unlock(lock);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> keySet() {
        this.init();
        return this.cache.keySet();
    }

    @Override
    public void close() throws IOException {
        this.lockFile.close();
        this.closed = true;
        this.inited = false;
    }

    @Override
    public String toString() {
        return "PersistentMap[" + this.dir + "] " + super.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear(long whenOlder) {
        this.init();
        try {
            FileLock lock = this.lock();
            try {
                for (File f : this.data.listFiles()) {
                    if (f.lastModified() >= whenOlder) continue;
                    IO.deleteWithException(f);
                }
                this.cache.clear();
            }
            finally {
                this.unlock(lock);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

