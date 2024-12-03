/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.Arrays;

public final class MethodUtil
extends SecureClassLoader {
    private static final String WS_UTIL_PKG = "com.sun.xml.ws.util.";
    private static final String TRAMPOLINE = "com.sun.xml.ws.util.Trampoline";
    private static final Method bounce = MethodUtil.getTrampoline();
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 0x7FFFFFF7;
    private static final String PROXY_PACKAGE = "com.sun.proxy";

    private MethodUtil() {
    }

    public static Object invoke(Method m, Object obj, Object[] params) throws InvocationTargetException, IllegalAccessException {
        try {
            return bounce.invoke(null, m, obj, params);
        }
        catch (InvocationTargetException ie) {
            Throwable t = ie.getCause();
            if (t instanceof InvocationTargetException) {
                throw (InvocationTargetException)t;
            }
            if (t instanceof IllegalAccessException) {
                throw (IllegalAccessException)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new Error("Unexpected invocation error", t);
        }
        catch (IllegalAccessException iae) {
            throw new Error("Unexpected invocation error", iae);
        }
    }

    private static Method getTrampoline() {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                @Override
                public Method run() throws Exception {
                    Class t = MethodUtil.getTrampolineClass();
                    Method b = t.getDeclaredMethod("invoke", Method.class, Object.class, Object[].class);
                    b.setAccessible(true);
                    return b;
                }
            });
        }
        catch (Exception e) {
            throw new InternalError("bouncer cannot be found", e);
        }
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        MethodUtil.checkPackageAccess(name);
        Class<?> c = this.findLoadedClass(name);
        if (c == null) {
            try {
                c = this.findClass(name);
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
            if (c == null) {
                c = this.getParent().loadClass(name);
            }
        }
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz;
        block9: {
            if (!name.startsWith(WS_UTIL_PKG)) {
                throw new ClassNotFoundException(name);
            }
            String path = "/".concat(name.replace('.', '/').concat(".class"));
            InputStream in = MethodUtil.class.getResourceAsStream(path);
            try {
                byte[] b = this.readAllBytes(in);
                clazz = this.defineClass(name, b);
                if (in == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new ClassNotFoundException(name, e);
                }
            }
            in.close();
        }
        return clazz;
    }

    private byte[] readAllBytes(InputStream in) throws IOException {
        byte[] buf = new byte[8192];
        int capacity = buf.length;
        int nread = 0;
        while (true) {
            int n;
            if ((n = in.read(buf, nread, capacity - nread)) > 0) {
                nread += n;
                continue;
            }
            if (n < 0) break;
            if (capacity <= 0x7FFFFFF7 - capacity) {
                capacity <<= 1;
            } else {
                if (capacity == 0x7FFFFFF7) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                capacity = 0x7FFFFFF7;
            }
            buf = Arrays.copyOf(buf, capacity);
        }
        return capacity == nread ? buf : Arrays.copyOf(buf, nread);
    }

    private Class<?> defineClass(String name, byte[] b) throws IOException {
        CodeSource cs = new CodeSource(null, (Certificate[])null);
        if (!name.equals(TRAMPOLINE)) {
            throw new IOException("MethodUtil: bad name " + name);
        }
        return this.defineClass(name, b, 0, b.length, cs);
    }

    @Override
    protected PermissionCollection getPermissions(CodeSource codesource) {
        PermissionCollection perms = super.getPermissions(codesource);
        perms.add(new AllPermission());
        return perms;
    }

    private static Class<?> getTrampolineClass() {
        try {
            return Class.forName(TRAMPOLINE, true, new MethodUtil());
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }

    private static void checkPackageAccess(String name) {
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            int i;
            int b;
            String cname = name.replace('/', '.');
            if (cname.startsWith("[") && (b = cname.lastIndexOf(91) + 2) > 1 && b < cname.length()) {
                cname = cname.substring(b);
            }
            if ((i = cname.lastIndexOf(46)) != -1) {
                s.checkPackageAccess(cname.substring(0, i));
            }
        }
    }

    private static void checkPackageAccess(Class<?> clazz) {
        MethodUtil.checkPackageAccess(clazz.getName());
        if (MethodUtil.isNonPublicProxyClass(clazz)) {
            MethodUtil.checkProxyPackageAccess(clazz);
        }
    }

    private static boolean isNonPublicProxyClass(Class<?> cls) {
        String name = cls.getName();
        int i = name.lastIndexOf(46);
        String pkg = i != -1 ? name.substring(0, i) : "";
        return Proxy.isProxyClass(cls) && !pkg.startsWith(PROXY_PACKAGE);
    }

    private static void checkProxyPackageAccess(Class<?> clazz) {
        SecurityManager s = System.getSecurityManager();
        if (s != null && Proxy.isProxyClass(clazz)) {
            for (Class<?> intf : clazz.getInterfaces()) {
                MethodUtil.checkPackageAccess(intf);
            }
        }
    }
}

