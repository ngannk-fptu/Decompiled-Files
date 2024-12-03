/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.provider;

import org.hibernate.validator.internal.metadata.core.AnnotationProcessingOptions;
import org.hibernate.validator.internal.metadata.raw.BeanConfiguration;

public interface MetaDataProvider {
    public AnnotationProcessingOptions getAnnotationProcessingOptions();

    public <T> BeanConfiguration<? super T> getBeanConfiguration(Class<T> var1);
}

