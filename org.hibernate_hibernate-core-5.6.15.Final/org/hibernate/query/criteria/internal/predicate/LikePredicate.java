/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

public class LikePredicate
extends AbstractSimplePredicate
implements Serializable {
    private final Expression<String> matchExpression;
    private final Expression<String> pattern;
    private final Expression<Character> escapeCharacter;

    public LikePredicate(CriteriaBuilderImpl criteriaBuilder, Expression<String> matchExpression, Expression<String> pattern) {
        this(criteriaBuilder, matchExpression, pattern, null);
    }

    public LikePredicate(CriteriaBuilderImpl criteriaBuilder, Expression<String> matchExpression, String pattern) {
        this(criteriaBuilder, matchExpression, new LiteralExpression<String>(criteriaBuilder, pattern));
    }

    public LikePredicate(CriteriaBuilderImpl criteriaBuilder, Expression<String> matchExpression, Expression<String> pattern, Expression<Character> escapeCharacter) {
        super(criteriaBuilder);
        this.matchExpression = matchExpression;
        this.pattern = pattern;
        this.escapeCharacter = escapeCharacter;
    }

    public LikePredicate(CriteriaBuilderImpl criteriaBuilder, Expression<String> matchExpression, Expression<String> pattern, char escapeCharacter) {
        this(criteriaBuilder, matchExpression, pattern, new LiteralExpression<Character>(criteriaBuilder, Character.valueOf(escapeCharacter)));
    }

    public LikePredicate(CriteriaBuilderImpl criteriaBuilder, Expression<String> matchExpression, String pattern, char escapeCharacter) {
        this(criteriaBuilder, matchExpression, new LiteralExpression<String>(criteriaBuilder, pattern), new LiteralExpression<Character>(criteriaBuilder, Character.valueOf(escapeCharacter)));
    }

    public LikePredicate(CriteriaBuilderImpl criteriaBuilder, Expression<String> matchExpression, String pattern, Expression<Character> escapeCharacter) {
        this(criteriaBuilder, matchExpression, new LiteralExpression<String>(criteriaBuilder, pattern), escapeCharacter);
    }

    public Expression<Character> getEscapeCharacter() {
        return this.escapeCharacter;
    }

    public Expression<String> getMatchExpression() {
        return this.matchExpression;
    }

    public Expression<String> getPattern() {
        return this.pattern;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getEscapeCharacter(), registry);
        ParameterContainer.Helper.possibleParameter(this.getMatchExpression(), registry);
        ParameterContainer.Helper.possibleParameter(this.getPattern(), registry);
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        String operator = isNegated ? " not like " : " like ";
        StringBuilder buffer = new StringBuilder();
        buffer.append(((Renderable)this.getMatchExpression()).render(renderingContext)).append(operator).append(((Renderable)this.getPattern()).render(renderingContext));
        if (this.escapeCharacter != null) {
            buffer.append(" escape ").append(((Renderable)this.getEscapeCharacter()).render(renderingContext));
        }
        return buffer.toString();
    }
}

