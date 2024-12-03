/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import org.springframework.core.KotlinReflectionParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.PrioritizedParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.util.ClassUtils;

public class DefaultParameterNameDiscoverer
extends PrioritizedParameterNameDiscoverer {
    private static final boolean kotlinPresent = ClassUtils.isPresent("kotlin.Unit", DefaultParameterNameDiscoverer.class.getClassLoader());

    public DefaultParameterNameDiscoverer() {
        if (kotlinPresent) {
            this.addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
        }
        this.addDiscoverer(new StandardReflectionParameterNameDiscoverer());
        this.addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
    }
}

