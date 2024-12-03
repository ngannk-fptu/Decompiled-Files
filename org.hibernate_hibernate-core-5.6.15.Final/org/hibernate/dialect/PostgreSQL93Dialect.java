/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.util.List;
import org.hibernate.dialect.PostgreSQL92Dialect;

public class PostgreSQL93Dialect
extends PostgreSQL92Dialect {
    @Override
    public void augmentRecognizedTableTypes(List<String> tableTypesList) {
        super.augmentRecognizedTableTypes(tableTypesList);
        tableTypesList.add("MATERIALIZED VIEW");
    }
}

