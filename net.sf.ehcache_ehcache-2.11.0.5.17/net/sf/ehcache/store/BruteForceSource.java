/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Element;
import net.sf.ehcache.config.Searchable;

interface BruteForceSource {
    public Iterable<Element> elements();

    public Searchable getSearchable();

    public Element transformForIndexing(Element var1);
}

