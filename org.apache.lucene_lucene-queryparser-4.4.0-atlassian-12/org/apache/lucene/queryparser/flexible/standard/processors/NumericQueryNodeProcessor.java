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
import org.apache.lucene.queryparser.flexible.core.nodes.RangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.NumericRangeQueryNode;

public class NumericQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        NumericConfig numericConfig;
        FieldQueryNode fieldNode;
        FieldConfig fieldConfig;
        QueryConfigHandler config;
        if (node instanceof FieldQueryNode && !(node.getParent() instanceof RangeQueryNode) && (config = this.getQueryConfigHandler()) != null && (fieldConfig = config.getFieldConfig((fieldNode = (FieldQueryNode)node).getFieldAsString())) != null && (numericConfig = fieldConfig.get(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG)) != null) {
            NumberFormat numberFormat = numericConfig.getNumberFormat();
            String text = fieldNode.getTextAsString();
            Number number = null;
            if (text.length() > 0) {
                try {
                    number = numberFormat.parse(text);
                }
                catch (ParseException e) {
                    throw new QueryNodeParseException(new MessageImpl(QueryParserMessages.COULD_NOT_PARSE_NUMBER, fieldNode.getTextAsString(), numberFormat.getClass().getCanonicalName()), (Throwable)e);
                }
                switch (numericConfig.getType()) {
                    case LONG: {
                        number = number.longValue();
                        break;
                    }
                    case INT: {
                        number = number.intValue();
                        break;
                    }
                    case DOUBLE: {
                        number = number.doubleValue();
                        break;
                    }
                    case FLOAT: {
                        number = Float.valueOf(number.floatValue());
                    }
                }
            } else {
                throw new QueryNodeParseException(new MessageImpl(QueryParserMessages.NUMERIC_CANNOT_BE_EMPTY, fieldNode.getFieldAsString()));
            }
            NumericQueryNode lowerNode = new NumericQueryNode(fieldNode.getField(), number, numberFormat);
            NumericQueryNode upperNode = new NumericQueryNode(fieldNode.getField(), number, numberFormat);
            return new NumericRangeQueryNode(lowerNode, upperNode, true, true, numericConfig);
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

