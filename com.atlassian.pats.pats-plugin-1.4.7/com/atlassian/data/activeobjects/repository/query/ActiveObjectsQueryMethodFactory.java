/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethod;
import java.lang.reflect.Method;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

public interface ActiveObjectsQueryMethodFactory {
    public ActiveObjectsQueryMethod build(Method var1, RepositoryMetadata var2, ProjectionFactory var3);
}

