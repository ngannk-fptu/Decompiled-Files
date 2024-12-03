/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand.entity;

import com.atlassian.plugins.rest.common.expand.entity.ListWrapperCallback;

public interface ListWrapper<T> {
    public ListWrapperCallback<T> getCallback();
}

