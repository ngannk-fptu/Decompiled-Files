/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.util.List;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.PostgreSQL10IdentityColumnSupport;

public class PostgreSQL10Dialect
extends PostgreSQL95Dialect {
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new PostgreSQL10IdentityColumnSupport();
    }

    @Override
    public void augmentRecognizedTableTypes(List<String> tableTypesList) {
        super.augmentRecognizedTableTypes(tableTypesList);
        tableTypesList.add("PARTITIONED TABLE");
    }
}

