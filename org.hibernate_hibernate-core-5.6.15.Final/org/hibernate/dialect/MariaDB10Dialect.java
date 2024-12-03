/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import org.hibernate.dialect.MariaDB53Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MariaDB10Dialect
extends MariaDB53Dialect {
    public MariaDB10Dialect() {
        this.registerFunction("regexp_replace", new StandardSQLFunction("regexp_replace", StandardBasicTypes.STRING));
        this.registerFunction("regexp_instr", new StandardSQLFunction("regexp_instr", StandardBasicTypes.INTEGER));
        this.registerFunction("regexp_substr", new StandardSQLFunction("regexp_substr", StandardBasicTypes.STRING));
        this.registerFunction("weight_string", new StandardSQLFunction("weight_string", StandardBasicTypes.STRING));
        this.registerFunction("to_base64", new StandardSQLFunction("to_base64", StandardBasicTypes.STRING));
        this.registerFunction("from_base64", new StandardSQLFunction("from_base64", StandardBasicTypes.STRING));
    }

    @Override
    public boolean supportsIfExistsBeforeConstraintName() {
        return true;
    }
}

