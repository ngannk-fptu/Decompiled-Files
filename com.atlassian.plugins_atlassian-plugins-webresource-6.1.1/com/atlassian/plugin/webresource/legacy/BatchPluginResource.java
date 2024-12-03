/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.legacy.AbstractPluginResource;
import com.google.common.base.Objects;
import java.util.Collections;

public class BatchPluginResource
extends AbstractPluginResource {
    private final String key;

    public BatchPluginResource(String key, String completeKey) {
        super(Collections.singleton(completeKey));
        this.key = key;
    }

    public String getModuleCompleteKey() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BatchPluginResource that = (BatchPluginResource)o;
        return Objects.equal((Object)this.key, (Object)that.key);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.key});
    }
}

