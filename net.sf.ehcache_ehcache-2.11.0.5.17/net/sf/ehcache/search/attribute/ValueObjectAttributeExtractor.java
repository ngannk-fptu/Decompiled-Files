/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.attribute;

import net.sf.ehcache.Element;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeExtractorException;
import net.sf.ehcache.search.attribute.AttributeType;

public class ValueObjectAttributeExtractor
implements AttributeExtractor {
    @Override
    public Object attributeFor(Element element, String attributeName) throws AttributeExtractorException {
        Object value = element.getObjectValue();
        if (AttributeType.isSupportedType(value)) {
            return value;
        }
        return null;
    }
}

