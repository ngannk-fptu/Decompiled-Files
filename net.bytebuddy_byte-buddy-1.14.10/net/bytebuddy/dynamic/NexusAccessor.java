/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.Nexus;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class NexusAccessor {
    private static final Dispatcher DISPATCHER;
    @MaybeNull
    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.REVERSE_NULLABILITY)
    private final ReferenceQueue<? super ClassLoader> referenceQueue;
    private static final boolean ACCESS_CONTROLLER;

    public NexusAccessor() {
        this(null);
    }

    public NexusAccessor(@MaybeNull ReferenceQueue<? super ClassLoader> referenceQueue) {
        this.referenceQueue = referenceQueue;
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    public static boolean isAlive() {
        return DISPATCHER.isAlive();
    }

    public static void clean(Reference<? extends ClassLoader> reference) {
        DISPATCHER.clean(reference);
    }

    public void register(String name, @MaybeNull ClassLoader classLoader, int identification, LoadedTypeInitializer loadedTypeInitializer) {
        if (loadedTypeInitializer.isAlive()) {
            DISPATCHER.register(name, classLoader, this.referenceQueue, identification, loadedTypeInitializer);
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
        DISPATCHER = NexusAccessor.doPrivileged(Dispatcher.CreationAction.INSTANCE);
    }

    public boolean equals(@MaybeNull Object object) {
        block10: {
            block9: {
                ReferenceQueue<? super ClassLoader> referenceQueue;
                block8: {
                    ReferenceQueue<? super ClassLoader> referenceQueue2;
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    ReferenceQueue<? super ClassLoader> referenceQueue3 = ((NexusAccessor)object).referenceQueue;
                    referenceQueue = referenceQueue2 = this.referenceQueue;
                    if (referenceQueue3 == null) break block8;
                    if (referenceQueue == null) break block9;
                    if (!referenceQueue2.equals(referenceQueue3)) {
                        return false;
                    }
                    break block10;
                }
                if (referenceQueue == null) break block10;
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int n = this.getClass().hashCode() * 31;
        ReferenceQueue<? super ClassLoader> referenceQueue = this.referenceQueue;
        if (referenceQueue != null) {
            n = n + referenceQueue.hashCode();
        }
        return n;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static interface Dispatcher {
        public boolean isAlive();

        public void clean(Reference<? extends ClassLoader> var1);

        public void register(String var1, @MaybeNull ClassLoader var2, @MaybeNull ReferenceQueue<? super ClassLoader> var3, int var4, LoadedTypeInitializer var5);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Unavailable
        implements Dispatcher {
            private final String message;

            protected Unavailable(String message) {
                this.message = message;
            }

            @Override
            public boolean isAlive() {
                return false;
            }

            @Override
            public void clean(Reference<? extends ClassLoader> reference) {
                throw new UnsupportedOperationException("Could not initialize Nexus accessor: " + this.message);
            }

            @Override
            public void register(String name, @MaybeNull ClassLoader classLoader, @MaybeNull ReferenceQueue<? super ClassLoader> referenceQueue, int identification, LoadedTypeInitializer loadedTypeInitializer) {
                throw new UnsupportedOperationException("Could not initialize Nexus accessor: " + this.message);
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
                return this.message.equals(((Unavailable)object).message);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.message.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Available
        implements Dispatcher {
            private final Method register;
            private final Method clean;

            protected Available(Method register, Method clean) {
                this.register = register;
                this.clean = clean;
            }

            @Override
            public boolean isAlive() {
                return true;
            }

            @Override
            public void clean(Reference<? extends ClassLoader> reference) {
                try {
                    this.clean.invoke(null, reference);
                }
                catch (IllegalAccessException exception) {
                    throw new IllegalStateException(exception);
                }
                catch (InvocationTargetException exception) {
                    throw new IllegalStateException(exception.getTargetException());
                }
            }

            @Override
            public void register(String name, @MaybeNull ClassLoader classLoader, @MaybeNull ReferenceQueue<? super ClassLoader> referenceQueue, int identification, LoadedTypeInitializer loadedTypeInitializer) {
                try {
                    this.register.invoke(null, name, classLoader, referenceQueue, identification, loadedTypeInitializer);
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
                if (!this.register.equals(((Available)object).register)) {
                    return false;
                }
                return this.clean.equals(((Available)object).clean);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.register.hashCode()) * 31 + this.clean.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum CreationAction implements PrivilegedAction<Dispatcher>
        {
            INSTANCE;


            @Override
            @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
            public Dispatcher run() {
                if (Boolean.getBoolean("net.bytebuddy.nexus.disabled")) {
                    return new Unavailable("Nexus injection was explicitly disabled");
                }
                try {
                    Class<?> nexusType = new ClassInjector.UsingReflection(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.NO_PROTECTION_DOMAIN).inject(Collections.singletonMap(TypeDescription.ForLoadedType.of(Nexus.class), ClassFileLocator.ForClassLoader.read(Nexus.class))).get(TypeDescription.ForLoadedType.of(Nexus.class));
                    return new Available(nexusType.getMethod("register", String.class, ClassLoader.class, ReferenceQueue.class, Integer.TYPE, Object.class), nexusType.getMethod("clean", Reference.class));
                }
                catch (Exception exception) {
                    Class<?> nexusType;
                    try {
                        nexusType = ClassLoader.getSystemClassLoader().loadClass(Nexus.class.getName());
                    }
                    catch (Exception ignored) {
                        return new Unavailable(exception.toString());
                    }
                    try {
                        JavaModule source = JavaModule.ofType(NexusAccessor.class);
                        JavaModule target = JavaModule.ofType(nexusType);
                        if (source != null && !source.canRead(target)) {
                            Class<?> module = Class.forName("java.lang.Module");
                            module.getMethod("addReads", module).invoke(source.unwrap(), target.unwrap());
                        }
                        return new Available(nexusType.getMethod("register", String.class, ClassLoader.class, ReferenceQueue.class, Integer.TYPE, Object.class), nexusType.getMethod("clean", Reference.class));
                    }
                    catch (Exception exception2) {
                        return new Unavailable(exception2.toString());
                    }
                }
            }
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class InitializationAppender
    implements ByteCodeAppender {
        private final int identification;

        public InitializationAppender(int identification) {
            this.identification = identification;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            try {
                return new ByteCodeAppender.Simple(new StackManipulation.Compound(MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(ClassLoader.class.getMethod("getSystemClassLoader", new Class[0]))), new TextConstant(Nexus.class.getName()), MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(ClassLoader.class.getMethod("loadClass", String.class))), new TextConstant("initialize"), ArrayFactory.forType(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Class.class)).withValues(Arrays.asList(ClassConstant.of(TypeDescription.ForLoadedType.of(Class.class)), ClassConstant.of(TypeDescription.ForLoadedType.of(Integer.TYPE)))), MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(Class.class.getMethod("getMethod", String.class, Class[].class))), NullConstant.INSTANCE, ArrayFactory.forType(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class)).withValues(Arrays.asList(ClassConstant.of(instrumentedMethod.getDeclaringType().asErasure()), new StackManipulation.Compound(IntegerConstant.forValue(this.identification), MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(Integer.class.getMethod("valueOf", Integer.TYPE)))))), MethodInvocation.invoke(new MethodDescription.ForLoadedMethod(Method.class.getMethod("invoke", Object.class, Object[].class))), Removal.SINGLE)).apply(methodVisitor, implementationContext, instrumentedMethod);
            }
            catch (NoSuchMethodException exception) {
                throw new IllegalStateException("Cannot locate method", exception);
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
            return this.identification == ((InitializationAppender)object).identification;
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.identification;
        }
    }
}

