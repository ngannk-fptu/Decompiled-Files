/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.convert.EntityInstantiator
 *  org.springframework.data.mapping.MappingException
 *  org.springframework.data.mapping.PersistentProperty
 *  org.springframework.data.mapping.PersistentPropertyAccessor
 *  org.springframework.data.mapping.PreferredConstructor$Parameter
 *  org.springframework.data.mapping.context.MappingContext
 *  org.springframework.data.mapping.model.ConvertingPropertyAccessor
 *  org.springframework.data.mapping.model.ParameterValueProvider
 *  org.springframework.data.mapping.model.PersistentEntityParameterValueProvider
 *  org.springframework.data.mapping.model.PropertyValueProvider
 *  org.springframework.data.util.ClassTypeInformation
 *  org.springframework.data.util.TypeInformation
 */
package org.springframework.vault.repository.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.convert.EntityInstantiator;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.data.mapping.model.ParameterValueProvider;
import org.springframework.data.mapping.model.PersistentEntityParameterValueProvider;
import org.springframework.data.mapping.model.PropertyValueProvider;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.vault.repository.convert.AbstractVaultConverter;
import org.springframework.vault.repository.convert.DefaultVaultTypeMapper;
import org.springframework.vault.repository.convert.SecretDocument;
import org.springframework.vault.repository.convert.SecretDocumentAccessor;
import org.springframework.vault.repository.convert.VaultTypeMapper;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;

