/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.QueryNodeParseException;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;

public class NumericRangeQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        NumericConfig numericConfig;
        TermRangeQueryNode termRangeNode;
        FieldConfig fieldConfig;
        QueryConfigHandler config;
        if (node instanceof TermRangeQueryNode && (config = this.getQueryConfigHandler()) != null && (fieldConfig = config.getFieldConfig(StringUtils.toString((termRangeNode = (TermRangeQueryNode)node).getField()))) != null && (numericConfig = fieldConfig.get(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG)) != null) {
            FieldQueryNode lower = (FieldQueryNode)termRangeNode.getLowerBound();
            FieldQueryNode upper = (FieldQueryNode)termRangeNode.getUpperBound();
            String lowerText = lower.getTextAsString();
            String upperText = upper.getTextAsString();
            NumberFormat numberFormat = numericConfig.getNumberFormat();
            Number lowerNumber = null;
            Number upperNumber = null;
            if (lowerText.length() > 0) {
                try {
                    lowerNumber = numberFormat.parse(lowerText);
                }
                catch (ParseException e) {
                    throw new QueryNodeParseException(new MessageImpl(QueryParserMessages.COULD_NOT_PARSE_NUMBER, lower.getTextAsString(), numberFormat.getClass().getCanonicalName()), (Throwable)e);
                }
            }
            if (upperText.length() > 0) {
                try {
                    upperNumber = numberFormat.parse(upperText);
                }
                catch (ParseException e) {
                    throw new QueryNodeParseException(new MessageImpl(QueryParserMessages.COULD_NOT_PARSE_NUMBER, upper.getTextAsString(), numberFormat.getClass().getCanonicalName()), (Throwable)e);
                }
            }
            switch (numericConfig.getType()) {
                case LONG: {
                    if (upperNumber != null) {
                        upperNumber = upperNumber.longValue();
                    }
                    if (lowerNumber == null) break;
                    lowerNumber = lowerNumber.longValue();
                    break;
                }
                case INT: {
                    if (upperNumber != null) {
                        upperNumber = upperNumber.intValue();
                    }
                    if (lowerNumber == null) break;
                    lowerNumber = lowerNumber.intValue();
                    break;
                }
                case DOUBLE: {
                    if (upperNumber != null) {
                        upperNumber = upperNumber.doubleValue();
                    }
                    if (lowerNumber == null) break;
                    lowerNumber = lowerNumber.doubleValue();
                    break;
                }
                case FLOAT: {
                    if (upperNumber != null) {
                        upperNumber = Float.valueOf(upperNumber.floatValue());
                    }
                    if (lowerNumber == null) break;
                    lowerNumber = Float.valueOf(lowerNumber.floatValue());
                }
            }
            NumericQueryNode lowerNode = new NumericQueryNode(termRangeNode.getField(), lowerNumber, numberFormat);
            NumericQueryNode upperNode = new NumericQueryNode(termRangeNode.getField(), upperNumber, numberFormat);
            boolean lowerInclusive = termRangeNode.isLowerInclusive();
            boolean upperInclusive = termRangeNode.isUpperInclusive();
            return new NumericRangeQueryNode(lowerNode, upperNode, lowerInclusive, upperInclusive, numericConfig);
        }
        return node;
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        return node;
    }

    @Override
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}

