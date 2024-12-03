/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.api.querydsl.util;

@FunctionalInterface
public interface OnRollback {
    public static final OnRollback NOOP = new NoopOnRollback();

    public void execute();

    public static final class NoopOnRollback
    implements OnRollback {
        @Override
        public void execute() {
        }
    }
}

