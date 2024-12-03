/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.Binding
 *  com.google.inject.ConfigurationException
 *  com.google.inject.Inject
 *  com.google.inject.Injector
 *  com.google.inject.Key
 *  com.google.inject.Module
 *  com.google.inject.Provider
 *  com.google.inject.TypeLiteral
 *  com.google.inject.binder.LinkedBindingBuilder
 *  com.google.inject.internal.Annotations
 *  com.google.inject.internal.Errors
 *  com.google.inject.name.Names
 *  com.google.inject.spi.BindingTargetVisitor
 *  com.google.inject.spi.Dependency
 *  com.google.inject.spi.Element
 *  com.google.inject.spi.HasDependencies
 *  com.google.inject.spi.Message
 *  com.google.inject.spi.ProviderInstanceBinding
 *  com.google.inject.spi.ProviderWithExtensionVisitor
 *  com.google.inject.spi.Toolable
 *  com.google.inject.util.Types
 */
package com.google.inject.multibindings;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.Annotations;
import com.google.inject.internal.Errors;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Multibinder<T> {
    private Multibinder() {
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, TypeLiteral<T> type) {
        binder = binder.skipSources(new Class[]{RealMultibinder.class, Multibinder.class});
        RealMultibinder result = new RealMultibinder(binder, type, Key.get(Multibinder.setOf(type)));
        binder.install(result);
        return result;
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, Class<T> type) {
        return Multibinder.newSetBinder(binder, TypeLiteral.get(type));
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, TypeLiteral<T> type, Annotation annotation) {
        binder = binder.skipSources(new Class[]{RealMultibinder.class, Multibinder.class});
        RealMultibinder result = new RealMultibinder(binder, type, Key.get(Multibinder.setOf(type), (Annotation)annotation));
        binder.install(result);
        return result;
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, Class<T> type, Annotation annotation) {
        return Multibinder.newSetBinder(binder, TypeLiteral.get(type), annotation);
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, TypeLiteral<T> type, Class<? extends Annotation> annotationType) {
        binder = binder.skipSources(new Class[]{RealMultibinder.class, Multibinder.class});
        RealMultibinder result = new RealMultibinder(binder, type, Key.get(Multibinder.setOf(type), annotationType));
        binder.install(result);
        return result;
    }

    public static <T> Multibinder<T> newSetBinder(Binder binder, Class<T> type, Class<? extends Annotation> annotationType) {
        return Multibinder.newSetBinder(binder, TypeLiteral.get(type), annotationType);
    }

    static <T> TypeLiteral<Set<T>> setOf(TypeLiteral<T> elementType) {
        ParameterizedType type = Types.setOf((Type)elementType.getType());
        return TypeLiteral.get((Type)type);
    }

    public abstract Multibinder<T> permitDuplicates();

    public abstract LinkedBindingBuilder<T> addBinding();

    static void checkConfiguration(boolean condition, String format, Object ... args) {
        if (condition) {
            return;
        }
        throw new ConfigurationException((Iterable)ImmutableSet.of((Object)new Message(Errors.format((String)format, (Object[])args))));
    }

    private static <T> ConfigurationException newDuplicateValuesException(Map<T, Binding<T>> existingBindings, Binding<T> binding, T newValue, Binding<T> duplicateBinding) {
        String newString;
        Object oldValue = Iterables.getOnlyElement((Iterable)Iterables.filter(existingBindings.keySet(), (Predicate)Predicates.equalTo(newValue)));
        String oldString = oldValue.toString();
        if (Objects.equal((Object)oldString, (Object)(newString = newValue.toString()))) {
            return new ConfigurationException((Iterable)ImmutableSet.of((Object)new Message(Errors.format((String)"Set injection failed due to duplicated element \"%s\"\n    Bound at %s\n    Bound at %s", (Object[])new Object[]{newValue, duplicateBinding.getSource(), binding.getSource()}))));
        }
        return new ConfigurationException((Iterable)ImmutableSet.of((Object)new Message(Errors.format((String)"Set injection failed due to multiple elements comparing equal:\n    \"%s\"\n        bound at %s\n    \"%s\"\n        bound at %s", (Object[])new Object[]{oldValue, duplicateBinding.getSource(), newValue, binding.getSource()}))));
    }

    static <T> T checkNotNull(T reference, String name) {
        if (reference != null) {
            return reference;
        }
        NullPointerException npe = new NullPointerException(name);
        throw new ConfigurationException((Iterable)ImmutableSet.of((Object)new Message(npe.toString(), (Throwable)npe)));
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

        protected void configure() {
            this.bind(this.key).toInstance((Object)true);
        }

        public boolean equals(Object o) {
            return o instanceof PermitDuplicatesModule && ((PermitDuplicatesModule)((Object)o)).key.equals(this.key);
        }

        public int hashCode() {
            return ((Object)((Object)this)).getClass().hashCode() ^ this.key.hashCode();
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
        private ImmutableList<Binding<T>> bindings;
        private Set<Dependency<?>> dependencies;
        private boolean permitDuplicates;

        private RealMultibinder(Binder binder, TypeLiteral<T> elementType, Key<Set<T>> setKey) {
            this.binder = RealMultibinder.checkNotNull(binder, "binder");
            this.elementType = RealMultibinder.checkNotNull(elementType, "elementType");
            this.setKey = RealMultibinder.checkNotNull(setKey, "setKey");
            this.setName = RealMultibinder.nameOf(setKey);
            this.permitDuplicatesKey = Key.get(Boolean.class, (Annotation)Names.named((String)(this.toString() + " permits duplicates")));
        }

        private static String nameOf(Key<?> setKey) {
            Annotation annotation = setKey.getAnnotation();
            Class annotationType = setKey.getAnnotationType();
            if (annotation != null && !Annotations.isMarker((Class)annotationType)) {
                return ((Object)setKey.getAnnotation()).toString();
            }
            if (setKey.getAnnotationType() != null) {
                return "@" + setKey.getAnnotationType().getName();
            }
            return "";
        }

        public void configure(Binder binder) {
            RealMultibinder.checkConfiguration(!this.isInitialized(), "Multibinder was already initialized", new Object[0]);
            binder.bind(this.setKey).toProvider((Provider)this);
        }

        @Override
        public Multibinder<T> permitDuplicates() {
            this.binder.install((Module)new PermitDuplicatesModule(this.permitDuplicatesKey));
            return this;
        }

        @Override
        public LinkedBindingBuilder<T> addBinding() {
            RealMultibinder.checkConfiguration(!this.isInitialized(), "Multibinder was already initialized", new Object[0]);
            return RealElement.addBinding(this.binder, Element.Type.MULTIBINDER, this.elementType, this.setName);
        }

        @Toolable
        @Inject
        void initialize(Injector injector) {
            ArrayList bindings = Lists.newArrayList();
            ArrayList dependencies = Lists.newArrayList();
            for (Binding entry : injector.findBindingsByType(this.elementType)) {
                if (!this.keyMatches(entry.getKey())) continue;
                Binding binding = entry;
                bindings.add(binding);
                dependencies.add(Dependency.get((Key)binding.getKey()));
            }
            this.bindings = ImmutableList.copyOf((Collection)bindings);
            this.dependencies = ImmutableSet.copyOf((Collection)dependencies);
            this.permitDuplicates = this.permitsDuplicates(injector);
            this.binder = null;
        }

        boolean permitsDuplicates(Injector injector) {
            return injector.getBindings().containsKey(this.permitDuplicatesKey);
        }

        private boolean keyMatches(Key<?> key) {
            return key.getTypeLiteral().equals(this.elementType) && key.getAnnotation() instanceof Element && ((Element)key.getAnnotation()).setName().equals(this.setName) && ((Element)key.getAnnotation()).type() == Element.Type.MULTIBINDER;
        }

        private boolean isInitialized() {
            return this.binder == null;
        }

        public Set<T> get() {
            RealMultibinder.checkConfiguration(this.isInitialized(), "Multibinder is not initialized", new Object[0]);
            LinkedHashMap<Object, Binding> result = new LinkedHashMap<Object, Binding>();
            for (Binding binding : this.bindings) {
                Object newValue = binding.getProvider().get();
                RealMultibinder.checkConfiguration(newValue != null, "Set injection failed due to null element", new Object[0]);
                Binding duplicateBinding = result.put(newValue, binding);
                if (this.permitDuplicates || duplicateBinding == null) continue;
                throw Multibinder.newDuplicateValuesException(result, binding, newValue, duplicateBinding);
            }
            return ImmutableSet.copyOf(result.keySet());
        }

        public <B, V> V acceptExtensionVisitor(BindingTargetVisitor<B, V> visitor, ProviderInstanceBinding<? extends B> binding) {
            if (visitor instanceof MultibindingsTargetVisitor) {
                return ((MultibindingsTargetVisitor)visitor).visit(this);
            }
            return (V)visitor.visit(binding);
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

        public Set<Dependency<?>> getDependencies() {
            if (!this.isInitialized()) {
                return ImmutableSet.of((Object)Dependency.get((Key)Key.get(Injector.class)));
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

