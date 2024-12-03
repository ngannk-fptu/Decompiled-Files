/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.web.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugin.web.api.WebFragment;
import javax.annotation.Nonnull;

@PublicApi
public interface WebItem
extends WebFragment {
    @Nonnull
    public String getSection();

    @Nonnull
    public String getUrl();

    public String getAccessKey();

    public String getEntryPoint();
}

