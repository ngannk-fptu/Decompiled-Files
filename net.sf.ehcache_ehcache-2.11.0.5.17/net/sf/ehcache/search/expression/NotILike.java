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
import net.sf.ehcache.search.expression.ILike;

public class NotILike
extends BaseCriteria {
    private final ILike src;

    public NotILike(String attributeName, String regex) {
        this.src = new ILike(attributeName, regex);
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        return !this.src.execute(e, attributeExtractors);
    }

    public String getAttributeName() {
        return this.src.getAttributeName();
    }

    public String getRegex() {
        return this.src.getRegex();
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        return Collections.singleton(new Attribute(this.getAttributeName()));
    }
}

