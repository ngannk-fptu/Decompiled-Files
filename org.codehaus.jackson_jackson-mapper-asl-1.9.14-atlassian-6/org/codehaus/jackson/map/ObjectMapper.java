/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.FormatSchema
 *  org.codehaus.jackson.JsonEncoding
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonGenerator$Feature
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParseException
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonParser$Feature
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.ObjectCodec
 *  org.codehaus.jackson.PrettyPrinter
 *  org.codehaus.jackson.Version
 *  org.codehaus.jackson.Versioned
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonMethod
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.io.SegmentedStringWriter
 *  org.codehaus.jackson.io.SerializedString
 *  org.codehaus.jackson.type.JavaType
 *  org.codehaus.jackson.type.TypeReference
 *  org.codehaus.jackson.util.ByteArrayBuilder
 *  org.codehaus.jackson.util.DefaultPrettyPrinter
 *  org.codehaus.jackson.util.TokenBuffer
 *  org.codehaus.jackson.util.VersionUtil
 */
package org.codehaus.jackson.map;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.DateFormat;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.jackson.FormatSchema;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.Versioned;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.io.SegmentedStringWriter;
import org.codehaus.jackson.io.SerializedString;
import org.codehaus.jackson.map.AbstractTypeResolver;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.ClassIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.HandlerInstantiator;
import org.codehaus.jackson.map.InjectableValues;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializers;
import org.codehaus.jackson.map.MappingIterator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerFactory;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import org.codehaus.jackson.map.deser.StdDeserializationContext;
import org.codehaus.jackson.map.deser.StdDeserializerProvider;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.codehaus.jackson.map.introspect.BasicClassIntrospector;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.SubtypeResolver;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.jsontype.impl.StdSubtypeResolver;
import org.codehaus.jackson.map.jsontype.impl.StdTypeResolverBuilder;
import org.codehaus.jackson.map.ser.BeanSerializerFactory;
import org.codehaus.jackson.map.ser.BeanSerializerModifier;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.codehaus.jackson.map.type.SimpleType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.type.TypeModifier;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TreeTraversingParser;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.util.ByteArrayBuilder;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.codehaus.jackson.util.TokenBuffer;
import org.codehaus.jackson.util.VersionUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ObjectMapper
extends ObjectCodec
implements Versioned {
    private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
    protected static final ClassIntrospector<? extends BeanDescription> DEFAULT_INTROSPECTOR = BasicClassIntrospector.instance;
    protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
    protected static final VisibilityChecker<?> STD_VISIBILITY_CHECKER = VisibilityChecker.Std.defaultInstance();
    protected final JsonFactory _jsonFactory;
    protected SubtypeResolver _subtypeResolver;
    protected TypeFactory _typeFactory;
    protected InjectableValues _injectableValues;
    protected SerializationConfig _serializationConfig;
    protected SerializerProvider _serializerProvider;
    protected SerializerFactory _serializerFactory;
    protected DeserializationConfig _deserializationConfig;
    protected DeserializerProvider _deserializerProvider;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers = new ConcurrentHashMap(64, 0.6f, 2);

    public ObjectMapper() {
        this(null, null, null);
    }

    public ObjectMapper(JsonFactory jf) {
        this(jf, null, null);
    }

    @Deprecated
    public ObjectMapper(SerializerFactory sf) {
        this(null, null, null);
        this.setSerializerFactory(sf);
    }

    public ObjectMapper(JsonFactory jf, SerializerProvider sp, DeserializerProvider dp) {
        this(jf, sp, dp, null, null);
    }

    public ObjectMapper(JsonFactory jf, SerializerProvider sp, DeserializerProvider dp, SerializationConfig sconfig, DeserializationConfig dconfig) {
        if (jf == null) {
            this._jsonFactory = new MappingJsonFactory(this);
        } else {
            this._jsonFactory = jf;
            if (jf.getCodec() == null) {
                this._jsonFactory.setCodec((ObjectCodec)this);
            }
        }
        this._typeFactory = TypeFactory.defaultInstance();
        this._serializationConfig = sconfig != null ? sconfig : new SerializationConfig(DEFAULT_INTROSPECTOR, DEFAULT_ANNOTATION_INTROSPECTOR, STD_VISIBILITY_CHECKER, null, null, this._typeFactory, null);
        this._deserializationConfig = dconfig != null ? dconfig : new DeserializationConfig(DEFAULT_INTROSPECTOR, DEFAULT_ANNOTATION_INTROSPECTOR, STD_VISIBILITY_CHECKER, null, null, this._typeFactory, null);
        this._serializerProvider = sp == null ? new StdSerializerProvider() : sp;
        this._deserializerProvider = dp == null ? new StdDeserializerProvider() : dp;
        this._serializerFactory = BeanSerializerFactory.instance;
    }

    public Version version() {
        return VersionUtil.versionFor(((Object)((Object)this)).getClass());
    }

    public void registerModule(Module module) {
        String name = module.getModuleName();
        if (name == null) {
            throw new IllegalArgumentException("Module without defined name");
        }
        Version version = module.version();
        if (version == null) {
            throw new IllegalArgumentException("Module without defined version");
        }
        final ObjectMapper mapper = this;
        module.setupModule(new Module.SetupContext(){

            @Override
            public Version getMapperVersion() {
                return ObjectMapper.this.version();
            }

            @Override
            public DeserializationConfig getDeserializationConfig() {
                return mapper.getDeserializationConfig();
            }

            @Override
            public SerializationConfig getSerializationConfig() {
                return mapper.getSerializationConfig();
            }

            @Override
            public boolean isEnabled(DeserializationConfig.Feature f) {
                return mapper.isEnabled(f);
            }

            @Override
            public boolean isEnabled(SerializationConfig.Feature f) {
                return mapper.isEnabled(f);
            }

            @Override
            public boolean isEnabled(JsonParser.Feature f) {
                return mapper.isEnabled(f);
            }

            @Override
            public boolean isEnabled(JsonGenerator.Feature f) {
                return mapper.isEnabled(f);
            }

            @Override
            public void addDeserializers(Deserializers d) {
                mapper._deserializerProvider = mapper._deserializerProvider.withAdditionalDeserializers(d);
            }

            @Override
            public void addKeyDeserializers(KeyDeserializers d) {
                mapper._deserializerProvider = mapper._deserializerProvider.withAdditionalKeyDeserializers(d);
            }

            @Override
            public void addSerializers(Serializers s) {
                mapper._serializerFactory = mapper._serializerFactory.withAdditionalSerializers(s);
            }

            @Override
            public void addKeySerializers(Serializers s) {
                mapper._serializerFactory = mapper._serializerFactory.withAdditionalKeySerializers(s);
            }

            @Override
            public void addBeanSerializerModifier(BeanSerializerModifier modifier) {
                mapper._serializerFactory = mapper._serializerFactory.withSerializerModifier(modifier);
            }

            @Override
            public void addBeanDeserializerModifier(BeanDeserializerModifier modifier) {
                mapper._deserializerProvider = mapper._deserializerProvider.withDeserializerModifier(modifier);
            }

            @Override
            public void addAbstractTypeResolver(AbstractTypeResolver resolver) {
                mapper._deserializerProvider = mapper._deserializerProvider.withAbstractTypeResolver(resolver);
            }

            @Override
            public void addTypeModifier(TypeModifier modifier) {
                TypeFactory f = mapper._typeFactory;
                f = f.withModifier(modifier);
                mapper.setTypeFactory(f);
            }

            @Override
            public void addValueInstantiators(ValueInstantiators instantiators) {
                mapper._deserializerProvider = mapper._deserializerProvider.withValueInstantiators(instantiators);
            }

            @Override
            public void insertAnnotationIntrospector(AnnotationIntrospector ai) {
                mapper._deserializationConfig = mapper._deserializationConfig.withInsertedAnnotationIntrospector(ai);
                mapper._serializationConfig = mapper._serializationConfig.withInsertedAnnotationIntrospector(ai);
            }

            @Override
            public void appendAnnotationIntrospector(AnnotationIntrospector ai) {
                mapper._deserializationConfig = mapper._deserializationConfig.withAppendedAnnotationIntrospector(ai);
                mapper._serializationConfig = mapper._serializationConfig.withAppendedAnnotationIntrospector(ai);
            }

            @Override
            public void setMixInAnnotations(Class<?> target, Class<?> mixinSource) {
                mapper._deserializationConfig.addMixInAnnotations(target, mixinSource);
                mapper._serializationConfig.addMixInAnnotations(target, mixinSource);
            }
        });
    }

    public ObjectMapper withModule(Module module) {
        this.registerModule(module);
        return this;
    }

    public SerializationConfig getSerializationConfig() {
        return this._serializationConfig;
    }

    public SerializationConfig copySerializationConfig() {
        return this._serializationConfig.createUnshared(this._subtypeResolver);
    }

    public ObjectMapper setSerializationConfig(SerializationConfig cfg) {
        this._serializationConfig = cfg;
        return this;
    }

    public DeserializationConfig getDeserializationConfig() {
        return this._deserializationConfig;
    }

    public DeserializationConfig copyDeserializationConfig() {
        return this._deserializationConfig.createUnshared(this._subtypeResolver).passSerializationFeatures(this._serializationConfig._featureFlags);
    }

    public ObjectMapper setDeserializationConfig(DeserializationConfig cfg) {
        this._deserializationConfig = cfg;
        return this;
    }

    public ObjectMapper setSerializerFactory(SerializerFactory f) {
        this._serializerFactory = f;
        return this;
    }

    public ObjectMapper setSerializerProvider(SerializerProvider p) {
        this._serializerProvider = p;
        return this;
    }

    public SerializerProvider getSerializerProvider() {
        return this._serializerProvider;
    }

    public ObjectMapper setDeserializerProvider(DeserializerProvider p) {
        this._deserializerProvider = p;
        return this;
    }

    public DeserializerProvider getDeserializerProvider() {
        return this._deserializerProvider;
    }

    public VisibilityChecker<?> getVisibilityChecker() {
        return this._serializationConfig.getDefaultVisibilityChecker();
    }

    public void setVisibilityChecker(VisibilityChecker<?> vc) {
        this._deserializationConfig = this._deserializationConfig.withVisibilityChecker((VisibilityChecker)vc);
        this._serializationConfig = this._serializationConfig.withVisibilityChecker((VisibilityChecker)vc);
    }

    public ObjectMapper setVisibility(JsonMethod forMethod, JsonAutoDetect.Visibility visibility) {
        this._deserializationConfig = this._deserializationConfig.withVisibility(forMethod, visibility);
        this._serializationConfig = this._serializationConfig.withVisibility(forMethod, visibility);
        return this;
    }

    public SubtypeResolver getSubtypeResolver() {
        if (this._subtypeResolver == null) {
            this._subtypeResolver = new StdSubtypeResolver();
        }
        return this._subtypeResolver;
    }

    public void setSubtypeResolver(SubtypeResolver r) {
        this._subtypeResolver = r;
    }

    public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai) {
        this._serializationConfig = this._serializationConfig.withAnnotationIntrospector(ai);
        this._deserializationConfig = this._deserializationConfig.withAnnotationIntrospector(ai);
        return this;
    }

    public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s) {
        this._serializationConfig = this._serializationConfig.withPropertyNamingStrategy(s);
        this._deserializationConfig = this._deserializationConfig.withPropertyNamingStrategy(s);
        return this;
    }

    public ObjectMapper setSerializationInclusion(JsonSerialize.Inclusion incl) {
        this._serializationConfig = this._serializationConfig.withSerializationInclusion(incl);
        return this;
    }

    public ObjectMapper enableDefaultTyping() {
        return this.enableDefaultTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE);
    }

    public ObjectMapper enableDefaultTyping(DefaultTyping dti) {
        return this.enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
    }

    public ObjectMapper enableDefaultTyping(DefaultTyping applicability, JsonTypeInfo.As includeAs) {
        DefaultTypeResolverBuilder typer = new DefaultTypeResolverBuilder(applicability);
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(includeAs);
        return this.setDefaultTyping(typer);
    }

    public ObjectMapper enableDefaultTypingAsProperty(DefaultTyping applicability, String propertyName) {
        DefaultTypeResolverBuilder typer = new DefaultTypeResolverBuilder(applicability);
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
        typer = typer.typeProperty(propertyName);
        return this.setDefaultTyping(typer);
    }

    public ObjectMapper disableDefaultTyping() {
        return this.setDefaultTyping(null);
    }

    public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer) {
        this._deserializationConfig = this._deserializationConfig.withTypeResolverBuilder((TypeResolverBuilder)typer);
        this._serializationConfig = this._serializationConfig.withTypeResolverBuilder((TypeResolverBuilder)typer);
        return this;
    }

    public void registerSubtypes(Class<?> ... classes) {
        this.getSubtypeResolver().registerSubtypes(classes);
    }

    public void registerSubtypes(NamedType ... types) {
        this.getSubtypeResolver().registerSubtypes(types);
    }

    public TypeFactory getTypeFactory() {
        return this._typeFactory;
    }

    public ObjectMapper setTypeFactory(TypeFactory f) {
        this._typeFactory = f;
        this._deserializationConfig = this._deserializationConfig.withTypeFactory(f);
        this._serializationConfig = this._serializationConfig.withTypeFactory(f);
        return this;
    }

    public JavaType constructType(Type t) {
        return this._typeFactory.constructType(t);
    }

    public ObjectMapper setNodeFactory(JsonNodeFactory f) {
        this._deserializationConfig = this._deserializationConfig.withNodeFactory(f);
        return this;
    }

    public void setFilters(FilterProvider filterProvider) {
        this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
    }

    public JsonFactory getJsonFactory() {
        return this._jsonFactory;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this._deserializationConfig = this._deserializationConfig.withDateFormat(dateFormat);
        this._serializationConfig = this._serializationConfig.withDateFormat(dateFormat);
    }

    public void setHandlerInstantiator(HandlerInstantiator hi) {
        this._deserializationConfig = this._deserializationConfig.withHandlerInstantiator(hi);
        this._serializationConfig = this._serializationConfig.withHandlerInstantiator(hi);
    }

    public ObjectMapper setInjectableValues(InjectableValues injectableValues) {
        this._injectableValues = injectableValues;
        return this;
    }

    public ObjectMapper configure(SerializationConfig.Feature f, boolean state) {
        this._serializationConfig.set(f, state);
        return this;
    }

    public ObjectMapper configure(DeserializationConfig.Feature f, boolean state) {
        this._deserializationConfig.set(f, state);
        return this;
    }

    public ObjectMapper configure(JsonParser.Feature f, boolean state) {
        this._jsonFactory.configure(f, state);
        return this;
    }

    public ObjectMapper configure(JsonGenerator.Feature f, boolean state) {
        this._jsonFactory.configure(f, state);
        return this;
    }

    public ObjectMapper enable(DeserializationConfig.Feature ... f) {
        this._deserializationConfig = this._deserializationConfig.with(f);
        return this;
    }

    public ObjectMapper disable(DeserializationConfig.Feature ... f) {
        this._deserializationConfig = this._deserializationConfig.without(f);
        return this;
    }

    public ObjectMapper enable(SerializationConfig.Feature ... f) {
        this._serializationConfig = this._serializationConfig.with(f);
        return this;
    }

    public ObjectMapper disable(SerializationConfig.Feature ... f) {
        this._serializationConfig = this._serializationConfig.without(f);
        return this;
    }

    public boolean isEnabled(SerializationConfig.Feature f) {
        return this._serializationConfig.isEnabled(f);
    }

    public boolean isEnabled(DeserializationConfig.Feature f) {
        return this._deserializationConfig.isEnabled(f);
    }

    public boolean isEnabled(JsonParser.Feature f) {
        return this._jsonFactory.isEnabled(f);
    }

    public boolean isEnabled(JsonGenerator.Feature f) {
        return this._jsonFactory.isEnabled(f);
    }

    public JsonNodeFactory getNodeFactory() {
        return this._deserializationConfig.getNodeFactory();
    }

    public <T> T readValue(JsonParser jp, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.copyDeserializationConfig(), jp, this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(JsonParser jp, TypeReference<?> valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.copyDeserializationConfig(), jp, this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.copyDeserializationConfig(), jp, valueType);
    }

    public JsonNode readTree(JsonParser jp) throws IOException, JsonProcessingException {
        DeserializationConfig cfg = this.copyDeserializationConfig();
        JsonToken t = jp.getCurrentToken();
        if (t == null && (t = jp.nextToken()) == null) {
            return null;
        }
        JsonNode n = (JsonNode)this._readValue(cfg, jp, JSON_NODE_TYPE);
        return n == null ? this.getNodeFactory().nullNode() : n;
    }

    public <T> MappingIterator<T> readValues(JsonParser jp, JavaType valueType) throws IOException, JsonProcessingException {
        DeserializationConfig config = this.copyDeserializationConfig();
        DeserializationContext ctxt = this._createDeserializationContext(jp, config);
        JsonDeserializer<Object> deser = this._findRootDeserializer(config, valueType);
        return new MappingIterator(valueType, jp, ctxt, deser, false, null);
    }

    public <T> MappingIterator<T> readValues(JsonParser jp, Class<T> valueType) throws IOException, JsonProcessingException {
        return this.readValues(jp, this._typeFactory.constructType(valueType));
    }

    public <T> MappingIterator<T> readValues(JsonParser jp, TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
        return this.readValues(jp, this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(JsonParser jp, Class<T> valueType, DeserializationConfig cfg) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(cfg, jp, this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(JsonParser jp, TypeReference<?> valueTypeRef, DeserializationConfig cfg) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(cfg, jp, this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(JsonParser jp, JavaType valueType, DeserializationConfig cfg) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(cfg, jp, valueType);
    }

    public JsonNode readTree(JsonParser jp, DeserializationConfig cfg) throws IOException, JsonProcessingException {
        JsonNode n = (JsonNode)this._readValue(cfg, jp, JSON_NODE_TYPE);
        return n == null ? NullNode.instance : n;
    }

    public JsonNode readTree(InputStream in) throws IOException, JsonProcessingException {
        JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createJsonParser(in), JSON_NODE_TYPE);
        return n == null ? NullNode.instance : n;
    }

    public JsonNode readTree(Reader r) throws IOException, JsonProcessingException {
        JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createJsonParser(r), JSON_NODE_TYPE);
        return n == null ? NullNode.instance : n;
    }

    public JsonNode readTree(String content) throws IOException, JsonProcessingException {
        JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createJsonParser(content), JSON_NODE_TYPE);
        return n == null ? NullNode.instance : n;
    }

    public JsonNode readTree(byte[] content) throws IOException, JsonProcessingException {
        JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createJsonParser(content), JSON_NODE_TYPE);
        return n == null ? NullNode.instance : n;
    }

    public JsonNode readTree(File file) throws IOException, JsonProcessingException {
        JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createJsonParser(file), JSON_NODE_TYPE);
        return n == null ? NullNode.instance : n;
    }

    public JsonNode readTree(URL source) throws IOException, JsonProcessingException {
        JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createJsonParser(source), JSON_NODE_TYPE);
        return n == null ? NullNode.instance : n;
    }

    public void writeValue(JsonGenerator jgen, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        SerializationConfig config = this.copySerializationConfig();
        if (config.isEnabled(SerializationConfig.Feature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._writeCloseableValue(jgen, value, config);
        } else {
            this._serializerProvider.serializeValue(config, jgen, value, this._serializerFactory);
            if (config.isEnabled(SerializationConfig.Feature.FLUSH_AFTER_WRITE_VALUE)) {
                jgen.flush();
            }
        }
    }

    public void writeValue(JsonGenerator jgen, Object value, SerializationConfig config) throws IOException, JsonGenerationException, JsonMappingException {
        if (config.isEnabled(SerializationConfig.Feature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._writeCloseableValue(jgen, value, config);
        } else {
            this._serializerProvider.serializeValue(config, jgen, value, this._serializerFactory);
            if (config.isEnabled(SerializationConfig.Feature.FLUSH_AFTER_WRITE_VALUE)) {
                jgen.flush();
            }
        }
    }

    public void writeTree(JsonGenerator jgen, JsonNode rootNode) throws IOException, JsonProcessingException {
        SerializationConfig config = this.copySerializationConfig();
        this._serializerProvider.serializeValue(config, jgen, rootNode, this._serializerFactory);
        if (config.isEnabled(SerializationConfig.Feature.FLUSH_AFTER_WRITE_VALUE)) {
            jgen.flush();
        }
    }

    public void writeTree(JsonGenerator jgen, JsonNode rootNode, SerializationConfig cfg) throws IOException, JsonProcessingException {
        this._serializerProvider.serializeValue(cfg, jgen, rootNode, this._serializerFactory);
        if (cfg.isEnabled(SerializationConfig.Feature.FLUSH_AFTER_WRITE_VALUE)) {
            jgen.flush();
        }
    }

    public ObjectNode createObjectNode() {
        return this._deserializationConfig.getNodeFactory().objectNode();
    }

    public ArrayNode createArrayNode() {
        return this._deserializationConfig.getNodeFactory().arrayNode();
    }

    public JsonParser treeAsTokens(JsonNode n) {
        return new TreeTraversingParser(n, this);
    }

    public <T> T treeToValue(JsonNode n, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return this.readValue(this.treeAsTokens(n), valueType);
    }

    public <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
        JsonNode result;
        if (fromValue == null) {
            return null;
        }
        TokenBuffer buf = new TokenBuffer((ObjectCodec)this);
        try {
            this.writeValue((JsonGenerator)buf, fromValue);
            JsonParser jp = buf.asParser();
            result = this.readTree(jp);
            jp.close();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return (T)result;
    }

    public boolean canSerialize(Class<?> type) {
        return this._serializerProvider.hasSerializerFor(this.copySerializationConfig(), type, this._serializerFactory);
    }

    public boolean canDeserialize(JavaType type) {
        return this._deserializerProvider.hasValueDeserializerFor(this.copyDeserializationConfig(), type);
    }

    public <T> T readValue(File src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(File src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(File src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), valueType);
    }

    public <T> T readValue(URL src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(URL src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(URL src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), valueType);
    }

    public <T> T readValue(String content, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(content), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(String content, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(content), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(String content, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(content), valueType);
    }

    public <T> T readValue(Reader src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(Reader src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(Reader src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), valueType);
    }

    public <T> T readValue(InputStream src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(InputStream src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(InputStream src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), valueType);
    }

    public <T> T readValue(byte[] src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src, offset, len), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(byte[] src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(byte[] src, int offset, int len, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(byte[] src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src), valueType);
    }

    public <T> T readValue(byte[] src, int offset, int len, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createJsonParser(src, offset, len), valueType);
    }

    public <T> T readValue(JsonNode root, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.copyDeserializationConfig(), this.treeAsTokens(root), this._typeFactory.constructType(valueType));
    }

    public <T> T readValue(JsonNode root, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.copyDeserializationConfig(), this.treeAsTokens(root), this._typeFactory.constructType(valueTypeRef));
    }

    public <T> T readValue(JsonNode root, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.copyDeserializationConfig(), this.treeAsTokens(root), valueType);
    }

    public void writeValue(File resultFile, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createJsonGenerator(resultFile, JsonEncoding.UTF8), value);
    }

    public void writeValue(OutputStream out, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createJsonGenerator(out, JsonEncoding.UTF8), value);
    }

    public void writeValue(Writer w, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createJsonGenerator(w), value);
    }

    public String writeValueAsString(Object value) throws IOException, JsonGenerationException, JsonMappingException {
        SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());
        this._configAndWriteValue(this._jsonFactory.createJsonGenerator((Writer)sw), value);
        return sw.getAndClear();
    }

    public byte[] writeValueAsBytes(Object value) throws IOException, JsonGenerationException, JsonMappingException {
        ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler());
        this._configAndWriteValue(this._jsonFactory.createJsonGenerator((OutputStream)bb, JsonEncoding.UTF8), value);
        byte[] result = bb.toByteArray();
        bb.release();
        return result;
    }

    public ObjectWriter writer() {
        return new ObjectWriter(this, this.copySerializationConfig());
    }

    public ObjectWriter writer(DateFormat df) {
        return new ObjectWriter(this, this.copySerializationConfig().withDateFormat(df));
    }

    public ObjectWriter writerWithView(Class<?> serializationView) {
        return new ObjectWriter(this, this.copySerializationConfig().withView(serializationView));
    }

    public ObjectWriter writerWithType(Class<?> rootType) {
        JavaType t = rootType == null ? null : this._typeFactory.constructType(rootType);
        return new ObjectWriter(this, this.copySerializationConfig(), t, null);
    }

    public ObjectWriter writerWithType(JavaType rootType) {
        return new ObjectWriter(this, this.copySerializationConfig(), rootType, null);
    }

    public ObjectWriter writerWithType(TypeReference<?> rootType) {
        JavaType t = rootType == null ? null : this._typeFactory.constructType(rootType);
        return new ObjectWriter(this, this.copySerializationConfig(), t, null);
    }

    public ObjectWriter writer(PrettyPrinter pp) {
        if (pp == null) {
            pp = ObjectWriter.NULL_PRETTY_PRINTER;
        }
        return new ObjectWriter(this, this.copySerializationConfig(), null, pp);
    }

    public ObjectWriter writerWithDefaultPrettyPrinter() {
        return new ObjectWriter(this, this.copySerializationConfig(), null, this._defaultPrettyPrinter());
    }

    public ObjectWriter writer(FilterProvider filterProvider) {
        return new ObjectWriter(this, this.copySerializationConfig().withFilters(filterProvider));
    }

    public ObjectWriter writer(FormatSchema schema) {
        return new ObjectWriter(this, this.copySerializationConfig(), schema);
    }

    @Deprecated
    public ObjectWriter typedWriter(Class<?> rootType) {
        return this.writerWithType(rootType);
    }

    @Deprecated
    public ObjectWriter typedWriter(JavaType rootType) {
        return this.writerWithType(rootType);
    }

    @Deprecated
    public ObjectWriter typedWriter(TypeReference<?> rootType) {
        return this.writerWithType(rootType);
    }

    @Deprecated
    public ObjectWriter viewWriter(Class<?> serializationView) {
        return this.writerWithView(serializationView);
    }

    @Deprecated
    public ObjectWriter prettyPrintingWriter(PrettyPrinter pp) {
        return this.writer(pp);
    }

    @Deprecated
    public ObjectWriter defaultPrettyPrintingWriter() {
        return this.writerWithDefaultPrettyPrinter();
    }

    @Deprecated
    public ObjectWriter filteredWriter(FilterProvider filterProvider) {
        return this.writer(filterProvider);
    }

    @Deprecated
    public ObjectWriter schemaBasedWriter(FormatSchema schema) {
        return this.writer(schema);
    }

    public ObjectReader reader() {
        return new ObjectReader(this, this.copyDeserializationConfig()).withInjectableValues(this._injectableValues);
    }

    public ObjectReader readerForUpdating(Object valueToUpdate) {
        JavaType t = this._typeFactory.constructType(valueToUpdate.getClass());
        return new ObjectReader(this, this.copyDeserializationConfig(), t, valueToUpdate, null, this._injectableValues);
    }

    public ObjectReader reader(JavaType type) {
        return new ObjectReader(this, this.copyDeserializationConfig(), type, null, null, this._injectableValues);
    }

    public ObjectReader reader(Class<?> type) {
        return this.reader(this._typeFactory.constructType(type));
    }

    public ObjectReader reader(TypeReference<?> type) {
        return this.reader(this._typeFactory.constructType(type));
    }

    public ObjectReader reader(JsonNodeFactory f) {
        return new ObjectReader(this, this.copyDeserializationConfig()).withNodeFactory(f);
    }

    public ObjectReader reader(FormatSchema schema) {
        return new ObjectReader(this, this.copyDeserializationConfig(), null, null, schema, this._injectableValues);
    }

    public ObjectReader reader(InjectableValues injectableValues) {
        return new ObjectReader(this, this.copyDeserializationConfig(), null, null, null, injectableValues);
    }

    @Deprecated
    public ObjectReader updatingReader(Object valueToUpdate) {
        return this.readerForUpdating(valueToUpdate);
    }

    @Deprecated
    public ObjectReader schemaBasedReader(FormatSchema schema) {
        return this.reader(schema);
    }

    public <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
        return (T)this._convert(fromValue, this._typeFactory.constructType(toValueType));
    }

    public <T> T convertValue(Object fromValue, TypeReference toValueTypeRef) throws IllegalArgumentException {
        return (T)this._convert(fromValue, this._typeFactory.constructType(toValueTypeRef));
    }

    public <T> T convertValue(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
        return (T)this._convert(fromValue, toValueType);
    }

    protected Object _convert(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
        if (fromValue == null) {
            return null;
        }
        TokenBuffer buf = new TokenBuffer((ObjectCodec)this);
        try {
            this.writeValue((JsonGenerator)buf, fromValue);
            JsonParser jp = buf.asParser();
            Object result = this.readValue(jp, toValueType);
            jp.close();
            return result;
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public JsonSchema generateJsonSchema(Class<?> t) throws JsonMappingException {
        return this.generateJsonSchema(t, this.copySerializationConfig());
    }

    public JsonSchema generateJsonSchema(Class<?> t, SerializationConfig cfg) throws JsonMappingException {
        return this._serializerProvider.generateJsonSchema(t, cfg, this._serializerFactory);
    }

    protected PrettyPrinter _defaultPrettyPrinter() {
        return new DefaultPrettyPrinter();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void _configAndWriteValue(JsonGenerator jgen, Object value) throws IOException, JsonGenerationException, JsonMappingException {
        SerializationConfig cfg = this.copySerializationConfig();
        if (cfg.isEnabled(SerializationConfig.Feature.INDENT_OUTPUT)) {
            jgen.useDefaultPrettyPrinter();
        }
        if (cfg.isEnabled(SerializationConfig.Feature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._configAndWriteCloseable(jgen, value, cfg);
            return;
        }
        boolean closed = false;
        try {
            this._serializerProvider.serializeValue(cfg, jgen, value, this._serializerFactory);
            closed = true;
            jgen.close();
        }
        finally {
            if (!closed) {
                try {
                    jgen.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void _configAndWriteValue(JsonGenerator jgen, Object value, Class<?> viewClass) throws IOException, JsonGenerationException, JsonMappingException {
        SerializationConfig cfg = this.copySerializationConfig().withView(viewClass);
        if (cfg.isEnabled(SerializationConfig.Feature.INDENT_OUTPUT)) {
            jgen.useDefaultPrettyPrinter();
        }
        if (cfg.isEnabled(SerializationConfig.Feature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._configAndWriteCloseable(jgen, value, cfg);
            return;
        }
        boolean closed = false;
        try {
            this._serializerProvider.serializeValue(cfg, jgen, value, this._serializerFactory);
            closed = true;
            jgen.close();
        }
        finally {
            if (!closed) {
                try {
                    jgen.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void _configAndWriteCloseable(JsonGenerator jgen, Object value, SerializationConfig cfg) throws IOException, JsonGenerationException, JsonMappingException {
        Closeable toClose = (Closeable)value;
        try {
            this._serializerProvider.serializeValue(cfg, jgen, value, this._serializerFactory);
            JsonGenerator tmpJgen = jgen;
            jgen = null;
            tmpJgen.close();
            Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        finally {
            if (jgen != null) {
                try {
                    jgen.close();
                }
                catch (IOException iOException) {}
            }
            if (toClose != null) {
                try {
                    toClose.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void _writeCloseableValue(JsonGenerator jgen, Object value, SerializationConfig cfg) throws IOException, JsonGenerationException, JsonMappingException {
        Closeable toClose = (Closeable)value;
        try {
            this._serializerProvider.serializeValue(cfg, jgen, value, this._serializerFactory);
            if (cfg.isEnabled(SerializationConfig.Feature.FLUSH_AFTER_WRITE_VALUE)) {
                jgen.flush();
            }
            Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        finally {
            if (toClose != null) {
                try {
                    toClose.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    protected Object _readValue(DeserializationConfig cfg, JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        Object result;
        JsonToken t = this._initForReading(jp);
        if (t == JsonToken.VALUE_NULL) {
            result = this._findRootDeserializer(cfg, valueType).getNullValue();
        } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = null;
        } else {
            DeserializationContext ctxt = this._createDeserializationContext(jp, cfg);
            JsonDeserializer<Object> deser = this._findRootDeserializer(cfg, valueType);
            result = cfg.isEnabled(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE) ? this._unwrapAndDeserialize(jp, valueType, ctxt, deser) : deser.deserialize(jp, ctxt);
        }
        jp.clearCurrentToken();
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object _readMapAndClose(JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        try {
            Object result;
            JsonToken t = this._initForReading(jp);
            if (t == JsonToken.VALUE_NULL) {
                result = this._findRootDeserializer(this._deserializationConfig, valueType).getNullValue();
            } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = null;
            } else {
                DeserializationConfig cfg = this.copyDeserializationConfig();
                DeserializationContext ctxt = this._createDeserializationContext(jp, cfg);
                JsonDeserializer<Object> deser = this._findRootDeserializer(cfg, valueType);
                result = cfg.isEnabled(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE) ? this._unwrapAndDeserialize(jp, valueType, ctxt, deser) : deser.deserialize(jp, ctxt);
            }
            jp.clearCurrentToken();
            Object object = result;
            return object;
        }
        finally {
            try {
                jp.close();
            }
            catch (IOException iOException) {}
        }
    }

    protected JsonToken _initForReading(JsonParser jp) throws IOException, JsonParseException, JsonMappingException {
        JsonToken t = jp.getCurrentToken();
        if (t == null && (t = jp.nextToken()) == null) {
            throw new EOFException("No content to map to Object due to end of input");
        }
        return t;
    }

    protected Object _unwrapAndDeserialize(JsonParser jp, JavaType rootType, DeserializationContext ctxt, JsonDeserializer<Object> deser) throws IOException, JsonParseException, JsonMappingException {
        SerializedString rootName = this._deserializerProvider.findExpectedRootName(ctxt.getConfig(), rootType);
        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(jp, "Current token not START_OBJECT (needed to unwrap root name '" + rootName + "'), but " + jp.getCurrentToken());
        }
        if (jp.nextToken() != JsonToken.FIELD_NAME) {
            throw JsonMappingException.from(jp, "Current token not FIELD_NAME (to contain expected root name '" + rootName + "'), but " + jp.getCurrentToken());
        }
        String actualName = jp.getCurrentName();
        if (!rootName.getValue().equals(actualName)) {
            throw JsonMappingException.from(jp, "Root name '" + actualName + "' does not match expected ('" + rootName + "') for type " + rootType);
        }
        jp.nextToken();
        Object result = deser.deserialize(jp, ctxt);
        if (jp.nextToken() != JsonToken.END_OBJECT) {
            throw JsonMappingException.from(jp, "Current token not END_OBJECT (to match wrapper object with root name '" + rootName + "'), but " + jp.getCurrentToken());
        }
        return result;
    }

    protected JsonDeserializer<Object> _findRootDeserializer(DeserializationConfig cfg, JavaType valueType) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
        if (deser != null) {
            return deser;
        }
        deser = this._deserializerProvider.findTypedValueDeserializer(cfg, valueType, null);
        if (deser == null) {
            throw new JsonMappingException("Can not find a deserializer for type " + valueType);
        }
        this._rootDeserializers.put(valueType, deser);
        return deser;
    }

    protected DeserializationContext _createDeserializationContext(JsonParser jp, DeserializationConfig cfg) {
        return new StdDeserializationContext(cfg, jp, this._deserializerProvider, this._injectableValues);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class DefaultTypeResolverBuilder
    extends StdTypeResolverBuilder {
        protected final DefaultTyping _appliesFor;

        public DefaultTypeResolverBuilder(DefaultTyping t) {
            this._appliesFor = t;
        }

        @Override
        public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes, BeanProperty property) {
            return this.useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes, property) : null;
        }

        @Override
        public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes, BeanProperty property) {
            return this.useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes, property) : null;
        }

        public boolean useForType(JavaType t) {
            switch (this._appliesFor) {
                case NON_CONCRETE_AND_ARRAYS: {
                    while (t.isArrayType()) {
                        t = t.getContentType();
                    }
                }
                case OBJECT_AND_NON_CONCRETE: {
                    return t.getRawClass() == Object.class || !t.isConcrete();
                }
                case NON_FINAL: {
                    while (t.isArrayType()) {
                        t = t.getContentType();
                    }
                    return !t.isFinal();
                }
            }
            return t.getRawClass() == Object.class;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DefaultTyping {
        JAVA_LANG_OBJECT,
        OBJECT_AND_NON_CONCRETE,
        NON_CONCRETE_AND_ARRAYS,
        NON_FINAL;

    }
}

