/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.Constants;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class RamUsageEstimator {
    public static final String JVM_INFO_STRING;
    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = 0x100000L;
    public static final long ONE_GB = 0x40000000L;
    public static final int NUM_BYTES_BOOLEAN = 1;
    public static final int NUM_BYTES_BYTE = 1;
    public static final int NUM_BYTES_CHAR = 2;
    public static final int NUM_BYTES_SHORT = 2;
    public static final int NUM_BYTES_INT = 4;
    public static final int NUM_BYTES_FLOAT = 4;
    public static final int NUM_BYTES_LONG = 8;
    public static final int NUM_BYTES_DOUBLE = 8;
    public static final int NUM_BYTES_OBJECT_REF;
    public static final int NUM_BYTES_OBJECT_HEADER;
    public static final int NUM_BYTES_ARRAY_HEADER;
    public static final int NUM_BYTES_OBJECT_ALIGNMENT;
    private static final Map<Class<?>, Integer> primitiveSizes;
    private static final Object theUnsafe;
    private static final Method objectFieldOffsetMethod;
    private static final EnumSet<JvmFeature> supportedFeatures;
    private final boolean checkInterned;

    public static boolean isSupportedJVM() {
        return supportedFeatures.size() == JvmFeature.values().length;
    }

    public static long alignObjectSize(long size) {
        return (size += (long)NUM_BYTES_OBJECT_ALIGNMENT - 1L) - size % (long)NUM_BYTES_OBJECT_ALIGNMENT;
    }

    public static long sizeOf(byte[] arr) {
        return RamUsageEstimator.alignObjectSize((long)NUM_BYTES_ARRAY_HEADER + (long)arr.length);
    }

    public static long sizeOf(boolean[] arr) {
        return RamUsageEstimator.alignObjectSize((long)NUM_BYTES_ARRAY_HEADER + (long)arr.length);
    }

    public static long sizeOf(char[] arr) {
        return RamUsageEstimator.alignObjectSize((long)NUM_BYTES_ARRAY_HEADER + 2L * (long)arr.length);
    }

    public static long sizeOf(short[] arr) {
        return RamUsageEstimator.alignObjectSize((long)NUM_BYTES_ARRAY_HEADER + 2L * (long)arr.length);
    }

    public static long sizeOf(int[] arr) {
        return RamUsageEstimator.alignObjectSize((long)NUM_BYTES_ARRAY_HEADER + 4L * (long)arr.length);
    }

    public static long sizeOf(float[] arr) {
        return RamUsageEstimator.alignObjectSize((long)NUM_BYTES_ARRAY_HEADER + 4L * (long)arr.length);
    }

    public static long sizeOf(long[] arr) {
        return RamUsageEstimator.alignObjectSize((long)NUM_BYTES_ARRAY_HEADER + 8L * (long)arr.length);
    }

    public static long sizeOf(double[] arr) {
        return RamUsageEstimator.alignObjectSize((long)NUM_BYTES_ARRAY_HEADER + 8L * (long)arr.length);
    }

    public static long sizeOf(Object obj) {
        return RamUsageEstimator.measureObjectSize(obj, false);
    }

    public static long shallowSizeOf(Object obj) {
        if (obj == null) {
            return 0L;
        }
        Class<?> clz = obj.getClass();
        if (clz.isArray()) {
            return RamUsageEstimator.shallowSizeOfArray(obj);
        }
        return RamUsageEstimator.shallowSizeOfInstance(clz);
    }

    public static long shallowSizeOfInstance(Class<?> clazz) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException("This method does not work with array classes.");
        }
        if (clazz.isPrimitive()) {
            return primitiveSizes.get(clazz).intValue();
        }
        long size = NUM_BYTES_OBJECT_HEADER;
        while (clazz != null) {
            Field[] fields;
            for (Field f : fields = clazz.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                size = RamUsageEstimator.adjustForField(size, f);
            }
            clazz = clazz.getSuperclass();
        }
        return RamUsageEstimator.alignObjectSize(size);
    }

    private static long shallowSizeOfArray(Object array) {
        long size = NUM_BYTES_ARRAY_HEADER;
        int len = Array.getLength(array);
        if (len > 0) {
            Class<?> arrayElementClazz = array.getClass().getComponentType();
            size = arrayElementClazz.isPrimitive() ? (size += (long)len * (long)primitiveSizes.get(arrayElementClazz).intValue()) : (size += (long)NUM_BYTES_OBJECT_REF * (long)len);
        }
        return RamUsageEstimator.alignObjectSize(size);
    }

    private static long measureObjectSize(Object root, boolean checkInterned) {
        IdentityHashSet<Object> seen = new IdentityHashSet<Object>();
        IdentityHashMap classCache = new IdentityHashMap();
        ArrayList<Object> stack = new ArrayList<Object>();
        stack.add(root);
        long totalSize = 0L;
        while (!stack.isEmpty()) {
            Object o;
            Object ob = stack.remove(stack.size() - 1);
            if (ob == null || seen.contains(ob)) continue;
            seen.add(ob);
            if (checkInterned && ob instanceof String && ob == ((String)ob).intern()) continue;
            Class<?> obClazz = ob.getClass();
            if (obClazz.isArray()) {
                long size = NUM_BYTES_ARRAY_HEADER;
                int len = Array.getLength(ob);
                if (len > 0) {
                    Class<?> componentClazz = obClazz.getComponentType();
                    if (componentClazz.isPrimitive()) {
                        size += (long)len * (long)primitiveSizes.get(componentClazz).intValue();
                    } else {
                        size += (long)NUM_BYTES_OBJECT_REF * (long)len;
                        int i = len;
                        while (--i >= 0) {
                            o = Array.get(ob, i);
                            if (o == null || seen.contains(o)) continue;
                            stack.add(o);
                        }
                    }
                }
                totalSize += RamUsageEstimator.alignObjectSize(size);
                continue;
            }
            try {
                ClassCache cachedInfo = (ClassCache)classCache.get(obClazz);
                if (cachedInfo == null) {
                    cachedInfo = RamUsageEstimator.createCacheEntry(obClazz);
                    classCache.put(obClazz, cachedInfo);
                }
                for (Field f : cachedInfo.referenceFields) {
                    o = f.get(ob);
                    if (o == null || seen.contains(o)) continue;
                    stack.add(o);
                }
                totalSize += cachedInfo.alignedShallowInstanceSize;
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Reflective field access failed?", e);
            }
        }
        seen.clear();
        stack.clear();
        classCache.clear();
        return totalSize;
    }

    private static ClassCache createCacheEntry(Class<?> clazz) {
        long shallowInstanceSize = NUM_BYTES_OBJECT_HEADER;
        ArrayList<Field> referenceFields = new ArrayList<Field>(32);
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Field[] fields;
            for (Field f : fields = c.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                shallowInstanceSize = RamUsageEstimator.adjustForField(shallowInstanceSize, f);
                if (f.getType().isPrimitive()) continue;
                f.setAccessible(true);
                referenceFields.add(f);
            }
        }
        ClassCache cachedInfo = new ClassCache(RamUsageEstimator.alignObjectSize(shallowInstanceSize), referenceFields.toArray(new Field[referenceFields.size()]));
        return cachedInfo;
    }

    private static long adjustForField(long sizeSoFar, Field f) {
        int fsize;
        Class<?> type = f.getType();
        int n = fsize = type.isPrimitive() ? primitiveSizes.get(type) : NUM_BYTES_OBJECT_REF;
        if (objectFieldOffsetMethod != null) {
            try {
                long offsetPlusSize = ((Number)objectFieldOffsetMethod.invoke(theUnsafe, f)).longValue() + (long)fsize;
                return Math.max(sizeSoFar, offsetPlusSize);
            }
            catch (IllegalAccessException ex) {
                throw new RuntimeException("Access problem with sun.misc.Unsafe", ex);
            }
            catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new RuntimeException("Call to Unsafe's objectFieldOffset() throwed checked Exception when accessing field " + f.getDeclaringClass().getName() + "#" + f.getName(), cause);
            }
        }
        return sizeSoFar + (long)fsize;
    }

    public static EnumSet<JvmFeature> getUnsupportedFeatures() {
        EnumSet<JvmFeature> unsupported = EnumSet.allOf(JvmFeature.class);
        unsupported.removeAll(supportedFeatures);
        return unsupported;
    }

    public static EnumSet<JvmFeature> getSupportedFeatures() {
        return EnumSet.copyOf(supportedFeatures);
    }

    public static String humanReadableUnits(long bytes) {
        return RamUsageEstimator.humanReadableUnits(bytes, new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ENGLISH)));
    }

    public static String humanReadableUnits(long bytes, DecimalFormat df) {
        if (bytes / 0x40000000L > 0L) {
            return df.format((float)bytes / 1.07374182E9f) + " GB";
        }
        if (bytes / 0x100000L > 0L) {
            return df.format((float)bytes / 1048576.0f) + " MB";
        }
        if (bytes / 1024L > 0L) {
            return df.format((float)bytes / 1024.0f) + " KB";
        }
        return bytes + " bytes";
    }

    public static String humanSizeOf(Object object) {
        return RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOf(object));
    }

    @Deprecated
    public RamUsageEstimator() {
        this(true);
    }

    @Deprecated
    public RamUsageEstimator(boolean checkInterned) {
        this.checkInterned = checkInterned;
    }

    @Deprecated
    public long estimateRamUsage(Object obj) {
        return RamUsageEstimator.measureObjectSize(obj, this.checkInterned);
    }

    static {
        primitiveSizes = new IdentityHashMap();
        primitiveSizes.put(Boolean.TYPE, 1);
        primitiveSizes.put(Byte.TYPE, 1);
        primitiveSizes.put(Character.TYPE, 2);
        primitiveSizes.put(Short.TYPE, 2);
        primitiveSizes.put(Integer.TYPE, 4);
        primitiveSizes.put(Float.TYPE, 4);
        primitiveSizes.put(Double.TYPE, 8);
        primitiveSizes.put(Long.TYPE, 8);
        int referenceSize = Constants.JRE_IS_64BIT ? 8 : 4;
        int objectHeader = Constants.JRE_IS_64BIT ? 16 : 8;
        int arrayHeader = Constants.JRE_IS_64BIT ? 24 : 12;
        supportedFeatures = EnumSet.noneOf(JvmFeature.class);
        Class<?> unsafeClass = null;
        Object tempTheUnsafe = null;
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            tempTheUnsafe = unsafeField.get(null);
        }
        catch (Exception e) {
            // empty catch block
        }
        theUnsafe = tempTheUnsafe;
        try {
            Method arrayIndexScaleM = unsafeClass.getMethod("arrayIndexScale", Class.class);
            referenceSize = ((Number)arrayIndexScaleM.invoke(theUnsafe, Object[].class)).intValue();
            supportedFeatures.add(JvmFeature.OBJECT_REFERENCE_SIZE);
        }
        catch (Exception e) {
            // empty catch block
        }
        objectHeader = Constants.JRE_IS_64BIT ? 8 + referenceSize : 8;
        arrayHeader = Constants.JRE_IS_64BIT ? 8 + 2 * referenceSize : 12;
        Method tempObjectFieldOffsetMethod = null;
        try {
            Method objectFieldOffsetM = unsafeClass.getMethod("objectFieldOffset", Field.class);
            Field dummy1Field = DummyTwoLongObject.class.getDeclaredField("dummy1");
            int ofs1 = ((Number)objectFieldOffsetM.invoke(theUnsafe, dummy1Field)).intValue();
            Field dummy2Field = DummyTwoLongObject.class.getDeclaredField("dummy2");
            int ofs2 = ((Number)objectFieldOffsetM.invoke(theUnsafe, dummy2Field)).intValue();
            if (Math.abs(ofs2 - ofs1) == 8) {
                Field baseField = DummyOneFieldObject.class.getDeclaredField("base");
                objectHeader = ((Number)objectFieldOffsetM.invoke(theUnsafe, baseField)).intValue();
                supportedFeatures.add(JvmFeature.FIELD_OFFSETS);
                tempObjectFieldOffsetMethod = objectFieldOffsetM;
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        objectFieldOffsetMethod = tempObjectFieldOffsetMethod;
        try {
            Method arrayBaseOffsetM = unsafeClass.getMethod("arrayBaseOffset", Class.class);
            arrayHeader = ((Number)arrayBaseOffsetM.invoke(theUnsafe, byte[].class)).intValue();
            supportedFeatures.add(JvmFeature.ARRAY_HEADER_SIZE);
        }
        catch (Exception e) {
            // empty catch block
        }
        NUM_BYTES_OBJECT_REF = referenceSize;
        NUM_BYTES_OBJECT_HEADER = objectHeader;
        NUM_BYTES_ARRAY_HEADER = arrayHeader;
        int objectAlignment = 8;
        try {
            Class<?> beanClazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
            Object hotSpotBean = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(), "com.sun.management:type=HotSpotDiagnostic", beanClazz);
            Method getVMOptionMethod = beanClazz.getMethod("getVMOption", String.class);
            Object vmOption = getVMOptionMethod.invoke(hotSpotBean, "ObjectAlignmentInBytes");
            objectAlignment = Integer.parseInt(vmOption.getClass().getMethod("getValue", new Class[0]).invoke(vmOption, new Object[0]).toString());
            supportedFeatures.add(JvmFeature.OBJECT_ALIGNMENT);
        }
        catch (Exception e) {
            // empty catch block
        }
        NUM_BYTES_OBJECT_ALIGNMENT = objectAlignment;
        JVM_INFO_STRING = "[JVM: " + Constants.JVM_NAME + ", " + Constants.JVM_VERSION + ", " + Constants.JVM_VENDOR + ", " + Constants.JAVA_VENDOR + ", " + Constants.JAVA_VERSION + "]";
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class IdentityHashSet<KType>
    implements Iterable<KType> {
        public static final float DEFAULT_LOAD_FACTOR = 0.75f;
        public static final int MIN_CAPACITY = 4;
        public Object[] keys;
        public int assigned;
        public final float loadFactor;
        private int resizeThreshold;

        public IdentityHashSet() {
            this(16, 0.75f);
        }

        public IdentityHashSet(int initialCapacity) {
            this(initialCapacity, 0.75f);
        }

        public IdentityHashSet(int initialCapacity, float loadFactor) {
            initialCapacity = Math.max(4, initialCapacity);
            assert (initialCapacity > 0) : "Initial capacity must be between (0, 2147483647].";
            assert (loadFactor > 0.0f && loadFactor < 1.0f) : "Load factor must be between (0, 1).";
            this.loadFactor = loadFactor;
            this.allocateBuffers(this.roundCapacity(initialCapacity));
        }

        public boolean add(KType e) {
            Object existing;
            assert (e != null) : "Null keys not allowed.";
            if (this.assigned >= this.resizeThreshold) {
                this.expandAndRehash();
            }
            int mask = this.keys.length - 1;
            int slot = IdentityHashSet.rehash(e) & mask;
            while ((existing = this.keys[slot]) != null) {
                if (e == existing) {
                    return false;
                }
                slot = slot + 1 & mask;
            }
            ++this.assigned;
            this.keys[slot] = e;
            return true;
        }

        public boolean contains(KType e) {
            Object existing;
            int mask = this.keys.length - 1;
            int slot = IdentityHashSet.rehash(e) & mask;
            while ((existing = this.keys[slot]) != null) {
                if (e == existing) {
                    return true;
                }
                slot = slot + 1 & mask;
            }
            return false;
        }

        private static int rehash(Object o) {
            int k = System.identityHashCode(o);
            k ^= k >>> 16;
            k *= -2048144789;
            k ^= k >>> 13;
            k *= -1028477387;
            k ^= k >>> 16;
            return k;
        }

        private void expandAndRehash() {
            Object[] oldKeys = this.keys;
            assert (this.assigned >= this.resizeThreshold);
            this.allocateBuffers(this.nextCapacity(this.keys.length));
            int mask = this.keys.length - 1;
            for (int i = 0; i < oldKeys.length; ++i) {
                Object key = oldKeys[i];
                if (key == null) continue;
                int slot = IdentityHashSet.rehash(key) & mask;
                while (this.keys[slot] != null) {
                    slot = slot + 1 & mask;
                }
                this.keys[slot] = key;
            }
            Arrays.fill(oldKeys, null);
        }

        private void allocateBuffers(int capacity) {
            this.keys = new Object[capacity];
            this.resizeThreshold = (int)((float)capacity * 0.75f);
        }

        protected int nextCapacity(int current) {
            assert (current > 0 && Long.bitCount(current) == 1) : "Capacity must be a power of two.";
            assert (current << 1 > 0) : "Maximum capacity exceeded (1073741824).";
            if (current < 2) {
                current = 2;
            }
            return current << 1;
        }

        protected int roundCapacity(int requestedCapacity) {
            int capacity;
            if (requestedCapacity > 0x40000000) {
                return 0x40000000;
            }
            for (capacity = 4; capacity < requestedCapacity; capacity <<= 1) {
            }
            return capacity;
        }

        public void clear() {
            this.assigned = 0;
            Arrays.fill(this.keys, null);
        }

        public int size() {
            return this.assigned;
        }

        public boolean isEmpty() {
            return this.size() == 0;
        }

        @Override
        public Iterator<KType> iterator() {
            return new Iterator<KType>(){
                int pos = -1;
                Object nextElement = this.fetchNext();

                @Override
                public boolean hasNext() {
                    return this.nextElement != null;
                }

                @Override
                public KType next() {
                    Object r = this.nextElement;
                    if (r == null) {
                        throw new NoSuchElementException();
                    }
                    this.nextElement = this.fetchNext();
                    return r;
                }

                private Object fetchNext() {
                    ++this.pos;
                    while (this.pos < IdentityHashSet.this.keys.length && IdentityHashSet.this.keys[this.pos] == null) {
                        ++this.pos;
                    }
                    return this.pos >= IdentityHashSet.this.keys.length ? null : IdentityHashSet.this.keys[this.pos];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    private static final class DummyTwoLongObject {
        public long dummy1;
        public long dummy2;

        private DummyTwoLongObject() {
        }
    }

    private static final class DummyOneFieldObject {
        public byte base;

        private DummyOneFieldObject() {
        }
    }

    private static final class ClassCache {
        public final long alignedShallowInstanceSize;
        public final Field[] referenceFields;

        public ClassCache(long alignedShallowInstanceSize, Field[] referenceFields) {
            this.alignedShallowInstanceSize = alignedShallowInstanceSize;
            this.referenceFields = referenceFields;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum JvmFeature {
        OBJECT_REFERENCE_SIZE("Object reference size estimated using array index scale"),
        ARRAY_HEADER_SIZE("Array header size estimated using array based offset"),
        FIELD_OFFSETS("Shallow instance size based on field offsets"),
        OBJECT_ALIGNMENT("Object alignment retrieved from HotSpotDiagnostic MX bean");

        public final String description;

        private JvmFeature(String description) {
            this.description = description;
        }

        public String toString() {
            return super.name() + " (" + this.description + ")";
        }
    }
}

