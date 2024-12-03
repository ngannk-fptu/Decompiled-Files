/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.security.auth.trustedapps.filter;

import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
public interface Authenticator {
    public Result authenticate(HttpServletRequest var1, HttpServletResponse var2);

    public static class Result {
        private final Status status;
        private final TransportErrorMessage message;
        private final Principal user;

        Result(Status status) {
            this(status, null, null);
        }

        Result(Status status, TransportErrorMessage message) {
            this(status, message, null);
            Null.not("message", message);
        }

        Result(Status status, Principal principal) {
            this(status, null, principal);
            Null.not("principal", principal);
        }

        Result(Status status, TransportErrorMessage message, Principal user) {
            if (status == null) {
                throw new IllegalArgumentException("status");
            }
            this.status = status;
            this.message = message;
            this.user = user;
        }

        public Status getStatus() {
            return this.status;
        }

        public String getMessage() {
            return this.message.toString();
        }

        public Principal getUser() {
            return this.user;
        }

        public static final class Success
        extends Result {
            private final String signedRequestUrl;

            public Success(Principal principal) {
                super(Status.SUCCESS, principal);
                this.signedRequestUrl = null;
            }

            public Success(Principal principal, String signedRequestUrl) {
                super(Status.SUCCESS, principal);
                this.signedRequestUrl = signedRequestUrl;
            }

            public String getSignedUrl() {
                return this.signedRequestUrl;
            }
        }

        public static final class Failure
        extends Result {
            Failure(TransportErrorMessage message) {
                super(Status.FAILED, message);
            }
        }

        public static final class Error
        extends Result {
            Error(TransportErrorMessage message) {
                super(Status.ERROR, message);
            }
        }

        public static final class NoAttempt
        extends Result {
            NoAttempt() {
                super(Status.NO_ATTEMPT);
            }
        }

        static final class Status {
            static final Status SUCCESS = new Status(0, "success");
            static final Status FAILED = new Status(1, "failed");
            static final Status ERROR = new Status(2, "error");
            static final Status NO_ATTEMPT = new Status(3, "no attempt");
            private final int ordinal;
            private final String name;

            private Status(int ordinal, String name) {
                this.ordinal = ordinal;
                this.name = name;
            }

            int getOrdinal() {
                return this.ordinal;
            }

            public String toString() {
                return this.name;
            }

            static final class Constants {
                static final int SUCCESS = 0;
                static final int FAILED = 1;
                static final int ERROR = 2;
                static final int NO_ATTEMPT = 3;

                Constants() {
                }
            }
        }
    }
}

