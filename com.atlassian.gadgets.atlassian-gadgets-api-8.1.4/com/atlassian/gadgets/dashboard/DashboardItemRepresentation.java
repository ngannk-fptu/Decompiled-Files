/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.annotations.PublicApi;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Option;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public class DashboardItemRepresentation {
    private final String jsonRepresentation;
    private final String html;
    private final String amdModule;
    private final String gadgetUrl;

    private DashboardItemRepresentation(@Nullable String jsonRepresentation, @Nullable String html, @Nullable String amdModule, @Nullable String gadgetUrl) {
        this.jsonRepresentation = jsonRepresentation;
        this.html = html;
        this.amdModule = amdModule;
        this.gadgetUrl = gadgetUrl;
    }

    public Option<String> getJsonRepresentation() {
        return Option.option((Object)this.jsonRepresentation);
    }

    public Option<String> getAmdModule() {
        return Option.option((Object)this.amdModule);
    }

    public Option<String> getHtml() {
        return Option.option((Object)this.html);
    }

    public Option<String> getGadgetUrl() {
        return Option.option((Object)this.gadgetUrl);
    }

    public static DashboardItemRepresentation dashboardItem(@Nullable String amdModule, @Nullable String html, @Nonnull String jsonRepresentation) {
        Preconditions.checkArgument((amdModule != null || html != null ? 1 : 0) != 0);
        return new DashboardItemRepresentation((String)Preconditions.checkNotNull((Object)jsonRepresentation), html, amdModule, null);
    }

    public static DashboardItemRepresentation openSocialGadget(@Nonnull String gadgetUrl) {
        return new DashboardItemRepresentation(null, null, null, (String)Preconditions.checkNotNull((Object)gadgetUrl));
    }
}

