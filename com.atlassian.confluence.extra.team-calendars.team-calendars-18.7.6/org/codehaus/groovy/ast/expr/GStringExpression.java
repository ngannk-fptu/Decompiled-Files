/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class GStringExpression
extends Expression {
    private String verbatimText;
    private List<ConstantExpression> strings = new ArrayList<ConstantExpression>();
    private List<Expression> values = new ArrayList<Expression>();

    public GStringExpression(String verbatimText) {
        this.verbatimText = verbatimText;
        super.setType(ClassHelper.GSTRING_TYPE);
    }

    public GStringExpression(String verbatimText, List<ConstantExpression> strings, List<Expression> values) {
        this.verbatimText = verbatimText;
        this.strings = strings;
        this.values = values;
        super.setType(ClassHelper.GSTRING_TYPE);
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitGStringExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        GStringExpression ret = new GStringExpression(this.verbatimText, this.transformExpressions(this.strings, transformer, ConstantExpression.class), this.transformExpressions(this.values, transformer));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public String toString() {
        return super.toString() + "[strings: " + this.strings + " values: " + this.values + "]";
    }

    @Override
    public String getText() {
        return this.verbatimText;
    }

    public List<ConstantExpression> getStrings() {
        return this.strings;
    }

    public List<Expression> getValues() {
        return this.values;
    }

    public void addString(ConstantExpression text) {
        if (text == null) {
            throw new NullPointerException("Cannot add a null text expression");
        }
        this.strings.add(text);
    }

    public void addValue(Expression value) {
        if (this.strings.isEmpty()) {
            this.strings.add(ConstantExpression.EMPTY_STRING);
        }
        this.values.add(value);
    }

    public Expression getValue(int idx) {
        return this.values.get(idx);
    }

    public boolean isConstantString() {
        return this.values.isEmpty();
    }

    public Expression asConstantString() {
        StringBuilder buffer = new StringBuilder();
        for (ConstantExpression expression : this.strings) {
            Object value = expression.getValue();
            if (value == null) continue;
            buffer.append(value);
        }
        return new ConstantExpression(buffer.toString());
    }
}

