/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.GreaterThanOrEqualsFilter;
import org.springframework.ldap.filter.LessThanOrEqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.ldap.filter.PresentFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.ldap.query.AppendableContainerCriteria;
import org.springframework.ldap.query.ConditionCriteria;
import org.springframework.ldap.query.ContainerCriteria;

class DefaultConditionCriteria
implements ConditionCriteria {
    private final AppendableContainerCriteria parent;
    private final String attribute;
    private boolean negated = false;

    DefaultConditionCriteria(AppendableContainerCriteria parent, String attribute) {
        this.parent = parent;
        this.attribute = attribute;
    }

    @Override
    public ContainerCriteria is(String value) {
        return this.appendToParent(new EqualsFilter(this.attribute, value));
    }

    @Override
    public ContainerCriteria gte(String value) {
        return this.appendToParent(new GreaterThanOrEqualsFilter(this.attribute, value));
    }

    @Override
    public ContainerCriteria lte(String value) {
        return this.appendToParent(new LessThanOrEqualsFilter(this.attribute, value));
    }

    @Override
    public ContainerCriteria like(String value) {
        return this.appendToParent(new LikeFilter(this.attribute, value));
    }

    @Override
    public ContainerCriteria whitespaceWildcardsLike(String value) {
        return this.appendToParent(new WhitespaceWildcardsFilter(this.attribute, value));
    }

    @Override
    public ContainerCriteria isPresent() {
        return this.appendToParent(new PresentFilter(this.attribute));
    }

    private ContainerCriteria appendToParent(Filter filter) {
        return this.parent.append(this.negateIfApplicable(filter));
    }

    private Filter negateIfApplicable(Filter myFilter) {
        if (this.negated) {
            return new NotFilter(myFilter);
        }
        return myFilter;
    }

    @Override
    public DefaultConditionCriteria not() {
        this.negated = !this.negated;
        return this;
    }
}

