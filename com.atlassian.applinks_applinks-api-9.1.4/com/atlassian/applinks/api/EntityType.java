/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import java.net.URI;
import javax.annotation.Nullable;

@PublicSpi
public interface EntityType {
    public Class<? extends ApplicationType> getApplicationType();

    public String getI18nKey();

    public String getPluralizedI18nKey();

    public String getShortenedI18nKey();

    @Nullable
    @Deprecated
    public URI getIconUrl();

    public URI getDisplayUrl(ApplicationLink var1, String var2);
}

