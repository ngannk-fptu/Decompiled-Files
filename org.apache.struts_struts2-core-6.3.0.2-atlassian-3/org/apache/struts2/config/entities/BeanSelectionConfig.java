/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.config.entities;

import com.opensymphony.xwork2.config.BeanSelectionProvider;

public class BeanSelectionConfig {
    private final Class<? extends BeanSelectionProvider> clazz;
    private final String name;

    public BeanSelectionConfig(Class<? extends BeanSelectionProvider> clazz) {
        this(clazz, "default");
    }

    public BeanSelectionConfig(Class<? extends BeanSelectionProvider> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class<? extends BeanSelectionProvider> getClazz() {
        return this.clazz;
    }

    public String getName() {
        return this.name;
    }
}

