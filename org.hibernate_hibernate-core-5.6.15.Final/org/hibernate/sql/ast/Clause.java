/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql.ast;

import org.hibernate.Incubating;

@Incubating
public enum Clause {
    INSERT,
    UPDATE,
    DELETE,
    SELECT,
    FROM,
    WHERE,
    GROUP,
    HAVING,
    ORDER,
    LIMIT,
    CALL,
    IRRELEVANT;

}

