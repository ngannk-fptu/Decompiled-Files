/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.FullTextSearch;
import javax.jcr.query.qom.StaticOperand;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class FullTextSearchImpl
extends ConstraintImpl
implements FullTextSearch {
    private final Name selectorName;
    private final Name propertyName;
    private final StaticOperand fullTextSearchExpression;

    FullTextSearchImpl(NamePathResolver resolver, Name selectorName, Name propertyName, StaticOperand fullTextSearchExpression) {
        super(resolver);
        this.selectorName = selectorName;
        this.propertyName = propertyName;
        this.fullTextSearchExpression = fullTextSearchExpression;
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
    public StaticOperand getFullTextSearchExpression() {
        return this.fullTextSearchExpression;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CONTAINS(");
        builder.append(this.getSelectorName());
        if (this.propertyName != null) {
            builder.append(".");
            builder.append(this.quote(this.propertyName));
            builder.append(", ");
        } else {
            builder.append(".*, ");
        }
        builder.append(this.getFullTextSearchExpression());
        builder.append(")");
        return builder.toString();
    }
}

