/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class Location {
    private final Resource resource;
    @Nullable
    private final Object source;

    public Location(Resource resource) {
        this(resource, null);
    }

    public Location(Resource resource, @Nullable Object source) {
        Assert.notNull((Object)resource, (String)"Resource must not be null");
        this.resource = resource;
        this.source = source;
    }

    public Resource getResource() {
        return this.resource;
    }

    @Nullable
    public Object getSource() {
        return this.source;
    }
}

