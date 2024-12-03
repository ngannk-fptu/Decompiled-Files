/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonView
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.apache.commons.logging.Log
 *  org.springframework.core.GenericTypeResolver
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Hints
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MimeType
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpLogging;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import org.springframework.util.ObjectUtils;

public abstract class Jackson2CodecSupport {
    public static final String JSON_VIEW_HINT = Jackson2CodecSupport.class.getName() + ".jsonView";
    static final String ACTUAL_TYPE_HINT = Jackson2CodecSupport.class.getName() + ".actualType";
    private static final String JSON_VIEW_HINT_ERROR = "@JsonView only supported for write hints with exactly 1 class argument: ";
    private static final List<MimeType> DEFAULT_MIME_TYPES = Collections.unmodifiableList(Arrays.asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"), MediaType.APPLICATION_NDJSON));
    protected final Log logger = HttpLogging.forLogName(this.getClass());
    private ObjectMapper defaultObjectMapper;
    @Nullable
    private Map<Class<?>, Map<MimeType, ObjectMapper>> objectMapperRegistrations;
    private final List<MimeType> mimeTypes;

    protected Jackson2CodecSupport(ObjectMapper objectMapper, MimeType ... mimeTypes) {
        Assert.notNull((Object)objectMapper, (String)"ObjectMapper must not be null");
        this.defaultObjectMapper = objectMapper;
        this.mimeTypes = !ObjectUtils.isEmpty((Object[])mimeTypes) ? Collections.unmodifiableList(Arrays.asList(mimeTypes)) : DEFAULT_MIME_TYPES;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull((Object)objectMapper, (String)"ObjectMapper must not be null");
        this.defaultObjectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return this.defaultObjectMapper;
    }

    public void registerObjectMappersForType(Class<?> clazz, Consumer<Map<MimeType, ObjectMapper>> registrar) {
        if (this.objectMapperRegistrations == null) {
            this.objectMapperRegistrations = new LinkedHashMap();
        }
        Map registrations = this.objectMapperRegistrations.computeIfAbsent(clazz, c -> new LinkedHashMap());
        registrar.accept(registrations);
    }

    @Nullable
    public Map<MimeType, ObjectMapper> getObjectMappersForType(Class<?> clazz) {
        for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> entry : this.getObjectMapperRegistrations().entrySet()) {
            if (!entry.getKey().isAssignableFrom(clazz)) continue;
            return entry.getValue();
        }
        return Collections.emptyMap();
    }

    protected Map<Class<?>, Map<MimeType, ObjectMapper>> getObjectMapperRegistrations() {
        return this.objectMapperRegistrations != null ? this.objectMapperRegistrations : Collections.emptyMap();
    }

    protected List<MimeType> getMimeTypes() {
        return this.mimeTypes;
    }

    protected List<MimeType> getMimeTypes(ResolvableType elementType) {
        Class elementClass = elementType.toClass();
        ArrayList<MimeType> result = null;
        for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> entry : this.getObjectMapperRegistrations().entrySet()) {
            if (!entry.getKey().isAssignableFrom(elementClass)) continue;
            result = result != null ? result : new ArrayList<MimeType>(entry.getValue().size());
            result.addAll(entry.getValue().keySet());
        }
        return CollectionUtils.isEmpty(result) ? this.getMimeTypes() : result;
    }

    protected boolean supportsMimeType(@Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        for (MimeType supportedMimeType : this.mimeTypes) {
            if (!supportedMimeType.isCompatibleWith(mimeType)) continue;
            return true;
        }
        return false;
    }

    protected void logWarningIfNecessary(Type type, @Nullable Throwable cause) {
        if (cause == null) {
            return;
        }
        if (this.logger.isDebugEnabled()) {
            String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") + "serialization for type [" + type + "]";
            this.logger.debug((Object)msg, cause);
        }
    }

    protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        return this.defaultObjectMapper.constructType(GenericTypeResolver.resolveType((Type)type, contextClass));
    }

    protected Map<String, Object> getHints(ResolvableType resolvableType) {
        MethodParameter param = this.getParameter(resolvableType);
        if (param != null) {
            JsonView annotation;
            HashMap<String, Object> hints = null;
            if (resolvableType.hasGenerics()) {
                hints = new HashMap<String, Object>(2);
                hints.put(ACTUAL_TYPE_HINT, resolvableType);
            }
            if ((annotation = this.getAnnotation(param, JsonView.class)) != null) {
                Class[] classes = annotation.value();
                Assert.isTrue((classes.length == 1 ? 1 : 0) != 0, () -> JSON_VIEW_HINT_ERROR + param);
                hints = hints != null ? hints : new HashMap<String, Object>(1);
                hints.put(JSON_VIEW_HINT, classes[0]);
            }
            if (hints != null) {
                return hints;
            }
        }
        return Hints.none();
    }

    @Nullable
    protected MethodParameter getParameter(ResolvableType type) {
        return type.getSource() instanceof MethodParameter ? (MethodParameter)type.getSource() : null;
    }

    @Nullable
    protected abstract <A extends Annotation> A getAnnotation(MethodParameter var1, Class<A> var2);

    @Nullable
    protected ObjectMapper selectObjectMapper(ResolvableType targetType, @Nullable MimeType targetMimeType) {
        if (targetMimeType == null || CollectionUtils.isEmpty(this.objectMapperRegistrations)) {
            return this.defaultObjectMapper;
        }
        Class targetClass = targetType.toClass();
        for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> typeEntry : this.getObjectMapperRegistrations().entrySet()) {
            if (!typeEntry.getKey().isAssignableFrom(targetClass)) continue;
            for (Map.Entry<MimeType, ObjectMapper> objectMapperEntry : typeEntry.getValue().entrySet()) {
                if (!objectMapperEntry.getKey().includes(targetMimeType)) continue;
                return objectMapperEntry.getValue();
            }
            return null;
        }
        return this.defaultObjectMapper;
    }
}

