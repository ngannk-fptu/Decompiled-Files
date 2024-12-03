/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Conversion;
import org.apache.avro.JsonProperties;
import org.apache.avro.LogicalType;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.BinaryData;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.AvroAlias;
import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.AvroDoc;
import org.apache.avro.reflect.AvroEncode;
import org.apache.avro.reflect.AvroIgnore;
import org.apache.avro.reflect.AvroMeta;
import org.apache.avro.reflect.AvroName;
import org.apache.avro.reflect.AvroSchema;
import org.apache.avro.reflect.FieldAccessor;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.reflect.ReflectionUtil;
import org.apache.avro.reflect.Stringable;
import org.apache.avro.reflect.Union;
import org.apache.avro.specific.FixedSize;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.ClassUtils;
import org.apache.avro.util.MapUtil;

public class ReflectData
extends SpecificData {
    private static final String STRING_OUTER_PARENT_REFERENCE = "this$0";
    private static final ReflectData INSTANCE = new ReflectData();
    private boolean defaultGenerated = false;
    private final Map<Type, Object> defaultValues = new WeakHashMap<Type, Object>();
    static final ClassValue<ClassAccessorData> ACCESSOR_CACHE = new ClassValue<ClassAccessorData>(){

        @Override
        protected ClassAccessorData computeValue(Class<?> c) {
            if (!IndexedRecord.class.isAssignableFrom(c)) {
                return new ClassAccessorData(c);
            }
            return null;
        }
    };
    @Deprecated
    static final String CLASS_PROP = "java-class";
    @Deprecated
    static final String KEY_CLASS_PROP = "java-key-class";
    @Deprecated
    static final String ELEMENT_PROP = "java-element-class";
    private static final Map<String, Class> CLASS_CACHE = new ConcurrentHashMap<String, Class>();
    private static final Class BYTES_CLASS = byte[].class;
    private static final IdentityHashMap<Class, Class> ARRAY_CLASSES = new IdentityHashMap();
    static final String NS_MAP_ARRAY_RECORD = "org.apache.avro.reflect.Pair";
    static final String NS_MAP_KEY = "key";
    static final int NS_MAP_KEY_INDEX = 0;
    static final String NS_MAP_VALUE = "value";
    static final int NS_MAP_VALUE_INDEX = 1;
    private static final Schema THROWABLE_MESSAGE;
    private static final ConcurrentMap<Class<?>, Field[]> FIELDS_CACHE;

    @Override
    public boolean useCustomCoders() {
        return false;
    }

    public ReflectData() {
    }

    public ReflectData(ClassLoader classLoader) {
        super(classLoader);
    }

    public static ReflectData get() {
        return INSTANCE;
    }

    public ReflectData addStringable(Class c) {
        this.stringableClasses.add(c);
        return this;
    }

    public ReflectData setDefaultsGenerated(boolean enabled) {
        this.defaultGenerated = enabled;
        return this;
    }

    public ReflectData setDefaultGeneratedValue(Type type, Object value) {
        this.defaultValues.put(type, value);
        this.setDefaultsGenerated(true);
        return this;
    }

    protected Object getOrCreateDefaultValue(Type type, Field field) {
        Object defaultValue = null;
        field.setAccessible(true);
        try {
            Object typeValue = this.getOrCreateDefaultValue(type);
            if (typeValue != null) {
                defaultValue = field.get(typeValue);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return defaultValue;
    }

    protected Object getOrCreateDefaultValue(Type type) {
        return this.defaultValues.computeIfAbsent(type, ignored -> {
            try {
                Constructor constructor = ((Class)type).getDeclaredConstructor(new Class[0]);
                constructor.setAccessible(true);
                return constructor.newInstance(new Object[0]);
            }
            catch (ClassCastException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exception) {
                return null;
            }
        });
    }

    @Override
    public DatumReader createDatumReader(Schema schema) {
        return new ReflectDatumReader(schema, schema, this);
    }

    @Override
    public DatumReader createDatumReader(Schema writer, Schema reader) {
        return new ReflectDatumReader(writer, reader, this);
    }

    @Override
    public DatumWriter createDatumWriter(Schema schema) {
        return new ReflectDatumWriter(schema, this);
    }

    @Override
    public void setField(Object record, String name, int position, Object value) {
        this.setField(record, name, position, value, null);
    }

    @Override
    protected void setField(Object record, String name, int position, Object value, Object state) {
        if (record instanceof IndexedRecord) {
            super.setField(record, name, position, value);
            return;
        }
        try {
            this.getAccessorForField(record, name, position, state).set(record, value);
        }
        catch (IOException | IllegalAccessException e) {
            throw new AvroRuntimeException(e);
        }
    }

    @Override
    public Object getField(Object record, String name, int position) {
        return this.getField(record, name, position, null);
    }

    @Override
    protected Object getField(Object record, String name, int pos, Object state) {
        if (record instanceof IndexedRecord) {
            return super.getField(record, name, pos);
        }
        try {
            return this.getAccessorForField(record, name, pos, state).get(record);
        }
        catch (IllegalAccessException e) {
            throw new AvroRuntimeException(e);
        }
    }

    private FieldAccessor getAccessorForField(Object record, String name, int pos, Object optionalState) {
        if (optionalState != null) {
            return ((FieldAccessor[])optionalState)[pos];
        }
        return this.getFieldAccessor(record.getClass(), name);
    }

    @Override
    protected boolean isRecord(Object datum) {
        if (datum == null) {
            return false;
        }
        if (super.isRecord(datum)) {
            return true;
        }
        if (datum instanceof Collection) {
            return false;
        }
        if (datum instanceof Map) {
            return false;
        }
        if (datum instanceof GenericFixed) {
            return false;
        }
        return this.getSchema(datum.getClass()).getType() == Schema.Type.RECORD;
    }

    @Override
    protected boolean isArray(Object datum) {
        if (datum == null) {
            return false;
        }
        Class<?> c = datum.getClass();
        return datum instanceof Collection || c.isArray() && c.getComponentType() != Byte.TYPE || this.isNonStringMap(datum);
    }

    @Override
    protected Collection getArrayAsCollection(Object datum) {
        return datum instanceof Map ? ((Map)datum).entrySet() : (Set)datum;
    }

    @Override
    protected boolean isBytes(Object datum) {
        if (datum == null) {
            return false;
        }
        if (super.isBytes(datum)) {
            return true;
        }
        Class<?> c = datum.getClass();
        return c.isArray() && c.getComponentType() == Byte.TYPE;
    }

    @Override
    protected Schema getRecordSchema(Object record) {
        if (record instanceof GenericContainer) {
            return super.getRecordSchema(record);
        }
        return this.getSchema(record.getClass());
    }

    @Override
    public boolean validate(Schema schema, Object datum) {
        switch (schema.getType()) {
            case ARRAY: {
                if (!datum.getClass().isArray()) {
                    return super.validate(schema, datum);
                }
                int length = Array.getLength(datum);
                for (int i = 0; i < length; ++i) {
                    if (this.validate(schema.getElementType(), Array.get(datum, i))) continue;
                    return false;
                }
                return true;
            }
        }
        return super.validate(schema, datum);
    }

    private ClassAccessorData getClassAccessorData(Class<?> c) {
        return ACCESSOR_CACHE.get(c);
    }

    private FieldAccessor[] getFieldAccessors(Class<?> c, Schema s) {
        ClassAccessorData data = this.getClassAccessorData(c);
        if (data != null) {
            return data.getAccessorsFor(s);
        }
        return null;
    }

    private FieldAccessor getFieldAccessor(Class<?> c, String fieldName) {
        ClassAccessorData data = this.getClassAccessorData(c);
        if (data != null) {
            return data.getAccessorFor(fieldName);
        }
        return null;
    }

    static Class getClassProp(Schema schema, String prop) {
        String name = schema.getProp(prop);
        if (name == null) {
            return null;
        }
        Class<?> c = CLASS_CACHE.get(name);
        if (c != null) {
            return c;
        }
        try {
            c = ClassUtils.forName(name);
            CLASS_CACHE.put(name, c);
        }
        catch (ClassNotFoundException e) {
            throw new AvroRuntimeException(e);
        }
        return c;
    }

    @Override
    protected boolean isMap(Object datum) {
        return datum instanceof Map && !this.isNonStringMap(datum);
    }

    private boolean isNonStringMap(Object datum) {
        Map m;
        if (datum instanceof Map && (m = (Map)datum).size() > 0) {
            Class<?> keyClass = m.keySet().iterator().next().getClass();
            return !this.isStringable(keyClass) && !this.isStringType(keyClass);
        }
        return false;
    }

    @Override
    public Class getClass(Schema schema) {
        Conversion conversion = this.getConversionFor(schema.getLogicalType());
        if (conversion != null) {
            return conversion.getConvertedType();
        }
        switch (schema.getType()) {
            case ARRAY: {
                Class collectionClass = ReflectData.getClassProp(schema, CLASS_PROP);
                if (collectionClass != null) {
                    return collectionClass;
                }
                Class elementClass = this.getClass(schema.getElementType());
                if (elementClass.isPrimitive()) {
                    return ARRAY_CLASSES.get(elementClass);
                }
                return Array.newInstance(elementClass, 0).getClass();
            }
            case STRING: {
                Class stringClass = ReflectData.getClassProp(schema, CLASS_PROP);
                if (stringClass != null) {
                    return stringClass;
                }
                return String.class;
            }
            case BYTES: {
                return BYTES_CLASS;
            }
            case INT: {
                String intClass = schema.getProp(CLASS_PROP);
                if (Byte.class.getName().equals(intClass)) {
                    return Byte.TYPE;
                }
                if (Short.class.getName().equals(intClass)) {
                    return Short.TYPE;
                }
                if (!Character.class.getName().equals(intClass)) break;
                return Character.TYPE;
            }
        }
        return super.getClass(schema);
    }

    Schema createNonStringMapSchema(Type keyType, Type valueType, Map<String, Schema> names) {
        Schema keySchema = this.createSchema(keyType, names);
        Schema valueSchema = this.createSchema(valueType, names);
        Schema.Field keyField = new Schema.Field(NS_MAP_KEY, keySchema, null, null);
        Schema.Field valueField = new Schema.Field(NS_MAP_VALUE, valueSchema, null, null);
        String name = this.getNameForNonStringMapRecord(keyType, valueType, keySchema, valueSchema);
        Schema elementSchema = Schema.createRecord(name, null, null, false);
        elementSchema.setFields(Arrays.asList(keyField, valueField));
        Schema arraySchema = Schema.createArray(elementSchema);
        return arraySchema;
    }

    private String getNameForNonStringMapRecord(Type keyType, Type valueType, Schema keySchema, Schema valueSchema) {
        String name;
        long fingerprint;
        if (keyType instanceof Class && valueType instanceof Class) {
            Class keyClass = (Class)keyType;
            Class valueClass = (Class)valueType;
            Package pkg1 = keyClass.getPackage();
            Package pkg2 = valueClass.getPackage();
            if (pkg1 != null && pkg1.getName().startsWith("java") && pkg2 != null && pkg2.getName().startsWith("java")) {
                return NS_MAP_ARRAY_RECORD + this.simpleName(keyClass) + this.simpleName(valueClass);
            }
        }
        if ((fingerprint = SchemaNormalization.fingerprint64((name = keySchema.getFullName() + valueSchema.getFullName()).getBytes(StandardCharsets.UTF_8))) < 0L) {
            fingerprint = -fingerprint;
        }
        String fpString = Long.toString(fingerprint, 16);
        return NS_MAP_ARRAY_RECORD + fpString;
    }

    static boolean isNonStringMapSchema(Schema s) {
        if (s != null && s.getType() == Schema.Type.ARRAY) {
            Class c = ReflectData.getClassProp(s, CLASS_PROP);
            return c != null && Map.class.isAssignableFrom(c);
        }
        return false;
    }

    protected Object createSchemaDefaultValue(Type type, Field field, Schema fieldSchema) {
        Schema defaultType;
        Object defaultValue;
        if (this.defaultGenerated && (defaultValue = this.getOrCreateDefaultValue(type, field)) != null) {
            return this.deepCopy(fieldSchema, defaultValue);
        }
        AvroDefault defaultAnnotation = field.getAnnotation(AvroDefault.class);
        Object object = defaultValue = defaultAnnotation == null ? null : Schema.parseJsonToObject(defaultAnnotation.value());
        if (defaultValue == null && fieldSchema.getType() == Schema.Type.UNION && (defaultType = fieldSchema.getTypes().get(0)).getType() == Schema.Type.NULL) {
            defaultValue = JsonProperties.NULL_VALUE;
        }
        return defaultValue;
    }

    @Override
    protected Schema createSchema(Type type, Map<String, Schema> names) {
        if (type instanceof GenericArrayType) {
            Type component = ((GenericArrayType)type).getGenericComponentType();
            if (component == Byte.TYPE) {
                return Schema.create(Schema.Type.BYTES);
            }
            Schema result = Schema.createArray(this.createSchema(component, names));
            this.setElement(result, component);
            return result;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            Class raw = (Class)ptype.getRawType();
            Type[] params = ptype.getActualTypeArguments();
            if (Map.class.isAssignableFrom(raw)) {
                Class key = (Class)params[0];
                if (this.isStringable(key)) {
                    Schema schema = Schema.createMap(this.createSchema(params[1], names));
                    schema.addProp(KEY_CLASS_PROP, key.getName());
                    return schema;
                }
                if (key != String.class) {
                    Schema schema = this.createNonStringMapSchema(params[0], params[1], names);
                    schema.addProp(CLASS_PROP, raw.getName());
                    return schema;
                }
            } else if (Collection.class.isAssignableFrom(raw)) {
                if (params.length != 1) {
                    throw new AvroTypeException("No array type specified.");
                }
                Schema schema = Schema.createArray(this.createSchema(params[0], names));
                schema.addProp(CLASS_PROP, raw.getName());
                return schema;
            }
        } else {
            if (type == Byte.class || type == Byte.TYPE) {
                Schema result = Schema.create(Schema.Type.INT);
                result.addProp(CLASS_PROP, Byte.class.getName());
                return result;
            }
            if (type == Short.class || type == Short.TYPE) {
                Schema result = Schema.create(Schema.Type.INT);
                result.addProp(CLASS_PROP, Short.class.getName());
                return result;
            }
            if (type == Character.class || type == Character.TYPE) {
                Schema result = Schema.create(Schema.Type.INT);
                result.addProp(CLASS_PROP, Character.class.getName());
                return result;
            }
            if (type instanceof Class) {
                Class c = (Class)type;
                while (c.isAnonymousClass()) {
                    c = c.getSuperclass();
                }
                if (c.isPrimitive() || c == Void.class || c == Boolean.class || c == Integer.class || c == Long.class || c == Float.class || c == Double.class || c == Byte.class || c == Short.class || c == Character.class) {
                    return super.createSchema(type, names);
                }
                if (c.isArray()) {
                    Class<?> component = c.getComponentType();
                    if (component == Byte.TYPE) {
                        Schema result = Schema.create(Schema.Type.BYTES);
                        result.addProp(CLASS_PROP, c.getName());
                        return result;
                    }
                    Schema result = Schema.createArray(this.createSchema(component, names));
                    result.addProp(CLASS_PROP, c.getName());
                    this.setElement(result, component);
                    return result;
                }
                AvroSchema explicit = c.getAnnotation(AvroSchema.class);
                if (explicit != null) {
                    return new Schema.Parser().parse(explicit.value());
                }
                if (CharSequence.class.isAssignableFrom(c)) {
                    return Schema.create(Schema.Type.STRING);
                }
                if (ByteBuffer.class.isAssignableFrom(c)) {
                    return Schema.create(Schema.Type.BYTES);
                }
                if (Collection.class.isAssignableFrom(c)) {
                    throw new AvroRuntimeException("Can't find element type of Collection");
                }
                Conversion conversion = this.getConversionByClass(c);
                if (conversion != null) {
                    return conversion.getRecommendedSchema();
                }
                String fullName = c.getName();
                Schema schema = names.get(fullName);
                if (schema == null) {
                    Union union;
                    String space;
                    AvroDoc annotatedDoc = c.getAnnotation(AvroDoc.class);
                    String doc = annotatedDoc != null ? annotatedDoc.value() : null;
                    String name = c.getSimpleName();
                    String string = space = c.getPackage() == null ? "" : c.getPackage().getName();
                    if (c.getEnclosingClass() != null) {
                        space = c.getEnclosingClass().getName().replace('$', '.');
                    }
                    if ((union = c.getAnnotation(Union.class)) != null) {
                        return this.getAnnotatedUnion(union, names);
                    }
                    if (this.isStringable(c)) {
                        Schema result = Schema.create(Schema.Type.STRING);
                        result.addProp(CLASS_PROP, c.getName());
                        return result;
                    }
                    if (c.isEnum()) {
                        Enum[] constants;
                        ArrayList<String> symbols = new ArrayList<String>();
                        for (Enum constant : constants = (Enum[])c.getEnumConstants()) {
                            symbols.add(constant.name());
                        }
                        schema = Schema.createEnum(name, doc, space, symbols);
                        this.consumeAvroAliasAnnotation(c, schema);
                    } else if (GenericFixed.class.isAssignableFrom(c)) {
                        int size = c.getAnnotation(FixedSize.class).value();
                        schema = Schema.createFixed(name, doc, space, size);
                        this.consumeAvroAliasAnnotation(c, schema);
                    } else {
                        AvroMeta[] metadata;
                        if (IndexedRecord.class.isAssignableFrom(c)) {
                            return super.createSchema(type, names);
                        }
                        ArrayList<Schema.Field> fields = new ArrayList<Schema.Field>();
                        boolean error = Throwable.class.isAssignableFrom(c);
                        schema = Schema.createRecord(name, doc, space, error);
                        this.consumeAvroAliasAnnotation(c, schema);
                        names.put(c.getName(), schema);
                        for (Field field : ReflectData.getCachedFields(c)) {
                            String fieldName;
                            if ((field.getModifiers() & 0x88) != 0 || field.isAnnotationPresent(AvroIgnore.class)) continue;
                            Schema fieldSchema = this.createFieldSchema(field, names);
                            annotatedDoc = field.getAnnotation(AvroDoc.class);
                            doc = annotatedDoc != null ? annotatedDoc.value() : null;
                            Object defaultValue = this.createSchemaDefaultValue(type, field, fieldSchema);
                            AvroName annotatedName = field.getAnnotation(AvroName.class);
                            String string2 = fieldName = annotatedName != null ? annotatedName.value() : field.getName();
                            if (STRING_OUTER_PARENT_REFERENCE.equals(fieldName)) {
                                throw new AvroTypeException("Class " + fullName + " must be a static inner class");
                            }
                            Schema.Field recordField = new Schema.Field(fieldName, fieldSchema, doc, defaultValue);
                            AvroMeta[] metadata2 = (AvroMeta[])field.getAnnotationsByType(AvroMeta.class);
                            for (AvroMeta meta : metadata2) {
                                if (recordField.getObjectProps().containsKey(meta.key())) {
                                    throw new AvroTypeException("Duplicate field prop key: " + meta.key());
                                }
                                recordField.addProp(meta.key(), meta.value());
                            }
                            for (Schema.Field f : fields) {
                                if (!f.name().equals(fieldName)) continue;
                                throw new AvroTypeException("double field entry: " + fieldName);
                            }
                            this.consumeFieldAlias(field, recordField);
                            fields.add(recordField);
                        }
                        if (error) {
                            fields.add(new Schema.Field("detailMessage", THROWABLE_MESSAGE, null, null));
                        }
                        schema.setFields(fields);
                        for (AvroMeta meta : metadata = (AvroMeta[])c.getAnnotationsByType(AvroMeta.class)) {
                            if (schema.getObjectProps().containsKey(meta.key())) {
                                throw new AvroTypeException("Duplicate type prop key: " + meta.key());
                            }
                            schema.addProp(meta.key(), meta.value());
                        }
                    }
                    names.put(fullName, schema);
                }
                return schema;
            }
        }
        return super.createSchema(type, names);
    }

    @Override
    protected boolean isStringable(Class<?> c) {
        return c.isAnnotationPresent(Stringable.class) || super.isStringable(c);
    }

    private String simpleName(Class<?> c) {
        String simpleName = null;
        if (c != null) {
            while (c.isAnonymousClass()) {
                c = c.getSuperclass();
            }
            simpleName = c.getSimpleName();
        }
        return simpleName;
    }

    private void setElement(Schema schema, Type element) {
        if (!(element instanceof Class)) {
            return;
        }
        Class c = (Class)element;
        Union union = c.getAnnotation(Union.class);
        if (union != null) {
            schema.addProp(ELEMENT_PROP, c.getName());
        }
    }

    private Schema getAnnotatedUnion(Union union, Map<String, Schema> names) {
        ArrayList<Schema> branches = new ArrayList<Schema>();
        for (Class branch : union.value()) {
            branches.add(this.createSchema(branch, names));
        }
        return Schema.createUnion(branches);
    }

    public static Schema makeNullable(Schema schema) {
        if (schema.getType() == Schema.Type.UNION) {
            for (Schema subType : schema.getTypes()) {
                if (subType.getType() != Schema.Type.NULL) continue;
                return schema;
            }
            ArrayList<Schema> withNull = new ArrayList<Schema>();
            withNull.add(Schema.create(Schema.Type.NULL));
            withNull.addAll(schema.getTypes());
            return Schema.createUnion(withNull);
        }
        return Schema.createUnion(Arrays.asList(Schema.create(Schema.Type.NULL), schema));
    }

    private static Field[] getCachedFields(Class<?> recordClass) {
        return MapUtil.computeIfAbsent(FIELDS_CACHE, recordClass, rc -> ReflectData.getFields(rc, true));
    }

    private static Field[] getFields(Class<?> recordClass, boolean excludeJava) {
        LinkedHashMap<String, Field> fields = new LinkedHashMap<String, Field>();
        Class<?> c = recordClass;
        while (!excludeJava || c.getPackage() == null || !c.getPackage().getName().startsWith("java.")) {
            Field[] declaredFields = c.getDeclaredFields();
            Arrays.sort(declaredFields, Comparator.comparing(Field::getName));
            for (Field field : declaredFields) {
                if ((field.getModifiers() & 0x88) != 0 || fields.put(field.getName(), field) == null) continue;
                throw new AvroTypeException(c + " contains two fields named: " + field);
            }
            if ((c = c.getSuperclass()) != null) continue;
        }
        Field[] fieldsList = fields.values().toArray(new Field[0]);
        return fieldsList;
    }

    protected Schema createFieldSchema(Field field, Map<String, Schema> names) {
        AvroEncode enc = field.getAnnotation(AvroEncode.class);
        if (enc != null) {
            try {
                return enc.using().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]).getSchema();
            }
            catch (Exception e) {
                throw new AvroRuntimeException("Could not create schema from custom serializer for " + field.getName());
            }
        }
        AvroSchema explicit = field.getAnnotation(AvroSchema.class);
        if (explicit != null) {
            return new Schema.Parser().parse(explicit.value());
        }
        Union union = field.getAnnotation(Union.class);
        if (union != null) {
            return this.getAnnotatedUnion(union, names);
        }
        Schema schema = this.createSchema(field.getGenericType(), names);
        if (field.isAnnotationPresent(Stringable.class)) {
            schema = Schema.create(Schema.Type.STRING);
        }
        if (field.isAnnotationPresent(Nullable.class)) {
            schema = ReflectData.makeNullable(schema);
        }
        return schema;
    }

    @Override
    public Protocol getProtocol(Class iface) {
        Protocol protocol = new Protocol(this.simpleName(iface), iface.getPackage() == null ? "" : iface.getPackage().getName());
        LinkedHashMap<String, Schema> names = new LinkedHashMap<String, Schema>();
        Map<String, Protocol.Message> messages = protocol.getMessages();
        Map<TypeVariable<?>, Type> genericTypeVariableMap = ReflectionUtil.resolveTypeVariables(iface);
        for (Method method : iface.getMethods()) {
            if ((method.getModifiers() & 8) != 0) continue;
            String name = method.getName();
            if (messages.containsKey(name)) {
                throw new AvroTypeException("Two methods with same name: " + name);
            }
            messages.put(name, this.getMessage(method, protocol, names, genericTypeVariableMap));
        }
        ArrayList<Schema> types = new ArrayList<Schema>(names.values());
        Collections.reverse(types);
        protocol.setTypes(types);
        return protocol;
    }

    private Protocol.Message getMessage(Method method, Protocol protocol, Map<String, Schema> names, Map<? extends Type, Type> genericTypeMap) {
        AvroSchema explicit;
        Schema response;
        ArrayList<Schema.Field> fields = new ArrayList<Schema.Field>();
        for (Parameter parameter : method.getParameters()) {
            Schema paramSchema = this.getSchema(genericTypeMap.getOrDefault(parameter.getParameterizedType(), parameter.getType()), names);
            for (Annotation annotation : parameter.getAnnotations()) {
                if (annotation instanceof AvroSchema) {
                    paramSchema = new Schema.Parser().parse(((AvroSchema)annotation).value());
                    continue;
                }
                if (annotation instanceof Union) {
                    paramSchema = this.getAnnotatedUnion((Union)annotation, names);
                    continue;
                }
                if (!(annotation instanceof Nullable)) continue;
                paramSchema = ReflectData.makeNullable(paramSchema);
            }
            fields.add(new Schema.Field(ReflectData.unmangle(parameter.getName()), paramSchema, null, null));
        }
        Schema request = Schema.createRecord(fields);
        Type genericReturnType = method.getGenericReturnType();
        Type returnType = genericTypeMap.getOrDefault(genericReturnType, genericReturnType);
        Union union = method.getAnnotation(Union.class);
        Schema schema = response = union == null ? this.getSchema(returnType, names) : this.getAnnotatedUnion(union, names);
        if (method.isAnnotationPresent(Nullable.class)) {
            response = ReflectData.makeNullable(response);
        }
        if ((explicit = method.getAnnotation(AvroSchema.class)) != null) {
            response = new Schema.Parser().parse(explicit.value());
        }
        ArrayList<Schema> errs = new ArrayList<Schema>();
        errs.add(Protocol.SYSTEM_ERROR);
        for (Type err : method.getGenericExceptionTypes()) {
            errs.add(this.getSchema(err, names));
        }
        Schema errors = Schema.createUnion(errs);
        return protocol.createMessage(method.getName(), null, Collections.emptyMap(), request, response, errors);
    }

    private Schema getSchema(Type type, Map<String, Schema> names) {
        try {
            return this.createSchema(type, names);
        }
        catch (AvroTypeException e) {
            throw new AvroTypeException("Error getting schema for " + type + ": " + e.getMessage(), e);
        }
    }

    @Override
    protected int compare(Object o1, Object o2, Schema s, boolean equals) {
        switch (s.getType()) {
            case ARRAY: {
                if (!o1.getClass().isArray()) break;
                Schema elementType = s.getElementType();
                int l1 = Array.getLength(o1);
                int l2 = Array.getLength(o2);
                int l = Math.min(l1, l2);
                for (int i = 0; i < l; ++i) {
                    int compare = this.compare(Array.get(o1, i), Array.get(o2, i), elementType, equals);
                    if (compare == 0) continue;
                    return compare;
                }
                return Integer.compare(l1, l2);
            }
            case BYTES: {
                if (!o1.getClass().isArray()) break;
                byte[] b1 = (byte[])o1;
                byte[] b2 = (byte[])o2;
                return BinaryData.compareBytes(b1, 0, b1.length, b2, 0, b2.length);
            }
        }
        return super.compare(o1, o2, s, equals);
    }

    @Override
    protected Object getRecordState(Object record, Schema schema) {
        return this.getFieldAccessors(record.getClass(), schema);
    }

    private void consumeAvroAliasAnnotation(Class<?> c, Schema schema) {
        AvroAlias[] aliases;
        for (AvroAlias alias : aliases = (AvroAlias[])c.getAnnotationsByType(AvroAlias.class)) {
            String space = alias.space();
            if ("NOT A VALID NAMESPACE".equals(space)) {
                space = null;
            }
            schema.addAlias(alias.alias(), space);
        }
    }

    private void consumeFieldAlias(Field field, Schema.Field recordField) {
        AvroAlias[] aliases;
        for (AvroAlias alias : aliases = (AvroAlias[])field.getAnnotationsByType(AvroAlias.class)) {
            if (!alias.space().equals("NOT A VALID NAMESPACE")) {
                throw new AvroRuntimeException("Namespaces are not allowed on field aliases. Offending field: " + recordField.name());
            }
            recordField.addAlias(alias.alias());
        }
    }

    @Override
    public Object createFixed(Object old, Schema schema) {
        Conversion conversion;
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType != null && (conversion = this.getConversionFor(schema.getLogicalType())) != null) {
            return new GenericData.Fixed(schema);
        }
        return super.createFixed(old, schema);
    }

    @Override
    public Object newRecord(Object old, Schema schema) {
        Conversion conversion;
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType != null && (conversion = this.getConversionFor(schema.getLogicalType())) != null) {
            return new GenericData.Record(schema);
        }
        return super.newRecord(old, schema);
    }

    static {
        ARRAY_CLASSES.put(Byte.TYPE, byte[].class);
        ARRAY_CLASSES.put(Character.TYPE, char[].class);
        ARRAY_CLASSES.put(Short.TYPE, short[].class);
        ARRAY_CLASSES.put(Integer.TYPE, int[].class);
        ARRAY_CLASSES.put(Long.TYPE, long[].class);
        ARRAY_CLASSES.put(Float.TYPE, float[].class);
        ARRAY_CLASSES.put(Double.TYPE, double[].class);
        ARRAY_CLASSES.put(Boolean.TYPE, boolean[].class);
        THROWABLE_MESSAGE = ReflectData.makeNullable(Schema.create(Schema.Type.STRING));
        FIELDS_CACHE = new ConcurrentHashMap();
    }

    static class ClassAccessorData {
        private final Class<?> clazz;
        private final Map<String, FieldAccessor> byName = new HashMap<String, FieldAccessor>();
        final Map<Schema, FieldAccessor[]> bySchema = new WeakHashMap<Schema, FieldAccessor[]>();

        private ClassAccessorData(Class<?> c) {
            this.clazz = c;
            for (Field f : ReflectData.getFields(c, false)) {
                if (f.isAnnotationPresent(AvroIgnore.class)) continue;
                FieldAccessor accessor = ReflectionUtil.getFieldAccess().getAccessor(f);
                AvroName avroname = f.getAnnotation(AvroName.class);
                this.byName.put(avroname != null ? avroname.value() : f.getName(), accessor);
            }
        }

        private synchronized FieldAccessor[] getAccessorsFor(Schema schema) {
            FieldAccessor[] result = this.bySchema.get(schema);
            if (result == null) {
                result = this.createAccessorsFor(schema);
                this.bySchema.put(schema, result);
            }
            return result;
        }

        private FieldAccessor[] createAccessorsFor(Schema schema) {
            List<Schema.Field> avroFields = schema.getFields();
            FieldAccessor[] result = new FieldAccessor[avroFields.size()];
            for (Schema.Field avroField : schema.getFields()) {
                result[avroField.pos()] = this.byName.get(avroField.name());
            }
            return result;
        }

        private FieldAccessor getAccessorFor(String fieldName) {
            FieldAccessor result = this.byName.get(fieldName);
            if (result == null) {
                throw new AvroRuntimeException("No field named " + fieldName + " in: " + this.clazz);
            }
            return result;
        }
    }

    public static class AllowNull
    extends ReflectData {
        private static final AllowNull INSTANCE = new AllowNull();

        public static AllowNull get() {
            return INSTANCE;
        }

        @Override
        protected Schema createFieldSchema(Field field, Map<String, Schema> names) {
            Schema schema = super.createFieldSchema(field, names);
            if (field.getType().isPrimitive()) {
                return schema;
            }
            return AllowNull.makeNullable(schema);
        }
    }
}

