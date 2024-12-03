/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.json.jsonorg.JSONException
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.Jsonable$JsonMappingException
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  javax.inject.Inject
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.pulp.wrm;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.plugins.pulp.wrm.ApplicationLinkStatusInterrogator;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.Objects;
import javax.inject.Inject;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public class ApplinksStatusWrmDataProvider
implements WebResourceDataProvider {
    private final ApplicationLinkStatusInterrogator applicationLinkStatusInterrogator;

    @Inject
    public ApplinksStatusWrmDataProvider(ApplicationLinkStatusInterrogator applicationLinkStatusInterrogator) {
        this.applicationLinkStatusInterrogator = Objects.requireNonNull(applicationLinkStatusInterrogator);
    }

    public @NonNull Jsonable get() {
        return writer -> {
            try {
                this.getBodyContentJsonObject().write(writer);
            }
            catch (JSONException e) {
                throw new Jsonable.JsonMappingException((Throwable)e);
            }
        };
    }

    private JSONObject getBodyContentJsonObject() throws JSONException {
        JSONObject appLinkStatus = new JSONObject();
        if (!this.applicationLinkStatusInterrogator.areApplicationLinksInstalled()) {
            return appLinkStatus;
        }
        long failedApplinks = this.applicationLinkStatusInterrogator.getNumberOfFailedApplicationLinks();
        appLinkStatus.put("failedApplinks", failedApplinks);
        return appLinkStatus;
    }
}

