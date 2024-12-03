/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.VersionPathStrategy;

public interface VersionStrategy
extends VersionPathStrategy {
    public String getResourceVersion(Resource var1);
}

