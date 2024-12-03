/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.GroupConversionDescriptor
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.reflect.TypeVariable;
import java.util.Set;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;

public interface CascadingMetaData {
    public TypeVariable<?> getTypeParameter();

    public boolean isCascading();

    public boolean isMarkedForCascadingOnAnnotatedObjectOrContainerElements();

    public Class<?> convertGroup(Class<?> var1);

    public Set<GroupConversionDescriptor> getGroupConversionDescriptors();

    public boolean isContainer();

    public <T extends CascadingMetaData> T as(Class<T> var1);

    public CascadingMetaData addRuntimeContainerSupport(ValueExtractorManager var1, Class<?> var2);
}

