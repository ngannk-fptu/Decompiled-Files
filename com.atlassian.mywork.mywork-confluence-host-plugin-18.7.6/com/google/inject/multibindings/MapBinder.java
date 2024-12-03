/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.multibindings;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$ImmutableMap;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Lists;
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
        binder = binder.skipSources(MapBinder.class, RealMapBinder.class);
        return MapBinder.newMapBinder(binder, keyType, valueType, Key.get(MapBinder.mapOf(keyType, valueType)), Key.get(MapBinder.mapOfProviderOf(keyType, valueType)), Key.get(MapBinder.mapOf(keyType, Multibinder.setOf(valueType))), Key.get(MapBinder.mapOfSetOfProviderOf(keyType, valueType)), Multibinder.newSetBinder(binder, MapBinder.entryOfProviderOf(keyType, valueType)));
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, Class<K> keyType, Class<V> valueType) {
        return MapBinder.newMapBinder(binder, TypeLiteral.get(keyType), TypeLiteral.get(valueType));
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Annotation annotation) {
        binder = binder.skipSources(MapBinder.class, RealMapBinder.class);
        return MapBinder.newMapBinder(binder, keyType, valueType, Key.get(MapBinder.mapOf(keyType, valueType), annotation), Key.get(MapBinder.mapOfProviderOf(keyType, valueType), annotation), Key.get(MapBinder.mapOf(keyType, Multibinder.setOf(valueType)), annotation), Key.get(MapBinder.mapOfSetOfProviderOf(keyType, valueType), annotation), Multibinder.newSetBinder(binder, MapBinder.entryOfProviderOf(keyType, valueType), annotation));
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, Class<K> keyType, Class<V> valueType, Annotation annotation) {
        return MapBinder.newMapBinder(binder, TypeLiteral.get(keyType), TypeLiteral.get(valueType), annotation);
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Class<? extends Annotation> annotationType) {
        binder = binder.skipSources(MapBinder.class, RealMapBinder.class);
        return MapBinder.newMapBinder(binder, keyType, valueType, Key.get(MapBinder.mapOf(keyType, valueType), annotationType), Key.get(MapBinder.mapOfProviderOf(keyType, valueType), annotationType), Key.get(MapBinder.mapOf(keyType, Multibinder.setOf(valueType)), annotationType), Key.get(MapBinder.mapOfSetOfProviderOf(keyType, valueType), annotationType), Multibinder.newSetBinder(binder, MapBinder.entryOfProviderOf(keyType, valueType), annotationType));
    }

    public static <K, V> MapBinder<K, V> newMapBinder(Binder binder, Class<K> keyType, Class<V> valueType, Class<? extends Annotation> annotationType) {
        return MapBinder.newMapBinder(binder, TypeLiteral.get(keyType), TypeLiteral.get(valueType), annotationType);
    }

    static <K, V> TypeLiteral<Map<K, V>> mapOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), valueType.getType()));
    }

    static <K, V> TypeLiteral<Map<K, Provider<V>>> mapOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), Types.providerOf(valueType.getType())));
    }

    static <K, V> TypeLiteral<Map<K, Set<Provider<V>>>> mapOfSetOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.mapOf(keyType.getType(), Types.setOf(Types.providerOf(valueType.getType()))));
    }

    static <K, V> TypeLiteral<Map.Entry<K, Provider<V>>> entryOfProviderOf(TypeLiteral<K> keyType, TypeLiteral<V> valueType) {
        return TypeLiteral.get(Types.newParameterizedTypeWithOwner(Map.class, Map.Entry.class, new Type[]{keyType.getType(), Types.providerOf(valueType.getType())}));
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
        private final Key<Map<K, Provider<V>>> providerMapKey;
        private final Key<Map<K, Set<V>>> multimapKey;
        private final Key<Map<K, Set<Provider<V>>>> providerMultimapKey;
        private final Multibinder.RealMultibinder<Map.Entry<K, Provider<V>>> entrySetBinder;
        private Binder binder;
        private boolean permitDuplicates;
        private $ImmutableList<Map.Entry<K, Binding<V>>> mapBindings;

        private RealMapBinder(Binder binder, TypeLiteral<K> keyType, TypeLiteral<V> valueType, Key<Map<K, V>> mapKey, Key<Map<K, Provider<V>>> providerMapKey, Key<Map<K, Set<V>>> multimapKey, Key<Map<K, Set<Provider<V>>>> providerMultimapKey, Multibinder<Map.Entry<K, Provider<V>>> entrySetBinder) {
            this.keyType = keyType;
            this.valueType = valueType;
            this.mapKey = mapKey;
            this.providerMapKey = providerMapKey;
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
            Key<V> valueKey = Key.get(this.valueType, (Annotation)new RealElement(this.entrySetBinder.getSetName()));
            this.entrySetBinder.addBinding().toInstance(new MapEntry(key, this.binder.getProvider(valueKey), valueKey));
            return this.binder.bind(valueKey);
        }

        @Override
        public void configure(Binder binder) {
            Multibinder.checkConfiguration(!this.isInitialized(), "MapBinder was already initialized", new Object[0]);
            final $ImmutableSet<Dependency<Set<Map.Entry<K, Provider<V>>>>> dependencies = $ImmutableSet.of(Dependency.get(this.entrySetBinder.getSetKey()));
            final Provider<Set<Map.Entry<K, Provider<V>>>> entrySetProvider = binder.getProvider(this.entrySetBinder.getSetKey());
            binder.bind(this.providerMapKey).toProvider(new RealMapBinderProviderWithDependencies<Map<K, Provider<V>>>(this.mapKey){
                private Map<K, Provider<V>> providerMap;

                @Toolable
                @Inject
                void initialize(Injector injector) {
                    RealMapBinder.this.binder = null;
                    RealMapBinder.this.permitDuplicates = RealMapBinder.this.entrySetBinder.permitsDuplicates(injector);
                    LinkedHashMap providerMapMutable = new LinkedHashMap();
                    ArrayList bindingsMutable = $Lists.newArrayList();
                    for (Map.Entry entry : (Set)entrySetProvider.get()) {
                        Provider previous = (Provider)providerMapMutable.put(entry.getKey(), entry.getValue());
                        Multibinder.checkConfiguration(previous == null || RealMapBinder.this.permitDuplicates, "Map injection failed due to duplicated key \"%s\"", entry.getKey());
                        Key<?> valueKey = ((MapEntry)entry).getValueKey();
                        bindingsMutable.add(new MapEntry(entry.getKey(), injector.getBinding(valueKey), valueKey));
                    }
                    this.providerMap = $ImmutableMap.copyOf(providerMapMutable);
                    RealMapBinder.this.mapBindings = $ImmutableList.copyOf(bindingsMutable);
                }

                @Override
                public Map<K, Provider<V>> get() {
                    return this.providerMap;
                }

                @Override
                public Set<Dependency<?>> getDependencies() {
                    return dependencies;
                }
            });
            final Provider<Map<K, Provider<V>>> mapProvider = binder.getProvider(this.providerMapKey);
            binder.bind(this.mapKey).toProvider(new RealMapWithExtensionProvider<Map<K, V>>(this.mapKey){

                @Override
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

                @Override
                public Set<Dependency<?>> getDependencies() {
                    return dependencies;
                }

                @Override
                public <B, R> R acceptExtensionVisitor(BindingTargetVisitor<B, R> visitor, ProviderInstanceBinding<? extends B> binding) {
                    if (visitor instanceof MultibindingsTargetVisitor) {
                        return (R)((MultibindingsTargetVisitor)visitor).visit(this);
                    }
                    return visitor.visit(binding);
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
                    return key.equals(RealMapBinder.this.mapKey) || key.equals(RealMapBinder.this.providerMapKey) || key.equals(RealMapBinder.this.multimapKey) || key.equals(RealMapBinder.this.providerMultimapKey) || key.equals(RealMapBinder.this.entrySetBinder.getSetKey()) || RealMapBinder.this.matchesValueKey(key);
                }
            });
        }

        private boolean matchesValueKey(Key key) {
            return key.getAnnotation() instanceof Element && ((Element)key.getAnnotation()).setName().equals(this.entrySetBinder.getSetName()) && key.getTypeLiteral().equals(this.valueType);
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
        private static final class MapEntry<K, V>
        implements Map.Entry<K, V> {
            private final K key;
            private final V value;
            private final Key<?> valueKey;

            private MapEntry(K key, V value, Key<?> valueKey) {
                this.key = key;
                this.value = value;
                this.valueKey = valueKey;
            }

            public Key<?> getValueKey() {
                return this.valueKey;
            }

            @Override
            public K getKey() {
                return this.key;
            }

            @Override
            public V getValue() {
                return this.value;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Map.Entry && this.key.equals(((Map.Entry)obj).getKey()) && this.value.equals(((Map.Entry)obj).getValue());
            }

            @Override
            public int hashCode() {
                return 127 * ("key".hashCode() ^ this.key.hashCode()) + 127 * ("value".hashCode() ^ this.value.hashCode());
            }

            public String toString() {
                return "MapEntry(" + this.key + ", " + this.value + ")";
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

            @Override
            public void configure(Binder binder) {
                final $ImmutableSet<Dependency<Set<Map.Entry<K, Provider<V>>>>> dependencies = $ImmutableSet.of(Dependency.get(this.entrySetKey));
                final Provider<Set<Map.Entry<K, Provider<V>>>> entrySetProvider = binder.getProvider(this.entrySetKey);
                binder.bind(this.providerMultimapKey).toProvider(new RealMapBinderProviderWithDependencies<Map<K, Set<Provider<V>>>>(this.multimapKey){
                    private Map<K, Set<Provider<V>>> providerMultimap;

                    @Inject
                    void initialize(Injector injector) {
                        LinkedHashMap providerMultimapMutable = new LinkedHashMap();
                        for (Map.Entry entry : (Set)entrySetProvider.get()) {
                            if (!providerMultimapMutable.containsKey(entry.getKey())) {
                                providerMultimapMutable.put(entry.getKey(), $ImmutableSet.builder());
                            }
                            (($ImmutableSet.Builder)providerMultimapMutable.get(entry.getKey())).add(entry.getValue());
                        }
                        $ImmutableMap.Builder providerMultimapBuilder = $ImmutableMap.builder();
                        for (Map.Entry entry : providerMultimapMutable.entrySet()) {
                            providerMultimapBuilder.put(entry.getKey(), (($ImmutableSet.Builder)entry.getValue()).build());
                        }
                        this.providerMultimap = providerMultimapBuilder.build();
                    }

                    @Override
                    public Map<K, Set<Provider<V>>> get() {
                        return this.providerMultimap;
                    }

                    @Override
                    public Set<Dependency<?>> getDependencies() {
                        return dependencies;
                    }
                });
                final Provider<Map<K, Set<Provider<V>>>> multimapProvider = binder.getProvider(this.providerMultimapKey);
                binder.bind(this.multimapKey).toProvider(new RealMapBinderProviderWithDependencies<Map<K, Set<V>>>(this.multimapKey){

                    @Override
                    public Map<K, Set<V>> get() {
                        $ImmutableMap.Builder multimapBuilder = $ImmutableMap.builder();
                        for (Map.Entry entry : ((Map)multimapProvider.get()).entrySet()) {
                            Object key = entry.getKey();
                            $ImmutableSet.Builder valuesBuilder = $ImmutableSet.builder();
                            for (Provider valueProvider : (Set)entry.getValue()) {
                                Object value = valueProvider.get();
                                Multibinder.checkConfiguration(value != null, "Multimap injection failed due to null value for key \"%s\"", key);
                                valuesBuilder.add(value);
                            }
                            multimapBuilder.put(key, valuesBuilder.build());
                        }
                        return multimapBuilder.build();
                    }

                    @Override
                    public Set<Dependency<?>> getDependencies() {
                        return dependencies;
                    }
                });
            }
        }
    }
}

