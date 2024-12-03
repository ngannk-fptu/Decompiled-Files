/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public class Ingres9IdentityColumnSupport
extends IdentityColumnSupportImpl {
    @Override
    public String getIdentitySelectString(String table, String column, int type) {
        return "select last_identity()";
    }
}

