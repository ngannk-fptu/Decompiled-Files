/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.Response;

public final class Statuses {
    public static Response.StatusType from(int code) {
        ClientResponse.Status result = ClientResponse.Status.fromStatusCode(code);
        return result != null ? result : new StatusImpl(code, "");
    }

    public static Response.StatusType from(int code, String reason) {
        return new StatusImpl(code, reason);
    }

    private Statuses() {
    }

    private static final class StatusImpl
    implements Response.StatusType {
        private int code;
        private String reason;
        private Response.Status.Family family;

        private StatusImpl(int code, String reason) {
            this.code = code;
            this.reason = reason;
            this.family = ClientResponse.Status.getFamilyByStatusCode(code);
        }

        @Override
        public int getStatusCode() {
            return this.code;
        }

        @Override
        public String getReasonPhrase() {
            return this.reason;
        }

        public String toString() {
            return this.reason;
        }

        @Override
        public Response.Status.Family getFamily() {
            return this.family;
        }
    }
}

