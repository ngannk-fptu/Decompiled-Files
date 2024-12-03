/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.extender.internal.dependencies.startup;

public enum ContextState {
    INITIALIZED,
    RESOLVING_DEPENDENCIES,
    DEPENDENCIES_RESOLVED,
    STARTED,
    INTERRUPTED,
    STOPPED;


    public boolean isDown() {
        return this.equals((Object)INTERRUPTED) || this.equals((Object)STOPPED);
    }

    public boolean isUnresolved() {
        return this.equals((Object)RESOLVING_DEPENDENCIES) || this.equals((Object)INITIALIZED);
    }

    public boolean isResolved() {
        return !this.isUnresolved();
    }
}

