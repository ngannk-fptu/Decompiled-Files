/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.LazyReference
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.spring.container;

import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.concurrent.LazyReference;
import org.apache.commons.lang.StringUtils;

public class LazyComponentReference<T>
extends LazyReference<T> {
    private final String key;

    public LazyComponentReference(String key) {
        if (StringUtils.isEmpty((String)key)) {
            throw new IllegalArgumentException("Argument 'key' cannot be empty or null");
        }
        this.key = key;
    }

    protected T create() throws Exception {
        if (!ContainerManager.isContainerSetup()) {
            throw new IllegalStateException("Container is not setup");
        }
        return (T)ContainerManager.getComponent(this.key);
    }
}

