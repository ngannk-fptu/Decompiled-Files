/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.Map;
import java.util.Set;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.expression.And;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.expression.Not;
import net.sf.ehcache.search.expression.Or;

public abstract class BaseCriteria
implements Criteria {
    @Override
    public Criteria and(Criteria other) {
        return new And(this, other);
    }

    @Override
    public Criteria not() {
        return new Not(this);
    }

    @Override
    public Criteria or(Criteria other) {
        return new Or(this, other);
    }

    public static AttributeExtractor getExtractor(String attrName, Map<String, AttributeExtractor> knownExtractors) {
        AttributeExtractor extr = knownExtractors.get(attrName);
        if (extr != null) {
            return extr;
        }
        throw new AssertionError((Object)("Unknown search attribute " + attrName));
    }

    public abstract Set<Attribute<?>> getAttributes();
}

