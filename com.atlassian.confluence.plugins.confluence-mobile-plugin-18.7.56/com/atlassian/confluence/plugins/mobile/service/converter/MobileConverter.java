/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service.converter;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface MobileConverter<D, S> {
    public D to(@Nonnull S var1);

    default public D to(@Nonnull S source, @Nonnull Expansions expansions) {
        throw new NotImplementedServiceException("Convert source with expansions is not implemented yet.");
    }

    default public List<D> to(List<S> sources) {
        throw new NotImplementedServiceException("Convert list objects is not implemented yet.");
    }

    default public List<D> to(@Nonnull List<S> sources, @Nonnull Expansions expansions) {
        throw new NotImplementedServiceException("Convert list objects with expansions is not implemented yet.");
    }

    default public List<D> to(List<S> sources, Predicate<S> filter) {
        throw new NotImplementedServiceException("Convert list objects with filter is not implemented yet.");
    }
}

