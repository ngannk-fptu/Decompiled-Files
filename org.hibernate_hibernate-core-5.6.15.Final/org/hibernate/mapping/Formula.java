/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.internal.AliasConstantsHelper;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Table;
import org.hibernate.sql.Template;

public class Formula
implements Selectable,
Serializable {
    private static final AtomicInteger formulaUniqueInteger = new AtomicInteger();
    private String formula;
    private final int uniqueInteger = formulaUniqueInteger.incrementAndGet();

    public Formula() {
    }

    public Formula(String formula) {
        this();
        this.formula = formula;
    }

    @Override
    public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {
        String template = Template.renderWhereStringTemplate(this.formula, dialect, functionRegistry);
        return StringHelper.safeInterning(StringHelper.replace(template, "{alias}", "$PlaceHolder$"));
    }

    @Override
    public String getText(Dialect dialect) {
        return this.getFormula();
    }

    @Override
    public String getText() {
        return this.getFormula();
    }

    @Override
    public String getAlias(Dialect dialect) {
        return "formula" + AliasConstantsHelper.get(this.uniqueInteger);
    }

    @Override
    public String getAlias(Dialect dialect, Table table) {
        return this.getAlias(dialect);
    }

    public String getFormula() {
        return this.formula;
    }

    public void setFormula(String string) {
        this.formula = string;
    }

    @Override
    public boolean isFormula() {
        return true;
    }

    public String toString() {
        return this.getClass().getName() + "( " + this.formula + " )";
    }
}

