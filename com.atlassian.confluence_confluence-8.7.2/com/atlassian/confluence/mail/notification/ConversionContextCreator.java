/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.notification;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;

public class ConversionContextCreator {
    public ConversionContext createConversionContext(ContentEntityObject ceo) {
        return new DefaultConversionContext(ceo.toPageContext());
    }
}

