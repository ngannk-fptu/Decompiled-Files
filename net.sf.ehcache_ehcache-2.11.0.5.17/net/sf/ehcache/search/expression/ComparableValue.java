/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeType;
import net.sf.ehcache.search.expression.BaseCriteria;

public abstract class ComparableValue
extends BaseCriteria {
    private static final Comparator<String> LUCENE_STRING_COMPARATOR = new LuceneCaseAgnosticStringComparator();
    private final String attributeName;
    private final AttributeType type;

    public ComparableValue(String attributeName, Object value) {
        this(attributeName, AttributeType.typeFor(attributeName, value));
    }

    public ComparableValue(String attributeName, AttributeType type) {
        this.attributeName = attributeName;
        this.type = type;
        if (!this.type.isComparable()) {
            throw new SearchException("Illegal (non-comparable) type for comparsion (" + type + ")");
        }
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public AttributeType getType() {
        return this.type;
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        Object attrValue = ComparableValue.getExtractor(this.getAttributeName(), attributeExtractors).attributeFor(e, this.getAttributeName());
        if (attrValue == null) {
            return false;
        }
        AttributeType attrType = AttributeType.typeFor(this.getAttributeName(), attrValue);
        if (!this.getType().equals((Object)attrType)) {
            throw new SearchException("Expecting attribute of type " + this.getType().name() + " but was " + attrType.name());
        }
        if (this.getType().equals((Object)AttributeType.STRING)) {
            return this.executeComparableString((Comparable)attrValue);
        }
        return this.executeComparable((Comparable)attrValue);
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        return Collections.singleton(new Attribute(this.attributeName));
    }

    protected abstract boolean executeComparable(Comparable var1);

    protected abstract boolean executeComparableString(Comparable var1);

    protected static int luceneStringCompare(String s1, String s2) {
        return LUCENE_STRING_COMPARATOR.compare(s1, s2);
    }

    private static class LuceneCaseAgnosticStringComparator
    implements Comparator<String>,
    Serializable {
        private LuceneCaseAgnosticStringComparator() {
        }

        @Override
        public int compare(String s1, String s2) {
            int n1 = s1.length();
            int n2 = s2.length();
            for (int i = 0; i < n1 && i < n2; ++i) {
                char c2;
                char c1 = s1.charAt(i);
                if (c1 == (c2 = s2.charAt(i)) || (c1 = Character.toLowerCase(c1)) == (c2 = Character.toLowerCase(c2))) continue;
                return c1 - c2;
            }
            return n1 - n2;
        }
    }
}

