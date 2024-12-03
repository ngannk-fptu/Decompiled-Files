/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import java.util.List;
import java.util.Set;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Direction;
import net.sf.ehcache.search.ExecutionHints;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;
import net.sf.ehcache.search.aggregator.Average;
import net.sf.ehcache.search.aggregator.Count;
import net.sf.ehcache.search.aggregator.Max;
import net.sf.ehcache.search.aggregator.Min;
import net.sf.ehcache.search.aggregator.Sum;
import net.sf.ehcache.search.expression.AlwaysMatch;
import net.sf.ehcache.search.expression.And;
import net.sf.ehcache.search.expression.Between;
import net.sf.ehcache.search.expression.ComparableValue;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.expression.EqualTo;
import net.sf.ehcache.search.expression.GreaterThan;
import net.sf.ehcache.search.expression.GreaterThanOrEqual;
import net.sf.ehcache.search.expression.ILike;
import net.sf.ehcache.search.expression.InCollection;
import net.sf.ehcache.search.expression.IsNull;
import net.sf.ehcache.search.expression.LessThan;
import net.sf.ehcache.search.expression.LessThanOrEqual;
import net.sf.ehcache.search.expression.Not;
import net.sf.ehcache.search.expression.NotEqualTo;
import net.sf.ehcache.search.expression.NotILike;
import net.sf.ehcache.search.expression.NotNull;
import net.sf.ehcache.search.expression.Or;
import net.sf.ehcache.store.StoreQuery;

public abstract class BaseQueryInterpreter {
    public void process(StoreQuery query) {
        this.includeKeys(query.requestsKeys());
        this.includeValues(query.requestsValues());
        this.maxResults(query.maxResults());
        this.processCriteria(query.getCriteria());
        this.processAttributes(query.requestedAttributes());
        this.processOrdering(query.getOrdering());
        this.processGroupBy(query.groupByAttributes());
        this.processAggregators(query.getAggregatorInstances());
        this.processHints(query.getExecutionHints());
    }

    private void processAggregators(List<AggregatorInstance<?>> aggregatorInstances) {
        for (AggregatorInstance<?> aggregatorInstance : aggregatorInstances) {
            if (aggregatorInstance instanceof Count) {
                this.count();
                continue;
            }
            if (aggregatorInstance instanceof Average) {
                this.average(aggregatorInstance.getAttribute().getAttributeName());
                continue;
            }
            if (aggregatorInstance instanceof Sum) {
                this.sum(aggregatorInstance.getAttribute().getAttributeName());
                continue;
            }
            if (aggregatorInstance instanceof Min) {
                this.min(aggregatorInstance.getAttribute().getAttributeName());
                continue;
            }
            if (aggregatorInstance instanceof Max) {
                this.max(aggregatorInstance.getAttribute().getAttributeName());
                continue;
            }
            throw new SearchException("unknown aggregator type: " + aggregatorInstance.getClass().getName());
        }
    }

    private void processAttributes(Set<Attribute<?>> attributes) {
        for (Attribute<?> attr : attributes) {
            this.attribute(attr.getAttributeName());
        }
    }

    private void processOrdering(List<StoreQuery.Ordering> orderings) {
        for (StoreQuery.Ordering ordering : orderings) {
            String attributeName = ordering.getAttribute().getAttributeName();
            if (Direction.DESCENDING.equals((Object)ordering.getDirection())) {
                this.attributeDescending(attributeName);
                continue;
            }
            this.attributeAscending(attributeName);
        }
    }

    private void processGroupBy(Set<Attribute<?>> attributes) {
        for (Attribute<?> attr : attributes) {
            this.groupBy(attr.getAttributeName());
        }
    }

    protected void processCriteria(Criteria criteria) {
        if (criteria instanceof AlwaysMatch) {
            this.all();
        } else if (criteria instanceof And) {
            this.and((And)And.class.cast(criteria));
        } else if (criteria instanceof Or) {
            this.or((Or)Or.class.cast(criteria));
        } else if (criteria instanceof Not) {
            this.processNotCriteria((Not)Not.class.cast(criteria));
        } else if (criteria instanceof NotEqualTo) {
            this.notEqualTerm((NotEqualTo)NotEqualTo.class.cast(criteria));
        } else if (criteria instanceof NotILike) {
            this.notIlike((NotILike)NotILike.class.cast(criteria));
        } else if (criteria instanceof NotNull) {
            this.notNull((NotNull)NotNull.class.cast(criteria));
        } else if (criteria instanceof Between) {
            this.between((Between)Between.class.cast(criteria));
        } else if (criteria instanceof EqualTo) {
            this.equalTo((EqualTo)EqualTo.class.cast(criteria));
        } else if (criteria instanceof IsNull) {
            this.isNull((IsNull)IsNull.class.cast(criteria));
        } else if (criteria instanceof ILike) {
            this.ilike((ILike)ILike.class.cast(criteria));
        } else if (criteria instanceof GreaterThan) {
            this.greaterThan((GreaterThan)GreaterThan.class.cast(criteria));
        } else if (criteria instanceof GreaterThanOrEqual) {
            this.greaterThanEqual((GreaterThanOrEqual)GreaterThanOrEqual.class.cast(criteria));
        } else if (criteria instanceof InCollection) {
            this.in((InCollection)InCollection.class.cast(criteria));
        } else if (criteria instanceof LessThan) {
            this.lessThan((LessThan)LessThan.class.cast(criteria));
        } else if (criteria instanceof LessThanOrEqual) {
            this.lessThanEqual((LessThanOrEqual)LessThanOrEqual.class.cast(criteria));
        } else {
            throw new SearchException("Unknown criteria type: " + criteria);
        }
    }

