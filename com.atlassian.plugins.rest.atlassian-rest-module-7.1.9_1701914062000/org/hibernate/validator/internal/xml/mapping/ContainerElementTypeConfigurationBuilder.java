/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.xml.mapping;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypePath;
import org.hibernate.validator.internal.xml.mapping.ContainerElementTypeStaxBuilder;

class ContainerElementTypeConfigurationBuilder {
    private final List<ContainerElementTypeStaxBuilder> containerElementTypeStaxBuilders = new ArrayList<ContainerElementTypeStaxBuilder>();
    private final Set<ContainerElementTypePath> configuredPaths = new HashSet<ContainerElementTypePath>();

    ContainerElementTypeConfigurationBuilder() {
    }

    public void add(ContainerElementTypeStaxBuilder containerElementTypeStaxBuilder) {
        this.containerElementTypeStaxBuilders.add(containerElementTypeStaxBuilder);
    }

    ContainerElementTypeConfiguration build(ConstraintLocation parentConstraintLocation, Type enclosingType) {
        return this.build(ContainerElementTypePath.root(), parentConstraintLocation, enclosingType);
    }

    private ContainerElementTypeConfiguration build(ContainerElementTypePath parentConstraintElementTypePath, ConstraintLocation parentConstraintLocation, Type enclosingType) {
        return this.containerElementTypeStaxBuilders.stream().map(builder -> builder.build(this.configuredPaths, parentConstraintElementTypePath, parentConstraintLocation, enclosingType)).reduce(ContainerElementTypeConfiguration.EMPTY_CONFIGURATION, ContainerElementTypeConfiguration::merge);
    }

    static class ContainerElementTypeConfiguration {
        public static final ContainerElementTypeConfiguration EMPTY_CONFIGURATION = new ContainerElementTypeConfiguration(Collections.emptySet(), Collections.emptyMap());
        private final Set<MetaConstraint<?>> metaConstraints;
        private final Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaDataBuilder;

        ContainerElementTypeConfiguration(Set<MetaConstraint<?>> metaConstraints, Map<TypeVariable<?>, CascadingMetaDataBuilder> containerElementTypesCascadingMetaData) {
            this.metaConstraints = metaConstraints;
            this.containerElementTypesCascadingMetaDataBuilder = containerElementTypesCascadingMetaData;
        }

        public Set<MetaConstraint<?>> getMetaConstraints() {
            return this.metaConstraints;
        }

        public Map<TypeVariable<?>, CascadingMetaDataBuilder> getTypeParametersCascadingMetaData() {
            return this.containerElementTypesCascadingMetaDataBuilder;
        }

        public static ContainerElementTypeConfiguration merge(ContainerElementTypeConfiguration l, ContainerElementTypeConfiguration r) {
            return new ContainerElementTypeConfiguration(Stream.concat(l.metaConstraints.stream(), r.metaConstraints.stream()).collect(Collectors.toSet()), Stream.concat(l.containerElementTypesCascadingMetaDataBuilder.entrySet().stream(), r.containerElementTypesCascadingMetaDataBuilder.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
    }
}

