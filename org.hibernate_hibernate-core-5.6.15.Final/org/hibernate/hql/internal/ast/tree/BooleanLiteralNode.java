/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.QueryException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.tree.LiteralNode;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;

public class BooleanLiteralNode
extends LiteralNode
implements ExpectedTypeAwareNode {
    private Type expectedType;

    @Override
    public Type getDataType() {
        return this.getExpectedType() == null ? StandardBasicTypes.BOOLEAN : this.getExpectedType();
    }

    public Boolean getValue() {
        return this.getType() == 50;
    }

    @Override
    public void setText(String s) {
        super.setText(s);
    }

    @Override
    public void setExpectedType(Type expectedType) {
        this.expectedType = expectedType;
    }

    @Override
    public Type getExpectedType() {
        return this.expectedType;
    }

    @Override
    public String getRenderText(SessionFactoryImplementor sessionFactory) {
        boolean literalValue = this.getValue();
        if (this.expectedType instanceof AttributeConverterTypeAdapter) {
            return this.determineConvertedValue((AttributeConverterTypeAdapter)this.expectedType, literalValue);
        }
        if (this.expectedType instanceof LiteralType) {
            try {
                return ((LiteralType)((Object)this.expectedType)).objectToSQLString(this.getValue(), sessionFactory.getDialect());
            }
            catch (Exception t) {
                throw new QueryException("Unable to render boolean literal value using expected LiteralType", t);
            }
        }
        return sessionFactory.getDialect().toBooleanValueString(literalValue);
    }
}

