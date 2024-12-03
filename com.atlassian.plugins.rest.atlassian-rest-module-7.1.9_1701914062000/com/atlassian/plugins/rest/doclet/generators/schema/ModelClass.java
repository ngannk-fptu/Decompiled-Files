/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.HashMultiset
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multiset
 *  com.google.common.collect.Sets
 */
package com.atlassian.plugins.rest.doclet.generators.schema;

import com.atlassian.plugins.rest.doclet.generators.schema.Annotations;
import com.atlassian.plugins.rest.doclet.generators.schema.PatternedProperties;
import com.atlassian.plugins.rest.doclet.generators.schema.Property;
import com.atlassian.plugins.rest.doclet.generators.schema.RichClass;
import com.atlassian.plugins.rest.doclet.generators.schema.Schema;
import com.atlassian.plugins.rest.doclet.generators.schema.Types;
import com.atlassian.rest.annotation.RestProperty;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;

public final class ModelClass
implements Comparable<ModelClass> {
    private final Class<?> actualClass;
    private final RichClass richClass;
    private final AnnotatedElement containingField;
    private final Schema.Type schemaType;

    public ModelClass(RichClass richClass, AnnotatedElement containingField) {
        this.actualClass = richClass.getActualClass();
        this.richClass = richClass;
        this.schemaType = Types.resolveType(richClass, containingField);
        this.containingField = containingField;
    }

    public Class<?> getActualClass() {
        return this.actualClass;
    }

    public Schema.Type getType() {
        return this.schemaType;
    }

    public String getDescription() {
        return Annotations.getDescription(this.containingField);
    }

    public String getTopLevelTitle() {
        if (this.richClass.hasGenericType()) {
            String wrappedTitle = this.richClass.getGenericTypes().stream().map(gt -> new ModelClass((RichClass)gt, null).getTitle()).filter(Objects::nonNull).collect(Collectors.joining("-and-"));
            return Strings.emptyToNull((String)wrappedTitle) != null ? this.getTitle(this.actualClass) + " of " + wrappedTitle : null;
        }
        return this.getTitle(this.actualClass);
    }

    public String getTitle() {
        if (Types.isPrimitive(this.actualClass) || Types.isCollection(this.richClass) || Types.isJDKClass(this.actualClass)) {
            return null;
        }
        return this.getTitle(this.actualClass);
    }

    private String getTitle(Class<?> actualClass) {
        String simplifiedClassName = actualClass.getSimpleName().replaceAll("(Bean|Data|DTO|Dto|Json|JSON|Enum)+$", "").replaceAll("^Abstract", "");
        return this.camelCaseToSpaces(simplifiedClassName);
    }

    private String camelCaseToSpaces(String camelCaseName) {
        CharSequence[] parts = camelCaseName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        return String.join((CharSequence)" ", parts);
    }

    public Set<Property> getProperties(RestProperty.Scope scope) {
        return this.schemaType == Schema.Type.Object ? Sets.newLinkedHashSet(this.getProperties(this.actualClass, scope)) : Collections.emptySet();
    }

    public List<Property> getProperties(Class<?> actualClass, RestProperty.Scope scope) {
        ArrayList result = Lists.newArrayList();
        if (this.schemaType == Schema.Type.Object && actualClass != Object.class && actualClass != null) {
            result.addAll(this.getFieldProperties(actualClass, scope));
            result.addAll(this.getGettersProperties(actualClass, scope));
            result.addAll(0, this.getProperties(actualClass.getSuperclass(), scope));
        }
        return result;
    }

    public Optional<PatternedProperties> getPatternedProperties() {
        if (Map.class.isAssignableFrom(this.actualClass) && this.richClass.getGenericTypes().size() == 2) {
            String pattern = this.containingField != null && this.containingField.isAnnotationPresent(RestProperty.class) ? this.containingField.getAnnotation(RestProperty.class).pattern() : ".+";
            return Optional.of(new PatternedProperties(pattern, new ModelClass(this.richClass.getGenericTypes().get(1), null)));
        }
        return Optional.empty();
    }

    public Optional<ModelClass> getCollectionItemModel() {
        if (this.getType() == Schema.Type.Array) {
            return Optional.of(new ModelClass(this.richClass.getGenericTypes().get(0), null));
        }
        return Optional.empty();
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.actualClass.getModifiers()) && !this.actualClass.isInterface() && !Types.isJDKClass(this.actualClass) && this.actualClass.getPackage() != null;
    }

    public List<ModelClass> getSubModels() {
        TreeSet result = Sets.newTreeSet();
        if (this.isAbstract() && this.actualClass.getTypeParameters().length == 0) {
            Reflections reflections = new Reflections(this.actualClass.getPackage().getName(), new Scanner[0]);
            for (Class<?> aClass : reflections.getSubTypesOf(this.actualClass)) {
                result.add(new ModelClass(RichClass.of(aClass), null));
            }
        }
        return ImmutableList.copyOf((Collection)result);
    }

    public Set<ModelClass> getSchemasReferencedMoreThanOnce(RestProperty.Scope scope) {
        HashMultiset referenceCount = HashMultiset.create();
        ModelClass.computeSchemasReferencedMoreThanOnce(this, scope, (Multiset<ModelClass>)referenceCount);
        ImmutableSet.Builder result = ImmutableSet.builder();
        for (ModelClass modelClass : referenceCount) {
            if (modelClass.getTitle() == null || referenceCount.count((Object)modelClass) <= 1) continue;
            result.add((Object)modelClass);
        }
        return result.build();
    }

    private static void computeSchemasReferencedMoreThanOnce(ModelClass currentNode, RestProperty.Scope scope, Multiset<ModelClass> alreadyReferenced) {
        List propertyModels = currentNode.getProperties(scope).stream().map(input -> input.model).collect(Collectors.toList());
        List<ModelClass> subModels = currentNode.getSubModels();
        for (ModelClass subClass : subModels) {
            alreadyReferenced.add((Object)subClass, 2);
        }
        ArrayList firstLevelModels = Lists.newArrayList();
        firstLevelModels.addAll(subModels);
        firstLevelModels.addAll(propertyModels);
        currentNode.getPatternedProperties().map(PatternedProperties::getValuesType).ifPresent(firstLevelModels::add);
        currentNode.getCollectionItemModel().ifPresent(firstLevelModels::add);
        for (ModelClass firstLevelModel : firstLevelModels) {
            alreadyReferenced.add((Object)firstLevelModel);
            if (alreadyReferenced.count((Object)firstLevelModel) > 1 && !Types.isCollection(firstLevelModel.richClass) && !Map.class.isAssignableFrom(firstLevelModel.actualClass)) continue;
            ModelClass.computeSchemasReferencedMoreThanOnce(firstLevelModel, scope, alreadyReferenced);
        }
    }

    private List<Property> getFieldProperties(Class<?> actualClass, RestProperty.Scope scope) {
        ArrayList properties = Lists.newArrayList();
        for (Field field : actualClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || !Annotations.shouldFieldBeIncludedInSchema(field, field.getName(), actualClass, scope)) continue;
            ModelClass propertyModel = new ModelClass(this.richClass.createContainedType(field.getGenericType()), field);
            properties.add(new Property(propertyModel, Annotations.resolveFieldName(field, field.getName()), Annotations.isRequired(field)));
        }
        return properties;
    }

    private List<Property> getGettersProperties(Class<?> actualClass, RestProperty.Scope scope) {
        ArrayList properties = Lists.newArrayList();
        for (PropertyDescriptor descriptor : this.getPropertyDescriptors(actualClass)) {
            Method getter = descriptor.getReadMethod();
            if (getter == null || getter.getDeclaringClass() == Object.class || !Annotations.shouldFieldBeIncludedInSchema(getter, descriptor.getName(), actualClass, scope)) continue;
            ModelClass propertyModel = new ModelClass(this.richClass.createContainedType(getter.getGenericReturnType()), getter);
            if (!properties.stream().noneMatch(p -> p.model.equals(propertyModel))) continue;
            properties.add(new Property(propertyModel, Annotations.resolveFieldName(getter, descriptor.getName()), Annotations.isRequired(getter)));
        }
        return properties;
    }

    private PropertyDescriptor[] getPropertyDescriptors(Class<?> actualClass) {
        try {
            return Introspector.getBeanInfo(actualClass).getPropertyDescriptors();
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ModelClass that = (ModelClass)o;
        return Objects.equals(this.actualClass, that.actualClass);
    }

    public int hashCode() {
        return Objects.hashCode(this.actualClass);
    }

    @Override
    public int compareTo(ModelClass o) {
        return this.actualClass.getName().compareTo(o.getActualClass().getName());
    }
}

