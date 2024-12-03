/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.entity;

import com.atlassian.plugins.rest.common.expand.parameter.Indexes;
import java.util.List;

public interface ListWrapperCallback<T> {
    public List<T> getItems(Indexes var1);
}

