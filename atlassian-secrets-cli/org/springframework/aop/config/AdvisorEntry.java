/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

public class AdvisorEntry
implements ParseState.Entry {
    private final String name;

    public AdvisorEntry(String name) {
        this.name = name;
    }

    public String toString() {
        return "Advisor '" + this.name + "'";
    }
}

