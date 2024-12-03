/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.loading;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.Callable;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ClassLoadingStrategy<T extends ClassLoader> {
    @AlwaysNull
    public static final ClassLoader BOOTSTRAP_LOADER = null;
    @AlwaysNull
    public static final ProtectionDomain NO_PROTECTION_DOMAIN = null;

    public Map<TypeDescription, Class<?>> load(@MaybeNull T var1, Map<TypeDescription, byte[]> var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForJnaInjection
    implements ClassLoadingStrategy<ClassLoader> {
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final ProtectionDomain protectionDomain;

        public ForJnaInjection() {
            this(NO_PROTECTION_DOMAIN);
        }

        public ForJnaInjection(@MaybeNull ProtectionDomain protectionDomain) {
            this.protectionDomain = protectionDomain;
        }

        @Override
        public Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
            return new ClassInjector.UsingUnsafe(classLoader, this.protectionDomain).inject(types);
        }

        public boolean equals(@MaybeNull Object object) {
            block10: {
                block9: {
                    ProtectionDomain protectionDomain;
                    block8: {
                        ProtectionDomain protectionDomain2;
                        if (this == object) {
                            return true;
                        }
                        if (object == null) {
                            return false;
                        }
                        if (this.getClass() != object.getClass()) {
                            return false;
                        }
                        ProtectionDomain protectionDomain3 = ((ForJnaInjection)object).protectionDomain;
                        protectionDomain = protectionDomain2 = this.protectionDomain;
                        if (protectionDomain3 == null) break block8;
                        if (protectionDomain == null) break block9;
                        if (!protectionDomain2.equals(protectionDomain3)) {
                            return false;
                        }
                        break block10;
                    }
                    if (protectionDomain == null) break block10;
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            int n = this.getClass().hashCode() * 31;
            ProtectionDomain protectionDomain = this.protectionDomain;
            if (protectionDomain != null) {
                n = n + protectionDomain.hashCode();
            }
            return n;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForUnsafeInjection
    implements ClassLoadingStrategy<ClassLoader> {
        @MaybeNull
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
        private final ProtectionDomain protectionDomain;

        public ForUnsafeInjection() {
            this(NO_PROTECTION_DOMAIN);
        }

        public ForUnsafeInjection(@MaybeNull ProtectionDomain protectionDomain) {
            this.protectionDomain = protectionDomain;
        }

        @Override
        public Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
            return new ClassInjector.UsingUnsafe(classLoader, this.protectionDomain).inject(types);
        }

        public boolean equals(@MaybeNull Object object) {
            block10: {
                block9: {
                    ProtectionDomain protectionDomain;
                    block8: {
                        ProtectionDomain protectionDomain2;
                        if (this == object) {
                            return true;
                        }
                        if (object == null) {
                            return false;
                        }
                        if (this.getClass() != object.getClass()) {
                            return false;
                        }
                        ProtectionDomain protectionDomain3 = ((ForUnsafeInjection)object).protectionDomain;
                        protectionDomain = protectionDomain2 = this.protectionDomain;
                        if (protectionDomain3 == null) break block8;
                        if (protectionDomain == null) break block9;
                        if (!protectionDomain2.equals(protectionDomain3)) {
                            return false;
                        }
                        break block10;
                    }
                    if (protectionDomain == null) break block10;
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            int n = this.getClass().hashCode() * 31;
            ProtectionDomain protectionDomain = this.protectionDomain;
            if (protectionDomain != null) {
                n = n + protectionDomain.hashCode();
            }
            return n;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForBootstrapInjection
    implements ClassLoadingStrategy<ClassLoader> {
        private final Instrumentation instrumentation;
        private final File folder;

        public ForBootstrapInjection(Instrumentation instrumentation, File folder) {
            this.instrumentation = instrumentation;
            this.folder = folder;
        }

        @Override
        public Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
            ClassInjector.UsingReflection classInjector = classLoader == null ? ClassInjector.UsingInstrumentation.of(this.folder, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, this.instrumentation) : new ClassInjector.UsingReflection(classLoader);
            return classInjector.inject(types);
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
            if (!this.instrumentation.equals(((ForBootstrapInjection)object).instrumentation)) {
                return false;
            }
            return this.folder.equals(((ForBootstrapInjection)object).folder);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.instrumentation.hashCode()) * 31 + this.folder.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class UsingLookup
    implements ClassLoadingStrategy<ClassLoader> {
        private final ClassInjector classInjector;

        protected UsingLookup(ClassInjector classInjector) {
            this.classInjector = classInjector;
        }

        public static ClassLoadingStrategy<ClassLoader> of(Object lookup) {
            return new UsingLookup(ClassInjector.UsingLookup.of(lookup));
        }

        public static ClassLoadingStrategy<ClassLoader> withFallback(Callable<?> lookup) {
            return UsingLookup.withFallback(lookup, false);
        }

        public static ClassLoadingStrategy<ClassLoader> withFallback(Callable<?> lookup, boolean wrapper) {
            if (ClassInjector.UsingLookup.isAvailable()) {
                try {
                    return UsingLookup.of(lookup.call());
                }
                catch (Exception exception) {
                    throw new IllegalStateException(exception);
                }
            }
            if (ClassInjector.UsingUnsafe.isAvailable()) {
                return new ForUnsafeInjection();
            }
            if (wrapper) {
                return Default.WRAPPER;
            }
            throw new IllegalStateException("Neither lookup or unsafe class injection is available");
        }

        @Override
        public Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
            return this.classInjector.inject(types);
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
            return this.classInjector.equals(((UsingLookup)object).classInjector);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.classInjector.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Configurable<S extends ClassLoader>
    extends ClassLoadingStrategy<S> {
        public Configurable<S> with(ProtectionDomain var1);

        public Configurable<S> with(PackageDefinitionStrategy var1);

        public Configurable<S> allowExistingTypes();

        public Configurable<S> opened();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Default implements Configurable<ClassLoader>
    {
        WRAPPER(new WrappingDispatcher(ByteArrayClassLoader.PersistenceHandler.LATENT, false)),
        WRAPPER_PERSISTENT(new WrappingDispatcher(ByteArrayClassLoader.PersistenceHandler.MANIFEST, false)),
        CHILD_FIRST(new WrappingDispatcher(ByteArrayClassLoader.PersistenceHandler.LATENT, true)),
        CHILD_FIRST_PERSISTENT(new WrappingDispatcher(ByteArrayClassLoader.PersistenceHandler.MANIFEST, true)),
        INJECTION(new InjectionDispatcher());

        private static final boolean DEFAULT_FORBID_EXISTING = true;
        private final Configurable<ClassLoader> dispatcher;

        private Default(Configurable<ClassLoader> dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
            return this.dispatcher.load(classLoader, types);
        }

        @Override
        public Configurable<ClassLoader> with(ProtectionDomain protectionDomain) {
            return this.dispatcher.with(protectionDomain);
        }

        @Override
        public Configurable<ClassLoader> with(PackageDefinitionStrategy packageDefinitionStrategy) {
            return this.dispatcher.with(packageDefinitionStrategy);
        }

        @Override
        public Configurable<ClassLoader> allowExistingTypes() {
            return this.dispatcher.allowExistingTypes();
        }

        @Override
        public Configurable<ClassLoader> opened() {
            return this.dispatcher.opened();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class WrappingDispatcher
        implements Configurable<ClassLoader> {
            private static final boolean CHILD_FIRST = true;
            private static final boolean PARENT_FIRST = false;
            @MaybeNull
            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
            private final ProtectionDomain protectionDomain;
            private final ByteArrayClassLoader.PersistenceHandler persistenceHandler;
            private final PackageDefinitionStrategy packageDefinitionStrategy;
            private final boolean childFirst;
            private final boolean forbidExisting;
            private final boolean sealed;

            protected WrappingDispatcher(ByteArrayClassLoader.PersistenceHandler persistenceHandler, boolean childFirst) {
                this(NO_PROTECTION_DOMAIN, PackageDefinitionStrategy.Trivial.INSTANCE, persistenceHandler, childFirst, true, true);
            }

            private WrappingDispatcher(@MaybeNull ProtectionDomain protectionDomain, PackageDefinitionStrategy packageDefinitionStrategy, ByteArrayClassLoader.PersistenceHandler persistenceHandler, boolean childFirst, boolean forbidExisting, boolean sealed) {
                this.protectionDomain = protectionDomain;
                this.packageDefinitionStrategy = packageDefinitionStrategy;
                this.persistenceHandler = persistenceHandler;
                this.childFirst = childFirst;
                this.forbidExisting = forbidExisting;
                this.sealed = sealed;
            }

            @Override
            public Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
                return this.childFirst ? ByteArrayClassLoader.ChildFirst.load(classLoader, types, this.protectionDomain, this.persistenceHandler, this.packageDefinitionStrategy, this.forbidExisting, this.sealed) : ByteArrayClassLoader.load(classLoader, types, this.protectionDomain, this.persistenceHandler, this.packageDefinitionStrategy, this.forbidExisting, this.sealed);
            }

            @Override
            public Configurable<ClassLoader> with(ProtectionDomain protectionDomain) {
                return new WrappingDispatcher(protectionDomain, this.packageDefinitionStrategy, this.persistenceHandler, this.childFirst, this.forbidExisting, this.sealed);
            }

            @Override
            public Configurable<ClassLoader> with(PackageDefinitionStrategy packageDefinitionStrategy) {
                return new WrappingDispatcher(this.protectionDomain, packageDefinitionStrategy, this.persistenceHandler, this.childFirst, this.forbidExisting, this.sealed);
            }

            @Override
            public Configurable<ClassLoader> allowExistingTypes() {
                return new WrappingDispatcher(this.protectionDomain, this.packageDefinitionStrategy, this.persistenceHandler, this.childFirst, false, this.sealed);
            }

            @Override
            public Configurable<ClassLoader> opened() {
                return new WrappingDispatcher(this.protectionDomain, this.packageDefinitionStrategy, this.persistenceHandler, this.childFirst, this.forbidExisting, false);
            }

            public boolean equals(@MaybeNull Object object) {
                block14: {
                    block13: {
                        ProtectionDomain protectionDomain;
                        block12: {
                            ProtectionDomain protectionDomain2;
                            if (this == object) {
                                return true;
                            }
                            if (object == null) {
                                return false;
                            }
                            if (this.getClass() != object.getClass()) {
                                return false;
                            }
                            if (this.childFirst != ((WrappingDispatcher)object).childFirst) {
                                return false;
                            }
                            if (this.forbidExisting != ((WrappingDispatcher)object).forbidExisting) {
                                return false;
                            }
                            if (this.sealed != ((WrappingDispatcher)object).sealed) {
                                return false;
                            }
                            if (!this.persistenceHandler.equals((Object)((WrappingDispatcher)object).persistenceHandler)) {
                                return false;
                            }
                            ProtectionDomain protectionDomain3 = ((WrappingDispatcher)object).protectionDomain;
                            protectionDomain = protectionDomain2 = this.protectionDomain;
                            if (protectionDomain3 == null) break block12;
                            if (protectionDomain == null) break block13;
                            if (!protectionDomain2.equals(protectionDomain3)) {
                                return false;
                            }
                            break block14;
                        }
                        if (protectionDomain == null) break block14;
                    }
                    return false;
                }
                return this.packageDefinitionStrategy.equals(((WrappingDispatcher)object).packageDefinitionStrategy);
            }

            public int hashCode() {
                int n = this.getClass().hashCode() * 31;
                ProtectionDomain protectionDomain = this.protectionDomain;
                if (protectionDomain != null) {
                    n = n + protectionDomain.hashCode();
                }
                return ((((n * 31 + this.persistenceHandler.hashCode()) * 31 + this.packageDefinitionStrategy.hashCode()) * 31 + this.childFirst) * 31 + this.forbidExisting) * 31 + this.sealed;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class InjectionDispatcher
        implements Configurable<ClassLoader> {
            @MaybeNull
            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
            private final ProtectionDomain protectionDomain;
            private final PackageDefinitionStrategy packageDefinitionStrategy;
            private final boolean forbidExisting;

            protected InjectionDispatcher() {
                this(NO_PROTECTION_DOMAIN, PackageDefinitionStrategy.NoOp.INSTANCE, true);
            }

            private InjectionDispatcher(@MaybeNull ProtectionDomain protectionDomain, PackageDefinitionStrategy packageDefinitionStrategy, boolean forbidExisting) {
                this.protectionDomain = protectionDomain;
                this.packageDefinitionStrategy = packageDefinitionStrategy;
                this.forbidExisting = forbidExisting;
            }

            @Override
            public Map<TypeDescription, Class<?>> load(@MaybeNull ClassLoader classLoader, Map<TypeDescription, byte[]> types) {
                if (classLoader == null) {
                    throw new IllegalArgumentException("Cannot inject classes into the bootstrap class loader");
                }
                return new ClassInjector.UsingReflection(classLoader, this.protectionDomain, this.packageDefinitionStrategy, this.forbidExisting).inject(types);
            }

            @Override
            public Configurable<ClassLoader> with(ProtectionDomain protectionDomain) {
                return new InjectionDispatcher(protectionDomain, this.packageDefinitionStrategy, this.forbidExisting);
            }

            @Override
            public Configurable<ClassLoader> with(PackageDefinitionStrategy packageDefinitionStrategy) {
                return new InjectionDispatcher(this.protectionDomain, packageDefinitionStrategy, this.forbidExisting);
            }

            @Override
            public Configurable<ClassLoader> allowExistingTypes() {
                return new InjectionDispatcher(this.protectionDomain, this.packageDefinitionStrategy, false);
            }

            @Override
            public Configurable<ClassLoader> opened() {
                return this;
            }

            public boolean equals(@MaybeNull Object object) {
                block11: {
                    block10: {
                        ProtectionDomain protectionDomain;
                        block9: {
                            ProtectionDomain protectionDomain2;
                            if (this == object) {
                                return true;
                            }
                            if (object == null) {
                                return false;
                            }
                            if (this.getClass() != object.getClass()) {
                                return false;
                            }
                            if (this.forbidExisting != ((InjectionDispatcher)object).forbidExisting) {
                                return false;
                            }
                            ProtectionDomain protectionDomain3 = ((InjectionDispatcher)object).protectionDomain;
                            protectionDomain = protectionDomain2 = this.protectionDomain;
                            if (protectionDomain3 == null) break block9;
                            if (protectionDomain == null) break block10;
                            if (!protectionDomain2.equals(protectionDomain3)) {
                                return false;
                            }
                            break block11;
                        }
                        if (protectionDomain == null) break block11;
                    }
                    return false;
                }
                return this.packageDefinitionStrategy.equals(((InjectionDispatcher)object).packageDefinitionStrategy);
            }

            public int hashCode() {
                int n = this.getClass().hashCode() * 31;
                ProtectionDomain protectionDomain = this.protectionDomain;
                if (protectionDomain != null) {
                    n = n + protectionDomain.hashCode();
                }
                return (n * 31 + this.packageDefinitionStrategy.hashCode()) * 31 + this.forbidExisting;
            }
        }
    }
}

