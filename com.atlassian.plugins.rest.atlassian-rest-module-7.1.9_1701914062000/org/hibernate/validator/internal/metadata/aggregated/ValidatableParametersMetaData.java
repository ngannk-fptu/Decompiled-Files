/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.validator.internal.metadata.aggregated.ParameterMetaData;
import org.hibernate.validator.internal.metadata.facets.Cascadable;
import org.hibernate.validator.internal.metadata.facets.Validatable;
import org.hibernate.validator.internal.util.CollectionHelper;

public class ValidatableParametersMetaData
implements Validatable {
    private final List<ParameterMetaData> parameterMetaData;
    private final List<Cascadable> cascadables;

    public ValidatableParametersMetaData(List<ParameterMetaData> parameterMetaData) {
        this.parameterMetaData = CollectionHelper.toImmutableList(parameterMetaData);
        this.cascadables = CollectionHelper.toImmutableList(parameterMetaData.stream().filter(p -> p.isCascading()).collect(Collectors.toList()));
    }

    @Override
    public Iterable<Cascadable> getCascadables() {
        return this.cascadables;
    }

    @Override
    public boolean hasCascadables() {
        return !this.cascadables.isEmpty();
    }
}

