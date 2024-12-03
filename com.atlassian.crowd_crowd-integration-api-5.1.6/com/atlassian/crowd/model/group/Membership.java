/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.group;

import java.util.Set;

public interface Membership {
    public String getGroupName();

    public Set<String> getUserNames();

    public Set<String> getChildGroupNames();

    public static class MembershipIterationException
    extends RuntimeException {
        public MembershipIterationException(Throwable cause) {
            super(cause);
        }
    }
}

