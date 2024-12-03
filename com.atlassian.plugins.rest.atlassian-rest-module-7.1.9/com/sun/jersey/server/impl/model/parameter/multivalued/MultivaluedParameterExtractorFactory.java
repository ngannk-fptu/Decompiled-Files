/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.model.parameter.multivalued.CollectionStringExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.CollectionStringReaderExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.PrimitiveMapper;
import com.sun.jersey.server.impl.model.parameter.multivalued.PrimitiveValueOfExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.StringExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.StringReaderExtractor;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderWorkers;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public final class MultivaluedParameterExtractorFactory
implements MultivaluedParameterExtractorProvider {
    private final StringReaderWorkers w;

    public MultivaluedParameterExtractorFactory(StringReaderWorkers w) {
        this.w = w;
    }

    @Override
    public MultivaluedParameterExtractor getWithoutDefaultValue(Parameter p) {
        return this.process(this.w, null, p.getParameterClass(), p.getParameterType(), p.getAnnotations(), p.getSourceName());
    }

    @Override
    public MultivaluedParameterExtractor get(Parameter p) {
        return this.process(this.w, p.getDefaultValue(), p.getParameterClass(), p.getParameterType(), p.getAnnotations(), p.getSourceName());
    }

    private MultivaluedParameterExtractor process(StringReaderWorkers w, String defaultValue, Class<?> parameter, Type parameterType, Annotation[] annotations, String parameterName) {
        if (parameter == List.class || parameter == Set.class || parameter == SortedSet.class) {
            ReflectionHelper.TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(parameterType);
            if (tcp == null || tcp.c == String.class) {
                return CollectionStringExtractor.getInstance(parameter, parameterName, defaultValue);
            }
            StringReader sr = w.getStringReader(tcp.c, tcp.t, annotations);
            if (sr == null) {
                return null;
            }
            try {
                return CollectionStringReaderExtractor.getInstance(parameter, sr, parameterName, defaultValue);
            }
            catch (Exception e) {
                throw new ContainerException("Could not process parameter type " + parameter, e);
            }
        }
        if (parameter == String.class) {
            return new StringExtractor(parameterName, defaultValue);
        }
        if (parameter.isPrimitive()) {
            if ((parameter = PrimitiveMapper.primitiveToClassMap.get(parameter)) == null) {
                return null;
            }
            Method valueOf = AccessController.doPrivileged(ReflectionHelper.getValueOfStringMethodPA(parameter));
            if (valueOf != null) {
                try {
                    Object defaultDefaultValue = PrimitiveMapper.primitiveToDefaultValueMap.get(parameter);
                    return new PrimitiveValueOfExtractor(valueOf, parameterName, defaultValue, defaultDefaultValue);
                }
                catch (Exception e) {
                    throw new ContainerException(ImplMessages.DEFAULT_COULD_NOT_PROCESS_METHOD(defaultValue, valueOf));
                }
            }
        } else {
            StringReader sr = w.getStringReader(parameter, parameterType, annotations);
            if (sr == null) {
                return null;
            }
            try {
                return new StringReaderExtractor(sr, parameterName, defaultValue);
            }
            catch (Exception e) {
                throw new ContainerException("Could not process parameter type " + parameter, e);
            }
        }
        return null;
    }
}

