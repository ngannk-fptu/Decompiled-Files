/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.core;

import java.lang.reflect.Member;

public interface AnnotationProcessingOptions {
    public boolean areClassLevelConstraintsIgnoredFor(Class<?> var1);

    public boolean areMemberConstraintsIgnoredFor(Member var1);

    public boolean areReturnValueConstraintsIgnoredFor(Member var1);

    public boolean areCrossParameterConstraintsIgnoredFor(Member var1);

    public boolean areParameterConstraintsIgnoredFor(Member var1, int var2);

    public void merge(AnnotationProcessingOptions var1);
}

