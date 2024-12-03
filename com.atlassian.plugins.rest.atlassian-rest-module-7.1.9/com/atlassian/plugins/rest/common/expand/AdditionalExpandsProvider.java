/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.expand;

import java.util.List;

public interface AdditionalExpandsProvider<T> {
    public List<String> getAdditionalExpands(T var1);

    public Class<T> getSupportedType();
}

