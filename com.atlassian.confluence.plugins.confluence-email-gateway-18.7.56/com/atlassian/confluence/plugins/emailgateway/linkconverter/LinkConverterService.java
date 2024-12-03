/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.linkconverter;

import com.atlassian.confluence.plugins.emailgateway.api.LinkConverter;

public interface LinkConverterService {
    public Iterable<LinkConverter<?, ?>> getLinkConverters();
}

