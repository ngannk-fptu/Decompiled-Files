/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.annotation.MergedAnnotation
 *  org.springframework.core.annotation.MergedAnnotations
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.data.web.querydsl;

import com.querydsl.core.types.Predicate;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder;
import org.springframework.data.util.CastUtils;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

public abstract class QuerydslPredicateArgumentResolverSupport {
    private static final ResolvableType PREDICATE = ResolvableType.forClass(Predicate.class);
    static final ResolvableType OPTIONAL_OF_PREDICATE = ResolvableType.forClassWithGenerics(Optional.class, (ResolvableType[])new ResolvableType[]{PREDICATE});
    protected final QuerydslBindingsFactory bindingsFactory;
    protected final QuerydslPredicateBuilder predicateBuilder;

    protected QuerydslPredicateArgumentResolverSupport(QuerydslBindingsFactory factory, ConversionService conversionService) {
        Assert.notNull((Object)factory, (String)"QuerydslBindingsFactory must not be null");
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null");
        this.bindingsFactory = factory;
        this.predicateBuilder = new QuerydslPredicateBuilder(conversionService, factory.getEntityPathResolver());
    }

    public boolean supportsParameter(MethodParameter parameter) {
        ResolvableType type = ResolvableType.forMethodParameter((MethodParameter)parameter);
        if (PREDICATE.isAssignableFrom(type) || OPTIONAL_OF_PREDICATE.isAssignableFrom(type)) {
            return true;
        }
        MergedAnnotations annotations = MergedAnnotations.from((AnnotatedElement)parameter.getParameter());
        if (annotations.isPresent(QuerydslPredicate.class)) {
            throw new IllegalArgumentException(String.format("Parameter at position %s must be of type Predicate but was %s.", parameter.getParameterIndex(), parameter.getParameterType()));
        }
        return false;
    }

    Predicate getPredicate(MethodParameter parameter, MultiValueMap<String, String> queryParameters) {
        MergedAnnotations annotations = MergedAnnotations.from((AnnotatedElement)parameter.getParameter());
        MergedAnnotation predicateAnnotation = annotations.get(QuerydslPredicate.class);
        TypeInformation<?> domainType = QuerydslPredicateArgumentResolverSupport.extractTypeInfo(parameter, (MergedAnnotation<QuerydslPredicate>)predicateAnnotation).getRequiredActualType();
        Optional<Class> bindingsAnnotation = predicateAnnotation.getValue("bindings").map(CastUtils::cast);
        QuerydslBindings bindings = bindingsAnnotation.map(it -> this.bindingsFactory.createBindingsFor(domainType, (Class<? extends QuerydslBinderCustomizer<?>>)it)).orElseGet(() -> this.bindingsFactory.createBindingsFor(domainType));
        return this.predicateBuilder.getPredicate(domainType, queryParameters, bindings);
    }

    @Nullable
    static Object potentiallyConvertMethodParameterValue(MethodParameter parameter, Predicate predicate) {
        if (!parameter.isOptional()) {
            return predicate;
        }
        if (OPTIONAL_OF_PREDICATE.isAssignableFrom(ResolvableType.forMethodParameter((MethodParameter)parameter))) {
            return QuerydslPredicateBuilder.isEmpty(predicate) ? Optional.empty() : Optional.of(predicate);
        }
        return QuerydslPredicateBuilder.isEmpty(predicate) ? null : predicate;
    }

    protected static TypeInformation<?> extractTypeInfo(MethodParameter parameter, MergedAnnotation<QuerydslPredicate> predicateAnnotation) {
        Optional annotation = predicateAnnotation.synthesize(MergedAnnotation::isPresent);
        return annotation.filter(it -> !Object.class.equals(it.root())).map(it -> ClassTypeInformation.from(it.root())).orElseGet(() -> QuerydslPredicateArgumentResolverSupport.detectDomainType(parameter));
    }

    private static TypeInformation<?> detectDomainType(MethodParameter parameter) {
        Method method = parameter.getMethod();
        if (method == null) {
            throw new IllegalArgumentException("Method parameter is not backed by a method!");
        }
        return QuerydslPredicateArgumentResolverSupport.detectDomainType(ClassTypeInformation.fromReturnTypeOf(method));
    }

    private static TypeInformation<?> detectDomainType(TypeInformation<?> source) {
        if (source.getTypeArguments().isEmpty()) {
            return source;
        }
        TypeInformation<?> actualType = source.getActualType();
        if (actualType == null) {
            throw new IllegalArgumentException(String.format("Could not determine domain type from %s!", source));
        }
        if (source != actualType) {
            return QuerydslPredicateArgumentResolverSupport.detectDomainType(actualType);
        }
        if (source instanceof Iterable) {
            return source;
        }
        return QuerydslPredicateArgumentResolverSupport.detectDomainType(source.getRequiredComponentType());
    }
}

