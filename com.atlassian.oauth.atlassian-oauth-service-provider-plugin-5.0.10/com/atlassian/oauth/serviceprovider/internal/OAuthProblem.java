/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.sal.api.message.Message;
import java.security.Principal;
import java.util.Objects;

public class OAuthProblem
implements Message {
    private final Problem problem;
    private final String[] arguments;

    OAuthProblem(Problem problem, String ... arguments) {
        Objects.requireNonNull(problem, "problem");
        Objects.requireNonNull(arguments, "arguments");
        for (int i = 0; i < arguments.length; ++i) {
            Objects.requireNonNull(String.valueOf(i), arguments[i]);
        }
        this.problem = problem;
        this.arguments = arguments;
    }

    public OAuthProblem(Problem problem) {
        this(problem, new String[0]);
    }

    OAuthProblem(Problem problem, String param) {
        this(problem, new String[]{param});
    }

    OAuthProblem(Problem problem, String one, String two) {
        this(problem, new String[]{one, two});
    }

    OAuthProblem(Problem problem, String one, String two, String three) {
        this(problem, new String[]{one, two, three});
    }

    public Problem getProblem() {
        return this.problem;
    }

    public String getKey() {
        return "com.atlassian.oauth.serviceprovider.oauth.problem." + this.problem.name().toLowerCase();
    }

    public String[] getArguments() {
        return (String[])this.arguments.clone();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.problem.toString().toLowerCase());
        builder.append(": ");
        for (String argument : this.arguments) {
            builder.append((Object)argument);
            builder.append(", ");
        }
        return builder.toString();
    }

    public static enum Problem {
        UNREADABLE_TOKEN,
        SYSTEM,
        VERSION_REJECTED,
        PARAMETER_ABSENT,
        PARAMETER_REJECTED,
        TIMESTAMP_REFUSED,
        NONCE_USED,
        SIGNATURE_METHOD_REJECTED,
        SIGNATURE_INVALID,
        CONSUMER_KEY_UNKNOWN,
        CONSUMER_KEY_REJECTED,
        CONSUMER_KEY_REFUSED,
        TOKEN_USED,
        TOKEN_EXPIRED,
        TOKEN_REVOKED,
        TOKEN_REJECTED,
        ADDITIONAL_AUTHORIZATION_REQUIRED,
        PERMISSION_UNKNOWN,
        PERMISSION_DENIED,
        USER_REFUSED;

    }

    public static class TokenExpired
    extends OAuthProblem {
        public TokenExpired(String token) {
            super(Problem.TOKEN_EXPIRED, token);
        }
    }

    public static class InvalidToken
    extends OAuthProblem {
        public InvalidToken(String token) {
            super(Problem.TOKEN_REJECTED, token);
        }
    }

    public static class PermissionDenied
    extends OAuthProblem {
        public PermissionDenied(Principal user) {
            super(Problem.PERMISSION_DENIED, user.getName());
        }

        public PermissionDenied(String username) {
            super(Problem.PERMISSION_DENIED, username);
        }

        public PermissionDenied() {
            super(Problem.PERMISSION_DENIED);
        }
    }

    public static class UnreadableToken
    extends OAuthProblem {
        public UnreadableToken(Throwable cause) {
            super(Problem.UNREADABLE_TOKEN, cause.toString());
        }
    }

    public static class System
    extends OAuthProblem {
        public System(Throwable cause) {
            super(Problem.SYSTEM, cause.toString());
        }
    }
}

