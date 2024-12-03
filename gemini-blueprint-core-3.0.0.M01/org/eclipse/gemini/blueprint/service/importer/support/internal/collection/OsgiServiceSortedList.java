/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.Comparator;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicCollection;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicSortedList;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceList;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

public class OsgiServiceSortedList
extends OsgiServiceList {
    private final Comparator comparator;

    public OsgiServiceSortedList(Filter filter, BundleContext context, ClassLoader classLoader, ServiceProxyCreator proxyCreator, boolean useServiceReference) {
        this(filter, context, classLoader, null, proxyCreator, useServiceReference);
    }

    public OsgiServiceSortedList(Filter filter, BundleContext context, ClassLoader classLoader, Comparator comparator, ServiceProxyCreator proxyCreator, boolean useServiceReference) {
        super(filter, context, classLoader, proxyCreator, useServiceReference);
        this.comparator = comparator;
    }

    @Override
    protected DynamicCollection createInternalDynamicStorage() {
        this.storage = new DynamicSortedList(this.comparator);
        return (DynamicCollection)((Object)this.storage);
    }

    public Comparator comparator() {
        return this.comparator;
    }
}

