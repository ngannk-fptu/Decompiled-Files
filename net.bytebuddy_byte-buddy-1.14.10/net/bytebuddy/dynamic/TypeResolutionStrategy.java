/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.NexusAccessor;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface TypeResolutionStrategy {
    public Resolved resolve();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Disabled implements TypeResolutionStrategy,
    Resolved
    {
        INSTANCE;


        @Override
        public Resolved resolve() {
            return this;
        }

        @Override
        public TypeInitializer injectedInto(TypeInitializer typeInitializer) {
            return typeInitializer;
        }

        @Override
        public <S extends ClassLoader> Map<TypeDescription, Class<?>> initialize(DynamicType dynamicType, @MaybeNull S classLoader, ClassLoadingStrategy<? super S> classLoadingStrategy) {
            throw new IllegalStateException("Cannot initialize a dynamic type for a disabled type resolution strategy");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Lazy implements TypeResolutionStrategy,
    Resolved
    {
        INSTANCE;


        @Override
        public Resolved resolve() {
            return this;
        }

        @Override
        public TypeInitializer injectedInto(TypeInitializer typeInitializer) {
            return typeInitializer;
        }

        @Override
        public <S extends ClassLoader> Map<TypeDescription, Class<?>> initialize(DynamicType dynamicType, @MaybeNull S classLoader, ClassLoadingStrategy<? super S> classLoadingStrategy) {
            return classLoadingStrategy.load(classLoader, dynamicType.getAllTypes());
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class Active
    implements TypeResolutionStrategy {
        private final NexusAccessor nexusAccessor;

        public Active() {
            this(new NexusAccessor());
        }

        public Active(NexusAccessor nexusAccessor) {
            this.nexusAccessor = nexusAccessor;
        }

        @SuppressFBWarnings(value={"DMI_RANDOM_USED_ONLY_ONCE"}, justification="Avoids thread-contention.")
        public net.bytebuddy.dynamic.TypeResolutionStrategy$Resolved resolve() {
            return new Resolved(this.nexusAccessor, new Random().nextInt());
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
            return this.nexusAccessor.equals(((Active)object).nexusAccessor);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.nexusAccessor.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Resolved
        implements net.bytebuddy.dynamic.TypeResolutionStrategy$Resolved {
            private final NexusAccessor nexusAccessor;
            private final int identification;

            protected Resolved(NexusAccessor nexusAccessor, int identification) {
                this.nexusAccessor = nexusAccessor;
                this.identification = identification;
            }

            @Override
            public TypeInitializer injectedInto(TypeInitializer typeInitializer) {
                return typeInitializer.expandWith(new NexusAccessor.InitializationAppender(this.identification));
            }

            @Override
            public <S extends ClassLoader> Map<TypeDescription, Class<?>> initialize(DynamicType dynamicType, @MaybeNull S classLoader, ClassLoadingStrategy<? super S> classLoadingStrategy) {
                HashMap<TypeDescription, LoadedTypeInitializer> loadedTypeInitializers = new HashMap<TypeDescription, LoadedTypeInitializer>(dynamicType.getLoadedTypeInitializers());
                TypeDescription instrumentedType = dynamicType.getTypeDescription();
                Map<TypeDescription, Class<?>> types = classLoadingStrategy.load(classLoader, dynamicType.getAllTypes());
                this.nexusAccessor.register(instrumentedType.getName(), types.get(instrumentedType).getClassLoader(), this.identification, (LoadedTypeInitializer)loadedTypeInitializers.remove(instrumentedType));
                for (Map.Entry entry : loadedTypeInitializers.entrySet()) {
                    ((LoadedTypeInitializer)entry.getValue()).onLoad(types.get(entry.getKey()));
                }
                return types;
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
                if (this.identification != ((Resolved)object).identification) {
                    return false;
                }
                return this.nexusAccessor.equals(((Resolved)object).nexusAccessor);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.nexusAccessor.hashCode()) * 31 + this.identification;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Passive implements TypeResolutionStrategy,
    Resolved
    {
        INSTANCE;


        @Override
        public Resolved resolve() {
            return this;
        }

        @Override
        public TypeInitializer injectedInto(TypeInitializer typeInitializer) {
            return typeInitializer;
        }

        @Override
        public <S extends ClassLoader> Map<TypeDescription, Class<?>> initialize(DynamicType dynamicType, @MaybeNull S classLoader, ClassLoadingStrategy<? super S> classLoadingStrategy) {
            Map<TypeDescription, Class<?>> types = classLoadingStrategy.load(classLoader, dynamicType.getAllTypes());
            for (Map.Entry<TypeDescription, LoadedTypeInitializer> entry : dynamicType.getLoadedTypeInitializers().entrySet()) {
                entry.getValue().onLoad(types.get(entry.getKey()));
            }
            return new HashMap(types);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Resolved {
        public TypeInitializer injectedInto(TypeInitializer var1);

        public <S extends ClassLoader> Map<TypeDescription, Class<?>> initialize(DynamicType var1, @MaybeNull S var2, ClassLoadingStrategy<? super S> var3);
    }
}

