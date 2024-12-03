/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.PropertyMatches
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.mapping.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.PropertyMatches;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class InvalidPersistentPropertyPath
extends MappingException {
    private static final long serialVersionUID = 2805815643641094488L;
    private static final String DEFAULT_MESSAGE = "No property '%s' found on %s! Did you mean: %s?";
    private final String source;
    private final String unresolvableSegment;
    private final String resolvedPath;
    private final TypeInformation<?> type;

    public InvalidPersistentPropertyPath(String source, TypeInformation<?> type, String unresolvableSegment, PersistentPropertyPath<? extends PersistentProperty<?>> resolvedPath) {
        super(InvalidPersistentPropertyPath.createMessage(resolvedPath.isEmpty() ? type : resolvedPath.getRequiredLeafProperty().getTypeInformation(), unresolvableSegment));
        Assert.notNull((Object)source, (String)"Source property path must not be null!");
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull((Object)unresolvableSegment, (String)"Unresolvable segment must not be null!");
        this.source = source;
        this.type = type;
        this.unresolvableSegment = unresolvableSegment;
        this.resolvedPath = InvalidPersistentPropertyPath.toDotPathOrEmpty(resolvedPath);
    }

    public String getSource() {
        return this.source;
    }

    public TypeInformation<?> getType() {
        return this.type;
    }

    public String getUnresolvableSegment() {
        return this.unresolvableSegment;
    }

    public String getResolvedPath() {
        return this.resolvedPath;
    }

    private static String toDotPathOrEmpty(@Nullable PersistentPropertyPath<? extends PersistentProperty<?>> path) {
        if (path == null) {
            return "";
        }
        String dotPath = path.toDotPath();
        return dotPath == null ? "" : dotPath;
    }

    private static String createMessage(TypeInformation<?> type, String unresolvableSegment) {
        Set<String> potentialMatches = InvalidPersistentPropertyPath.detectPotentialMatches(unresolvableSegment, type.getType());
        String match = StringUtils.collectionToCommaDelimitedString(potentialMatches);
        return String.format(DEFAULT_MESSAGE, unresolvableSegment, type.getType(), match);
    }

    private static Set<String> detectPotentialMatches(String propertyName, Class<?> type) {
        HashSet<String> result = new HashSet<String>();
        result.addAll(Arrays.asList(PropertyMatches.forField((String)propertyName, type).getPossibleMatches()));
        result.addAll(Arrays.asList(PropertyMatches.forProperty((String)propertyName, type).getPossibleMatches()));
        return result;
    }
}

