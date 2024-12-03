/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.EquiJoinCondition;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.JoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class EquiJoinConditionImpl
extends JoinConditionImpl
implements EquiJoinCondition {
    private final Name selector1Name;
    private final Name property1Name;
    private final Name selector2Name;
    private final Name property2Name;

    EquiJoinConditionImpl(NamePathResolver resolver, Name selector1Name, Name property1Name, Name selector2Name, Name property2Name) {
        super(resolver);
        this.selector1Name = selector1Name;
        this.property1Name = property1Name;
        this.selector2Name = selector2Name;
        this.property2Name = property2Name;
    }

    @Override
    public String getSelector1Name() {
        return this.getJCRName(this.selector1Name);
    }

    @Override
    public String getProperty1Name() {
        return this.getJCRName(this.property1Name);
    }

    @Override
    public String getSelector2Name() {
        return this.getJCRName(this.selector2Name);
    }

    @Override
    public String getProperty2Name() {
        return this.getJCRName(this.property2Name);
    }

    public Name getSelector1QName() {
        return this.selector1Name;
    }

    public Name getSelector2QName() {
        return this.selector2Name;
    }

    public Name getProperty1QName() {
        return this.property1Name;
    }

    public Name getProperty2QName() {
        return this.property2Name;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        return this.getSelector1Name() + "." + this.quote(this.getProperty1QName()) + " = " + this.getSelector2Name() + "." + this.quote(this.getProperty2QName());
    }
}

