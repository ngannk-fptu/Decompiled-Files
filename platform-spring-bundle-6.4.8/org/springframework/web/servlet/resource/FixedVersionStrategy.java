/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.AbstractVersionStrategy;

public class FixedVersionStrategy
extends AbstractVersionStrategy {
    private final String version;

    public FixedVersionStrategy(String version) {
        super(new AbstractVersionStrategy.PrefixVersionPathStrategy(version));
        this.version = version;
    }

    @Override
    public String getResourceVersion(Resource resource) {
        return this.version;
    }
}

