/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.modification;

import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;

public class ModificationResultEntry
extends TinySpace {
    private String message = "";

    public ModificationResultEntry(String key, String name) {
        super(key, name);
    }

    public ModificationResultEntry(String key, String name, String message) {
        super(key, name);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

