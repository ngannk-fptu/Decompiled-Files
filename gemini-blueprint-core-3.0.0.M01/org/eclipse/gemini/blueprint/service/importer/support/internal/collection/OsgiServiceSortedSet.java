/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.Comparator;
import java.util.SortedSet;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicCollection;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicSortedSet;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

public class OsgiServiceSortedSet
extends OsgiServiceSet
implements SortedSet {
    private SortedSet storage;
    private final Comparator comparator;

    public OsgiServiceSortedSet(Filter filter, BundleContext context, ClassLoader classLoader, ServiceProxyCreator proxyCreator, boolean useServiceReferences) {
        this(filter, context, classLoader, null, proxyCreator, useServiceReferences);
    }

    public OsgiServiceSortedSet(Filter filter, BundleContext context, ClassLoader classLoader, Comparator comparator, ServiceProxyCreator proxyCreator, boolean useServiceReferences) {
        super(filter, context, classLoader, proxyCreator, useServiceReferences);
        this.comparator = comparator;
    }

    @Override
    protected DynamicCollection createInternalDynamicStorage() {
        this.storage = new DynamicSortedSet(this.comparator);
        return (DynamicCollection)((Object)this.storage);
    }

    public Comparator comparator() {
        return this.storage.comparator();
    }

    public Object first() {
        return this.storage.first();
    }

    public Object last() {
        return this.storage.last();
    }

    public SortedSet tailSet(Object fromElement) {
        return this.storage.tailSet(fromElement);
    }

    public SortedSet headSet(Object toElement) {
        return this.storage.headSet(toElement);
    }

    public SortedSet subSet(Object fromElement, Object toElement) {
        return this.storage.subSet(fromElement, toElement);
    }
}

