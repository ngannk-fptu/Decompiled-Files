/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.impl.hibernate.query;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.hibernate.query.InClauseType;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.query.Query;

@Internal
public class InExpressionBuilder {
    private InClauseType clauseType;
    private int inExpressionLimit;
    private String fieldName;
    private String parameterName;
    private List<String> parameterValues;

    private InExpressionBuilder(InClauseType clauseType, String fieldName, String parameterName, List<String> parameterValues, int inExpressionLimit) {
        this.clauseType = clauseType;
        this.inExpressionLimit = inExpressionLimit;
        this.fieldName = fieldName;
        this.parameterName = parameterName;
        this.parameterValues = parameterValues;
    }

    public static InExpressionBuilder getInExpressionBuilderDefaultLimit(String fieldName, String parameterName, List<String> parameterValues, Dialect dialect) {
        return new InExpressionBuilder(InClauseType.IN, fieldName, parameterName, parameterValues, dialect.getInExpressionCountLimit());
    }

    public static InExpressionBuilder getInExpressionBuilderCustomLimit(String fieldName, String parameterName, List<String> parameterValues, int inExpressionLimit) {
        return new InExpressionBuilder(InClauseType.IN, fieldName, parameterName, parameterValues, inExpressionLimit);
    }

    public static InExpressionBuilder getInExpressionBuilderDefaultLimit(String fieldName, String parameterName, Dialect dialect) {
        return new InExpressionBuilder(InClauseType.IN, fieldName, parameterName, new ArrayList<String>(), dialect.getInExpressionCountLimit());
    }

    public static InExpressionBuilder getNotInExpressionBuilderDefaultLimit(String fieldName, String parameterName, List<String> parameterValues, Dialect dialect) {
        return new InExpressionBuilder(InClauseType.NOT_IN, fieldName, parameterName, parameterValues, dialect.getInExpressionCountLimit());
    }

    public static InExpressionBuilder getNotInExpressionBuilderCustomLimit(String fieldName, String parameterName, List<String> parameterValues, int inExpressionLimit) {
        return new InExpressionBuilder(InClauseType.NOT_IN, fieldName, parameterName, parameterValues, inExpressionLimit);
    }

    public static InExpressionBuilder getNotInExpressionBuilderDefaultLimit(String fieldName, String parameterName, Dialect dialect) {
        return new InExpressionBuilder(InClauseType.NOT_IN, fieldName, parameterName, new ArrayList<String>(), dialect.getInExpressionCountLimit());
    }

    private boolean shouldPartition() {
        return this.inExpressionLimit > 0 && this.parameterValues.size() > this.inExpressionLimit;
    }

    public String buildInExpressionString() {
        int listSize = this.parameterValues.size();
        if (listSize <= 0) {
            return this.clauseType == InClauseType.IN ? "(1 = 0)" : "(1 = 1)";
        }
        if (this.shouldPartition()) {
            int numPartitions = (listSize + this.inExpressionLimit - 1) / this.inExpressionLimit;
            return "(" + Stream.iterate(0, i -> i + 1).limit(numPartitions).map(i -> String.format("%s %s (:%s%d)", this.fieldName, this.clauseType.getClauseType(), this.parameterName, i)).collect(Collectors.joining(this.clauseType.getJoinString())) + ")";
        }
        return String.format("%s %s (:%s)", this.fieldName, this.clauseType.getClauseType(), this.parameterName);
    }

    public String convertIdsToInClauseString(List<Long> ids) {
        if (ids.isEmpty()) {
            return this.clauseType == InClauseType.IN ? "(1 = 0)" : "(1 = 1)";
        }
        if (this.inExpressionLimit > 0 && ids.size() > this.inExpressionLimit) {
            List partitions = Lists.partition(ids, (int)this.inExpressionLimit);
            return "(" + Stream.iterate(0, i -> i + 1).limit(partitions.size()).map(i -> String.format("%s %s (%s)", this.fieldName, this.clauseType.getClauseType(), StringUtils.join((Iterable)((Iterable)partitions.get((int)i)), (char)','))).collect(Collectors.joining(this.clauseType.getJoinString())) + ")";
        }
        return String.format("%s %s (%s)", this.fieldName, this.clauseType.getClauseType(), StringUtils.join(ids, (char)','));
    }

    public void substituteInExpressionParameters(Query query) {
        if (this.parameterValues.size() == 0) {
            return;
        }
        if (this.shouldPartition()) {
            List partitions = Lists.partition(this.parameterValues, (int)this.inExpressionLimit);
            for (int i = 0; i < partitions.size(); ++i) {
                query.setParameterList(String.format("%s%d", this.parameterName, i), (Collection)partitions.get(i));
            }
        } else {
            query.setParameterList(this.parameterName, this.parameterValues);
        }
    }
}

