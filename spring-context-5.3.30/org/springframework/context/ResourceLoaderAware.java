/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.Aware
 *  org.springframework.core.io.ResourceLoader
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.io.ResourceLoader;

public interface ResourceLoaderAware
extends Aware {
    public void setResourceLoader(ResourceLoader var1);
}

