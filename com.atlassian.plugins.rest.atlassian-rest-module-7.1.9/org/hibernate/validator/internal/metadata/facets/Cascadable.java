/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.facets;

import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaData;
import org.hibernate.validator.internal.metadata.aggregated.CascadingMetaDataBuilder;

public interface Cascadable {
    public ElementType getElementType();

    public Type getCascadableType();

    public Object getValue(Object var1);

    public void appendTo(PathImpl var1);

    public CascadingMetaData getCascadingMetaData();

    public static interface Builder {
        public void mergeCascadingMetaData(CascadingMetaDataBuilder var1);

        public Cascadable build();
    }
}

