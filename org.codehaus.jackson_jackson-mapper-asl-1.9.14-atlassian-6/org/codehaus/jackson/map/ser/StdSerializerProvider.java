/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.ser;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.ContextualSerializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerFactory;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.impl.FailingSerializer;
import org.codehaus.jackson.map.ser.impl.ReadOnlyClassToSerializerMap;
import org.codehaus.jackson.map.ser.impl.SerializerCache;
import org.codehaus.jackson.map.ser.impl.UnknownSerializer;
import org.codehaus.jackson.map.ser.std.NullSerializer;
import org.codehaus.jackson.map.ser.std.StdKeySerializer;
import org.codehaus.jackson.map.ser.std.StdKeySerializers;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.RootNameLookup;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.schema.SchemaAware;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdSerializerProvider
extends SerializerProvider {
    static final boolean CACHE_UNKNOWN_MAPPINGS = false;
    public static final JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER = new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");
    @Deprecated
    public static final JsonSerializer<Object> DEFAULT_KEY_SERIALIZER = new StdKeySerializer();
    public static final JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER = new UnknownSerializer();
    protected final SerializerFactory _serializerFactory;
    protected final SerializerCache _serializerCache;
    protected final RootNameLookup _rootNames;
    protected JsonSerializer<Object> _unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _nullValueSerializer = NullSerializer.instance;
    protected JsonSerializer<Object> _nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
    protected final ReadOnlyClassToSerializerMap _knownSerializers;
    protected DateFormat _dateFormat;

    public StdSerializerProvider() {
        super(null);
        this._serializerFactory = null;
        this._serializerCache = new SerializerCache();
        this._knownSerializers = null;
        this._rootNames = new RootNameLookup();
    }

    protected StdSerializerProvider(SerializationConfig config, StdSerializerProvider src, SerializerFactory f) {
        super(config);
        if (config == null) {
            throw new NullPointerException();
        }
        this._serializerFactory = f;
        this._serializerCache = src._serializerCache;
        this._unknownTypeSerializer = src._unknownTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._nullValueSerializer = src._nullValueSerializer;
        this._nullKeySerializer = src._nullKeySerializer;
        this._rootNames = src._rootNames;
        this._knownSerializers = this._serializerCache.getReadOnlyLookupMap();
    }

    protected StdSerializerProvider createInstance(SerializationConfig config, SerializerFactory jsf) {
        return new StdSerializerProvider(config, this, jsf);
    }

    @Override
    public void setDefaultKeySerializer(JsonSerializer<Object> ks) {
        if (ks == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._keySerializer = ks;
    }

    @Override
    public void setNullValueSerializer(JsonSerializer<Object> nvs) {
        if (nvs == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._nullValueSerializer = nvs;
    }

    @Override
    public void setNullKeySerializer(JsonSerializer<Object> nks) {
        if (nks == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._nullKeySerializer = nks;
    }

    @Override
    public final void serializeValue(SerializationConfig config, JsonGenerator jgen, Object value, SerializerFactory jsf) throws IOException, JsonGenerationException {
        if (jsf == null) {
            throw new IllegalArgumentException("Can not pass null serializerFactory");
        }
        StdSerializerProvider inst = this.createInstance(config, jsf);
        if (inst.getClass() != this.getClass()) {
            throw new IllegalStateException("Broken serializer provider: createInstance returned instance of type " + inst.getClass() + "; blueprint of type " + this.getClass());
        }
        inst._serializeValue(jgen, value);
    }

    @Override
    public final void serializeValue(SerializationConfig config, JsonGenerator jgen, Object value, JavaType rootType, SerializerFactory jsf) throws IOException, JsonGenerationException {
        if (jsf == null) {
            throw new IllegalArgumentException("Can not pass null serializerFactory");
        }
        StdSerializerProvider inst = this.createInstance(config, jsf);
        if (inst.getClass() != this.getClass()) {
            throw new IllegalStateException("Broken serializer provider: createInstance returned instance of type " + inst.getClass() + "; blueprint of type " + this.getClass());
        }
        inst._serializeValue(jgen, value, rootType);
    }

    @Override
    public JsonSchema generateJsonSchema(Class<?> type, SerializationConfig config, SerializerFactory jsf) throws JsonMappingException {
        JsonNode schemaNode;
        if (type == null) {
            throw new IllegalArgumentException("A class must be provided");
        }
        StdSerializerProvider inst = this.createInstance(config, jsf);
        if (inst.getClass() != this.getClass()) {
            throw new IllegalStateException("Broken serializer provider: createInstance returned instance of type " + inst.getClass() + "; blueprint of type " + this.getClass());
        }
        JsonSerializer<Object> ser = inst.findValueSerializer(type, null);
        JsonNode jsonNode = schemaNode = ser instanceof SchemaAware ? ((SchemaAware)((Object)ser)).getSchema(inst, null) : JsonSchema.getDefaultSchemaNode();
        if (!(schemaNode instanceof ObjectNode)) {
            throw new IllegalArgumentException("Class " + type.getName() + " would not be serialized as a JSON object and therefore has no schema");
        }
        return new JsonSchema((ObjectNode)schemaNode);
    }

    @Override
    public boolean hasSerializerFor(SerializationConfig config, Class<?> cls, SerializerFactory jsf) {
        return this.createInstance(config, jsf)._findExplicitUntypedSerializer(cls, null) != null;
    }

    @Override
    public int cachedSerializersCount() {
        return this._serializerCache.size();
    }

    @Override
    public void flushCachedSerializers() {
        this._serializerCache.flush();
    }

    @Override
    public JsonSerializer<Object> findValueSerializer(Class<?> valueType, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null && (ser = this._serializerCache.untypedValueSerializer(valueType)) == null && (ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType))) == null && (ser = this._createAndCacheUntypedSerializer(valueType, property)) == null) {
            ser = this.getUnknownTypeSerializer(valueType);
            return ser;
        }
        return this._handleContextualResolvable(ser, property);
    }

    @Override
    public JsonSerializer<Object> findValueSerializer(JavaType valueType, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null && (ser = this._serializerCache.untypedValueSerializer(valueType)) == null && (ser = this._createAndCacheUntypedSerializer(valueType, property)) == null) {
            ser = this.getUnknownTypeSerializer(valueType.getRawClass());
            return ser;
        }
        return this._handleContextualResolvable(ser, property);
    }

    @Override
    public JsonSerializer<Object> findTypedValueSerializer(Class<?> valueType, boolean cache, BeanProperty property) throws JsonMappingException {
        JsonSerializer ser = this._knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this.findValueSerializer(valueType, property);
        TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, this._config.constructType(valueType), property);
        if (typeSer != null) {
            ser = new WrappedSerializer(typeSer, ser);
        }
        if (cache) {
            this._serializerCache.addTypedSerializer(valueType, (JsonSerializer<Object>)ser);
        }
        return ser;
    }

    @Override
    public JsonSerializer<Object> findTypedValueSerializer(JavaType valueType, boolean cache, BeanProperty property) throws JsonMappingException {
        JsonSerializer ser = this._knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this.findValueSerializer(valueType, property);
        TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, valueType, property);
        if (typeSer != null) {
            ser = new WrappedSerializer(typeSer, ser);
        }
        if (cache) {
            this._serializerCache.addTypedSerializer(valueType, (JsonSerializer<Object>)ser);
        }
        return ser;
    }

    @Override
    public JsonSerializer<Object> findKeySerializer(JavaType keyType, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._serializerFactory.createKeySerializer(this._config, keyType, property);
        if (ser == null) {
            ser = this._keySerializer == null ? StdKeySerializers.getStdKeySerializer(keyType) : this._keySerializer;
        }
        if (ser instanceof ContextualSerializer) {
            ContextualSerializer contextual = (ContextualSerializer)((Object)ser);
            ser = contextual.createContextual(this._config, property);
        }
        return ser;
    }

    @Override
    public JsonSerializer<Object> getNullKeySerializer() {
        return this._nullKeySerializer;
    }

    @Override
    public JsonSerializer<Object> getNullValueSerializer() {
        return this._nullValueSerializer;
    }

    @Override
    public JsonSerializer<Object> getUnknownTypeSerializer(Class<?> unknownType) {
        return this._unknownTypeSerializer;
    }

    @Override
    public final void defaultSerializeDateValue(long timestamp, JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS)) {
            jgen.writeNumber(timestamp);
        } else {
            if (this._dateFormat == null) {
                this._dateFormat = (DateFormat)this._config.getDateFormat().clone();
            }
            jgen.writeString(this._dateFormat.format(new Date(timestamp)));
        }
    }

    @Override
    public final void defaultSerializeDateValue(Date date, JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS)) {
            jgen.writeNumber(date.getTime());
        } else {
            if (this._dateFormat == null) {
                DateFormat blueprint = this._config.getDateFormat();
                this._dateFormat = (DateFormat)blueprint.clone();
            }
            jgen.writeString(this._dateFormat.format(date));
        }
    }

    @Override
    public void defaultSerializeDateKey(long timestamp, JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this.isEnabled(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            jgen.writeFieldName(String.valueOf(timestamp));
        } else {
            if (this._dateFormat == null) {
                DateFormat blueprint = this._config.getDateFormat();
                this._dateFormat = (DateFormat)blueprint.clone();
            }
            jgen.writeFieldName(this._dateFormat.format(new Date(timestamp)));
        }
    }

    @Override
    public void defaultSerializeDateKey(Date date, JsonGenerator jgen) throws IOException, JsonProcessingException {
        if (this.isEnabled(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            jgen.writeFieldName(String.valueOf(date.getTime()));
        } else {
            if (this._dateFormat == null) {
                DateFormat blueprint = this._config.getDateFormat();
                this._dateFormat = (DateFormat)blueprint.clone();
            }
            jgen.writeFieldName(this._dateFormat.format(date));
        }
    }

    protected void _serializeValue(JsonGenerator jgen, Object value) throws IOException, JsonProcessingException {
        boolean wrap;
        JsonSerializer<Object> ser;
        if (value == null) {
            ser = this.getNullValueSerializer();
            wrap = false;
        } else {
            Class<?> cls = value.getClass();
            ser = this.findTypedValueSerializer(cls, true, null);
            wrap = this._config.isEnabled(SerializationConfig.Feature.WRAP_ROOT_VALUE);
            if (wrap) {
                jgen.writeStartObject();
                jgen.writeFieldName(this._rootNames.findRootName(value.getClass(), this._config));
            }
        }
        try {
            ser.serialize(value, jgen, this);
            if (wrap) {
                jgen.writeEndObject();
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "[no message for " + e.getClass().getName() + "]";
            }
            throw new JsonMappingException(msg, e);
        }
    }

    protected void _serializeValue(JsonGenerator jgen, Object value, JavaType rootType) throws IOException, JsonProcessingException {
        boolean wrap;
        JsonSerializer<Object> ser;
        if (value == null) {
            ser = this.getNullValueSerializer();
            wrap = false;
        } else {
            if (!rootType.getRawClass().isAssignableFrom(value.getClass())) {
                this._reportIncompatibleRootType(value, rootType);
            }
            ser = this.findTypedValueSerializer(rootType, true, null);
            wrap = this._config.isEnabled(SerializationConfig.Feature.WRAP_ROOT_VALUE);
            if (wrap) {
                jgen.writeStartObject();
                jgen.writeFieldName(this._rootNames.findRootName(rootType, this._config));
            }
        }
        try {
            ser.serialize(value, jgen, this);
            if (wrap) {
                jgen.writeEndObject();
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = "[no message for " + e.getClass().getName() + "]";
            }
            throw new JsonMappingException(msg, e);
        }
    }

    protected void _reportIncompatibleRootType(Object value, JavaType rootType) throws IOException, JsonProcessingException {
        Class<?> wrapperType;
        if (rootType.isPrimitive() && (wrapperType = ClassUtil.wrapperType(rootType.getRawClass())).isAssignableFrom(value.getClass())) {
            return;
        }
        throw new JsonMappingException("Incompatible types: declared root type (" + rootType + ") vs " + value.getClass().getName());
    }

    protected JsonSerializer<Object> _findExplicitUntypedSerializer(Class<?> runtimeType, BeanProperty property) {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(runtimeType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.untypedValueSerializer(runtimeType);
        if (ser != null) {
            return ser;
        }
        try {
            return this._createAndCacheUntypedSerializer(runtimeType, property);
        }
        catch (Exception e) {
            return null;
        }
    }

    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(Class<?> type, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser;
        try {
            ser = this._createUntypedSerializer(this._config.constructType(type), property);
        }
        catch (IllegalArgumentException iae) {
            throw new JsonMappingException(iae.getMessage(), null, iae);
        }
        if (ser != null) {
            this._serializerCache.addAndResolveNonTypedSerializer(type, ser, (SerializerProvider)this);
        }
        return ser;
    }

    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(JavaType type, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser;
        try {
            ser = this._createUntypedSerializer(type, property);
        }
        catch (IllegalArgumentException iae) {
            throw new JsonMappingException(iae.getMessage(), null, iae);
        }
        if (ser != null) {
            this._serializerCache.addAndResolveNonTypedSerializer(type, ser, (SerializerProvider)this);
        }
        return ser;
    }

    protected JsonSerializer<Object> _createUntypedSerializer(JavaType type, BeanProperty property) throws JsonMappingException {
        return this._serializerFactory.createSerializer(this._config, type, property);
    }

    protected JsonSerializer<Object> _handleContextualResolvable(JsonSerializer<Object> ser, BeanProperty property) throws JsonMappingException {
        if (!(ser instanceof ContextualSerializer)) {
            return ser;
        }
        JsonSerializer ctxtSer = ((ContextualSerializer)((Object)ser)).createContextual(this._config, property);
        if (ctxtSer != ser) {
            if (ctxtSer instanceof ResolvableSerializer) {
                ((ResolvableSerializer)((Object)ctxtSer)).resolve(this);
            }
            ser = ctxtSer;
        }
        return ser;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class WrappedSerializer
    extends JsonSerializer<Object> {
        protected final TypeSerializer _typeSerializer;
        protected final JsonSerializer<Object> _serializer;

        public WrappedSerializer(TypeSerializer typeSer, JsonSerializer<Object> ser) {
            this._typeSerializer = typeSer;
            this._serializer = ser;
        }

        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            this._serializer.serializeWithType(value, jgen, provider, this._typeSerializer);
        }

        @Override
        public void serializeWithType(Object value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
            this._serializer.serializeWithType(value, jgen, provider, typeSer);
        }

        @Override
        public Class<Object> handledType() {
            return Object.class;
        }
    }
}

