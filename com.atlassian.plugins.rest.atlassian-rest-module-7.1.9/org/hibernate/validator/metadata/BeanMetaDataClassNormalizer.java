/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.metadata;

import org.hibernate.validator.Incubating;

@Incubating
public interface BeanMetaDataClassNormalizer {
    public <T> Class<? super T> normalize(Class<T> var1);
}

