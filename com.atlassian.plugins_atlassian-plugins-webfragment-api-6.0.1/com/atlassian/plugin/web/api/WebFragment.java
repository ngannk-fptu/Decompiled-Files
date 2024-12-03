/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.web.api;

import com.atlassian.annotations.PublicApi;
import java.util.Map;
import javax.annotation.Nonnull;

@PublicApi
public interface WebFragment {
    public String getCompleteKey();

    public String getLabel();

    public String getTitle();

    public String getStyleClass();

    public String getId();

    @Nonnull
    public Map<String, String> getParams();

    public int getWeight();
}

