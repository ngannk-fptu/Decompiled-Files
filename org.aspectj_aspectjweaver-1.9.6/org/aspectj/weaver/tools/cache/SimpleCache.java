/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import org.aspectj.weaver.Dump;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public class SimpleCache {
    private static final String SAME_BYTES_STRING = "IDEM";
    private static final byte[] SAME_BYTES = "IDEM".getBytes();
    private Map<String, byte[]> cacheMap;
    private boolean enabled = false;
    private Map<String, byte[]> generatedCache;
    private static final String GENERATED_CACHE_SUBFOLDER = "panenka.cache";
    private static final String GENERATED_CACHE_SEPARATOR = ";";
    public static final String IMPL_NAME = "shared";
    private Method defineClassMethod = null;
    private Method defineClassWithProtectionDomainMethod = null;

    protected SimpleCache(String folder, boolean enabled) {
        this.enabled = enabled;
        this.cacheMap = Collections.synchronizedMap(StoreableCachingMap.init(folder));
        if (enabled) {
            String generatedCachePath = folder + File.separator + GENERATED_CACHE_SUBFOLDER;
            File f = new File(generatedCachePath);
            if (!f.exists()) {
                f.mkdir();
            }
            this.generatedCache = Collections.synchronizedMap(StoreableCachingMap.init(generatedCachePath, 0));
        }
    }

    public byte[] getAndInitialize(String classname, byte[] bytes, ClassLoader loader, ProtectionDomain protectionDomain) {
        if (!this.enabled) {
            return null;
        }
        byte[] res = this.get(classname, bytes);
        if (Arrays.equals(SAME_BYTES, res)) {
            return bytes;
        }
        if (res != null) {
            this.initializeClass(classname, res, loader, protectionDomain);
        }
        return res;
    }

    private byte[] get(String classname, byte[] bytes) {
        String key = this.generateKey(classname, bytes);
        byte[] res = this.cacheMap.get(key);
        return res;
    }

    public void put(String classname, byte[] origbytes, byte[] wovenbytes) {
        if (!this.enabled) {
            return;
        }
        String key = this.generateKey(classname, origbytes);
        if (Arrays.equals(origbytes, wovenbytes)) {
            this.cacheMap.put(key, SAME_BYTES);
            return;
        }
        this.cacheMap.put(key, wovenbytes);
    }

    private String generateKey(String classname, byte[] bytes) {
        CRC32 checksum = new CRC32();
        checksum.update(bytes);
        long crc = checksum.getValue();
        classname = classname.replace("/", ".");
        return new String(classname + "-" + crc);
    }

    private void initializeClass(String className, byte[] bytes, ClassLoader loader, ProtectionDomain protectionDomain) {
        String[] generatedClassesNames = this.getGeneratedClassesNames(className, bytes);
        if (generatedClassesNames == null) {
            return;
        }
        for (String generatedClassName : generatedClassesNames) {
            byte[] generatedBytes = this.get(generatedClassName, bytes);
            if (protectionDomain == null) {
                this.defineClass(loader, generatedClassName, generatedBytes);
                continue;
            }
            this.defineClass(loader, generatedClassName, generatedBytes, protectionDomain);
        }
    }

    private String[] getGeneratedClassesNames(String className, byte[] bytes) {
        String key = this.generateKey(className, bytes);
        byte[] readBytes = this.generatedCache.get(key);
        if (readBytes == null) {
            return null;
        }
        String readString = new String(readBytes);
        return readString.split(GENERATED_CACHE_SEPARATOR);
    }

    public void addGeneratedClassesNames(String parentClassName, byte[] parentBytes, String generatedClassName) {
        if (!this.enabled) {
            return;
        }
        String key = this.generateKey(parentClassName, parentBytes);
        byte[] storedBytes = this.generatedCache.get(key);
        if (storedBytes == null) {
            this.generatedCache.put(key, generatedClassName.getBytes());
        } else {
            String storedClasses = new String(storedBytes);
            storedClasses = storedClasses + GENERATED_CACHE_SEPARATOR + generatedClassName;
            this.generatedCache.put(key, storedClasses.getBytes());
        }
    }

    private void defineClass(ClassLoader loader, String name, byte[] bytes) {
        Object clazz = null;
        try {
            if (this.defineClassMethod == null) {
                this.defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, bytes.getClass(), Integer.TYPE, Integer.TYPE);
            }
            this.defineClassMethod.setAccessible(true);
            clazz = this.defineClassMethod.invoke((Object)loader, name, bytes, new Integer(0), new Integer(bytes.length));
        }
        catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof LinkageError) {
                e.printStackTrace();
            } else {
                System.out.println("define generated class failed" + e.getTargetException());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Dump.dumpWithException(e);
        }
    }

    private void defineClass(ClassLoader loader, String name, byte[] bytes, ProtectionDomain protectionDomain) {
        Object clazz = null;
        try {
            if (this.defineClassWithProtectionDomainMethod == null) {
                this.defineClassWithProtectionDomainMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, bytes.getClass(), Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
            }
            this.defineClassWithProtectionDomainMethod.setAccessible(true);
            clazz = this.defineClassWithProtectionDomainMethod.invoke((Object)loader, name, bytes, 0, new Integer(bytes.length), protectionDomain);
        }
        catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof LinkageError) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
        catch (NullPointerException e) {
            System.out.println("NullPointerException loading class:" + name + ".  Probabily caused by a corruput cache. Please clean it and reboot the server");
        }
        catch (Exception e) {
            e.printStackTrace();
            Dump.dumpWithException(e);
        }
    }

    private static class StoreableCachingMap
    extends HashMap {
        private String folder;
        private static final String CACHENAMEIDX = "cache.idx";
        private long lastStored = System.currentTimeMillis();
        private static int DEF_STORING_TIMER = 60000;
        private int storingTimer;
        private transient Trace trace;

        private void initTrace() {
            this.trace = TraceFactory.getTraceFactory().getTrace(StoreableCachingMap.class);
        }

        private StoreableCachingMap(String folder, int storingTimer) {
            this.folder = folder;
            this.initTrace();
            this.storingTimer = storingTimer;
        }

        public static StoreableCachingMap init(String folder) {
            return StoreableCachingMap.init(folder, DEF_STORING_TIMER);
        }

        public static StoreableCachingMap init(String folder, int storingTimer) {
            File file = new File(folder + File.separator + CACHENAMEIDX);
            if (file.exists()) {
                try {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                    StoreableCachingMap sm = (StoreableCachingMap)in.readObject();
                    sm.initTrace();
                    in.close();
                    return sm;
                }
                catch (Exception e) {
                    Trace trace = TraceFactory.getTraceFactory().getTrace(StoreableCachingMap.class);
                    trace.error("Error reading Storable Cache", e);
                }
            }
            return new StoreableCachingMap(folder, storingTimer);
        }

        @Override
        public Object get(Object obj) {
            try {
                if (super.containsKey(obj)) {
                    String path = (String)super.get(obj);
                    if (path.equals(SimpleCache.SAME_BYTES_STRING)) {
                        return SAME_BYTES;
                    }
                    return this.readFromPath(path);
                }
                return null;
            }
            catch (IOException e) {
                this.trace.error("Error reading key:" + obj.toString(), e);
                Dump.dumpWithException(e);
                return null;
            }
        }

        @Override
        public Object put(Object key, Object value) {
            try {
                String path = null;
                byte[] valueBytes = (byte[])value;
                path = Arrays.equals(valueBytes, SAME_BYTES) ? SimpleCache.SAME_BYTES_STRING : this.writeToPath((String)key, valueBytes);
                String result = super.put(key, path);
                this.storeMap();
                return result;
            }
            catch (IOException e) {
                this.trace.error("Error inserting in cache: key:" + key.toString() + "; value:" + value.toString(), e);
                Dump.dumpWithException(e);
                return null;
            }
        }

        public void storeMap() {
            long now = System.currentTimeMillis();
            if (now - this.lastStored < (long)this.storingTimer) {
                return;
            }
            File file = new File(this.folder + File.separator + CACHENAMEIDX);
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(this);
                out.close();
                this.lastStored = now;
            }
            catch (Exception e) {
                this.trace.error("Error storing cache; cache file:" + file.getAbsolutePath(), e);
                Dump.dumpWithException(e);
            }
        }

        private byte[] readFromPath(String fullPath) throws IOException {
            int nRead;
            FileInputStream is = null;
            try {
                is = new FileInputStream(fullPath);
            }
            catch (FileNotFoundException e) {
                System.out.println("FileNotFoundExceptions: The aspectj cache is corrupt. Please clean it and reboot the server. Cache path:" + this.folder);
                e.printStackTrace();
                return null;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[16384];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            is.close();
            return buffer.toByteArray();
        }

        private String writeToPath(String key, byte[] bytes) throws IOException {
            String fullPath = this.folder + File.separator + key;
            FileOutputStream fos = new FileOutputStream(fullPath);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return fullPath;
        }
    }
}

