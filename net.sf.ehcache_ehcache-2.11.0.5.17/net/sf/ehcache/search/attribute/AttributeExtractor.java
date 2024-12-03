/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.attribute;

import java.io.Serializable;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.attribute.AttributeExtractorException;

public interface AttributeExtractor
extends Serializable {
    public Object attributeFor(Element var1, String var2) throws AttributeExtractorException;
}

