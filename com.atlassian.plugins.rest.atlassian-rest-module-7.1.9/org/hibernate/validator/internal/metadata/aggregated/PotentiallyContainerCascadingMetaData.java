/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.metadata.GroupConversionDescriptor
 */
package org.hibernate.validator.internal.metadata.aggregated;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Set;
import javax.validation.metadata.GroupConversionDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.AnnotatedObject;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorDescriptor;
import org.hibernate.validator.internal.engine.valueextraction.ValueExtractorManager;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;
import org.hibernate.validator.internal.metadata.aggregated.ContainerCascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.GroupConversionHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public class PotentiallyContainerCascadingMetaData
implements CascadingMetaData {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private final GroupConversionHelper groupConversionHelper;
    private final Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors;

    public static PotentiallyContainerCascadingMetaData of(CascadingMetaDataBuilder cascadingMetaDataBuilder, Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors, Object context) {
        return new PotentiallyContainerCascadingMetaData(cascadingMetaDataBuilder, potentialValueExtractorDescriptors);
    }

    private PotentiallyContainerCascadingMetaData(CascadingMetaDataBuilder cascadingMetaDataBuilder, Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors) {
        this(potentialValueExtractorDescriptors, GroupConversionHelper.of(cascadingMetaDataBuilder.getGroupConversions()));
    }

    private PotentiallyContainerCascadingMetaData(Set<ValueExtractorDescriptor> potentialValueExtractorDescriptors, GroupConversionHelper groupConversionHelper) {
        this.potentialValueExtractorDescriptors = potentialValueExtractorDescriptors;
        this.groupConversionHelper = groupConversionHelper;
    }

    @Override
    public TypeVariable<?> getTypeParameter() {
        return AnnotatedObject.INSTANCE;
    }

    @Override
    public boolean isCascading() {
        return true;
    }

    @Override
    public boolean isMarkedForCascadingOnAnnotatedObjectOrContainerElements() {
        return true;
    }

    @Override
    public Class<?> convertGroup(Class<?> originalGroup) {
        return this.groupConversionHelper.convertGroup(originalGroup);
    }

    @Override
    public Set<GroupConversionDescriptor> getGroupConversionDescriptors() {
        return this.groupConversionHelper.asDescriptors();
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public CascadingMetaData addRuntimeContainerSupport(ValueExtractorManager valueExtractorManager, Class<?> valueClass) {
        ValueExtractorDescriptor compliantValueExtractor = valueExtractorManager.getResolver().getMaximallySpecificValueExtractorForAllContainerElements(valueClass, this.potentialValueExtractorDescriptors);
        if (compliantValueExtractor == null) {
            return this;
        }
        return new ContainerCascadingMetaData(valueClass, Collections.singletonList(new ContainerCascadingMetaData(compliantValueExtractor.getContainerType(), compliantValueExtractor.getExtractedTypeParameter(), compliantValueExtractor.getContainerType(), compliantValueExtractor.getExtractedTypeParameter(), this.groupConversionHelper.isEmpty() ? GroupConversionHelper.EMPTY : this.groupConversionHelper)), this.groupConversionHelper, Collections.singleton(compliantValueExtractor));
    }

    @Override
    public <T extends CascadingMetaData> T as(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.getClass())) {
            return (T)this;
        }
        throw LOG.getUnableToCastException(this, clazz);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [");
        sb.append("groupConversions=").append(this.groupConversionHelper).append(", ");
        sb.append("]");
        return sb.toString();
    }
}

