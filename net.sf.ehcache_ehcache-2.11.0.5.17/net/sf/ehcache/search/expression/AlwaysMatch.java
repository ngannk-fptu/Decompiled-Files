/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.expression.BaseCriteria;
import net.sf.ehcache.search.expression.Criteria;

public class AlwaysMatch
extends BaseCriteria {
    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        return true;
    }

    @Override
    public Criteria and(Criteria other) {
        return other;
    }

    @Override
    public Criteria or(Criteria other) {
        return this;
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        return Collections.emptySet();
    }
}

