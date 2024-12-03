/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.mapping.Table;

public interface Selectable {
    public String getAlias(Dialect var1);

    public String getAlias(Dialect var1, Table var2);

    public boolean isFormula();

    public String getTemplate(Dialect var1, SQLFunctionRegistry var2);

    public String getText(Dialect var1);

    public String getText();
}

