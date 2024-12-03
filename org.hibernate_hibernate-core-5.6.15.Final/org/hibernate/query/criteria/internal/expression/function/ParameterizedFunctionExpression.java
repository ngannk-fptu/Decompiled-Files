/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;
import org.hibernate.query.criteria.internal.expression.function.FunctionExpression;

public class ParameterizedFunctionExpression<X>
extends BasicFunctionExpression<X>
implements FunctionExpression<X> {
    public static final List<String> STANDARD_JPA_FUNCTION_NAMES = Arrays.asList("CONCAT", "SUBSTRING", "TRIM", "UPPER", "LOWER", "LOCATE", "LENGTH", "ABS", "SQRT", "MOD", "SIZE", "INDEX", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP");
    private final List<Expression<?>> argumentExpressions;
    private final boolean isStandardJpaFunction;

    public ParameterizedFunctionExpression(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, String functionName, List<Expression<?>> argumentExpressions) {
        super(criteriaBuilder, javaType, functionName);
        this.argumentExpressions = argumentExpressions;
        this.isStandardJpaFunction = STANDARD_JPA_FUNCTION_NAMES.contains(functionName.toUpperCase(Locale.ROOT));
    }

    public ParameterizedFunctionExpression(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, String functionName, Expression<?> ... argumentExpressions) {
        super(criteriaBuilder, javaType, functionName);
        this.argumentExpressions = Arrays.asList(argumentExpressions);
        this.isStandardJpaFunction = STANDARD_JPA_FUNCTION_NAMES.contains(functionName.toUpperCase(Locale.ROOT));
    }

    protected boolean isStandardJpaFunction() {
        return this.isStandardJpaFunction;
    }

    protected static int properSize(int number) {
        return number + (int)((double)number * 0.75) + 1;
    }

    public List<Expression<?>> getArgumentExpressions() {
        return this.argumentExpressions;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        for (Expression<?> argument : this.getArgumentExpressions()) {
            if (!ParameterContainer.class.isInstance(argument)) continue;
            ((ParameterContainer)argument).registerParameters(registry);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String render(RenderingContext renderingContext) {
        renderingContext.getFunctionStack().push(this);
        try {
            StringBuilder buffer = new StringBuilder();
            if (this.isStandardJpaFunction()) {
                buffer.append(this.getFunctionName()).append("(");
            } else {
                buffer.append("function('").append(this.getFunctionName()).append("', ");
            }
            this.renderArguments(buffer, renderingContext);
            String string = buffer.append(')').toString();
            return string;
        }
        finally {
            renderingContext.getFunctionStack().pop();
        }
    }

    protected void renderArguments(StringBuilder buffer, RenderingContext renderingContext) {
        String sep = "";
        for (Expression<?> argument : this.argumentExpressions) {
            buffer.append(sep).append(((Renderable)argument).render(renderingContext));
            sep = ", ";
        }
    }
}

