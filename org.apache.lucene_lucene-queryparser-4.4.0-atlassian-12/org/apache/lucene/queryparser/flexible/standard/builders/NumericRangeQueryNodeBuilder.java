/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.FieldType$NumericType
 *  org.apache.lucene.search.NumericRangeQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericRangeQueryNode;
import org.apache.lucene.search.NumericRangeQuery;

public class NumericRangeQueryNodeBuilder
implements StandardQueryBuilder {
    public NumericRangeQuery<? extends Number> build(QueryNode queryNode) throws QueryNodeException {
        NumericRangeQueryNode numericRangeNode = (NumericRangeQueryNode)queryNode;
        NumericQueryNode lowerNumericNode = (NumericQueryNode)numericRangeNode.getLowerBound();
        NumericQueryNode upperNumericNode = (NumericQueryNode)numericRangeNode.getUpperBound();
        Number lowerNumber = lowerNumericNode.getValue();
        Number upperNumber = upperNumericNode.getValue();
        NumericConfig numericConfig = numericRangeNode.getNumericConfig();
        FieldType.NumericType numberType = numericConfig.getType();
        String field = StringUtils.toString(numericRangeNode.getField());
        boolean minInclusive = numericRangeNode.isLowerInclusive();
        boolean maxInclusive = numericRangeNode.isUpperInclusive();
        int precisionStep = numericConfig.getPrecisionStep();
        switch (numberType) {
            case LONG: {
                return NumericRangeQuery.newLongRange((String)field, (int)precisionStep, (Long)((Long)lowerNumber), (Long)((Long)upperNumber), (boolean)minInclusive, (boolean)maxInclusive);
            }
            case INT: {
                return NumericRangeQuery.newIntRange((String)field, (int)precisionStep, (Integer)((Integer)lowerNumber), (Integer)((Integer)upperNumber), (boolean)minInclusive, (boolean)maxInclusive);
            }
            case FLOAT: {
                return NumericRangeQuery.newFloatRange((String)field, (int)precisionStep, (Float)((Float)lowerNumber), (Float)((Float)upperNumber), (boolean)minInclusive, (boolean)maxInclusive);
            }
            case DOUBLE: {
                return NumericRangeQuery.newDoubleRange((String)field, (int)precisionStep, (Double)((Double)lowerNumber), (Double)((Double)upperNumber), (boolean)minInclusive, (boolean)maxInclusive);
            }
        }
        throw new QueryNodeException(new MessageImpl(QueryParserMessages.UNSUPPORTED_NUMERIC_DATA_TYPE, numberType));
    }
}

