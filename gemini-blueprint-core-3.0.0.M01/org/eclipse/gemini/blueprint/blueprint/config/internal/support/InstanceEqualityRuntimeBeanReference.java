/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.RuntimeBeanReference
 */
package org.eclipse.gemini.blueprint.blueprint.config.internal.support;

import org.springframework.beans.factory.config.RuntimeBeanReference;

public class InstanceEqualityRuntimeBeanReference
extends RuntimeBeanReference {
    public InstanceEqualityRuntimeBeanReference(String beanName, boolean toParent) {
        super(beanName, toParent);
    }

    public InstanceEqualityRuntimeBeanReference(String beanName) {
        super(beanName);
    }

    public boolean equals(Object other) {
        return this == other;
    }
}

