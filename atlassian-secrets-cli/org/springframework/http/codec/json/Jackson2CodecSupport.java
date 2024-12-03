/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.ObjectUtils;

public abstract class Jackson2CodecSupport {
    public static final String JSON_VIEW_HINT = Jackson2CodecSupport.class.getName() + ".jsonView";
    private static final String JSON_VIEW_HINT_ERROR = "@JsonView only supported for write hints with exactly 1 class argument: ";
    private static final List<MimeType> DEFAULT_MIME_TYPES = Collections.unmodifiableList(Arrays.asList(new MimeType("application", "json", StandardCharsets.UTF_8), new MimeType("application", "*+json", StandardCharsets.UTF_8)));
    private final ObjectMapper objectMapper;
    private final List<MimeType> mimeTypes;

    protected Jackson2CodecSupport(ObjectMapper objectMapper, MimeType ... mimeTypes) {
        Assert.notNull((Object)objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        this.mimeTypes = !ObjectUtils.isEmpty(mimeTypes) ? Collections.unmodifiableList(Arrays.asList(mimeTypes)) : DEFAULT_MIME_TYPES;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    protected List<MimeType> getMimeTypes() {
        return this.mimeTypes;
    }

    protected boolean supportsMimeType(@Nullable MimeType mimeType) {
        return mimeType == null || this.mimeTypes.stream().anyMatch(m -> m.isCompatibleWith(mimeType));
    }

    protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        return typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    protected Map<String, Object> getHints(ResolvableType resolvableType) {
        JsonView annotation;
        MethodParameter param = this.getParameter(resolvableType);
        if (param != null && (annotation = this.getAnnotation(param, JsonView.class)) != null) {
            Class<?>[] classes = annotation.value();
            Assert.isTrue(classes.length == 1, JSON_VIEW_HINT_ERROR + param);
            return Collections.singletonMap(JSON_VIEW_HINT, classes[0]);
        }
        return Collections.emptyMap();
    }

    @Nullable
    protected MethodParameter getParameter(ResolvableType type) {
        return type.getSource() instanceof MethodParameter ? (MethodParameter)type.getSource() : null;
    }

    @Nullable
    protected abstract <A extends Annotation> A getAnnotation(MethodParameter var1, Class<A> var2);
}

