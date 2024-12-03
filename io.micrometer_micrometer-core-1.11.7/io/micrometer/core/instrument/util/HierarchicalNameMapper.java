/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.util;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import java.util.stream.Collectors;

public interface HierarchicalNameMapper {
    public static final HierarchicalNameMapper DEFAULT = (id, convention) -> id.getConventionName(convention) + id.getConventionTags(convention).stream().map(t -> "." + t.getKey() + "." + t.getValue()).map(nameSegment -> nameSegment.replace(" ", "_")).collect(Collectors.joining(""));

    public String toHierarchicalName(Meter.Id var1, NamingConvention var2);
}

