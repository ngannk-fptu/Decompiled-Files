/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.applinks.internal.rest.model.status;

import com.atlassian.applinks.internal.common.rest.model.status.RestOAuthConfig;
import com.atlassian.applinks.internal.rest.model.IllegalRestRepresentationStateException;
import com.atlassian.applinks.internal.rest.model.RestRepresentation;
import com.atlassian.applinks.internal.status.oauth.ApplinkOAuthStatus;
import javax.annotation.Nonnull;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class RestApplinkOAuthStatus
implements RestRepresentation<ApplinkOAuthStatus> {
    public static final String INCOMING = "incoming";
    public static final String OUTGOING = "outgoing";
    public RestOAuthConfig incoming;
    public RestOAuthConfig outgoing;

    public RestApplinkOAuthStatus() {
    }

    public RestApplinkOAuthStatus(@Nonnull ApplinkOAuthStatus status) {
        this.incoming = new RestOAuthConfig(status.getIncoming());
        this.outgoing = new RestOAuthConfig(status.getOutgoing());
    }

    public boolean equals(Object o) {
        return this == o || o instanceof RestApplinkOAuthStatus && this.asDomain().equals(((RestApplinkOAuthStatus)o).asDomain());
    }

    public int hashCode() {
        return this.asDomain().hashCode();
    }

    @Override
    @Nonnull
    public ApplinkOAuthStatus asDomain() {
        if (this.incoming == null) {
            throw new IllegalRestRepresentationStateException(INCOMING);
        }
        if (this.outgoing == null) {
            throw new IllegalRestRepresentationStateException(OUTGOING);
        }
        return new ApplinkOAuthStatus(this.incoming.asDomain(), this.outgoing.asDomain());
    }
}

