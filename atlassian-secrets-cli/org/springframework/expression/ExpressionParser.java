/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;

public interface ExpressionParser {
    public Expression parseExpression(String var1) throws ParseException;

    public Expression parseExpression(String var1, ParserContext var2) throws ParseException;
}

