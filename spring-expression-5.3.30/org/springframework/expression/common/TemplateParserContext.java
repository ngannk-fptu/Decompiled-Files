/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression.common;

import org.springframework.expression.ParserContext;

public class TemplateParserContext
implements ParserContext {
    private final String expressionPrefix;
    private final String expressionSuffix;

    public TemplateParserContext() {
        this("#{", "}");
    }

    public TemplateParserContext(String expressionPrefix, String expressionSuffix) {
        this.expressionPrefix = expressionPrefix;
        this.expressionSuffix = expressionSuffix;
    }

    @Override
    public final boolean isTemplate() {
        return true;
    }

    @Override
    public final String getExpressionPrefix() {
        return this.expressionPrefix;
    }

    @Override
    public final String getExpressionSuffix() {
        return this.expressionSuffix;
    }
}

