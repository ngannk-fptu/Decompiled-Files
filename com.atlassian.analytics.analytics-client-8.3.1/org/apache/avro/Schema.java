/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.avro;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.NullNode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.JsonProperties;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.SchemaParseException;
import org.apache.avro.SystemLimitException;
import org.apache.avro.util.internal.Accessor;
import org.apache.avro.util.internal.JacksonUtils;
import org.apache.avro.util.internal.ThreadLocalWithInitial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Schema
extends JsonProperties
implements Serializable {
    private static final long serialVersionUID = 1L;
    static final JsonFactory FACTORY = new JsonFactory();
    static final Logger LOG = LoggerFactory.getLogger(Schema.class);
    static final ObjectMapper MAPPER = new ObjectMapper(FACTORY);
    private static final int NO_HASHCODE = Integer.MIN_VALUE;
    private final Type type;
    private LogicalType logicalType = null;
    private static final Set<String> SCHEMA_RESERVED;
    private static final Set<String> ENUM_RESERVED;
    int hashCode = Integer.MIN_VALUE;
    private static final Set<String> FIELD_RESERVED;
    private static final ThreadLocal<Set> SEEN_EQUALS;
    private static final ThreadLocal<Map> SEEN_HASHCODE;
    static final Map<String, Type> PRIMITIVES;
    private static ThreadLocal<Boolean> validateNames;
    private static final ThreadLocal<Boolean> VALIDATE_DEFAULTS;

    protected Object writeReplace() {
        SerializableSchema ss = new SerializableSchema();
        ss.schemaString = this.toString();
        return ss;
    }

    Schema(Type type) {
        super(type == Type.ENUM ? ENUM_RESERVED : SCHEMA_RESERVED);
        this.type = type;
    }

    public static Schema create(Type type) {
        switch (type) {
            case STRING: {
                return new StringSchema();
            }
            case BYTES: {
                return new BytesSchema();
            }
            case INT: {
                return new IntSchema();
            }
            case LONG: {
                return new LongSchema();
            }
            case FLOAT: {
                return new FloatSchema();
            }
            case DOUBLE: {
                return new DoubleSchema();
            }
            case BOOLEAN: {
                return new BooleanSchema();
            }
            case NULL: {
                return new NullSchema();
            }
        }
        throw new AvroRuntimeException("Can't create a: " + (Object)((Object)type));
    }

    @Override
    public void addProp(String name, String value) {
        super.addProp(name, value);
        this.hashCode = Integer.MIN_VALUE;
    }

    @Override
    public void addProp(String name, Object value) {
        super.addProp(name, value);
        this.hashCode = Integer.MIN_VALUE;
    }

    public LogicalType getLogicalType() {
        return this.logicalType;
    }

    void setLogicalType(LogicalType logicalType) {
        this.logicalType = logicalType;
    }

    @Deprecated
    public static Schema createRecord(List<Field> fields) {
        Schema result = Schema.createRecord(null, null, null, false);
        result.setFields(fields);
        return result;
    }

    public static Schema createRecord(String name, String doc, String namespace, boolean isError) {
        return new RecordSchema(new Name(name, namespace), doc, isError);
    }

    public static Schema createRecord(String name, String doc, String namespace, boolean isError, List<Field> fields) {
        return new RecordSchema(new Name(name, namespace), doc, isError, fields);
    }

    public static Schema createEnum(String name, String doc, String namespace, List<String> values) {
        return new EnumSchema(new Name(name, namespace), doc, new LockableArrayList<String>(values), null);
    }

    public static Schema createEnum(String name, String doc, String namespace, List<String> values, String enumDefault) {
        return new EnumSchema(new Name(name, namespace), doc, new LockableArrayList<String>(values), enumDefault);
    }

    public static Schema createArray(Schema elementType) {
        return new ArraySchema(elementType);
    }

    public static Schema createMap(Schema valueType) {
        return new MapSchema(valueType);
    }

    public static Schema createUnion(List<Schema> types) {
        return new UnionSchema(new LockableArrayList<Schema>(types));
    }

    public static Schema createUnion(Schema ... types) {
        return Schema.createUnion(new LockableArrayList<Schema>(types));
    }

    public static Schema createFixed(String name, String doc, String space, int size) {
        return new FixedSchema(new Name(name, space), doc, size);
    }

    public Type getType() {
        return this.type;
    }

    public Field getField(String fieldname) {
        throw new AvroRuntimeException("Not a record: " + this);
    }

    public List<Field> getFields() {
        throw new AvroRuntimeException("Not a record: " + this);
    }

    public boolean hasFields() {
        throw new AvroRuntimeException("Not a record: " + this);
    }

    public void setFields(List<Field> fields) {
        throw new AvroRuntimeException("Not a record: " + this);
    }

    public List<String> getEnumSymbols() {
        throw new AvroRuntimeException("Not an enum: " + this);
    }

    public String getEnumDefault() {
        throw new AvroRuntimeException("Not an enum: " + this);
    }

    public int getEnumOrdinal(String symbol) {
        throw new AvroRuntimeException("Not an enum: " + this);
    }

    public boolean hasEnumSymbol(String symbol) {
        throw new AvroRuntimeException("Not an enum: " + this);
    }

    public String getName() {
        return this.type.name;
    }

    public String getDoc() {
        return null;
    }

    public String getNamespace() {
        throw new AvroRuntimeException("Not a named type: " + this);
    }

    public String getFullName() {
        return this.getName();
    }

    public void addAlias(String alias) {
        throw new AvroRuntimeException("Not a named type: " + this);
    }

    public void addAlias(String alias, String space) {
        throw new AvroRuntimeException("Not a named type: " + this);
    }

    public Set<String> getAliases() {
        throw new AvroRuntimeException("Not a named type: " + this);
    }

    public boolean isError() {
        throw new AvroRuntimeException("Not a record: " + this);
    }

    public Schema getElementType() {
        throw new AvroRuntimeException("Not an array: " + this);
    }

    public Schema getValueType() {
        throw new AvroRuntimeException("Not a map: " + this);
    }

    public List<Schema> getTypes() {
        throw new AvroRuntimeException("Not a union: " + this);
    }

    public Integer getIndexNamed(String name) {
        throw new AvroRuntimeException("Not a union: " + this);
    }

    public int getFixedSize() {
        throw new AvroRuntimeException("Not fixed: " + this);
    }

    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean pretty) {
        return this.toString(new Names(), pretty);
    }

    @Deprecated
    public String toString(Collection<Schema> referencedSchemas, boolean pretty) {
        Names names = new Names();
        if (referencedSchemas != null) {
            for (Schema s : referencedSchemas) {
                names.add(s);
            }
        }
        return this.toString(names, pretty);
    }

    String toString(Names names, boolean pretty) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator gen = FACTORY.createGenerator(writer);
            if (pretty) {
                gen.useDefaultPrettyPrinter();
            }
            this.toJson(names, gen);
            gen.flush();
            return writer.toString();
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    void toJson(Names names, JsonGenerator gen) throws IOException {
        if (!this.hasProps()) {
            gen.writeString(this.getName());
        } else {
            gen.writeStartObject();
            gen.writeStringField("type", this.getName());
            this.writeProps(gen);
            gen.writeEndObject();
        }
    }

    void fieldsToJson(Names names, JsonGenerator gen) throws IOException {
        throw new AvroRuntimeException("Not a record: " + this);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Schema)) {
            return false;
        }
        Schema that = (Schema)o;
        if (this.type != that.type) {
            return false;
        }
        return this.equalCachedHash(that) && this.propsEqual(that);
    }

    public final int hashCode() {
        if (this.hashCode == Integer.MIN_VALUE) {
            this.hashCode = this.computeHash();
        }
        return this.hashCode;
    }

    int computeHash() {
        return this.getType().hashCode() + this.propsHashCode();
    }

    final boolean equalCachedHash(Schema other) {
        return this.hashCode == other.hashCode || this.hashCode == Integer.MIN_VALUE || other.hashCode == Integer.MIN_VALUE;
    }

    public boolean isUnion() {
        return this instanceof UnionSchema;
    }

    public boolean isNullable() {
        if (!this.isUnion()) {
            return this.getType().equals((Object)Type.NULL);
        }
        for (Schema schema : this.getTypes()) {
            if (!schema.isNullable()) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public static Schema parse(File file) throws IOException {
        return new Parser().parse(file);
    }

    @Deprecated
    public static Schema parse(InputStream in) throws IOException {
        return new Parser().parse(in);
    }

    @Deprecated
    public static Schema parse(String jsonSchema) {
        return new Parser().parse(jsonSchema);
    }

    @Deprecated
    public static Schema parse(String jsonSchema, boolean validate) {
        return new Parser().setValidate(validate).parse(jsonSchema);
    }

    private static String validateName(String name) {
        if (!validateNames.get().booleanValue()) {
            return name;
        }
        if (name == null) {
            throw new SchemaParseException("Null name");
        }
        int length = name.length();
        if (length == 0) {
            throw new SchemaParseException("Empty name");
        }
        char first = name.charAt(0);
        if (!Character.isLetter(first) && first != '_') {
            throw new SchemaParseException("Illegal initial character: " + name);
        }
        for (int i = 1; i < length; ++i) {
            char c = name.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_') continue;
            throw new SchemaParseException("Illegal character in: " + name);
        }
        return name;
    }

    private static JsonNode validateDefault(String fieldName, Schema schema, JsonNode defaultValue) {
        if (VALIDATE_DEFAULTS.get().booleanValue() && defaultValue != null && !Schema.isValidDefault(schema, defaultValue)) {
            String message = "Invalid default for field " + fieldName + ": " + defaultValue + " not a " + schema;
            throw new AvroTypeException(message);
        }
        return defaultValue;
    }

    private static boolean isValidDefault(Schema schema, JsonNode defaultValue) {
        if (defaultValue == null) {
            return false;
        }
        switch (schema.getType()) {
            case STRING: 
            case BYTES: 
            case ENUM: 
            case FIXED: {
                return defaultValue.isTextual();
            }
            case INT: {
                return defaultValue.isIntegralNumber() && defaultValue.canConvertToInt();
            }
            case LONG: {
                return defaultValue.isIntegralNumber() && defaultValue.canConvertToLong();
            }
            case FLOAT: 
            case DOUBLE: {
                return defaultValue.isNumber();
            }
            case BOOLEAN: {
                return defaultValue.isBoolean();
            }
            case NULL: {
                return defaultValue.isNull();
            }
            case ARRAY: {
                if (!defaultValue.isArray()) {
                    return false;
                }
                for (JsonNode element : defaultValue) {
                    if (Schema.isValidDefault(schema.getElementType(), element)) continue;
                    return false;
                }
                return true;
            }
            case MAP: {
                if (!defaultValue.isObject()) {
                    return false;
                }
                for (JsonNode value : defaultValue) {
                    if (Schema.isValidDefault(schema.getValueType(), value)) continue;
                    return false;
                }
                return true;
            }
            case UNION: {
                return Schema.isValidDefault(schema.getTypes().get(0), defaultValue);
            }
            case RECORD: {
                if (!defaultValue.isObject()) {
                    return false;
                }
                for (Field field : schema.getFields()) {
                    if (Schema.isValidDefault(field.schema(), defaultValue.has(field.name()) ? defaultValue.get(field.name()) : field.defaultValue())) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    static Schema parse(JsonNode schema, Names names) {
        if (schema == null) {
            throw new SchemaParseException("Cannot parse <null> schema");
        }
        if (schema.isTextual()) {
            Schema result = names.get(schema.textValue());
            if (result == null) {
                throw new SchemaParseException("Undefined name: " + schema);
            }
            return result;
        }
        if (schema.isObject()) {
            Set<String> aliases;
            Schema result;
            String type = Schema.getRequiredText(schema, "type", "No type");
            Name name = null;
            String savedSpace = names.space();
            String doc = null;
            boolean isTypeError = "error".equals(type);
            boolean isTypeRecord = "record".equals(type);
            boolean isTypeEnum = "enum".equals(type);
            boolean isTypeFixed = "fixed".equals(type);
            if (isTypeRecord || isTypeError || isTypeEnum || isTypeFixed) {
                String space = Schema.getOptionalText(schema, "namespace");
                doc = Schema.getOptionalText(schema, "doc");
                if (space == null) {
                    space = savedSpace;
                }
                name = new Name(Schema.getRequiredText(schema, "name", "No name in schema"), space);
                names.space(name.space);
            }
            if (PRIMITIVES.containsKey(type)) {
                result = Schema.create(PRIMITIVES.get(type));
            } else if (isTypeRecord || isTypeError) {
                JsonNode fieldsNode;
                ArrayList<Field> fields = new ArrayList<Field>();
                result = new RecordSchema(name, doc, isTypeError);
                if (name != null) {
                    names.add(result);
                }
                if ((fieldsNode = schema.get("fields")) == null || !fieldsNode.isArray()) {
                    throw new SchemaParseException("Record has no fields: " + schema);
                }
                for (JsonNode field : fieldsNode) {
                    JsonNode defaultValue;
                    String fieldName = Schema.getRequiredText(field, "name", "No field name");
                    String fieldDoc = Schema.getOptionalText(field, "doc");
                    JsonNode fieldTypeNode = field.get("type");
                    if (fieldTypeNode == null) {
                        throw new SchemaParseException("No field type: " + field);
                    }
                    if (fieldTypeNode.isTextual() && names.get(fieldTypeNode.textValue()) == null) {
                        throw new SchemaParseException(fieldTypeNode + " is not a defined name. The type of the \"" + fieldName + "\" field must be a defined name or a {\"type\": ...} expression.");
                    }
                    Schema fieldSchema = Schema.parse(fieldTypeNode, names);
                    Field.Order order = Field.Order.ASCENDING;
                    JsonNode orderNode = field.get("order");
                    if (orderNode != null) {
                        order = Field.Order.valueOf(orderNode.textValue().toUpperCase(Locale.ENGLISH));
                    }
                    if ((defaultValue = field.get("default")) != null && (Type.FLOAT.equals((Object)fieldSchema.getType()) || Type.DOUBLE.equals((Object)fieldSchema.getType())) && defaultValue.isTextual()) {
                        defaultValue = new DoubleNode(Double.valueOf(defaultValue.textValue()));
                    }
                    Field f = new Field(fieldName, fieldSchema, fieldDoc, defaultValue, true, order);
                    Iterator<String> i = field.fieldNames();
                    while (i.hasNext()) {
                        String prop = i.next();
                        if (FIELD_RESERVED.contains(prop)) continue;
                        f.addProp(prop, field.get(prop));
                    }
                    f.aliases = Schema.parseAliases(field);
                    fields.add(f);
                    if (fieldSchema.getLogicalType() != null || Schema.getOptionalText(field, "logicalType") == null) continue;
                    LOG.warn("Ignored the {}.{}.logicalType property (\"{}\"). It should probably be nested inside the \"type\" for the field.", new Object[]{name, fieldName, Schema.getOptionalText(field, "logicalType")});
                }
                result.setFields(fields);
            } else if (isTypeEnum) {
                JsonNode symbolsNode = schema.get("symbols");
                if (symbolsNode == null || !symbolsNode.isArray()) {
                    throw new SchemaParseException("Enum has no symbols: " + schema);
                }
                LockableArrayList<String> symbols = new LockableArrayList<String>(symbolsNode.size());
                for (JsonNode n : symbolsNode) {
                    symbols.add(n.textValue());
                }
                JsonNode enumDefault = schema.get("default");
                String defaultSymbol = null;
                if (enumDefault != null) {
                    defaultSymbol = enumDefault.textValue();
                }
                result = new EnumSchema(name, doc, symbols, defaultSymbol);
                if (name != null) {
                    names.add(result);
                }
            } else if (type.equals("array")) {
                JsonNode itemsNode = schema.get("items");
                if (itemsNode == null) {
                    throw new SchemaParseException("Array has no items type: " + schema);
                }
                result = new ArraySchema(Schema.parse(itemsNode, names));
            } else if (type.equals("map")) {
                JsonNode valuesNode = schema.get("values");
                if (valuesNode == null) {
                    throw new SchemaParseException("Map has no values type: " + schema);
                }
                result = new MapSchema(Schema.parse(valuesNode, names));
            } else if (isTypeFixed) {
                JsonNode sizeNode = schema.get("size");
                if (sizeNode == null || !sizeNode.isInt()) {
                    throw new SchemaParseException("Invalid or no size: " + schema);
                }
                result = new FixedSchema(name, doc, sizeNode.intValue());
                if (name != null) {
                    names.add(result);
                }
            } else {
                Name nameFromType = new Name(type, names.space);
                if (names.containsKey(nameFromType)) {
                    return (Schema)names.get(nameFromType);
                }
                throw new SchemaParseException("Type not supported: " + type);
            }
            Iterator<String> i = schema.fieldNames();
            Set<String> reserved = SCHEMA_RESERVED;
            if (isTypeEnum) {
                reserved = ENUM_RESERVED;
            }
            while (i.hasNext()) {
                String prop = i.next();
                if (reserved.contains(prop)) continue;
                result.addProp(prop, schema.get(prop));
            }
            result.logicalType = LogicalTypes.fromSchemaIgnoreInvalid(result);
            names.space(savedSpace);
            if (result instanceof NamedSchema && (aliases = Schema.parseAliases(schema)) != null) {
                for (String alias : aliases) {
                    result.addAlias(alias);
                }
            }
            return result;
        }
        if (schema.isArray()) {
            LockableArrayList<Schema> types = new LockableArrayList<Schema>(schema.size());
            for (JsonNode typeNode : schema) {
                types.add(Schema.parse(typeNode, names));
            }
            return new UnionSchema(types);
        }
        throw new SchemaParseException("Schema not yet supported: " + schema);
    }

    static Set<String> parseAliases(JsonNode node) {
        JsonNode aliasesNode = node.get("aliases");
        if (aliasesNode == null) {
            return null;
        }
        if (!aliasesNode.isArray()) {
            throw new SchemaParseException("aliases not an array: " + node);
        }
        LinkedHashSet<String> aliases = new LinkedHashSet<String>();
        for (JsonNode aliasNode : aliasesNode) {
            if (!aliasNode.isTextual()) {
                throw new SchemaParseException("alias not a string: " + aliasNode);
            }
            aliases.add(aliasNode.textValue());
        }
        return aliases;
    }

    private static String getRequiredText(JsonNode container, String key, String error) {
        String out = Schema.getOptionalText(container, key);
        if (null == out) {
            throw new SchemaParseException(error + ": " + container);
        }
        return out;
    }

    private static String getOptionalText(JsonNode container, String key) {
        JsonNode jsonNode = container.get(key);
        return jsonNode != null ? jsonNode.textValue() : null;
    }

    static JsonNode parseJson(String s) {
        try {
            return (JsonNode)MAPPER.readTree(FACTORY.createParser(s));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object parseJsonToObject(String s) {
        return JacksonUtils.toObject(Schema.parseJson(s));
    }

    public static Schema applyAliases(Schema writer, Schema reader) {
        if (writer.equals(reader)) {
            return writer;
        }
        IdentityHashMap<Schema, Schema> seen = new IdentityHashMap<Schema, Schema>(1);
        HashMap<Name, Name> aliases = new HashMap<Name, Name>(1);
        HashMap<Name, Map<String, String>> fieldAliases = new HashMap<Name, Map<String, String>>(1);
        Schema.getAliases(reader, seen, aliases, fieldAliases);
        if (aliases.size() == 0 && fieldAliases.size() == 0) {
            return writer;
        }
        seen.clear();
        return Schema.applyAliases(writer, seen, aliases, fieldAliases);
    }

    private static Schema applyAliases(Schema s, Map<Schema, Schema> seen, Map<Name, Name> aliases, Map<Name, Map<String, String>> fieldAliases) {
        Name name = s instanceof NamedSchema ? ((NamedSchema)s).name : null;
        Schema result = s;
        switch (s.getType()) {
            case RECORD: {
                if (seen.containsKey(s)) {
                    return seen.get(s);
                }
                if (aliases.containsKey(name)) {
                    name = aliases.get(name);
                }
                result = Schema.createRecord(name.full, s.getDoc(), null, s.isError());
                seen.put(s, result);
                ArrayList<Field> newFields = new ArrayList<Field>();
                for (Field f : s.getFields()) {
                    Schema fSchema = Schema.applyAliases(f.schema, seen, aliases, fieldAliases);
                    String fName = Schema.getFieldAlias(name, f.name, fieldAliases);
                    Field newF = new Field(fName, fSchema, f.doc, f.defaultValue, true, f.order);
                    newF.putAll(f);
                    newFields.add(newF);
                }
                result.setFields(newFields);
                break;
            }
            case ENUM: {
                if (!aliases.containsKey(name)) break;
                result = Schema.createEnum(aliases.get(name).full, s.getDoc(), null, s.getEnumSymbols(), s.getEnumDefault());
                break;
            }
            case ARRAY: {
                Schema e = Schema.applyAliases(s.getElementType(), seen, aliases, fieldAliases);
                if (e.equals(s.getElementType())) break;
                result = Schema.createArray(e);
                break;
            }
            case MAP: {
                Schema v = Schema.applyAliases(s.getValueType(), seen, aliases, fieldAliases);
                if (v.equals(s.getValueType())) break;
                result = Schema.createMap(v);
                break;
            }
            case UNION: {
                ArrayList<Schema> types = new ArrayList<Schema>();
                for (Schema branch : s.getTypes()) {
                    types.add(Schema.applyAliases(branch, seen, aliases, fieldAliases));
                }
                result = Schema.createUnion(types);
                break;
            }
            case FIXED: {
                if (!aliases.containsKey(name)) break;
                result = Schema.createFixed(aliases.get(name).full, s.getDoc(), null, s.getFixedSize());
                break;
            }
        }
        if (!result.equals(s)) {
            result.putAll(s);
        }
        return result;
    }

    private static void getAliases(Schema schema, Map<Schema, Schema> seen, Map<Name, Name> aliases, Map<Name, Map<String, String>> fieldAliases) {
        if (schema instanceof NamedSchema) {
            NamedSchema namedSchema = (NamedSchema)schema;
            if (namedSchema.aliases != null) {
                for (Name alias : namedSchema.aliases) {
                    aliases.put(alias, namedSchema.name);
                }
            }
        }
        switch (schema.getType()) {
            case RECORD: {
                if (seen.containsKey(schema)) {
                    return;
                }
                seen.put(schema, schema);
                RecordSchema record = (RecordSchema)schema;
                for (Field field : schema.getFields()) {
                    if (field.aliases != null) {
                        for (String fieldAlias : field.aliases) {
                            Map recordAliases = fieldAliases.computeIfAbsent(record.name, k -> new HashMap());
                            recordAliases.put(fieldAlias, field.name);
                        }
                    }
                    Schema.getAliases(field.schema, seen, aliases, fieldAliases);
                }
                if (record.aliases == null || !fieldAliases.containsKey(record.name)) break;
                for (Name recordAlias : record.aliases) {
                    fieldAliases.put(recordAlias, fieldAliases.get(record.name));
                }
                break;
            }
            case ARRAY: {
                Schema.getAliases(schema.getElementType(), seen, aliases, fieldAliases);
                break;
            }
            case MAP: {
                Schema.getAliases(schema.getValueType(), seen, aliases, fieldAliases);
                break;
            }
            case UNION: {
                for (Schema s : schema.getTypes()) {
                    Schema.getAliases(s, seen, aliases, fieldAliases);
                }
                break;
            }
        }
    }

    private static String getFieldAlias(Name record, String field, Map<Name, Map<String, String>> fieldAliases) {
        Map<String, String> recordAliases = fieldAliases.get(record);
        if (recordAliases == null) {
            return field;
        }
        String alias = recordAliases.get(field);
        if (alias == null) {
            return field;
        }
        return alias;
    }

    static {
        FACTORY.enable(JsonParser.Feature.ALLOW_COMMENTS);
        FACTORY.setCodec(MAPPER);
        SCHEMA_RESERVED = new HashSet<String>(Arrays.asList("doc", "fields", "items", "name", "namespace", "size", "symbols", "values", "type", "aliases"));
        ENUM_RESERVED = new HashSet<String>(SCHEMA_RESERVED);
        ENUM_RESERVED.add("default");
        FIELD_RESERVED = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("default", "doc", "name", "order", "type", "aliases")));
        SEEN_EQUALS = ThreadLocalWithInitial.of(HashSet::new);
        SEEN_HASHCODE = ThreadLocalWithInitial.of(IdentityHashMap::new);
        PRIMITIVES = new HashMap<String, Type>();
        PRIMITIVES.put("string", Type.STRING);
        PRIMITIVES.put("bytes", Type.BYTES);
        PRIMITIVES.put("int", Type.INT);
        PRIMITIVES.put("long", Type.LONG);
        PRIMITIVES.put("float", Type.FLOAT);
        PRIMITIVES.put("double", Type.DOUBLE);
        PRIMITIVES.put("boolean", Type.BOOLEAN);
        PRIMITIVES.put("null", Type.NULL);
        validateNames = ThreadLocalWithInitial.of(() -> true);
        VALIDATE_DEFAULTS = ThreadLocalWithInitial.of(() -> true);
    }

    static class LockableArrayList<E>
    extends ArrayList<E> {
        private static final long serialVersionUID = 1L;
        private boolean locked = false;

        public LockableArrayList() {
        }

        public LockableArrayList(int size) {
            super(size);
        }

        public LockableArrayList(List<E> types) {
            super(types);
        }

        public LockableArrayList(E ... types) {
            super(types.length);
            Collections.addAll(this, types);
        }

        public List<E> lock() {
            this.locked = true;
            return this;
        }

        private void ensureUnlocked() {
            if (this.locked) {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean add(E e) {
            this.ensureUnlocked();
            return super.add(e);
        }

        @Override
        public boolean remove(Object o) {
            this.ensureUnlocked();
            return super.remove(o);
        }

        @Override
        public E remove(int index) {
            this.ensureUnlocked();
            return super.remove(index);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            this.ensureUnlocked();
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            this.ensureUnlocked();
            return super.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            this.ensureUnlocked();
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            this.ensureUnlocked();
            return super.retainAll(c);
        }

        @Override
        public void clear() {
            this.ensureUnlocked();
            super.clear();
        }
    }

    static class Names
    extends LinkedHashMap<Name, Schema> {
        private static final long serialVersionUID = 1L;
        private String space;

        public Names() {
        }

        public Names(String space) {
            this.space = space;
        }

        public String space() {
            return this.space;
        }

        public void space(String space) {
            this.space = space;
        }

        public Schema get(String o) {
            Type primitive = PRIMITIVES.get(o);
            if (primitive != null) {
                return Schema.create(primitive);
            }
            Name name = new Name(o, this.space);
            if (!this.containsKey(name)) {
                name = new Name(o, "");
            }
            return (Schema)super.get(name);
        }

        public boolean contains(Schema schema) {
            return this.get(((NamedSchema)schema).name) != null;
        }

        public void add(Schema schema) {
            this.put(((NamedSchema)schema).name, schema);
        }

        @Override
        public Schema put(Name name, Schema schema) {
            if (this.containsKey(name)) {
                throw new SchemaParseException("Can't redefine: " + name);
            }
            return super.put(name, schema);
        }
    }

    public static class Parser {
        private Names names = new Names();
        private boolean validate = true;
        private boolean validateDefaults = true;

        public Parser addTypes(Map<String, Schema> types) {
            for (Schema s : types.values()) {
                this.names.add(s);
            }
            return this;
        }

        public Map<String, Schema> getTypes() {
            LinkedHashMap<String, Schema> result = new LinkedHashMap<String, Schema>();
            for (Schema s : this.names.values()) {
                result.put(s.getFullName(), s);
            }
            return result;
        }

        public Parser setValidate(boolean validate) {
            this.validate = validate;
            return this;
        }

        public boolean getValidate() {
            return this.validate;
        }

        public Parser setValidateDefaults(boolean validateDefaults) {
            this.validateDefaults = validateDefaults;
            return this;
        }

        public boolean getValidateDefaults() {
            return this.validateDefaults;
        }

        public Schema parse(File file) throws IOException {
            return this.parse(FACTORY.createParser(file), false);
        }

        public Schema parse(InputStream in) throws IOException {
            return this.parse(FACTORY.createParser(in).disable(JsonParser.Feature.AUTO_CLOSE_SOURCE), true);
        }

        public Schema parse(String s, String ... more) {
            StringBuilder b = new StringBuilder(s);
            for (String part : more) {
                b.append(part);
            }
            return this.parse(b.toString());
        }

        public Schema parse(String s) {
            try {
                return this.parse(FACTORY.createParser(s), false);
            }
            catch (IOException e) {
                throw new SchemaParseException(e);
            }
        }

        private Schema parse(JsonParser parser, boolean allowDanglingContent) throws IOException {
            boolean saved = (Boolean)validateNames.get();
            boolean savedValidateDefaults = (Boolean)VALIDATE_DEFAULTS.get();
            try {
                validateNames.set(this.validate);
                VALIDATE_DEFAULTS.set(this.validateDefaults);
                JsonNode jsonNode = (JsonNode)MAPPER.readTree(parser);
                Schema schema = Schema.parse(jsonNode, this.names);
                if (!allowDanglingContent) {
                    String dangling;
                    StringWriter danglingWriter = new StringWriter();
                    int numCharsReleased = parser.releaseBuffered(danglingWriter);
                    if (numCharsReleased == -1) {
                        ByteArrayOutputStream danglingOutputStream = new ByteArrayOutputStream();
                        parser.releaseBuffered(danglingOutputStream);
                        dangling = new String(danglingOutputStream.toByteArray(), StandardCharsets.UTF_8).trim();
                    } else {
                        dangling = danglingWriter.toString().trim();
                    }
                    if (!dangling.isEmpty()) {
                        throw new SchemaParseException("dangling content after end of schema: " + dangling);
                    }
                }
                Schema schema2 = schema;
                return schema2;
            }
            catch (JsonParseException e) {
                throw new SchemaParseException(e);
            }
            finally {
                parser.close();
                validateNames.set(saved);
                VALIDATE_DEFAULTS.set(savedValidateDefaults);
            }
        }
    }

    private static class NullSchema
    extends Schema {
        public NullSchema() {
            super(Type.NULL);
        }
    }

    private static class BooleanSchema
    extends Schema {
        public BooleanSchema() {
            super(Type.BOOLEAN);
        }
    }

    private static class DoubleSchema
    extends Schema {
        public DoubleSchema() {
            super(Type.DOUBLE);
        }
    }

    private static class FloatSchema
    extends Schema {
        public FloatSchema() {
            super(Type.FLOAT);
        }
    }

    private static class LongSchema
    extends Schema {
        public LongSchema() {
            super(Type.LONG);
        }
    }

    private static class IntSchema
    extends Schema {
        public IntSchema() {
            super(Type.INT);
        }
    }

    private static class BytesSchema
    extends Schema {
        public BytesSchema() {
            super(Type.BYTES);
        }
    }

    private static class StringSchema
    extends Schema {
        public StringSchema() {
            super(Type.STRING);
        }
    }

    private static class FixedSchema
    extends NamedSchema {
        private final int size;

        public FixedSchema(Name name, String doc, int size) {
            super(Type.FIXED, name, doc);
            SystemLimitException.checkMaxBytesLength(size);
            this.size = size;
        }

        @Override
        public int getFixedSize() {
            return this.size;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof FixedSchema)) {
                return false;
            }
            FixedSchema that = (FixedSchema)o;
            return this.equalCachedHash(that) && this.equalNames(that) && this.size == that.size && this.propsEqual(that);
        }

        @Override
        int computeHash() {
            return super.computeHash() + this.size;
        }

        @Override
        void toJson(Names names, JsonGenerator gen) throws IOException {
            if (this.writeNameRef(names, gen)) {
                return;
            }
            gen.writeStartObject();
            gen.writeStringField("type", "fixed");
            this.writeName(names, gen);
            if (this.getDoc() != null) {
                gen.writeStringField("doc", this.getDoc());
            }
            gen.writeNumberField("size", this.size);
            this.writeProps(gen);
            this.aliasesToJson(gen);
            gen.writeEndObject();
        }
    }

    private static class UnionSchema
    extends Schema {
        private final List<Schema> types;
        private final Map<String, Integer> indexByName;

        public UnionSchema(LockableArrayList<Schema> types) {
            super(Type.UNION);
            this.indexByName = new HashMap<String, Integer>(Math.multiplyExact(2, types.size()));
            this.types = types.lock();
            int index = 0;
            for (Schema type : types) {
                if (type.getType() == Type.UNION) {
                    throw new AvroRuntimeException("Nested union: " + this);
                }
                String name = type.getFullName();
                if (name == null) {
                    throw new AvroRuntimeException("Nameless in union:" + this);
                }
                if (this.indexByName.put(name, index++) == null) continue;
                throw new AvroRuntimeException("Duplicate in union:" + name);
            }
        }

        @Override
        public List<Schema> getTypes() {
            return this.types;
        }

        @Override
        public Integer getIndexNamed(String name) {
            return this.indexByName.get(name);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof UnionSchema)) {
                return false;
            }
            UnionSchema that = (UnionSchema)o;
            return this.equalCachedHash(that) && this.types.equals(that.types) && this.propsEqual(that);
        }

        @Override
        int computeHash() {
            int hash = super.computeHash();
            for (Schema type : this.types) {
                hash += type.computeHash();
            }
            return hash;
        }

        @Override
        public void addProp(String name, String value) {
            throw new AvroRuntimeException("Can't set properties on a union: " + this);
        }

        @Override
        void toJson(Names names, JsonGenerator gen) throws IOException {
            gen.writeStartArray();
            for (Schema type : this.types) {
                type.toJson(names, gen);
            }
            gen.writeEndArray();
        }
    }

    private static class MapSchema
    extends Schema {
        private final Schema valueType;

        public MapSchema(Schema valueType) {
            super(Type.MAP);
            this.valueType = valueType;
        }

        @Override
        public Schema getValueType() {
            return this.valueType;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MapSchema)) {
                return false;
            }
            MapSchema that = (MapSchema)o;
            return this.equalCachedHash(that) && this.valueType.equals(that.valueType) && this.propsEqual(that);
        }

        @Override
        int computeHash() {
            return super.computeHash() + this.valueType.computeHash();
        }

        @Override
        void toJson(Names names, JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("type", "map");
            gen.writeFieldName("values");
            this.valueType.toJson(names, gen);
            this.writeProps(gen);
            gen.writeEndObject();
        }
    }

    private static class ArraySchema
    extends Schema {
        private final Schema elementType;

        public ArraySchema(Schema elementType) {
            super(Type.ARRAY);
            this.elementType = elementType;
        }

        @Override
        public Schema getElementType() {
            return this.elementType;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ArraySchema)) {
                return false;
            }
            ArraySchema that = (ArraySchema)o;
            return this.equalCachedHash(that) && this.elementType.equals(that.elementType) && this.propsEqual(that);
        }

        @Override
        int computeHash() {
            return super.computeHash() + this.elementType.computeHash();
        }

        @Override
        void toJson(Names names, JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("type", "array");
            gen.writeFieldName("items");
            this.elementType.toJson(names, gen);
            this.writeProps(gen);
            gen.writeEndObject();
        }
    }

    private static class EnumSchema
    extends NamedSchema {
        private final List<String> symbols;
        private final Map<String, Integer> ordinals;
        private final String enumDefault;

        public EnumSchema(Name name, String doc, LockableArrayList<String> symbols, String enumDefault) {
            super(Type.ENUM, name, doc);
            this.symbols = symbols.lock();
            this.ordinals = new HashMap<String, Integer>(Math.multiplyExact(2, symbols.size()));
            this.enumDefault = enumDefault;
            int i = 0;
            for (String symbol : symbols) {
                if (this.ordinals.put(Schema.validateName(symbol), i++) == null) continue;
                throw new SchemaParseException("Duplicate enum symbol: " + symbol);
            }
            if (enumDefault != null && !symbols.contains(enumDefault)) {
                throw new SchemaParseException("The Enum Default: " + enumDefault + " is not in the enum symbol set: " + symbols);
            }
        }

        @Override
        public List<String> getEnumSymbols() {
            return this.symbols;
        }

        @Override
        public boolean hasEnumSymbol(String symbol) {
            return this.ordinals.containsKey(symbol);
        }

        @Override
        public int getEnumOrdinal(String symbol) {
            return this.ordinals.get(symbol);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof EnumSchema)) {
                return false;
            }
            EnumSchema that = (EnumSchema)o;
            return this.equalCachedHash(that) && this.equalNames(that) && this.symbols.equals(that.symbols) && this.propsEqual(that);
        }

        @Override
        public String getEnumDefault() {
            return this.enumDefault;
        }

        @Override
        int computeHash() {
            return super.computeHash() + this.symbols.hashCode();
        }

        @Override
        void toJson(Names names, JsonGenerator gen) throws IOException {
            if (this.writeNameRef(names, gen)) {
                return;
            }
            gen.writeStartObject();
            gen.writeStringField("type", "enum");
            this.writeName(names, gen);
            if (this.getDoc() != null) {
                gen.writeStringField("doc", this.getDoc());
            }
            gen.writeArrayFieldStart("symbols");
            for (String symbol : this.symbols) {
                gen.writeString(symbol);
            }
            gen.writeEndArray();
            if (this.getEnumDefault() != null) {
                gen.writeStringField("default", this.getEnumDefault());
            }
            this.writeProps(gen);
            this.aliasesToJson(gen);
            gen.writeEndObject();
        }
    }

    private static class RecordSchema
    extends NamedSchema {
        private List<Field> fields;
        private Map<String, Field> fieldMap;
        private final boolean isError;

        public RecordSchema(Name name, String doc, boolean isError) {
            super(Type.RECORD, name, doc);
            this.isError = isError;
        }

        public RecordSchema(Name name, String doc, boolean isError, List<Field> fields) {
            super(Type.RECORD, name, doc);
            this.isError = isError;
            this.setFields(fields);
        }

        @Override
        public boolean isError() {
            return this.isError;
        }

        @Override
        public Field getField(String fieldname) {
            if (this.fieldMap == null) {
                throw new AvroRuntimeException("Schema fields not set yet");
            }
            return this.fieldMap.get(fieldname);
        }

        @Override
        public List<Field> getFields() {
            if (this.fields == null) {
                throw new AvroRuntimeException("Schema fields not set yet");
            }
            return this.fields;
        }

        @Override
        public boolean hasFields() {
            return this.fields != null;
        }

        @Override
        public void setFields(List<Field> fields) {
            if (this.fields != null) {
                throw new AvroRuntimeException("Fields are already set");
            }
            int i = 0;
            this.fieldMap = new HashMap<String, Field>(Math.multiplyExact(2, fields.size()));
            LockableArrayList<Field> ff = new LockableArrayList<Field>(fields.size());
            for (Field f : fields) {
                if (f.position != -1) {
                    throw new AvroRuntimeException("Field already used: " + f);
                }
                f.position = i++;
                Field existingField = this.fieldMap.put(f.name(), f);
                if (existingField != null) {
                    throw new AvroRuntimeException(String.format("Duplicate field %s in record %s: %s and %s.", f.name(), this.name, f, existingField));
                }
                ff.add(f);
            }
            this.fields = ff.lock();
            this.hashCode = Integer.MIN_VALUE;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o) {
            SeenPair here;
            if (o == this) {
                return true;
            }
            if (!(o instanceof RecordSchema)) {
                return false;
            }
            RecordSchema that = (RecordSchema)o;
            if (!this.equalCachedHash(that)) {
                return false;
            }
            if (!this.equalNames(that)) {
                return false;
            }
            if (!this.propsEqual(that)) {
                return false;
            }
            Set seen = (Set)SEEN_EQUALS.get();
            if (seen.contains(here = new SeenPair(this, o))) {
                return true;
            }
            boolean first = seen.isEmpty();
            try {
                seen.add(here);
                boolean bl = Objects.equals(this.fields, that.fields);
                return bl;
            }
            finally {
                if (first) {
                    seen.clear();
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        int computeHash() {
            Map seen = (Map)SEEN_HASHCODE.get();
            if (seen.containsKey(this)) {
                return 0;
            }
            boolean first = seen.isEmpty();
            try {
                seen.put(this, this);
                int n = super.computeHash() + this.fields.hashCode();
                return n;
            }
            finally {
                if (first) {
                    seen.clear();
                }
            }
        }

        @Override
        void toJson(Names names, JsonGenerator gen) throws IOException {
            if (this.writeNameRef(names, gen)) {
                return;
            }
            String savedSpace = names.space;
            gen.writeStartObject();
            gen.writeStringField("type", this.isError ? "error" : "record");
            this.writeName(names, gen);
            names.space = this.name.space;
            if (this.getDoc() != null) {
                gen.writeStringField("doc", this.getDoc());
            }
            if (this.fields != null) {
                gen.writeFieldName("fields");
                this.fieldsToJson(names, gen);
            }
            this.writeProps(gen);
            this.aliasesToJson(gen);
            gen.writeEndObject();
            names.space = savedSpace;
        }

        @Override
        void fieldsToJson(Names names, JsonGenerator gen) throws IOException {
            gen.writeStartArray();
            for (Field f : this.fields) {
                gen.writeStartObject();
                gen.writeStringField("name", f.name());
                gen.writeFieldName("type");
                f.schema().toJson(names, gen);
                if (f.doc() != null) {
                    gen.writeStringField("doc", f.doc());
                }
                if (f.hasDefaultValue()) {
                    gen.writeFieldName("default");
                    gen.writeTree(f.defaultValue());
                }
                if (f.order() != Field.Order.ASCENDING) {
                    gen.writeStringField("order", f.order().name);
                }
                if (f.aliases != null && f.aliases.size() != 0) {
                    gen.writeFieldName("aliases");
                    gen.writeStartArray();
                    for (String alias : f.aliases) {
                        gen.writeString(alias);
                    }
                    gen.writeEndArray();
                }
                f.writeProps(gen);
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }

    public static class SeenPair {
        private Object s1;
        private Object s2;

        public SeenPair(Object s1, Object s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        public boolean equals(Object o) {
            if (!(o instanceof SeenPair)) {
                return false;
            }
            return this.s1 == ((SeenPair)o).s1 && this.s2 == ((SeenPair)o).s2;
        }

        public int hashCode() {
            return System.identityHashCode(this.s1) + System.identityHashCode(this.s2);
        }
    }

    private static abstract class NamedSchema
    extends Schema {
        final Name name;
        final String doc;
        Set<Name> aliases;

        public NamedSchema(Type type, Name name, String doc) {
            super(type);
            this.name = name;
            this.doc = doc;
            if (PRIMITIVES.containsKey(name.full)) {
                throw new AvroTypeException("Schemas may not be named after primitives: " + name.full);
            }
        }

        @Override
        public String getName() {
            return this.name.name;
        }

        @Override
        public String getDoc() {
            return this.doc;
        }

        @Override
        public String getNamespace() {
            return this.name.space;
        }

        @Override
        public String getFullName() {
            return this.name.full;
        }

        @Override
        public void addAlias(String alias) {
            this.addAlias(alias, null);
        }

        @Override
        public void addAlias(String name, String space) {
            if (this.aliases == null) {
                this.aliases = new LinkedHashSet<Name>();
            }
            if (space == null) {
                space = this.name.space;
            }
            this.aliases.add(new Name(name, space));
        }

        @Override
        public Set<String> getAliases() {
            LinkedHashSet<String> result = new LinkedHashSet<String>();
            if (this.aliases != null) {
                for (Name alias : this.aliases) {
                    result.add(alias.full);
                }
            }
            return result;
        }

        public boolean writeNameRef(Names names, JsonGenerator gen) throws IOException {
            if (this.equals(names.get(this.name))) {
                gen.writeString(this.name.getQualified(names.space()));
                return true;
            }
            if (this.name.name != null) {
                names.put(this.name, this);
            }
            return false;
        }

        public void writeName(Names names, JsonGenerator gen) throws IOException {
            this.name.writeName(names, gen);
        }

        public boolean equalNames(NamedSchema that) {
            return this.name.equals(that.name);
        }

        @Override
        int computeHash() {
            return super.computeHash() + this.name.hashCode();
        }

        public void aliasesToJson(JsonGenerator gen) throws IOException {
            if (this.aliases == null || this.aliases.isEmpty()) {
                return;
            }
            gen.writeFieldName("aliases");
            gen.writeStartArray();
            for (Name alias : this.aliases) {
                gen.writeString(alias.getQualified(this.name.space));
            }
            gen.writeEndArray();
        }
    }

    static class Name {
        private final String name;
        private final String space;
        private final String full;

        public Name(String name, String space) {
            if (name == null) {
                this.full = null;
                this.space = null;
                this.name = null;
                return;
            }
            int lastDot = name.lastIndexOf(46);
            if (lastDot < 0) {
                this.name = Schema.validateName(name);
            } else {
                space = name.substring(0, lastDot);
                this.name = Schema.validateName(name.substring(lastDot + 1));
            }
            if ("".equals(space)) {
                space = null;
            }
            this.space = space;
            this.full = this.space == null ? this.name : this.space + "." + this.name;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Name)) {
                return false;
            }
            Name that = (Name)o;
            return Objects.equals(this.full, that.full);
        }

        public int hashCode() {
            return this.full == null ? 0 : this.full.hashCode();
        }

        public String toString() {
            return this.full;
        }

        public void writeName(Names names, JsonGenerator gen) throws IOException {
            if (this.name != null) {
                gen.writeStringField("name", this.name);
            }
            if (this.space != null) {
                if (!this.space.equals(names.space())) {
                    gen.writeStringField("namespace", this.space);
                }
            } else if (names.space() != null) {
                gen.writeStringField("namespace", "");
            }
        }

        public String getQualified(String defaultSpace) {
            return this.shouldWriteFull(defaultSpace) ? this.full : this.name;
        }

        private boolean shouldWriteFull(String defaultSpace) {
            if (this.space != null && this.space.equals(defaultSpace)) {
                for (Type schemaType : Type.values()) {
                    if (!schemaType.name.equals(this.name)) continue;
                    return true;
                }
                return false;
            }
            return true;
        }
    }

    public static class Field
    extends JsonProperties {
        public static final Object NULL_DEFAULT_VALUE;
        private final String name;
        private int position = -1;
        private final Schema schema;
        private final String doc;
        private final JsonNode defaultValue;
        private final Order order;
        private Set<String> aliases;

        Field(String name, Schema schema, String doc, JsonNode defaultValue, boolean validateDefault, Order order) {
            super(FIELD_RESERVED);
            this.name = Schema.validateName(name);
            this.schema = Objects.requireNonNull(schema, "schema is required and cannot be null");
            this.doc = doc;
            this.defaultValue = validateDefault ? Schema.validateDefault(name, schema, defaultValue) : defaultValue;
            this.order = Objects.requireNonNull(order, "Order cannot be null");
        }

        public Field(Field field, Schema schema) {
            this(field.name, schema, field.doc, field.defaultValue, true, field.order);
            this.putAll(field);
            if (field.aliases != null) {
                this.aliases = new LinkedHashSet<String>(field.aliases);
            }
        }

        public Field(String name, Schema schema) {
            this(name, schema, null, null, true, Order.ASCENDING);
        }

        public Field(String name, Schema schema, String doc) {
            this(name, schema, doc, null, true, Order.ASCENDING);
        }

        public Field(String name, Schema schema, String doc, Object defaultValue) {
            this(name, schema, doc, defaultValue == NULL_DEFAULT_VALUE ? NullNode.getInstance() : JacksonUtils.toJsonNode(defaultValue), true, Order.ASCENDING);
        }

        public Field(String name, Schema schema, String doc, Object defaultValue, Order order) {
            this(name, schema, doc, defaultValue == NULL_DEFAULT_VALUE ? NullNode.getInstance() : JacksonUtils.toJsonNode(defaultValue), true, Objects.requireNonNull(order));
        }

        public String name() {
            return this.name;
        }

        public int pos() {
            return this.position;
        }

        public Schema schema() {
            return this.schema;
        }

        public String doc() {
            return this.doc;
        }

        public boolean hasDefaultValue() {
            return this.defaultValue != null;
        }

        JsonNode defaultValue() {
            return this.defaultValue;
        }

        public Object defaultVal() {
            return JacksonUtils.toObject(this.defaultValue, this.schema);
        }

        public Order order() {
            return this.order;
        }

        public void addAlias(String alias) {
            if (this.aliases == null) {
                this.aliases = new LinkedHashSet<String>();
            }
            this.aliases.add(alias);
        }

        public Set<String> aliases() {
            if (this.aliases == null) {
                return Collections.emptySet();
            }
            return Collections.unmodifiableSet(this.aliases);
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof Field)) {
                return false;
            }
            Field that = (Field)other;
            return this.name.equals(that.name) && this.schema.equals(that.schema) && this.defaultValueEquals(that.defaultValue) && this.order == that.order && this.propsEqual(that);
        }

        public int hashCode() {
            return this.name.hashCode() + this.schema.computeHash();
        }

        private boolean defaultValueEquals(JsonNode thatDefaultValue) {
            if (this.defaultValue == null) {
                return thatDefaultValue == null;
            }
            if (thatDefaultValue == null) {
                return false;
            }
            if (Double.isNaN(this.defaultValue.doubleValue())) {
                return Double.isNaN(thatDefaultValue.doubleValue());
            }
            return this.defaultValue.equals(thatDefaultValue);
        }

        public String toString() {
            return this.name + " type:" + (Object)((Object)this.schema.type) + " pos:" + this.position;
        }

        static {
            Accessor.setAccessor(new Accessor.FieldAccessor(){

                @Override
                protected JsonNode defaultValue(Field field) {
                    return field.defaultValue();
                }

                @Override
                protected Field createField(String name, Schema schema, String doc, JsonNode defaultValue) {
                    return new Field(name, schema, doc, defaultValue, true, Order.ASCENDING);
                }

                @Override
                protected Field createField(String name, Schema schema, String doc, JsonNode defaultValue, boolean validate, Order order) {
                    return new Field(name, schema, doc, defaultValue, validate, order);
                }
            });
            NULL_DEFAULT_VALUE = new Object();
        }

        public static enum Order {
            ASCENDING,
            DESCENDING,
            IGNORE;

            private final String name = this.name().toLowerCase(Locale.ENGLISH);
        }
    }

    public static enum Type {
        RECORD,
        ENUM,
        ARRAY,
        MAP,
        UNION,
        FIXED,
        STRING,
        BYTES,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        NULL;

        private final String name = this.name().toLowerCase(Locale.ENGLISH);

        public String getName() {
            return this.name;
        }
    }

    private static final class SerializableSchema
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private String schemaString;

        private SerializableSchema() {
        }

        private Object readResolve() {
            return new Parser().parse(this.schemaString);
        }
    }
}