public class MappingVaultConverter
extends AbstractVaultConverter {
    private final MappingContext<? extends VaultPersistentEntity<?>, VaultPersistentProperty> mappingContext;
    private VaultTypeMapper typeMapper;

    public MappingVaultConverter(MappingContext<? extends VaultPersistentEntity<?>, VaultPersistentProperty> mappingContext) {
        super(new DefaultConversionService());
        Assert.notNull(mappingContext, "MappingContext must not be null");
        this.mappingContext = mappingContext;
        this.typeMapper = new DefaultVaultTypeMapper("_class", mappingContext);
    }

    public void setTypeMapper(VaultTypeMapper typeMapper) {
        Assert.notNull((Object)typeMapper, "VaultTypeMapper must not be null");
        this.typeMapper = typeMapper;
    }

    public MappingContext<? extends VaultPersistentEntity<?>, VaultPersistentProperty> getMappingContext() {
        return this.mappingContext;
    }

    public <S> S read(Class<S> type, SecretDocument source) {
        return this.read((TypeInformation<S>)ClassTypeInformation.from(type), (Object)source);
    }

    private <S> S read(TypeInformation<S> type, Object source) {
        SecretDocument secretDocument = this.getSecretDocument(source);
        ClassTypeInformation typeToUse = secretDocument != null ? this.typeMapper.readType(secretDocument.getBody(), type) : ClassTypeInformation.OBJECT;
        Class rawType = typeToUse.getType();
        if (this.conversions.hasCustomReadTarget(source.getClass(), rawType)) {
            return (S)this.conversionService.convert(source, rawType);
        }
        if (SecretDocument.class.isAssignableFrom(rawType)) {
            return (S)source;
        }
        if (Map.class.isAssignableFrom(rawType) && secretDocument != null) {
            return (S)secretDocument.getBody();
        }
        if (typeToUse.isMap() && secretDocument != null) {
            return (S)this.readMap((TypeInformation<?>)typeToUse, secretDocument.getBody());
        }
        if (typeToUse.equals(ClassTypeInformation.OBJECT)) {
            return (S)source;
        }
        return this.read((VaultPersistentEntity)this.mappingContext.getRequiredPersistentEntity((TypeInformation)typeToUse), secretDocument);
    }

    @Nullable
    private SecretDocument getSecretDocument(Object source) {
        SecretDocument secretDocument = null;
        if (source instanceof Map) {
            secretDocument = new SecretDocument((Map)source);
        } else if (source instanceof SecretDocument) {
            secretDocument = (SecretDocument)source;
        }
        return secretDocument;
    }

    private ParameterValueProvider<VaultPersistentProperty> getParameterProvider(VaultPersistentEntity<?> entity, SecretDocument source) {
        VaultPropertyValueProvider provider = new VaultPropertyValueProvider(source);
        final PersistentEntityParameterValueProvider parameterProvider = new PersistentEntityParameterValueProvider(entity, (PropertyValueProvider)provider, (Object)source);
        return new ParameterValueProvider<VaultPersistentProperty>(){

            @Nullable
            public <T> T getParameterValue(PreferredConstructor.Parameter<T, VaultPersistentProperty> parameter) {
                Object value = parameterProvider.getParameterValue(parameter);
                return (T)(value != null ? MappingVaultConverter.this.readValue(value, parameter.getType()) : null);
            }
        };
    }

    private <S> S read(VaultPersistentEntity<S> entity, SecretDocument source) {
        ParameterValueProvider<VaultPersistentProperty> provider = this.getParameterProvider(entity, source);
        EntityInstantiator instantiator = this.instantiators.getInstantiatorFor(entity);
        Object instance = instantiator.createInstance(entity, provider);
        ConvertingPropertyAccessor accessor = new ConvertingPropertyAccessor(entity.getPropertyAccessor(instance), (ConversionService)this.conversionService);
        VaultPersistentProperty idProperty = (VaultPersistentProperty)entity.getIdProperty();
        SecretDocumentAccessor documentAccessor = new SecretDocumentAccessor(source);
        if (entity.requiresPropertyPopulation()) {
            if (idProperty != null && !entity.isConstructorArgument((PersistentProperty)idProperty) && documentAccessor.hasValue(idProperty)) {
                Object idValue = this.readIdValue(idProperty, documentAccessor);
                accessor.setProperty((PersistentProperty)idProperty, idValue);
            }
            VaultPropertyValueProvider valueProvider = new VaultPropertyValueProvider(documentAccessor);
            this.readProperties(entity, (PersistentPropertyAccessor)accessor, idProperty, documentAccessor, valueProvider);
        }
        return (S)instance;
    }

    @Nullable
    private Object readIdValue(VaultPersistentProperty idProperty, SecretDocumentAccessor documentAccessor) {
        Object resolvedValue = documentAccessor.get(idProperty);
        return resolvedValue != null ? this.readValue(resolvedValue, idProperty.getTypeInformation()) : null;
    }

    private void readProperties(VaultPersistentEntity<?> entity, PersistentPropertyAccessor accessor, @Nullable VaultPersistentProperty idProperty, SecretDocumentAccessor documentAccessor, VaultPropertyValueProvider valueProvider) {
        Iterator iterator = entity.iterator();
        while (iterator.hasNext()) {
            VaultPersistentProperty prop = (VaultPersistentProperty)((Object)iterator.next());
            if (idProperty != null && idProperty.equals((Object)prop) || entity.isConstructorArgument((PersistentProperty)prop) || !documentAccessor.hasValue(prop)) continue;
            accessor.setProperty((PersistentProperty)prop, valueProvider.getPropertyValue(prop));
        }
    }

    @Nullable
    private <T> T readValue(Object value, TypeInformation<?> type) {
        Class rawType = type.getType();
        if (this.conversions.hasCustomReadTarget(value.getClass(), rawType)) {
            return this.conversionService.convert(value, rawType);
        }
        if (value instanceof List) {
            return (T)this.readCollectionOrArray(type, (List)value);
        }
        if (value instanceof Map) {
            return (T)this.read(type, (Object)((Map)value));
        }
        return (T)this.getPotentiallyConvertedSimpleRead(value, rawType);
    }

    @Nullable
    private Object readCollectionOrArray(TypeInformation<?> targetType, List sourceValue) {
        Collection<Object> items;
        Assert.notNull(targetType, "Target type must not be null");
        Class<List> collectionType = targetType.getType();
        ClassTypeInformation componentType = targetType.getComponentType() != null ? targetType.getComponentType() : ClassTypeInformation.OBJECT;
        Class rawComponentType = componentType.getType();
        collectionType = Collection.class.isAssignableFrom(collectionType) ? collectionType : List.class;
        Collection collection = items = targetType.getType().isArray() ? new ArrayList(sourceValue.size()) : CollectionFactory.createCollection(collectionType, rawComponentType, sourceValue.size());
        if (sourceValue.isEmpty()) {
            return this.getPotentiallyConvertedSimpleRead(items, collectionType);
        }
        for (Object obj : sourceValue) {
            if (obj instanceof Map) {
                items.add(this.read((TypeInformation)componentType, (Object)((Map)obj)));
                continue;
            }
            if (obj instanceof List) {
                items.add(this.readCollectionOrArray((TypeInformation<?>)ClassTypeInformation.OBJECT, (List)obj));
                continue;
            }
            items.add(this.getPotentiallyConvertedSimpleRead(obj, rawComponentType));
        }
        return this.getPotentiallyConvertedSimpleRead(items, targetType.getType());
    }

    protected Map<Object, Object> readMap(TypeInformation<?> type, Map<String, Object> sourceMap) {
        Assert.notNull(sourceMap, "Source map must not be null");
        Class mapType = this.typeMapper.readType(sourceMap, type).getType();
        TypeInformation keyType = type.getComponentType();
        TypeInformation valueType = type.getMapValueType();
        Class rawKeyType = keyType != null ? keyType.getType() : null;
        Class rawValueType = valueType != null ? valueType.getType() : null;
        Map<Object, Object> map = CollectionFactory.createMap(mapType, rawKeyType, sourceMap.keySet().size());
        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            TypeInformation defaultedValueType;
            if (this.typeMapper.isTypeKey(entry.getKey())) continue;
            String key = entry.getKey();
            if (rawKeyType != null && !rawKeyType.isAssignableFrom(key.getClass())) {
                key = this.conversionService.convert((Object)key, rawKeyType);
            }
            Object value = entry.getValue();
            Object object = defaultedValueType = valueType != null ? valueType : ClassTypeInformation.OBJECT;
            if (value instanceof Map) {
                map.put(key, this.read(defaultedValueType, (Object)((Map)value)));
                continue;
            }
            if (value instanceof List) {
                map.put(key, this.readCollectionOrArray((TypeInformation<?>)(valueType != null ? valueType : ClassTypeInformation.LIST), (List)value));
                continue;
            }
            map.put(key, this.getPotentiallyConvertedSimpleRead(value, rawValueType));
        }
        return map;
    }

    @Nullable
    private Object getPotentiallyConvertedSimpleRead(@Nullable Object value, @Nullable Class<?> target) {
        if (value == null || target == null || target.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (Enum.class.isAssignableFrom(target)) {
            return Enum.valueOf(target, value.toString());
        }
        return this.conversionService.convert(value, target);
    }

    public void write(Object source, SecretDocument sink) {
        Class<?> entityType = ClassUtils.getUserClass(source.getClass());
        ClassTypeInformation type = ClassTypeInformation.from(entityType);
        SecretDocumentAccessor documentAccessor = new SecretDocumentAccessor(sink);
        this.writeInternal(source, documentAccessor, (TypeInformation<?>)type);
        boolean handledByCustomConverter = this.conversions.hasCustomWriteTarget(entityType, SecretDocument.class);
        if (!handledByCustomConverter) {
            this.typeMapper.writeType((TypeInformation)type, sink.getBody());
        }
    }

    protected void writeInternal(Object obj, SecretDocumentAccessor sink, @Nullable TypeInformation<?> typeHint) {
        Class<?> entityType = obj.getClass();
        Optional customTarget = this.conversions.getCustomWriteTarget(entityType, SecretDocument.class);
        if (customTarget.isPresent()) {
            SecretDocument result = this.conversionService.convert(obj, SecretDocument.class);
            if (result.getId() != null) {
                sink.setId(result.getId());
            }
            sink.getBody().putAll(result.getBody());
            return;
        }
        if (Map.class.isAssignableFrom(entityType)) {
            this.writeMapInternal((Map)obj, sink.getBody(), (TypeInformation<?>)ClassTypeInformation.MAP);
            return;
        }
        VaultPersistentEntity entity = (VaultPersistentEntity)this.mappingContext.getRequiredPersistentEntity(entityType);
        this.writeInternal(obj, sink, entity);
        this.addCustomTypeKeyIfNecessary(typeHint, obj, sink);
    }

    protected void writeInternal(Object obj, SecretDocumentAccessor sink, VaultPersistentEntity<?> entity) {
        Object value;
        PersistentPropertyAccessor accessor = entity.getPropertyAccessor(obj);
        VaultPersistentProperty idProperty = (VaultPersistentProperty)entity.getIdProperty();
        if (idProperty != null && !sink.hasValue(idProperty) && (value = accessor.getProperty((PersistentProperty)idProperty)) != null) {
            sink.put(idProperty, value);
        }
        this.writeProperties(entity, accessor, sink, idProperty);
    }

    private void writeProperties(VaultPersistentEntity<?> entity, PersistentPropertyAccessor accessor, SecretDocumentAccessor sink, @Nullable VaultPersistentProperty idProperty) {
        Iterator iterator = entity.iterator();
        while (iterator.hasNext()) {
            Object value;
            VaultPersistentProperty prop = (VaultPersistentProperty)((Object)iterator.next());
            if (prop.equals((Object)idProperty) || !prop.isWritable() || (value = accessor.getProperty((PersistentProperty)prop)) == null) continue;
            if (!this.conversions.isSimpleType(value.getClass())) {
                this.writePropertyInternal(value, sink, prop);
                continue;
            }
            sink.put(prop, this.getPotentiallyConvertedSimpleWrite(value));
        }
    }

    protected void writePropertyInternal(@Nullable Object obj, SecretDocumentAccessor accessor, VaultPersistentProperty prop) {
        if (obj == null) {
            return;
        }
        ClassTypeInformation valueType = ClassTypeInformation.from(obj.getClass());
        TypeInformation type = prop.getTypeInformation();
        if (valueType.isCollectionLike()) {
            List<Object> collectionInternal = this.createCollection(MappingVaultConverter.asCollection(obj), prop);
            accessor.put(prop, collectionInternal);
            return;
        }
        if (valueType.isMap()) {
            Map<String, Object> mapDbObj = this.createMap((Map)obj, prop);
            accessor.put(prop, mapDbObj);
            return;
        }
        Optional basicTargetType = this.conversions.getCustomWriteTarget(obj.getClass());
        if (basicTargetType.isPresent()) {
            accessor.put(prop, this.conversionService.convert(obj, (Class)basicTargetType.get()));
            return;
        }
        VaultPersistentEntity entity = MappingVaultConverter.isSubtype(prop.getType(), obj.getClass()) ? (VaultPersistentEntity)this.mappingContext.getRequiredPersistentEntity(obj.getClass()) : (VaultPersistentEntity)this.mappingContext.getRequiredPersistentEntity(type);
        SecretDocumentAccessor nested = accessor.writeNested(prop);
        this.writeInternal(obj, nested, entity);
        this.addCustomTypeKeyIfNecessary((TypeInformation<?>)ClassTypeInformation.from((Class)prop.getRawType()), obj, nested);
    }

    private static boolean isSubtype(Class<?> left, Class<?> right) {
        return left.isAssignableFrom(right) && !left.equals(right);
    }

    protected List<Object> createCollection(Collection<?> collection, VaultPersistentProperty property) {
        return this.writeCollectionInternal(collection, property.getTypeInformation(), new ArrayList<Object>());
    }

    private List<Object> writeCollectionInternal(Collection<?> source, @Nullable TypeInformation<?> type, List<Object> sink) {
        TypeInformation componentType = null;
        if (type != null) {
            componentType = type.getComponentType();
        }
        for (Object element : source) {
            Class<?> elementType;
            Class<?> clazz = elementType = element == null ? null : element.getClass();
            if (elementType == null || this.conversions.isSimpleType(elementType)) {
                sink.add(this.getPotentiallyConvertedSimpleWrite(element));
                continue;
            }
            if (element instanceof Collection || elementType.isArray()) {
                sink.add(this.writeCollectionInternal(MappingVaultConverter.asCollection(element), componentType, new ArrayList<Object>()));
                continue;
            }
            SecretDocumentAccessor accessor = new SecretDocumentAccessor(new SecretDocument());
            this.writeInternal(element, accessor, componentType);
            sink.add(accessor.getBody());
        }
        return sink;
    }

    protected Map<String, Object> createMap(Map<Object, Object> map, VaultPersistentProperty property) {
        Assert.notNull(map, "Given map must not be null");
        Assert.notNull((Object)property, "PersistentProperty must not be null");
        return this.writeMapInternal(map, new LinkedHashMap<String, Object>(), property.getTypeInformation());
    }

    protected Map<String, Object> writeMapInternal(Map<Object, Object> obj, Map<String, Object> bson, TypeInformation<?> propertyType) {
        for (Map.Entry<Object, Object> entry : obj.entrySet()) {
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (this.conversions.isSimpleType(key.getClass())) {
                String simpleKey = key.toString();
                if (val == null || this.conversions.isSimpleType(val.getClass())) {
                    bson.put(simpleKey, val);
                    continue;
                }
                if (val instanceof Collection || val.getClass().isArray()) {
                    bson.put(simpleKey, this.writeCollectionInternal(MappingVaultConverter.asCollection(val), propertyType.getMapValueType(), new ArrayList<Object>()));
                    continue;
                }
                SecretDocumentAccessor nested = new SecretDocumentAccessor(new SecretDocument());
                ClassTypeInformation valueTypeInfo = propertyType.isMap() ? propertyType.getMapValueType() : ClassTypeInformation.OBJECT;
                this.writeInternal(val, nested, (TypeInformation<?>)valueTypeInfo);
                bson.put(simpleKey, nested.getBody());
                continue;
            }
            throw new MappingException("Cannot use a complex object as a key value.");
        }
        return bson;
    }

    protected void addCustomTypeKeyIfNecessary(@Nullable TypeInformation<?> type, Object value, SecretDocumentAccessor accessor) {
        boolean notTheSameClass;
        Class reference = type != null ? type.getActualType().getType() : Object.class;
        Class<?> valueType = ClassUtils.getUserClass(value.getClass());
        boolean bl = notTheSameClass = !valueType.equals(reference);
        if (notTheSameClass) {
            this.typeMapper.writeType(valueType, accessor.getBody());
        }
    }

    @Nullable
    private Object getPotentiallyConvertedSimpleWrite(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        Optional customTarget = this.conversions.getCustomWriteTarget(value.getClass());
        if (customTarget.isPresent()) {
            return this.conversionService.convert(value, (Class)customTarget.get());
        }
        if (ObjectUtils.isArray(value)) {
            if (value instanceof byte[]) {
                return value;
            }
            return MappingVaultConverter.asCollection(value);
        }
        return Enum.class.isAssignableFrom(value.getClass()) ? ((Enum)value).name() : value;
    }

    private static Collection<?> asCollection(Object source) {
        if (source instanceof Collection) {
            return (Collection)source;
        }
        return source.getClass().isArray() ? CollectionUtils.arrayToList(source) : Collections.singleton(source);
    }

    class VaultPropertyValueProvider
    implements PropertyValueProvider<VaultPersistentProperty> {
        private final SecretDocumentAccessor source;

        VaultPropertyValueProvider(SecretDocument source) {
            Assert.notNull((Object)source, "Source document must no be null!");
            this.source = new SecretDocumentAccessor(source);
        }

        VaultPropertyValueProvider(SecretDocumentAccessor accessor) {
            Assert.notNull((Object)accessor, "SecretDocumentAccessor must no be null!");
            this.source = accessor;
        }

        @Nullable
        public <T> T getPropertyValue(VaultPersistentProperty property) {
            Object value = this.source.get(property);
            if (value == null) {
                return null;
            }
            return (T)MappingVaultConverter.this.readValue(value, property.getTypeInformation());
        }
    }
}

