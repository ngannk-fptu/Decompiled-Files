/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.config;

import java.util.List;
import java.util.Optional;
import org.apache.struts2.config.entities.BeanConfig;
import org.apache.struts2.config.entities.BeanSelectionConfig;
import org.apache.struts2.config.entities.ConstantConfig;

public interface StrutsJavaConfiguration {
    public List<BeanConfig> beans();

    public List<ConstantConfig> constants();

    default public Optional<BeanSelectionConfig> beanSelection() {
        return Optional.empty();
    }

    public List<String> unknownHandlerStack();
}

