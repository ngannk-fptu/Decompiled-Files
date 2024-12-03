/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import java.util.List;

public interface ResourceFilterFactory {
    public List<ResourceFilter> create(AbstractMethod var1);
}

