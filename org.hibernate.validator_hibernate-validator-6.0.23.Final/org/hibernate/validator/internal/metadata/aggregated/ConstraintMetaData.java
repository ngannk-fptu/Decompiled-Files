/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ElementKind
 *  javax.validation.metadata.ElementDescriptor
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Type;
import java.util.List;
import javax.validation.ElementKind;
import javax.validation.metadata.ElementDescriptor;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;

public interface ConstraintMetaData
extends Iterable<MetaConstraint<?>> {
    public String getName();

    public Type getType();

    public ElementKind getKind();

    public boolean isCascading();

    public boolean isConstrained();

    public ElementDescriptor asDescriptor(boolean var1, List<Class<?>> var2);
}

