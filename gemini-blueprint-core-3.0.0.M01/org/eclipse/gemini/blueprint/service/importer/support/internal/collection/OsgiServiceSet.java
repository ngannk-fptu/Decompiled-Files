/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.Set;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceProxyCreator;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicCollection;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicSet;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.OsgiServiceCollection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;

public class OsgiServiceSet
extends OsgiServiceCollection
implements Set {
    public OsgiServiceSet(Filter filter, BundleContext context, ClassLoader classLoader, ServiceProxyCreator proxyCreator, boolean useServiceReferences) {
        super(filter, context, classLoader, proxyCreator, useServiceReferences);
    }

    protected DynamicCollection createInternalDynamicStorage() {
        return new DynamicSet();
    }
}

