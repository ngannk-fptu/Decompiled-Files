/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonEncoding
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.core.PrettyPrinter
 *  com.fasterxml.jackson.core.util.DefaultIndenter
 *  com.fasterxml.jackson.core.util.DefaultPrettyPrinter
 *  com.fasterxml.jackson.core.util.DefaultPrettyPrinter$Indenter
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.JsonMappingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ObjectReader
 *  com.fasterxml.jackson.databind.ObjectWriter
 *  com.fasterxml.jackson.databind.SerializationConfig
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.exc.InvalidDefinitionException
 *  com.fasterxml.jackson.databind.ser.FilterProvider
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.TypeUtils
 */
package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.TypeUtils;

public abstract class AbstractJackson2HttpMessageConverter
extends AbstractGenericHttpMessageConverter<Object> {
    private static final Map<String, JsonEncoding> ENCODINGS = CollectionUtils.newHashMap((int)JsonEncoding.values().length);
    @Nullable
    @Deprecated
    public static final Charset DEFAULT_CHARSET;
    protected ObjectMapper defaultObjectMapper;
    @Nullable
    private Map<Class<?>, Map<MediaType, ObjectMapper>> objectMapperRegistrations;
    @Nullable
    private Boolean prettyPrint;
    @Nullable
    private PrettyPrinter ssePrettyPrinter;

    protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        this.defaultObjectMapper = objectMapper;
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith((DefaultPrettyPrinter.Indenter)new DefaultIndenter("  ", "\ndata:"));
        this.ssePrettyPrinter = prettyPrinter;
    }

    protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType supportedMediaType) {
        this(objectMapper);
        this.setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
    }

    protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType ... supportedMediaTypes) {
        this(objectMapper);
        this.setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull((Object)objectMapper, (String)"ObjectMapper must not be null");
        this.defaultObjectMapper = objectMapper;
        this.configurePrettyPrint();
    }

    public ObjectMapper getObjectMapper() {
        return this.defaultObjectMapper;
    }

    public void registerObjectMappersForType(Class<?> clazz, Consumer<Map<MediaType, ObjectMapper>> registrar) {
        if (this.objectMapperRegistrations == null) {
            this.objectMapperRegistrations = new LinkedHashMap();
        }
        Map registrations = this.objectMapperRegistrations.computeIfAbsent(clazz, c -> new LinkedHashMap());
        registrar.accept(registrations);
    }

    @Nullable
    public Map<MediaType, ObjectMapper> getObjectMappersForType(Class<?> clazz) {
        for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> entry : this.getObjectMapperRegistrations().entrySet()) {
            if (!entry.getKey().isAssignableFrom(clazz)) continue;
            return entry.getValue();
        }
        return Collections.emptyMap();
    }

    @Override
    public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        ArrayList<MediaType> result = null;
        for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> entry : this.getObjectMapperRegistrations().entrySet()) {
            if (!entry.getKey().isAssignableFrom(clazz)) continue;
            result = result != null ? result : new ArrayList<MediaType>(entry.getValue().size());
            result.addAll(entry.getValue().keySet());
        }
        return CollectionUtils.isEmpty(result) ? this.getSupportedMediaTypes() : result;
    }

    private Map<Class<?>, Map<MediaType, ObjectMapper>> getObjectMapperRegistrations() {
        return this.objectMapperRegistrations != null ? this.objectMapperRegistrations : Collections.emptyMap();
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        this.configurePrettyPrint();
    }

    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.defaultObjectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint.booleanValue());
        }
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.canRead(clazz, null, mediaType);
    }

    @Override
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!this.canRead(mediaType)) {
            return false;
        }
        JavaType javaType = this.getJavaType(type, contextClass);
        ObjectMapper objectMapper = this.selectObjectMapper(javaType.getRawClass(), mediaType);
        if (objectMapper == null) {
            return false;
        }
        AtomicReference causeRef = new AtomicReference();
        if (objectMapper.canDeserialize(javaType, causeRef)) {
            return true;
        }
        this.logWarningIfNecessary((Type)javaType, (Throwable)causeRef.get());
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        Charset charset;
        if (!this.canWrite(mediaType)) {
            return false;
        }
        if (mediaType != null && mediaType.getCharset() != null && !ENCODINGS.containsKey((charset = mediaType.getCharset()).name())) {
            return false;
        }
        ObjectMapper objectMapper = this.selectObjectMapper(clazz, mediaType);
        if (objectMapper == null) {
            return false;
        }
        AtomicReference causeRef = new AtomicReference();
        if (objectMapper.canSerialize(clazz, causeRef)) {
            return true;
        }
        this.logWarningIfNecessary(clazz, (Throwable)causeRef.get());
        return false;
    }

    @Nullable
    private ObjectMapper selectObjectMapper(Class<?> targetType, @Nullable MediaType targetMediaType) {
        if (targetMediaType == null || CollectionUtils.isEmpty(this.objectMapperRegistrations)) {
            return this.defaultObjectMapper;
        }
        for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> typeEntry : this.getObjectMapperRegistrations().entrySet()) {
            if (!typeEntry.getKey().isAssignableFrom(targetType)) continue;
            for (Map.Entry<MediaType, ObjectMapper> objectMapperEntry : typeEntry.getValue().entrySet()) {
                if (!objectMapperEntry.getKey().includes(targetMediaType)) continue;
                return objectMapperEntry.getValue();
            }
            return null;
        }
        return this.defaultObjectMapper;
    }

    protected void logWarningIfNecessary(Type type, @Nullable Throwable cause) {
        boolean debugLevel;
        if (cause == null) {
            return;
        }
        boolean bl = debugLevel = cause instanceof JsonMappingException && cause.getMessage().startsWith("Cannot find");
        if (debugLevel ? this.logger.isDebugEnabled() : this.logger.isWarnEnabled()) {
            String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") + "serialization for type [" + type + "]";
            if (debugLevel) {
                this.logger.debug((Object)msg, cause);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.warn((Object)msg, cause);
            } else {
                this.logger.warn((Object)(msg + ": " + cause));
            }
        }
    }

    @Override
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(type, contextClass);
        return this.readJavaType(javaType, inputMessage);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(clazz, null);
        return this.readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = this.getCharset(contentType);
        ObjectMapper objectMapper = this.selectObjectMapper(javaType.getRawClass(), contentType);
        Assert.state((objectMapper != null ? 1 : 0) != 0, () -> "No ObjectMapper for " + javaType);
        boolean isUnicode = ENCODINGS.containsKey(charset.name()) || "UTF-16".equals(charset.name()) || "UTF-32".equals(charset.name());
        try {
            Class<?> deserializationView;
            InputStream inputStream = StreamUtils.nonClosing((InputStream)inputMessage.getBody());
            if (inputMessage instanceof MappingJacksonInputMessage && (deserializationView = ((MappingJacksonInputMessage)inputMessage).getDeserializationView()) != null) {
                ObjectReader objectReader = objectMapper.readerWithView(deserializationView).forType(javaType);
                if (isUnicode) {
                    return objectReader.readValue(inputStream);
                }
                InputStreamReader reader = new InputStreamReader(inputStream, charset);
                return objectReader.readValue((Reader)reader);
            }
            if (isUnicode) {
                return objectMapper.readValue(inputStream, javaType);
            }
            InputStreamReader reader = new InputStreamReader(inputStream, charset);
            return objectMapper.readValue((Reader)reader, javaType);
        }
        catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getOriginalMessage(), ex, inputMessage);
        }
    }

    protected Charset getCharset(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        return StandardCharsets.UTF_8;
    }

    @Override
    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = this.getJsonEncoding(contentType);
        Class<?> clazz = object instanceof MappingJacksonValue ? ((MappingJacksonValue)object).getValue().getClass() : object.getClass();
        ObjectMapper objectMapper = this.selectObjectMapper(clazz, contentType);
        Assert.state((objectMapper != null ? 1 : 0) != 0, () -> "No ObjectMapper for " + clazz.getName());
        OutputStream outputStream = StreamUtils.nonClosing((OutputStream)outputMessage.getBody());
        try (JsonGenerator generator = objectMapper.getFactory().createGenerator(outputStream, encoding);){
            ObjectWriter objectWriter;
            this.writePrefix(generator, object);
            Object value = object;
            Class<?> serializationView = null;
            FilterProvider filters = null;
            JavaType javaType = null;
            if (object instanceof MappingJacksonValue) {
                MappingJacksonValue container = (MappingJacksonValue)object;
                value = container.getValue();
                serializationView = container.getSerializationView();
                filters = container.getFilters();
            }
            if (type != null && TypeUtils.isAssignable((Type)type, value.getClass())) {
                javaType = this.getJavaType(type, null);
            }
            ObjectWriter objectWriter2 = objectWriter = serializationView != null ? objectMapper.writerWithView(serializationView) : objectMapper.writer();
            if (filters != null) {
                objectWriter = objectWriter.with(filters);
            }
            if (javaType != null && javaType.isContainerType()) {
                objectWriter = objectWriter.forType(javaType);
            }
            SerializationConfig config = objectWriter.getConfig();
            if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) && config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                objectWriter = objectWriter.with(this.ssePrettyPrinter);
            }
            objectWriter.writeValue(generator, value);
            this.writeSuffix(generator, object);
            generator.flush();
        }
        catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
        }
    }

    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
    }

    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
    }

    protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        return this.defaultObjectMapper.constructType(GenericTypeResolver.resolveType((Type)type, contextClass));
    }

    protected JsonEncoding getJsonEncoding(@Nullable MediaType contentType) {
        Charset charset;
        JsonEncoding encoding;
        if (contentType != null && contentType.getCharset() != null && (encoding = ENCODINGS.get((charset = contentType.getCharset()).name())) != null) {
            return encoding;
        }
        return JsonEncoding.UTF8;
    }

    @Override
    @Nullable
    protected MediaType getDefaultContentType(Object object) throws IOException {
        if (object instanceof MappingJacksonValue) {
            object = ((MappingJacksonValue)object).getValue();
        }
        return super.getDefaultContentType(object);
    }

    @Override
    protected Long getContentLength(Object object, @Nullable MediaType contentType) throws IOException {
        if (object instanceof MappingJacksonValue) {
            object = ((MappingJacksonValue)object).getValue();
        }
        return super.getContentLength(object, contentType);
    }

    static {
        for (JsonEncoding encoding : JsonEncoding.values()) {
            ENCODINGS.put(encoding.getJavaName(), encoding);
        }
        ENCODINGS.put("US-ASCII", JsonEncoding.UTF8);
        DEFAULT_CHARSET = null;
    }
}

