/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import java.util.Locale;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.SessionFactoryAwareNode;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.type.LiteralType;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;

public class JavaConstantNode
extends Node
implements ExpectedTypeAwareNode,
SessionFactoryAwareNode {
    private SessionFactoryImplementor factory;
    private String constantExpression;
    private Object constantValue;
    private Type heuristicType;
    private Type expectedType;

    public void setText(String s) {
        if (StringHelper.isNotEmpty(s)) {
            this.constantExpression = s;
            this.constantValue = ReflectHelper.getConstantValue(s, this.factory);
            this.heuristicType = this.factory.getTypeResolver().heuristicType(this.constantValue.getClass().getName());
            super.setText(s);
        }
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
    public void setSessionFactory(SessionFactoryImplementor factory) {
        this.factory = factory;
    }

    @Override
    public String getRenderText(SessionFactoryImplementor sessionFactory) {
        Type type = this.expectedType == null ? this.heuristicType : (Number.class.isAssignableFrom(this.heuristicType.getReturnedClass()) ? this.heuristicType : this.expectedType);
        try {
            if (LiteralType.class.isInstance(type)) {
                LiteralType literalType = (LiteralType)((Object)type);
                Dialect dialect = this.factory.getDialect();
                return literalType.objectToSQLString(this.constantValue, dialect);
            }
            if (AttributeConverterTypeAdapter.class.isInstance(type)) {
                AttributeConverterTypeAdapter converterType = (AttributeConverterTypeAdapter)type;
                if (!converterType.getModelType().isInstance(this.constantValue)) {
                    throw new QueryException(String.format(Locale.ENGLISH, "Recognized query constant expression [%s] was not resolved to type [%s] expected by defined AttributeConverter [%s]", this.constantExpression, this.constantValue.getClass().getName(), converterType.getModelType().getName()));
                }
                Object value = converterType.getAttributeConverter().toRelationalValue(this.constantValue);
                if (String.class.equals((Object)converterType.getJdbcType())) {
                    return "'" + value + "'";
                }
                return value.toString();
            }
            throw new QueryException(String.format(Locale.ENGLISH, "Unrecognized Hibernate Type for handling query constant (%s); expecting LiteralType implementation or AttributeConverter", this.constantExpression));
        }
        catch (QueryException e) {
            throw e;
        }
        catch (Exception t) {
            throw new QueryException("Could not format constant value to SQL literal: " + this.constantExpression, t);
        }
    }
}

