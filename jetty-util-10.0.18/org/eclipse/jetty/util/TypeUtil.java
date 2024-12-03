/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.module.Configuration;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.eclipse.jetty.util.ServiceLoaderSpliterator;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeUtil {
    private static final Logger LOG = LoggerFactory.getLogger(TypeUtil.class);
    public static final Class<?>[] NO_ARGS = new Class[0];
    public static final int CR = 13;
    public static final int LF = 10;
    private static final HashMap<String, Class<?>> name2Class = new HashMap();
    private static final HashMap<Class<?>, String> class2Name;
    private static final HashMap<Class<?>, Method> class2Value;
    private static final MethodHandle[] LOCATION_METHODS;

    public static <T> List<T> asList(T[] a) {
        if (a == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(a);
    }

    public static Class<?> fromName(String name) {
        return name2Class.get(name);
    }

    public static String toName(Class<?> type) {
        return class2Name.get(type);
    }

    public static String toClassReference(Class<?> clazz) {
        return TypeUtil.toClassReference(clazz.getName());
    }

    public static String toClassReference(String className) {
        return StringUtil.replace(className, '.', '/').concat(".class");
    }

    public static Object valueOf(Class<?> type, String value) {
        try {
            if (type.equals(String.class)) {
                return value;
            }
            Method m = class2Value.get(type);
            if (m != null) {
                return m.invoke(null, value);
            }
            if (type.equals(Character.TYPE) || type.equals(Character.class)) {
                return Character.valueOf(value.charAt(0));
            }
            Constructor<?> c = type.getConstructor(String.class);
            return c.newInstance(value);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException x) {
            LOG.trace("IGNORED", (Throwable)x);
        }
        catch (InvocationTargetException x) {
            if (x.getTargetException() instanceof Error) {
                throw (Error)x.getTargetException();
            }
            LOG.trace("IGNORED", (Throwable)x);
        }
        return null;
    }

    public static Object valueOf(String type, String value) {
        return TypeUtil.valueOf(TypeUtil.fromName(type), value);
    }

    public static int parseInt(String s, int offset, int length, int base) throws NumberFormatException {
        int value = 0;
        if (length < 0) {
            length = s.length() - offset;
        }
        for (int i = 0; i < length; ++i) {
            char c = s.charAt(offset + i);
            int digit = TypeUtil.convertHexDigit((int)c);
            if (digit < 0 || digit >= base) {
                throw new NumberFormatException(s.substring(offset, offset + length));
            }
            value = value * base + digit;
        }
        return value;
    }

    public static int parseInt(byte[] b, int offset, int length, int base) throws NumberFormatException {
        int value = 0;
        if (length < 0) {
            length = b.length - offset;
        }
        for (int i = 0; i < length; ++i) {
            char c = (char)(0xFF & b[offset + i]);
            int digit = c - 48;
            if (!(digit >= 0 && digit < base && digit < 10 || (digit = 10 + c - 65) >= 10 && digit < base)) {
                digit = 10 + c - 97;
            }
            if (digit < 0 || digit >= base) {
                throw new NumberFormatException(new String(b, offset, length));
            }
            value = value * base + digit;
        }
        return value;
    }

    @Deprecated
    public static byte[] parseBytes(String s, int base) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            bytes[i / 2] = (byte)TypeUtil.parseInt(s, i, 2, base);
        }
        return bytes;
    }

    public static String toString(byte[] bytes, int base) {
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            int bi = 0xFF & b;
            int c = 48 + bi / base % base;
            if (c > 57) {
                c = 97 + (c - 48 - 10);
            }
            buf.append((char)c);
            c = 48 + bi % base;
            if (c > 57) {
                c = 97 + (c - 48 - 10);
            }
            buf.append((char)c);
        }
        return buf.toString();
    }

    public static byte convertHexDigit(byte c) {
        byte b = (byte)((c & 0x1F) + (c >> 6) * 25 - 16);
        if (b < 0 || b > 15) {
            throw new NumberFormatException("!hex " + c);
        }
        return b;
    }

    public static int convertHexDigit(char c) {
        int d = (c & 0x1F) + (c >> 6) * 25 - 16;
        if (d < 0 || d > 15) {
            throw new NumberFormatException("!hex " + c);
        }
        return d;
    }

    public static int convertHexDigit(int c) {
        int d = (c & 0x1F) + (c >> 6) * 25 - 16;
        if (d < 0 || d > 15) {
            throw new NumberFormatException("!hex " + c);
        }
        return d;
    }

    public static void toHex(byte b, Appendable buf) {
        try {
            int d = 0xF & (0xF0 & b) >> 4;
            buf.append((char)((d > 9 ? 55 : 48) + d));
            d = 0xF & b;
            buf.append((char)((d > 9 ? 55 : 48) + d));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void toHex(int value, Appendable buf) throws IOException {
        int d = 0xF & (0xF0000000 & value) >> 28;
        buf.append((char)((d > 9 ? 55 : 48) + d));
        d = 0xF & (0xF000000 & value) >> 24;
        buf.append((char)((d > 9 ? 55 : 48) + d));
        d = 0xF & (0xF00000 & value) >> 20;
        buf.append((char)((d > 9 ? 55 : 48) + d));
        d = 0xF & (0xF0000 & value) >> 16;
        buf.append((char)((d > 9 ? 55 : 48) + d));
        d = 0xF & (0xF000 & value) >> 12;
        buf.append((char)((d > 9 ? 55 : 48) + d));
        d = 0xF & (0xF00 & value) >> 8;
        buf.append((char)((d > 9 ? 55 : 48) + d));
        d = 0xF & (0xF0 & value) >> 4;
        buf.append((char)((d > 9 ? 55 : 48) + d));
        d = 0xF & value;
        buf.append((char)((d > 9 ? 55 : 48) + d));
        Integer.toString(0, 36);
    }

    public static void toHex(long value, Appendable buf) throws IOException {
        TypeUtil.toHex((int)(value >> 32), buf);
        TypeUtil.toHex((int)value, buf);
    }

    @Deprecated
    public static String toHexString(byte b) {
        return StringUtil.toHexString(b);
    }

    @Deprecated
    public static String toHexString(byte[] b) {
        return StringUtil.toHexString(b);
    }

    @Deprecated
    public static String toHexString(byte[] b, int offset, int length) {
        return StringUtil.toHexString(b, offset, length);
    }

    @Deprecated
    public static byte[] fromHexString(String s) {
        return StringUtil.fromHexString(s);
    }

    public static void dump(Class<?> c) {
        System.err.println("Dump: " + c);
        TypeUtil.dump(c.getClassLoader());
    }

    public static void dump(ClassLoader cl) {
        System.err.println("Dump Loaders:");
        while (cl != null) {
            System.err.println("  loader " + cl);
            cl = cl.getParent();
        }
    }

    public static boolean isTrue(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return (Boolean)o;
        }
        return Boolean.parseBoolean(o.toString());
    }

    public static boolean isFalse(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return (Boolean)o == false;
        }
        return "false".equalsIgnoreCase(o.toString());
    }

    public static URI getLocationOfClass(Class<?> clazz) {
        for (MethodHandle locationMethod : LOCATION_METHODS) {
            try {
                URI location = locationMethod.invoke(clazz);
                if (location == null) continue;
                return location;
            }
            catch (Throwable cause) {
                cause.printStackTrace(System.err);
            }
        }
        return null;
    }

    public static URI getSystemClassLoaderLocation(Class<?> clazz) {
        return TypeUtil.getClassLoaderLocation(clazz, ClassLoader.getSystemClassLoader());
    }

    public static URI getClassLoaderLocation(Class<?> clazz) {
        return TypeUtil.getClassLoaderLocation(clazz, clazz.getClassLoader());
    }

    public static URI getClassLoaderLocation(Class<?> clazz, ClassLoader loader) {
        if (loader == null) {
            return null;
        }
        try {
            URL url;
            String resourceName = TypeUtil.toClassReference(clazz);
            if (loader != null && (url = loader.getResource(resourceName)) != null) {
                int idx;
                URI uri = url.toURI();
                String uriStr = uri.toASCIIString();
                if (uriStr.startsWith("jar:file:") && (idx = (uriStr = uriStr.substring(4)).indexOf("!/")) > 0) {
                    return URI.create(uriStr.substring(0, idx));
                }
                return uri;
            }
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
        return null;
    }

    public static URI getCodeSourceLocation(Class<?> clazz) {
        try {
            URL location;
            CodeSource source;
            ProtectionDomain domain = clazz.getProtectionDomain();
            if (domain != null && (source = domain.getCodeSource()) != null && (location = source.getLocation()) != null) {
                return location.toURI();
            }
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
        return null;
    }

    public static URI getModuleLocation(Class<?> clazz) {
        Module module = clazz.getModule();
        if (module == null) {
            return null;
        }
        ModuleLayer layer = module.getLayer();
        if (layer == null) {
            return null;
        }
        Configuration configuration = layer.configuration();
        if (configuration == null) {
            return null;
        }
        Optional<ResolvedModule> resolvedModule = configuration.findModule(module.getName());
        if (resolvedModule == null || !resolvedModule.isPresent()) {
            return null;
        }
        ModuleReference moduleReference = resolvedModule.get().reference();
        if (moduleReference == null) {
            return null;
        }
        Optional<URI> location = moduleReference.location();
        if (location.isPresent()) {
            return location.get();
        }
        return null;
    }

    public static <T> Iterator<T> concat(final Iterator<T> i1, final Iterator<T> i2) {
        return new Iterator<T>(){

            @Override
            public boolean hasNext() {
                return i1.hasNext() || i2.hasNext();
            }

            @Override
            public T next() {
                return i1.hasNext() ? i1.next() : i2.next();
            }
        };
    }

    private static <T> Stream<T> mapToService(ServiceLoader.Provider<T> provider) {
        try {
            return Stream.of(provider.get());
        }
        catch (ServiceConfigurationError error) {
            LOG.warn("Service Provider failed to load", (Throwable)error);
            return Stream.empty();
        }
    }

    public static <T> Stream<T> serviceStream(ServiceLoader<T> serviceLoader) {
        return TypeUtil.serviceProviderStream(serviceLoader).flatMap(TypeUtil::mapToService);
    }

    public static <T> Stream<ServiceLoader.Provider<T>> serviceProviderStream(ServiceLoader<T> serviceLoader) {
        return StreamSupport.stream(new ServiceLoaderSpliterator<T>(serviceLoader), false);
    }

    static {
        name2Class.put("boolean", Boolean.TYPE);
        name2Class.put("byte", Byte.TYPE);
        name2Class.put("char", Character.TYPE);
        name2Class.put("double", Double.TYPE);
        name2Class.put("float", Float.TYPE);
        name2Class.put("int", Integer.TYPE);
        name2Class.put("long", Long.TYPE);
        name2Class.put("short", Short.TYPE);
        name2Class.put("void", Void.TYPE);
        name2Class.put("java.lang.Boolean.TYPE", Boolean.TYPE);
        name2Class.put("java.lang.Byte.TYPE", Byte.TYPE);
        name2Class.put("java.lang.Character.TYPE", Character.TYPE);
        name2Class.put("java.lang.Double.TYPE", Double.TYPE);
        name2Class.put("java.lang.Float.TYPE", Float.TYPE);
        name2Class.put("java.lang.Integer.TYPE", Integer.TYPE);
        name2Class.put("java.lang.Long.TYPE", Long.TYPE);
        name2Class.put("java.lang.Short.TYPE", Short.TYPE);
        name2Class.put("java.lang.Void.TYPE", Void.TYPE);
        name2Class.put("java.lang.Boolean", Boolean.class);
        name2Class.put("java.lang.Byte", Byte.class);
        name2Class.put("java.lang.Character", Character.class);
        name2Class.put("java.lang.Double", Double.class);
        name2Class.put("java.lang.Float", Float.class);
        name2Class.put("java.lang.Integer", Integer.class);
        name2Class.put("java.lang.Long", Long.class);
        name2Class.put("java.lang.Short", Short.class);
        name2Class.put("Boolean", Boolean.class);
        name2Class.put("Byte", Byte.class);
        name2Class.put("Character", Character.class);
        name2Class.put("Double", Double.class);
        name2Class.put("Float", Float.class);
        name2Class.put("Integer", Integer.class);
        name2Class.put("Long", Long.class);
        name2Class.put("Short", Short.class);
        name2Class.put(null, Void.TYPE);
        name2Class.put("string", String.class);
        name2Class.put("String", String.class);
        name2Class.put("java.lang.String", String.class);
        class2Name = new HashMap();
        class2Name.put(Boolean.TYPE, "boolean");
        class2Name.put(Byte.TYPE, "byte");
        class2Name.put(Character.TYPE, "char");
        class2Name.put(Double.TYPE, "double");
        class2Name.put(Float.TYPE, "float");
        class2Name.put(Integer.TYPE, "int");
        class2Name.put(Long.TYPE, "long");
        class2Name.put(Short.TYPE, "short");
        class2Name.put(Void.TYPE, "void");
        class2Name.put(Boolean.class, "java.lang.Boolean");
        class2Name.put(Byte.class, "java.lang.Byte");
        class2Name.put(Character.class, "java.lang.Character");
        class2Name.put(Double.class, "java.lang.Double");
        class2Name.put(Float.class, "java.lang.Float");
        class2Name.put(Integer.class, "java.lang.Integer");
        class2Name.put(Long.class, "java.lang.Long");
        class2Name.put(Short.class, "java.lang.Short");
        class2Name.put(null, "void");
        class2Name.put(String.class, "java.lang.String");
        class2Value = new HashMap();
        try {
            Class[] s = new Class[]{String.class};
            class2Value.put(Boolean.TYPE, Boolean.class.getMethod("valueOf", s));
            class2Value.put(Byte.TYPE, Byte.class.getMethod("valueOf", s));
            class2Value.put(Double.TYPE, Double.class.getMethod("valueOf", s));
            class2Value.put(Float.TYPE, Float.class.getMethod("valueOf", s));
            class2Value.put(Integer.TYPE, Integer.class.getMethod("valueOf", s));
            class2Value.put(Long.TYPE, Long.class.getMethod("valueOf", s));
            class2Value.put(Short.TYPE, Short.class.getMethod("valueOf", s));
            class2Value.put(Boolean.class, Boolean.class.getMethod("valueOf", s));
            class2Value.put(Byte.class, Byte.class.getMethod("valueOf", s));
            class2Value.put(Double.class, Double.class.getMethod("valueOf", s));
            class2Value.put(Float.class, Float.class.getMethod("valueOf", s));
            class2Value.put(Integer.class, Integer.class.getMethod("valueOf", s));
            class2Value.put(Long.class, Long.class.getMethod("valueOf", s));
            class2Value.put(Short.class, Short.class.getMethod("valueOf", s));
        }
        catch (Exception e) {
            throw new Error(e);
        }
        ArrayList<MethodHandle> locationMethods = new ArrayList<MethodHandle>();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType type = MethodType.methodType(URI.class, Class.class);
        try {
            locationMethods.add(lookup.findStatic(TypeUtil.class, "getCodeSourceLocation", type));
            locationMethods.add(lookup.findStatic(TypeUtil.class, "getModuleLocation", type));
            locationMethods.add(lookup.findStatic(TypeUtil.class, "getClassLoaderLocation", type));
            locationMethods.add(lookup.findStatic(TypeUtil.class, "getSystemClassLoaderLocation", type));
            LOCATION_METHODS = locationMethods.toArray(new MethodHandle[0]);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to establish Location Lookup Handles", e);
        }
    }
}

