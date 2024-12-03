/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyMatches
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.PropertyMatches;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.util.Lazy;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class PropertyReferenceException
extends RuntimeException {
    private static final long serialVersionUID = -5254424051438976570L;
    private static final String ERROR_TEMPLATE = "No property %s found for type %s!";
    private static final String HINTS_TEMPLATE = " Did you mean %s?";
    private final String propertyName;
    private final TypeInformation<?> type;
    private final List<PropertyPath> alreadyResolvedPath;
    private final Lazy<Set<String>> propertyMatches;

    public PropertyReferenceException(String propertyName, TypeInformation<?> type, List<PropertyPath> alreadyResolvedPah) {
        Assert.hasText((String)propertyName, (String)"Property name must not be null!");
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull(alreadyResolvedPah, (String)"Already resolved paths must not be null!");
        this.propertyName = propertyName;
        this.type = type;
        this.alreadyResolvedPath = alreadyResolvedPah;
        this.propertyMatches = Lazy.of(() -> PropertyReferenceException.detectPotentialMatches(propertyName, type.getType()));
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public TypeInformation<?> getType() {
        return this.type;
    }

    Collection<String> getPropertyMatches() {
        return this.propertyMatches.get();
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder(String.format(ERROR_TEMPLATE, this.propertyName, this.type.getType().getSimpleName()));
        Collection<String> potentialMatches = this.getPropertyMatches();
        if (!potentialMatches.isEmpty()) {
            String matches = StringUtils.collectionToDelimitedString(potentialMatches, (String)",", (String)"'", (String)"'");
            builder.append(String.format(HINTS_TEMPLATE, matches));
        }
        if (!this.alreadyResolvedPath.isEmpty()) {
            builder.append(" Traversed path: ");
            builder.append(this.alreadyResolvedPath.get(0).toString());
            builder.append(".");
        }
        return builder.toString();
    }

    @Nullable
    public PropertyPath getBaseProperty() {
        return this.alreadyResolvedPath.isEmpty() ? null : this.alreadyResolvedPath.get(this.alreadyResolvedPath.size() - 1);
    }

    public boolean hasDeeperResolutionDepthThan(PropertyReferenceException exception) {
        return this.alreadyResolvedPath.size() > exception.alreadyResolvedPath.size();
    }

    private static Set<String> detectPotentialMatches(String propertyName, Class<?> type) {
        HashSet<String> result = new HashSet<String>();
        result.addAll(Arrays.asList(PropertyMatches.forField((String)propertyName, type).getPossibleMatches()));
        result.addAll(Arrays.asList(PropertyMatches.forProperty((String)propertyName, type).getPossibleMatches()));
        return result;
    }
}

