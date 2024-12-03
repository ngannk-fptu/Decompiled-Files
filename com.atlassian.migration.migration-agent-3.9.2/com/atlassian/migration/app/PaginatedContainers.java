/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.ContainerV1;
import java.util.List;

public interface PaginatedContainers {
    public boolean next();

    public List<ContainerV1> getContainers();
}

