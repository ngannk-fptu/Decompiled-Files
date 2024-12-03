/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanExpressionException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.config.BeanExpressionContext
 *  org.springframework.beans.factory.config.BeanExpressionResolver
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.expression.BeanResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ExpressionParser
 *  org.springframework.expression.ParserContext
 *  org.springframework.expression.PropertyAccessor
 *  org.springframework.expression.TypeConverter
 *  org.springframework.expression.TypeLocator
 *  org.springframework.expression.spel.SpelParserConfiguration
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.expression.spel.support.StandardTypeConverter
 *  org.springframework.expression.spel.support.StandardTypeLocator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.context.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanExpressionException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.TypeLocator;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class StandardBeanExpressionResolver
implements BeanExpressionResolver {
    public static final String DEFAULT_EXPRESSION_PREFIX = "#{";
    public static final String DEFAULT_EXPRESSION_SUFFIX = "}";
    private String expressionPrefix = "#{";
    private String expressionSuffix = "}";
    private ExpressionParser expressionParser;
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<String, Expression>(256);
    private final Map<BeanExpressionContext, StandardEvaluationContext> evaluationCache = new ConcurrentHashMap<BeanExpressionContext, StandardEvaluationContext>(8);
    private final ParserContext beanExpressionParserContext = new ParserContext(){

        public boolean isTemplate() {
            return true;
        }

        public String getExpressionPrefix() {
            return StandardBeanExpressionResolver.this.expressionPrefix;
        }

        public String getExpressionSuffix() {
            return StandardBeanExpressionResolver.this.expressionSuffix;
        }
    };

    public StandardBeanExpressionResolver() {
        this.expressionParser = new SpelExpressionParser();
    }

    public StandardBeanExpressionResolver(@Nullable ClassLoader beanClassLoader) {
        this.expressionParser = new SpelExpressionParser(new SpelParserConfiguration(null, beanClassLoader));
    }

    public void setExpressionPrefix(String expressionPrefix) {
        Assert.hasText((String)expressionPrefix, (String)"Expression prefix must not be empty");
        this.expressionPrefix = expressionPrefix;
    }

    public void setExpressionSuffix(String expressionSuffix) {
        Assert.hasText((String)expressionSuffix, (String)"Expression suffix must not be empty");
        this.expressionSuffix = expressionSuffix;
    }

    public void setExpressionParser(ExpressionParser expressionParser) {
        Assert.notNull((Object)expressionParser, (String)"ExpressionParser must not be null");
        this.expressionParser = expressionParser;
    }

    @Nullable
    public Object evaluate(@Nullable String value, BeanExpressionContext beanExpressionContext) throws BeansException {
        if (!StringUtils.hasLength((String)value)) {
            return value;
        }
        try {
            StandardEvaluationContext sec;
            Expression expr = this.expressionCache.get(value);
            if (expr == null) {
                expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
                this.expressionCache.put(value, expr);
            }
            if ((sec = this.evaluationCache.get(beanExpressionContext)) == null) {
                sec = new StandardEvaluationContext((Object)beanExpressionContext);
                sec.addPropertyAccessor((PropertyAccessor)new BeanExpressionContextAccessor());
                sec.addPropertyAccessor((PropertyAccessor)new BeanFactoryAccessor());
                sec.addPropertyAccessor((PropertyAccessor)new MapAccessor());
                sec.addPropertyAccessor((PropertyAccessor)new EnvironmentAccessor());
                sec.setBeanResolver((BeanResolver)new BeanFactoryResolver((BeanFactory)beanExpressionContext.getBeanFactory()));
                sec.setTypeLocator((TypeLocator)new StandardTypeLocator(beanExpressionContext.getBeanFactory().getBeanClassLoader()));
                sec.setTypeConverter((TypeConverter)new StandardTypeConverter(() -> {
                    ConversionService cs = beanExpressionContext.getBeanFactory().getConversionService();
                    return cs != null ? cs : DefaultConversionService.getSharedInstance();
                }));
                this.customizeEvaluationContext(sec);
                this.evaluationCache.put(beanExpressionContext, sec);
            }
            return expr.getValue((EvaluationContext)sec);
        }
        catch (Throwable ex) {
            throw new BeanExpressionException("Expression parsing failed", ex);
        }
    }

    protected void customizeEvaluationContext(StandardEvaluationContext evalContext) {
    }
}

