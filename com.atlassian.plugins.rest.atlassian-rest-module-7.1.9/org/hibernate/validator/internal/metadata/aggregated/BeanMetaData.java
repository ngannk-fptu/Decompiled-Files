/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.BeanDescriptor
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.Executable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.metadata.BeanDescriptor;
import org.hibernate.validator.internal.engine.groups.Sequence;
import org.hibernate.validator.internal.metadata.aggregated.ExecutableMetaData;
import org.hibernate.validator.internal.metadata.aggregated.PropertyMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.facets.Validatable;

public interface BeanMetaData<T>
extends Validatable {
    public Class<T> getBeanClass();

    public boolean hasConstraints();

    public BeanDescriptor getBeanDescriptor();

    public PropertyMetaData getMetaDataFor(String var1);

    public List<Class<?>> getDefaultGroupSequence(T var1);

    public Iterator<Sequence> getDefaultValidationSequence(T var1);

    public boolean defaultGroupSequenceIsRedefined();

    public Set<MetaConstraint<?>> getMetaConstraints();

    public Set<MetaConstraint<?>> getDirectMetaConstraints();

    public Optional<ExecutableMetaData> getMetaDataFor(Executable var1) throws IllegalArgumentException;

    public List<Class<? super T>> getClassHierarchy();
}

