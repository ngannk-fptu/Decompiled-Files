/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

import com.amazonaws.jmespath.Comparator;
import com.amazonaws.jmespath.InvalidTypeException;
import com.amazonaws.jmespath.JmesPathAndExpression;
import com.amazonaws.jmespath.JmesPathField;
import com.amazonaws.jmespath.JmesPathFilter;
import com.amazonaws.jmespath.JmesPathFlatten;
import com.amazonaws.jmespath.JmesPathFunction;
import com.amazonaws.jmespath.JmesPathIdentity;
import com.amazonaws.jmespath.JmesPathLiteral;
import com.amazonaws.jmespath.JmesPathMultiSelectList;
import com.amazonaws.jmespath.JmesPathNotExpression;
import com.amazonaws.jmespath.JmesPathProjection;
import com.amazonaws.jmespath.JmesPathSubExpression;
import com.amazonaws.jmespath.JmesPathValueProjection;

public interface JmesPathVisitor<Input, Output> {
    public Output visit(JmesPathSubExpression var1, Input var2) throws InvalidTypeException;

    public Output visit(JmesPathField var1, Input var2);

    public Output visit(JmesPathProjection var1, Input var2) throws InvalidTypeException;

    public Output visit(JmesPathFlatten var1, Input var2) throws InvalidTypeException;

    public Output visit(JmesPathIdentity var1, Input var2);

    public Output visit(JmesPathValueProjection var1, Input var2) throws InvalidTypeException;

    public Output visit(JmesPathFunction var1, Input var2) throws InvalidTypeException;

    public Output visit(JmesPathLiteral var1, Input var2);

    public Output visit(JmesPathFilter var1, Input var2) throws InvalidTypeException;

    public Output visit(Comparator var1, Input var2) throws InvalidTypeException;

    public Output visit(JmesPathNotExpression var1, Input var2) throws InvalidTypeException;

    public Output visit(JmesPathAndExpression var1, Input var2) throws InvalidTypeException;

    public Output visit(JmesPathMultiSelectList var1, Input var2) throws InvalidTypeException;
}

