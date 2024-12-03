/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.confluence.plugins.emailgateway.api.AttachmentFile;

@PublicSpi
public interface AttachmentConverter<T> {
    public T convertAttachment(AttachmentFile var1);

    public Class<T> getConversionClass();
}

