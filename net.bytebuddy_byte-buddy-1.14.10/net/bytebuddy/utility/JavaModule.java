/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.utility;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JavaModule
implements NamedElement.WithOptionalName,
AnnotationSource {
    @AlwaysNull
    public static final JavaModule UNSUPPORTED;
    protected static final Resolver RESOLVER;
    protected static final Module MODULE;
    private final AnnotatedElement module;
    private static final boolean ACCESS_CONTROLLER;

    protected JavaModule(AnnotatedElement module) {
        this.module = module;
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    @MaybeNull
    public static JavaModule ofType(Class<?> type) {
        Object module = RESOLVER.getModule(type);
        return module == null ? UNSUPPORTED : new JavaModule((AnnotatedElement)module);
    }

    public static JavaModule of(Object module) {
        if (!MODULE.isInstance(module)) {
            throw new IllegalArgumentException("Not a Java module: " + module);
        }
        return new JavaModule((AnnotatedElement)module);
    }

    public static boolean isSupported() {
        return ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).isAtLeast(ClassFileVersion.JAVA_V9);
    }

    @Override
    public boolean isNamed() {
        return MODULE.isNamed(this.module);
    }

    @Override
    public String getActualName() {
        return MODULE.getName(this.module);
    }

    public Set<String> getPackages() {
        return MODULE.getPackages(this.module);
    }

    @MaybeNull
    public InputStream getResourceAsStream(String name) throws IOException {
        return MODULE.getResourceAsStream(this.module, name);
    }

    @MaybeNull
    public ClassLoader getClassLoader() {
        return MODULE.getClassLoader(this.module);
    }

    public Object unwrap() {
        return this.module;
    }

    public boolean canRead(JavaModule module) {
        return MODULE.canRead(this.module, module.unwrap());
    }

    public boolean isExported(@MaybeNull PackageDescription packageDescription, JavaModule module) {
        return packageDescription == null || packageDescription.isDefault() || MODULE.isExported(this.module, packageDescription.getName(), module.unwrap());
    }

    public boolean isOpened(@MaybeNull PackageDescription packageDescription, JavaModule module) {
        return packageDescription == null || packageDescription.isDefault() || MODULE.isOpen(this.module, packageDescription.getName(), module.unwrap());
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return new AnnotationList.ForLoadedAnnotations(this.module.getDeclaredAnnotations());
    }

    public int hashCode() {
        return this.module.hashCode();
    }

    public boolean equals(@MaybeNull Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof JavaModule)) {
            return false;
        }
        JavaModule javaModule = (JavaModule)other;
        return this.module.equals(javaModule.module);
    }

    public String toString() {
        return this.module.toString();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
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
        UNSUPPORTED = null;
        RESOLVER = JavaModule.doPrivileged(JavaDispatcher.of(Resolver.class));
        MODULE = JavaModule.doPrivileged(JavaDispatcher.of(Module.class));
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JavaDispatcher.Proxied(value="java.lang.Module")
    protected static interface Module {
        @JavaDispatcher.Instance
        @JavaDispatcher.Proxied(value="isInstance")
        public boolean isInstance(Object var1);

        @JavaDispatcher.Proxied(value="isNamed")
        public boolean isNamed(Object var1);

        @JavaDispatcher.Proxied(value="getName")
        public String getName(Object var1);

        @JavaDispatcher.Proxied(value="getPackages")
        public Set<String> getPackages(Object var1);

        @MaybeNull
        @JavaDispatcher.Proxied(value="getClassLoader")
        public ClassLoader getClassLoader(Object var1);

        @MaybeNull
        @JavaDispatcher.Proxied(value="getResourceAsStream")
        public InputStream getResourceAsStream(Object var1, String var2) throws IOException;

        @JavaDispatcher.Proxied(value="isExported")
        public boolean isExported(Object var1, String var2, @JavaDispatcher.Proxied(value="java.lang.Module") Object var3);

        @JavaDispatcher.Proxied(value="isOpen")
        public boolean isOpen(Object var1, String var2, @JavaDispatcher.Proxied(value="java.lang.Module") Object var3);

        @JavaDispatcher.Proxied(value="canRead")
        public boolean canRead(Object var1, @JavaDispatcher.Proxied(value="java.lang.Module") Object var2);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @JavaDispatcher.Proxied(value="java.lang.Class")
    protected static interface Resolver {
        @MaybeNull
        @JavaDispatcher.Defaults
        @JavaDispatcher.Proxied(value="getModule")
        public Object getModule(Class<?> var1);
    }
}

