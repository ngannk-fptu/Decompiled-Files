/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.gemini.blueprint.service.importer.support.LocalBundleContext;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

class LocalBundleContextAdvice
implements MethodInterceptor {
    private static final int hashCode = LocalBundleContextAdvice.class.hashCode() * 13;
    private final BundleContext context;

    LocalBundleContextAdvice(Bundle bundle) {
        this(OsgiBundleUtils.getBundleContext(bundle));
    }

    LocalBundleContextAdvice(BundleContext bundle) {
        this.context = bundle;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        BundleContext oldContext = LocalBundleContext.setInvokerBundleContext(this.context);
        try {
            Object object = invocation.proceed();
            return object;
        }
        finally {
            LocalBundleContext.setInvokerBundleContext(oldContext);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LocalBundleContextAdvice) {
            LocalBundleContextAdvice oth = (LocalBundleContextAdvice)other;
            return this.context.equals(oth.context);
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }
}

