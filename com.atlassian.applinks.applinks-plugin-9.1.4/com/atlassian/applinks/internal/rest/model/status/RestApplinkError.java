/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.model.status;

import com.atlassian.applinks.internal.rest.model.ApplinksRestRepresentation;
import com.atlassian.applinks.internal.rest.model.status.RestAuthorisationUriAwareApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkError;
import com.atlassian.applinks.internal.status.error.ApplinkErrorVisitor;
import com.atlassian.applinks.internal.status.error.AuthorisationUriAwareApplinkError;
import com.atlassian.applinks.internal.status.error.ResponseApplinkError;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RestApplinkError
extends ApplinksRestRepresentation {
    public static final String CATEGORY = "category";
    public static final String DETAILS = "details";
    public static final String TYPE = "type";
    private String category;
    private String details;
    private String type;

    public RestApplinkError(@Nonnull ApplinkError errorDetails) {
        Objects.requireNonNull(errorDetails, "errorDetails");
        this.category = errorDetails.getType().getCategory().name();
        this.type = errorDetails.getType().name();
        this.details = errorDetails.getDetails();
    }

    public static final class Visitor
    implements ApplinkErrorVisitor<RestApplinkError> {
        private final URI authorisationCallback;

        public Visitor() {
            this(null);
        }

        public Visitor(@Nullable URI authorisationCallback) {
            this.authorisationCallback = authorisationCallback;
        }

        @Override
        @Nullable
        public RestApplinkError visit(@Nonnull ApplinkError error) {
            return new RestApplinkError(error);
        }

        @Override
        @Nullable
        public RestApplinkError visit(@Nonnull AuthorisationUriAwareApplinkError error) {
            return new RestAuthorisationUriAwareApplinkError(error, this.authorisationCallback);
        }

        @Override
        @Nullable
        public RestApplinkError visit(@Nonnull ResponseApplinkError responseError) {
            return new RestApplinkError(responseError);
        }
    }
}

