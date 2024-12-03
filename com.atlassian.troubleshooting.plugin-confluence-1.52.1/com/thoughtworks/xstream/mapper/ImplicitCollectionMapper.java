/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImplicitCollectionMapper
extends MapperWrapper {
    private ReflectionProvider reflectionProvider;
    private final Map classNameToMapper = new HashMap();
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$com$thoughtworks$xstream$mapper$Mapper$Null;

    public ImplicitCollectionMapper(Mapper wrapped, ReflectionProvider reflectionProvider) {
        super(wrapped);
        this.reflectionProvider = reflectionProvider;
    }

    private ImplicitCollectionMapperForClass getMapper(Class declaredFor, String fieldName) {
        Class<?> inheritanceStop;
        Class definedIn;
        Field field = fieldName != null ? this.reflectionProvider.getFieldOrNull(definedIn, fieldName) : null;
        Class<?> clazz = inheritanceStop = field != null ? field.getDeclaringClass() : null;
        for (definedIn = declaredFor; definedIn != null; definedIn = definedIn.getSuperclass()) {
            ImplicitCollectionMapperForClass mapper = (ImplicitCollectionMapperForClass)this.classNameToMapper.get(definedIn);
            if (mapper != null) {
                return mapper;
            }
            if (definedIn == inheritanceStop) break;
        }
        return null;
    }

    private ImplicitCollectionMapperForClass getOrCreateMapper(Class definedIn) {
        ImplicitCollectionMapperForClass mapper = (ImplicitCollectionMapperForClass)this.classNameToMapper.get(definedIn);
        if (mapper == null) {
            mapper = new ImplicitCollectionMapperForClass(definedIn);
            this.classNameToMapper.put(definedIn, mapper);
        }
        return mapper;
    }

    public String getFieldNameForItemTypeAndName(Class definedIn, Class itemType, String itemFieldName) {
        ImplicitCollectionMapperForClass mapper = this.getMapper(definedIn, null);
        if (mapper != null) {
            return mapper.getFieldNameForItemTypeAndName(itemType, itemFieldName);
        }
        return null;
    }

    public Class getItemTypeForItemFieldName(Class definedIn, String itemFieldName) {
        ImplicitCollectionMapperForClass mapper = this.getMapper(definedIn, null);
        if (mapper != null) {
            return mapper.getItemTypeForItemFieldName(itemFieldName);
        }
        return null;
    }

    public Mapper.ImplicitCollectionMapping getImplicitCollectionDefForFieldName(Class itemType, String fieldName) {
        ImplicitCollectionMapperForClass mapper = this.getMapper(itemType, fieldName);
        if (mapper != null) {
            return mapper.getImplicitCollectionDefForFieldName(fieldName);
        }
        return null;
    }

    public void add(Class definedIn, String fieldName, Class itemType) {
        this.add(definedIn, fieldName, null, itemType);
    }

    public void add(Class definedIn, String fieldName, String itemFieldName, Class itemType) {
        this.add(definedIn, fieldName, itemFieldName, itemType, null);
    }

    public void add(Class definedIn, String fieldName, String itemFieldName, Class itemType, String keyFieldName) {
        Field field = null;
        if (definedIn != null) {
            Class declaredIn = definedIn;
            while (declaredIn != (class$java$lang$Object == null ? ImplicitCollectionMapper.class$("java.lang.Object") : class$java$lang$Object)) {
                try {
                    field = declaredIn.getDeclaredField(fieldName);
                    if (!Modifier.isStatic(field.getModifiers())) break;
                    field = null;
                }
                catch (SecurityException e) {
                    throw new InitializationException("Access denied for field with implicit collection", e);
                }
                catch (NoSuchFieldException e) {
                    declaredIn = declaredIn.getSuperclass();
                }
            }
        }
        if (field == null) {
            throw new InitializationException("No field \"" + fieldName + "\" for implicit collection");
        }
        if (Map.class.isAssignableFrom(field.getType())) {
            if (itemFieldName == null && keyFieldName == null) {
                itemType = Map.Entry.class;
            }
        } else if (!Collection.class.isAssignableFrom(field.getType())) {
            Class<?> fieldType = field.getType();
            if (!fieldType.isArray()) {
                throw new InitializationException("Field \"" + fieldName + "\" declares no collection or array");
            }
            Class componentType = fieldType.getComponentType();
            Class clazz = componentType = componentType.isPrimitive() ? Primitives.box(componentType) : componentType;
            if (itemType == null) {
                itemType = componentType;
            } else {
                Class clazz2 = itemType = itemType.isPrimitive() ? Primitives.box(itemType) : itemType;
                if (!componentType.isAssignableFrom(itemType)) {
                    throw new InitializationException("Field \"" + fieldName + "\" declares an array, but the array type is not compatible with " + itemType.getName());
                }
            }
        }
        ImplicitCollectionMapperForClass mapper = this.getOrCreateMapper(definedIn);
        mapper.add(new ImplicitCollectionMappingImpl(fieldName, itemType, itemFieldName, keyFieldName));
    }

    private static class NamedItemType {
        Class itemType;
        String itemFieldName;

        NamedItemType(Class itemType, String itemFieldName) {
            Class clazz = itemType == null ? (class$java$lang$Object == null ? (class$java$lang$Object = ImplicitCollectionMapper.class$("java.lang.Object")) : class$java$lang$Object) : itemType;
            this.itemType = clazz;
            this.itemFieldName = itemFieldName;
        }

        public boolean equals(Object obj) {
            if (obj instanceof NamedItemType) {
                NamedItemType b = (NamedItemType)obj;
                return this.itemType.equals(b.itemType) && NamedItemType.isEquals(this.itemFieldName, b.itemFieldName);
            }
            return false;
        }

        private static boolean isEquals(Object a, Object b) {
            if (a == null) {
                return b == null;
            }
            return a.equals(b);
        }

        public int hashCode() {
            int hash = this.itemType.hashCode() << 7;
            if (this.itemFieldName != null) {
                hash += this.itemFieldName.hashCode();
            }
            return hash;
        }
    }

    private static class ImplicitCollectionMappingImpl
    implements Mapper.ImplicitCollectionMapping {
        private final String fieldName;
        private final String itemFieldName;
        private final Class itemType;
        private final String keyFieldName;

        ImplicitCollectionMappingImpl(String fieldName, Class itemType, String itemFieldName, String keyFieldName) {
            this.fieldName = fieldName;
            this.itemFieldName = itemFieldName;
            this.itemType = itemType;
            this.keyFieldName = keyFieldName;
        }

        public NamedItemType createNamedItemType() {
            return new NamedItemType(this.itemType, this.itemFieldName);
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public String getItemFieldName() {
            return this.itemFieldName;
        }

        public Class getItemType() {
            return this.itemType;
        }

        public String getKeyFieldName() {
            return this.keyFieldName;
        }
    }

    private class ImplicitCollectionMapperForClass {
        private Class definedIn;
        private Map namedItemTypeToDef = new HashMap();
        private Map itemFieldNameToDef = new HashMap();
        private Map fieldNameToDef = new HashMap();

        ImplicitCollectionMapperForClass(Class definedIn) {
            this.definedIn = definedIn;
        }

        public String getFieldNameForItemTypeAndName(Class itemType, String itemFieldName) {
            ImplicitCollectionMappingImpl unnamed = null;
            Iterator iterator = this.namedItemTypeToDef.keySet().iterator();
            while (iterator.hasNext()) {
                NamedItemType itemTypeForFieldName = (NamedItemType)iterator.next();
                ImplicitCollectionMappingImpl def = (ImplicitCollectionMappingImpl)this.namedItemTypeToDef.get(itemTypeForFieldName);
                if (itemType == (class$com$thoughtworks$xstream$mapper$Mapper$Null == null ? ImplicitCollectionMapper.class$("com.thoughtworks.xstream.mapper.Mapper$Null") : class$com$thoughtworks$xstream$mapper$Mapper$Null)) {
                    unnamed = def;
                    break;
                }
                if (!itemTypeForFieldName.itemType.isAssignableFrom(itemType)) continue;
                if (def.getItemFieldName() != null) {
                    if (!def.getItemFieldName().equals(itemFieldName)) continue;
                    return def.getFieldName();
                }
                if (unnamed != null && unnamed.getItemType() != null && (def.getItemType() == null || !unnamed.getItemType().isAssignableFrom(def.getItemType()))) continue;
                unnamed = def;
            }
            if (unnamed != null) {
                return unnamed.getFieldName();
            }
            ImplicitCollectionMapperForClass mapper = ImplicitCollectionMapper.this.getMapper(this.definedIn.getSuperclass(), null);
            return mapper != null ? mapper.getFieldNameForItemTypeAndName(itemType, itemFieldName) : null;
        }

        public Class getItemTypeForItemFieldName(String itemFieldName) {
            ImplicitCollectionMappingImpl def = this.getImplicitCollectionDefByItemFieldName(itemFieldName);
            if (def != null) {
                return def.getItemType();
            }
            ImplicitCollectionMapperForClass mapper = ImplicitCollectionMapper.this.getMapper(this.definedIn.getSuperclass(), null);
            return mapper != null ? mapper.getItemTypeForItemFieldName(itemFieldName) : null;
        }

        private ImplicitCollectionMappingImpl getImplicitCollectionDefByItemFieldName(String itemFieldName) {
            if (itemFieldName == null) {
                return null;
            }
            ImplicitCollectionMappingImpl mapping = (ImplicitCollectionMappingImpl)this.itemFieldNameToDef.get(itemFieldName);
            if (mapping != null) {
                return mapping;
            }
            ImplicitCollectionMapperForClass mapper = ImplicitCollectionMapper.this.getMapper(this.definedIn.getSuperclass(), null);
            return mapper != null ? mapper.getImplicitCollectionDefByItemFieldName(itemFieldName) : null;
        }

        public Mapper.ImplicitCollectionMapping getImplicitCollectionDefForFieldName(String fieldName) {
            Mapper.ImplicitCollectionMapping mapping = (Mapper.ImplicitCollectionMapping)this.fieldNameToDef.get(fieldName);
            if (mapping != null) {
                return mapping;
            }
            ImplicitCollectionMapperForClass mapper = ImplicitCollectionMapper.this.getMapper(this.definedIn.getSuperclass(), null);
            return mapper != null ? mapper.getImplicitCollectionDefForFieldName(fieldName) : null;
        }

        public void add(ImplicitCollectionMappingImpl def) {
            this.fieldNameToDef.put(def.getFieldName(), def);
            this.namedItemTypeToDef.put(def.createNamedItemType(), def);
            if (def.getItemFieldName() != null) {
                this.itemFieldNameToDef.put(def.getItemFieldName(), def);
            }
        }
    }
}

