/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.lang;

import com.twelvemonkeys.lang.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class SystemUtil {
    public static String XML_PROPERTIES = ".xml";
    public static String STD_PROPERTIES = ".properties";

    private SystemUtil() {
    }

    private static InputStream getResourceAsStream(ClassLoader classLoader, String string, boolean bl) {
        InputStream inputStream;
        if (!bl) {
            inputStream = classLoader.getResourceAsStream(string);
            if (inputStream != null && string.endsWith(XML_PROPERTIES)) {
                inputStream = new XMLPropertiesInputStream(inputStream);
            }
        } else {
            inputStream = classLoader.getResourceAsStream(string + STD_PROPERTIES);
            if (inputStream == null && (inputStream = classLoader.getResourceAsStream(string + XML_PROPERTIES)) != null) {
                inputStream = new XMLPropertiesInputStream(inputStream);
            }
        }
        return inputStream;
    }

    private static InputStream getFileAsStream(String string, boolean bl) {
        InputStream inputStream = null;
        try {
            if (!bl) {
                File file = new File(string);
                if (file.exists()) {
                    inputStream = new FileInputStream(file);
                    if (string.endsWith(XML_PROPERTIES)) {
                        inputStream = new XMLPropertiesInputStream(inputStream);
                    }
                }
            } else {
                File file = new File(string + STD_PROPERTIES);
                if (file.exists()) {
                    inputStream = new FileInputStream(file);
                } else {
                    file = new File(string + XML_PROPERTIES);
                    if (file.exists()) {
                        inputStream = new XMLPropertiesInputStream(new FileInputStream(file));
                    }
                }
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            // empty catch block
        }
        return inputStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Properties loadProperties(Class clazz, String string) throws IOException {
        InputStream inputStream;
        boolean bl;
        String string2 = !StringUtil.isEmpty(string) ? string : clazz.getName().replace('.', '/');
        boolean bl2 = bl = string == null || string.indexOf(46) < 0;
        if ((clazz == null || (inputStream = SystemUtil.getResourceAsStream(clazz.getClassLoader(), string2, bl)) == null) && (inputStream = SystemUtil.getResourceAsStream(ClassLoader.getSystemClassLoader(), string2, bl)) == null && (inputStream = SystemUtil.getFileAsStream(string2, bl)) == null) {
            if (bl) {
                throw new FileNotFoundException(string2 + ".properties or " + string2 + ".xml");
            }
            throw new FileNotFoundException(string2);
        }
        try {
            Properties properties = SystemUtil.loadProperties(inputStream);
            return properties;
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException iOException) {}
        }
    }

    public static Properties loadProperties(Class clazz) throws IOException {
        return SystemUtil.loadProperties(clazz, null);
    }

    public static Properties loadProperties(String string) throws IOException {
        return SystemUtil.loadProperties(null, string);
    }

    private static Properties loadProperties(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream == null!");
        }
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    public static Object clone(Cloneable cloneable) throws CloneNotSupportedException {
        if (cloneable == null) {
            return null;
        }
        if (cloneable instanceof Object[]) {
            return ((Object[])cloneable).clone();
        }
        if (cloneable.getClass().isArray()) {
            int n = Array.getLength(cloneable);
            Object object = Array.newInstance(cloneable.getClass().getComponentType(), n);
            System.arraycopy(cloneable, 0, object, 0, n);
            return object;
        }
        try {
            Method method = null;
            Class<?> clazz = cloneable.getClass();
            while (true) {
                try {
                    method = clazz.getDeclaredMethod("clone", new Class[0]);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    if ((clazz = clazz.getSuperclass()) != null) continue;
                }
                break;
            }
            if (method == null) {
                throw new CloneNotSupportedException(cloneable.getClass().getName());
            }
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke((Object)cloneable, new Object[0]);
        }
        catch (SecurityException securityException) {
            CloneNotSupportedException cloneNotSupportedException = new CloneNotSupportedException(cloneable.getClass().getName());
            cloneNotSupportedException.initCause(securityException);
            throw cloneNotSupportedException;
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new CloneNotSupportedException(cloneable.getClass().getName());
        }
        catch (InvocationTargetException invocationTargetException) {
            if (invocationTargetException.getTargetException() instanceof CloneNotSupportedException) {
                throw (CloneNotSupportedException)invocationTargetException.getTargetException();
            }
            if (invocationTargetException.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)invocationTargetException.getTargetException();
            }
            if (invocationTargetException.getTargetException() instanceof Error) {
                throw (Error)invocationTargetException.getTargetException();
            }
            throw new CloneNotSupportedException(cloneable.getClass().getName());
        }
    }

    public static void main(String[] stringArray) throws CloneNotSupportedException {
        System.out.println("clone: " + ((String[])stringArray.clone()).length + " (" + stringArray.length + ")");
        System.out.println("copy: " + ((String[])SystemUtil.clone((Cloneable)stringArray)).length + " (" + stringArray.length + ")");
        int[] nArray = new int[]{1, 2, 3};
        int[] nArray2 = (int[])SystemUtil.clone((Cloneable)nArray);
        System.out.println("Copies: " + nArray2.length + " (" + nArray.length + ")");
        int[][] nArrayArray = new int[][]{{1}, {2, 3}, {4, 5, 6}};
        int[][] nArray3 = (int[][])SystemUtil.clone((Cloneable)nArrayArray);
        System.out.println("Copies: " + nArray3.length + " (" + nArrayArray.length + ")");
        System.out.println("Copies0: " + nArray3[0].length + " (" + nArrayArray[0].length + ")");
        System.out.println("Copies1: " + nArray3[1].length + " (" + nArrayArray[1].length + ")");
        System.out.println("Copies2: " + nArray3[2].length + " (" + nArrayArray[2].length + ")");
        HashMap<String, String> hashMap = new HashMap<String, String>();
        for (String string : stringArray) {
            hashMap.put(string, string);
        }
        Map map = (Map)SystemUtil.clone(hashMap);
        System.out.println("Map : " + hashMap);
        System.out.println("Copy: " + map);
        Cloneable cloneable = new Cloneable(){};
        Cloneable cloneable2 = (Cloneable)SystemUtil.clone(cloneable);
        System.out.println("cloneable: " + cloneable);
        System.out.println("clone: " + cloneable2);
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                return null;
            }
        }, AccessController.getContext());
    }

    public static boolean isClassAvailable(String string) {
        return SystemUtil.isClassAvailable(string, (ClassLoader)null);
    }

    public static boolean isClassAvailable(String string, Class clazz) {
        ClassLoader classLoader = clazz != null ? clazz.getClassLoader() : null;
        return SystemUtil.isClassAvailable(string, classLoader);
    }

    private static boolean isClassAvailable(String string, ClassLoader classLoader) {
        try {
            SystemUtil.getClass(string, true, classLoader);
            return true;
        }
        catch (SecurityException securityException) {
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (LinkageError linkageError) {
            // empty catch block
        }
        return false;
    }

    public static boolean isFieldAvailable(String string, String string2) {
        return SystemUtil.isFieldAvailable(string, string2, (ClassLoader)null);
    }

    public static boolean isFieldAvailable(String string, String string2, Class clazz) {
        ClassLoader classLoader = clazz != null ? clazz.getClassLoader() : null;
        return SystemUtil.isFieldAvailable(string, string2, classLoader);
    }

    private static boolean isFieldAvailable(String string, String string2, ClassLoader classLoader) {
        try {
            Class clazz = SystemUtil.getClass(string, false, classLoader);
            Field field = clazz.getField(string2);
            if (field != null) {
                return true;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (LinkageError linkageError) {
        }
        catch (NoSuchFieldException noSuchFieldException) {
            // empty catch block
        }
        return false;
    }

    public static boolean isMethodAvailable(String string, String string2) {
        return SystemUtil.isMethodAvailable(string, string2, null, (ClassLoader)null);
    }

    public static boolean isMethodAvailable(String string, String string2, Class[] classArray) {
        return SystemUtil.isMethodAvailable(string, string2, classArray, (ClassLoader)null);
    }

    public static boolean isMethodAvailable(String string, String string2, Class[] classArray, Class clazz) {
        ClassLoader classLoader = clazz != null ? clazz.getClassLoader() : null;
        return SystemUtil.isMethodAvailable(string, string2, classArray, classLoader);
    }

    private static boolean isMethodAvailable(String string, String string2, Class[] classArray, ClassLoader classLoader) {
        try {
            Class clazz = SystemUtil.getClass(string, false, classLoader);
            Method method = clazz.getMethod(string2, classArray);
            if (method != null) {
                return true;
            }
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (LinkageError linkageError) {
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        return false;
    }

    private static Class getClass(String string, boolean bl, ClassLoader classLoader) throws ClassNotFoundException {
        ClassLoader classLoader2 = classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
        return Class.forName(string, bl, classLoader2);
    }

    private static class XMLPropertiesInputStream
    extends FilterInputStream {
        public XMLPropertiesInputStream(InputStream inputStream) {
            super(inputStream);
        }
    }
}