    private void processNotCriteria(Not not) {
        Criteria negated = not.getCriteria();
        this.processCriteria(BaseQueryInterpreter.notOf(negated));
    }

    private void processHints(ExecutionHints hints) {
        if (hints != null) {
            this.setHints(hints);
        }
    }

    private static Criteria notOf(Criteria c) {
        if (c instanceof NotEqualTo) {
            return new EqualTo(((NotEqualTo)c).getAttributeName(), ((NotEqualTo)c).getValue());
        }
        if (c instanceof EqualTo) {
            return new NotEqualTo(((EqualTo)c).getAttributeName(), ((EqualTo)c).getValue());
        }
        if (c instanceof And) {
            Criteria[] criterion = ((And)c).getCriterion();
            Criteria rv = new Or(BaseQueryInterpreter.notOf(criterion[0]), BaseQueryInterpreter.notOf(criterion[1]));
            for (int i = 2; i < criterion.length; ++i) {
                rv = rv.or(BaseQueryInterpreter.notOf(criterion[i]));
            }
            return rv;
        }
        if (c instanceof Or) {
            Criteria[] criterion = ((Or)c).getCriterion();
            Criteria rv = new And(BaseQueryInterpreter.notOf(criterion[0]), BaseQueryInterpreter.notOf(criterion[1]));
            for (int i = 2; i < criterion.length; ++i) {
                rv = rv.and(BaseQueryInterpreter.notOf(criterion[i]));
            }
            return rv;
        }
        if (c instanceof Between) {
            Between b = (Between)c;
            String name = b.getAttributeName();
            ComparableValue lhs = b.isMinInclusive() ? new LessThan(name, b.getMin()) : new LessThanOrEqual(name, b.getMin());
            ComparableValue rhs = b.isMaxInclusive() ? new GreaterThan(name, b.getMax()) : new GreaterThanOrEqual(name, b.getMax());
            return new Or(lhs, (Criteria)rhs);
        }
        if (c instanceof GreaterThan) {
            return new LessThanOrEqual(((GreaterThan)c).getAttributeName(), ((GreaterThan)c).getComparableValue());
        }
        if (c instanceof GreaterThanOrEqual) {
            return new LessThan(((GreaterThanOrEqual)c).getAttributeName(), ((GreaterThanOrEqual)c).getComparableValue());
        }
        if (c instanceof LessThan) {
            return new GreaterThanOrEqual(((LessThan)c).getAttributeName(), ((LessThan)c).getComparableValue());
        }
        if (c instanceof LessThanOrEqual) {
            return new GreaterThan(((LessThanOrEqual)c).getAttributeName(), ((LessThanOrEqual)c).getComparableValue());
        }
        if (c instanceof Not) {
            return ((Not)c).getCriteria();
        }
        if (c instanceof ILike) {
            ILike ilike = (ILike)c;
            return new NotILike(ilike.getAttributeName(), ilike.getRegex());
        }
        if (c instanceof NotILike) {
            NotILike ni = (NotILike)c;
            return new ILike(ni.getAttributeName(), ni.getRegex());
        }
        if (c instanceof InCollection) {
            InCollection in = (InCollection)c;
            String name = in.getAttributeName();
            Object[] values = in.values().toArray();
            if (values.length == 1) {
                return new NotEqualTo(in.getAttributeName(), values[0]);
            }
            Criteria rv = new And(new NotEqualTo(name, values[0]), (Criteria)new NotEqualTo(name, values[1]));
            for (int i = 2; i < values.length; ++i) {
                rv = rv.and(new NotEqualTo(name, values[i]));
            }
            return rv;
        }
        if (c instanceof IsNull) {
            return new NotNull(((IsNull)c).getAttributeName());
        }
        if (c instanceof NotNull) {
            return new IsNull(((NotNull)c).getAttributeName());
        }
        if (c instanceof AlwaysMatch) {
            throw new UnsupportedOperationException();
        }
        throw new AssertionError((Object)("negate for " + c.getClass()));
    }

    protected abstract void maxResults(int var1);

    protected abstract void includeKeys(boolean var1);

    protected abstract void includeValues(boolean var1);

    protected abstract void max(String var1);

    protected abstract void min(String var1);

    protected abstract void sum(String var1);

    protected abstract void average(String var1);

    protected abstract void count();

    protected abstract void attribute(String var1);

    protected abstract void attributeAscending(String var1);

    protected abstract void attributeDescending(String var1);

    protected abstract void groupBy(String var1);

    protected abstract void ilike(ILike var1);

    protected abstract void isNull(IsNull var1);

    protected abstract void notNull(NotNull var1);

    protected abstract void all();

    protected abstract void and(And var1);

    protected abstract void or(Or var1);

    protected abstract void in(InCollection var1);

    protected abstract void equalTo(EqualTo var1);

    protected abstract void notIlike(NotILike var1);

    protected abstract void greaterThan(GreaterThan var1);

    protected abstract void greaterThanEqual(GreaterThanOrEqual var1);

    protected abstract void between(Between var1);

    protected abstract void notEqualTerm(NotEqualTo var1);

    protected abstract void lessThanEqual(LessThanOrEqual var1);

    protected abstract void lessThan(LessThan var1);

    protected abstract void setHints(ExecutionHints var1);
}

