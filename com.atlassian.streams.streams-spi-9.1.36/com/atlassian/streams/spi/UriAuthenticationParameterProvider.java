/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;

public interface UriAuthenticationParameterProvider {
    public Option<Pair<String, String>> get();
}

