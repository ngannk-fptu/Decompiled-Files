/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.StringCodec;
import com.thoughtworks.xstream.core.util.Base64Encoder;
import com.thoughtworks.xstream.core.util.CustomObjectOutputStream;
import com.thoughtworks.xstream.core.util.DependencyInjectionFactory;
import com.thoughtworks.xstream.core.util.PresortedMap;
import com.thoughtworks.xstream.core.util.PresortedSet;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.AttributedString;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

public class JVM
implements Caching {
    private ReflectionProvider reflectionProvider;
    private static final boolean isAWTAvailable;
    private static final boolean isSwingAvailable;
    private static final boolean isSQLAvailable;
    private static final boolean canAllocateWithUnsafe;
    private static final boolean canWriteWithUnsafe;
    private static final boolean optimizedTreeSetAddAll;
    private static final boolean optimizedTreeMapPutAll;
    private static final boolean canParseUTCDateFormat;
    private static final boolean canParseISO8601TimeZoneInDateFormat;
    private static final boolean canCreateDerivedObjectOutputStream;
    private static final String vendor;
    private static final float majorJavaVersion;
    private static final float DEFAULT_JAVA_VERSION = 1.4f;
    private static final boolean reverseFieldOrder = false;
    private static final Class reflectionProviderType;
    private static final StringCodec base64Codec;

    private static final float getMajorJavaVersion() {
        try {
            return JVM.isAndroid() ? 1.5f : Float.parseFloat(System.getProperty("java.specification.version"));
        }
        catch (NumberFormatException e) {
            return 1.4f;
        }
    }

    public static boolean is14() {
        return JVM.isVersion(4);
    }

    public static boolean is15() {
        return JVM.isVersion(5);
    }

    public static boolean is16() {
        return JVM.isVersion(6);
    }

    public static boolean is17() {
        return JVM.isVersion(7);
    }

    public static boolean is18() {
        return JVM.isVersion(8);
    }

    public static boolean is19() {
        return majorJavaVersion >= 1.9f;
    }

    public static boolean is9() {
        return JVM.isVersion(9);
    }

    public static boolean isVersion(int version) {
        if (version < 1) {
            throw new IllegalArgumentException("Java version range starts with at least 1.");
        }
        float v = majorJavaVersion < 9.0f ? 1.0f + (float)version * 0.1f : (float)version;
        return majorJavaVersion >= v;
    }

    private static boolean isIBM() {
        return vendor.indexOf("IBM") != -1;
    }

    private static boolean isAndroid() {
        return vendor.indexOf("Android") != -1;
    }

    public static Class loadClassForName(String name) {
        return JVM.loadClassForName(name, true);
    }

    public Class loadClass(String name) {
        return JVM.loadClassForName(name, true);
    }

    public static Class loadClassForName(String name, boolean initialize) {
        try {
            Class<?> clazz = Class.forName(name, initialize, JVM.class.getClassLoader());
            return clazz;
        }
        catch (LinkageError e) {
            return null;
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    public Class loadClass(String name, boolean initialize) {
        return JVM.loadClassForName(name, initialize);
    }

    public static ReflectionProvider newReflectionProvider() {
        return (ReflectionProvider)DependencyInjectionFactory.newInstance(reflectionProviderType, null);
    }

    public static ReflectionProvider newReflectionProvider(FieldDictionary dictionary) {
        return (ReflectionProvider)DependencyInjectionFactory.newInstance(reflectionProviderType, new Object[]{dictionary});
    }

    public static Class getStaxInputFactory() throws ClassNotFoundException {
        if (JVM.isVersion(6)) {
            if (JVM.isIBM()) {
                return Class.forName("com.ibm.xml.xlxp.api.stax.XMLInputFactoryImpl");
            }
            return Class.forName("com.sun.xml.internal.stream.XMLInputFactoryImpl");
        }
        return null;
    }

    public static Class getStaxOutputFactory() throws ClassNotFoundException {
        if (JVM.isVersion(6)) {
            if (JVM.isIBM()) {
                return Class.forName("com.ibm.xml.xlxp.api.stax.XMLOutputFactoryImpl");
            }
            return Class.forName("com.sun.xml.internal.stream.XMLOutputFactoryImpl");
        }
        return null;
    }

    public static StringCodec getBase64Codec() {
        return base64Codec;
    }

    public synchronized ReflectionProvider bestReflectionProvider() {
        if (this.reflectionProvider == null) {
            this.reflectionProvider = JVM.newReflectionProvider();
        }
        return this.reflectionProvider;
    }

    private static boolean canUseSunUnsafeReflectionProvider() {
        return canAllocateWithUnsafe;
    }

    private static boolean canUseSunLimitedUnsafeReflectionProvider() {
        return canWriteWithUnsafe;
    }

    public static boolean reverseFieldDefinition() {
        return false;
    }

    public static boolean isAWTAvailable() {
        return isAWTAvailable;
    }

    public boolean supportsAWT() {
        return isAWTAvailable;
    }

    public static boolean isSwingAvailable() {
        return isSwingAvailable;
    }

    public boolean supportsSwing() {
        return isSwingAvailable;
    }

    public static boolean isSQLAvailable() {
        return isSQLAvailable;
    }

    public boolean supportsSQL() {
        return isSQLAvailable;
    }

    public static boolean hasOptimizedTreeSetAddAll() {
        return optimizedTreeSetAddAll;
    }

    public static boolean hasOptimizedTreeMapPutAll() {
        return optimizedTreeMapPutAll;
    }

    public static boolean canParseUTCDateFormat() {
        return canParseUTCDateFormat;
    }

    public static boolean canParseISO8601TimeZoneInDateFormat() {
        return canParseISO8601TimeZoneInDateFormat;
    }

    public static boolean canCreateDerivedObjectOutputStream() {
        return canCreateDerivedObjectOutputStream;
    }

    public void flushCache() {
    }

    public static void main(String[] args) {
        boolean reverseJDK = false;
        Field[] fields = AttributedString.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (!fields[i].getName().equals("text")) continue;
            reverseJDK = i > 3;
            break;
        }
        boolean reverseLocal = false;
        fields = Test.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (!fields[i].getName().equals("o")) continue;
            reverseLocal = i > 3;
            break;
        }
        String staxInputFactory = null;
        try {
            staxInputFactory = JVM.getStaxInputFactory().getName();
        }
        catch (ClassNotFoundException e) {
            staxInputFactory = e.getMessage();
        }
        catch (NullPointerException e) {
            // empty catch block
        }
        String staxOutputFactory = null;
        try {
            staxOutputFactory = JVM.getStaxOutputFactory().getName();
        }
        catch (ClassNotFoundException e) {
            staxOutputFactory = e.getMessage();
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        System.out.println("XStream JVM diagnostics");
        System.out.println("java.specification.version: " + System.getProperty("java.specification.version"));
        System.out.println("java.specification.vendor: " + System.getProperty("java.specification.vendor"));
        System.out.println("java.specification.name: " + System.getProperty("java.specification.name"));
        System.out.println("java.vm.vendor: " + vendor);
        System.out.println("java.vendor: " + System.getProperty("java.vendor"));
        System.out.println("java.vm.name: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + majorJavaVersion);
        System.out.println("XStream support for enhanced Mode: " + JVM.canUseSunUnsafeReflectionProvider());
        System.out.println("XStream support for reduced Mode: " + JVM.canUseSunLimitedUnsafeReflectionProvider());
        System.out.println("Supports AWT: " + JVM.isAWTAvailable());
        System.out.println("Supports Swing: " + JVM.isSwingAvailable());
        System.out.println("Supports SQL: " + JVM.isSQLAvailable());
        System.out.println("Java Beans EventHandler present: " + (JVM.loadClassForName("java.beans.EventHandler") != null));
        System.out.println("Standard StAX XMLInputFactory: " + staxInputFactory);
        System.out.println("Standard StAX XMLOutputFactory: " + staxOutputFactory);
        System.out.println("Standard Base64 Codec: " + JVM.getBase64Codec().getClass().toString());
        System.out.println("Optimized TreeSet.addAll: " + JVM.hasOptimizedTreeSetAddAll());
        System.out.println("Optimized TreeMap.putAll: " + JVM.hasOptimizedTreeMapPutAll());
        System.out.println("Can parse UTC date format: " + JVM.canParseUTCDateFormat());
        System.out.println("Can create derive ObjectOutputStream: " + JVM.canCreateDerivedObjectOutputStream());
        System.out.println("Reverse field order detected for JDK: " + reverseJDK);
        System.out.println("Reverse field order detected (only if JVM class itself has been compiled): " + reverseLocal);
    }

    static {
        Class cls;
        Class type;
        vendor = System.getProperty("java.vm.vendor");
        majorJavaVersion = JVM.getMajorJavaVersion();
        boolean test = true;
        Object unsafe = null;
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = unsafeField.get(null);
            Method allocateInstance = unsafeClass.getDeclaredMethod("allocateInstance", Class.class);
            allocateInstance.setAccessible(true);
            test = allocateInstance.invoke(unsafe, Test.class) != null;
        }
        catch (Exception e) {
            test = false;
        }
        catch (Error e) {
            test = false;
        }
        canAllocateWithUnsafe = test;
        test = false;
        Class clazz = type = PureJavaReflectionProvider.class;
        if (JVM.canUseSunUnsafeReflectionProvider() && (cls = JVM.loadClassForName("com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider")) != null) {
            try {
                ReflectionProvider provider = (ReflectionProvider)DependencyInjectionFactory.newInstance(cls, null);
                Test t = (Test)provider.newInstance(Test.class);
                try {
                    provider.writeField(t, "o", "object", Test.class);
                    provider.writeField(t, "c", new Character('c'), Test.class);
                    provider.writeField(t, "b", new Byte(1), Test.class);
                    provider.writeField(t, "s", new Short(1), Test.class);
                    provider.writeField(t, "i", new Integer(1), Test.class);
                    provider.writeField(t, "l", new Long(1L), Test.class);
                    provider.writeField(t, "f", new Float(1.0f), Test.class);
                    provider.writeField(t, "d", new Double(1.0), Test.class);
                    provider.writeField(t, "bool", Boolean.TRUE, Test.class);
                    test = true;
                }
                catch (IncompatibleClassChangeError e) {
                    cls = null;
                }
                catch (ObjectAccessException e) {
                    cls = null;
                }
                if (cls == null) {
                    cls = JVM.loadClassForName("com.thoughtworks.xstream.converters.reflection.SunLimitedUnsafeReflectionProvider");
                }
                type = cls;
            }
            catch (ObjectAccessException provider) {
                // empty catch block
            }
        }
        reflectionProviderType = type;
        canWriteWithUnsafe = test;
        Comparator comparator = new Comparator(){

            public int compare(Object o1, Object o2) {
                throw new RuntimeException();
            }
        };
        PresortedMap map = new PresortedMap(comparator);
        map.put("one", null);
        map.put("two", null);
        try {
            new TreeMap(comparator).putAll(map);
            test = true;
        }
        catch (RuntimeException e) {
            test = false;
        }
        optimizedTreeMapPutAll = test;
        PresortedSet set = new PresortedSet(comparator);
        set.addAll(map.keySet());
        try {
            new TreeSet(comparator).addAll(set);
            test = true;
        }
        catch (RuntimeException e) {
            test = false;
        }
        optimizedTreeSetAddAll = test;
        try {
            new SimpleDateFormat("z").parse("UTC");
            test = true;
        }
        catch (RuntimeException e) {
            test = false;
        }
        catch (ParseException e) {
            test = false;
        }
        canParseUTCDateFormat = test;
        try {
            new SimpleDateFormat("X").parse("Z");
            test = true;
        }
        catch (RuntimeException e) {
            test = false;
        }
        catch (ParseException e) {
            test = false;
        }
        canParseISO8601TimeZoneInDateFormat = test;
        try {
            test = new CustomObjectOutputStream(null) != null;
        }
        catch (RuntimeException e) {
            test = false;
        }
        catch (IOException e) {
            test = false;
        }
        canCreateDerivedObjectOutputStream = test;
        isAWTAvailable = JVM.loadClassForName("java.awt.Color", false) != null;
        isSwingAvailable = JVM.loadClassForName("javax.swing.LookAndFeel", false) != null;
        isSQLAvailable = JVM.loadClassForName("java.sql.Date") != null;
        StringCodec base64 = null;
        Class base64Class = JVM.loadClassForName("com.thoughtworks.xstream.core.util.Base64JavaUtilCodec");
        if (base64Class == null) {
            base64Class = JVM.loadClassForName("com.thoughtworks.xstream.core.util.Base64JAXBCodec");
        }
        if (base64Class != null) {
            try {
                base64 = (StringCodec)base64Class.newInstance();
            }
            catch (Exception exception) {
            }
            catch (Error error) {
                // empty catch block
            }
        }
        if (base64 == null) {
            base64 = new Base64Encoder();
        }
        base64Codec = base64;
    }

    static class Test {
        private Object o;
        private char c;
        private byte b;
        private short s;
        private int i;
        private long l;
        private float f;
        private double d;
        private boolean bool;

        Test() {
            throw new UnsupportedOperationException();
        }
    }
}

