/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal;

import org.hibernate.internal.FilterAliasGenerator;

public class StaticFilterAliasGenerator
implements FilterAliasGenerator {
    private final String alias;

    public StaticFilterAliasGenerator(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias(String table) {
        return this.alias;
    }
}

