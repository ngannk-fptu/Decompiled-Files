/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.Column;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AbstractQOMNode;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class ColumnImpl
extends AbstractQOMNode
implements Column {
    public static final ColumnImpl[] EMPTY_ARRAY = new ColumnImpl[0];
    private final Name selectorName;
    private final Name propertyName;
    private final String columnName;

    ColumnImpl(NamePathResolver resolver, Name selectorName, Name propertyName, String columnName) {
        super(resolver);
        this.selectorName = selectorName;
        this.propertyName = propertyName;
        this.columnName = columnName;
    }

    public Name getSelectorQName() {
        return this.selectorName;
    }

    public Name getPropertyQName() {
        return this.propertyName;
    }

    @Override
    public String getSelectorName() {
        return this.getJCRName(this.selectorName);
    }

    @Override
    public String getPropertyName() {
        return this.getJCRName(this.propertyName);
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        if (this.propertyName != null) {
            return this.getSelectorName() + "." + this.getPropertyName() + " AS " + this.getColumnName();
        }
        return this.getSelectorName() + ".*";
    }
}

