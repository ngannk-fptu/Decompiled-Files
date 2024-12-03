/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FileSystem {
    private static /* synthetic */ FileSystem INSTANCE;
    private static final boolean ACCESS_CONTROLLER;

    @CachedReturnPlugin.Enhance(value="INSTANCE")
    public static FileSystem getInstance() {
        FileSystem fileSystem;
        FileSystem fileSystem2;
        FileSystem fileSystem3 = INSTANCE;
        if (fileSystem3 != null) {
            fileSystem2 = null;
        } else {
            try {
                Class.forName("java.nio.file.Files", false, ClassLoadingStrategy.BOOTSTRAP_LOADER);
                fileSystem2 = new ForNio2CapableVm();
            }
            catch (ClassNotFoundException ignored) {
                fileSystem2 = fileSystem = new ForLegacyVm();
            }
        }
        if (fileSystem == null) {
            fileSystem = INSTANCE;
        } else {
            INSTANCE = fileSystem;
        }
        return fileSystem;
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    public abstract void copy(File var1, File var2) throws IOException;

    public abstract void move(File var1, File var2) throws IOException;

    static /* synthetic */ Object access$000(PrivilegedAction x0) {
        return FileSystem.doPrivileged(x0);
    }

    static {
        try {
            Class.forName("java.security.AccessController", false, null);
            ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
        }
        catch (ClassNotFoundException classNotFoundException) {
            ACCESS_CONTROLLER = false;
        }
        catch (SecurityException securityException) {
            ACCESS_CONTROLLER = true;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ForNio2CapableVm
    extends FileSystem {
        private static final Dispatcher DISPATCHER = (Dispatcher)FileSystem.access$000(JavaDispatcher.of(Dispatcher.class));
        private static final Files FILES = (Files)FileSystem.access$000(JavaDispatcher.of(Files.class));
        private static final StandardCopyOption STANDARD_COPY_OPTION = (StandardCopyOption)FileSystem.access$000(JavaDispatcher.of(StandardCopyOption.class));

        protected ForNio2CapableVm() {
        }

        public void copy(File source, File target) throws IOException {
            Object[] option = STANDARD_COPY_OPTION.toArray(1);
            option[0] = STANDARD_COPY_OPTION.valueOf("REPLACE_EXISTING");
            FILES.copy(DISPATCHER.toPath(source), DISPATCHER.toPath(target), option);
        }

        public void move(File source, File target) throws IOException {
            Object[] option = STANDARD_COPY_OPTION.toArray(1);
            option[0] = STANDARD_COPY_OPTION.valueOf("REPLACE_EXISTING");
            FILES.move(DISPATCHER.toPath(source), DISPATCHER.toPath(target), option);
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            return this.getClass() == object.getClass();
        }

        public int hashCode() {
            return this.getClass().hashCode();
        }

        @JavaDispatcher.Proxied(value="java.nio.file.StandardCopyOption")
        protected static interface StandardCopyOption {
            @JavaDispatcher.Container
            @JavaDispatcher.Proxied(value="toArray")
            public Object[] toArray(int var1);

            @JavaDispatcher.IsStatic
            @JavaDispatcher.Proxied(value="valueOf")
            public Object valueOf(String var1);
        }

        @JavaDispatcher.Proxied(value="java.nio.file.Files")
        protected static interface Files {
            @JavaDispatcher.IsStatic
            @JavaDispatcher.Proxied(value="copy")
            public Object copy(@JavaDispatcher.Proxied(value="java.nio.file.Path") Object var1, @JavaDispatcher.Proxied(value="java.nio.file.Path") Object var2, @JavaDispatcher.Proxied(value="java.nio.file.CopyOption") Object[] var3) throws IOException;

            @JavaDispatcher.IsStatic
            @JavaDispatcher.Proxied(value="move")
            public Object move(@JavaDispatcher.Proxied(value="java.nio.file.Path") Object var1, @JavaDispatcher.Proxied(value="java.nio.file.Path") Object var2, @JavaDispatcher.Proxied(value="java.nio.file.CopyOption") Object[] var3) throws IOException;
        }

        @JavaDispatcher.Proxied(value="java.io.File")
        protected static interface Dispatcher {
            @JavaDispatcher.Proxied(value="toPath")
            public Object toPath(File var1) throws IOException;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class ForLegacyVm
    extends FileSystem {
        protected ForLegacyVm() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void copy(File source, File target) throws IOException {
            FileInputStream inputStream = new FileInputStream(source);
            try {
                FileOutputStream outputStream = new FileOutputStream(target);
                try {
                    int length;
                    byte[] buffer = new byte[1024];
                    while ((length = ((InputStream)inputStream).read(buffer)) != -1) {
                        ((OutputStream)outputStream).write(buffer, 0, length);
                    }
                    Object var8_7 = null;
                }
                catch (Throwable throwable) {
                    Object var8_8 = null;
                    ((OutputStream)outputStream).close();
                    throw throwable;
                }
                ((OutputStream)outputStream).close();
                Object var10_10 = null;
            }
            catch (Throwable throwable) {
                Object var10_11 = null;
                ((InputStream)inputStream).close();
                throw throwable;
            }
            ((InputStream)inputStream).close();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void move(File source, File target) throws IOException {
            FileInputStream inputStream = new FileInputStream(source);
            try {
                FileOutputStream outputStream = new FileOutputStream(target);
                try {
                    int length;
                    byte[] buffer = new byte[1024];
                    while ((length = ((InputStream)inputStream).read(buffer)) != -1) {
                        ((OutputStream)outputStream).write(buffer, 0, length);
                    }
                    Object var8_7 = null;
                }
                catch (Throwable throwable) {
                    Object var8_8 = null;
                    ((OutputStream)outputStream).close();
                    throw throwable;
                }
                ((OutputStream)outputStream).close();
                Object var10_10 = null;
            }
            catch (Throwable throwable) {
                Object var10_11 = null;
                ((InputStream)inputStream).close();
                throw throwable;
            }
            ((InputStream)inputStream).close();
            if (!source.delete()) {
                source.deleteOnExit();
            }
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            return this.getClass() == object.getClass();
        }

        public int hashCode() {
            return this.getClass().hashCode();
        }
    }
}

