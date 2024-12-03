/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.util.StringUtils;

public class PropertyEntry
implements ParseState.Entry {
    private final String name;

    public PropertyEntry(String name) {
        if (!StringUtils.hasText((String)name)) {
            throw new IllegalArgumentException("Invalid property name '" + name + "'");
        }
        this.name = name;
    }

    public String toString() {
        return "Property '" + this.name + "'";
    }
}

