/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

public class InitCommand {
    private final String[] initCommands;

    public InitCommand(String ... initCommands) {
        this.initCommands = initCommands;
    }

    public String[] getInitCommands() {
        return this.initCommands;
    }
}

