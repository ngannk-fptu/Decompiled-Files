/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.sql.ordering.antlr;

import org.hibernate.sql.ordering.antlr.OrderByAliasResolver;

public interface OrderByTranslation {
    public String injectAliases(OrderByAliasResolver var1);
}

