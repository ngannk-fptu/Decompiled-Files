/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.Map;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.attribute.AttributeExtractor;

public interface Criteria {
    public boolean execute(Element var1, Map<String, AttributeExtractor> var2);

    public Criteria and(Criteria var1);

    public Criteria or(Criteria var1);

    public Criteria not();
}

