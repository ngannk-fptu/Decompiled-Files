/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import java.util.Locale;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;

public class LiteralNode
extends AbstractSelectExpression
implements HqlSqlTokenTypes,
ExpectedTypeAwareNode {
    private Type expectedType;

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }

    @Override
    public Type getDataType() {
        if (this.getExpectedType() != null) {
            return this.getExpectedType();
        }
        switch (this.getType()) {
            case 133: {
                return StandardBasicTypes.INTEGER;
            }
            case 102: {
                return StandardBasicTypes.FLOAT;
            }
            case 103: {
                return StandardBasicTypes.LONG;
            }
            case 101: {
                return StandardBasicTypes.DOUBLE;
            }
            case 104: {
                return StandardBasicTypes.BIG_INTEGER;
            }
            case 105: {
                return StandardBasicTypes.BIG_DECIMAL;
            }
            case 130: {
                return StandardBasicTypes.STRING;
            }
            case 20: 
            case 50: {
                return StandardBasicTypes.BOOLEAN;
            }
        }
        return null;
    }

    public Object getLiteralValue() {
        Type inherentType;
        String text = this.getText();
        if (this.getType() == 130) {
            text = text.substring(1, text.length() - 1);
        }
        if ((inherentType = this.getDataType()) == null) {
            return text;
        }
        return ((SingleColumnType)inherentType).fromStringValue(text);
    }

    @Override
    public void setExpectedType(Type expectedType) {
        if (this.expectedType != null) {
            return;
        }
        if (AttributeConverterTypeAdapter.class.isInstance(expectedType)) {
            AttributeConverterTypeAdapter adapterType = (AttributeConverterTypeAdapter)expectedType;
            this.setText(this.determineConvertedValue(adapterType, this.getLiteralValue()));
            this.expectedType = expectedType;
        }
    }

    protected String determineConvertedValue(AttributeConverterTypeAdapter converterTypeAdapter, Object literalValue) {
        if (this.getDataType().getReturnedClass().equals(converterTypeAdapter.getModelType())) {
            JpaAttributeConverter converter = converterTypeAdapter.getAttributeConverter();
            Object converted = converter.toRelationalValue(this.getLiteralValue());
            if (this.isCharacterData(converterTypeAdapter.sqlType())) {
                return "'" + converted.toString() + "'";
            }
            return converted.toString();
        }
        if (this.getDataType().getReturnedClass().equals(converterTypeAdapter.getJdbcType())) {
            if (this.isCharacterData(converterTypeAdapter.sqlType())) {
                return "'" + literalValue.toString() + "'";
            }
            return literalValue.toString();
        }
        throw new QueryException(String.format(Locale.ROOT, "AttributeConverter domain-model attribute type [%s] and JDBC type [%s] did not match query literal type [%s]", converterTypeAdapter.getModelType().getName(), converterTypeAdapter.getJdbcType().getName(), this.getDataType().getReturnedClass().getName()));
    }

    private boolean isCharacterData(int typeCode) {
        return 12 == typeCode || 1 == typeCode || -9 == typeCode || -15 == typeCode;
    }

    @Override
    public Type getExpectedType() {
        return this.expectedType;
    }
}

