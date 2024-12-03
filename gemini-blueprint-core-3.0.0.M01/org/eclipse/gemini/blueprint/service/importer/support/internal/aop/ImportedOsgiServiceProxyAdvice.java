/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 *  org.springframework.aop.support.DelegatingIntroductionInterceptor
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy;
import org.eclipse.gemini.blueprint.service.importer.ServiceReferenceProxy;
import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.StaticServiceReferenceProxy;
import org.osgi.framework.ServiceReference;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.util.Assert;

public class ImportedOsgiServiceProxyAdvice
extends DelegatingIntroductionInterceptor
implements ImportedOsgiServiceProxy {
    private static final long serialVersionUID = 6455437774724678999L;
    private static final int hashCode = ImportedOsgiServiceProxyAdvice.class.hashCode() * 13;
    private final transient ServiceReferenceProxy reference;

    public ImportedOsgiServiceProxyAdvice(ServiceReference reference) {
        Assert.notNull((Object)reference);
        this.reference = reference instanceof ServiceReferenceProxy ? (ServiceReferenceProxy)reference : new StaticServiceReferenceProxy(reference);
    }

    @Override
    public ServiceReferenceProxy getServiceReference() {
        return this.reference;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ImportedOsgiServiceProxyAdvice) {
            ImportedOsgiServiceProxyAdvice oth = (ImportedOsgiServiceProxyAdvice)other;
            return this.reference.equals(oth.reference);
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }
}

