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

public class Or
extends BaseCriteria {
    private final Criteria[] criteria;

    public Or(Criteria lhs, Criteria rhs) {
        this.criteria = new Criteria[]{lhs, rhs};
    }

    private Or(Or original, Criteria additional) {
        Criteria[] originalCriteria = original.getCriterion();
        this.criteria = new Criteria[originalCriteria.length + 1];
        System.arraycopy(originalCriteria, 0, this.criteria, 0, originalCriteria.length);
        this.criteria[originalCriteria.length] = additional;
    }

    @Override
    public Criteria or(Criteria other) {
        return new Or(this, other);
    }

    public Criteria[] getCriterion() {
        return this.criteria;
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        for (Criteria c : this.criteria) {
            if (!c.execute(e, attributeExtractors)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        HashSet attrs = new HashSet();
        for (Criteria c : this.criteria) {
            attrs.addAll(((BaseCriteria)c).getAttributes());
        }
        return attrs;
    }
}

