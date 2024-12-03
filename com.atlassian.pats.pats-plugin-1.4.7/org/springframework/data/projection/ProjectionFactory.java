/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.projection;

import org.springframework.data.projection.ProjectionInformation;
import org.springframework.lang.Nullable;

public interface ProjectionFactory {
    public <T> T createProjection(Class<T> var1, Object var2);

    @Nullable
    default public <T> T createNullableProjection(Class<T> projectionType, @Nullable Object source) {
        return source == null ? null : (T)this.createProjection(projectionType, source);
    }

    public <T> T createProjection(Class<T> var1);

    public ProjectionInformation getProjectionInformation(Class<?> var1);
}

