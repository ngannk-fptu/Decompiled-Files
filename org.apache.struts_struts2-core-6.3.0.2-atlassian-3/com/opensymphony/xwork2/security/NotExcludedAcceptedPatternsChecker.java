/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;

public interface NotExcludedAcceptedPatternsChecker
extends ExcludedPatternsChecker,
AcceptedPatternsChecker {
    public IsAllowed isAllowed(String var1);

    public static final class IsAllowed {
        private final boolean allowed;
        private final String allowedPattern;

        public static IsAllowed yes(String allowedPattern) {
            return new IsAllowed(true, allowedPattern);
        }

        public static IsAllowed no(String allowedPattern) {
            return new IsAllowed(false, allowedPattern);
        }

        private IsAllowed(boolean allowed, String allowedPattern) {
            this.allowed = allowed;
            this.allowedPattern = allowedPattern;
        }

        public boolean isAllowed() {
            return this.allowed;
        }

        public String getAllowedPattern() {
            return this.allowedPattern;
        }

        public String toString() {
            return "IsAllowed { allowed=" + this.allowed + ", allowedPattern=" + this.allowedPattern + " }";
        }
    }
}

