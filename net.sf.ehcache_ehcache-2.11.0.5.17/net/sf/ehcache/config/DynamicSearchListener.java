/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.search.attribute.DynamicAttributesExtractor;

public interface DynamicSearchListener {
    public void extractorChanged(DynamicAttributesExtractor var1, DynamicAttributesExtractor var2);
}

