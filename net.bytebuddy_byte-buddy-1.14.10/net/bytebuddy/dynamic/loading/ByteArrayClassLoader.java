/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic.loading;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLStreamHandler;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassFilePostProcessor;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.utility.GraalImageCode;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ByteArrayClassLoader
extends InjectionClassLoader {
    public static final String URL_SCHEMA = "bytebuddy";
    private static final int FROM_BEGINNING = 0;
    @AlwaysNull
    private static final URL NO_URL;
    private static final PackageLookupStrategy PACKAGE_LOOKUP_STRATEGY;
    protected static final SynchronizationStrategy.Initializable SYNCHRONIZATION_STRATEGY;
    protected final ConcurrentMap<String, byte[]> typeDefinitions;
    protected final PersistenceHandler persistenceHandler;
    @MaybeNull
    protected final ProtectionDomain protectionDomain;
    protected final PackageDefinitionStrategy packageDefinitionStrategy;
    protected final ClassFilePostProcessor classFilePostProcessor;
    @MaybeNull
    protected final Object accessControlContext;
    private static final boolean ACCESS_CONTROLLER;

    @SuppressFBWarnings(value={"DP_DO_INSIDE_DO_PRIVILEGED"}, justification="Must be invoked from targeting class loader type.")
    private static void doRegisterAsParallelCapable() {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable", new Class[0]);
            method.setAccessible(true);
            method.invoke(null, new Object[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    public ByteArrayClassLoader(@MaybeNull ClassLoader parent, Map<String, byte[]> typeDefinitions) {
        this(parent, true, typeDefinitions);
    }

    public ByteArrayClassLoader(@MaybeNull ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions) {
        this(parent, sealed, typeDefinitions, PersistenceHandler.LATENT);
    }

    public ByteArrayClassLoader(@MaybeNull ClassLoader parent, Map<String, byte[]> typeDefinitions, PersistenceHandler persistenceHandler) {
        this(parent, true, typeDefinitions, persistenceHandler);
    }

    public ByteArrayClassLoader(@MaybeNull ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions, PersistenceHandler persistenceHandler) {
        this(parent, sealed, typeDefinitions, ClassLoadingStrategy.NO_PROTECTION_DOMAIN, persistenceHandler, PackageDefinitionStrategy.Trivial.INSTANCE);
    }

    public ByteArrayClassLoader(@MaybeNull ClassLoader parent, Map<String, byte[]> typeDefinitions, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy) {
        this(parent, true, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy);
    }

    public ByteArrayClassLoader(@MaybeNull ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy) {
        this(parent, sealed, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy, ClassFilePostProcessor.NoOp.INSTANCE);
    }

    public ByteArrayClassLoader(@MaybeNull ClassLoader parent, Map<String, byte[]> typeDefinitions, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy, ClassFilePostProcessor classFilePostProcessor) {
        this(parent, true, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy, classFilePostProcessor);
    }

    public ByteArrayClassLoader(@MaybeNull ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy, ClassFilePostProcessor classFilePostProcessor) {
        super(parent, sealed);
        this.typeDefinitions = new ConcurrentHashMap<String, byte[]>(typeDefinitions);
        this.protectionDomain = protectionDomain;
        this.persistenceHandler = persistenceHandler;
        this.packageDefinitionStrategy = packageDefinitionStrategy;
        this.classFilePostProcessor = classFilePostProcessor;
        this.accessControlContext = ByteArrayClassLoader.getContext();
    }

    @MaybeNull
    @AccessControllerPlugin.Enhance
    private static Object getContext() {
        if (ACCESS_CONTROLLER) {
            return AccessController.getContext();
        }
        return null;
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction, @MaybeNull Object object) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction, (AccessControlContext)object);
        }
        return action.run();
    }

    private static Object methodHandle() throws Exception {
        return Class.forName("java.lang.invoke.MethodHandles").getMethod("lookup", new Class[0]).invoke(null, new Object[0]);
    }

    public static Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
        return ByteArrayClassLoader.load(classLoader, types, ClassLoadingStrategy.NO_PROTECTION_DOMAIN, PersistenceHandler.LATENT, PackageDefinitionStrategy.Trivial.INSTANCE, false, true);
    }

    @SuppressFBWarnings(value={"DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED"}, justification="Assuring privilege is explicit user responsibility.")
    public static Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy, boolean forbidExisting, boolean sealed) {
        HashMap<String, byte[]> typesByName = new HashMap<String, byte[]>();
        for (Map.Entry<TypeDescription, byte[]> entry : types.entrySet()) {
            typesByName.put(entry.getKey().getName(), entry.getValue());
        }
        classLoader = new ByteArrayClassLoader(classLoader, sealed, typesByName, protectionDomain, persistenceHandler, packageDefinitionStrategy, ClassFilePostProcessor.NoOp.INSTANCE);
        LinkedHashMap result = new LinkedHashMap();
        for (TypeDescription typeDescription : types.keySet()) {
            try {
                Class<?> type = Class.forName(typeDescription.getName(), false, classLoader);
                if (!GraalImageCode.getCurrent().isNativeImageExecution() && forbidExisting && type.getClassLoader() != classLoader) {
                    throw new IllegalStateException("Class already loaded: " + type);
                }
                result.put(typeDescription, type);
            }
            catch (ClassNotFoundException exception) {
                throw new IllegalStateException("Cannot load class " + typeDescription, exception);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Map<String, Class<?>> doDefineClasses(Map<String, byte[]> typeDefinitions) throws ClassNotFoundException {
        LinkedHashMap<String, Class<?>> linkedHashMap;
        HashMap<String, byte[]> previous = new HashMap<String, byte[]>();
        for (Map.Entry<String, byte[]> entry : typeDefinitions.entrySet()) {
            previous.put(entry.getKey(), this.typeDefinitions.putIfAbsent(entry.getKey(), entry.getValue()));
        }
        try {
            LinkedHashMap types = new LinkedHashMap();
            for (String name : typeDefinitions.keySet()) {
                Object object = SYNCHRONIZATION_STRATEGY.initialize().getClassLoadingLock(this, name);
                synchronized (object) {
                    types.put(name, this.loadClass(name));
                }
            }
            linkedHashMap = types;
            Object var9_8 = null;
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            for (Map.Entry entry : previous.entrySet()) {
                if (entry.getValue() == null) {
                    this.persistenceHandler.release((String)entry.getKey(), this.typeDefinitions);
                    continue;
                }
                this.typeDefinitions.put((String)entry.getKey(), (byte[])entry.getValue());
            }
            throw throwable;
        }
        for (Map.Entry entry : previous.entrySet()) {
            if (entry.getValue() == null) {
                this.persistenceHandler.release((String)entry.getKey(), this.typeDefinitions);
                continue;
            }
            this.typeDefinitions.put((String)entry.getKey(), (byte[])entry.getValue());
        }
        return linkedHashMap;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] binaryRepresentation = this.persistenceHandler.lookup(name, this.typeDefinitions);
        if (binaryRepresentation == null) {
            throw new ClassNotFoundException(name);
        }
        return (Class)ByteArrayClassLoader.doPrivileged(new ClassDefinitionAction(name, this.classFilePostProcessor.transform(this, name, this.protectionDomain, binaryRepresentation)), this.accessControlContext);
    }

    @Override
    @MaybeNull
    protected URL findResource(String name) {
        return this.persistenceHandler.url(name, this.typeDefinitions);
    }

    @Override
    protected Enumeration<URL> findResources(String name) {
        URL url = this.persistenceHandler.url(name, this.typeDefinitions);
        return url == null ? EmptyEnumeration.INSTANCE : new SingletonEnumeration(url);
    }

    @MaybeNull
    private Package doGetPackage(String name) {
        return this.getPackage(name);
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
        NO_URL = null;
        PACKAGE_LOOKUP_STRATEGY = ByteArrayClassLoader.doPrivileged(PackageLookupStrategy.CreationAction.INSTANCE);
        SYNCHRONIZATION_STRATEGY = ByteArrayClassLoader.doPrivileged(SynchronizationStrategy.CreationAction.INSTANCE);
        ByteArrayClassLoader.doRegisterAsParallelCapable();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class SingletonEnumeration
    implements Enumeration<URL> {
        @MaybeNull
        private URL element;

        protected SingletonEnumeration(URL element) {
            this.element = element;
        }

        @Override
        public boolean hasMoreElements() {
            return this.element != null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public URL nextElement() {
            if (this.element == null) {
                throw new NoSuchElementException();
            }
            try {
                URL uRL = this.element;
                Object var3_2 = null;
                this.element = null;
                return uRL;
            }
            catch (Throwable throwable) {
                Object var3_3 = null;
                this.element = null;
                throw throwable;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static enum EmptyEnumeration implements Enumeration<URL>
    {
        INSTANCE;


        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public URL nextElement() {
            throw new NoSuchElementException();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ChildFirst
    extends ByteArrayClassLoader {
        private static final String CLASS_FILE_SUFFIX = ".class";

        @SuppressFBWarnings(value={"DP_DO_INSIDE_DO_PRIVILEGED"}, justification="Must be invoked from targeting class loader type.")
        private static void doRegisterAsParallelCapable() {
            try {
                Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable", new Class[0]);
                method.setAccessible(true);
                method.invoke(null, new Object[0]);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }

        public ChildFirst(@MaybeNull ClassLoader parent, Map<String, byte[]> typeDefinitions) {
            super(parent, typeDefinitions);
        }

        public ChildFirst(@MaybeNull ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions) {
            super(parent, sealed, typeDefinitions);
        }

        public ChildFirst(@MaybeNull ClassLoader parent, Map<String, byte[]> typeDefinitions, PersistenceHandler persistenceHandler) {
            super(parent, typeDefinitions, persistenceHandler);
        }

        public ChildFirst(@MaybeNull ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions, PersistenceHandler persistenceHandler) {
            super(parent, sealed, typeDefinitions, persistenceHandler);
        }

        public ChildFirst(@MaybeNull ClassLoader parent, Map<String, byte[]> typeDefinitions, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy) {
            super(parent, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy);
        }

        public ChildFirst(@MaybeNull ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy) {
            super(parent, sealed, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy);
        }

        public ChildFirst(@MaybeNull ClassLoader parent, Map<String, byte[]> typeDefinitions, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy, ClassFilePostProcessor classFilePostProcessor) {
            super(parent, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy, classFilePostProcessor);
        }

        public ChildFirst(@MaybeNull ClassLoader parent, boolean sealed, Map<String, byte[]> typeDefinitions, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy, ClassFilePostProcessor classFilePostProcessor) {
            super(parent, sealed, typeDefinitions, protectionDomain, persistenceHandler, packageDefinitionStrategy, classFilePostProcessor);
        }

        public static Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
            return ChildFirst.load(classLoader, types, ClassLoadingStrategy.NO_PROTECTION_DOMAIN, PersistenceHandler.LATENT, PackageDefinitionStrategy.Trivial.INSTANCE, false, true);
        }

        @SuppressFBWarnings(value={"DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED"}, justification="Assuring privilege is explicit user responsibility.")
        public static Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types, @MaybeNull ProtectionDomain protectionDomain, PersistenceHandler persistenceHandler, PackageDefinitionStrategy packageDefinitionStrategy, boolean forbidExisting, boolean sealed) {
            HashMap<String, byte[]> typesByName = new HashMap<String, byte[]>();
            for (Map.Entry<TypeDescription, byte[]> entry : types.entrySet()) {
                typesByName.put(entry.getKey().getName(), entry.getValue());
            }
            classLoader = new ChildFirst(classLoader, sealed, typesByName, protectionDomain, persistenceHandler, packageDefinitionStrategy, ClassFilePostProcessor.NoOp.INSTANCE);
            LinkedHashMap result = new LinkedHashMap();
            for (TypeDescription typeDescription : types.keySet()) {
                try {
                    Class<?> type = Class.forName(typeDescription.getName(), false, classLoader);
                    if (!GraalImageCode.getCurrent().isNativeImageExecution() && forbidExisting && type.getClassLoader() != classLoader) {
                        throw new IllegalStateException("Class already loaded: " + type);
                    }
                    result.put(typeDescription, type);
                }
                catch (ClassNotFoundException exception) {
                    throw new IllegalStateException("Cannot load class " + typeDescription, exception);
                }
            }
            return result;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Object object = SYNCHRONIZATION_STRATEGY.initialize().getClassLoadingLock(this, name);
            synchronized (object) {
                Class<?> type = this.findLoadedClass(name);
                if (type != null) {
                    return type;
                }
                try {
                    type = this.findClass(name);
                    if (resolve) {
                        this.resolveClass(type);
                    }
                    return type;
                }
                catch (ClassNotFoundException exception) {
                    return super.loadClass(name, resolve);
                }
            }
        }

        @Override
        public URL getResource(String name) {
            URL url = this.persistenceHandler.url(name, this.typeDefinitions);
            return url != null || this.isShadowed(name) ? url : super.getResource(name);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            URL url = this.persistenceHandler.url(name, this.typeDefinitions);
            return url == null ? super.getResources(name) : new PrependingEnumeration(url, super.getResources(name));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean isShadowed(String resourceName) {
            if (this.persistenceHandler.isManifest() || !resourceName.endsWith(CLASS_FILE_SUFFIX)) {
                return false;
            }
            ChildFirst childFirst = this;
            synchronized (childFirst) {
                String typeName = resourceName.replace('/', '.').substring(0, resourceName.length() - CLASS_FILE_SUFFIX.length());
                if (this.typeDefinitions.containsKey(typeName)) {
                    return true;
                }
                // MONITOREXIT @DISABLED, blocks:[0, 1] lbl10 : MonitorExitStatement: MONITOREXIT : var2_2
                Class<?> loadedClass = this.findLoadedClass(typeName);
                return loadedClass != null && loadedClass.getClassLoader() == this;
            }
        }

        static {
            ChildFirst.doRegisterAsParallelCapable();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class PrependingEnumeration
        implements Enumeration<URL> {
            @MaybeNull
            private URL nextElement;
            private final Enumeration<URL> enumeration;

            protected PrependingEnumeration(URL url, Enumeration<URL> enumeration) {
                this.nextElement = url;
                this.enumeration = enumeration;
            }

            @Override
            public boolean hasMoreElements() {
                return this.nextElement != null && this.enumeration.hasMoreElements();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public URL nextElement() {
                if (this.nextElement != null && this.enumeration.hasMoreElements()) {
                    try {
                        URL uRL = this.nextElement;
                        Object var3_2 = null;
                        this.nextElement = this.enumeration.nextElement();
                        return uRL;
                    }
                    catch (Throwable throwable) {
                        Object var3_3 = null;
                        this.nextElement = this.enumeration.nextElement();
                        throw throwable;
                    }
                }
                throw new NoSuchElementException();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum PersistenceHandler {
        MANIFEST(true){

            @Override
            protected byte[] lookup(String name, ConcurrentMap<String, byte[]> typeDefinitions) {
                return (byte[])typeDefinitions.get(name);
            }

            @Override
            protected URL url(String resourceName, ConcurrentMap<String, byte[]> typeDefinitions) {
                String typeName;
                byte[] binaryRepresentation;
                if (!resourceName.endsWith(PersistenceHandler.CLASS_FILE_SUFFIX)) {
                    return NO_URL;
                }
                if (resourceName.startsWith("/")) {
                    resourceName = resourceName.substring(1);
                }
                return (binaryRepresentation = (byte[])typeDefinitions.get(typeName = resourceName.replace('/', '.').substring(0, resourceName.length() - PersistenceHandler.CLASS_FILE_SUFFIX.length()))) == null ? NO_URL : (URL)ByteArrayClassLoader.doPrivileged(new UrlDefinitionAction(resourceName, binaryRepresentation));
            }

            @Override
            protected void release(String name, ConcurrentMap<String, byte[]> typeDefinitions) {
            }
        }
        ,
        LATENT(false){

            @Override
            protected byte[] lookup(String name, ConcurrentMap<String, byte[]> typeDefinitions) {
                return (byte[])typeDefinitions.remove(name);
            }

            @Override
            protected URL url(String resourceName, ConcurrentMap<String, byte[]> typeDefinitions) {
                return NO_URL;
            }

            @Override
            protected void release(String name, ConcurrentMap<String, byte[]> typeDefinitions) {
                typeDefinitions.remove(name);
            }
        };

        private static final String CLASS_FILE_SUFFIX = ".class";
        private final boolean manifest;

        private PersistenceHandler(boolean manifest) {
            this.manifest = manifest;
        }

        public boolean isManifest() {
            return this.manifest;
        }

        @MaybeNull
        protected abstract byte[] lookup(String var1, ConcurrentMap<String, byte[]> var2);

        @MaybeNull
        protected abstract URL url(String var1, ConcurrentMap<String, byte[]> var2);

        protected abstract void release(String var1, ConcurrentMap<String, byte[]> var2);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class UrlDefinitionAction
        implements PrivilegedAction<URL> {
            private static final Dispatcher DISPATCHER;
            private static final String ENCODING = "UTF-8";
            private static final int NO_PORT = -1;
            private static final String NO_FILE = "";
            private final String typeName;
            private final byte[] binaryRepresentation;
            private static final boolean ACCESS_CONTROLLER;

            protected UrlDefinitionAction(String typeName, byte[] binaryRepresentation) {
                this.typeName = typeName;
                this.binaryRepresentation = binaryRepresentation;
            }

            @AccessControllerPlugin.Enhance
            private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
                PrivilegedAction<T> action;
                if (ACCESS_CONTROLLER) {
                    return AccessController.doPrivileged(privilegedAction);
                }
                return action.run();
            }

            @Override
            public URL run() {
                try {
                    String path = URLEncoder.encode(this.typeName.replace('.', '/'), ENCODING);
                    ByteArrayUrlStreamHandler handler = new ByteArrayUrlStreamHandler(this.binaryRepresentation);
                    URL url = DISPATCHER.of(URI.create("bytebuddy://" + path), handler);
                    return url == null ? DISPATCHER.make(ByteArrayClassLoader.URL_SCHEMA, path, -1, NO_FILE, handler) : url;
                }
                catch (MalformedURLException exception) {
                    throw new IllegalStateException("Cannot create URL for " + this.typeName, exception);
                }
                catch (UnsupportedEncodingException exception) {
                    throw new IllegalStateException("Could not find encoding: UTF-8", exception);
                }
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
                DISPATCHER = UrlDefinitionAction.doPrivileged(JavaDispatcher.of(Dispatcher.class));
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                if (!this.typeName.equals(((UrlDefinitionAction)object).typeName)) {
                    return false;
                }
                return Arrays.equals(this.binaryRepresentation, ((UrlDefinitionAction)object).binaryRepresentation);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.typeName.hashCode()) * 31 + Arrays.hashCode(this.binaryRepresentation);
            }

            @JavaDispatcher.Proxied(value="java.net.URL")
            protected static interface Dispatcher {
                @JavaDispatcher.IsConstructor
                @JavaDispatcher.Proxied(value="make")
                public URL make(String var1, String var2, int var3, String var4, URLStreamHandler var5) throws MalformedURLException;

                @MaybeNull
                @JavaDispatcher.IsStatic
                @JavaDispatcher.Defaults
                @JavaDispatcher.Proxied(value="of")
                public URL of(URI var1, URLStreamHandler var2) throws MalformedURLException;
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class ByteArrayUrlStreamHandler
            extends URLStreamHandler {
                private final byte[] binaryRepresentation;

                protected ByteArrayUrlStreamHandler(byte[] binaryRepresentation) {
                    this.binaryRepresentation = binaryRepresentation;
                }

                protected URLConnection openConnection(URL url) {
                    return new ByteArrayUrlConnection(url, new ByteArrayInputStream(this.binaryRepresentation));
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return Arrays.equals(this.binaryRepresentation, ((ByteArrayUrlStreamHandler)object).binaryRepresentation);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + Arrays.hashCode(this.binaryRepresentation);
                }

                protected static class ByteArrayUrlConnection
                extends URLConnection {
                    private final InputStream inputStream;

                    protected ByteArrayUrlConnection(URL url, InputStream inputStream) {
                        super(url);
                        this.inputStream = inputStream;
                    }

                    public void connect() {
                        this.connected = true;
                    }

                    public InputStream getInputStream() {
                        this.connect();
                        return this.inputStream;
                    }
                }
            }
        }
    }

    protected static interface PackageLookupStrategy {
        @MaybeNull
        public Package apply(ByteArrayClassLoader var1, String var2);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForJava9CapableVm
        implements PackageLookupStrategy {
            private final Method getDefinedPackage;

            protected ForJava9CapableVm(Method getDefinedPackage) {
                this.getDefinedPackage = getDefinedPackage;
            }

            @MaybeNull
            public Package apply(ByteArrayClassLoader classLoader, String name) {
                try {
                    return (Package)this.getDefinedPackage.invoke((Object)classLoader, name);
                }
                catch (IllegalAccessException exception) {
                    throw new IllegalStateException(exception);
                }
                catch (InvocationTargetException exception) {
                    throw new IllegalStateException(exception.getTargetException());
                }
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.getDefinedPackage.equals(((ForJava9CapableVm)object).getDefinedPackage);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.getDefinedPackage.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForLegacyVm implements PackageLookupStrategy
        {
            INSTANCE;


            @Override
            @MaybeNull
            public Package apply(ByteArrayClassLoader classLoader, String name) {
                return classLoader.doGetPackage(name);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum CreationAction implements PrivilegedAction<PackageLookupStrategy>
        {
            INSTANCE;


            @Override
            @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
            public PackageLookupStrategy run() {
                if (JavaModule.isSupported()) {
                    try {
                        return new ForJava9CapableVm(ClassLoader.class.getMethod("getDefinedPackage", String.class));
                    }
                    catch (Exception ignored) {
                        return ForLegacyVm.INSTANCE;
                    }
                }
                return ForLegacyVm.INSTANCE;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class ClassDefinitionAction
    implements PrivilegedAction<Class<?>> {
        private final String name;
        private final byte[] binaryRepresentation;

        protected ClassDefinitionAction(String name, byte[] binaryRepresentation) {
            this.name = name;
            this.binaryRepresentation = binaryRepresentation;
        }

        @Override
        public Class<?> run() {
            String packageName;
            PackageDefinitionStrategy.Definition definition;
            int packageIndex = this.name.lastIndexOf(46);
            if (packageIndex != -1 && (definition = ByteArrayClassLoader.this.packageDefinitionStrategy.define(ByteArrayClassLoader.this, packageName = this.name.substring(0, packageIndex), this.name)).isDefined()) {
                Package definedPackage = PACKAGE_LOOKUP_STRATEGY.apply(ByteArrayClassLoader.this, packageName);
                if (definedPackage == null) {
                    ByteArrayClassLoader.this.definePackage(packageName, definition.getSpecificationTitle(), definition.getSpecificationVersion(), definition.getSpecificationVendor(), definition.getImplementationTitle(), definition.getImplementationVersion(), definition.getImplementationVendor(), definition.getSealBase());
                } else if (!definition.isCompatibleTo(definedPackage)) {
                    throw new SecurityException("Sealing violation for package " + packageName);
                }
            }
            return ByteArrayClassLoader.this.defineClass(this.name, this.binaryRepresentation, 0, this.binaryRepresentation.length, ByteArrayClassLoader.this.protectionDomain);
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            if (!this.name.equals(((ClassDefinitionAction)object).name)) {
                return false;
            }
            if (!Arrays.equals(this.binaryRepresentation, ((ClassDefinitionAction)object).binaryRepresentation)) {
                return false;
            }
            return ByteArrayClassLoader.this.equals(((ClassDefinitionAction)object).ByteArrayClassLoader.this);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + Arrays.hashCode(this.binaryRepresentation)) * 31 + ByteArrayClassLoader.this.hashCode();
        }
    }

    protected static interface SynchronizationStrategy {
        public Object getClassLoadingLock(ByteArrayClassLoader var1, String var2);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForJava8CapableVm
        implements SynchronizationStrategy,
        Initializable {
            private final Object methodHandle;
            private final Method bindTo;
            private final Method invokeWithArguments;

            protected ForJava8CapableVm(Object methodHandle, Method bindTo, Method invokeWithArguments) {
                this.methodHandle = methodHandle;
                this.bindTo = bindTo;
                this.invokeWithArguments = invokeWithArguments;
            }

            public SynchronizationStrategy initialize() {
                return this;
            }

            public Object getClassLoadingLock(ByteArrayClassLoader classLoader, String name) {
                try {
                    return this.invokeWithArguments.invoke(this.bindTo.invoke(this.methodHandle, classLoader), new Object[]{new Object[]{name}});
                }
                catch (IllegalAccessException exception) {
                    throw new IllegalStateException(exception);
                }
                catch (InvocationTargetException exception) {
                    throw new IllegalStateException(exception.getTargetException());
                }
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                if (!this.methodHandle.equals(((ForJava8CapableVm)object).methodHandle)) {
                    return false;
                }
                if (!this.bindTo.equals(((ForJava8CapableVm)object).bindTo)) {
                    return false;
                }
                return this.invokeWithArguments.equals(((ForJava8CapableVm)object).invokeWithArguments);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.methodHandle.hashCode()) * 31 + this.bindTo.hashCode()) * 31 + this.invokeWithArguments.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForJava7CapableVm
        implements SynchronizationStrategy,
        Initializable {
            private final Method method;

            protected ForJava7CapableVm(Method method) {
                this.method = method;
            }

            public Object getClassLoadingLock(ByteArrayClassLoader classLoader, String name) {
                try {
                    return this.method.invoke((Object)classLoader, name);
                }
                catch (IllegalAccessException exception) {
                    throw new IllegalStateException(exception);
                }
                catch (InvocationTargetException exception) {
                    throw new IllegalStateException(exception.getTargetException());
                }
            }

            @SuppressFBWarnings(value={"DP_DO_INSIDE_DO_PRIVILEGED"}, justification="Assuring privilege is explicit user responsibility.")
            public SynchronizationStrategy initialize() {
                try {
                    this.method.setAccessible(true);
                    return this;
                }
                catch (Exception ignored) {
                    return ForLegacyVm.INSTANCE;
                }
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.method.equals(((ForJava7CapableVm)object).method);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.method.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForLegacyVm implements SynchronizationStrategy,
        Initializable
        {
            INSTANCE;


            @Override
            public Object getClassLoadingLock(ByteArrayClassLoader classLoader, String name) {
                return classLoader;
            }

            @Override
            public SynchronizationStrategy initialize() {
                return this;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum CreationAction implements PrivilegedAction<Initializable>
        {
            INSTANCE;


            @Override
            @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
            public Initializable run() {
                try {
                    Class<?> methodType = Class.forName("java.lang.invoke.MethodType");
                    Class<?> methodHandle = Class.forName("java.lang.invoke.MethodHandle");
                    return new ForJava8CapableVm(Class.forName("java.lang.invoke.MethodHandles$Lookup").getMethod("findVirtual", Class.class, String.class, methodType).invoke(ByteArrayClassLoader.methodHandle(), ClassLoader.class, "getClassLoadingLock", methodType.getMethod("methodType", Class.class, Class[].class).invoke(null, Object.class, new Class[]{String.class})), methodHandle.getMethod("bindTo", Object.class), methodHandle.getMethod("invokeWithArguments", Object[].class));
                }
                catch (Exception ignored) {
                    try {
                        return (Initializable)((Object)(ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V5).isAtLeast(ClassFileVersion.JAVA_V9) && ByteArrayClassLoader.class.getClassLoader() == null ? ForLegacyVm.INSTANCE : new ForJava7CapableVm(ClassLoader.class.getDeclaredMethod("getClassLoadingLock", String.class))));
                    }
                    catch (Exception ignored2) {
                        return ForLegacyVm.INSTANCE;
                    }
                }
            }
        }

        public static interface Initializable {
            public SynchronizationStrategy initialize();
        }
    }
}

