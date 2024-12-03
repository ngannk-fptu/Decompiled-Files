/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.multibindings;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Lists;
import com.google.inject.multibindings.Element;
import com.google.inject.multibindings.MultibinderBinding;
import com.google.inject.multibindings.MultibindingsTargetVisitor;
import com.google.inject.multibindings.RealElement;
import com.google.inject.name.Names;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.Message;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.spi.Toolable;
import com.google.inject.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Multibinder<T> {
    private Multibinder() {
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, TypeLiteral<T> type) {
        binder = binder.skipSources(RealMultibinder.class, Multibinder.class);
        RealMultibinder result = new RealMultibinder(binder, type, Key.get(Multibinder.setOf(type)));
        binder.install(result);
        return result;
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, Class<T> type) {
        return Multibinder.newSetBinder(binder, TypeLiteral.get(type));
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, TypeLiteral<T> type, Annotation annotation) {
        binder = binder.skipSources(RealMultibinder.class, Multibinder.class);
        RealMultibinder result = new RealMultibinder(binder, type, Key.get(Multibinder.setOf(type), annotation));
        binder.install(result);
        return result;
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, Class<T> type, Annotation annotation) {
        return Multibinder.newSetBinder(binder, TypeLiteral.get(type), annotation);
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, TypeLiteral<T> type, Class<? extends Annotation> annotationType) {
        binder = binder.skipSources(RealMultibinder.class, Multibinder.class);
        RealMultibinder result = new RealMultibinder(binder, type, Key.get(Multibinder.setOf(type), annotationType));
        binder.install(result);
        return result;
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, Class<T> type, Class<? extends Annotation> annotationType) {
        return Multibinder.newSetBinder(binder, TypeLiteral.get(type), annotationType);
    }

    static <T> TypeLiteral<Set<T>> setOf(TypeLiteral<T> elementType) {
        ParameterizedType type = Types.setOf(elementType.getType());
        return TypeLiteral.get(type);
    }

    public abstract Multibinder<T> permitDuplicates();

    public abstract LinkedBindingBuilder<T> addBinding();

    static void checkConfiguration(boolean condition, String format, Object ... args) {
        if (condition) {
            return;
        }
        throw new ConfigurationException($ImmutableSet.of(new Message(Errors.format(format, args))));
    }

    static <T> T checkNotNull(T reference, String name) {
        if (reference != null) {
            return reference;
        }
        NullPointerException npe = new NullPointerException(name);
        throw new ConfigurationException($ImmutableSet.of(new Message($ImmutableList.<Object>of(), npe.toString(), npe)));
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class PermitDuplicatesModule
    extends AbstractModule {
        private final Key<Boolean> key;

        PermitDuplicatesModule(Key<Boolean> key) {
            this.key = key;
        }

        @Override
        protected void configure() {
            this.bind(this.key).toInstance(true);
        }

        public boolean equals(Object o) {
            return o instanceof PermitDuplicatesModule && ((PermitDuplicatesModule)o).key.equals(this.key);
        }

        public int hashCode() {
            return this.getClass().hashCode() ^ this.key.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class RealMultibinder<T>
    extends Multibinder<T>
    implements Module,
    ProviderWithExtensionVisitor<Set<T>>,
    HasDependencies,
    MultibinderBinding<Set<T>> {
        private final TypeLiteral<T> elementType;
        private final String setName;
        private final Key<Set<T>> setKey;
        private final Key<Boolean> permitDuplicatesKey;
        private Binder binder;
        private $ImmutableList<Binding<T>> bindings;
        private Set<Dependency<?>> dependencies;
        private boolean permitDuplicates;

        private RealMultibinder(Binder binder, TypeLiteral<T> elementType, Key<Set<T>> setKey) {
            this.binder = RealMultibinder.checkNotNull(binder, "binder");
            this.elementType = RealMultibinder.checkNotNull(elementType, "elementType");
            this.setKey = RealMultibinder.checkNotNull(setKey, "setKey");
            this.setName = this.nameOf(setKey);
            this.permitDuplicatesKey = Key.get(Boolean.class, (Annotation)Names.named(this.toString() + " permits duplicates"));
        }

        private String nameOf(Key<?> key) {
            Annotation annotation = this.setKey.getAnnotation();
            Class<Annotation> annotationType = this.setKey.getAnnotationType();
            if (annotation != null && !Annotations.isMarker(annotationType)) {
                return ((Object)this.setKey.getAnnotation()).toString();
            }
            if (this.setKey.getAnnotationType() != null) {
                return "@" + this.setKey.getAnnotationType().getName();
            }
            return "";
        }

        @Override
        public void configure(Binder binder) {
            RealMultibinder.checkConfiguration(!this.isInitialized(), "Multibinder was already initialized", new Object[0]);
            binder.bind(this.setKey).toProvider(this);
        }

        @Override
        public Multibinder<T> permitDuplicates() {
            this.binder.install(new PermitDuplicatesModule(this.permitDuplicatesKey));
            return this;
        }

        @Override
        public LinkedBindingBuilder<T> addBinding() {
            RealMultibinder.checkConfiguration(!this.isInitialized(), "Multibinder was already initialized", new Object[0]);
            return this.binder.bind(Key.get(this.elementType, (Annotation)new RealElement(this.setName)));
        }

        @Toolable
        @Inject
        void initialize(Injector injector) {
            ArrayList<Binding<T>> bindings = $Lists.newArrayList();
            ArrayList<Dependency<T>> dependencies = $Lists.newArrayList();
            for (Binding<T> entry : injector.findBindingsByType(this.elementType)) {
                if (!this.keyMatches(entry.getKey())) continue;
                Binding<T> binding = entry;
                bindings.add(binding);
                dependencies.add(Dependency.get(binding.getKey()));
            }
            this.bindings = $ImmutableList.copyOf(bindings);
            this.dependencies = $ImmutableSet.copyOf(dependencies);
            this.permitDuplicates = this.permitsDuplicates(injector);
            this.binder = null;
        }

        boolean permitsDuplicates(Injector injector) {
            return injector.getBindings().containsKey(this.permitDuplicatesKey);
        }

        private boolean keyMatches(Key<?> key) {
            return key.getTypeLiteral().equals(this.elementType) && key.getAnnotation() instanceof Element && ((Element)key.getAnnotation()).setName().equals(this.setName);
        }

        private boolean isInitialized() {
            return this.binder == null;
        }

        @Override
        public Set<T> get() {
            RealMultibinder.checkConfiguration(this.isInitialized(), "Multibinder is not initialized", new Object[0]);
            LinkedHashSet result = new LinkedHashSet();
            for (Binding binding : this.bindings) {
                Object newValue = binding.getProvider().get();
                RealMultibinder.checkConfiguration(newValue != null, "Set injection failed due to null element", new Object[0]);
                RealMultibinder.checkConfiguration(result.add(newValue) || this.permitDuplicates, "Set injection failed due to duplicated element \"%s\"", newValue);
            }
            return Collections.unmodifiableSet(result);
        }

        @Override
        public <B, V> V acceptExtensionVisitor(BindingTargetVisitor<B, V> visitor, ProviderInstanceBinding<? extends B> binding) {
            if (visitor instanceof MultibindingsTargetVisitor) {
                return ((MultibindingsTargetVisitor)visitor).visit(this);
            }
            return visitor.visit(binding);
        }

        String getSetName() {
            return this.setName;
        }

        @Override
        public TypeLiteral<?> getElementTypeLiteral() {
            return this.elementType;
        }

        @Override
        public Key<Set<T>> getSetKey() {
            return this.setKey;
        }

        @Override
        public List<Binding<?>> getElements() {
            if (this.isInitialized()) {
                return this.bindings;
            }
            throw new UnsupportedOperationException("getElements() not supported for module bindings");
        }

        @Override
        public boolean permitsDuplicates() {
            if (this.isInitialized()) {
                return this.permitDuplicates;
            }
            throw new UnsupportedOperationException("permitsDuplicates() not supported for module bindings");
        }

        @Override
        public boolean containsElement(com.google.inject.spi.Element element) {
            if (element instanceof Binding) {
                Binding binding = (Binding)element;
                return this.keyMatches(binding.getKey()) || binding.getKey().equals(this.permitDuplicatesKey) || binding.getKey().equals(this.setKey);
            }
            return false;
        }

        @Override
        public Set<Dependency<?>> getDependencies() {
            if (!this.isInitialized()) {
                return $ImmutableSet.of(Dependency.get(Key.get(Injector.class)));
            }
            return this.dependencies;
        }

        public boolean equals(Object o) {
            return o instanceof RealMultibinder && ((RealMultibinder)o).setKey.equals(this.setKey);
        }

        public int hashCode() {
            return this.setKey.hashCode();
        }

        public String toString() {
            return this.setName + (this.setName.length() > 0 ? " " : "") + "Multibinder<" + this.elementType + ">";
        }
    }
}

