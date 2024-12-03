/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.expression;

public interface ParserContext {
    public static final ParserContext TEMPLATE_EXPRESSION = new ParserContext(){

        @Override
        public boolean isTemplate() {
            return true;
        }

        @Override
        public String getExpressionPrefix() {
            return "#{";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }
    };

    public boolean isTemplate();

    public String getExpressionPrefix();

    public String getExpressionSuffix();
}

