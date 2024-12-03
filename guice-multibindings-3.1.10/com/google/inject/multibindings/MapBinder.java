/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.inject.Binder
 *  com.google.inject.Binding
 *  com.google.inject.Inject
 *  com.google.inject.Injector
 *  com.google.inject.Key
 *  com.google.inject.Module
 *  com.google.inject.Provider
 *  com.google.inject.TypeLiteral
 *  com.google.inject.binder.LinkedBindingBuilder
 *  com.google.inject.internal.RehashableKeys$Keys
 *  com.google.inject.spi.BindingTargetVisitor
 *  com.google.inject.spi.Dependency
 *  com.google.inject.spi.Element
 *  com.google.inject.spi.ProviderInstanceBinding
 *  com.google.inject.spi.ProviderLookup
 *  com.google.inject.spi.ProviderWithDependencies
 *  com.google.inject.spi.ProviderWithExtensionVisitor
 *  com.google.inject.spi.Toolable
 *  com.google.inject.util.Types
 *  javax.inject.Provider
 */
package com.google.inject.multibindings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.RehashableKeys;
import com.google.inject.multibindings.Element;
import com.google.inject.multibindings.MapBinderBinding;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsTargetVisitor;
import com.google.inject.multibindings.RealElement;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.ProviderWithDependencies;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.spi.Toolable;
import com.google.inject.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class MapBinder<K, V> {
    private MapBinder() {
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        binder = binder.skipSources(new Class[]{MapBinder.class, RealMapBinder.class});
        return MapBinder.newMapBinder(binder, keyType, valueType, Key.get(MapBinder.mapOf(keyType, valueType)), Key.get(MapBinder.mapOfProviderOf(keyType, valueType)), Key.get(MapBinder.mapOf(keyType, Multibinder.setOf(valueType))), Key.get(MapBinder.mapOfSetOfProviderOf(keyType, valueType)), Multibinder.newSetBinder(binder, MapBinder.entryOfProviderOf(keyType, valueType)));
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, Class<K> keyType, Class<V> valueType) {
        return MapBinder.newMapBinder(binder, TypeLiteral.get(keyType), TypeLiteral.get(valueType));
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Annotation annotation) {
        binder = binder.skipSources(new Class[]{MapBinder.class, RealMapBinder.class});
        return MapBinder.newMapBinder(binder, keyType, valueType, Key.get(MapBinder.mapOf(keyType, valueType), (Annotation)annotation), Key.get(MapBinder.mapOfProviderOf(keyType, valueType), (Annotation)annotation), Key.get(MapBinder.mapOf(keyType, Multibinder.setOf(valueType)), (Annotation)annotation), Key.get(MapBinder.mapOfSetOfProviderOf(keyType, valueType), (Annotation)annotation), Multibinder.newSetBinder(binder, MapBinder.entryOfProviderOf(keyType, valueType), annotation));
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, Class<K> keyType, Class<V> valueType, Annotation annotation) {
        return MapBinder.newMapBinder(binder, TypeLiteral.get(keyType), TypeLiteral.get(valueType), annotation);
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Class<? extends Annotation> annotationType) {
        binder = binder.skipSources(new Class[]{MapBinder.class, RealMapBinder.class});
        return MapBinder.newMapBinder(binder, keyType, valueType, Key.get(MapBinder.mapOf(keyType, valueType), annotationType), Key.get(MapBinder.mapOfProviderOf(keyType, valueType), annotationType), Key.get(MapBinder.mapOf(keyType, Multibinder.setOf(valueType)), annotationType), Key.get(MapBinder.mapOfSetOfProviderOf(keyType, valueType), annotationType), Multibinder.newSetBinder(binder, MapBinder.entryOfProviderOf(keyType, valueType), annotationType));
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, Class<K> keyType, Class<V> valueType, Class<? extends Annotation> annotationType) {
        return MapBinder.newMapBinder(binder, TypeLiteral.get(keyType), TypeLiteral.get(valueType), annotationType);
    }

    static <K, V> TypeLiteral<Map<K, V>> mapOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get((Type)Types.mapOf((Type)keyType.getType(), (Type)valueType.getType()));
    }

    static <K, V> TypeLiteral<Map<K, Provider<V>>> mapOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get((Type)Types.mapOf((Type)keyType.getType(), (Type)Types.providerOf((Type)valueType.getType())));
    }

    static <K, V> TypeLiteral<Map<K, javax.inject.Provider<V>>> mapOfJavaxProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get((Type)Types.mapOf((Type)keyType.getType(), (Type)Types.newParameterizedType(javax.inject.Provider.class, (Type[])new Type[]{valueType.getType()})));
    }

    static <K, V> TypeLiteral<Map<K, Set<Provider<V>>>> mapOfSetOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get((Type)Types.mapOf((Type)keyType.getType(), (Type)Types.setOf((Type)Types.providerOf((Type)valueType.getType()))));
    }

    static <K, V> TypeLiteral<Map.Entry<K, Provider<V>>> entryOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get((Type)Types.newParameterizedTypeWithOwner(Map.class, Map.Entry.class, (Type[])new Type[]{keyType.getType(), Types.providerOf((Type)valueType.getType())}));
    }

    private static <K, V> MapBinder<K, V> newMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Key<Map<K, V>> mapKey, Key<Map<K, Provider<V>>> providerMapKey, Key<Map<K, Set<V>>> multimapKey, Key<Map<K, Set<Provider<V>>>> providerMultimapKey, Multibinder<Map.Entry<K, Provider<V>>> entrySetBinder) {
        RealMapBinder mapBinder = new RealMapBinder(binder, keyType, valueType, mapKey, providerMapKey, multimapKey, providerMultimapKey, entrySetBinder);
        binder.install(mapBinder);
        return mapBinder;
    }

    public abstract MapBinder<K, V> permitDuplicates();

    public abstract LinkedBindingBuilder<V> addBinding(K var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class RealMapBinder<K, V>
    extends MapBinder<K, V>
    implements Module {
        private final TypeLiteral<K> keyType;
        private final TypeLiteral<V> valueType;
        private final Key<Map<K, V>> mapKey;
        private final Key<Map<K, javax.inject.Provider<V>>> javaxProviderMapKey;
        private final Key<Map<K, Provider<V>>> providerMapKey;
        private final Key<Map<K, Set<V>>> multimapKey;
        private final Key<Map<K, Set<Provider<V>>>> providerMultimapKey;
        private final Multibinder.RealMultibinder<Map.Entry<K, Provider<V>>> entrySetBinder;
        private Binder binder;
        private boolean permitDuplicates;
        private ImmutableList<Map.Entry<K, Binding<V>>> mapBindings;

        private RealMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Key<Map<K, V>> mapKey, Key<Map<K, Provider<V>>> providerMapKey, Key<Map<K, Set<V>>> multimapKey, Key<Map<K, Set<Provider<V>>>> providerMultimapKey, Multibinder<Map.Entry<K, Provider<V>>> entrySetBinder) {
            this.keyType = keyType;
            this.valueType = valueType;
            this.mapKey = mapKey;
            this.providerMapKey = providerMapKey;
            this.javaxProviderMapKey = providerMapKey.ofType(RealMapBinder.mapOfJavaxProviderOf(keyType, valueType));
            this.multimapKey = multimapKey;
            this.providerMultimapKey = providerMultimapKey;
            this.entrySetBinder = (Multibinder.RealMultibinder)entrySetBinder;
            this.binder = binder;
        }

        @Override
        public MapBinder<K, V> permitDuplicates() {
            this.entrySetBinder.permitDuplicates();
            this.binder.install(new MultimapBinder<K, V>(this.multimapKey, this.providerMultimapKey, this.entrySetBinder.getSetKey()));
            return this;
        }

        @Override
        public LinkedBindingBuilder<V> addBinding(K key) {
            Multibinder.checkNotNull(key, "key");
            Multibinder.checkConfiguration(!this.isInitialized(), "MapBinder was already initialized", new Object[0]);
            RealElement.BindingBuilder<V> valueBinding = RealElement.addMapBinding(this.binder, key, this.valueType, this.entrySetBinder.getSetName());
            Key valueKey = Key.get(this.valueType, (Annotation)valueBinding.getAnnotation());
            this.entrySetBinder.addBinding().toInstance(new ProviderMapEntry(key, this.binder.getProvider(valueKey), valueKey));
            return valueBinding;
        }

        public void configure(Binder binder) {
            Multibinder.checkConfiguration(!this.isInitialized(), "MapBinder was already initialized", new Object[0]);
            final ImmutableSet dependencies = ImmutableSet.of((Object)Dependency.get(this.entrySetBinder.getSetKey()));
            final Provider entrySetProvider = binder.getProvider(this.entrySetBinder.getSetKey());
            binder.bind(this.providerMapKey).toProvider((Provider)new RealMapBinderProviderWithDependencies<Map<K, Provider<V>>>(this.mapKey){
                private Map<K, Provider<V>> providerMap;

                @Toolable
                @Inject
                void initialize(Injector injector) {
                    RealMapBinder.this.binder = null;
                    RealMapBinder.this.permitDuplicates = RealMapBinder.this.entrySetBinder.permitsDuplicates(injector);
                    LinkedHashMap providerMapMutable = new LinkedHashMap();
                    ArrayList bindingsMutable = Lists.newArrayList();
                    for (Map.Entry entry : (Set)entrySetProvider.get()) {
                        Provider previous = (Provider)providerMapMutable.put(entry.getKey(), entry.getValue());
                        Multibinder.checkConfiguration(previous == null || RealMapBinder.this.permitDuplicates, "Map injection failed due to duplicated key \"%s\"", entry.getKey());
                        ProviderMapEntry providerEntry = (ProviderMapEntry)entry;
                        Key valueKey = providerEntry.getValueKey();
                        bindingsMutable.add(Maps.immutableEntry(entry.getKey(), (Object)injector.getBinding(valueKey)));
                    }
                    this.providerMap = ImmutableMap.copyOf(providerMapMutable);
                    RealMapBinder.this.mapBindings = ImmutableList.copyOf((Collection)bindingsMutable);
                }

                public Map<K, Provider<V>> get() {
                    return this.providerMap;
                }

                public Set<Dependency<?>> getDependencies() {
                    return dependencies;
                }
            });
            Key<Map<K, Provider<V>>> massagedProviderMapKey = this.providerMapKey;
            binder.bind(this.javaxProviderMapKey).to(massagedProviderMapKey);
            final Provider mapProvider = binder.getProvider(this.providerMapKey);
            binder.bind(this.mapKey).toProvider((Provider)new RealMapWithExtensionProvider<Map<K, V>>(this.mapKey){

                public Map<K, V> get() {
                    LinkedHashMap map = new LinkedHashMap();
                    for (Map.Entry entry : ((Map)mapProvider.get()).entrySet()) {
                        Object value = ((Provider)entry.getValue()).get();
                        Object key = entry.getKey();
                        Multibinder.checkConfiguration(value != null, "Map injection failed due to null value for key \"%s\"", key);
                        map.put(key, value);
                    }
                    return Collections.unmodifiableMap(map);
                }

                public Set<Dependency<?>> getDependencies() {
                    return dependencies;
                }

                public <B, R> R acceptExtensionVisitor(BindingTargetVisitor<B, R> visitor, ProviderInstanceBinding<? extends B> binding) {
                    if (visitor instanceof MultibindingsTargetVisitor) {
                        return (R)((MultibindingsTargetVisitor)visitor).visit(this);
                    }
                    return (R)visitor.visit(binding);
                }

                @Override
                public Key<Map<K, V>> getMapKey() {
                    return RealMapBinder.this.mapKey;
                }

                @Override
                public TypeLiteral<?> getKeyTypeLiteral() {
                    return RealMapBinder.this.keyType;
                }

                @Override
                public TypeLiteral<?> getValueTypeLiteral() {
                    return RealMapBinder.this.valueType;
                }

                @Override
                public List<Map.Entry<?, Binding<?>>> getEntries() {
                    if (RealMapBinder.this.isInitialized()) {
                        return RealMapBinder.this.mapBindings;
                    }
                    throw new UnsupportedOperationException("getElements() not supported for module bindings");
                }

                @Override
                public boolean permitsDuplicates() {
                    if (RealMapBinder.this.isInitialized()) {
                        return RealMapBinder.this.permitDuplicates;
                    }
                    throw new UnsupportedOperationException("permitsDuplicates() not supported for module bindings");
                }

                @Override
                public boolean containsElement(com.google.inject.spi.Element element) {
                    Key key;
                    if (RealMapBinder.this.entrySetBinder.containsElement(element)) {
                        return true;
                    }
                    if (element instanceof Binding) {
                        key = ((Binding)element).getKey();
                    } else if (element instanceof ProviderLookup) {
                        key = ((ProviderLookup)element).getKey();
                    } else {
                        return false;
                    }
                    return key.equals((Object)RealMapBinder.this.mapKey) || key.equals((Object)RealMapBinder.this.providerMapKey) || key.equals((Object)RealMapBinder.this.javaxProviderMapKey) || key.equals((Object)RealMapBinder.this.multimapKey) || key.equals((Object)RealMapBinder.this.providerMultimapKey) || key.equals(RealMapBinder.this.entrySetBinder.getSetKey()) || RealMapBinder.this.matchesValueKey(key);
                }
            });
        }

        private boolean matchesValueKey(Key<?> key) {
            return key.getAnnotation() instanceof Element && ((Element)key.getAnnotation()).setName().equals(this.entrySetBinder.getSetName()) && ((Element)key.getAnnotation()).type() == Element.Type.MAPBINDER && key.getTypeLiteral().equals(this.valueType);
        }

        private boolean isInitialized() {
            return this.binder == null;
        }

        public boolean equals(Object o) {
            return o instanceof RealMapBinder && ((RealMapBinder)o).mapKey.equals(this.mapKey);
        }

        public int hashCode() {
            return this.mapKey.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static abstract class RealMapBinderProviderWithDependencies<T>
        implements ProviderWithDependencies<T> {
            private final Object equality;

            public RealMapBinderProviderWithDependencies(Object equality) {
                this.equality = equality;
            }

            public boolean equals(Object obj) {
                return this.getClass() == obj.getClass() && this.equality.equals(((RealMapBinderProviderWithDependencies)obj).equality);
            }

            public int hashCode() {
                return this.equality.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static abstract class RealMapWithExtensionProvider<T>
        extends RealMapBinderProviderWithDependencies<T>
        implements ProviderWithExtensionVisitor<T>,
        MapBinderBinding<T> {
            public RealMapWithExtensionProvider(Object equality) {
                super(equality);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static final class ProviderMapEntry<K, V>
        implements Map.Entry<K, Provider<V>> {
            private final K key;
            private final Provider<V> provider;
            private volatile Key<V> valueKey;

            private ProviderMapEntry(K key, Provider<V> provider, Key<V> valueKey) {
                this.key = key;
                this.provider = provider;
                this.valueKey = valueKey;
            }

            public Key<V> getValueKey() {
                Key currentValueKey = this.valueKey;
                if (RehashableKeys.Keys.needsRehashing(currentValueKey)) {
                    this.valueKey = currentValueKey = RehashableKeys.Keys.rehash(currentValueKey);
                }
                return currentValueKey;
            }

            @Override
            public K getKey() {
                return this.key;
            }

            @Override
            public Provider<V> getValue() {
                return this.provider;
            }

            @Override
            public Provider<V> setValue(Provider<V> value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof ProviderMapEntry && this.key.equals(((ProviderMapEntry)obj).getKey()) && this.getValueKey().equals(((ProviderMapEntry)obj).getValueKey());
            }

            @Override
            public int hashCode() {
                return this.key.hashCode();
            }

            public String toString() {
                return "ProviderMapEntry(" + this.key + ", " + this.valueKey + ")";
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private static final class MultimapBinder<K, V>
        implements Module {
            private final Key<Map<K, Set<V>>> multimapKey;
            private final Key<Map<K, Set<Provider<V>>>> providerMultimapKey;
            private final Key<Set<Map.Entry<K, Provider<V>>>> entrySetKey;

            public MultimapBinder(Key<Map<K, Set<V>>> multimapKey, Key<Map<K, Set<Provider<V>>>> providerMultimapKey, Key<Set<Map.Entry<K, Provider<V>>>> entrySetKey) {
                this.multimapKey = multimapKey;
                this.providerMultimapKey = providerMultimapKey;
                this.entrySetKey = entrySetKey;
            }

            public void configure(Binder binder) {
                final ImmutableSet dependencies = ImmutableSet.of((Object)Dependency.get(this.entrySetKey));
                final Provider entrySetProvider = binder.getProvider(this.entrySetKey);
                binder.bind(this.providerMultimapKey).toProvider((Provider)new RealMapBinderProviderWithDependencies<Map<K, Set<Provider<V>>>>(this.multimapKey){
                    private Map<K, Set<Provider<V>>> providerMultimap;

                    @Inject
                    void initialize(Injector injector) {
                        LinkedHashMap providerMultimapMutable = new LinkedHashMap();
                        for (Map.Entry entry : (Set)entrySetProvider.get()) {
                            if (!providerMultimapMutable.containsKey(entry.getKey())) {
                                providerMultimapMutable.put(entry.getKey(), ImmutableSet.builder());
                            }
                            ((ImmutableSet.Builder)providerMultimapMutable.get(entry.getKey())).add(entry.getValue());
                        }
                        ImmutableMap.Builder providerMultimapBuilder = ImmutableMap.builder();
                        for (Map.Entry entry : providerMultimapMutable.entrySet()) {
                            providerMultimapBuilder.put(entry.getKey(), (Object)((ImmutableSet.Builder)entry.getValue()).build());
                        }
                        this.providerMultimap = providerMultimapBuilder.build();
                    }

                    public Map<K, Set<Provider<V>>> get() {
                        return this.providerMultimap;
                    }

                    public Set<Dependency<?>> getDependencies() {
                        return dependencies;
                    }
                });
                final Provider multimapProvider = binder.getProvider(this.providerMultimapKey);
                binder.bind(this.multimapKey).toProvider((Provider)new RealMapBinderProviderWithDependencies<Map<K, Set<V>>>(this.multimapKey){

                    public Map<K, Set<V>> get() {
                        ImmutableMap.Builder multimapBuilder = ImmutableMap.builder();
                        for (Map.Entry entry : ((Map)multimapProvider.get()).entrySet()) {
                            Object key = entry.getKey();
                            ImmutableSet.Builder valuesBuilder = ImmutableSet.builder();
                            for (Provider valueProvider : (Set)entry.getValue()) {
                                Object value = valueProvider.get();
                                Multibinder.checkConfiguration(value != null, "Multimap injection failed due to null value for key \"%s\"", key);
                                valuesBuilder.add(value);
                            }
                            multimapBuilder.put(key, (Object)valuesBuilder.build());
                        }
                        return multimapBuilder.build();
                    }

                    public Set<Dependency<?>> getDependencies() {
                        return dependencies;
                    }
                });
            }

            public int hashCode() {
                return this.multimapKey.hashCode();
            }

            public boolean equals(Object o) {
                return o instanceof MultimapBinder && ((MultimapBinder)o).multimapKey.equals(this.multimapKey);
            }
        }
    }
}

