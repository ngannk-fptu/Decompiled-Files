/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.expression.spel.standard;

import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.InternalSpelExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SpelExpressionParser
extends TemplateAwareExpressionParser {
    private final SpelParserConfiguration configuration;

    public SpelExpressionParser() {
        this.configuration = new SpelParserConfiguration();
    }

    public SpelExpressionParser(SpelParserConfiguration configuration) {
        Assert.notNull((Object)configuration, (String)"SpelParserConfiguration must not be null");
        this.configuration = configuration;
    }

    public SpelExpression parseRaw(String expressionString) throws ParseException {
        Assert.hasText((String)expressionString, (String)"'expressionString' must not be null or blank");
        return this.doParseExpression(expressionString, null);
    }

    @Override
    protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
        return new InternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
    }
}

