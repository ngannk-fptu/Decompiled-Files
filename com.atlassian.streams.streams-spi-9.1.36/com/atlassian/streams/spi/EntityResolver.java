/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Function
 */
package com.atlassian.streams.spi;

import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;

@Deprecated
public interface EntityResolver
extends Function<String, Option<Object>>,
java.util.function.Function<String, Option<Object>> {
}

