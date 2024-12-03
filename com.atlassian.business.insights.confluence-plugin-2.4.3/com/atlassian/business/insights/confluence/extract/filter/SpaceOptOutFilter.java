/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.filter.OptOutEntityIdentifier
 *  com.atlassian.business.insights.api.filter.OptOutEntityType
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.confluence.extract.filter;

import com.atlassian.business.insights.api.filter.OptOutEntityIdentifier;
import com.atlassian.business.insights.api.filter.OptOutEntityType;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpaceOptOutFilter
implements Predicate<String> {
    private final Set<String> optedOutSpaceKeys;

    public SpaceOptOutFilter(@Nonnull List<OptOutEntityIdentifier> optOutEntityIdentifiers) {
        Objects.requireNonNull(optOutEntityIdentifiers, "optOutEntityIdentifiers must not be null");
        this.optedOutSpaceKeys = optOutEntityIdentifiers.stream().filter(optOutResourceIdentifier -> OptOutEntityType.SPACE == optOutResourceIdentifier.getType()).map(OptOutEntityIdentifier::getIdentifier).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public boolean test(@Nullable String spaceKey) {
        return Optional.ofNullable(spaceKey).map(nonNullSpaceKey -> !this.optedOutSpaceKeys.contains(nonNullSpaceKey)).orElse(true);
    }
}

