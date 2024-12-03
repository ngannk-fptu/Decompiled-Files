/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.bundle.CollisionHook;
import org.osgi.framework.hooks.bundle.EventHook;
import org.osgi.framework.hooks.bundle.FindHook;
import org.osgi.framework.hooks.resolver.ResolverHook;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.hooks.weaving.WovenClassListener;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

public class SecureAction {
    private static final byte[] accessor;
    private static final ThreadLocal m_actions;
    protected static transient int BUFSIZE;
    private AccessControlContext m_acc = null;
    private static volatile Consumer<AccessibleObject[]> m_accessorCache;

    public SecureAction() {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(0, null);
                this.m_acc = (AccessControlContext)AccessController.doPrivileged(actions);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        } else {
            this.m_acc = AccessController.getContext();
        }
    }

    public String getSystemProperty(String name, String def) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(20, name, def);
                return (String)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return System.getProperty(name, def);
    }

    public ClassLoader getParentClassLoader(ClassLoader loader) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(21, loader);
                return (ClassLoader)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return loader.getParent();
    }

    public ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(22);
                return (ClassLoader)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return ClassLoader.getSystemClassLoader();
    }

    public ClassLoader getClassLoader(Class clazz) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(39, clazz);
                return (ClassLoader)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return clazz.getClassLoader();
    }

    public Class forName(String name, ClassLoader classloader) throws ClassNotFoundException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(9, name, classloader);
                return (Class)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        if (classloader != null) {
            return Class.forName(name, true, classloader);
        }
        return Class.forName(name);
    }

    public URL createURL(String protocol, String host, int port, String path, URLStreamHandler handler) throws MalformedURLException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(3, protocol, host, new Integer(port), path, handler);
                return (URL)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof MalformedURLException) {
                    throw (MalformedURLException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return new URL(protocol, host, port, path, handler);
    }

    public URL createURL(URL context, String spec, URLStreamHandler handler) throws MalformedURLException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(4, context, spec, handler);
                return (URL)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof MalformedURLException) {
                    throw (MalformedURLException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return new URL(context, spec, handler);
    }

    public Process exec(String command) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(6, command);
                return (Process)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return Runtime.getRuntime().exec(command);
    }

    public String getAbsolutePath(File file) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(10, file);
                return (String)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return file.getAbsolutePath();
    }

    public boolean fileExists(File file) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(7, file);
                return (Boolean)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return file.exists();
    }

    public boolean isFile(File file) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(60, file);
                return (Boolean)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return file.isFile();
    }

    public boolean isFileDirectory(File file) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(8, file);
                return (Boolean)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return file.isDirectory();
    }

    public boolean mkdir(File file) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(29, file);
                return (Boolean)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return file.mkdir();
    }

    public boolean mkdirs(File file) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(28, file);
                return (Boolean)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return file.mkdirs();
    }

    public File[] listDirectory(File file) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(27, file);
                return (File[])AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return file.listFiles();
    }

    public boolean renameFile(File oldFile, File newFile) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(32, oldFile, newFile);
                return (Boolean)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return oldFile.renameTo(newFile);
    }

    public InputStream getInputStream(File file) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(62, file);
                return (InputStream)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return Files.newInputStream(file.toPath(), new OpenOption[0]);
    }

    public OutputStream getOutputStream(File file) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(63, file);
                return (OutputStream)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return Files.newOutputStream(file.toPath(), new OpenOption[0]);
    }

    public FileInputStream getFileInputStream(File file) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(15, file);
                return (FileInputStream)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return new FileInputStream(file);
    }

    public FileOutputStream getFileOutputStream(File file) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(16, file);
                return (FileOutputStream)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return new FileOutputStream(file);
    }

    public FileChannel getFileChannel(File file) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(61, file);
                return (FileChannel)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    public URI toURI(File file) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(17, file);
                return (URI)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return file.toURI();
    }

    public InputStream getURLConnectionInputStream(URLConnection conn) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(23, conn);
                return (InputStream)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return conn.getInputStream();
    }

    public boolean deleteFile(File target) {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(5, target);
                return (Boolean)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return target.delete();
    }

    public File createTempFile(String prefix, String suffix, File dir) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(2, prefix, suffix, dir);
                return (File)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return File.createTempFile(prefix, suffix, dir);
    }

    public void deleteFileOnExit(File file) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(55, file);
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        } else {
            file.deleteOnExit();
        }
    }

    public URLConnection openURLConnection(URL url) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(31, url);
                return (URLConnection)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return url.openConnection();
    }

    public ZipFile openZipFile(File file) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(30, file);
                return (ZipFile)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return new ZipFile(file);
    }

    public JarFile openJarFile(File file) throws IOException {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(54, file);
                return (JarFile)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                if (ex.getException() instanceof IOException) {
                    throw (IOException)ex.getException();
                }
                throw (RuntimeException)ex.getException();
            }
        }
        return new JarFile(file);
    }

    public void startActivator(BundleActivator activator, BundleContext context) throws Exception {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(34, activator, context);
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw ex.getException();
            }
        } else {
            activator.start(context);
        }
    }

    public void stopActivator(BundleActivator activator, BundleContext context) throws Exception {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(35, activator, context);
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw ex.getException();
            }
        } else {
            activator.stop(context);
        }
    }

    public Policy getPolicy() {
        if (System.getSecurityManager() != null) {
            try {
                Actions actions = (Actions)m_actions.get();
                actions.set(19, null);
                return (Policy)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException ex) {
                throw (RuntimeException)ex.getException();
            }
        }
        return Policy.getPolicy();
    }

    public void addURLToURLClassLoader(URL extension, ClassLoader loader) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(1, extension, loader);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            SecureAction.getAccessor(URLClassLoader.class).accept(new AccessibleObject[]{addURL});
            addURL.invoke((Object)loader, extension);
        }
    }

    public Constructor getConstructor(Class target, Class[] types) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(11, target, types);
            try {
                return (Constructor)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return target.getConstructor(types);
    }

    public Constructor getDeclaredConstructor(Class target, Class[] types) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(12, target, types);
            try {
                return (Constructor)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return target.getDeclaredConstructor(types);
    }

    public Method getMethod(Class target, String method, Class[] types) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(18, target, method, types);
            try {
                return (Method)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return target.getMethod(method, types);
    }

    public Method getDeclaredMethod(Class target, String method, Class[] types) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(13, target, method, types);
            try {
                return (Method)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return target.getDeclaredMethod(method, types);
    }

    public void setAccesssible(Executable ao) {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(33, ao);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw (RuntimeException)e.getException();
            }
        } else {
            SecureAction.getAccessor(ao.getDeclaringClass()).accept(new AccessibleObject[]{ao});
        }
    }

    public Object invoke(Method method, Object target, Object[] params) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(26, method, target, params);
            try {
                return AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        SecureAction.getAccessor(method.getDeclaringClass()).accept(new AccessibleObject[]{method});
        return method.invoke(target, params);
    }

    public Object invokeDirect(Method method, Object target, Object[] params) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(25, method, target, params);
            try {
                return AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return method.invoke(target, params);
    }

    public Object invoke(Constructor constructor, Object[] params) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(24, constructor, params);
            try {
                return AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return constructor.newInstance(params);
    }

    public Object getDeclaredField(Class targetClass, String name, Object target) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(14, targetClass, name, target);
            try {
                return AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        Field field = targetClass.getDeclaredField(name);
        SecureAction.getAccessor(targetClass).accept(new AccessibleObject[]{field});
        return field.get(target);
    }

    public Object swapStaticFieldIfNotClass(Class targetClazz, Class targetType, Class condition, String lockName) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(36, targetClazz, targetType, condition, lockName);
            try {
                return AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return SecureAction._swapStaticFieldIfNotClass(targetClazz, targetType, condition, lockName);
    }

    private static Consumer<AccessibleObject[]> getAccessor(Class clazz) {
        String packageName = clazz.getPackage().getName();
        if ("java.net".equals(packageName) || "jdk.internal.loader".equals(packageName)) {
            if (m_accessorCache == null) {
                try {
                    Class<?> result;
                    Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                    Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    Object unsafe = theUnsafe.get(null);
                    try {
                        Method defineAnonymousClass = unsafeClass.getMethod("defineAnonymousClass", Class.class, byte[].class, Object[].class);
                        result = (Class<?>)defineAnonymousClass.invoke(unsafe, URL.class, accessor, null);
                    }
                    catch (NoSuchMethodException ex) {
                        long offset = (Long)unsafeClass.getMethod("staticFieldOffset", Field.class).invoke(unsafe, MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP"));
                        MethodHandles.Lookup lookup = (MethodHandles.Lookup)unsafeClass.getMethod("getObject", Object.class, Long.TYPE).invoke(unsafe, MethodHandles.Lookup.class, offset);
                        lookup = lookup.in(URL.class);
                        Class<?> classOption = Class.forName("java.lang.invoke.MethodHandles$Lookup$ClassOption");
                        Object classOptions = Array.newInstance(classOption, 0);
                        Method defineHiddenClass = MethodHandles.Lookup.class.getMethod("defineHiddenClass", byte[].class, Boolean.TYPE, classOptions.getClass());
                        lookup = (MethodHandles.Lookup)defineHiddenClass.invoke((Object)lookup, accessor, Boolean.FALSE, classOptions);
                        result = lookup.lookupClass();
                    }
                    m_accessorCache = (Consumer)result.getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (Throwable t) {
                    m_accessorCache = objects -> AccessibleObject.setAccessible(objects, true);
                }
            }
            return m_accessorCache;
        }
        return objects -> AccessibleObject.setAccessible(objects, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object _swapStaticFieldIfNotClass(Class targetClazz, Class targetType, Class condition, String lockName) throws Exception {
        Object lock = null;
        if (lockName != null) {
            try {
                Field lockField = targetClazz.getDeclaredField(lockName);
                SecureAction.getAccessor(targetClazz).accept(new AccessibleObject[]{lockField});
                lock = lockField.get(null);
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
        }
        if (lock == null) {
            lock = targetClazz;
        }
        Class clazz = lock;
        synchronized (clazz) {
            int i;
            Field[] fields = targetClazz.getDeclaredFields();
            SecureAction.getAccessor(targetClazz).accept(fields);
            Object result = null;
            for (i = 0; i < fields.length && result == null; ++i) {
                if (!Modifier.isStatic(fields[i].getModifiers()) || fields[i].getType() != targetType || (result = fields[i].get(null)) == null || condition != null && result.getClass().getName().equals(condition.getName())) continue;
                fields[i].set(null, null);
            }
            if (result != null) {
                if (condition == null || !result.getClass().getName().equals(condition.getName())) {
                    for (i = 0; i < fields.length; ++i) {
                        Hashtable cache;
                        if (!Modifier.isStatic(fields[i].getModifiers()) || fields[i].getType() != Hashtable.class || (cache = (Hashtable)fields[i].get(null)) == null) continue;
                        cache.clear();
                    }
                }
                return result;
            }
        }
        return null;
    }

    public void flush(Class targetClazz, Object lock) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(38, targetClazz, lock);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            SecureAction._flush(targetClazz, lock);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void _flush(Class targetClazz, Object lock) throws Exception {
        Object object = lock;
        synchronized (object) {
            Field[] fields = targetClazz.getDeclaredFields();
            SecureAction.getAccessor(targetClazz).accept(fields);
            for (int i = 0; i < fields.length; ++i) {
                Map cache;
                if (!Modifier.isStatic(fields[i].getModifiers()) || fields[i].getType() != Hashtable.class && fields[i].getType() != HashMap.class) continue;
                if (fields[i].getType() == Hashtable.class) {
                    cache = (Hashtable)fields[i].get(null);
                    if (cache == null) continue;
                    ((Hashtable)cache).clear();
                    continue;
                }
                cache = (HashMap)fields[i].get(null);
                if (cache == null) continue;
                ((HashMap)cache).clear();
            }
        }
    }

    public void invokeBundleCollisionHook(CollisionHook ch, int operationType, Bundle targetBundle, Collection<Bundle> collisionCandidates) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(53, ch, operationType, targetBundle, collisionCandidates);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            ch.filterCollisions(operationType, targetBundle, collisionCandidates);
        }
    }

    public void invokeBundleFindHook(FindHook fh, BundleContext bc, Collection<Bundle> bundles) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(40, fh, bc, bundles);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            fh.find(bc, bundles);
        }
    }

    public void invokeBundleEventHook(EventHook eh, BundleEvent event, Collection<BundleContext> contexts) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(41, eh, event, contexts);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            eh.event(event, contexts);
        }
    }

    public void invokeWeavingHook(WeavingHook wh, WovenClass wc) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(42, wh, wc);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            wh.weave(wc);
        }
    }

    public void invokeServiceEventHook(org.osgi.framework.hooks.service.EventHook eh, ServiceEvent event, Collection<BundleContext> contexts) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(43, eh, event, contexts);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            eh.event(event, contexts);
        }
    }

    public void invokeServiceFindHook(org.osgi.framework.hooks.service.FindHook fh, BundleContext context, String name, String filter, boolean allServices, Collection<ServiceReference<?>> references) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(44, fh, context, name, filter, allServices ? Boolean.TRUE : Boolean.FALSE, references);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            fh.find(context, name, filter, allServices, references);
        }
    }

    public void invokeServiceListenerHookAdded(ListenerHook lh, Collection<ListenerHook.ListenerInfo> listeners) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(45, lh, listeners);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            lh.added(listeners);
        }
    }

    public void invokeServiceListenerHookRemoved(ListenerHook lh, Collection<ListenerHook.ListenerInfo> listeners) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(46, lh, listeners);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            lh.removed(listeners);
        }
    }

    public void invokeServiceEventListenerHook(EventListenerHook elh, ServiceEvent event, Map<BundleContext, Collection<ListenerHook.ListenerInfo>> listeners) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(47, elh, event, listeners);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            elh.event(event, listeners);
        }
    }

    public ResolverHook invokeResolverHookFactory(ResolverHookFactory rhf, Collection<BundleRevision> triggers) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(48, rhf, triggers);
            try {
                return (ResolverHook)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return rhf.begin(triggers);
    }

    public void invokeResolverHookResolvable(ResolverHook rh, Collection<BundleRevision> candidates) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(49, rh, candidates);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            rh.filterResolvable(candidates);
        }
    }

    public void invokeResolverHookSingleton(ResolverHook rh, BundleCapability singleton, Collection<BundleCapability> collisions) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(50, rh, singleton, collisions);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            rh.filterSingletonCollisions(singleton, collisions);
        }
    }

    public void invokeResolverHookMatches(ResolverHook rh, BundleRequirement req, Collection<BundleCapability> candidates) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(51, rh, req, candidates);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            rh.filterMatches(req, candidates);
        }
    }

    public void invokeResolverHookEnd(ResolverHook rh) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(52, rh);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            rh.end();
        }
    }

    public void invokeWovenClassListener(WovenClassListener wcl, WovenClass wc) throws Exception {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(56, wcl, wc);
            try {
                AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        } else {
            wcl.modified(wc);
        }
    }

    public <T> T run(PrivilegedAction<T> action) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(action);
        }
        return action.run();
    }

    public <T> T run(PrivilegedExceptionAction<T> action) throws Exception {
        if (System.getSecurityManager() != null) {
            try {
                return AccessController.doPrivileged(action);
            }
            catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
        return action.run();
    }

    public String getCanonicalPath(File dataFile) throws IOException {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(57, dataFile);
            try {
                return (String)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw (IOException)e.getException();
            }
        }
        return dataFile.getCanonicalPath();
    }

    public Object createProxy(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler handler) {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(58, classLoader, interfaces, handler);
            try {
                return AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw (RuntimeException)e.getException();
            }
        }
        return Proxy.newProxyInstance(classLoader, interfaces, handler);
    }

    public long getLastModified(File file) {
        if (System.getSecurityManager() != null) {
            Actions actions = (Actions)m_actions.get();
            actions.set(59, file);
            try {
                return (Long)AccessController.doPrivileged(actions, this.m_acc);
            }
            catch (PrivilegedActionException e) {
                throw (RuntimeException)e.getException();
            }
        }
        return file.lastModified();
    }

    static {
        byte[] result;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             InputStream input = SecureAction.class.getResourceAsStream("accessor.bytes");){
            byte[] buffer = new byte[input.available() > 0 ? input.available() : 1024];
            int i = input.read(buffer);
            while (i != -1) {
                output.write(buffer, 0, i);
                i = input.read(buffer);
            }
            result = output.toByteArray();
        }
        catch (Throwable t) {
            t.printStackTrace();
            result = new byte[]{};
        }
        accessor = result;
        SecureAction.getAccessor(URL.class);
        m_actions = new ThreadLocal(){

            public Object initialValue() {
                return new Actions();
            }
        };
        BUFSIZE = 4096;
        m_accessorCache = null;
    }

    private static class Actions
    implements PrivilegedExceptionAction {
        public static final int INITIALIZE_CONTEXT_ACTION = 0;
        public static final int ADD_EXTENSION_URL_ACTION = 1;
        public static final int CREATE_TMPFILE_ACTION = 2;
        public static final int CREATE_URL_ACTION = 3;
        public static final int CREATE_URL_WITH_CONTEXT_ACTION = 4;
        public static final int DELETE_FILE_ACTION = 5;
        public static final int EXEC_ACTION = 6;
        public static final int FILE_EXISTS_ACTION = 7;
        public static final int FILE_IS_DIRECTORY_ACTION = 8;
        public static final int FOR_NAME_ACTION = 9;
        public static final int GET_ABSOLUTE_PATH_ACTION = 10;
        public static final int GET_CONSTRUCTOR_ACTION = 11;
        public static final int GET_DECLARED_CONSTRUCTOR_ACTION = 12;
        public static final int GET_DECLARED_METHOD_ACTION = 13;
        public static final int GET_FIELD_ACTION = 14;
        public static final int GET_FILE_INPUT_ACTION = 15;
        public static final int GET_FILE_OUTPUT_ACTION = 16;
        public static final int TO_URI_ACTION = 17;
        public static final int GET_METHOD_ACTION = 18;
        public static final int GET_POLICY_ACTION = 19;
        public static final int GET_PROPERTY_ACTION = 20;
        public static final int GET_PARENT_CLASS_LOADER_ACTION = 21;
        public static final int GET_SYSTEM_CLASS_LOADER_ACTION = 22;
        public static final int GET_URL_INPUT_ACTION = 23;
        public static final int INVOKE_CONSTRUCTOR_ACTION = 24;
        public static final int INVOKE_DIRECTMETHOD_ACTION = 25;
        public static final int INVOKE_METHOD_ACTION = 26;
        public static final int LIST_DIRECTORY_ACTION = 27;
        public static final int MAKE_DIRECTORIES_ACTION = 28;
        public static final int MAKE_DIRECTORY_ACTION = 29;
        public static final int OPEN_ZIPFILE_ACTION = 30;
        public static final int OPEN_URLCONNECTION_ACTION = 31;
        public static final int RENAME_FILE_ACTION = 32;
        public static final int SET_ACCESSIBLE_ACTION = 33;
        public static final int START_ACTIVATOR_ACTION = 34;
        public static final int STOP_ACTIVATOR_ACTION = 35;
        public static final int SWAP_FIELD_ACTION = 36;
        public static final int SYSTEM_EXIT_ACTION = 37;
        public static final int FLUSH_FIELD_ACTION = 38;
        public static final int GET_CLASS_LOADER_ACTION = 39;
        public static final int INVOKE_BUNDLE_FIND_HOOK = 40;
        public static final int INVOKE_BUNDLE_EVENT_HOOK = 41;
        public static final int INVOKE_WEAVING_HOOK = 42;
        public static final int INVOKE_SERVICE_EVENT_HOOK = 43;
        public static final int INVOKE_SERVICE_FIND_HOOK = 44;
        public static final int INVOKE_SERVICE_LISTENER_HOOK_ADDED = 45;
        public static final int INVOKE_SERVICE_LISTENER_HOOK_REMOVED = 46;
        public static final int INVOKE_SERVICE_EVENT_LISTENER_HOOK = 47;
        public static final int INVOKE_RESOLVER_HOOK_FACTORY = 48;
        public static final int INVOKE_RESOLVER_HOOK_RESOLVABLE = 49;
        public static final int INVOKE_RESOLVER_HOOK_SINGLETON = 50;
        public static final int INVOKE_RESOLVER_HOOK_MATCHES = 51;
        public static final int INVOKE_RESOLVER_HOOK_END = 52;
        public static final int INVOKE_BUNDLE_COLLISION_HOOK = 53;
        public static final int OPEN_JARFILE_ACTION = 54;
        public static final int DELETE_FILEONEXIT_ACTION = 55;
        public static final int INVOKE_WOVEN_CLASS_LISTENER = 56;
        public static final int GET_CANONICAL_PATH = 57;
        public static final int CREATE_PROXY = 58;
        public static final int LAST_MODIFIED = 59;
        public static final int FILE_IS_FILE_ACTION = 60;
        public static final int GET_FILE_CHANNEL_ACTION = 61;
        private static final int GET_INPUT_ACTION = 62;
        private static final int GET_OUTPUT_ACTION = 63;
        private int m_action = -1;
        private Object m_arg1 = null;
        private Object m_arg2 = null;
        private Object m_arg3 = null;
        private Object m_arg4 = null;
        private Object m_arg5 = null;
        private Object m_arg6 = null;

        private Actions() {
        }

        public void set(int action) {
            this.m_action = action;
        }

        public void set(int action, Object arg1) {
            this.m_action = action;
            this.m_arg1 = arg1;
        }

        public void set(int action, Object arg1, Object arg2) {
            this.m_action = action;
            this.m_arg1 = arg1;
            this.m_arg2 = arg2;
        }

        public void set(int action, Object arg1, Object arg2, Object arg3) {
            this.m_action = action;
            this.m_arg1 = arg1;
            this.m_arg2 = arg2;
            this.m_arg3 = arg3;
        }

        public void set(int action, Object arg1, Object arg2, Object arg3, Object arg4) {
            this.m_action = action;
            this.m_arg1 = arg1;
            this.m_arg2 = arg2;
            this.m_arg3 = arg3;
            this.m_arg4 = arg4;
        }

        public void set(int action, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
            this.m_action = action;
            this.m_arg1 = arg1;
            this.m_arg2 = arg2;
            this.m_arg3 = arg3;
            this.m_arg4 = arg4;
            this.m_arg5 = arg5;
        }

        public void set(int action, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
            this.m_action = action;
            this.m_arg1 = arg1;
            this.m_arg2 = arg2;
            this.m_arg3 = arg3;
            this.m_arg4 = arg4;
            this.m_arg5 = arg5;
            this.m_arg6 = arg6;
        }

        private void unset() {
            this.m_action = -1;
            this.m_arg1 = null;
            this.m_arg2 = null;
            this.m_arg3 = null;
            this.m_arg4 = null;
            this.m_arg5 = null;
            this.m_arg6 = null;
        }

        public Object run() throws Exception {
            int action = this.m_action;
            Object arg1 = this.m_arg1;
            Object arg2 = this.m_arg2;
            Object arg3 = this.m_arg3;
            Object arg4 = this.m_arg4;
            Object arg5 = this.m_arg5;
            Object arg6 = this.m_arg6;
            this.unset();
            switch (action) {
                case 0: {
                    return AccessController.getContext();
                }
                case 1: {
                    Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    SecureAction.getAccessor(URLClassLoader.class).accept(new AccessibleObject[]{addURL});
                    addURL.invoke(arg2, arg1);
                    return null;
                }
                case 2: {
                    return File.createTempFile((String)arg1, (String)arg2, (File)arg3);
                }
                case 3: {
                    return new URL((String)arg1, (String)arg2, (Integer)arg3, (String)arg4, (URLStreamHandler)arg5);
                }
                case 4: {
                    return new URL((URL)arg1, (String)arg2, (URLStreamHandler)arg3);
                }
                case 5: {
                    return ((File)arg1).delete() ? Boolean.TRUE : Boolean.FALSE;
                }
                case 6: {
                    return Runtime.getRuntime().exec((String)arg1);
                }
                case 7: {
                    return ((File)arg1).exists() ? Boolean.TRUE : Boolean.FALSE;
                }
                case 8: {
                    return ((File)arg1).isDirectory() ? Boolean.TRUE : Boolean.FALSE;
                }
                case 9: {
                    return arg2 == null ? Class.forName((String)arg1) : Class.forName((String)arg1, true, (ClassLoader)arg2);
                }
                case 10: {
                    return ((File)arg1).getAbsolutePath();
                }
                case 11: {
                    return ((Class)arg1).getConstructor((Class[])arg2);
                }
                case 12: {
                    return ((Class)arg1).getDeclaredConstructor((Class[])arg2);
                }
                case 13: {
                    return ((Class)arg1).getDeclaredMethod((String)arg2, (Class[])arg3);
                }
                case 14: {
                    Field field = ((Class)arg1).getDeclaredField((String)arg2);
                    SecureAction.getAccessor((Class)arg1).accept(new AccessibleObject[]{field});
                    return field.get(arg3);
                }
                case 15: {
                    return new FileInputStream((File)arg1);
                }
                case 16: {
                    return new FileOutputStream((File)arg1);
                }
                case 17: {
                    return ((File)arg1).toURI();
                }
                case 18: {
                    return ((Class)arg1).getMethod((String)arg2, (Class[])arg3);
                }
                case 19: {
                    return Policy.getPolicy();
                }
                case 20: {
                    return System.getProperty((String)arg1, (String)arg2);
                }
                case 21: {
                    return ((ClassLoader)arg1).getParent();
                }
                case 22: {
                    return ClassLoader.getSystemClassLoader();
                }
                case 23: {
                    return ((URLConnection)arg1).getInputStream();
                }
                case 24: {
                    return ((Constructor)arg1).newInstance((Object[])arg2);
                }
                case 25: {
                    return ((Method)arg1).invoke(arg2, (Object[])arg3);
                }
                case 26: {
                    SecureAction.getAccessor(((Method)arg1).getDeclaringClass()).accept(new AccessibleObject[]{(Method)arg1});
                    return ((Method)arg1).invoke(arg2, (Object[])arg3);
                }
                case 27: {
                    return ((File)arg1).listFiles();
                }
                case 28: {
                    return ((File)arg1).mkdirs() ? Boolean.TRUE : Boolean.FALSE;
                }
                case 29: {
                    return ((File)arg1).mkdir() ? Boolean.TRUE : Boolean.FALSE;
                }
                case 30: {
                    return new ZipFile((File)arg1);
                }
                case 31: {
                    return ((URL)arg1).openConnection();
                }
                case 32: {
                    return ((File)arg1).renameTo((File)arg2) ? Boolean.TRUE : Boolean.FALSE;
                }
                case 33: {
                    SecureAction.getAccessor(((Executable)arg1).getDeclaringClass()).accept(new AccessibleObject[]{(Executable)arg1});
                    return null;
                }
                case 34: {
                    ((BundleActivator)arg1).start((BundleContext)arg2);
                    return null;
                }
                case 35: {
                    ((BundleActivator)arg1).stop((BundleContext)arg2);
                    return null;
                }
                case 36: {
                    return SecureAction._swapStaticFieldIfNotClass((Class)arg1, (Class)arg2, (Class)arg3, (String)arg4);
                }
                case 37: {
                    System.exit((Integer)arg1);
                }
                case 38: {
                    SecureAction._flush((Class)arg1, arg2);
                    return null;
                }
                case 39: {
                    return ((Class)arg1).getClassLoader();
                }
                case 40: {
                    ((FindHook)arg1).find((BundleContext)arg2, (Collection)arg3);
                    return null;
                }
                case 41: {
                    ((EventHook)arg1).event((BundleEvent)arg2, (Collection)arg3);
                    return null;
                }
                case 42: {
                    ((WeavingHook)arg1).weave((WovenClass)arg2);
                    return null;
                }
                case 43: {
                    ((org.osgi.framework.hooks.service.EventHook)arg1).event((ServiceEvent)arg2, (Collection)arg3);
                    return null;
                }
                case 44: {
                    ((org.osgi.framework.hooks.service.FindHook)arg1).find((BundleContext)arg2, (String)arg3, (String)arg4, (Boolean)arg5, (Collection)arg6);
                    return null;
                }
                case 45: {
                    ((ListenerHook)arg1).added((Collection)arg2);
                    return null;
                }
                case 46: {
                    ((ListenerHook)arg1).removed((Collection)arg2);
                    return null;
                }
                case 47: {
                    ((EventListenerHook)arg1).event((ServiceEvent)arg2, (Map)arg3);
                    return null;
                }
                case 48: {
                    return ((ResolverHookFactory)arg1).begin((Collection)arg2);
                }
                case 49: {
                    ((ResolverHook)arg1).filterResolvable((Collection)arg2);
                    return null;
                }
                case 50: {
                    ((ResolverHook)arg1).filterSingletonCollisions((BundleCapability)arg2, (Collection)arg3);
                    return null;
                }
                case 51: {
                    ((ResolverHook)arg1).filterMatches((BundleRequirement)arg2, (Collection)arg3);
                    return null;
                }
                case 52: {
                    ((ResolverHook)arg1).end();
                    return null;
                }
                case 53: {
                    ((CollisionHook)arg1).filterCollisions((Integer)arg2, (Bundle)arg3, (Collection)arg4);
                    return null;
                }
                case 54: {
                    return new JarFile((File)arg1);
                }
                case 55: {
                    ((File)arg1).deleteOnExit();
                    return null;
                }
                case 56: {
                    ((WovenClassListener)arg1).modified((WovenClass)arg2);
                    return null;
                }
                case 57: {
                    return ((File)arg1).getCanonicalPath();
                }
                case 58: {
                    return Proxy.newProxyInstance((ClassLoader)arg1, (Class[])arg2, (InvocationHandler)arg3);
                }
                case 59: {
                    return ((File)arg1).lastModified();
                }
                case 60: {
                    return ((File)arg1).isFile() ? Boolean.TRUE : Boolean.FALSE;
                }
                case 61: {
                    return FileChannel.open(((File)arg1).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                }
                case 62: {
                    return Files.newInputStream(((File)arg1).toPath(), new OpenOption[0]);
                }
                case 63: {
                    return Files.newOutputStream(((File)arg1).toPath(), new OpenOption[0]);
                }
            }
            return null;
        }
    }
}

