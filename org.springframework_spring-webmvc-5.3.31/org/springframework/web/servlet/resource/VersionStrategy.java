/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 */
package org.springframework.web.servlet.resource;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.VersionPathStrategy;

public interface VersionStrategy
extends VersionPathStrategy {
    public String getResourceVersion(Resource var1);
}

