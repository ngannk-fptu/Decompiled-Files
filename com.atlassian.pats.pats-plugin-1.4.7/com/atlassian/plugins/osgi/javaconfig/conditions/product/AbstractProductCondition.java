/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.FrameworkUtil
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.plugins.osgi.javaconfig.conditions.product;

import java.util.Objects;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public abstract class AbstractProductCondition
implements Condition {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String canaryServiceClassName;

    protected AbstractProductCondition(String canaryServiceClassName) {
        this.canaryServiceClassName = Objects.requireNonNull(canaryServiceClassName);
    }

    public final boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        if (bundle == null) {
            this.logger.debug("Bundle not found for {}", (Object)this.getClass().getName());
            return false;
        }
        BundleContext bundleContext = bundle.getBundleContext();
        if (bundleContext == null) {
            this.logger.debug("BundleContext not found for {}", (Object)bundle);
            return false;
        }
        return this.isCanaryServicePresent(bundleContext);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isCanaryServicePresent(BundleContext bundleContext) {
        ServiceReference canaryServiceReference = null;
        try {
            canaryServiceReference = bundleContext.getServiceReference(this.canaryServiceClassName);
            boolean servicePresent = canaryServiceReference != null;
            this.logger.debug("{} present = {}", (Object)this.canaryServiceClassName, (Object)servicePresent);
            boolean bl = servicePresent;
            return bl;
        }
        finally {
            if (canaryServiceReference != null) {
                bundleContext.ungetService(canaryServiceReference);
            }
        }
    }
}

