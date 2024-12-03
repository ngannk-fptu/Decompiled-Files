/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.parsing.ParseState$Entry
 */
package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

public class PointcutEntry
implements ParseState.Entry {
    private final String name;

    public PointcutEntry(String name) {
        this.name = name;
    }

    public String toString() {
        return "Pointcut '" + this.name + "'";
    }
}

