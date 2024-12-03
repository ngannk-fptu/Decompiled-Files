/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.sal.api.auth;

import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Authenticator {
    public Result authenticate(HttpServletRequest var1, HttpServletResponse var2);

    public static class Result {
        private final Status status;
        private final Message message;
        private final Principal principal;
        private static final Message NO_ATTEMPT_MESSAGE = new Message(){

            @Override
            public Serializable[] getArguments() {
                return null;
            }

            @Override
            public String getKey() {
                return "No authentication attempted";
            }
        };
        private static final Message SUCCESS_MESSAGE = new Message(){

            @Override
            public Serializable[] getArguments() {
                return null;
            }

            @Override
            public String getKey() {
                return "Successful authentication";
            }
        };

        Result(Status status, Message message) {
            this(status, message, null);
        }

        Result(Status status, Message message, Principal principal) {
            if (status == null) {
                throw new NullPointerException("status");
            }
            if (message == null) {
                throw new NullPointerException("message");
            }
            this.status = status;
            this.message = message;
            this.principal = principal;
        }

        public Status getStatus() {
            return this.status;
        }

        public String getMessage() {
            return this.message.toString();
        }

        public Principal getPrincipal() {
            return this.principal;
        }

        public static final class Success
        extends Result {
            public Success(Principal principal) {
                this(SUCCESS_MESSAGE, principal);
            }

            public Success(Message message, Principal principal) {
                super(Status.SUCCESS, message, principal);
            }
        }

        public static final class Failure
        extends Result {
            public Failure(Message message) {
                super(Status.FAILED, message);
            }
        }

        public static final class Error
        extends Result {
            public Error(Message message) {
                super(Status.ERROR, message);
            }
        }

        public static final class NoAttempt
        extends Result {
            public NoAttempt() {
                super(Status.NO_ATTEMPT, NO_ATTEMPT_MESSAGE);
            }
        }

        public static enum Status {
            SUCCESS("success"),
            FAILED("failed"),
            ERROR("error"),
            NO_ATTEMPT("no attempt");

            private final String name;

            private Status(String name) {
                this.name = name;
            }

            public String toString() {
                return this.name;
            }
        }
    }
}

