/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import java.util.ArrayList;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class InExpression
implements Criterion {
    private final String propertyName;
    private final Object[] values;

    protected InExpression(String propertyName, Object[] values) {
        this.propertyName = propertyName;
        this.values = values;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) {
        CharSequence[] columns = criteriaQuery.findColumns(this.propertyName, criteria);
        Dialect dialect = criteriaQuery.getFactory().getDialect();
        if (dialect.supportsRowValueConstructorSyntaxInInList() || columns.length <= 1) {
            String singleValueParam = StringHelper.repeat("?, ", columns.length - 1) + "?";
            if (columns.length > 1) {
                singleValueParam = '(' + singleValueParam + ')';
            }
            String params = this.values.length > 0 ? StringHelper.repeat(singleValueParam + ", ", this.values.length - 1) + singleValueParam : (dialect.supportsEmptyInList() ? "" : "null");
            String cols = String.join((CharSequence)", ", columns);
            if (columns.length > 1) {
                cols = '(' + cols + ')';
            }
            return cols + " in (" + params + ')';
        }
        String cols = " ( " + String.join((CharSequence)" = ? and ", columns) + "= ? ) ";
        cols = this.values.length > 0 ? StringHelper.repeat(cols + "or ", this.values.length - 1) + cols : "";
        cols = " ( " + cols + " ) ";
        return cols;
    }

    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
        ArrayList<TypedValue> list = new ArrayList<TypedValue>();
        Type type = criteriaQuery.getTypeUsingProjection(criteria, this.propertyName);
        if (type.isComponentType()) {
            CompositeType compositeType = (CompositeType)type;
            Type[] subTypes = compositeType.getSubtypes();
            for (Object value : this.values) {
                for (int i = 0; i < subTypes.length; ++i) {
                    Object subValue = value == null ? null : compositeType.getPropertyValues(value, EntityMode.POJO)[i];
                    list.add(new TypedValue(subTypes[i], subValue));
                }
            }
        } else {
            for (Object value : this.values) {
                list.add(criteriaQuery.getTypedValue(criteria, this.propertyName, value));
            }
        }
        return list.toArray(new TypedValue[list.size()]);
    }

    public String toString() {
        return this.propertyName + " in (" + StringHelper.toString(this.values) + ')';
    }
}

