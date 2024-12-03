/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeType;
import net.sf.ehcache.search.expression.BaseCriteria;

public class InCollection
extends BaseCriteria {
    private final String attributeName;
    private final Collection<?> values;
    private final AttributeType type;
    private final boolean empty;

    public InCollection(String attributeName, Collection<?> values) {
        if (attributeName == null || values == null) {
            throw new NullPointerException();
        }
        this.attributeName = attributeName;
        this.values = values;
        this.empty = values.isEmpty();
        this.type = !this.empty ? this.verifyCommonType() : null;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public Collection<?> values() {
        return Collections.unmodifiableCollection(this.values);
    }

    private AttributeType verifyCommonType() {
        if (this.values.isEmpty()) {
            throw new AssertionError();
        }
        AttributeType rv = null;
        for (Object value : this.values) {
            if (value == null) {
                throw new NullPointerException("null element in set");
            }
            AttributeType at = AttributeType.typeFor(this.attributeName, value);
            if (rv == null) {
                rv = at;
                continue;
            }
            if (at == rv) continue;
            throw new SearchException("Multiple types detected in collection: " + at + " and " + rv);
        }
        return rv;
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        if (this.empty) {
            return false;
        }
        Object attrValue = InCollection.getExtractor(this.attributeName, attributeExtractors).attributeFor(e, this.attributeName);
        if (attrValue == null) {
            return false;
        }
        AttributeType attrType = AttributeType.typeFor(this.getAttributeName(), attrValue);
        if (!this.type.equals((Object)attrType)) {
            throw new SearchException("Expecting attribute of type " + this.type.name() + " but was " + attrType.name());
        }
        if (AttributeType.STRING.equals((Object)this.type)) {
            for (Object o : this.values) {
                if (!attrValue.toString().equalsIgnoreCase(o.toString())) continue;
                return true;
            }
            return false;
        }
        return this.values.contains(attrValue);
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        return Collections.singleton(new Attribute(this.attributeName));
    }
}

