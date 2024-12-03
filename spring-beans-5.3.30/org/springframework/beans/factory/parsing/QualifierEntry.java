/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.util.StringUtils;

public class QualifierEntry
implements ParseState.Entry {
    private final String typeName;

    public QualifierEntry(String typeName) {
        if (!StringUtils.hasText((String)typeName)) {
            throw new IllegalArgumentException("Invalid qualifier type '" + typeName + "'");
        }
        this.typeName = typeName;
    }

    public String toString() {
        return "Qualifier '" + this.typeName + "'";
    }
}

