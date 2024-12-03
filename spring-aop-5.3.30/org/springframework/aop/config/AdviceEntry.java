/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.parsing.ParseState$Entry
 */
package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

public class AdviceEntry
implements ParseState.Entry {
    private final String kind;

    public AdviceEntry(String kind) {
        this.kind = kind;
    }

    public String toString() {
        return "Advice (" + this.kind + ")";
    }
}

