/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.specific;

import java.io.File;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.ExternalizableInput;
import org.apache.avro.specific.ExternalizableOutput;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.util.ClassUtils;
import org.apache.avro.util.MapUtil;
import org.apache.avro.util.SchemaUtil;
import org.apache.avro.util.internal.ClassValueCache;

public class SpecificData
extends GenericData {
    private static final SpecificData INSTANCE = new SpecificData();
    private static final Class<?>[] NO_ARG = new Class[0];
    private static final Class<?>[] SCHEMA_ARG = new Class[]{Schema.class};
    private static final Function<Class<?>, Constructor<?>> CTOR_CACHE = new ClassValueCache<Constructor>(c -> {
        boolean useSchema = SchemaConstructable.class.isAssignableFrom((Class<?>)c);
        try {
            Constructor meth = c.getDeclaredConstructor(useSchema ? SCHEMA_ARG : NO_ARG);
            meth.setAccessible(true);
            return meth;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    private static final Function<Class<?>, SpecificData> MODEL_CACHE = new ClassValueCache<SpecificData>(c -> {
        try {
            Field specificDataField = c.getDeclaredField("MODEL$");
            specificDataField.setAccessible(true);
            return (SpecificData)specificDataField.get(null);
        }
        catch (NoSuchFieldException e) {
            return SpecificData.get();
        }
        catch (IllegalAccessException e) {
            throw new AvroRuntimeException("while trying to access field MODEL$ on " + c.getCanonicalName(), e);
        }
    });
    public static final String CLASS_PROP = "java-class";
    public static final String KEY_CLASS_PROP = "java-key-class";
    public static final String ELEMENT_PROP = "java-element-class";
    public static final char RESERVED_WORD_ESCAPE_CHAR = '$';
    public static final Set<String> RESERVED_WORDS = new HashSet<String>(Arrays.asList("_", "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "true", "false", "null", "Builder"));
    protected Set<Class> stringableClasses = new HashSet<Class>(Arrays.asList(BigDecimal.class, BigInteger.class, URI.class, URL.class, File.class));
    private boolean useCustomCoderFlag = Boolean.parseBoolean(System.getProperty("org.apache.avro.specific.use_custom_coders", "false"));
    private final ConcurrentMap<String, Class> classCache = new ConcurrentHashMap<String, Class>();
    private static final Class NO_CLASS = new Object(){}.getClass();
    private static final Schema NULL_SCHEMA = Schema.create(Schema.Type.NULL);
    private final ClassValueCache<Schema> schemaClassCache = new ClassValueCache<Schema>(c -> this.createSchema((Type)c, (Map<String, Schema>)new HashMap<String, Schema>()));
    private final Map<Type, Schema> schemaTypeCache = Collections.synchronizedMap(new WeakHashMap());

    public SpecificData() {
    }

    public SpecificData(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public DatumReader createDatumReader(Schema schema) {
        return this.createDatumReader(schema, schema);
    }

    @Override
    public DatumReader createDatumReader(Schema writer, Schema reader) {
        return new SpecificDatumReader(writer, reader, this);
    }

    @Override
    public DatumWriter createDatumWriter(Schema schema) {
        return new SpecificDatumWriter(schema, this);
    }

    public static SpecificData get() {
        return INSTANCE;
    }

    public static SpecificData getForSchema(Schema reader) {
        Class clazz;
        if (reader != null && (reader.getType() == Schema.Type.RECORD || reader.getType() == Schema.Type.UNION) && (clazz = SpecificData.get().getClass(reader)) != null) {
            return SpecificData.getForClass(clazz);
        }
        return SpecificData.get();
    }

    public static <T> SpecificData getForClass(Class<T> c) {
        if (SpecificRecordBase.class.isAssignableFrom(c)) {
            return MODEL_CACHE.apply(c);
        }
        return SpecificData.get();
    }

    public boolean useCustomCoders() {
        return this.useCustomCoderFlag;
    }

    public void setCustomCoders(boolean flag) {
        this.useCustomCoderFlag = flag;
    }

    @Override
    protected boolean isEnum(Object datum) {
        return datum instanceof Enum || super.isEnum(datum);
    }

    @Override
    public Object createEnum(String symbol, Schema schema) {
        Class c = this.getClass(schema);
        if (c == null) {
            return super.createEnum(symbol, schema);
        }
        if (RESERVED_WORDS.contains(symbol)) {
            symbol = symbol + "$";
        }
        return Enum.valueOf(c, symbol);
    }

    @Override
    protected Schema getEnumSchema(Object datum) {
        return datum instanceof Enum ? this.getSchema(datum.getClass()) : super.getEnumSchema(datum);
    }

    protected static String unmangle(String word) {
        while (word.endsWith("$")) {
            word = word.substring(0, word.length() - 1);
        }
        return word;
    }

    public Class getClass(Schema schema) {
        switch (schema.getType()) {
            case FIXED: 
            case RECORD: 
            case ENUM: {
                String name = schema.getFullName();
                if (name == null) {
                    return null;
                }
                Class c = MapUtil.computeIfAbsent(this.classCache, name, n -> {
                    try {
                        return ClassUtils.forName(this.getClassLoader(), SpecificData.getClassName(schema));
                    }
                    catch (ClassNotFoundException e) {
                        StringBuilder nestedName = new StringBuilder((String)n);
                        int lastDot = n.lastIndexOf(46);
                        while (lastDot != -1) {
                            nestedName.setCharAt(lastDot, '$');
                            try {
                                return ClassUtils.forName(this.getClassLoader(), nestedName.toString());
                            }
                            catch (ClassNotFoundException classNotFoundException) {
                                lastDot = n.lastIndexOf(46, lastDot - 1);
                            }
                        }
                        return NO_CLASS;
                    }
                });
                return c == NO_CLASS ? null : c;
            }
            case ARRAY: {
                return List.class;
            }
            case MAP: {
                return Map.class;
            }
            case UNION: {
                List<Schema> types = schema.getTypes();
                if (types.size() == 2 && types.contains(NULL_SCHEMA)) {
                    return this.getWrapper(types.get(types.get(0).equals(NULL_SCHEMA) ? 1 : 0));
                }
                return Object.class;
            }
            case STRING: {
                if ("String".equals(schema.getProp("avro.java.string"))) {
                    return String.class;
                }
                return CharSequence.class;
            }
            case BYTES: {
                return ByteBuffer.class;
            }
            case INT: {
                return Integer.TYPE;
            }
            case LONG: {
                return Long.TYPE;
            }
            case FLOAT: {
                return Float.TYPE;
            }
            case DOUBLE: {
                return Double.TYPE;
            }
            case BOOLEAN: {
                return Boolean.TYPE;
            }
            case NULL: {
                return Void.TYPE;
            }
        }
        throw new AvroRuntimeException("Unknown type: " + schema);
    }

    private Class getWrapper(Schema schema) {
        switch (schema.getType()) {
            case INT: {
                return Integer.class;
            }
            case LONG: {
                return Long.class;
            }
            case FLOAT: {
                return Float.class;
            }
            case DOUBLE: {
                return Double.class;
            }
            case BOOLEAN: {
                return Boolean.class;
            }
        }
        return this.getClass(schema);
    }

    public static String getClassName(Schema schema) {
        String namespace = schema.getNamespace();
        String name = schema.getName();
        if (namespace == null || "".equals(namespace)) {
            return name;
        }
        StringBuilder classNameBuilder = new StringBuilder();
        String[] words = namespace.split("\\.");
        for (int i = 0; i < words.length; ++i) {
            String word = words[i];
            classNameBuilder.append(word);
            if (RESERVED_WORDS.contains(word)) {
                classNameBuilder.append('$');
            }
            if (i == words.length - 1 && word.endsWith("$")) continue;
            classNameBuilder.append(".");
        }
        classNameBuilder.append(name);
        return classNameBuilder.toString();
    }

    public Schema getSchema(Type type) {
        try {
            if (type instanceof Class) {
                return this.schemaClassCache.apply((Class)type);
            }
            return this.schemaTypeCache.computeIfAbsent(type, t -> this.createSchema((Type)t, (Map<String, Schema>)new HashMap<String, Schema>()));
        }
        catch (Exception e) {
            throw e instanceof AvroRuntimeException ? (AvroRuntimeException)e : new AvroRuntimeException(e);
        }
    }

    protected Schema createSchema(Type type, Map<String, Schema> names) {
        if (type instanceof Class && CharSequence.class.isAssignableFrom((Class)type)) {
            return Schema.create(Schema.Type.STRING);
        }
        if (type == ByteBuffer.class) {
            return Schema.create(Schema.Type.BYTES);
        }
        if (type == Integer.class || type == Integer.TYPE) {
            return Schema.create(Schema.Type.INT);
        }
        if (type == Long.class || type == Long.TYPE) {
            return Schema.create(Schema.Type.LONG);
        }
        if (type == Float.class || type == Float.TYPE) {
            return Schema.create(Schema.Type.FLOAT);
        }
        if (type == Double.class || type == Double.TYPE) {
            return Schema.create(Schema.Type.DOUBLE);
        }
        if (type == Boolean.class || type == Boolean.TYPE) {
            return Schema.create(Schema.Type.BOOLEAN);
        }
        if (type == Void.class || type == Void.TYPE) {
            return Schema.create(Schema.Type.NULL);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            Class raw = (Class)ptype.getRawType();
            Type[] params = ptype.getActualTypeArguments();
            if (Collection.class.isAssignableFrom(raw)) {
                if (params.length != 1) {
                    throw new AvroTypeException("No array type specified.");
                }
                return Schema.createArray(this.createSchema(params[0], names));
            }
            if (Map.class.isAssignableFrom(raw)) {
                Type key = params[0];
                Type value = params[1];
                if (!(key instanceof Class) || !CharSequence.class.isAssignableFrom((Class)key)) {
                    throw new AvroTypeException("Map key class not CharSequence: " + SchemaUtil.describe(key));
                }
                return Schema.createMap(this.createSchema(value, names));
            }
            return this.createSchema(raw, names);
        }
        if (type instanceof Class) {
            Class c = (Class)type;
            String fullName = c.getName();
            Schema schema = names.get(fullName);
            if (schema == null) {
                try {
                    schema = (Schema)c.getDeclaredField("SCHEMA$").get(null);
                    if (!fullName.equals(SpecificData.getClassName(schema))) {
                        schema = new Schema.Parser().parse(schema.toString().replace(schema.getNamespace(), c.getPackage().getName()));
                    }
                }
                catch (NoSuchFieldException e) {
                    throw new AvroRuntimeException("Not a Specific class: " + c);
                }
                catch (IllegalAccessException e) {
                    throw new AvroRuntimeException(e);
                }
            }
            names.put(fullName, schema);
            return schema;
        }
        throw new AvroTypeException("Unknown type: " + type);
    }

    @Override
    protected String getSchemaName(Object datum) {
        Class<?> c;
        if (datum != null && this.isStringable(c = datum.getClass())) {
            return Schema.Type.STRING.getName();
        }
        return super.getSchemaName(datum);
    }

    protected boolean isStringable(Class<?> c) {
        return this.stringableClasses.contains(c);
    }

    protected boolean isStringType(Class<?> c) {
        return CharSequence.class.isAssignableFrom(c);
    }

    public Protocol getProtocol(Class iface) {
        try {
            Protocol p = (Protocol)iface.getDeclaredField("PROTOCOL").get(null);
            if (!p.getNamespace().equals(iface.getPackage().getName())) {
                p = Protocol.parse(p.toString().replace(p.getNamespace(), iface.getPackage().getName()));
            }
            return p;
        }
        catch (NoSuchFieldException e) {
            throw new AvroRuntimeException("Not a Specific protocol: " + iface);
        }
        catch (IllegalAccessException e) {
            throw new AvroRuntimeException(e);
        }
    }

    @Override
    protected int compare(Object o1, Object o2, Schema s, boolean eq) {
        switch (s.getType()) {
            case ENUM: {
                if (!(o1 instanceof Enum)) break;
                return ((Enum)o1).ordinal() - ((Enum)o2).ordinal();
            }
        }
        return super.compare(o1, o2, s, eq);
    }

    public static Object newInstance(Class c, Schema s) {
        Object result;
        boolean useSchema = SchemaConstructable.class.isAssignableFrom(c);
        try {
            Object[] objectArray;
            Constructor<?> meth = CTOR_CACHE.apply(c);
            if (useSchema) {
                Object[] objectArray2 = new Object[1];
                objectArray = objectArray2;
                objectArray2[0] = s;
            } else {
                objectArray = null;
            }
            result = meth.newInstance(objectArray);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Object createFixed(Object old, Schema schema) {
        Class c = this.getClass(schema);
        if (c == null) {
            return super.createFixed(old, schema);
        }
        return c.isInstance(old) ? old : SpecificData.newInstance(c, schema);
    }

    @Override
    public Object newRecord(Object old, Schema schema) {
        Class c = this.getClass(schema);
        if (c == null) {
            return super.newRecord(old, schema);
        }
        return c.isInstance(old) ? old : SpecificData.newInstance(c, schema);
    }

    @Override
    public GenericData.InstanceSupplier getNewRecordSupplier(Schema schema) {
        Object[] objectArray;
        Class c = this.getClass(schema);
        if (c == null) {
            return super.getNewRecordSupplier(schema);
        }
        boolean useSchema = SchemaConstructable.class.isAssignableFrom(c);
        Constructor<?> meth = CTOR_CACHE.apply(c);
        if (useSchema) {
            Object[] objectArray2 = new Object[1];
            objectArray = objectArray2;
            objectArray2[0] = schema;
        } else {
            objectArray = null;
        }
        Object[] params = objectArray;
        return (old, sch) -> {
            try {
                return c.isInstance(old) ? old : meth.newInstance(params);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static BinaryDecoder getDecoder(ObjectInput in) {
        return DecoderFactory.get().directBinaryDecoder(new ExternalizableInput(in), null);
    }

    public static BinaryEncoder getEncoder(ObjectOutput out) {
        return EncoderFactory.get().directBinaryEncoder(new ExternalizableOutput(out), null);
    }

    @Override
    public Object createString(Object value) {
        if (value instanceof String) {
            return value;
        }
        if (this.isStringable(value.getClass())) {
            return value;
        }
        return super.createString(value);
    }

    public static interface SchemaConstructable {
    }
}

