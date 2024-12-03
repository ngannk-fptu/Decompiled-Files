/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.confluence.xhtml.api.LinkBody
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.confluence.xhtml.api.LinkBody;
import java.net.URL;

@PublicSpi
public interface LinkConverter<B, T> {
    public boolean isFinal();

    public T convert(URL var1, LinkBody<B> var2);

    public Class<T> getConversionClass();
}

