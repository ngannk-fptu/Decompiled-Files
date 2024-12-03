/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.identity.H2IdentityColumnSupport;

public class H2FinalTableIdentityColumnSupport
extends H2IdentityColumnSupport {
    public static final H2FinalTableIdentityColumnSupport INSTANCE = new H2FinalTableIdentityColumnSupport();

    private H2FinalTableIdentityColumnSupport() {
    }

    @Override
    public boolean supportsInsertSelectIdentity() {
        return true;
    }

    @Override
    public String appendIdentitySelectToInsert(String identityColumnName, String insertString) {
        return "select " + identityColumnName + " from final table ( " + insertString + " )";
    }
}

