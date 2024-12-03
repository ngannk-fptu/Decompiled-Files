/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.core.UncheckedParseException;
import freemarker.core._CoreStringUtils;
import freemarker.template.TemplateException;
import java.io.IOException;

class EscapeBlock
extends TemplateElement {
    private final String variable;
    private final Expression expr;
    private Expression escapedExpr;

    EscapeBlock(String variable, Expression expr, Expression escapedExpr) {
        this.variable = variable;
        this.expr = expr;
        this.escapedExpr = escapedExpr;
    }

    void setContent(TemplateElements children) {
        this.setChildren(children);
        this.escapedExpr = null;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return this.getChildBuffer();
    }

    Expression doEscape(Expression expression) throws ParseException {
        try {
            return this.escapedExpr.deepCloneWithIdentifierReplaced(this.variable, expression, new Expression.ReplacemenetState());
        }
        catch (UncheckedParseException e) {
            throw e.getParseException();
        }
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol()).append(' ').append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.variable)).append(" as ").append(this.expr.getCanonicalForm());
        if (canonical) {
            sb.append('>');
            sb.append(this.getChildrenCanonicalForm());
            sb.append("</").append(this.getNodeTypeSymbol()).append('>');
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#escape";
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.variable;
            }
            case 1: {
                return this.expr;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.PLACEHOLDER_VARIABLE;
            }
            case 1: {
                return ParameterRole.EXPRESSION_TEMPLATE;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isOutputCacheable() {
        return true;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

