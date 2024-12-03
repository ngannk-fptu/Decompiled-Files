/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.query;

import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethod;
import com.atlassian.data.activeobjects.repository.query.ActiveObjectsQueryMethodFactory;
import java.lang.reflect.Method;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

public class DefaultActiveObjectsQueryMethodFactory
implements ActiveObjectsQueryMethodFactory {
    @Override
    public ActiveObjectsQueryMethod build(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        return new ActiveObjectsQueryMethod(method, metadata, factory);
    }
}

