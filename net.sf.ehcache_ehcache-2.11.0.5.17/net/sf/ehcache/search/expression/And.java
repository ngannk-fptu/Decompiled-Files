/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.expression.BaseCriteria;
import net.sf.ehcache.search.expression.Criteria;

public class And
extends BaseCriteria {
    private final Criteria[] criterion;

    public And(Criteria lhs, Criteria rhs) {
        this.criterion = new Criteria[]{lhs, rhs};
    }

    private And(And original, Criteria additional) {
        Criteria[] originalCriteria = original.getCriterion();
        this.criterion = new Criteria[originalCriteria.length + 1];
        System.arraycopy(originalCriteria, 0, this.criterion, 0, originalCriteria.length);
        this.criterion[originalCriteria.length] = additional;
    }

    @Override
    public Criteria and(Criteria other) {
        return new And(this, other);
    }

    public Criteria[] getCriterion() {
        return this.criterion;
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        for (Criteria c : this.criterion) {
            if (c.execute(e, attributeExtractors)) continue;
            return false;
        }
        return true;
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        HashSet attrs = new HashSet();
        for (Criteria c : this.criterion) {
            attrs.addAll(((BaseCriteria)c).getAttributes());
        }
        return attrs;
    }
}

