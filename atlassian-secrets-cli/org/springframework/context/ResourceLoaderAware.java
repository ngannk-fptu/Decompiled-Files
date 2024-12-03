/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.io.ResourceLoader;

public interface ResourceLoaderAware
extends Aware {
    public void setResourceLoader(ResourceLoader var1);
}

