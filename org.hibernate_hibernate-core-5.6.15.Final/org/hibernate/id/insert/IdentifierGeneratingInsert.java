/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.insert;

import org.hibernate.dialect.Dialect;
import org.hibernate.sql.Insert;

public class IdentifierGeneratingInsert
extends Insert {
    public IdentifierGeneratingInsert(Dialect dialect) {
        super(dialect);
    }
}

