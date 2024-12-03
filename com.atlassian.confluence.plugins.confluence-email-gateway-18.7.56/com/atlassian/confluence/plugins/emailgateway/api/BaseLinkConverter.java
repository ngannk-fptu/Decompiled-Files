/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.confluence.xhtml.api.Link
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.confluence.plugins.emailgateway.api.LinkConverter;
import com.atlassian.confluence.xhtml.api.Link;

@PublicSpi
public abstract class BaseLinkConverter<B>
implements LinkConverter<B, Link> {
    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public Class<Link> getConversionClass() {
        return Link.class;
    }
}

