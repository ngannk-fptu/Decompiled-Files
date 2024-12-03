/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

public interface ComponentDefinition
extends BeanMetadataElement {
    public String getName();

    public String getDescription();

    public BeanDefinition[] getBeanDefinitions();

    public BeanDefinition[] getInnerBeanDefinitions();

    public BeanReference[] getBeanReferences();
}

