/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.render.prefetch;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.impl.content.render.prefetch.ResourceIdentifiers;

public interface ResourceIdentifierExtractor {
    public boolean handles(BodyType var1);

    public ResourceIdentifiers extractResourceIdentifiers(BodyContent var1, ConversionContext var2) throws Exception;
}

