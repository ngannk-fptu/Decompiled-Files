/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.hibernate;

import java.util.List;
import java.util.function.Function;

interface CustomDataFetcher<T> {
    public List<String> attributes(String var1);

    public Function<Object[], T> getTransformer(int var1);
}

