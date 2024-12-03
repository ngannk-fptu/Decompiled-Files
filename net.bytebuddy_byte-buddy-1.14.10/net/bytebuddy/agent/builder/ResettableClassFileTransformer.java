/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.agent.builder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Iterator;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.JavaType;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ResettableClassFileTransformer
extends ClassFileTransformer {
    public Iterator<AgentBuilder.Transformer> iterator(TypeDescription var1, @MaybeNull ClassLoader var2, @MaybeNull JavaModule var3, @MaybeNull Class<?> var4, ProtectionDomain var5);

    public boolean reset(Instrumentation var1, AgentBuilder.RedefinitionStrategy var2);

    public boolean reset(Instrumentation var1, AgentBuilder.RedefinitionStrategy var2, AgentBuilder.RedefinitionStrategy.BatchAllocator var3);

    public boolean reset(Instrumentation var1, AgentBuilder.RedefinitionStrategy var2, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy var3);

    public boolean reset(Instrumentation var1, AgentBuilder.RedefinitionStrategy var2, AgentBuilder.RedefinitionStrategy.BatchAllocator var3, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy var4);

    public boolean reset(Instrumentation var1, AgentBuilder.RedefinitionStrategy var2, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy var3, AgentBuilder.RedefinitionStrategy.Listener var4);

    public boolean reset(Instrumentation var1, AgentBuilder.RedefinitionStrategy var2, AgentBuilder.RedefinitionStrategy.BatchAllocator var3, AgentBuilder.RedefinitionStrategy.Listener var4);

    public boolean reset(Instrumentation var1, AgentBuilder.RedefinitionStrategy var2, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy var3, AgentBuilder.RedefinitionStrategy.BatchAllocator var4, AgentBuilder.RedefinitionStrategy.Listener var5);

    public boolean reset(Instrumentation var1, ResettableClassFileTransformer var2, AgentBuilder.RedefinitionStrategy var3, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy var4, AgentBuilder.RedefinitionStrategy.BatchAllocator var5, AgentBuilder.RedefinitionStrategy.Listener var6);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static abstract class WithDelegation
    extends AbstractBase {
        protected final ResettableClassFileTransformer classFileTransformer;

        protected WithDelegation(ResettableClassFileTransformer classFileTransformer) {
            this.classFileTransformer = classFileTransformer;
        }

        @Override
        public Iterator<AgentBuilder.Transformer> iterator(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
            return this.classFileTransformer.iterator(typeDescription, classLoader, module, classBeingRedefined, protectionDomain);
        }

        @Override
        public boolean reset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, AgentBuilder.RedefinitionStrategy redefinitionStrategy, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy, AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator, AgentBuilder.RedefinitionStrategy.Listener redefinitionListener) {
            return this.classFileTransformer.reset(instrumentation, classFileTransformer, redefinitionStrategy, redefinitionDiscoveryStrategy, redefinitionBatchAllocator, redefinitionListener);
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
            return this.classFileTransformer.equals(((WithDelegation)object).classFileTransformer);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.classFileTransformer.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Substitutable
        extends AbstractBase
        implements net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable {
            private static final Factory DISPATCHER;
            protected volatile ResettableClassFileTransformer classFileTransformer;
            private static final boolean ACCESS_CONTROLLER;

            protected Substitutable(ResettableClassFileTransformer classFileTransformer) {
                this.classFileTransformer = classFileTransformer;
            }

            @AccessControllerPlugin.Enhance
            private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
                PrivilegedAction<T> action;
                if (ACCESS_CONTROLLER) {
                    return AccessController.doPrivileged(privilegedAction);
                }
                return action.run();
            }

            public static net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable of(ResettableClassFileTransformer classFileTransformer) {
                return DISPATCHER.make(classFileTransformer);
            }

            @Override
            public void substitute(ResettableClassFileTransformer classFileTransformer) {
                while (classFileTransformer instanceof net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable) {
                    classFileTransformer = ((net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable)classFileTransformer).unwrap();
                }
                this.classFileTransformer = classFileTransformer;
            }

            @Override
            public ResettableClassFileTransformer unwrap() {
                return this.classFileTransformer;
            }

            @Override
            public Iterator<AgentBuilder.Transformer> iterator(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, @MaybeNull Class<?> classBeingRedefined, ProtectionDomain protectionDomain) {
                return this.classFileTransformer.iterator(typeDescription, classLoader, module, classBeingRedefined, protectionDomain);
            }

            @Override
            public boolean reset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, AgentBuilder.RedefinitionStrategy redefinitionStrategy, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy, AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator, AgentBuilder.RedefinitionStrategy.Listener redefinitionListener) {
                return this.classFileTransformer.reset(instrumentation, classFileTransformer, redefinitionStrategy, redefinitionDiscoveryStrategy, redefinitionBatchAllocator, redefinitionListener);
            }

            @Override
            public byte[] transform(ClassLoader classLoader, String internalName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] binaryRepresentation) throws IllegalClassFormatException {
                return this.classFileTransformer.transform(classLoader, internalName, classBeingRedefined, protectionDomain, binaryRepresentation);
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
                DISPATCHER = Substitutable.doPrivileged(Factory.CreationAction.INSTANCE);
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
                return this.classFileTransformer.equals(((Substitutable)object).classFileTransformer);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.classFileTransformer.hashCode();
            }

            static interface Factory {
                public net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable make(ResettableClassFileTransformer var1);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForLegacyVm implements Factory
                {
                    INSTANCE;


                    @Override
                    public net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable make(ResettableClassFileTransformer classFileTransformer) {
                        return new Substitutable(classFileTransformer);
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                public static class ForJava9CapableVm
                implements Factory {
                    private final Constructor<? extends net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable> substitutable;

                    protected ForJava9CapableVm(Constructor<? extends net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable> substitutable) {
                        this.substitutable = substitutable;
                    }

                    @Override
                    public net.bytebuddy.agent.builder.ResettableClassFileTransformer$Substitutable make(ResettableClassFileTransformer classFileTransformer) {
                        try {
                            return this.substitutable.newInstance(classFileTransformer);
                        }
                        catch (IllegalAccessException exception) {
                            throw new IllegalStateException("Cannot access " + this.substitutable, exception);
                        }
                        catch (InstantiationException exception) {
                            throw new IllegalStateException("Cannot instantiate " + this.substitutable.getDeclaringClass(), exception);
                        }
                        catch (InvocationTargetException exception) {
                            throw new IllegalStateException("Cannot invoke " + this.substitutable, exception.getTargetException());
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
                        return this.substitutable.equals(((ForJava9CapableVm)object).substitutable);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.substitutable.hashCode();
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum CreationAction implements PrivilegedAction<Factory>
                {
                    INSTANCE;


                    @Override
                    @SuppressFBWarnings(value={"REC_CATCH_EXCEPTION"}, justification="Exception should not be rethrown but trigger a fallback.")
                    public Factory run() {
                        try {
                            return new ForJava9CapableVm(new ByteBuddy().with(TypeValidation.DISABLED).subclass(Substitutable.class).name(Substitutable.class.getName() + "$ByteBuddy$ModuleSupport").method(ElementMatchers.named("transform").and(ElementMatchers.takesArgument(0, JavaType.MODULE.load()))).intercept(MethodCall.invoke(ClassFileTransformer.class.getDeclaredMethod("transform", JavaType.MODULE.load(), ClassLoader.class, String.class, Class.class, ProtectionDomain.class, byte[].class)).onField("classFileTransformer").withAllArguments()).make().load(Substitutable.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER_PERSISTENT.with(Substitutable.class.getProtectionDomain())).getLoaded().getDeclaredConstructor(ResettableClassFileTransformer.class));
                        }
                        catch (Exception ignored) {
                            return ForLegacyVm.INSTANCE;
                        }
                    }
                }
            }
        }
    }

    public static abstract class AbstractBase
    implements ResettableClassFileTransformer {
        public boolean reset(Instrumentation instrumentation, AgentBuilder.RedefinitionStrategy redefinitionStrategy) {
            return this.reset(instrumentation, redefinitionStrategy, AgentBuilder.RedefinitionStrategy.BatchAllocator.ForTotal.INSTANCE);
        }

        public boolean reset(Instrumentation instrumentation, AgentBuilder.RedefinitionStrategy redefinitionStrategy, AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator) {
            return this.reset(instrumentation, redefinitionStrategy, redefinitionBatchAllocator, (AgentBuilder.RedefinitionStrategy.Listener)AgentBuilder.RedefinitionStrategy.Listener.NoOp.INSTANCE);
        }

        public boolean reset(Instrumentation instrumentation, AgentBuilder.RedefinitionStrategy redefinitionStrategy, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy) {
            return this.reset(instrumentation, redefinitionStrategy, redefinitionDiscoveryStrategy, (AgentBuilder.RedefinitionStrategy.Listener)AgentBuilder.RedefinitionStrategy.Listener.NoOp.INSTANCE);
        }

        public boolean reset(Instrumentation instrumentation, AgentBuilder.RedefinitionStrategy redefinitionStrategy, AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy) {
            return this.reset(instrumentation, redefinitionStrategy, redefinitionDiscoveryStrategy, redefinitionBatchAllocator, AgentBuilder.RedefinitionStrategy.Listener.NoOp.INSTANCE);
        }

        public boolean reset(Instrumentation instrumentation, AgentBuilder.RedefinitionStrategy redefinitionStrategy, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy, AgentBuilder.RedefinitionStrategy.Listener redefinitionListener) {
            return this.reset(instrumentation, redefinitionStrategy, redefinitionDiscoveryStrategy, AgentBuilder.RedefinitionStrategy.BatchAllocator.ForTotal.INSTANCE, redefinitionListener);
        }

        public boolean reset(Instrumentation instrumentation, AgentBuilder.RedefinitionStrategy redefinitionStrategy, AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator, AgentBuilder.RedefinitionStrategy.Listener redefinitionListener) {
            return this.reset(instrumentation, redefinitionStrategy, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.SinglePass.INSTANCE, redefinitionBatchAllocator, redefinitionListener);
        }

        public boolean reset(Instrumentation instrumentation, AgentBuilder.RedefinitionStrategy redefinitionStrategy, AgentBuilder.RedefinitionStrategy.DiscoveryStrategy redefinitionDiscoveryStrategy, AgentBuilder.RedefinitionStrategy.BatchAllocator redefinitionBatchAllocator, AgentBuilder.RedefinitionStrategy.Listener redefinitionListener) {
            return this.reset(instrumentation, this, redefinitionStrategy, redefinitionDiscoveryStrategy, redefinitionBatchAllocator, redefinitionListener);
        }
    }

    public static interface Substitutable
    extends ResettableClassFileTransformer {
        public void substitute(ResettableClassFileTransformer var1);

        public ResettableClassFileTransformer unwrap();
    }
}

