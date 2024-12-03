/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.attribute;

import java.util.Map;
import net.sf.ehcache.Element;

public interface DynamicAttributesExtractor {
    public <T> Map<String, T> attributesFor(Element var1);
}

