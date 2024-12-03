/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.spi;

import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.dialect.Dialect;

public interface QualifiedObjectNameFormatter {
    public String format(QualifiedTableName var1, Dialect var2);

    public String format(QualifiedSequenceName var1, Dialect var2);

    public String format(QualifiedName var1, Dialect var2);
}

