/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.model.status;

import com.atlassian.applinks.internal.rest.model.status.RestApplinkError;
import com.atlassian.applinks.internal.status.error.AuthorisationUriAwareApplinkError;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RestAuthorisationUriAwareApplinkError
extends RestApplinkError {
    public static final String AUTHORISATION_URI = "authorisationUri";
    private URI authorisationUri;

    public RestAuthorisationUriAwareApplinkError(@Nonnull AuthorisationUriAwareApplinkError error, @Nullable URI callback) {
        super(error);
        this.authorisationUri = callback != null ? error.getAuthorisationUriGenerator().getAuthorisationURI(callback) : error.getAuthorisationUriGenerator().getAuthorisationURI();
    }

    public RestAuthorisationUriAwareApplinkError(@Nonnull AuthorisationUriAwareApplinkError error) {
        this(error, null);
    }
}

