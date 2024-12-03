/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.migration.agent.rest;

import java.util.Optional;
import javax.ws.rs.core.Response;

public enum ContainerTokenState {
    INVALID,
    EXPIRED,
    VALID;


    public Optional<Response> toResponseWhenNotValid() {
        switch (this) {
            case INVALID: {
                return Optional.ofNullable(Response.status((Response.Status)Response.Status.BAD_REQUEST).build());
            }
            case EXPIRED: {
                return Optional.ofNullable(Response.status((Response.Status)Response.Status.UNAUTHORIZED).header("Cloud-token-expired", (Object)"true").build());
            }
            case VALID: {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build());
    }
}

