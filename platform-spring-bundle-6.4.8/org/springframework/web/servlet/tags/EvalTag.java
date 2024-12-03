/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.el.VariableResolver
 */
package org.springframework.web.servlet.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.VariableResolver;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.tags.HtmlEscapingAwareTag;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;

public class EvalTag
extends HtmlEscapingAwareTag {
    private static final String EVALUATION_CONTEXT_PAGE_ATTRIBUTE = "org.springframework.web.servlet.tags.EVALUATION_CONTEXT";
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    @Nullable
    private Expression expression;
    @Nullable
    private String var;
    private int scope = 1;
    private boolean javaScriptEscape = false;

    public void setExpression(String expression) {
        this.expression = this.expressionParser.parseExpression(expression);
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = TagUtils.getScope(scope);
    }

    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    @Override
    public int doStartTagInternal() throws JspException {
        return 1;
    }

    public int doEndTag() throws JspException {
        EvaluationContext evaluationContext = (EvaluationContext)this.pageContext.getAttribute(EVALUATION_CONTEXT_PAGE_ATTRIBUTE);
        if (evaluationContext == null) {
            evaluationContext = this.createEvaluationContext(this.pageContext);
            this.pageContext.setAttribute(EVALUATION_CONTEXT_PAGE_ATTRIBUTE, (Object)evaluationContext);
        }
        if (this.var != null) {
            Object result = this.expression != null ? this.expression.getValue(evaluationContext) : null;
            this.pageContext.setAttribute(this.var, result, this.scope);
        } else {
            try {
                String result = this.expression != null ? this.expression.getValue(evaluationContext, String.class) : null;
                result = ObjectUtils.getDisplayString(result);
                result = this.htmlEscape(result);
                result = this.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(result) : result;
                this.pageContext.getOut().print(result);
            }
            catch (IOException ex) {
                throw new JspException((Throwable)ex);
            }
        }
        return 6;
    }

    private EvaluationContext createEvaluationContext(PageContext pageContext) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.addPropertyAccessor(new JspPropertyAccessor(pageContext));
        context.addPropertyAccessor(new MapAccessor());
        context.addPropertyAccessor(new EnvironmentAccessor());
        context.setBeanResolver(new BeanFactoryResolver(this.getRequestContext().getWebApplicationContext()));
        ConversionService conversionService = this.getConversionService(pageContext);
        if (conversionService != null) {
            context.setTypeConverter(new StandardTypeConverter(conversionService));
        }
        return context;
    }

    @Nullable
    private ConversionService getConversionService(PageContext pageContext) {
        return (ConversionService)pageContext.getRequest().getAttribute(ConversionService.class.getName());
    }

    private static class JspPropertyAccessor
    implements PropertyAccessor {
        private final PageContext pageContext;
        @Nullable
        private final VariableResolver variableResolver;

        public JspPropertyAccessor(PageContext pageContext) {
            this.pageContext = pageContext;
            this.variableResolver = pageContext.getVariableResolver();
        }

        @Override
        @Nullable
        public Class<?>[] getSpecificTargetClasses() {
            return null;
        }

        @Override
        public boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
            return target == null && (this.resolveImplicitVariable(name) != null || this.pageContext.findAttribute(name) != null);
        }

        @Override
        public TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException {
            Object implicitVar = this.resolveImplicitVariable(name);
            if (implicitVar != null) {
                return new TypedValue(implicitVar);
            }
            return new TypedValue(this.pageContext.findAttribute(name));
        }

        @Override
        public boolean canWrite(EvaluationContext context, @Nullable Object target, String name) {
            return false;
        }

        @Override
        public void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        private Object resolveImplicitVariable(String name) throws AccessException {
            if (this.variableResolver == null) {
                return null;
            }
            try {
                return this.variableResolver.resolveVariable(name);
            }
            catch (Exception ex) {
                throw new AccessException("Unexpected exception occurred accessing '" + name + "' as an implicit variable", ex);
            }
        }
    }
}

