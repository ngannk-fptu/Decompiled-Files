/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata;

import org.hibernate.validator.metadata.BeanMetaDataClassNormalizer;

public class DefaultBeanMetaDataClassNormalizer
implements BeanMetaDataClassNormalizer {
    public <T> Class<T> normalize(Class<T> beanClass) {
        return beanClass;
    }
}

