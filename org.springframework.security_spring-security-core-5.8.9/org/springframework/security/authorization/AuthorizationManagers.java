/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authorization;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;

public final class AuthorizationManagers {
    @SafeVarargs
    public static <T> AuthorizationManager<T> anyOf(AuthorizationManager<T> ... managers) {
        return (authentication, object) -> {
            ArrayList<AuthorizationDecision> decisions = new ArrayList<AuthorizationDecision>();
            for (AuthorizationManager manager : managers) {
                AuthorizationDecision decision = manager.check(authentication, object);
                if (decision == null) continue;
                if (decision.isGranted()) {
                    return decision;
                }
                decisions.add(decision);
            }
            if (decisions.isEmpty()) {
                return new AuthorizationDecision(false);
            }
            return new CompositeAuthorizationDecision(false, decisions);
        };
    }

    @SafeVarargs
    public static <T> AuthorizationManager<T> allOf(AuthorizationManager<T> ... managers) {
        return (authentication, object) -> {
            ArrayList<AuthorizationDecision> decisions = new ArrayList<AuthorizationDecision>();
            for (AuthorizationManager manager : managers) {
                AuthorizationDecision decision = manager.check(authentication, object);
                if (decision == null) continue;
                if (!decision.isGranted()) {
                    return decision;
                }
                decisions.add(decision);
            }
            if (decisions.isEmpty()) {
                return new AuthorizationDecision(true);
            }
            return new CompositeAuthorizationDecision(true, decisions);
        };
    }

    private AuthorizationManagers() {
    }

    private static final class CompositeAuthorizationDecision
    extends AuthorizationDecision {
        private final List<AuthorizationDecision> decisions;

        private CompositeAuthorizationDecision(boolean granted, List<AuthorizationDecision> decisions) {
            super(granted);
            this.decisions = decisions;
        }

        @Override
        public String toString() {
            return "CompositeAuthorizationDecision [decisions=" + this.decisions + ']';
        }
    }
}

