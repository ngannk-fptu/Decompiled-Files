/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.util.StringUtils;

public class QualifierEntry
implements ParseState.Entry {
    private final String typeName;

    public QualifierEntry(String typeName) {
        if (!StringUtils.hasText(typeName)) {
            throw new IllegalArgumentException("Invalid qualifier type '" + typeName + "'");
        }
        this.typeName = typeName;
    }

    public String toString() {
        return "Qualifier '" + this.typeName + "'";
    }
}

