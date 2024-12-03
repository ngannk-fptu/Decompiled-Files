/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeType;
import net.sf.ehcache.search.expression.BaseCriteria;

public class EqualTo
extends BaseCriteria {
    private final Object value;
    private final String attributeName;
    private final AttributeType type;

    public EqualTo(String attributeName, Object value) {
        if (value == null || attributeName == null) {
            throw new NullPointerException();
        }
        this.attributeName = attributeName;
        this.value = value;
        this.type = AttributeType.typeFor(attributeName, value);
    }

    public Object getValue() {
        return this.value;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public AttributeType getType() {
        return this.type;
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        Object attributeValue = EqualTo.getExtractor(this.getAttributeName(), attributeExtractors).attributeFor(e, this.getAttributeName());
        if (attributeValue == null) {
            return false;
        }
        AttributeType attrType = AttributeType.typeFor(this.getAttributeName(), attributeValue);
        if (!this.getType().equals((Object)attrType)) {
            throw new SearchException("Expecting attribute of type " + this.getType().name() + " but was " + attrType.name());
        }
        if (this.getType().equals((Object)AttributeType.STRING)) {
            return ((String)this.value).equalsIgnoreCase((String)attributeValue);
        }
        return this.value.equals(attributeValue);
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        return Collections.singleton(new Attribute(this.attributeName));
    }
}

