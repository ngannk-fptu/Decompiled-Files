/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.relations;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.CumulativeContributorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.FavouriteRelationDescriptor;
import com.atlassian.confluence.api.model.relations.LikeRelationDescriptor;
import com.atlassian.confluence.api.model.relations.NamedRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.model.relations.TouchedRelationDescriptor;
import com.atlassian.confluence.api.model.relations.ValidatingRelationDescriptor;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RelationDescriptors {
    private static Map<String, List<RelationDescriptor>> relationDescriptorsByName = new HashMap<String, List<RelationDescriptor>>();

    static synchronized void registerBuiltIn(RelationDescriptor ... relationDescriptors) {
        Objects.requireNonNull(relationDescriptors, "relationDescriptors");
        HashMap<String, List<RelationDescriptor>> newRelationDescriptorsByName = new HashMap<String, List<RelationDescriptor>>(relationDescriptorsByName);
        for (RelationDescriptor relationDescriptor : relationDescriptors) {
            String key = relationDescriptor.getRelationName().toLowerCase(Locale.ENGLISH);
            newRelationDescriptorsByName.putIfAbsent(key, new ArrayList());
            ((List)newRelationDescriptorsByName.get(key)).add(relationDescriptor);
        }
        relationDescriptorsByName = Collections.unmodifiableMap(newRelationDescriptorsByName);
    }

    public static <S extends Relatable, T extends Relatable> RelationDescriptor<S, T> lookupBuiltinOrCreate(final Class<S> sourceClass, final String name, final Class<T> targetClass) {
        Objects.requireNonNull(sourceClass, "sourceClass");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(targetClass, "targetClass");
        final Collection descriptors = relationDescriptorsByName.getOrDefault(name.toLowerCase(Locale.ENGLISH), Collections.emptyList());
        if (descriptors.isEmpty()) {
            return new NamedRelationDescriptor<S, T>(name, sourceClass, targetClass);
        }
        for (RelationDescriptor descriptor : descriptors) {
            if (!sourceClass.isAssignableFrom(descriptor.getSourceClass()) || !targetClass.isAssignableFrom(descriptor.getTargetClass())) continue;
            return descriptor;
        }
        return new ValidatingRelationDescriptor<S, T>(){

            @Override
            public String getRelationName() {
                return name;
            }

            @Override
            public ValidationResult canRelate(Relatable source, Relatable target) {
                return RelationDescriptors.reportInvalidSourceOrTargetType(descriptors, name, source, target);
            }

            @Override
            public Class<S> getSourceClass() {
                return sourceClass;
            }

            @Override
            public Class<T> getTargetClass() {
                return targetClass;
            }
        };
    }

    private static <S extends Relatable, T extends Relatable> ValidationResult reportInvalidSourceOrTargetType(Collection<RelationDescriptor> descriptors, String name, S source, T target) {
        if (!descriptors.stream().anyMatch(x -> x.getSourceClass().isInstance(source))) {
            String validSourceTypes = String.join((CharSequence)", ", descriptors.stream().map(x -> x.getSourceClass().getSimpleName()).distinct().collect(Collectors.toList()));
            return RelationDescriptors.createError(String.format("Source of a %s relation must be of type %s", name, validSourceTypes));
        }
        if (!descriptors.stream().anyMatch(x -> x.getTargetClass().isInstance(target))) {
            String validTargetTypes = String.join((CharSequence)", ", descriptors.stream().map(x -> x.getTargetClass().getSimpleName()).distinct().collect(Collectors.toList()));
            return RelationDescriptors.createError(String.format("Target of a %s relation must be of type %s", name, validTargetTypes));
        }
        throw new IllegalStateException("Source and target types are valid");
    }

    @Deprecated
    public static <S extends Relatable, T extends Relatable> ValidationResult canRelate(S source, RelationDescriptor<S, T> relationDescriptor, T target) {
        if (relationDescriptor instanceof ValidatingRelationDescriptor) {
            return ((ValidatingRelationDescriptor)relationDescriptor).canRelate(source, target);
        }
        if ((source instanceof Space || source instanceof Content) && target instanceof User) {
            return RelationDescriptors.createError("Unrecognised source / type combination");
        }
        if (!relationDescriptor.getSourceClass().isInstance(source)) {
            return RelationDescriptors.createError(String.format("The source of a '%s' relation must be a %s", relationDescriptor.getRelationName(), relationDescriptor.getSourceClass().getSimpleName()));
        }
        if (!relationDescriptor.getTargetClass().isInstance(target)) {
            return RelationDescriptors.createError(String.format("The target of a '%s' relation must be a %s", relationDescriptor.getRelationName(), relationDescriptor.getTargetClass().getSimpleName()));
        }
        return SimpleValidationResult.VALID;
    }

    private static ValidationResult createError(String error) {
        SimpleValidationResult.Builder resultBuilder = SimpleValidationResult.builder().authorized(true);
        resultBuilder.addError(error, new Object[0]);
        return resultBuilder.build();
    }

    static {
        FavouriteRelationDescriptor.register();
        LikeRelationDescriptor.register();
        CollaboratorRelationDescriptor.register();
        TouchedRelationDescriptor.register();
        CumulativeContributorRelationDescriptor.register();
    }
}

