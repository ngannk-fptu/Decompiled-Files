/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.mapper;

import org.apache.catalina.Wrapper;

public class WrapperMappingInfo {
    private final String mapping;
    private final Wrapper wrapper;
    private final boolean jspWildCard;
    private final boolean resourceOnly;

    public WrapperMappingInfo(String mapping, Wrapper wrapper, boolean jspWildCard, boolean resourceOnly) {
        this.mapping = mapping;
        this.wrapper = wrapper;
        this.jspWildCard = jspWildCard;
        this.resourceOnly = resourceOnly;
    }

    public String getMapping() {
        return this.mapping;
    }

    public Wrapper getWrapper() {
        return this.wrapper;
    }

    public boolean isJspWildCard() {
        return this.jspWildCard;
    }

    public boolean isResourceOnly() {
        return this.resourceOnly;
    }
}

