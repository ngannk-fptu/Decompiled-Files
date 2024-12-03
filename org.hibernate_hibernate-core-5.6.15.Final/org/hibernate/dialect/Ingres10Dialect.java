/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.util.Properties;
import org.hibernate.dialect.Ingres9Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.Ingres10IdentityColumnSupport;

public class Ingres10Dialect
extends Ingres9Dialect {
    public Ingres10Dialect() {
        this.registerBooleanSupport();
        this.registerDefaultProperties();
    }

    protected void registerBooleanSupport() {
        this.registerColumnType(-7, "boolean");
        this.registerColumnType(16, "boolean");
    }

    private void registerDefaultProperties() {
        Properties properties = this.getDefaultProperties();
        String querySubst = properties.getProperty("hibernate.query.substitutions");
        if (querySubst != null) {
            String newQuerySubst = querySubst.replace("true=1,false=0", "");
            properties.setProperty("hibernate.query.substitutions", newQuerySubst);
        }
    }

    @Override
    public String toBooleanValueString(boolean bool) {
        return bool ? "true" : "false";
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new Ingres10IdentityColumnSupport();
    }
}

