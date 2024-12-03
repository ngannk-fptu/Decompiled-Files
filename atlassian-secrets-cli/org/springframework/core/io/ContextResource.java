/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io;

import org.springframework.core.io.Resource;

public interface ContextResource
extends Resource {
    public String getPathWithinContext();
}

