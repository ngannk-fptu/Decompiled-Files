/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.plugins.mobile.dto.LinkExtractorDto;
import javax.annotation.Nonnull;

public interface LinkExtractorService {
    @Nonnull
    public LinkExtractorDto extractor(@Nonnull String var1);
}

