/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.CollectionFactory
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Slice;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ResultProcessor {
    private final QueryMethod method;
    private final ProjectingConverter converter;
    private final ProjectionFactory factory;
    private final ReturnedType type;

    ResultProcessor(QueryMethod method, ProjectionFactory factory) {
        this(method, factory, method.getReturnedObjectType());
    }

    private ResultProcessor(QueryMethod method, ProjectionFactory factory, Class<?> type) {
        Assert.notNull((Object)method, (String)"QueryMethod must not be null!");
        Assert.notNull((Object)factory, (String)"ProjectionFactory must not be null!");
        Assert.notNull(type, (String)"Type must not be null!");
        this.method = method;
        this.type = ReturnedType.of(type, method.getDomainClass(), factory);
        this.converter = new ProjectingConverter(this.type, factory);
        this.factory = factory;
    }

    private ResultProcessor(QueryMethod method, ProjectingConverter converter, ProjectionFactory factory, ReturnedType type) {
        this.method = method;
        this.converter = converter;
        this.factory = factory;
        this.type = type;
    }

    public ResultProcessor withDynamicProjection(ParameterAccessor accessor) {
        Assert.notNull((Object)accessor, (String)"Parameter accessor must not be null!");
        Class<?> projection = accessor.findDynamicProjection();
        return projection == null ? this : this.withType(projection);
    }

    public ReturnedType getReturnedType() {
        return this.type;
    }

    @Nullable
    public <T> T processResult(@Nullable Object source) {
        return this.processResult(source, NoOpConverter.INSTANCE);
    }

    @Nullable
    public <T> T processResult(@Nullable Object source, Converter<Object, Object> preparingConverter) {
        ChainingConverter converter;
        block8: {
            block9: {
                if (source == null || this.type.isInstance(source) || !this.type.isProjecting()) {
                    return (T)source;
                }
                Assert.notNull(preparingConverter, (String)"Preparing converter must not be null!");
                converter = ChainingConverter.of(this.type.getReturnedType(), preparingConverter).and(this.converter);
                if (!(source instanceof Slice)) break block8;
                if (this.method.isPageQuery()) break block9;
                if (!this.method.isSliceQuery()) break block8;
            }
            return (T)((Slice)source).map(converter::convert);
        }
        if (source instanceof Collection && this.method.isCollectionQuery()) {
            Collection collection = (Collection)source;
            Collection<Object> target = ResultProcessor.createCollectionFor(collection);
            for (Object columns : collection) {
                target.add(this.type.isInstance(columns) ? columns : converter.convert(columns));
            }
            return (T)target;
        }
        if (source instanceof Stream && this.method.isStreamQuery()) {
            return (T)((Stream)source).map(t -> this.type.isInstance(t) ? t : converter.convert(t));
        }
        if (ReactiveWrapperConverters.supports(source.getClass())) {
            return ReactiveWrapperConverters.map(source, converter::convert);
        }
        return (T)converter.convert(source);
    }

    private ResultProcessor withType(Class<?> type) {
        ReturnedType returnedType = ReturnedType.of(type, this.method.getDomainClass(), this.factory);
        return new ResultProcessor(this.method, this.converter.withType(returnedType), this.factory, returnedType);
    }

    private static Collection<Object> createCollectionFor(Collection<?> source) {
        try {
            return CollectionFactory.createCollection(source.getClass(), (int)source.size());
        }
        catch (RuntimeException o_O) {
            return CollectionFactory.createApproximateCollection(source, (int)source.size());
        }
    }

    private static class ProjectingConverter
    implements Converter<Object, Object> {
        private final ReturnedType type;
        private final ProjectionFactory factory;
        private final ConversionService conversionService;

        ProjectingConverter(ReturnedType type, ProjectionFactory factory) {
            this(type, factory, DefaultConversionService.getSharedInstance());
        }

        public ProjectingConverter(ReturnedType type, ProjectionFactory factory, ConversionService conversionService) {
            this.type = type;
            this.factory = factory;
            this.conversionService = conversionService;
        }

        ProjectingConverter withType(ReturnedType type) {
            Assert.notNull((Object)type, (String)"ReturnedType must not be null!");
            return new ProjectingConverter(type, this.factory, this.conversionService);
        }

        @Nullable
        public Object convert(Object source) {
            Class<?> targetType = this.type.getReturnedType();
            if (targetType.isInterface()) {
                return this.factory.createProjection(targetType, this.getProjectionTarget(source));
            }
            return this.conversionService.convert(source, targetType);
        }

        private Object getProjectionTarget(Object source) {
            if (source != null && source.getClass().isArray()) {
                source = Arrays.asList((Object[])source);
            }
            if (source instanceof Collection) {
                return ProjectingConverter.toMap((Collection)source, this.type.getInputProperties());
            }
            return source;
        }

        private static Map<String, Object> toMap(Collection<?> values, List<String> names) {
            int i = 0;
            HashMap<String, Object> result = new HashMap<String, Object>(values.size());
            for (Object element : values) {
                result.put(names.get(i++), element);
            }
            return result;
        }
    }

    private static enum NoOpConverter implements Converter<Object, Object>
    {
        INSTANCE;


        public Object convert(Object source) {
            return source;
        }
    }

    private static class ChainingConverter
    implements Converter<Object, Object> {
        private final Class<?> targetType;
        private final Converter<Object, Object> delegate;

        private ChainingConverter(Class<?> targetType, Converter<Object, Object> delegate) {
            this.targetType = targetType;
            this.delegate = delegate;
        }

        public static ChainingConverter of(Class<?> targetType, Converter<Object, Object> delegate) {
            return new ChainingConverter(targetType, delegate);
        }

        public ChainingConverter and(Converter<Object, Object> converter) {
            Assert.notNull(converter, (String)"Converter must not be null!");
            return new ChainingConverter(this.targetType, (Converter<Object, Object>)((Converter)source -> {
                if (source == null || this.targetType.isInstance(source)) {
                    return source;
                }
                Object intermediate = this.convert(source);
                return intermediate == null || this.targetType.isInstance(intermediate) ? intermediate : converter.convert(intermediate);
            }));
        }

        @Nullable
        public Object convert(Object source) {
            return this.delegate.convert(source);
        }
    }
}

