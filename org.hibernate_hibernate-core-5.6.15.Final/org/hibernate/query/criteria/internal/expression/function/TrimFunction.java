/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaBuilder$Trimspec
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;

public class TrimFunction
extends BasicFunctionExpression<String>
implements Serializable {
    public static final String NAME = "trim";
    public static final CriteriaBuilder.Trimspec DEFAULT_TRIMSPEC = CriteriaBuilder.Trimspec.BOTH;
    public static final char DEFAULT_TRIM_CHAR = ' ';
    private final CriteriaBuilder.Trimspec trimspec;
    private final Expression<Character> trimCharacter;
    private final Expression<String> trimSource;

    public TrimFunction(CriteriaBuilderImpl criteriaBuilder, CriteriaBuilder.Trimspec trimspec, Expression<Character> trimCharacter, Expression<String> trimSource) {
        super(criteriaBuilder, String.class, NAME);
        this.trimspec = trimspec;
        this.trimCharacter = trimCharacter;
        this.trimSource = trimSource;
    }

    public TrimFunction(CriteriaBuilderImpl criteriaBuilder, CriteriaBuilder.Trimspec trimspec, char trimCharacter, Expression<String> trimSource) {
        super(criteriaBuilder, String.class, NAME);
        this.trimspec = trimspec;
        this.trimCharacter = new LiteralExpression<Character>(criteriaBuilder, Character.valueOf(trimCharacter));
        this.trimSource = trimSource;
    }

    public TrimFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> trimSource) {
        this(criteriaBuilder, DEFAULT_TRIMSPEC, ' ', trimSource);
    }

    public TrimFunction(CriteriaBuilderImpl criteriaBuilder, Expression<Character> trimCharacter, Expression<String> trimSource) {
        this(criteriaBuilder, DEFAULT_TRIMSPEC, trimCharacter, trimSource);
    }

    public TrimFunction(CriteriaBuilderImpl criteriaBuilder, char trimCharacter, Expression<String> trimSource) {
        this(criteriaBuilder, DEFAULT_TRIMSPEC, trimCharacter, trimSource);
    }

    public TrimFunction(CriteriaBuilderImpl criteriaBuilder, CriteriaBuilder.Trimspec trimspec, Expression<String> trimSource) {
        this(criteriaBuilder, trimspec, ' ', trimSource);
    }

    public Expression<Character> getTrimCharacter() {
        return this.trimCharacter;
    }

    public Expression<String> getTrimSource() {
        return this.trimSource;
    }

    public CriteriaBuilder.Trimspec getTrimspec() {
        return this.trimspec;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getTrimCharacter(), registry);
        ParameterContainer.Helper.possibleParameter(this.getTrimSource(), registry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String render(RenderingContext renderingContext) {
        renderingContext.getFunctionStack().push(this);
        try {
            String renderedTrimChar = this.trimCharacter.getClass().isAssignableFrom(LiteralExpression.class) ? '\'' + ((Character)((LiteralExpression)this.trimCharacter).getLiteral()).toString() + '\'' : ((Renderable)this.trimCharacter).render(renderingContext);
            String string = "trim(" + this.trimspec.name() + ' ' + renderedTrimChar + " from " + ((Renderable)this.trimSource).render(renderingContext) + ')';
            return string;
        }
        finally {
            renderingContext.getFunctionStack().pop();
        }
    }
}

