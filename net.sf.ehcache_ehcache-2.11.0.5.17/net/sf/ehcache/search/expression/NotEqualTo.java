/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.Map;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.expression.EqualTo;

public class NotEqualTo
extends EqualTo {
    public NotEqualTo(String attributeName, Object value) {
        super(attributeName, value);
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        return !super.execute(e, attributeExtractors);
    }
}

