/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.health;

public enum LifecyclePhase {
    SETUP,
    BOOTSTRAP_END,
    PLUGIN_FRAMEWORK_STARTED{

        @Override
        public boolean isLast() {
            return true;
        }
    };


    public boolean isLast() {
        return false;
    }
}

