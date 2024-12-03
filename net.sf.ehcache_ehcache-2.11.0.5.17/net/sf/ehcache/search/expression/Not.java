/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.expression.BaseCriteria;
import net.sf.ehcache.search.expression.Criteria;

public class Not
extends BaseCriteria {
    private final Criteria c;

    public Not(Criteria c) {
        this.c = c;
    }

    public Criteria getCriteria() {
        return this.c;
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        return !this.c.execute(e, attributeExtractors);
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        return ((BaseCriteria)this.c).getAttributes();
    }
}

