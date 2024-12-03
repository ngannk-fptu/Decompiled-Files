/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.builders;

import java.util.HashMap;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;

public class QueryTreeBuilder
implements QueryBuilder {
    public static final String QUERY_TREE_BUILDER_TAGID = QueryTreeBuilder.class.getName();
    private HashMap<Class<? extends QueryNode>, QueryBuilder> queryNodeBuilders;
    private HashMap<String, QueryBuilder> fieldNameBuilders;

    public void setBuilder(CharSequence fieldName, QueryBuilder builder) {
        if (this.fieldNameBuilders == null) {
            this.fieldNameBuilders = new HashMap();
        }
        this.fieldNameBuilders.put(fieldName.toString(), builder);
    }

    public void setBuilder(Class<? extends QueryNode> queryNodeClass, QueryBuilder builder) {
        if (this.queryNodeBuilders == null) {
            this.queryNodeBuilders = new HashMap();
        }
        this.queryNodeBuilders.put(queryNodeClass, builder);
    }

    private void process(QueryNode node) throws QueryNodeException {
        if (node != null) {
            List<QueryNode> children;
            QueryBuilder builder = this.getBuilder(node);
            if (!(builder instanceof QueryTreeBuilder) && (children = node.getChildren()) != null) {
                for (QueryNode child : children) {
                    this.process(child);
                }
            }
            this.processNode(node, builder);
        }
    }

    private QueryBuilder getBuilder(QueryNode node) {
        QueryBuilder builder = null;
        if (this.fieldNameBuilders != null && node instanceof FieldableNode) {
            CharSequence field = ((FieldableNode)node).getField();
            if (field != null) {
                field = field.toString();
            }
            builder = this.fieldNameBuilders.get(field);
        }
        if (builder == null && this.queryNodeBuilders != null) {
            Class<?> clazz = node.getClass();
            do {
                Class<?> actualClass;
                Class<?>[] classes;
                if ((builder = this.getQueryBuilder(clazz)) != null) continue;
                Class<?>[] classArray = classes = clazz.getInterfaces();
                int n = classArray.length;
                for (int i = 0; i < n && (builder = this.getQueryBuilder(actualClass = classArray[i])) == null; ++i) {
                }
            } while (builder == null && (clazz = clazz.getSuperclass()) != null);
        }
        return builder;
    }

    private void processNode(QueryNode node, QueryBuilder builder) throws QueryNodeException {
        if (builder == null) {
            throw new QueryNodeException(new MessageImpl(QueryParserMessages.LUCENE_QUERY_CONVERSION_ERROR, node.toQueryString(new EscapeQuerySyntaxImpl()), node.getClass().getName()));
        }
        Object obj = builder.build(node);
        if (obj != null) {
            node.setTag(QUERY_TREE_BUILDER_TAGID, obj);
        }
    }

    private QueryBuilder getQueryBuilder(Class<?> clazz) {
        if (QueryNode.class.isAssignableFrom(clazz)) {
            return this.queryNodeBuilders.get(clazz);
        }
        return null;
    }

    @Override
    public Object build(QueryNode queryNode) throws QueryNodeException {
        this.process(queryNode);
        return queryNode.getTag(QUERY_TREE_BUILDER_TAGID);
    }
}

