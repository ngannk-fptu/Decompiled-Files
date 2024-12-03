/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import org.hibernate.loader.custom.Return;
import org.hibernate.type.Type;

public class ScalarReturn
implements Return {
    private final Type type;
    private final String columnAlias;

    public ScalarReturn(Type type, String columnAlias) {
        this.type = type;
        this.columnAlias = columnAlias;
    }

    public Type getType() {
        return this.type;
    }

    public String getColumnAlias() {
        return this.columnAlias;
    }
}

