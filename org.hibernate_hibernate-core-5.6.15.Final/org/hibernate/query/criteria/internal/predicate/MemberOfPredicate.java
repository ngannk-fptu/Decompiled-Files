/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

public class MemberOfPredicate<E, C extends Collection<E>>
extends AbstractSimplePredicate
implements Serializable {
    private final Expression<E> elementExpression;
    private final PluralAttributePath<C> collectionPath;

    public MemberOfPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<E> elementExpression, PluralAttributePath<C> collectionPath) {
        super(criteriaBuilder);
        this.elementExpression = elementExpression;
        this.collectionPath = collectionPath;
    }

    public MemberOfPredicate(CriteriaBuilderImpl criteriaBuilder, E element, PluralAttributePath<C> collectionPath) {
        this(criteriaBuilder, new LiteralExpression<E>(criteriaBuilder, element), collectionPath);
    }

    public PluralAttributePath<C> getCollectionPath() {
        return this.collectionPath;
    }

    public Expression<E> getElementExpression() {
        return this.elementExpression;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getCollectionPath(), registry);
        ParameterContainer.Helper.possibleParameter(this.getElementExpression(), registry);
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        return ((Renderable)this.elementExpression).render(renderingContext) + (isNegated ? " not" : "") + " member of " + this.getCollectionPath().render(renderingContext);
    }
}

