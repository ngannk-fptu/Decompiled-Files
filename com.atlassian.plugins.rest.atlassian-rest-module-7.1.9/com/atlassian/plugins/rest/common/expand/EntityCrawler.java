/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.rest.common.expand;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugins.rest.common.expand.AdditionalExpandsProvider;
import com.atlassian.plugins.rest.common.expand.DefaultExpandContext;
import com.atlassian.plugins.rest.common.expand.EntityExpander;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.parameter.ExpandParameter;
import com.atlassian.plugins.rest.common.expand.resolver.EntityExpanderResolver;
import com.atlassian.plugins.rest.common.util.ReflectionUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.StringUtils;

public class EntityCrawler {
    private final Collection<? extends AdditionalExpandsProvider> additionalExpandsProviders;
    @TenantAware(value=TenancyScope.TENANTLESS)
    private LoadingCache<Class, List<Field>> declaredFields = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<Class, List<Field>>(){

        public List<Field> load(@Nonnull Class cls) {
            return ImmutableList.copyOf(ReflectionUtils.getDeclaredFields(cls));
        }
    });
    @TenantAware(value=TenancyScope.TENANTLESS)
    private LoadingCache<Class, Optional<Field>> expandFields = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<Class, Optional<Field>>(){

        public Optional<Field> load(@Nonnull Class cls) {
            for (Field field : (List)EntityCrawler.this.declaredFields.getUnchecked((Object)cls)) {
                XmlAttribute annotation;
                if (!field.getType().equals(String.class) || (annotation = field.getAnnotation(XmlAttribute.class)) == null || !field.getName().equals("expand") && !"expand".equals(annotation.name())) continue;
                return Optional.of(field);
            }
            return Optional.empty();
        }
    });

    public EntityCrawler() {
        this.additionalExpandsProviders = Collections.emptyList();
    }

    public EntityCrawler(Collection<? extends AdditionalExpandsProvider> additionalExpandsProviders) {
        this.additionalExpandsProviders = additionalExpandsProviders;
    }

    public void crawl(Object entity, ExpandParameter expandParameter, EntityExpanderResolver expanderResolver) {
        if (entity == null) {
            return;
        }
        Collection<Field> expandableFields = this.getExpandableFields(entity);
        this.setExpandParameter(expandableFields, entity);
        this.expandFields(expandableFields, entity, expandParameter, expanderResolver);
    }

    private Collection<String> getAdditionalExpands(Object entity) {
        return this.additionalExpandsProviders.stream().filter(provider -> provider.getSupportedType().isInstance(entity)).flatMap(filteredProvider -> this.getAdditionalExpandFromProvider(entity, (AdditionalExpandsProvider)filteredProvider).stream()).collect(Collectors.toList());
    }

    private List<String> getAdditionalExpandFromProvider(Object entity, AdditionalExpandsProvider filteredProvider) {
        return filteredProvider.getAdditionalExpands(filteredProvider.getSupportedType().cast(entity));
    }

    private void setExpandParameter(Collection<Field> expandableFields, Object entity) {
        Optional expand = (Optional)this.expandFields.getUnchecked(entity.getClass());
        if (expand.isPresent() && !expandableFields.isEmpty()) {
            String expandValue = this.createExpandString(expandableFields, this.getAdditionalExpands(entity));
            ReflectionUtils.setFieldValue((Field)expand.get(), entity, expandValue);
        }
    }

    private String createExpandString(Collection<Field> expandableFields, Collection<String> additionalExpands) {
        return Stream.concat(expandableFields.stream().map(this::getExpandable).map(Expandable::value), additionalExpands.stream()).distinct().collect(Collectors.joining(","));
    }

    private Collection<Field> getExpandableFields(Object entity) {
        return ImmutableList.copyOf((Collection)((List)this.declaredFields.getUnchecked(entity.getClass())).stream().filter(field -> this.getExpandable((Field)field) != null && ReflectionUtils.getFieldValue(field, entity) != null).collect(Collectors.toList()));
    }

    private void expandFields(Collection<Field> expandableFields, Object entity, ExpandParameter expandParameter, EntityExpanderResolver expanderResolver) {
        for (Field field : expandableFields) {
            Expandable expandable = this.getExpandable(field);
            if (!expandParameter.shouldExpand(expandable) || !expanderResolver.hasExpander(field.getType())) continue;
            EntityExpander<?> entityExpander = expanderResolver.getExpander(field.getType());
            DefaultExpandContext<Object> context = new DefaultExpandContext<Object>(ReflectionUtils.getFieldValue(field, entity), expandable, expandParameter);
            ReflectionUtils.setFieldValue(field, entity, entityExpander.expand(context, expanderResolver, this));
        }
    }

    Expandable getExpandable(Field field) {
        if (field == null) {
            return null;
        }
        Expandable expandable = field.getAnnotation(Expandable.class);
        if (expandable == null) {
            return null;
        }
        if (StringUtils.isNotEmpty((CharSequence)expandable.value())) {
            return expandable;
        }
        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
        if (xmlElement != null && StringUtils.isNotEmpty((CharSequence)xmlElement.name()) && !StringUtils.equals((CharSequence)"##default", (CharSequence)xmlElement.name())) {
            return new ExpandableWithValue(xmlElement.name());
        }
        return new ExpandableWithValue(field.getName());
    }

    private static class ExpandableWithValue
    implements Expandable {
        private final String value;

        public ExpandableWithValue(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return this.value;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Expandable.class;
        }
    }
}

