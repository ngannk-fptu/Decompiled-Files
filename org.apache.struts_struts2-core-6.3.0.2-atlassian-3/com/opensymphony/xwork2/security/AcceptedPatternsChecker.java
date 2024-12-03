/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.security;

import java.util.Set;
import java.util.regex.Pattern;

public interface AcceptedPatternsChecker {
    public IsAccepted isAccepted(String var1);

    public void setAcceptedPatterns(String var1);

    public void setAcceptedPatterns(String[] var1);

    public void setAcceptedPatterns(Set<String> var1);

    public Set<Pattern> getAcceptedPatterns();

    public static final class IsAccepted {
        private final boolean accepted;
        private final String acceptedPattern;

        public static IsAccepted yes(String acceptedPattern) {
            return new IsAccepted(true, acceptedPattern);
        }

        public static IsAccepted no(String acceptedPatterns) {
            return new IsAccepted(false, acceptedPatterns);
        }

        private IsAccepted(boolean accepted, String acceptedPattern) {
            this.accepted = accepted;
            this.acceptedPattern = acceptedPattern;
        }

        public boolean isAccepted() {
            return this.accepted;
        }

        public String getAcceptedPattern() {
            return this.acceptedPattern;
        }

        public String toString() {
            return "IsAccepted {accepted=" + this.accepted + ", acceptedPattern=" + this.acceptedPattern + " }";
        }
    }
}

