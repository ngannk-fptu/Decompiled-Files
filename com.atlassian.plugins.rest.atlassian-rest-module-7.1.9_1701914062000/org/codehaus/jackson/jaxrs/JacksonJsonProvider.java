/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.Versioned;
import org.codehaus.jackson.jaxrs.Annotations;
import org.codehaus.jackson.jaxrs.MapperConfigurator;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonView;
import org.codehaus.jackson.map.type.ClassKey;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.util.VersionUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Consumes(value={"application/json", "text/json"})
@Produces(value={"application/json", "text/json"})
public class JacksonJsonProvider
implements MessageBodyReader<Object>,
MessageBodyWriter<Object>,
Versioned {
    public static final Annotations[] BASIC_ANNOTATIONS = new Annotations[]{Annotations.JACKSON};
    public static final HashSet<ClassKey> _untouchables = new HashSet();
    public static final Class<?>[] _unreadableClasses;
    public static final Class<?>[] _unwritableClasses;
    protected final MapperConfigurator _mapperConfig;
    protected HashSet<ClassKey> _cfgCustomUntouchables;
    protected String _jsonpFunctionName;
    @Context
    protected Providers _providers;
    protected boolean _cfgCheckCanSerialize = false;
    protected boolean _cfgCheckCanDeserialize = false;

    public JacksonJsonProvider() {
        this((ObjectMapper)null, BASIC_ANNOTATIONS);
    }

    public JacksonJsonProvider(Annotations ... annotationsToUse) {
        this((ObjectMapper)null, annotationsToUse);
    }

    public JacksonJsonProvider(ObjectMapper mapper) {
        this(mapper, BASIC_ANNOTATIONS);
    }

    public JacksonJsonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
        this._mapperConfig = new MapperConfigurator(mapper, annotationsToUse);
    }

    @Override
    public Version version() {
        return VersionUtil.versionFor(this.getClass());
    }

    public void checkCanDeserialize(boolean state) {
        this._cfgCheckCanDeserialize = state;
    }

    public void checkCanSerialize(boolean state) {
        this._cfgCheckCanSerialize = state;
    }

    public void setAnnotationsToUse(Annotations[] annotationsToUse) {
        this._mapperConfig.setAnnotationsToUse(annotationsToUse);
    }

    public void setMapper(ObjectMapper m) {
        this._mapperConfig.setMapper(m);
    }

    public JacksonJsonProvider configure(DeserializationConfig.Feature f, boolean state) {
        this._mapperConfig.configure(f, state);
        return this;
    }

    public JacksonJsonProvider configure(SerializationConfig.Feature f, boolean state) {
        this._mapperConfig.configure(f, state);
        return this;
    }

    public JacksonJsonProvider configure(JsonParser.Feature f, boolean state) {
        this._mapperConfig.configure(f, state);
        return this;
    }

    public JacksonJsonProvider configure(JsonGenerator.Feature f, boolean state) {
        this._mapperConfig.configure(f, state);
        return this;
    }

    public JacksonJsonProvider enable(DeserializationConfig.Feature f, boolean state) {
        this._mapperConfig.configure(f, true);
        return this;
    }

    public JacksonJsonProvider enable(SerializationConfig.Feature f, boolean state) {
        this._mapperConfig.configure(f, true);
        return this;
    }

    public JacksonJsonProvider enable(JsonParser.Feature f, boolean state) {
        this._mapperConfig.configure(f, true);
        return this;
    }

    public JacksonJsonProvider enable(JsonGenerator.Feature f, boolean state) {
        this._mapperConfig.configure(f, true);
        return this;
    }

    public JacksonJsonProvider disable(DeserializationConfig.Feature f, boolean state) {
        this._mapperConfig.configure(f, false);
        return this;
    }

    public JacksonJsonProvider disable(SerializationConfig.Feature f, boolean state) {
        this._mapperConfig.configure(f, false);
        return this;
    }

    public JacksonJsonProvider disable(JsonParser.Feature f, boolean state) {
        this._mapperConfig.configure(f, false);
        return this;
    }

    public JacksonJsonProvider disable(JsonGenerator.Feature f, boolean state) {
        this._mapperConfig.configure(f, false);
        return this;
    }

    public void addUntouchable(Class<?> type) {
        if (this._cfgCustomUntouchables == null) {
            this._cfgCustomUntouchables = new HashSet();
        }
        this._cfgCustomUntouchables.add(new ClassKey(type));
    }

    public void setJSONPFunctionName(String fname) {
        this._jsonpFunctionName = fname;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        ObjectMapper mapper;
        if (!this.isJsonType(mediaType)) {
            return false;
        }
        if (_untouchables.contains(new ClassKey(type))) {
            return false;
        }
        for (Class<?> cls : _unreadableClasses) {
            if (!cls.isAssignableFrom(type)) continue;
            return false;
        }
        if (JacksonJsonProvider._containedIn(type, this._cfgCustomUntouchables)) {
            return false;
        }
        return !this._cfgCheckCanSerialize || (mapper = this.locateMapper(type, mediaType)).canDeserialize(mapper.constructType(type));
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        ObjectMapper mapper = this.locateMapper(type, mediaType);
        JsonParser jp = mapper.getJsonFactory().createJsonParser(entityStream);
        jp.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        return mapper.readValue(jp, mapper.constructType(genericType));
    }

    @Override
    public long getSize(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!this.isJsonType(mediaType)) {
            return false;
        }
        if (_untouchables.contains(new ClassKey(type))) {
            return false;
        }
        for (Class<?> cls : _unwritableClasses) {
            if (!cls.isAssignableFrom(type)) continue;
            return false;
        }
        if (JacksonJsonProvider._containedIn(type, this._cfgCustomUntouchables)) {
            return false;
        }
        return !this._cfgCheckCanSerialize || this.locateMapper(type, mediaType).canSerialize(type);
    }

    @Override
    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        ObjectMapper mapper = this.locateMapper(type, mediaType);
        JsonEncoding enc = this.findEncoding(mediaType, httpHeaders);
        JsonGenerator jg = mapper.getJsonFactory().createJsonGenerator(entityStream, enc);
        jg.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        if (mapper.getSerializationConfig().isEnabled(SerializationConfig.Feature.INDENT_OUTPUT)) {
            jg.useDefaultPrettyPrinter();
        }
        JavaType rootType = null;
        if (genericType != null && value != null && genericType.getClass() != Class.class && (rootType = mapper.getTypeFactory().constructType(genericType)).getRawClass() == Object.class) {
            rootType = null;
        }
        Class<?> viewToUse = null;
        if (annotations != null && annotations.length > 0) {
            viewToUse = this._findView(mapper, annotations);
        }
        if (viewToUse != null) {
            ObjectWriter viewWriter = mapper.viewWriter(viewToUse);
            if (this._jsonpFunctionName != null) {
                viewWriter.writeValue(jg, (Object)new JSONPObject(this._jsonpFunctionName, value, rootType));
            } else if (rootType != null) {
                mapper.typedWriter(rootType).withView(viewToUse).writeValue(jg, value);
            } else {
                viewWriter.writeValue(jg, value);
            }
        } else if (this._jsonpFunctionName != null) {
            mapper.writeValue(jg, (Object)new JSONPObject(this._jsonpFunctionName, value, rootType));
        } else if (rootType != null) {
            mapper.typedWriter(rootType).writeValue(jg, value);
        } else {
            mapper.writeValue(jg, value);
        }
    }

    protected JsonEncoding findEncoding(MediaType mediaType, MultivaluedMap<String, Object> httpHeaders) {
        return JsonEncoding.UTF8;
    }

    protected boolean isJsonType(MediaType mediaType) {
        if (mediaType != null) {
            String subtype = mediaType.getSubtype();
            return "json".equalsIgnoreCase(subtype) || subtype.endsWith("+json");
        }
        return true;
    }

    public ObjectMapper locateMapper(Class<?> type, MediaType mediaType) {
        ObjectMapper m = this._mapperConfig.getConfiguredMapper();
        if (m == null) {
            if (this._providers != null) {
                ContextResolver<ObjectMapper> resolver = this._providers.getContextResolver(ObjectMapper.class, mediaType);
                if (resolver == null) {
                    resolver = this._providers.getContextResolver(ObjectMapper.class, null);
                }
                if (resolver != null) {
                    m = resolver.getContext(type);
                }
            }
            if (m == null) {
                m = this._mapperConfig.getDefaultMapper();
            }
        }
        return m;
    }

    protected static boolean _containedIn(Class<?> mainType, HashSet<ClassKey> set) {
        if (set != null) {
            ClassKey key = new ClassKey(mainType);
            if (set.contains(key)) {
                return true;
            }
            for (Class<?> cls : ClassUtil.findSuperTypes(mainType, null)) {
                key.reset(cls);
                if (!set.contains(key)) continue;
                return true;
            }
        }
        return false;
    }

    protected Class<?> _findView(ObjectMapper mapper, Annotation[] annotations) throws JsonMappingException {
        for (Annotation annotation : annotations) {
            if (!annotation.annotationType().isAssignableFrom(JsonView.class)) continue;
            JsonView jsonView = (JsonView)annotation;
            Class<?>[] views = jsonView.value();
            if (views.length > 1) {
                StringBuilder s = new StringBuilder("Multiple @JsonView's can not be used on a JAX-RS method. Got ");
                s.append(views.length).append(" views: ");
                for (int i = 0; i < views.length; ++i) {
                    if (i > 0) {
                        s.append(", ");
                    }
                    s.append(views[i].getName());
                }
                throw new JsonMappingException(s.toString());
            }
            return views[0];
        }
        return null;
    }

    static {
        _untouchables.add(new ClassKey(InputStream.class));
        _untouchables.add(new ClassKey(Reader.class));
        _untouchables.add(new ClassKey(OutputStream.class));
        _untouchables.add(new ClassKey(Writer.class));
        _untouchables.add(new ClassKey(byte[].class));
        _untouchables.add(new ClassKey(char[].class));
        _untouchables.add(new ClassKey(String.class));
        _untouchables.add(new ClassKey(StreamingOutput.class));
        _untouchables.add(new ClassKey(Response.class));
        _unreadableClasses = new Class[]{InputStream.class, Reader.class};
        _unwritableClasses = new Class[]{OutputStream.class, Writer.class, StreamingOutput.class, Response.class};
    }
}

