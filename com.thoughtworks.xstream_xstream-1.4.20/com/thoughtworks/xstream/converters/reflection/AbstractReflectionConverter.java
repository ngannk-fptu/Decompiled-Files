/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SerializationMethodInvoker;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.ReferencingMarshallingContext;
import com.thoughtworks.xstream.core.util.ArrayIterator;
import com.thoughtworks.xstream.core.util.FastField;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.core.util.SerializationMembers;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.Mapper;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractReflectionConverter
implements Converter,
Caching {
    protected final ReflectionProvider reflectionProvider;
    protected final Mapper mapper;
    protected transient SerializationMethodInvoker serializationMethodInvoker;
    protected transient SerializationMembers serializationMembers;
    private transient ReflectionProvider pureJavaReflectionProvider;
    static /* synthetic */ Class class$com$thoughtworks$xstream$mapper$Mapper$Null;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$util$Map$Entry;

    public AbstractReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        this.mapper = mapper;
        this.reflectionProvider = reflectionProvider;
        this.serializationMethodInvoker = new SerializationMethodInvoker();
        this.serializationMembers = this.serializationMethodInvoker.serializationMembers;
    }

    protected boolean canAccess(Class type) {
        try {
            this.reflectionProvider.getFieldOrNull(type, "%");
            return true;
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
    }

    public void marshal(Object original, HierarchicalStreamWriter writer, MarshallingContext context) {
        Object source = this.serializationMembers.callWriteReplace(original);
        if (source != original && context instanceof ReferencingMarshallingContext) {
            ((ReferencingMarshallingContext)context).replace(original, source);
        }
        if (source.getClass() != original.getClass()) {
            String attributeName = this.mapper.aliasForSystemAttribute("resolves-to");
            if (attributeName != null) {
                writer.addAttribute(attributeName, this.mapper.serializedClass(source.getClass()));
            }
            context.convertAnother(source);
        } else {
            this.doMarshal(source, writer, context);
        }
    }

    protected void doMarshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final ArrayList fields = new ArrayList();
        final HashMap defaultFieldDefinition = new HashMap();
        final Class sourceType = source.getClass();
        this.reflectionProvider.visitSerializableFields(source, new ReflectionProvider.Visitor(){
            final Set writtenAttributes = new HashSet();

            public void visit(String fieldName, Class type, Class definedIn, Object value) {
                SingleValueConverter converter;
                if (!AbstractReflectionConverter.this.mapper.shouldSerializeMember(definedIn, fieldName)) {
                    return;
                }
                if (!defaultFieldDefinition.containsKey(fieldName)) {
                    Class lookupType = source.getClass();
                    if (definedIn != sourceType && !AbstractReflectionConverter.this.mapper.shouldSerializeMember(lookupType, fieldName)) {
                        lookupType = definedIn;
                    }
                    defaultFieldDefinition.put(fieldName, AbstractReflectionConverter.this.reflectionProvider.getField(lookupType, fieldName));
                }
                if ((converter = AbstractReflectionConverter.this.mapper.getConverterFromItemType(fieldName, type, definedIn)) != null) {
                    String attribute = AbstractReflectionConverter.this.mapper.aliasForAttribute(AbstractReflectionConverter.this.mapper.serializedMember(definedIn, fieldName));
                    if (value != null) {
                        if (this.writtenAttributes.contains(fieldName)) {
                            ConversionException exception = new ConversionException("Cannot write field as attribute for object, attribute name already in use");
                            exception.add("field-name", fieldName);
                            exception.add("object-type", sourceType.getName());
                            throw exception;
                        }
                        String str = converter.toString(value);
                        if (str != null) {
                            writer.addAttribute(attribute, str);
                        }
                    }
                    this.writtenAttributes.add(fieldName);
                } else {
                    fields.add(new FieldInfo(fieldName, type, definedIn, value));
                }
            }
        });
        FieldMarshaller fieldMarshaller = new FieldMarshaller(){

            public void writeField(String fieldName, String aliasName, Class fieldType, Class definedIn, Object newObj) {
                Class<?> actualType = newObj != null ? newObj.getClass() : fieldType;
                ExtendedHierarchicalStreamWriterHelper.startNode(writer, aliasName != null ? aliasName : AbstractReflectionConverter.this.mapper.serializedMember(sourceType, fieldName), actualType);
                if (newObj != null) {
                    Field defaultField;
                    String attributeName;
                    String serializedClassName;
                    Class defaultType = AbstractReflectionConverter.this.mapper.defaultImplementationOf(fieldType);
                    if (!actualType.equals(defaultType) && !(serializedClassName = AbstractReflectionConverter.this.mapper.serializedClass(actualType)).equals(AbstractReflectionConverter.this.mapper.serializedClass(defaultType)) && (attributeName = AbstractReflectionConverter.this.mapper.aliasForSystemAttribute("class")) != null) {
                        writer.addAttribute(attributeName, serializedClassName);
                    }
                    if ((defaultField = (Field)defaultFieldDefinition.get(fieldName)).getDeclaringClass() != definedIn && (attributeName = AbstractReflectionConverter.this.mapper.aliasForSystemAttribute("defined-in")) != null) {
                        writer.addAttribute(attributeName, AbstractReflectionConverter.this.mapper.serializedClass(definedIn));
                    }
                    Field field = AbstractReflectionConverter.this.reflectionProvider.getField(definedIn, fieldName);
                    AbstractReflectionConverter.this.marshallField(context, newObj, field);
                }
                writer.endNode();
            }

            public void writeItem(Object item) {
                if (item == null) {
                    String name = AbstractReflectionConverter.this.mapper.serializedClass(null);
                    ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, class$com$thoughtworks$xstream$mapper$Mapper$Null == null ? (class$com$thoughtworks$xstream$mapper$Mapper$Null = AbstractReflectionConverter.class$("com.thoughtworks.xstream.mapper.Mapper$Null")) : class$com$thoughtworks$xstream$mapper$Mapper$Null);
                    writer.endNode();
                } else {
                    String name = AbstractReflectionConverter.this.mapper.serializedClass(item.getClass());
                    ExtendedHierarchicalStreamWriterHelper.startNode(writer, name, item.getClass());
                    context.convertAnother(item);
                    writer.endNode();
                }
            }
        };
        HashMap<String, HashSet<Mapper.ImplicitCollectionMapping>> hiddenMappers = new HashMap<String, HashSet<Mapper.ImplicitCollectionMapping>>();
        Iterator fieldIter = fields.iterator();
        while (fieldIter.hasNext()) {
            FieldInfo info = (FieldInfo)fieldIter.next();
            if (info.value == null) continue;
            Field defaultField = (Field)defaultFieldDefinition.get(info.fieldName);
            Mapper.ImplicitCollectionMapping mapping = this.mapper.getImplicitCollectionDefForFieldName(defaultField.getDeclaringClass() == info.definedIn ? sourceType : info.definedIn, info.fieldName);
            if (mapping != null) {
                HashSet<Mapper.ImplicitCollectionMapping> mappings = (HashSet<Mapper.ImplicitCollectionMapping>)hiddenMappers.get(info.fieldName);
                if (mappings == null) {
                    mappings = new HashSet<Mapper.ImplicitCollectionMapping>();
                    mappings.add(mapping);
                    hiddenMappers.put(info.fieldName, mappings);
                } else if (!mappings.add(mapping)) {
                    mapping = null;
                }
            }
            if (mapping != null) {
                Iterator iter;
                if (context instanceof ReferencingMarshallingContext && info.value != Collections.EMPTY_LIST && info.value != Collections.EMPTY_SET && info.value != Collections.EMPTY_MAP) {
                    ReferencingMarshallingContext refContext = (ReferencingMarshallingContext)context;
                    refContext.registerImplicit(info.value);
                }
                boolean isCollection = info.value instanceof Collection;
                boolean isMap = info.value instanceof Map;
                boolean isEntry = isMap && mapping.getKeyFieldName() == null;
                boolean isArray = info.value.getClass().isArray();
                Iterator iterator = isArray ? new ArrayIterator(info.value) : (isCollection ? ((Collection)info.value).iterator() : (iter = isEntry ? ((Map)info.value).entrySet().iterator() : ((Map)info.value).values().iterator()));
                while (iter.hasNext()) {
                    String itemName;
                    Class<?> itemType;
                    Object obj = iter.next();
                    if (obj == null) {
                        itemType = class$java$lang$Object == null ? AbstractReflectionConverter.class$("java.lang.Object") : class$java$lang$Object;
                        itemName = this.mapper.serializedClass(null);
                    } else {
                        if (isEntry) {
                            String entryName = mapping.getItemFieldName() != null ? mapping.getItemFieldName() : this.mapper.serializedClass(class$java$util$Map$Entry == null ? AbstractReflectionConverter.class$("java.util.Map$Entry") : class$java$util$Map$Entry);
                            Map.Entry entry = (Map.Entry)obj;
                            ExtendedHierarchicalStreamWriterHelper.startNode(writer, entryName, entry.getClass());
                            fieldMarshaller.writeItem(entry.getKey());
                            fieldMarshaller.writeItem(entry.getValue());
                            writer.endNode();
                            continue;
                        }
                        if (mapping.getItemFieldName() != null) {
                            itemType = mapping.getItemType();
                            itemName = mapping.getItemFieldName();
                        } else {
                            itemType = obj.getClass();
                            itemName = this.mapper.serializedClass(itemType);
                        }
                    }
                    fieldMarshaller.writeField(info.fieldName, itemName, itemType, info.definedIn, obj);
                }
                continue;
            }
            fieldMarshaller.writeField(info.fieldName, null, info.type, info.definedIn, info.value);
        }
    }

    protected void marshallField(MarshallingContext context, Object newObj, Field field) {
        context.convertAnother(newObj, this.mapper.getLocalConverter(field.getDeclaringClass(), field.getName()));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Object result = this.instantiateNewInstance(reader, context);
        result = this.doUnmarshal(result, reader, context);
        return this.serializationMembers.callReadResolve(result);
    }

    public Object doUnmarshal(Object result, HierarchicalStreamReader reader, UnmarshallingContext context) {
        Map.Entry<Object, Object> value;
        Class resultType = result.getClass();
        HashSet seenFields = new HashSet(){

            public boolean add(Object e) {
                if (!super.add(e)) {
                    throw new DuplicateFieldException(((FastField)e).getName());
                }
                return true;
            }
        };
        Iterator it = reader.getAttributeNames();
        while (it.hasNext()) {
            Class<?> classDefiningField;
            String attrAlias = (String)it.next();
            String attrName = this.mapper.realMember(resultType, this.mapper.attributeForAlias(attrAlias));
            Field field = this.reflectionProvider.getFieldOrNull(resultType, attrName);
            if (field == null || !this.shouldUnmarshalField(field) || !this.mapper.shouldSerializeMember(classDefiningField = field.getDeclaringClass(), attrName)) continue;
            SingleValueConverter converter = this.mapper.getConverterFromAttribute(classDefiningField, attrName, field.getType());
            Class type = field.getType();
            if (converter == null) continue;
            value = converter.fromString(reader.getAttribute(attrAlias));
            if (type.isPrimitive()) {
                type = Primitives.box(type);
            }
            if (value != null && !type.isAssignableFrom(value.getClass())) {
                ConversionException exception = new ConversionException("Cannot convert type");
                exception.add("source-type", value.getClass().getName());
                exception.add("target-type", type.getName());
                throw exception;
            }
            seenFields.add(new FastField(classDefiningField, attrName));
            this.reflectionProvider.writeField(result, attrName, value, classDefiningField);
        }
        HashMap implicitCollectionsForCurrentObject = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String originalNodeName = reader.getNodeName();
            Class explicitDeclaringClass = this.readDeclaringClass(reader);
            Class fieldDeclaringClass = explicitDeclaringClass == null ? resultType : explicitDeclaringClass;
            String fieldName = this.mapper.realMember(fieldDeclaringClass, originalNodeName);
            Mapper.ImplicitCollectionMapping implicitCollectionMapping = this.mapper.getImplicitCollectionDefForFieldName(fieldDeclaringClass, fieldName);
            String implicitFieldName = null;
            Field field = null;
            Class type = null;
            if (implicitCollectionMapping == null) {
                String classAttribute2;
                field = this.reflectionProvider.getFieldOrNull(fieldDeclaringClass, fieldName);
                if (field == null) {
                    Class itemType = this.mapper.getItemTypeForItemFieldName(fieldDeclaringClass, fieldName);
                    if (itemType != null) {
                        classAttribute2 = HierarchicalStreams.readClassAttribute(reader, this.mapper);
                        type = classAttribute2 != null ? this.mapper.realClass(classAttribute2) : itemType;
                    } else {
                        try {
                            type = this.mapper.realClass(originalNodeName);
                            implicitFieldName = this.mapper.getFieldNameForItemTypeAndName(fieldDeclaringClass, type, originalNodeName);
                        }
                        catch (CannotResolveClassException classAttribute2) {
                            // empty catch block
                        }
                        if (type == null || type != null && implicitFieldName == null) {
                            this.handleUnknownField(explicitDeclaringClass, fieldName, fieldDeclaringClass, originalNodeName);
                            type = null;
                        }
                    }
                    if (type == null) {
                        value = null;
                    } else if ((class$java$util$Map$Entry == null ? AbstractReflectionConverter.class$("java.util.Map$Entry") : class$java$util$Map$Entry).equals(type)) {
                        reader.moveDown();
                        Object key = context.convertAnother(result, HierarchicalStreams.readClassType(reader, this.mapper));
                        reader.moveUp();
                        reader.moveDown();
                        Object v = context.convertAnother(result, HierarchicalStreams.readClassType(reader, this.mapper));
                        reader.moveUp();
                        value = Collections.singletonMap(key, v).entrySet().iterator().next();
                    } else {
                        value = context.convertAnother(result, type);
                    }
                } else {
                    boolean fieldAlreadyChecked = false;
                    if (explicitDeclaringClass == null) {
                        while (field != null && !(fieldAlreadyChecked = this.shouldUnmarshalField(field) && this.mapper.shouldSerializeMember(field.getDeclaringClass(), fieldName))) {
                            field = this.reflectionProvider.getFieldOrNull(field.getDeclaringClass().getSuperclass(), fieldName);
                        }
                    }
                    if (field != null && (fieldAlreadyChecked || this.shouldUnmarshalField(field) && this.mapper.shouldSerializeMember(field.getDeclaringClass(), fieldName))) {
                        classAttribute2 = HierarchicalStreams.readClassAttribute(reader, this.mapper);
                        type = classAttribute2 != null ? this.mapper.realClass(classAttribute2) : this.mapper.defaultImplementationOf(field.getType());
                        value = this.unmarshallField(context, result, type, field);
                        Class definedType = field.getType();
                        if (!definedType.isPrimitive()) {
                            type = definedType;
                        }
                    } else {
                        value = null;
                    }
                }
            } else {
                implicitFieldName = implicitCollectionMapping.getFieldName();
                type = implicitCollectionMapping.getItemType();
                if (type == null) {
                    String classAttribute = HierarchicalStreams.readClassAttribute(reader, this.mapper);
                    type = this.mapper.realClass(classAttribute != null ? classAttribute : originalNodeName);
                }
                value = context.convertAnother(result, type);
            }
            if (value != null && !type.isAssignableFrom(value.getClass())) {
                throw new ConversionException("Cannot convert type " + value.getClass().getName() + " to type " + type.getName());
            }
            if (field != null) {
                this.reflectionProvider.writeField(result, fieldName, value, field.getDeclaringClass());
                seenFields.add(new FastField(field.getDeclaringClass(), fieldName));
            } else if (type != null) {
                if (implicitFieldName == null) {
                    implicitFieldName = this.mapper.getFieldNameForItemTypeAndName(fieldDeclaringClass, value != null ? value.getClass() : (class$com$thoughtworks$xstream$mapper$Mapper$Null == null ? AbstractReflectionConverter.class$("com.thoughtworks.xstream.mapper.Mapper$Null") : class$com$thoughtworks$xstream$mapper$Mapper$Null), originalNodeName);
                }
                if (implicitCollectionsForCurrentObject == null) {
                    implicitCollectionsForCurrentObject = new HashMap();
                }
                this.writeValueToImplicitCollection(value, implicitCollectionsForCurrentObject, result, new FieldLocation(implicitFieldName, fieldDeclaringClass));
            }
            reader.moveUp();
        }
        if (implicitCollectionsForCurrentObject != null) {
            Iterator iter = implicitCollectionsForCurrentObject.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = iter.next();
                Object value2 = entry.getValue();
                if (!(value2 instanceof ArraysList)) continue;
                Object array = ((ArraysList)value2).toPhysicalArray();
                FieldLocation fieldLocation = (FieldLocation)entry.getKey();
                Field field = this.reflectionProvider.getFieldOrNull(fieldLocation.definedIn, fieldLocation.fieldName);
                this.reflectionProvider.writeField(result, fieldLocation.fieldName, array, field != null ? field.getDeclaringClass() : null);
            }
        }
        return result;
    }

    protected Object unmarshallField(UnmarshallingContext context, Object result, Class type, Field field) {
        return context.convertAnother(result, type, this.mapper.getLocalConverter(field.getDeclaringClass(), field.getName()));
    }

    protected boolean shouldUnmarshalTransientFields() {
        return false;
    }

    protected boolean shouldUnmarshalField(Field field) {
        return !Modifier.isTransient(field.getModifiers()) || this.shouldUnmarshalTransientFields();
    }

    private void handleUnknownField(Class classDefiningField, String fieldName, Class resultType, String originalNodeName) {
        if (classDefiningField == null) {
            for (Class cls = resultType; cls != null; cls = cls.getSuperclass()) {
                if (this.mapper.shouldSerializeMember(cls, originalNodeName)) continue;
                return;
            }
        }
        throw new UnknownFieldException(resultType.getName(), fieldName);
    }

    private void writeValueToImplicitCollection(Object value, Map implicitCollections, Object result, FieldLocation fieldLocation) {
        Collection collection = (Collection)implicitCollections.get(fieldLocation);
        if (collection == null) {
            Class physicalFieldType;
            Field field = this.reflectionProvider.getFieldOrNull(fieldLocation.definedIn, fieldLocation.fieldName);
            Class clazz = physicalFieldType = field != null ? field.getType() : this.reflectionProvider.getFieldType(result, fieldLocation.fieldName, null);
            if (physicalFieldType.isArray()) {
                collection = new ArraysList(physicalFieldType);
            } else {
                Object instance;
                Class fieldType;
                if (!Collection.class.isAssignableFrom(fieldType = this.mapper.defaultImplementationOf(physicalFieldType)) && !Map.class.isAssignableFrom(fieldType)) {
                    ObjectAccessException oaex = new ObjectAccessException("Field is configured for an implicit Collection or Map, but is of an incompatible type");
                    oaex.add("field", result.getClass().getName() + "." + fieldLocation.fieldName);
                    oaex.add("field-type", fieldType.getName());
                    throw oaex;
                }
                if (this.pureJavaReflectionProvider == null) {
                    this.pureJavaReflectionProvider = new PureJavaReflectionProvider();
                }
                if ((instance = this.pureJavaReflectionProvider.newInstance(fieldType)) instanceof Collection) {
                    collection = (Collection)instance;
                } else {
                    Mapper.ImplicitCollectionMapping implicitCollectionMapping = this.mapper.getImplicitCollectionDefForFieldName(fieldLocation.definedIn, fieldLocation.fieldName);
                    collection = new MappingList((Map)instance, implicitCollectionMapping.getKeyFieldName());
                }
                this.reflectionProvider.writeField(result, fieldLocation.fieldName, instance, field != null ? field.getDeclaringClass() : null);
            }
            implicitCollections.put(fieldLocation, collection);
        }
        collection.add(value);
    }

    private Class readDeclaringClass(HierarchicalStreamReader reader) {
        String attributeName = this.mapper.aliasForSystemAttribute("defined-in");
        String definedIn = attributeName == null ? null : reader.getAttribute(attributeName);
        return definedIn == null ? null : this.mapper.realClass(definedIn);
    }

    protected Object instantiateNewInstance(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String attributeName = this.mapper.aliasForSystemAttribute("resolves-to");
        String readResolveValue = attributeName == null ? null : reader.getAttribute(attributeName);
        Object currentObject = context.currentObject();
        if (currentObject != null) {
            return currentObject;
        }
        if (readResolveValue != null) {
            return this.reflectionProvider.newInstance(this.mapper.realClass(readResolveValue));
        }
        return this.reflectionProvider.newInstance(context.getRequiredType());
    }

    public void flushCache() {
        this.serializationMethodInvoker.flushCache();
    }

    protected Object readResolve() {
        this.serializationMethodInvoker = new SerializationMethodInvoker();
        this.serializationMembers = this.serializationMethodInvoker.serializationMembers;
        return this;
    }

    private class MappingList
    extends AbstractList {
        private final Map map;
        private final String keyFieldName;
        private final Map fieldCache = new HashMap();

        public MappingList(Map map, String keyFieldName) {
            this.map = map;
            this.keyFieldName = keyFieldName;
        }

        public boolean add(Object object) {
            if (object == null) {
                boolean containsNull = !this.map.containsKey(null);
                this.map.put(null, null);
                return containsNull;
            }
            Class<?> itemType = object.getClass();
            if (this.keyFieldName != null) {
                Field field = (Field)this.fieldCache.get(itemType);
                if (field == null) {
                    field = AbstractReflectionConverter.this.reflectionProvider.getField(itemType, this.keyFieldName);
                    this.fieldCache.put(itemType, field);
                }
                if (field != null) {
                    Object key = Fields.read(field, object);
                    return this.map.put(key, object) == null;
                }
            } else if (object instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry)object;
                return this.map.put(entry.getKey(), entry.getValue()) == null;
            }
            ConversionException exception = new ConversionException("Element  is not defined as entry for implicit map");
            exception.add("map-type", this.map.getClass().getName());
            exception.add("element-type", object.getClass().getName());
            throw exception;
        }

        public Object get(int index) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return this.map.size();
        }
    }

    private static class ArraysList
    extends ArrayList {
        final Class physicalFieldType;

        ArraysList(Class physicalFieldType) {
            this.physicalFieldType = physicalFieldType;
        }

        Object toPhysicalArray() {
            Object[] objects = this.toArray();
            Object array = Array.newInstance(this.physicalFieldType.getComponentType(), objects.length);
            if (this.physicalFieldType.getComponentType().isPrimitive()) {
                for (int i = 0; i < objects.length; ++i) {
                    Array.set(array, i, Array.get(objects, i));
                }
            } else {
                System.arraycopy(objects, 0, array, 0, objects.length);
            }
            return array;
        }
    }

    private static interface FieldMarshaller {
        public void writeItem(Object var1);

        public void writeField(String var1, String var2, Class var3, Class var4, Object var5);
    }

    private static class FieldInfo
    extends FieldLocation {
        final Class type;
        final Object value;

        FieldInfo(String fieldName, Class type, Class definedIn, Object value) {
            super(fieldName, definedIn);
            this.type = type;
            this.value = value;
        }
    }

    private static class FieldLocation {
        final String fieldName;
        final Class definedIn;

        FieldLocation(String fieldName, Class definedIn) {
            this.fieldName = fieldName;
            this.definedIn = definedIn;
        }

        public int hashCode() {
            int prime = 7;
            int result = 1;
            result = 7 * result + (this.definedIn == null ? 0 : this.definedIn.getName().hashCode());
            result = 7 * result + (this.fieldName == null ? 0 : this.fieldName.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            FieldLocation other = (FieldLocation)obj;
            if (this.definedIn != other.definedIn) {
                return false;
            }
            return !(this.fieldName == null ? other.fieldName != null : !this.fieldName.equals(other.fieldName));
        }
    }

    public static class UnknownFieldException
    extends ConversionException {
        public UnknownFieldException(String type, String field) {
            super("No such field " + type + "." + field);
            this.add("field", field);
        }
    }

    public static class DuplicateFieldException
    extends ConversionException {
        public DuplicateFieldException(String msg) {
            super("Duplicate field " + msg);
            this.add("field", msg);
        }
    }
}

