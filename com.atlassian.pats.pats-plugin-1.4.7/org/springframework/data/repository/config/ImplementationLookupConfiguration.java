/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 */
package org.springframework.data.repository.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;

public interface ImplementationLookupConfiguration
extends ImplementationDetectionConfiguration {
    public String getImplementationBeanName();

    public String getImplementationClassName();

    public boolean matches(BeanDefinition var1);

    public boolean hasMatchingBeanName(BeanDefinition var1);
}

