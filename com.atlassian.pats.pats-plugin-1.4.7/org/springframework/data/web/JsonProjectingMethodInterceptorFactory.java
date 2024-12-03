/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.jayway.jsonpath.Configuration
 *  com.jayway.jsonpath.DocumentContext
 *  com.jayway.jsonpath.JsonPath
 *  com.jayway.jsonpath.Option
 *  com.jayway.jsonpath.ParseContext
 *  com.jayway.jsonpath.PathNotFoundException
 *  com.jayway.jsonpath.Predicate
 *  com.jayway.jsonpath.TypeRef
 *  com.jayway.jsonpath.spi.json.JsonProvider
 *  com.jayway.jsonpath.spi.mapper.MappingProvider
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.web;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.projection.Accessor;
import org.springframework.data.projection.MethodInterceptorFactory;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.data.web.JsonPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class JsonProjectingMethodInterceptorFactory
implements MethodInterceptorFactory {
    private final ParseContext context;

    public JsonProjectingMethodInterceptorFactory(MappingProvider mappingProvider) {
        this(Configuration.defaultConfiguration().jsonProvider(), mappingProvider);
    }

    public JsonProjectingMethodInterceptorFactory(JsonProvider jsonProvider, MappingProvider mappingProvider) {
        Assert.notNull((Object)jsonProvider, (String)"JsonProvider must not be null!");
        Assert.notNull((Object)mappingProvider, (String)"MappingProvider must not be null!");
        Configuration configuration = Configuration.builder().options(new Option[]{Option.ALWAYS_RETURN_LIST}).jsonProvider(jsonProvider).mappingProvider(mappingProvider).build();
        this.context = com.jayway.jsonpath.JsonPath.using((Configuration)configuration);
    }

    @Override
    public MethodInterceptor createMethodInterceptor(Object source, Class<?> targetType) {
        DocumentContext context = InputStream.class.isInstance(source) ? this.context.parse((InputStream)source) : this.context.parse(source);
        return new InputMessageProjecting(context);
    }

    @Override
    public boolean supports(Object source, Class<?> targetType) {
        if (InputStream.class.isInstance(source) || JSONObject.class.isInstance(source) || JSONArray.class.isInstance(source)) {
            return true;
        }
        return Map.class.isInstance(source) && JsonProjectingMethodInterceptorFactory.hasJsonPathAnnotation(targetType);
    }

    private static boolean hasJsonPathAnnotation(Class<?> type) {
        for (Method method : type.getMethods()) {
            if (AnnotationUtils.findAnnotation((Method)method, JsonPath.class) == null) continue;
            return true;
        }
        return false;
    }

    private static class InputMessageProjecting
    implements MethodInterceptor {
        private final DocumentContext context;

        public InputMessageProjecting(DocumentContext context) {
            this.context = context;
        }

        @Nullable
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            TypeInformation returnType = ClassTypeInformation.fromReturnTypeOf(method);
            ResolvableType type = ResolvableType.forMethodReturnType((Method)method);
            boolean isCollectionResult = Collection.class.isAssignableFrom(type.getRawClass());
            type = isCollectionResult ? type : ResolvableType.forClassWithGenerics(List.class, (ResolvableType[])new ResolvableType[]{type});
            Collection<String> jsonPaths = InputMessageProjecting.getJsonPaths(method);
            for (String jsonPath : jsonPaths) {
                try {
                    if (returnType.getRequiredActualType().getType().isInterface()) {
                        List result = (List)this.context.read(jsonPath, new Predicate[0]);
                        List nested = result.isEmpty() ? null : (List)result.get(0);
                        return isCollectionResult && !(nested instanceof Collection) ? result : nested;
                    }
                    boolean definitePath = com.jayway.jsonpath.JsonPath.isPathDefinite((String)jsonPath);
                    type = isCollectionResult && definitePath ? ResolvableType.forClassWithGenerics(List.class, (ResolvableType[])new ResolvableType[]{type}) : type;
                    List result = (List)this.context.read(jsonPath, (TypeRef)new ResolvableTypeRef(type));
                    if (isCollectionResult && definitePath) {
                        result = (List)result.get(0);
                    }
                    return isCollectionResult ? result : (result.isEmpty() ? null : result.get(0));
                }
                catch (PathNotFoundException pathNotFoundException) {
                }
            }
            return null;
        }

        private static Collection<String> getJsonPaths(Method method) {
            JsonPath annotation = (JsonPath)AnnotationUtils.findAnnotation((Method)method, JsonPath.class);
            if (annotation != null) {
                return Arrays.asList(annotation.value());
            }
            return Collections.singletonList("$.".concat(new Accessor(method).getPropertyName()));
        }

        private static class ResolvableTypeRef
        extends TypeRef<Object> {
            private final ResolvableType type;

            ResolvableTypeRef(ResolvableType type) {
                this.type = type;
            }

            public Type getType() {
                return this.type.getType();
            }
        }
    }
}

