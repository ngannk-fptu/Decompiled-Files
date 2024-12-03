/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.webresource.api.assembler.resource;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public class CompleteWebResourceKey {
    private final String webResourceKey;
    private final String pluginKey;

    public CompleteWebResourceKey(String pluginKey, String webResourceKey) {
        this.pluginKey = pluginKey;
        this.webResourceKey = webResourceKey;
    }

    public String getWebResourceKey() {
        return this.webResourceKey;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getCompleteKey() {
        return this.pluginKey + ":" + this.webResourceKey;
    }

    public String toString() {
        return "CompleteWebResourceKey{" + this.getCompleteKey() + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CompleteWebResourceKey that = (CompleteWebResourceKey)o;
        return this.webResourceKey.equals(that.webResourceKey) && this.pluginKey.equals(that.pluginKey);
    }

    public int hashCode() {
        int result = this.webResourceKey.hashCode();
        result = 31 * result + this.pluginKey.hashCode();
        return result;
    }
}

