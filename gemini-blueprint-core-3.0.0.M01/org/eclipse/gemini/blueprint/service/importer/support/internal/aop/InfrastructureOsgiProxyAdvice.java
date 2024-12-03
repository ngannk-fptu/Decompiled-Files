/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.DelegatingIntroductionInterceptor
 *  org.springframework.core.InfrastructureProxy
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import org.eclipse.gemini.blueprint.service.importer.support.internal.aop.ServiceInvoker;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.core.InfrastructureProxy;
import org.springframework.util.Assert;

public class InfrastructureOsgiProxyAdvice
extends DelegatingIntroductionInterceptor
implements InfrastructureProxy {
    private static final long serialVersionUID = -496653472310304413L;
    private static final int hashCode = InfrastructureOsgiProxyAdvice.class.hashCode() * 13;
    private final transient ServiceInvoker invoker;

    public InfrastructureOsgiProxyAdvice(ServiceInvoker serviceInvoker) {
        Assert.notNull((Object)serviceInvoker);
        this.invoker = serviceInvoker;
    }

    public Object getWrappedObject() {
        return this.invoker.getTarget();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof InfrastructureOsgiProxyAdvice) {
            InfrastructureOsgiProxyAdvice oth = (InfrastructureOsgiProxyAdvice)((Object)other);
            return this.invoker.equals(oth.invoker);
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }
}

