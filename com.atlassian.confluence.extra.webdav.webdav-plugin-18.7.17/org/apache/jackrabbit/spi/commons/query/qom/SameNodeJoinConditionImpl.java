/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.SameNodeJoinCondition;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.JoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class SameNodeJoinConditionImpl
extends JoinConditionImpl
implements SameNodeJoinCondition {
    private final Name selector1Name;
    private final Name selector2Name;
    private final Path selector2Path;

    SameNodeJoinConditionImpl(NamePathResolver resolver, Name selector1Name, Name selector2Name, Path selector2Path) {
        super(resolver);
        this.selector1Name = selector1Name;
        this.selector2Name = selector2Name;
        this.selector2Path = selector2Path;
    }

    @Override
    public String getSelector1Name() {
        return this.getJCRName(this.selector1Name);
    }

    @Override
    public String getSelector2Name() {
        return this.getJCRName(this.selector2Name);
    }

    @Override
    public String getSelector2Path() {
        return this.getJCRPath(this.selector2Path);
    }

    public Name getSelector1QName() {
        return this.selector1Name;
    }

    public Name getSelector2QName() {
        return this.selector2Name;
    }

    public Path getSelector2QPath() {
        return this.selector2Path;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ISSAMENODE(");
        builder.append(this.getSelector1Name());
        builder.append(", ");
        builder.append(this.getSelector2Name());
        if (this.selector2Path != null) {
            builder.append(", ");
            builder.append(this.quote(this.selector2Path));
        }
        builder.append(")");
        return builder.toString();
    }
}

