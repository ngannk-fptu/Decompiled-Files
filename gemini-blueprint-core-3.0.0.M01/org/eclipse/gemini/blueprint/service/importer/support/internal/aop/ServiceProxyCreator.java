/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ProxyPlusCallback;
import org.osgi.framework.ServiceReference;

public interface ServiceProxyCreator {
    public ProxyPlusCallback createServiceProxy(ServiceReference var1);
}

