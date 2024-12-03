/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.metadata;

import java.lang.reflect.Method;
import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.springframework.jmx.export.metadata.ManagedAttribute;
import org.springframework.jmx.export.metadata.ManagedMetric;
import org.springframework.jmx.export.metadata.ManagedNotification;
import org.springframework.jmx.export.metadata.ManagedOperation;
import org.springframework.jmx.export.metadata.ManagedOperationParameter;
import org.springframework.jmx.export.metadata.ManagedResource;
import org.springframework.lang.Nullable;

public interface JmxAttributeSource {
    @Nullable
    public ManagedResource getManagedResource(Class<?> var1) throws InvalidMetadataException;

    @Nullable
    public ManagedAttribute getManagedAttribute(Method var1) throws InvalidMetadataException;

    @Nullable
    public ManagedMetric getManagedMetric(Method var1) throws InvalidMetadataException;

    @Nullable
    public ManagedOperation getManagedOperation(Method var1) throws InvalidMetadataException;

    public ManagedOperationParameter[] getManagedOperationParameters(Method var1) throws InvalidMetadataException;

    public ManagedNotification[] getManagedNotifications(Class<?> var1) throws InvalidMetadataException;
}

