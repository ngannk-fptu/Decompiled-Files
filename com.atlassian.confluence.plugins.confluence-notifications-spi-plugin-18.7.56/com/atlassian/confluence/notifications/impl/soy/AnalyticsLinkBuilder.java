/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.notifications.impl.soy;

import com.atlassian.fugue.Option;
import java.util.Optional;

public interface AnalyticsLinkBuilder {
    @Deprecated
    public String buildAnalyticsQuery(String var1, Option<String> var2);

    default public String buildAnalyticsQuery(String link, Optional<String> action) {
        return this.buildAnalyticsQuery(link, (Option<String>)Option.option((Object)action.orElse(null)));
    }
}

